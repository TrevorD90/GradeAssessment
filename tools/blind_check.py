#!/usr/bin/env python3
"""
Blind answer-key check.

  export : writes each subject's questions WITHOUT answer keys (ordering items shuffled) to
           <outdir>/<subject>_blind.json, for an independent solver who never sees the keys.
  compare: reads the solver's <outdir>/<subject>_answers.json ({"id": answer, ...}) and reports
           every question where the solver disagrees with the key, using the same matching rules
           as the app's AnswerEvaluator (case, spacing, trailing punctuation, numbers, articles).

Answer formats expected from the solver:
  MULTIPLE_CHOICE / PASSAGE_BASED : the exact choice text
  MULTI_SELECT                    : list of exact choice texts
  FILL_IN_BLANK                   : the typed answer
  CLOZE                           : list of typed answers, one per ___
  ORDERING                        : list of the items in the correct order

Usage: python -I tools/blind_check.py export  <outdir>
       python -I tools/blind_check.py compare <outdir>
"""
import glob
import json
import os
import random
import re
import sys
from decimal import Decimal, InvalidOperation

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources", "questions"))
KEY_FIELDS = ("correctAnswer", "correctAnswers", "blanks", "orderedItems", "explanation", "piParameters", "difficulty")
NUMBER_WORDS = {w: i for i, w in enumerate("zero one two three four five six seven eight nine ten eleven twelve thirteen fourteen fifteen sixteen seventeen eighteen nineteen twenty".split())}


def load_bank():
    bank = {}
    for path in sorted(glob.glob(os.path.join(ROOT, "**", "*_grade_*.json"), recursive=True)):
        for q in json.load(open(path, encoding="utf-8")):
            bank[q["id"]] = q
    return bank


def subject_dir(q):
    return q["subject"].lower()


def export(outdir):
    os.makedirs(outdir, exist_ok=True)
    rng = random.Random(20261113)
    by_subject = {}
    for q in load_bank().values():
        blind = {k: v for k, v in q.items() if k not in KEY_FIELDS}
        if q["type"] == "ORDERING":
            items = list(q["orderedItems"])
            while len(items) > 1 and items == q["orderedItems"]:
                rng.shuffle(items)
            blind["itemsToOrder"] = items
        by_subject.setdefault(subject_dir(q), []).append(blind)
    for subject, items in by_subject.items():
        with open(os.path.join(outdir, f"{subject}_blind.json"), "w", encoding="utf-8") as fh:
            json.dump(items, fh, indent=1, ensure_ascii=False)
        print(f"{subject}: {len(items)} questions exported")


# ---- matching rules mirrored from AnswerEvaluator.kt ----
def norm(s):
    s = str(s).strip().rstrip(".!?")
    s = re.sub(r"\s+", " ", s).replace("’", "'").replace("−", "-").replace("–", "-").replace("—", "-")
    return s.lower()


def to_num(s):
    if s in NUMBER_WORDS:
        return Decimal(NUMBER_WORDS[s])
    c = s.replace(",", "").replace("$", "").replace("%", "").strip()
    if not c or not any(ch.isdigit() for ch in c):
        return None
    try:
        return Decimal(c)
    except InvalidOperation:
        return None


def typed_match(given, expected, ignore_articles):
    g, e = norm(given), norm(expected)
    if ignore_articles:
        g = re.sub(r"^(a|an|the) (?=\S)", "", g)
        e = re.sub(r"^(a|an|the) (?=\S)", "", e)
    if not g:
        return False
    if g == e:
        return True
    gn, en = to_num(g), to_num(e)
    return gn is not None and en is not None and gn == en


def is_correct(q, ans):
    t = q["type"]
    arts = q["subject"] != "WRITING"
    if t in ("MULTIPLE_CHOICE", "PASSAGE_BASED"):
        return ans == q["correctAnswer"]
    if t == "MULTI_SELECT":
        return isinstance(ans, list) and set(ans) == set(q["correctAnswers"])
    if t == "FILL_IN_BLANK":
        accepted = [a for a in [q.get("correctAnswer", "")] + (q.get("correctAnswers") or []) if str(a).strip()]
        return isinstance(ans, (str, int, float)) and any(typed_match(str(ans), a, arts) for a in accepted)
    if t == "CLOZE":
        return isinstance(ans, list) and len(ans) == len(q["blanks"]) and all(typed_match(str(g), e, arts) for g, e in zip(ans, q["blanks"]))
    if t == "ORDERING":
        return ans == q["orderedItems"]
    return True


def key_text(q):
    t = q["type"]
    if t == "MULTI_SELECT":
        return q["correctAnswers"]
    if t == "CLOZE":
        return q["blanks"]
    if t == "ORDERING":
        return q["orderedItems"]
    if t == "FILL_IN_BLANK":
        return [q.get("correctAnswer")] + (q.get("correctAnswers") or [])
    return q["correctAnswer"]


def compare(outdir):
    bank = load_bank()
    total = agree = 0
    report = []
    missing = []
    for path in sorted(glob.glob(os.path.join(outdir, "*_answers.json"))):
        answers = json.load(open(path, encoding="utf-8"))
        subject = os.path.basename(path).replace("_answers.json", "")
        subject_ids = [i for i, q in bank.items() if subject_dir(q) == subject]
        missing += [i for i in subject_ids if i not in answers]
        for qid in subject_ids:
            if qid not in answers:
                continue
            q = bank[qid]
            total += 1
            if is_correct(q, answers[qid]):
                agree += 1
            else:
                report.append({"id": qid, "type": q["type"], "gradeLevel": q.get("gradeLevel"),
                               "prompt": q["prompt"], "passage": q.get("passage"), "choices": q.get("choices"),
                               "key": key_text(q), "blindAnswer": answers[qid], "explanation": q.get("explanation")})
    with open(os.path.join(outdir, "disagreements.json"), "w", encoding="utf-8") as fh:
        json.dump(report, fh, indent=1, ensure_ascii=False)
    print(f"compared {total}, agreed {agree}, disagreed {len(report)}, unanswered {len(missing)}")
    if missing:
        print("unanswered ids:", ", ".join(missing[:40]), "..." if len(missing) > 40 else "")
    for r in report:
        print(f"  {r['id']}: key={json.dumps(r['key'], ensure_ascii=False)[:80]} blind={json.dumps(r['blindAnswer'], ensure_ascii=False)[:80]}")


if __name__ == "__main__":
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="backslashreplace")
    {"export": export, "compare": compare}[sys.argv[1]](sys.argv[2])
