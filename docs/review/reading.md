# Reading Question Bank: Independent Review

Scope: `src/main/resources/questions/reading/comprehension/reading_grade_{K,1..10}.json` (195 items) and `src/main/resources/questions/reading/fluency/*.json` (12 timed-fluency forms).

Method: I read every passage and prompt and answered each item from the passage before looking at the key. For each item I then checked the key, whether only one choice is defensible, whether it can be answered from the text alone, the facts in informational passages, that shared passages are identical, grade fit and difficulty order, and that the item works with the scoring code in `AnswerEvaluator.kt`. The CLOZE `blanks` are matched one-to-one with no variants. ORDERING needs an exact list match. The fluency panel renders items in the default sans-serif font, and selections are stored as a Set keyed by item text. The validator (`python tools/validate_question_bank.py reading`) reports **0 errors, 0 warnings** after my edits.

## Summary per file

| File | Checked | Content issues found | Content issues fixed | Longest-answer rebalances |
|---|---|---|---|---|
| reading_grade_K.json | 17 | 0 | 0 | 0 |
| reading_grade_1.json | 17 | 0 | 0 | 2 |
| reading_grade_2.json | 17 | 0 | 0 | 2 |
| reading_grade_3.json | 17 | 0 | 0 | 1 |
| reading_grade_4.json | 18 | 0 | 0 | 2 |
| reading_grade_5.json | 18 | 0 | 0 | 3 |
| reading_grade_6.json | 18 | 1 (minor wording) | 1 | 2 |
| reading_grade_7.json | 18 | 0 | 0 | 3 |
| reading_grade_8.json | 18 | 0 | 0 | 3 |
| reading_grade_9.json | 18 | 0 | 0 | 1 |
| reading_grade_10.json | 19 | 0 | 0 | 4 |
| fluency/letter_naming.json | 4 forms | 3 (I/l ambiguity) | 3 | n/a |
| fluency/nonsense_words.json | 4 forms | 2 forms (non-English spelling patterns) | 2 | n/a |
| fluency/phoneme_seg.json | 4 forms | 0 | 0 | n/a |

The comprehension bank was in good shape. I found **no wrong keys, no ambiguous keys, no factual errors and no passage inconsistencies** in the 195 items. Most of my changes rebalance answer-choice lengths (see below).

## Longest-answer giveaway

The metric is the share of MULTIPLE_CHOICE/PASSAGE_BASED items where the key is the single longest choice, counting only items whose longest choice is 15 or more characters.

- **Before: 51 / 122 = 41.8%**
- **After: 28 / 122 = 23.0%** (target was 35% or less; chance is about 25%)

I fixed 23 items, mostly by lengthening distractors with plausible readings the text does not support and sometimes by shortening the key. Each key still has exactly one defensible answer. The remaining 28 hits are mostly items where the key is only a few characters longer, or verbatim passage quotes (`g1_013`, `g2_015`), which I could not shorten without changing the passage.

| id | Change |
|---|---|
| reading_g1_006 | Distractor "Sam got a new pet" → "Sam got a new pet for his birthday" |
| reading_g1_015 | Distractor "Her mom told her to" → "Her mom told her to hug it" |
| reading_g2_013 | Key shortened to "Squirrel shared with him, but he did not share". Distractors lengthened. "He had lost his carrots in the field" became "He did not like the taste of Squirrel's nuts at all", so no distractor touches the rotting carrots, which a student could otherwise tie to Rabbit's feelings |
| reading_g2_014 | Key shortened to "It is good to share with your friends". Two distractors lengthened |
| reading_g3_008 | Key shortened to "An octopus's body and tricks help it survive". Sharks/eels and crabs distractors lengthened (both are still details, not the main idea) |
| reading_g4_007 | Two detail distractors lengthened (teeth, flooding) |
| reading_g4_015 | Two distractors lengthened (tools and machines; working with farmers and scientists) |
| reading_g5_007 | "Algae eat skeleton" distractor lengthened |
| reading_g5_014 | "Raise taxes" distractor lengthened |
| reading_g5_017 | Two distractors lengthened. Both still contradict the passage: a bleached coral "is not dead", and reefs cover "less than one percent" of the ocean floor |
| reading_g6_008 | Nobel-prize detail distractor lengthened |
| reading_g6_015 | Key shortened to "To show the discovery was surprising and needed proof". Distractor lengthened |
| reading_g7_009 | Note-card distractor lengthened |
| reading_g7_014 | Key shortened from 99 to 65 characters: "By admitting the problems are real but saying they can be solved". Distractor lengthened |
| reading_g7_016 | Key shortened to "Her honesty made the audience trust her". Distractor lengthened |
| reading_g8_004 | Two distractors reworded ("by where they took place", "from most to least important") |
| reading_g8_008 | Cramming distractor lengthened |
| reading_g8_014 | "Mistake" distractor lengthened |
| reading_g9_007 | Key shortened from 128 to 64 characters: "They were near the pump but drank other water and stayed healthy". Two distractors lengthened |
| reading_g10_007 | Key shortened to "It suggested that doctors were causing the deaths". Two distractors lengthened |
| reading_g10_014 | Key changed to "It shows that leaving things out is unavoidable, not a flaw". Mercator distractor lengthened |
| reading_g10_016 | "Forgot cleaning" distractor lengthened |
| reading_g10_017 | Key shortened to "It suggests that repair limits undermine real ownership". "Uncertain" distractor lengthened |

