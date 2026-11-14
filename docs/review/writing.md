# Writing Question Bank — Independent Review

Scope: `src/main/resources/questions/writing/writing_grade_{K,1..10}.json` (188 items).
Method: every item answered independently before reading the key, then checked for correctness, uniqueness, clarity, grade fit, difficulty order and scoring mechanics (exact-match MC, set-equal MULTI_SELECT, case-insensitive FILL_IN_BLANK/CLOZE, single-order ORDERING). `piParameters` were not touched.
Validator: `python tools/validate_question_bank.py writing` → 188 questions, 0 errors, 0 warnings.

## Per-file summary

| File | Questions checked | Content issues found | Content issues fixed | Length-cue rebalances |
|---|---|---|---|---|
| writing_grade_K.json | 17 | 0 | 0 | 3 (gk_012, gk_015, gk_017) |
| writing_grade_1.json | 17 | 0 | 0 | 0 |
| writing_grade_2.json | 17 | 0 | 0 | 2 (g2_014, g2_017) |
| writing_grade_3.json | 19 | 0 | 0 | 0 |
| writing_grade_4.json | 17 | 0 | 0 | 2 (g4_015, g4_016) |
| writing_grade_5.json | 17 | 0 | 0 | 1 (g5_015) |
| writing_grade_6.json | 17 | 0 | 0 | 2 (g6_013, g6_016) |
| writing_grade_7.json | 17 | 1 (serious) | 1 | 2 (g7_014, g7_017) |
| writing_grade_8.json | 16 | 0 | 0 | 2 (g8_005, g8_013) |
| writing_grade_9.json | 17 | 0 | 0 | 2 (g9_002, g9_010) |
| writing_grade_10.json | 17 | 1 (minor) | 1 | 3 (g10_008, g10_013, g10_014) |
| **Total** | **188** | **2** | **2** | **19** |

No keys were wrong. The bank was generally accurate.

## Content changes

### writing_g7_011 (CLOZE) — two valid answer orders (serious)
- **Problem:** "The students ___ their research papers were due on Friday; ___, many of them started working on Wednesday. ___, the teacher extended the deadline to Monday." The swapped fill also works and reads naturally: "knew…; *however*, many of them started [only] on Wednesday. *Therefore*, the teacher extended the deadline." A student who gave that order would be marked wrong.
- **Fix:** Rewrote the second and third sentences: "…; ___, most of them started working early in the week. ___, a few students still waited until Thursday night to begin." Now only therefore → however works: knowing the deadline does not contrast with starting early, and "still waited" needs a contrast word. The key (knew, therefore, however) is unchanged and the explanation is updated.

### writing_g10_015 (MC, parallelism) — disputed collective-noun agreement (minor)
- **Problem:** Every choice used "the cast … their". Many American usage guides treat "cast" as singular ("its"). It did not affect which choice is right, but it put a disputed usage into the keyed model sentence.
- **Fix:** Changed "the cast" to "the actors" in the prompt and in all four choices. The key is the same choice in the same position.

## Longest-answer giveaway (coordinator request)

Metric: MC/PASSAGE items whose longest choice has 15+ characters, counting how often the key is the single longest choice.

- **Before:** 38 / 96 = **39.6%**
- **After:** 19 / 96 = **19.8%** (target ≤ 35%; chance is about 25%)

Each fix lengthened one existing wrong choice so the key is no longer the single longest. The script checked that only a non-key choice was replaced. Every new distractor is still clearly wrong for the same reason as before:

