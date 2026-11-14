#!/usr/bin/env python3
"""
Question bank validator / normalizer for the Grade Assessment platform.

Usage:
  python tools/validate_question_bank.py                 # validate every per-grade file
  python tools/validate_question_bank.py math            # validate one subject directory
  python tools/validate_question_bank.py --normalize     # also (re)write piParameters + index.json manifest

Per-grade file layout (one file per subject per grade, grade token K,1..10):
  src/main/resources/questions/<dir>/<prefix>_grade_<token>.json

Calibration contract (single source of truth, mirrored in GradeScale.kt):
  theta = 0.6 * (gradeEquivalent - 5)
  An item tagged gradeLevel=g with difficulty EASY/MEDIUM/HARD is located at
  gradeEquivalent g+0.2 / g+0.5 / g+0.8, i.e. a student working at the middle of
  grade g has a 50% (above guessing) chance on a MEDIUM grade-g item.
"""
import collections
import glob
import json
import os
import re
import sys

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources", "questions")
ROOT = os.path.normpath(ROOT)

# directory -> (Subject enum, id prefix)
SUBJECTS = {
    "math": ("MATH", "math"),
    "reading/comprehension": ("READING", "reading"),
    "writing": ("WRITING", "writing"),
    "science": ("SCIENCE", "science"),
    "arts": ("ARTS", "arts"),
    "world_history": ("WORLD_HISTORY", "world_history"),
    "social_studies": ("SOCIAL_STUDIES", "social_studies"),
    "critical_thinking": ("CRITICAL_THINKING", "critical_thinking"),
    "iq_assessment": ("IQ_ASSESSMENT", "iq_assessment"),
}
GRADE_TOKENS = ["K", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
TYPES = {"MULTIPLE_CHOICE", "MULTI_SELECT", "FILL_IN_BLANK", "CLOZE", "ORDERING", "PASSAGE_BASED"}
DIFFICULTIES = {"EASY": 0.2, "MEDIUM": 0.5, "HARD": 0.8}
FLUENCY_GE = {"LETTER_NAMING": 0.3, "PHONEME_SEGMENTATION": 0.7, "NONSENSE_WORD": 1.3}
LONGEST_KEY_MIN_CHARS = 15   # below this, choice length is not a meaningful cue (numbers, single words)
# Chance is ~25%. Warn when "always pick the Nth-longest choice" clearly beats it for ANY N:
# fixing only the longest-choice cue tends to move the key to second-longest instead.
LENGTH_RANK_MAX_SHARE = 0.40
LENGTH_RANK_NAMES = ["longest", "2nd-longest", "3rd-longest", "4th-longest", "5th-longest"]
MIN_PER_GRADE = 12
MIN_PER_DIFFICULTY = 3


def band(grade):
    return "K-2" if grade <= 2 else "3-5" if grade <= 5 else "6-8" if grade <= 8 else "9-10"


def irt_for(q):
    """Expert-assigned 3PL parameters derived from grade + difficulty + item format."""
    ge = q["gradeLevel"] + DIFFICULTIES[q["difficulty"]]
    b = round(0.6 * (ge - 5.0), 3)
    t = q["type"]
    if t in ("MULTIPLE_CHOICE", "PASSAGE_BASED"):
        c = round(1.0 / max(len(q.get("choices") or []), 2), 3)  # blind-guess floor
    else:
        c = 0.0  # constructed / multi-part responses cannot realistically be guessed
    return {"a": 1.2, "b": b, "c": c}


def validate_file(path, subject_enum, prefix, grade, errors, warnings, seen_ids):
    rel = os.path.relpath(path, ROOT)
    try:
        with open(path, encoding="utf-8") as fh:
            data = json.load(fh)
    except Exception as exc:  # noqa: BLE001
        errors.append(f"{rel}: JSON parse failure: {exc}")
        return None
    if not isinstance(data, list):
        errors.append(f"{rel}: top level must be a JSON array")
        return None

    diff_counts = collections.Counter()
    type_counts = collections.Counter()
    answer_pos = collections.Counter()
    last_blooms_types = set()
    for q in data:
        qid = q.get("id", "<no id>")
        where = f"{rel}:{qid}"

        def err(msg):
            errors.append(f"{where}: {msg}")

        tok = "k" if grade == 0 else str(grade)
        if not re.fullmatch(rf"{prefix}_g{tok}_\d{{3}}", str(qid)):
            err(f"id must match {prefix}_g{tok}_NNN")
        if qid in seen_ids:
            err("duplicate id")
        seen_ids.add(qid)
        if q.get("subject") != subject_enum:
            err(f"subject must be {subject_enum}")
        if q.get("gradeLevel") != grade:
            err(f"gradeLevel must be {grade}")
        if q.get("gradeRange") != band(grade):
            err(f"gradeRange must be '{band(grade)}'")
        if q.get("difficulty") not in DIFFICULTIES:
            err("difficulty must be EASY, MEDIUM or HARD")
        else:
            diff_counts[q["difficulty"]] += 1
        if q.get("bloomsLevel") not in (1, 2, 3, 4, 5, 6):
            err("bloomsLevel must be 1..6")
        if not str(q.get("standardCode", "")).strip():
            err("standardCode is required")
        if not str(q.get("prompt", "")).strip():
            err("prompt is required")
        if not str(q.get("explanation", "")).strip():
            err("explanation is required")
        if subject_enum == "READING" and q.get("readingSubtest") != "COMPREHENSION":
            err("reading comprehension items need readingSubtest='COMPREHENSION'")

        t = q.get("type")
        if t not in TYPES:
            err(f"type must be one of {sorted(TYPES)}")
            continue
        type_counts[t] += 1
        last_blooms_types.add((q.get("bloomsLevel"), t))
        choices = q.get("choices") or []
        if any(not isinstance(c, str) or not c.strip() for c in choices):
            err("choices must be non-empty strings")
        if len(set(c.strip().lower() for c in choices)) != len(choices):
            err("duplicate choices")

        if t in ("MULTIPLE_CHOICE", "PASSAGE_BASED"):
            if len(choices) < 3 or len(choices) > 5:
                err("needs 3-5 choices (4 preferred)")
            if q.get("correctAnswer") not in choices:
                err("correctAnswer must be exactly one of choices")
            else:
                answer_pos[choices.index(q["correctAnswer"])] += 1
            if t == "PASSAGE_BASED" and not str(q.get("passage") or "").strip():
                err("PASSAGE_BASED needs a passage")
        elif t == "MULTI_SELECT":
            ca = q.get("correctAnswers") or []
            if len(choices) < 4:
                err("MULTI_SELECT needs >= 4 choices")
            if len(ca) < 2 or len(ca) >= len(choices):
                err("MULTI_SELECT needs 2..(n-1) correctAnswers")
            if any(a not in choices for a in ca):
                err("every correctAnswers entry must be exactly one of choices")
            if len(set(ca)) != len(ca):
                err("duplicate correctAnswers")
        elif t == "FILL_IN_BLANK":
            if not str(q.get("correctAnswer") or "").strip():
                err("FILL_IN_BLANK needs correctAnswer")
            if len(str(q.get("correctAnswer") or "")) > 30:
                warnings.append(f"{where}: long FILL_IN_BLANK answer; exact typing is unlikely")
        elif t == "CLOZE":
            blanks = q.get("blanks") or []
            # The app renders one input box per "___" in the PROMPT only.
            n_gaps = len(re.findall(r"_{3,}", q.get("prompt", "")))
            if not blanks:
                err("CLOZE needs blanks")
            elif n_gaps != len(blanks):
                err(f"CLOZE has {n_gaps} '___' gaps but {len(blanks)} blanks")
        elif t == "ORDERING":
            items = q.get("orderedItems") or []
            if len(items) < 3 or len(items) > 6:
                err("ORDERING needs 3-6 orderedItems (in the correct order)")
            if len(set(items)) != len(items):
                err("duplicate orderedItems")

    if len(data) < MIN_PER_GRADE:
        errors.append(f"{rel}: only {len(data)} questions, need >= {MIN_PER_GRADE}")
    for d in DIFFICULTIES:
        if diff_counts[d] < MIN_PER_DIFFICULTY:
            errors.append(f"{rel}: only {diff_counts[d]} {d} questions, need >= {MIN_PER_DIFFICULTY}")
    total_pos = sum(answer_pos.values())
    if total_pos >= 6 and max(answer_pos.values()) / total_pos > 0.5:
        warnings.append(f"{rel}: correct answer sits in the same choice position {max(answer_pos.values())}/{total_pos} times; spread it out")
    if len(last_blooms_types) < 3:
        warnings.append(f"{rel}: very little variety in (bloomsLevel, type); the selector alternates both")
    return data


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    normalize = "--normalize" in sys.argv
    wanted = {a.strip("/\\").replace("\\", "/") for a in args}
    if "reading" in wanted:
        wanted.add("reading/comprehension")

    errors, warnings, seen_ids, manifest = [], [], set(), []
    for directory, (subject_enum, prefix) in SUBJECTS.items():
        if wanted and directory not in wanted:
            continue
        total = 0
        long_items = 0                        # MC items with sentence-length choices
        key_rank = collections.Counter()      # length rank of the key among its choices (0 = longest)
        for grade, token in enumerate(GRADE_TOKENS):
            path = os.path.join(ROOT, *directory.split("/"), f"{prefix}_grade_{token}.json")
            if not os.path.exists(path):
                errors.append(f"missing file: {os.path.relpath(path, ROOT)}")
                continue
            data = validate_file(path, subject_enum, prefix, grade, errors, warnings, seen_ids)
            if data is None:
                continue
            total += len(data)
            for q in data:
                if q.get("type") in ("MULTIPLE_CHOICE", "PASSAGE_BASED") and q.get("correctAnswer") in (q.get("choices") or []):
                    lengths = [len(c) for c in q["choices"]]
                    if max(lengths) >= LONGEST_KEY_MIN_CHARS:
                        long_items += 1
                        key_len = len(q["correctAnswer"])
                        if lengths.count(key_len) == 1:  # a tie gives the strategy nothing to go on
                            key_rank[sorted(lengths, reverse=True).index(key_len)] += 1
            manifest.append(f"questions/{directory}/{prefix}_grade_{token}.json")
            if normalize:
                for q in data:
                    if q.get("type") == "CLOZE":
                        # The app splits the prompt on exactly three underscores.
                        q["prompt"] = re.sub(r"_{3,}", "___", q["prompt"])
                    if q.get("difficulty") in DIFFICULTIES and q.get("type") in TYPES:
                        q["piParameters"] = irt_for(q)
                with open(path, "w", encoding="utf-8", newline="\n") as fh:
                    json.dump(data, fh, indent=2, ensure_ascii=False)
                    fh.write("\n")
        print(f"{directory}: {total} questions")
        # Test-wiseness check: if the key sits at the same length position too often (e.g. it is usually
        # the longest, most qualified choice), a child can beat chance by choosing on length alone.
        if long_items >= 10:
            for rank, count in sorted(key_rank.items()):
                share = count / long_items
                if share > LENGTH_RANK_MAX_SHARE:
                    warnings.append(
                        f"{directory}: correct answer is the {LENGTH_RANK_NAMES[rank]} choice in {count}/{long_items} "
                        f"({share:.0%}) sentence-length MC items; keep every length position at or below "
                        f"{LENGTH_RANK_MAX_SHARE:.0%} (chance is about 25%) by varying which choice is longest"
                    )

    if normalize and not wanted:
        # Fluency subtests keep their legacy single files.
        for legacy in sorted(glob.glob(os.path.join(ROOT, "reading", "fluency", "*.json"))):
            manifest.append("questions/reading/fluency/" + os.path.basename(legacy))
            with open(legacy, encoding="utf-8") as fh:
                forms = json.load(fh)
            for form in forms:
                # Foundational-skill subtests sit at fixed points of the grade scale
                # (grade equivalents: letter naming K.3, phoneme segmentation K.7, nonsense words 1.3).
                ge = FLUENCY_GE.get(form.get("readingSubtest"))
                if ge is not None:
                    form["piParameters"] = {"a": 1.2, "b": round(0.6 * (ge - 5.0), 3), "c": 0.0}
            with open(legacy, "w", encoding="utf-8", newline="\n") as fh:
                json.dump(forms, fh, indent=2, ensure_ascii=False)
                fh.write("\n")
        with open(os.path.join(ROOT, "index.json"), "w", encoding="utf-8", newline="\n") as fh:
            json.dump(sorted(manifest), fh, indent=2)
            fh.write("\n")
        print(f"wrote index.json with {len(manifest)} files")

    for w in warnings:
        print("WARN ", w)
    for e in errors:
        print("ERROR", e)
    print(f"{len(errors)} error(s), {len(warnings)} warning(s)")
    sys.exit(1 if errors else 0)


if __name__ == "__main__":
    main()