## Content changes

| id | Problem | Fix |
|---|---|---|
| reading_g6_005 | The prompt asked about "the second meet", but the passage only mentions "the first race" and "the next meet". A careful reader could wonder whether the first race was at a meet. | The prompt now reads "What happened to Marcus at the next meet?", which matches the passage wording. |
| LN-001, LN-003, LN-004 (letter_naming) | Each form contained both uppercase **I** and lowercase **l**. LN-001 and LN-003 even place them next to each other. The panel draws them in the app's default sans-serif font, where the two glyphs are practically identical, so a child who says "I" for "l" (or the reverse) could be marked wrong for a font problem, not a reading problem. | I replaced both glyphs in each form with letters that are not already present (no duplicate strings, so selection toggling still works): LN-001 I→Q, l→x (also adds missing q/x); LN-003 I→c, l→m (all 26 letters still covered, as its explanation claims); LN-004 I→i, l→z. Both `fluencyItems` and `fluencyCorrectItems` were updated. |
| NW-001, NW-002 (nonsense_words) | `jiv`, `mev` and `huj` end in v or j. English spelling never ends a word that way, so these items test unusual orthography rather than standard CVC decoding. Standard NWF forms avoid them. | jiv→jit, mev→mep, huj→huk (none duplicate existing items). |

## Fact checks (all passed, no changes)

- **g3 monarchs:** migration of up to about 3,000 miles to Mexico. Milkweed is the only larval food.
- **g5 coral:** reefs cover less than 1% of the ocean floor and hold about 25% of marine species. Polyps build limestone skeletons, zooxanthellae live in the coral, and bleaching is not the same as death.
- **g6 von Frisch:** Austrian, decoded the waggle dance in the 1940s, shared the 1973 Nobel Prize. The direction of the run is set relative to the sun and its duration encodes distance.
- **g7 Dust Bowl:** drought from 1931, "black blizzards", dust darkened the sky over New York (1934), windbreaks and contour plowing, rain returned in 1939. Teen sleep of 8–10 hours matches the AASM recommendation. The Minnesota 8:40 start matches Minneapolis Public Schools.
- **g8 Ebbinghaus:** 1885, nonsense syllables, he was his own only subject. About 56% lost within an hour and about 66% after a day, consistent with his savings data. He also observed the spacing effect.
- **g9 John Snow:** 1854 Soho, over 500 deaths in about 10 days, deaths drawn as bars, Broad Street pump. The brewery workers and the Poland Street workhouse with its own well were spared, and the handle was removed when the outbreak was already waning. The 2014 "boredom" study did use copying from a phone book followed by a plastic-cup uses task.
- **g10 Mercator:** 1569, built for navigation. Africa is about 14 times the area of Greenland (30.4M vs 2.17M km²).
- **g10 Semmelweis:** Hungarian, Vienna, 1847, chlorine handwashing. First-clinic mortality was about 10%, with the midwives' clinic under half that. He was dismissed, died in an asylum in 1865, and was vindicated by Pasteur and Lister.

## Doubts left as is

- **reading_g2_015, reading_g1_013:** the key is the longest choice, but all choices are verbatim passage sentences. I left them, because changing the passage would also change other items that share it.
- **reading_g4_010 (CLOZE):** the article "An ___ designed..." hints at "engineer", since "wetland" also takes "a". The item is still answerable only one way, so I left it.
- **reading_gk_012, g1_010:** the ORDERING prompts list the events in order, so the item tests copying more than comprehension. This is acceptable for K–1 sequencing, and they are tagged MEDIUM, so I kept them.
- **Fluency standard codes:** PS-001/PS-002 are tagged RF.K.2d (isolating phonemes), although full segmentation is closer to RF.1.2d. This is metadata only, so I left it.
- **NW items such as `vin`, `fam`, `crim`, `gonk`, `naf`:** these overlap with rare real words or slang. That is a minor contamination for a nonsense-word measure, and I left them.
- **[REVIEW NEEDED]** The 60% fluency pass fraction in `AnswerEvaluator` is a placeholder, not a benchmark. That code is outside my scope.

