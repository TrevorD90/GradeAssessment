# Math Question Bank — Independent Review

Reviewer: second-pass review agent (did not author the bank). Date: 2026-11-13.
Scope: `src/main/resources/questions/math/math_grade_{K,1..10}.json` (176 questions, all reviewed; no sampling).

## Method

- Every prompt was solved independently before reading the key. All computational items (about 50) were re-checked in Python
  (exact fractions where relevant); the system-of-equations, vertex, zero-finding and triangle-angle items were checked by
  enumeration / law of cosines.
- Each item checked for: correct key and explanation, a single defensible answer (no true distractors), clarity without images,
  Common Core grade fit, EASY/MEDIUM/HARD ordering, and scoring mechanics as implemented in
  `AnswerEvaluator.kt` (exact-match MC, set-equal MULTI_SELECT, normalized typed matching for FILL_IN_BLANK/CLOZE,
  exact order for ORDERING). Note: CLOZE `blanks` accept exactly one string per gap — no variants are possible.
- `python tools/validate_question_bank.py math` → `176 questions, 0 error(s), 0 warning(s)` after edits.
- `piParameters` were not touched.

## Per-file summary

| File | Questions checked | Issues found | Issues fixed | E / M / H |
|---|---|---|---|---|
| math_grade_K.json | 16 | 1 (minor, CLOZE answer form) | 1 | 5 / 6 / 5 |
| math_grade_1.json | 16 | 1 (minor, CLOZE answer form) | 1 | 4 / 8 / 4 |
| math_grade_2.json | 16 | 1 (minor wording) | 1 | 5 / 5 / 6 |
| math_grade_3.json | 16 | 2 (typed-answer variants) | 2 | 5 / 6 / 5 |
| math_grade_4.json | 16 | 1 (typed-answer variant) | 1 | 5 / 6 / 5 |
| math_grade_5.json | 16 | 1 (typed-answer variant) | 1 | 4 / 7 / 5 |
| math_grade_6.json | 16 | 3 (2 confusing explanations, 1 variant) | 3 | 4 / 7 / 5 |
| math_grade_7.json | 16 | 2 (typed-answer variants) | 2 | 4 / 6 / 6 |
| math_grade_8.json | 16 | 2 (typed-answer variants) | 2 | 4 / 7 / 5 |
| math_grade_9.json | 16 | 0 | 0 | 4 / 6 / 6 |
| math_grade_10.json | 16 | 2 (typed-answer variants) | 2 | 4 / 6 / 6 |
| **Total** | **176** | **16** | **16** | |

**No wrong answer keys, no wrong explanations (mathematically), no true distractors, and no non-unique ORDERING items were found.**
All issues are clarity or scoring-robustness fixes. Grade 9 had nothing wrong.

## Changes

| id | What was wrong | What changed |
|---|---|---|
| math_gk_015 | CLOZE `9 = 5 + ___` gave no answer-form hint; CLOZE cannot accept "four", so a kindergartner typing the word would be marked wrong. | Prompt now "Type the number that makes this true: 9 = 5 + ___". |
| math_g1_008 | Same CLOZE issue ("forty-four" would be rejected). | Prompt now "Type the number. 10 more than 34 is ___." |
| math_g2_016 | "A bar graph shows votes…" implies a picture the app cannot show; a child may look for a missing graph. | Reworded: "A class made a bar graph … The Apples bar shows 7 votes, …". Key unchanged (5). |
| math_g3_002 | "32 cookies" would be rejected. | Added accepted answer "32 cookies". |
| math_g3_013 | "6cm" / "6 centimeters" rejected (only "6", "6 cm" listed). | Added "6cm", "6 centimeters". |
| math_g4_014 | "12ft" rejected. | Added "12ft". |
| math_g5_013 | "1.75 yards" / "1 3/4 yards" rejected. | Added both. |
| math_g6_004 | Explanation "Replace x with 5: 3 x 5 + 4" uses the letter x both as the variable and as the multiplication sign, in the very item that teaches substitution. | Explanation now "3x means 3 times x, so 3x + 4 = 3(5) + 4 = 15 + 4 = 19." |
| math_g6_008 | Same x-as-times confusion in "Check: 4 x 13 = 52" for an equation in x. | Changed to "Check: 4(13) = 52." |
| math_g6_014 | Surface-area answer with units ("52 square cm", "52 cm^2" …) rejected. | Added "52 square cm", "52 sq cm", "52 cm^2", "52 square centimeters". |
| math_g7_008 | "78.5 square cm" etc. rejected. | Added "78.5 square cm", "78.5 sq cm", "78.5 cm^2", "78.5 square centimeters". |
| math_g7_013 | "30 percent" rejected ("30%" already worked via % stripping). | Added "30 percent". |
| math_g8_010 | "125.6 cubic cm" / "125.6 cm^3" rejected. | Added "125.6 cubic cm", "125.6 cm^3", "125.6 cubic centimeters". |
| math_g8_016 | "12ft" rejected. | Added "12ft". |
| math_g10_002 | Students often write "x = 5" for a coordinate; rejected. | Added "x = 5", "x=5". |
| math_g10_013 | "60 square units" rejected. | Added "60 square units". |

## Left as is (with reasoning)

- **math_g7_002** (speed = 150 mi / 3 h): finding a unit rate with whole numbers is really 6.RP.A.3b (grade 6), tagged 7.RP.A.2b
  EASY. It is one grade off, within the "about one grade" tolerance, and acceptable as a grade-7 EASY item. If tighter fit is wanted,
  replace with a complex-fraction unit rate (7.RP.A.1), e.g. "1/2 mile every 1/4 hour → 2 mph". [REVIEW NEEDED: optional]
- **math_g6_012** (mean of 6, 8, 8, 9, 14) is tagged HARD; it is a routine procedure and could be MEDIUM. The median/mode
  distractor (8) does make it discriminating, so the tag was kept.
- **math_g9_001** (3(x − 4) = 2x + 5) is essentially an 8.EE.C.7b skill; fine as a grade-9 EASY item.
- **math_g10_010** (order angles from side lengths) is tagged HSG-CO.C.10; the side–angle inequality is not named verbatim in
  CCSS but is standard grade-10 geometry content. Verified with the law of cosines (C ≈ 33.6°, B ≈ 50.7°, A ≈ 95.7°).
- **math_g10_016** (8^(2/3)) is HSN-RN.A.2, usually Algebra 1/2; tagged grade 10 HARD, which is reasonable.
- **Typed answers in other FILL_IN_BLANK items** (e.g. g2_007 "34", g4_009 "24") accept digits only; the prompt says
  "Type the number" / "Write your answer as a number", so spelled-out words were not added beyond what the author already listed.
- **Negative numbers** (g7_015 "-4", g10_015 "-3") rely on an ASCII hyphen-minus; a typographic minus (−) would be rejected by
  the evaluator. Children on a keyboard type "-", so no change; this is an evaluator-level limitation, not a bank issue.
- **ORDERING uniqueness**: g7_006, g8_006, g9_006, g9_013 are step-ordering items; each step text names its intermediate
  result (e.g. "to get 2x = 8"), so only one order is consistent. All numeric/fraction/decimal ORDERING items have distinct values.
- Choice-position spread and type variety pass the validator's checks in every file.
