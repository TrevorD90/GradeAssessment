# Critical Thinking Question Bank: Independent Review

Scope: `src/main/resources/questions/critical_thinking/critical_thinking_grade_{K,1..10}.json`, 155 questions.
Reviewer: second, independent reviewer (did not write the bank). Every item was checked; none were sampled.

## Method

- Solved every item before reading the key.
- Enumerated every ordering, seating, assignment and truth-teller/label puzzle in Python (`python -I`). These were g2_006, g3_007, g4_010, g5_009, g6_009, g7_007, g8_009, g9_010, g5_014, g7_014, g1_011, g2_014, g3_012, g4_008, g4_013, g5_011, g6_014, g8_011 and g9_015. Each has exactly one solution, and it matches the key.
- Checked the categorical syllogisms (g4_011, g7_009, g8_002, g8_012) by searching every model with a 3-element universe. In each one, the keyed choices are exactly the choices that follow necessarily.
- Checked propositional items by truth table: g7_003, g7_011, g9_004, g9_009, g9_012, g10_002, g10_009, g10_010 and the four-card task g10_013 (keyed E and 7, which is correct).
- Recomputed the statistics items. g10_012: 1 / (1 + 0.05 x 999) is about 1.96%, so "about 2%" is right. g10_007: (9 x 10 + 510) / 10 = 60, so the mean is right and the median is 10. g9_014: 1 in a million to 2 in a million is a doubling of relative risk, and the absolute change is tiny.
- Checked that every named fallacy fits its example: ad hominem (g8_001, g9_001), false dilemma (g8_004, g9_002), slippery slope (g9_005), straw man (g10_001), appeal to popularity (g10_004), circular reasoning (g7_012) and hasty generalization (g4_001, g6_001). All are correct.
- Ran `python tools/validate_question_bank.py critical_thinking` after the edits: **0 errors, 0 warnings**.

## Per-file summary

"Content issues" means wrong, ambiguous, knowledge-dependent, mis-tagged or under-accepting scoring. "Length cue" means the key was conspicuously the longest or the only hedged choice. That is a separate coordinator request, covered in its own section below.

| File | Checked | Content issues found | Length-cue items | Items changed |
|---|---|---|---|---|
| grade_K | 14 | 0 | 0 changed (3 small-gap items left) | 0 |
| grade_1 | 14 | 0 | 0 changed (2 small-gap items left) | 0 |
| grade_2 | 14 | 0 | 0 changed (2 small-gap items left) | 0 |
| grade_3 | 14 | 1 (g3_007 too easy for MEDIUM) | 2 | 3 |
| grade_4 | 14 | 1 (g4_008 needs outside knowledge) | 4 | 5 |
| grade_5 | 14 | 3 (g5_004 and g5_014 answer variants; g5_009 too easy for MEDIUM) | 2 | 5 |
| grade_6 | 14 | 0 | 6 | 6 |
| grade_7 | 14 | 1 (g7_014 answer variant) | 5 (+1 gap narrowed) | 7 |
| grade_8 | 14 | 2 (g8_007 ambiguous key; g8_011 answer variant) | 5 (+3 gaps narrowed) | 10 |
| grade_9 | 15 | 0 | 4 | 4 |
| grade_10 | 14 | 0 | 5 | 5 |
| **Total** | **155** | **8** | **33 fixed + 4 narrowed** | **45** |

No item had a wrong key. No item was replaced, moved between grades, or retagged. Every file still has at least 4 EASY, 4 MEDIUM and 4 HARD items (5/5/4 in every file; grade 9 is 5/6/4).

## Content changes