## Decision Log

- [DECISION: shortened keys and lengthened distractors only where the difference was conspicuous. I left near-ties and quote-based items alone, which brought the rate to 23%, below the 35% target, without rewriting sound items.]
- [DECISION: replaced I/l in the letter-naming forms instead of changing the UI font, because I only own the question files.]
- [ASSUMPTION: the existing JSON formatting (2-space indent, UTF-8, LF line endings) is canonical. It round-trips byte-identically through the validator's writer.]

## Length-position rebalance

**Problem.** This covers the sentence-length MC/passage items: 122 items whose longest choice is at least 15 characters. In 67 of them (55%), the key was the 2nd-longest choice. The earlier "key is the longest" fix caused this by lengthening one distractor per item. A child who always picks the second-longest choice would score far above their real level.

**Distribution** (the key's length rank among the choices; "tie" means the key's length equals another choice's length):

| Position | Before | After |
|---|---|---|
| Longest | 28 (23%) | 39 (32%) |
| 2nd-longest | 67 (55%) | 37 (30%) |
| 3rd-longest | 8 (7%) | 25 (20%) |
| Shortest (4th) | 11 (9%) | 13 (11%) |
| Tie | 8 (7%) | 8 (7%) |

No position is above 35%. `python -I tools/validate_question_bank.py reading` reports 0 errors and 0 warnings.

**Method.** I edited 30 items across grades 1–10. K had only one affected item, gk_014, and its 6–15-character choices leave no room for a natural edit. I did not change any passage, any `piParameters` value, or the order of any choices. Each distractor I lengthened only gained detail that the passage leaves out or contradicts, so it is still clearly wrong. Each distractor I shortened was an over-padded wrong answer, and the shorter version is just as wrong. In one item the key's text changed (g1_001). I set `correctAnswer` to the exact new string, and its meaning is the same. I re-read the passage after every edit.

### Key is now the longest choice (shortened the padded distractor)

| Item | Change | Why it remains single-answer |
|---|---|---|
| reading_g1_006 | "Sam got a new pet for his birthday" → "Sam got a new pet" | The story has no pet. The key (growing a flower) is still the whole story. |
| reading_g10_002 | "move all of the maternity patients into the neighboring ward run by midwives" → "move all patients into the ward run by midwives" | The passage orders only chlorine handwashing. The midwives' ward appears only as a comparison. |
| reading_g10_009 | "By agreeing that only authorized experts should ever be allowed to repair complex modern devices" → "By agreeing that only authorized experts should repair devices" | The author says the concern "proves too much" and argues for owner repair, so this is still wrong. |
| reading_g10_015 | "Schools too often fail to reward, or even to notice, their most loyal and longest-serving teachers" → "Schools often fail to notice their most loyal teachers" | The passage never criticizes the school. The theme is the teacher's legacy in his students. |
| reading_g3_008 | "Sharks and eels are dangerous hunters that chase octopuses" → "Sharks and eels hunt octopuses" | It is still a minor detail from the last sentence, not the main idea. |
| reading_g4_007 | "Beavers have strong front teeth that let them cut down small trees" → "Beavers have strong front teeth for cutting trees" | It is still a supporting detail. The main idea is dams and ponds that help many living things. |
| reading_g5_015 | "Mateo is still too young and clumsy to be trusted with repairing watches" → "Mateo is too clumsy to repair watches" | Abuelo says it with a wink to encourage Mateo, and Mateo then succeeds. The passage still contradicts it. |
| reading_g6_007 | "You should always practice harder and longer than everyone else on your team" → "You should always practice harder than everyone else" | Practice is not the point. The theme shows when Darius helps Marcus. |
| reading_g7_013 | "Success belongs only to the individual who earns it through years of education and study" → "Success belongs only to the person who earns it" | "We won it" directly contradicts "only to the person". |
| reading_g7_017 | "It was a purely natural disaster that no farmer or government could have predicted or prevented" → "It was a purely natural disaster that no one could have prevented" | The passage says "partly natural and partly human-made". |
| reading_g8_009 | "The instructor is shouting instructions at him from the deck beside the deep end" → "The instructor is shouting at him from the deck" | The instructor "said nothing at all". Tomas's motive comes from his memory of last year. |

### Key is now 3rd-longest (lengthened one distractor)

| Item | Change | Why it remains single-answer |
|---|---|---|
| reading_g1_015 | "It was time for bed" → "It was time for her to go to bed" | The story never mentions bedtime. Mia hugs the bear because she found it. |
| reading_g10_003 | "a faded periodic table" → "a faded periodic table with a taped corner" | The taped table hangs "above the board" and stays for his successor. The drawer holds only the index card. |
| reading_g10_005 (no passage) | "deep sympathy" → "deep sympathy for the suffering of others" | "a-" means "without", so apathy cannot be a strong feeling of any kind. |
| reading_g10_014 | "It argues that maps are generally useless" → "It argues that maps are generally too useless to be worth making" | The sentence explains why leaving things out is unavoidable. The passage values maps and teaches how to read them well. |
| reading_g10_016 | "He is angry at the teacher replacing him" → "He is angry at the brisk young teacher who is taking his place" | The passage describes the successor only neutrally, and he acts calmly throughout. "for whoever came next" signals acceptance. |
| reading_g2_003 | "in a dark closet" → "in a dark kitchen closet" | The directions say "by a sunny window". |
| reading_g2_010 | "Pick the bean sprout" → "Pick the tiny green sprout" | The sprout appears only "in about a week". The next step is "pour in a little water". |
| reading_g3_006 | "They live only in Mexico" → "They live only in the warm forests of Mexico" | They "fly north again" in spring, so "only" is still contradicted. |
| reading_g3_014 | "swim up to the surface" → "swim up to the surface of the water" | The only hiding trick in the passage is matching color. |
| reading_g4_015 | "They use tools and machines to cut down trees" → "They use sharp tools and heavy machines to cut down trees" | Beavers use their teeth, not tools. |
| reading_g5_002 | "warm ocean water" → "warm ocean water moving over the rocks" | Reefs are "built by tiny animals called coral polyps". Warm water causes bleaching. |
| reading_g5_017 | "Coral reefs cover a large part of the warm ocean floor" → "Coral reefs cover a large part of the warm, shallow ocean floor" | Reefs cover "less than one percent of the ocean floor". |
| reading_g6_015 | "To show that scientists dislike studying insects" → "To show that most scientists dislike studying tiny insects" | No scientist is said to dislike insects. The doubt was about whether insects could communicate. |
| reading_g7_003 | "a spelling contest" → "a spelling contest in the school gym" | The speech was for "the student council election". The gym setting does not make a spelling contest correct. |
| reading_g7_006 (no passage) | "The author is neutral and simply reporting facts" → "The author is neutral and is simply reporting the facts" | "so-called improvements" is loaded language, so the author is not neutral. |
| reading_g9_001 | "beside a workhouse well" → "beside the well that belonged to a workhouse" | The workhouse inmates with their own well "were spared". The deaths clustered at the Broad Street pump. |
| reading_g9_002 | "stacking plastic cups" → "stacking plastic cups into tall towers" | The cups were for the later creativity task (finding uses for them). Nobody stacked them, and that was not the dull task. |

### Key is now the shortest choice

| Item | Change | Why it remains single-answer |
|---|---|---|
| reading_g1_001 | Key "It drank some milk" → "It drank milk" (`correctAnswer` updated) | The meaning is the same. The passage says the cat "drank some milk" after waking. |
| reading_g4_009 | "She was hungry" → "She was hungry for breakfast"; "She was bored" → "She was bored and sleepy" | The story gives no sign of hunger or sleepiness. Breakfast is mentioned only as a time she studied. A fluttering stomach before the final round still means nervous. |

### Decision Log (length-position rebalance)

- [DECISION: most edits that made the key longest shortened an over-padded distractor instead of padding the key. This undoes the earlier padding and keeps the keys concise.]
- [DECISION: left these items alone. g1_013 and g2_015 use verbatim passage sentences. gk_014's choices are too short to change naturally. g8_005's key cannot be trimmed below the distractors without dropping "material". g4_006 and g8_006 are near-ties.]
- [DECISION: aimed for about 30% each for longest and 2nd-longest and about 20% for 3rd. Shortest stays near 11%, so "pick the shortest" does not become a new cue.]
- [DECISION: appended this section to docs/review/reading.md because the deliverable explicitly asked for it, even though the task limited file ownership to the reading_grade_*.json files.]
- [REVIEW NEEDED: the new distractors in g10_003 and g7_003 borrow setting details from the passage (the taped corner, the gym) on purpose, to catch surface matching. A reviewer may judge them too tricky.]
