#!/usr/bin/env python3
"""
Cross-subject heuristic audit of the question bank (read-only).

Complements validate_question_bank.py (which checks structure) by flagging
content smells a human reviewer would look for:
  - near-duplicate prompts (same item asked twice, possibly in different grades)
  - CLOZE blanks missing from the prompt's word bank
  - multiple-choice keys that are conspicuously the longest choice ("test-wise" giveaway)
  - correct answer text repeated verbatim in the prompt
  - FILL_IN_BLANK answers likely to be typed differently (units, fractions, multi-word)
  - characters that may not render in the desktop UI font
  - correct-answer position imbalance per file

Usage: python tools/audit_question_bank.py [subject_dir ...]
"""
import collections
import difflib
import glob
import json
import os
import re
import sys

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources", "questions"))
SAFE = set(chr(c) for c in range(32, 127)) | set("\n’‘“”—–éèñüöäáíóúç")


def norm(s):
    return re.sub(r"[^a-z0-9 ]", "", s.lower()).strip()


def main():
    # The Windows console defaults to cp1252, which cannot print the characters this audit reports.
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="backslashreplace")
    wanted = set(sys.argv[1:])
    items = []
    for path in sorted(glob.glob(os.path.join(ROOT, "**", "*_grade_*.json"), recursive=True)):
        rel = os.path.relpath(path, ROOT).replace("\\", "/")
        if wanted and rel.split("/")[0] not in wanted:
            continue
        for q in json.load(open(path, encoding="utf-8")):
            items.append((rel, q))

    flags = collections.defaultdict(list)

    # Near-duplicate prompts across the whole bank (bucketed by subject to keep this O(n^2) small).
    by_subject = collections.defaultdict(list)
    for rel, q in items:
        by_subject[q["subject"]].append(q)
    for subject, qs in by_subject.items():
        for i in range(len(qs)):
            a = norm(qs[i]["prompt"] + " " + (qs[i].get("passage") or "")[:200])
            for j in range(i + 1, len(qs)):
                b = norm(qs[j]["prompt"] + " " + (qs[j].get("passage") or "")[:200])
                if qs[i].get("passage") and qs[i].get("passage") == qs[j].get("passage"):
                    continue  # questions sharing one passage are expected to look alike
                if abs(len(a) - len(b)) > 40:
                    continue
                r = difflib.SequenceMatcher(None, a, b).ratio()
                if r > 0.88:
                    flags["near-duplicate"].append(f"{qs[i]['id']} ~ {qs[j]['id']} ({r:.2f})")

    pos = collections.defaultdict(collections.Counter)
    for rel, q in items:
        t = q["type"]
        prompt = q.get("prompt", "")
        text = prompt + " " + (q.get("passage") or "") + " " + " ".join(q.get("choices") or [])
        bad = sorted(set(ch for ch in text if ch not in SAFE))
        if bad:
            flags["unusual-characters"].append(f"{q['id']}: {''.join(bad)}")

        if t in ("MULTIPLE_CHOICE", "PASSAGE_BASED"):
            ch = q["choices"]
            ans = q["correctAnswer"]
            pos[rel][ch.index(ans)] += 1
            lens = sorted(len(c) for c in ch)
            if len(ans) == lens[-1] and len(lens) > 2 and lens[-1] > 1.6 * lens[-2] and lens[-1] > 25:
                flags["key-is-much-longest-choice"].append(f"{q['id']}: '{ans[:60]}'")
            if len(ans) > 4 and norm(ans) and norm(ans) in norm(prompt) and not q.get("passage"):
                flags["answer-text-in-prompt"].append(f"{q['id']}: '{ans[:60]}'")
        if t == "CLOZE":
            bank_match = re.search(r"(word bank|choose from|use these words|words?:)(.*)", prompt, re.I)
            for blank in q.get("blanks") or []:
                if bank_match and blank.lower() not in bank_match.group(2).lower():
                    flags["cloze-blank-not-in-word-bank"].append(f"{q['id']}: '{blank}'")
            if not bank_match:
                flags["cloze-without-word-bank"].append(q["id"])
        if t == "FILL_IN_BLANK":
            answers = [q.get("correctAnswer", "")] + (q.get("correctAnswers") or [])
            main_ans = answers[0]
            if "/" in main_ans and not any("." in a for a in answers):
                flags["fib-fraction-without-decimal-variant"].append(f"{q['id']}: {answers}")
            if len(main_ans.split()) > 2:
                flags["fib-multi-word-answer"].append(f"{q['id']}: '{main_ans}'")
            if q.get("gradeLevel") == 0:
                flags["fib-in-kindergarten"].append(q["id"])

    for rel, counter in pos.items():
        total = sum(counter.values())
        if total >= 8 and max(counter.values()) > 0.45 * total:
            flags["answer-position-skew"].append(f"{rel}: {dict(counter)}")

    for name in sorted(flags):
        print(f"## {name} ({len(flags[name])})")
        for line in flags[name]:
            print("  " + line)
    print(f"\nScanned {len(items)} questions.")


if __name__ == "__main__":
    main()