| id | Problem | Change |
|---|---|---|
| g3_007 (ORDERING) | The clues were listed in answer order and each one fixed a position almost directly (Rio > Sky > Tam, Uma last). A child could copy the names down, so MEDIUM overstated it. | New clues, scrambled and all relational: "Uma finished right after Rio. Sky finished before Tam. Tam finished before Rio." The answer is now Sky, Tam, Rio, Uma. Enumeration shows exactly one solution, and removing any one clue gives 3 or 4 solutions, so every clue is needed. The explanation was rewritten. |
| g5_009 (ORDERING) | Same problem: Ana won, Dev right after, Ben before Cruz, Eli last, which is nearly a direct readout. | New clues: "Dev finished right after Ben. Ana finished before Eli. Ben finished right after Eli. Cruz finished before Ana." The answer is now Cruz, Ana, Eli, Ben, Dev. Enumeration shows exactly one solution, and removing any clue gives 3 or 4 solutions. The explanation was rewritten. |
| g4_008 | Solving it depended on outside knowledge: which of tennis, soccer and swimming use a ball. | Added "Tennis and soccer use a ball; swimming does not." to the prompt. |
| g8_007 | The keyed "irrelevant" sentence ("our school's building is over 50 years old") could be argued to be relevant: an old building is costly to run, so a four-day week could save more. A careful reasoner could object. | Replaced it in the passage and in the choice with "Our school's mascot is a falcon." The explanation was updated. |
| g5_004 (FILL) | Captain : ___. Reasonable typed answers would have been marked wrong. | Added the accepted answers "vessel", "a vessel", "the ship", "the boat", "ships" and "boats". |
| g5_014 (FILL) | "seat three" was not accepted. | Added it. |
| g7_014 (FILL) | "seat four" was not accepted. | Added it. |
| g8_011 (FILL) | "box two" was not accepted. | Added it. |

## Longest-answer cue (coordinator request)

The measurement covers MULTIPLE_CHOICE and PASSAGE_BASED items whose longest choice is at least 15 characters (96 items). It counts how often the key is the single longest choice.

- **Before: 55 / 96 = 57.3%**
- **After: 22 / 96 = 22.9%** (target was 35% or less)

I deliberately did not push the figure close to 0%. A key that is never the longest becomes a reverse cue. Chance with 3 or 4 choices is about 25 to 33%.

Approach: wherever the key carried the only qualification or reasoning, a distractor got the same "because..." or hedged form, built on faulty reasoning (affirming the consequent, the inverse, "large sample, so reliable", and similar). Some long keys were also trimmed. After each change I re-checked that exactly one choice is defensible.

| id | Change (key = keyed answer) |
|---|---|
| g3_010 | All three distractors lengthened, e.g. "All kinds of fruit must be making Kai itchy." |
| g3_013 | Distractor: "Sam is on the Red Team for sure, because he wears red." (the converse error) |
| g4_001 | Distractor: "Her brother is not really tall, so her rule is wrong about him." |
| g4_009 | Distractor: "How many stores sell Zoom Shoes, and how much do they cost?" |
| g4_012 | Distractor: "Zed's best friend, who uses the slide every day, agrees with him completely." |
| g4_014 | Key trimmed to "Whether anything else changed, such as the water or the plant food". Distractor lengthened (fish names). |
| g5_007 | The mayor's-color sentence was lengthened in both the passage and the choice. It is still irrelevant. |
| g5_013 | Distractor: "...instead of water, since juice is stickier." |
| g6_001 | Distractor: "...more kinds of animals, like birds and fish." |
| g6_002 | Key trimmed. Distractor: "Reading lots of books every day makes students' feet grow faster than usual." |
| g6_006 | Distractor: "The dog is not under the bed, because there is no thunder today." (the inverse error) |
| g6_010 | Key trimmed to "Several large studies found that language learners got better at solving problems." Three distractors lengthened. The explanation was updated. |
| g6_011 | Distractor: "Machine X must be a Grob, because all Grobs have four wheels." (the converse error) |
| g6_013 | Distractor lengthened (walking home). It is still irrelevant. |
| g7_001 | Distractor: "Tests are so hard that students feel they need to cheat to pass." |
| g7_002 | Key trimmed. Distractor: "Crime actually goes down in summer, when more people are outside watching the streets." |
| g7_004 | Key trimmed. The "proves it true" distractor was lengthened. |
| g7_006 | Distractor: "Fertilizer definitely makes all plants grow taller, since Group A grew 30% more." |
| g7_008 | Key trimmed. The "Yes" distractor was lengthened ("...and a neighbor noticed a change too"). |
| g8_003 | Distractor: "Both are equally credible, because both are written documents that were made public." |
| g8_008 | Key trimmed. Distractor: "Grades are reported as letters, which makes them hard to compare across teens." |
| g8_012 | Distractor: "No students on the bus are seniors, since some of them take Art 1." |
| g8_013 | Key trimmed to "...more than music: people and time of day." Distractor lengthened. |
| g8_014 | Key trimmed. Distractor: "It is certainly true, because 200 students is a large sample, and large samples are reliable." |
| g9_007 | Key trimmed. Distractor: "...so the link must be pure coincidence." |
| g9_008 | Key trimmed. Distractor lengthened (drivers feel watched). This does not weaken the argument. |
| g9_013 | Distractor: "Handwriting has been taught in schools for much longer than typing has." |
| g9_014 | Distractor: "It is false, because going from 1 to 2 in a million is not really doubling." |
| g10_003 | Key trimmed. Distractor: "Historian B's evidence is clearly stronger, because letters show what leaders really thought." |
| g10_006 | All four choices rewritten in parallel form: "Valid/Invalid, ..., so it does/does not prove its conclusion." The key is unchanged in meaning. |
| g10_007 | Key trimmed to "Correct but misleading: ...". All distractors now give a reason ("It is wrong, because..." / "It is fair, because..."). |
| g10_008 | Key trimmed to "Randomly assign the supplement or a look-alike fake pill without saying which, then give a memory test." Two distractors lengthened. |
| g10_014 | Key trimmed to "Elite Prep's students would not have earned as much at other schools." Distractor lengthened. |
| g7_013, g8_001, g8_004, g8_010 | Gap narrowed only: one distractor lengthened, but the key is still the longest. These were the largest remaining gaps. |

