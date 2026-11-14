# Blind Answer-Key Check (2026-11-13)

Goal: confirm that every question's answer key is correct, without the checker being influenced by seeing the key.

## Method
1. `python -I tools/blind_check.py export <dir>` wrote every question with all key fields removed (`correctAnswer`, `correctAnswers`, `blanks`, `orderedItems`, `explanation`, `difficulty`, `piParameters`). Ordering items were shuffled.
2. Nine fresh solver agents, one per subject, each answered every question in their file. They were forbidden to open the project. A search of their activity logs found no access to the question bank, review docs, or tools.
3. `python -I tools/blind_check.py compare <dir>` scored the solver answers with the same matching rules as the app's `AnswerEvaluator` (case, spacing, trailing punctuation, number formats, number words, leading articles outside Writing).
4. Validation of the comparison itself:
   - Positive test: feeding the real keys back in gave 1665/1665 agreement.
   - Negative test: of 90 planted wrong answers, 85 were caught. The other 5 were reversed multi-select lists, which are still correct.

## Result
**1665 of 1665 blind answers matched the answer keys. 0 disagreements.**

| Subject | Questions | Agreed |
|---|---|---|
| Math | 176 | 176 |
| Reading | 195 | 195 |
| Writing | 188 | 188 |
| Science | 195 | 195 |
| Arts | 230 | 230 |
| World History | 186 | 186 |
| Social Studies | 172 | 172 |
| Critical Thinking | 155 | 155 |
| IQ Assessment | 168 | 168 |

## Solver flags (no key was wrong)
Solvers flagged 17 items as worth a look. Five had wording problems and were fixed:

- `reading_gk_016`: the question asked where the pup ran, but the text never said it ran. Added "The pup ran outside."
- `reading_g9_007`: the keyed choice said both groups were "near the pump", but the passage only places the brewery nearby. Changed to "in the outbreak area".
- `reading_g2_006`: choices used the name "Sofia" before Maya learns it. Changed to "the new girl".
- `iq_assessment_g9_009`: "tipped directly away" was open to interpretation. Now reads "rolls once away from you, over its far bottom edge". The answer is still 2.
- `critical_thinking_g5_004`: added a hint that the analogy is about what each person steers, so a sports "team" is not a reasonable reading.

The other 12 were judged fine as written:
- `arts_g5_018`: the climax choice is the only defensible option.
- `critical_thinking_g6_008`: the key already lists both weakening statements.
- `iq_assessment_g10_013`: the key matches the contrapositive reading.
- `reading_g8_006`: the mill closing is clearly implied.
- `social_studies_g2_003`: "legend" was already accepted.
- `social_studies_g9_002`: matching ignores case.
- `social_studies_g4_010`: uses the standard textbook order of city, county, state.
- `social_studies_g2_014`: the prompt says "usually".
- `world_history_g4_003`: "Greece" and "ancient Greece" are both accepted.
- `world_history_g7_014`: the chronology holds for any Renaissance start date.
- `writing_g9_017` and `writing_g7_016`: stylistic notes only; the key is still clearly best.

## How to re-run
```
python -I tools/blind_check.py export <dir>
# have an independent solver write <dir>/<subject>_answers.json without seeing the bank
python -I tools/blind_check.py compare <dir>
```