| id | Distractor changed to | Still wrong because |
|---|---|---|
| gk_012 | "The big brown dog in the yard" | still a fragment (no verb) |
| gk_015 | "The sun is hot and bright today." | off topic |
| gk_017 | "Bears eat fish and berries in the woods." | fact, not opinion |
| g2_014 | "I saw a big bee on a flower at the park yesterday." | personal anecdote, not a conclusion |
| g2_017 | "Hey, thanks a bunch for coming to our class, dude!" | informal/slang |
| g4_015 | "My family puts our blue recycling bin out at the curb every Tuesday." | detail, not a conclusion |
| g4_016 | "The bird went down to the ground and got some kind of animal." | vague words |
| g5_015 | "In many towns across the country, school starts at about 8:00 a.m. on weekdays." | fact that gives no reason |
| g6_013 | "The cafeteria's lunch trays come in several bright colors, including red, blue, and green." | irrelevant to waste |
| g6_016 | "Crosswalks are marked areas of a road, often painted with white stripes, where people on foot can cross safely." | definition; does not state the request |
| g7_014 | "Uniforms also save families time in the morning because students do not have to choose an outfit." | supports the claim; not an opposing view |
| g7_017 | "A comment posted on social media by an anonymous user who says energy drinks are safe" | anonymous, not credible |
| g8_005 | "The writer copied a spelling error exactly as it appeared in the original." | describes [sic], not an ellipsis |
| g8_013 | "Opponents are simply wrong, and anyone who has visited a library on a busy Saturday should be able to see that." | dismissal, no evidence on cost |
| g9_002 | "She went to the store after school; bought milk and bread for dinner." | fragment after semicolon |
| g9_010 | "Public transit systems, including buses, subways, and light rail, exist in many large cities around the world today." | fact, not a debatable claim |
| g10_008 | "According to a gardening guide, gardens need at least six hours of sunlight and about an inch of water each week to produce a healthy crop of vegetables." | irrelevant to eating habits |
| g10_013 | "Opened in 2015 on what was once an empty lot, the Maple Street Community Garden also hosts weekend cooking classes." | the notes do not link the classes to people in need |
| g10_014 | "Therefore, because the new lights are too bright, cities should return to traditional bulbs even though LEDs save money." | contradicts the paragraph's argument |

Left as they are, because the key is only slightly longer and these are plain sentence-level contrasts: g1_014, g2_004, g3_010, g3_015, g4_002, g4_005, g5_016, g6_003, g6_006, g7_002, g7_003, g7_009, g8_001, g8_006, g8_008, g9_003, g9_006, g10_010, g10_012.

## Doubts left unresolved

- **Serial (Oxford) comma — g1_014, g5_004, g5_016, and several keys at grades 9–10.** The keys use the serial comma. No wrong choice is "correct except for leaving out the serial comma", so no item depends on the style choice. I left them as they are. If a future edit adds a non-serial version as a distractor, that item will have two defensible answers.
- **g9_017** — the key "The recipe calls for three spices: cumin, paprika, and turmeric; the rest is up to you." is grammatical: the semicolon joins two independent clauses, and the first one contains a colon list. Some editors find a colon followed by a semicolon awkward. The other choices are clearly wrong, so I kept it.
- **g8_003** (FILL_IN_BLANK) accepts "gave" as well as "presented". Both are correct active-voice completions. Accepting "gave" is lenient but defensible, so I left it.
- **g8_006 / g8_016** test the subjunctive "were". "If I was…" is common in casual speech, but the prompts explicitly ask for the subjunctive or contrary-to-fact form, so the keys are sound.
- **g9_011** is a recall item on ethos, pathos and logos tagged W.9-10.1.B. Its standard fit is loose (closer to RI.9-10.6), but the content is correct. I did not change it.
- **Difficulty order:** gk_006 (MEDIUM) and gk_013 (HARD) test nearly the same skill, but the HARD item combines three conventions, so the order is acceptable. No other problems with the EASY/MEDIUM/HARD order.

## Decision Log

- [DECISION: g7_011 was rewritten, not replaced. The skill tested (cause vs. contrast transitions) and the key were sound; only the context allowed a second answer.]
- [DECISION: The length-cue fix lengthens distractors and never shortens or rewords keys, so no correct answer changes.]
- [DECISION: Rebalanced 19 items, giving 19.8%. That is slightly below chance (25%), but no single cue now favours the key or a distractor across the bank.]