The 22 items where the key is still the longest mostly have small gaps of 1 to 10 characters. They include K–2 items with 3 short choices, and g10_004, where the choices are fallacy names.

## Doubts left in place

- **g10_004**: The key "Appeal to popularity" is the longest choice because of how long the fallacy names are. The only longer-or-equal option I found was "Hasty generalization", which would invite a debate about whether it fits. I left the item as it is.
- **g1_013** ("Some of the cookies have nuts... We cannot tell"): In strict logic, "some" allows "all". That only strengthens the keyed "cannot tell", so the key is safe. The explanation says "part may not", which is acceptable.
- **g6_008** (MULTI_SELECT, weakens the "lucky socks" claim): "The team has also won games when Lena wore other socks" shows the socks are not necessary, not that they did not help this time. I judge it a legitimate weakener at grade 6, but a very strict reader could argue it.
- **g4_012**: The new distractor (a friend who uses the slide every day) is first-hand testimony. The thermometer readings over five days are clearly stronger evidence, so the key stays unique. Flagged only because this choice is now more attractive.
- **g6_002** is tagged EASY. Spotting a confounder (age) may be MEDIUM for some 6th graders. I left the tag because the third factor is well signposted.
- **g2_011** (Pip cannot fly, so Pip is not a bird): real flightless birds exist. The premise says "All the birds in the park can fly", so the item is valid within its own terms. A child who knows about penguins may hesitate, but the deduction is sound.
- **K–2 reading load**: Several prompts (e.g. g2_006, g2_010) are long for independent reading at K–1. I assume the app reads prompts aloud for K–2, and I did not verify this. `[REVIEW NEEDED: confirm read-aloud for K–2]`
- piParameters were not touched. The changed items keep their difficulty tags, so regenerating them changes nothing for these items.

## Decision Log

- [DECISION: For the length cue, aimed at about 23% rather than 0%, so that "never the longest" does not become a reverse cue.]
- [DECISION: For g3_007 and g5_009, chose option (a), a scrambled and relational clue rewrite, over retagging them EASY. Uniqueness and the necessity of every clue were verified by enumeration.]
- [DECISION: For g8_007, replaced the arguable irrelevant sentence instead of rewording the question, because any "old building" sentence can be tied to cost.]
- [ASSUMPTION: K–2 prompts are read aloud by the app.]

## Length-position rebalance

The earlier "key is the longest" fix lengthened one distractor per item, which pushed many keys into second place. As a result, "always pick the 2nd-longest choice" beat chance. This pass evens out which length position holds the key.

Scope: MULTIPLE_CHOICE and PASSAGE_BASED items whose longest choice is at least 15 characters (96 items). An item where the key's length ties with another choice gives no length cue, so it is counted as "tie".

| Key position | Before | After |
|---|---|---|
| Longest | 22 (23%) | 24 (25%) |
| 2nd-longest | 39 (41%) | 29 (30%) |
| 3rd-longest | 12 (12%) | 20 (21%) |
| 4th-longest | 5 (5%) | 5 (5%) |
| Tie (no cue) | 18 (19%) | 18 (19%) |

