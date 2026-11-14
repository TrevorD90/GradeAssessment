# IQ_ASSESSMENT question bank: second review

- Scope: `src/main/resources/questions/iq_assessment/iq_assessment_grade_{K,1..10}.json`, 168 questions, all reviewed (no sampling).
- Method:
  - I solved every item myself before reading the key.
  - Python checked every number series, letter series, word or letter manipulation and arithmetic puzzle, plus the percent change and the tournament count.
  - Every seating, ranking and race ORDERING item, and the three-box label puzzle, was checked by brute-force enumeration. Each has exactly one solution.
  - I worked every spatial item by hand: the painted cubes (2x2x2, 3x3x3, 4x4x4), the cube net, fold-and-punch/cut, the die tip, the gears (3 in a row and a ring of 5), the arrow rotate/flip, the clock mirror and the compass turns.
- Validator: `python tools/validate_question_bank.py iq_assessment` reports 0 errors and 0 warnings after the edits.
- `piParameters` were not touched.

## Per-file summary

| File | Questions checked | Issues found | Issues fixed |
|---|---|---|---|
| grade_K | 15 | 3 (gk_010 wording, gk_014 ambiguous question, gk_015 trivial for HARD) | 3 |
| grade_1 | 15 | 0 | 0 |
| grade_2 | 15 | 1 (g2_011 longest-answer cue) | 1 |
| grade_3 | 15 | 2 (g3_014 punch position unstated, g3_011 longest-answer cue) | 2 |
| grade_4 | 15 | 2 (g4_011, g4_014 longest-answer cue) | 2 |
| grade_5 | 15 | 2 (g5_014 mirror type unstated, g5_011 longest-answer cue) | 2 |
| grade_6 | 16 | 1 (g6_012 longest-answer cue) | 1 |
| grade_7 | 15 | 3 (g7_004 depends on how p/d look in the font, g7_009 and g7_011 longest-answer cue) | 3 |
| grade_8 | 15 | 1 (g8_008 longest-answer cue) | 1 |
| grade_9 | 16 | 2 (g9_008, g9_011 longest-answer cue) | 2 |
| grade_10 | 16 | 1 (g10_011 longest-answer cue / weak distractor) | 1 |
| **Total** | **168** | **18** | **18** |

**No answer key was wrong.** Every keyed answer and explanation matched my own solution. Every series has a single simplest rule, and every ORDERING item has a unique solution.

## Changes

### Content and wording fixes

| id | Problem | Change |
|---|---|---|
| iq_assessment_gk_010 | "Say it backward: cat, dog" could be read as spelling each word backward ("tac, god"). | Prompt now reads "Say these words in backward order: cat, dog." |
| iq_assessment_gk_014 | "Who is in front?" is ambiguous: Ann is also in front of Tom. | Prompt now asks "Who is at the very front of the line?" |
| iq_assessment_gk_015 | The HARD item was "order 1, 2, 3 from smallest to biggest". That is pure rote counting and easier than every EASY item in the grade. | Replaced with a 3-person height ordering by transitive inference: Ann > Ben, Cal > Ann, so the order from shortest to tallest is Ben, Ann, Cal. The id is kept; standardCode changed to IQ.FLUID and bloomsLevel to 3. The order is unique. |
| iq_assessment_g3_014 | Fold twice, punch one hole: the answer is 4 only if the punch avoids the folds and the corner where they meet. A punch on the folded corner gives 1 hole, and one on a fold edge gives 2. | Prompt now says the hole is punched "near the middle of the folded paper, away from every edge and fold". |
| iq_assessment_g5_014 | The mirror type was not stated. A wall mirror shows 3:00 as 9:00, but a floor or lake mirror does not. | Prompt now says "in an ordinary wall mirror that faces it". |
| iq_assessment_g7_004 | The lowercase 'pod' rotated 180 degrees relies on p and d being exact point-reflections of each other, which varies with the font (serifs, bowl and stem placement). It also mostly tests letter shape rather than rotation. | Replaced with a font-free rotation item: an arrow points up and is turned a quarter turn clockwise, then a half turn, then another quarter turn clockwise. The total is one full turn, so the answer is Up. The id, the EASY level and the IQ.SPATIAL code are kept. |

### Longest-answer giveaway (coordinator request)

The measurement covers MC/PASSAGE items whose longest choice is at least 15 characters, and counts the key when it is the single longest choice.