`python -I tools/validate_question_bank.py social_studies critical_thinking` now reports 0 errors and 0 warnings.

10 items were edited. Each edit shortened or lengthened one distractor. No key (correctAnswer) text changed, and piParameters were not touched. Each lengthened distractor gained a flawed reason, never a second valid answer.

| id | Change | New key position | Why it is still single-answer |
|---|---|---|---|
| g6_006 (dog and thunder, deduction) | Distractor "The dog is not under the bed, because there is no thunder today." shortened to "The dog is not under the bed." | Longest | It is still the denying-the-antecedent error. The rule says nothing about days without thunder, so only "may or may not" follows necessarily. |
| g4_001 (Emma's tall brother) | Distractor "Her brother is not really tall, so her rule is wrong about him." shortened to "Her brother is not really tall." | Longest | It contradicts the stated premise and names no flaw. Generalizing from one example is still the only flaw offered. |
| g5_006 (rain and canceled game, deduction) | Distractor "It is raining for sure." lengthened to "It is raining for sure, because the game is canceled." | 3rd | The affirming-the-consequent error is now explicit. "It may or may not be raining." is still the only conclusion that follows. |
| g3_011 (Lee's star, deduction) | Distractor "Lee does not like books." lengthened to "Lee does not like to read any books." | 3rd | The rule still says nothing about this. Only "Lee did not finish the book" follows (modus tollens). |
| g4_011 (library cards and chess, deduction) | Distractor "No chess players have a library card." lengthened to "No chess players in Ms. Hill's class have a library card." | 3rd | The narrower claim directly contradicts the premises, because those chess players are her students and all of them have cards. The key still follows necessarily. |
| g8_012 (officers, seniors, Art 1, deduction) | Distractor "Some club officers take Art 1." lengthened to "Some club officers take Art 1, since some bus riders do." | 3rd | The claim is impossible, because officers are seniors and no senior takes Art 1. The added "since" clause is a non sequitur. The key still follows necessarily. |
| g6_011 (Grobs and Zents, deduction) | Distractor "Machine X cannot be a Grob or a Zent." lengthened to "Machine X cannot be a Grob or a Zent, since we were not told it is one." | 3rd | The new clause argues from ignorance: not being told something does not make it false. "Could be a Grob, but we cannot be certain" is still the only correct conclusion. |
| g7_006 (fertilizer experiment) | Distractor "Sunlight has no effect on plant growth." lengthened to "Sunlight has no effect on plant growth, since both groups got the same amount." | 3rd | Holding sunlight constant tests nothing about its effect, so the added reason is the flaw. The cautious fertilizer conclusion is still the only reasonable one. |
| g9_013 (handwriting vs laptop, strengthen) | Distractor "The quiz was given on a Friday." lengthened to "The quiz was given on a Friday, right after a long school assembly." | 3rd | Both groups took the same quiz at the same time, so the detail says nothing about the comparison. Random assignment is still the only strengthener. |
| g5_013 (paper towel test) | Distractor "Brand B costs more than Brand A." lengthened to "Brand B costs more than Brand A at most of the stores in town." | 3rd | Price still has nothing to do with whether the test was fair. The unequal water is still the only flaw. |

Doubts:
- g9_013: a strict reader might argue that a long assembly could lower scores. It affects both groups equally, so it cannot strengthen the hand-versus-laptop conclusion. I judged it safe.
- g4_001 now has two distractors of the same length (31 characters). This does not affect the key's position or whether it is unique.
- The 4th-longest share stayed at 5%. Moving a key to 4th usually means lengthening two distractors in one item, so I did not do it. A low 4th-longest share is not a usable cue.

Decision Log (length-position rebalance):
- [DECISION: Edited only distractors, never key text, so every correctAnswer is unchanged.]
- [DECISION: Moved 2 keys to "longest" and 8 to "3rd", because "longest" was already at 23%. The result is 25/30/21/5% plus 19% ties, with no position above 35%.]
- [DECISION: For the deduction items (g3_011, g4_011, g5_006, g6_006, g6_011, g8_012), I re-checked that the key still follows necessarily and that each edited distractor either contradicts the premises or commits a named fallacy.]