- **Before: 13 / 25 = 52.0%**
- **After: 1 / 25 = 4.0%** (the remaining item is g10_003, where the key is 16 characters and the nearest distractor is 15, so it does not stand out)

Each fix keeps exactly one defensible answer.

| id | Change |
|---|---|
| g2_011 (car/boat) | Key trimmed to "Both take people places". Distractors lengthened: "Both have wheels and drive on roads" and "Both have sails that catch the wind". |
| g3_011 (clock/ruler) | Key trimmed to "Both are used to measure". "Both make a ticking sound" is now the longest choice. |
| g4_011 (seed/egg) | Key changed to "Both can grow into a living thing". New distractors: "Both are laid by birds in nests", "Both must be planted in soil to grow", "Both are always round and white". The original distractor "hard to break" was dropped: seeds and eggs both have protective coats, so it was arguably defensible. |
| g4_014 (fold-and-notch) | Choices are now "One small triangle", "A four-sided diamond" (key), "A long, thin straight slit" and "Two separate triangles". |
| g5_011 (key/password) | Key changed to "Both control who can get in". Distractors lengthened to similar size. The explanation was updated. |
| g6_012 (lighthouse/traffic light) | Key changed to "Both use light to guide travelers". New distractors: "Both stand beside the sea to warn ships" and "Both switch between red, yellow and green". This replaces "Both change color", which is partly true of lighthouses with colored sectors. |
| g7_009 (fold-and-cut center) | Key trimmed to "One hole in the middle". Distractors lengthened. |
| g7_011 (praise/criticism) | Key changed to "Both judge what someone did". Distractors lengthened. |
| g8_008 (syllogism) | Distractor "No employees are robots" became "No employees at the school are robots" (still invalid). |
| g9_008 (contrapositive) | Key reworded to "A student who does not study hard is not brilliant". Distractors are now the converse ("If a student studies hard, that student is brilliant"), "Only brilliant students study hard", and the inverse ("If a student is not brilliant, that student does not study hard"). The inverse is the longest choice. The explanation was updated. |
| g9_011 (metaphor/map) | Key changed to "Both stand for something else". A distractor was lengthened to "Both give exact measurements of things". |
| g10_011 (thermostat analogy) | Distractor "thermometer : heat" became "thermometer : temperature". It is now the longest choice and a stronger measure-versus-regulate distractor. |

Key positions across MC/PASSAGE items stay balanced: index 0 = 31, index 1 = 33, index 2 = 32, index 3 = 26.

## Doubts left in place

- **Longest-answer rate overshoot.** The rate is now 4%, below the 25% chance level. A child who uses an "avoid the longest" strategy gains only a little (about 1/3 instead of 1/4 on those 25 items). I judged this better than leaving obvious cues, but a later pass could let the key be marginally longest on 3–5 items.
- **g3_004 (mirror the lowercase b to get d).** This item uses the b/d letters. I kept it because a left-right mirror of b gives d in essentially every common font (stem and bowl just swap sides), and that is the textbook example. It is less font-sensitive than p/d rotation, which is why I replaced g7_004. If the UI uses an unusual display font, revisit it.
- **Knowledge and vocabulary dependence (kept, low risk).**
  - g2_006 asks whether a carrot is a vegetable or a fruit.
  - g5_001 assumes the student knows time units.
  - g6_007 is the Fibonacci series, which some children will recognise rather than derive; the rule can still be derived.
  - g7_006 needs the word "periscope", but the item can be solved without knowing it.
  - g8_006 needs "skeptical", g9_006 "ambiguous", g10_006 "valid/persuasive" and g10_011 "cruise control".
  - These are verbal-reasoning items at grades where the vocabulary is age-appropriate, so I left them in. They load more on vocabulary than pure reasoning.
- **g3_009, g4_009, g6_004 (compass turns)** assume the child knows the N/E/S/W layout. That is standard by grade 3, but it is still school knowledge.
- **g7_012 (bat-and-ball style pen/notebook)** is a well-known Cognitive Reflection Test item, so some students may have seen it. The key ($0.50) is correct.
- **Difficulty calibration** within a grade is plausible, but there is no empirical data behind it. EASY/MEDIUM/HARD are expert judgment only. `[REVIEW NEEDED: calibrate with pilot data]`
- **K items** assume an adult reads the prompt aloud. Several K and grade-1 items ("Look: cup, dog, ball", "Say the numbers backward") are labelled IQ.MEMORY, but they are shown on screen, so they test sequencing rather than memory span.
