# Science question bank: second review

Scope: `src/main/resources/questions/science/science_grade_{K,1..10}.json`, 195 items.
Method: I answered every item myself before reading the key. Every numeric key was recomputed in Python (F = ma, KE, wave speed, energy pyramid, free-fall speed, stoichiometry, half-life, Punnett ratio, heat data, conservation of mass). For each item I also checked that only one answer is defensible, that data passages are internally consistent, that the item fits its grade, how it is scored, and whether it renders. I did not touch `piParameters`. `python tools/validate_question_bank.py science` reports 0 errors and 0 warnings.

## Summary

No keyed answer was wrong. I found no misconceptions built into any key (seasons, phases, heat and temperature, plant mass, mass and weight, evolution "need", photosynthesis and respiration equations, periodic trends). Data items are consistent. For example, in g6_014, 100 g x 40 degrees and 200 g x 20 degrees both give 4000, so equal energy input checks out. The problems I did find were:

1. **Answer-length giveaway (most serious).** Among multiple-choice and passage items whose longest choice is 15 or more characters, the key was the single longest choice in **60/110 = 54.5%** before review. After review it is **28/109 = 25.7%**, close to chance. g1_012 now has only short choices, so it no longer counts toward the measure. I fixed 31 items by lengthening distractors with plausible misconceptions and, where needed, trimming the key. No key changed position, and every item still has exactly one defensible answer.
2. **Rendering.** 12 items used `°`, `²` or `→`. I replaced them with "degrees C", "^2" and "->".
3. **Fill-in-blank items that rejected correct answers.** In 4 items a child typing a natural, correct answer would have been marked wrong. I added the missing answer variants.
4. **Small content problems.** I fixed 4: an inconsistent date span, a reflex explanation, an article cue, and a weak distractor and explanation.

| File | Checked | Issues found | Issues fixed |
|---|---|---|---|
| science_grade_K.json | 16 | 1 (length cue gk_012) | 1 |
| science_grade_1.json | 17 | 3 (length cues g1_007, g1_012, g1_016) | 3 |
| science_grade_2.json | 17 | 3 (length cues g2_009, g2_015, g2_017) | 3 |
| science_grade_3.json | 17 | 3 (length cues g3_003, g3_017; degree sign in g3_013) | 3 |
| science_grade_4.json | 16 | 2 (length cues g4_012, g4_014) | 2 |
| science_grade_5.json | 18 | 4 (length cues g5_006, g5_017, g5_018; blank variant g5_004) | 4 |
| science_grade_6.json | 18 | 7 (length cues g6_013, g6_014, g6_016, g6_017, g6_018; passage span g6_008; reflex explanation g6_013; degree sign g6_014) | 7 |
| science_grade_7.json | 19 | 7 (length cues g7_008, g7_010, g7_015, g7_018, g7_019; article cue g7_012; degree sign g7_014) | 7 |
| science_grade_8.json | 19 | 5 (length cues g8_008, g8_016, g8_019; blank variant g8_003; superscript g8_015) | 5 |
| science_grade_9.json | 19 | 6 (length cue g9_015; blank variant g9_001; arrows g9_009; superscripts and degree signs g9_012, g9_015, g9_018) | 6 |
| science_grade_10.json | 19 | 9 (length cues g10_012, g10_015, g10_017, g10_019; weak distractor g10_019; blank variant g10_003; arrows and degree signs g10_008, g10_014, g10_015, g10_019) | 9 |

Counts are per issue, so one item can count more than once. The difficulty mix is unchanged, and every grade still has at least 4 EASY, 4 MEDIUM and 4 HARD items.

## Every change

### Content and scoring
- **science_g6_008**: The passage said "data … over 150 years" but then described 1880-2020, which is 140 years. I changed the first sentence to "Scientists analyzed records of average global temperature going back to the 1800s." The key is unchanged.
- **science_g6_013**: The explanation implied the brain sends the withdrawal signal. In a hot-pan reflex, the spinal cord triggers the muscles before the brain registers the pain. I rewrote the explanation. The key is still the best of the four paths.
- **science_g7_012** (CLOZE): "Oxygen gas is an ___" gave the answer away ("an" means "element"), and "a ___" ruled it out for the first blank. Both blanks now read "is classified as a(n) ___". The blanks are unchanged.
- **science_g10_019**: One distractor read "covalent bonds inside water are much weaker than in methane", which predicts the opposite of the data and is not a real misconception. I replaced it with the real one: "Boiling breaks covalent bonds, and the bonds inside water are stronger…". I also added to the explanation that boiling does not break covalent bonds. I trimmed the key wording.
- **science_g8_003** (FILL "natural ___"): added "natural selection", because children often type the whole term.
- **science_g9_001** (FILL "DNA"): added "deoxyribonucleic acid".
- **science_g10_003** (FILL "___ gases"): added "noble gases", "noble gas", "inert gases".
- **science_g5_004** (FILL "atmosphere"): added "the atmosphere". The matcher does not strip articles.

### Rendering (special characters to ASCII)
g3_013 passage and explanation, g6_014, g7_014, g9_015, g10_015, g10_019: "°C" became "degrees C". g8_015 and g9_012 choices, key and explanation, and g9_018: "m/s²" became "m/s^2". g9_009 and g10_008 choices and key, and g10_014 prompt: "→" became "->". In every case the key string was changed together with its choice, so exact-match scoring still works. No non-ASCII characters remain in the science files.

### Answer-length rebalancing (key unchanged in meaning and position)
For each item, I made at least one distractor longer than the key, and sometimes shortened the key.
- gk_012: distractor became "Throw old cans on the ground in the park".
- g1_007: distractors became "To make its wings strong so it can fly" and "To keep the nest warm at night".
- g1_012: shortened to "A flashlight" / "A whisper" / "A blanket". The prompt already says "with light".
- g1_016: key became "Puppies look like their parents, but not exactly". Distractors were lengthened.
- g2_009: two distractors were lengthened ("brings water … from the pond", "keeps the plants warm by buzzing…").
- g2_015: key became "The same pieces can make different objects". One distractor was lengthened.
- g2_017: key became "Much longer than a person's whole life". The "One day/week/year" distractors were lengthened.
- g3_003: key became "The usual weather of a place over many years". Two distractors were lengthened.
- g3_017: one distractor became "The frogs will not be affected, because frogs never need water".
- g4_012: key became "A higher ramp made the marble faster, so it transferred more energy to the cup". Distractors were lengthened.
- g4_014: two distractors were lengthened ("occur randomly, so…", "mostly in the coldest places, near the … Poles").
- g5_006: two distractors were lengthened ("destroyed and no longer exists", "turned into water molecules and became water").
- g5_017: two distractors were lengthened with misconceptions.
- g5_018: "The stars themselves move around Earth once each year…" is now longest.
- g6_013, g6_014, g6_016, g6_017, g6_018: distractors were rewritten at full length. g6_016's key became "Clouds and precipitation, as the warm, humid air is pushed up and cools".
- g7_008, g7_010, g7_015, g7_018, g7_019: same approach. The keys of g7_015 and g7_019 were trimmed.
- g8_008, g8_016, g8_019: same approach. g8_016's key ("Dark beetles were harder for birds to see on dark rock, so more survived and passed on the dark color") and g8_019's key were trimmed. g8_016's longest choice is now the Lamarckian "turned darker … because they needed to hide".
- g9_015: key became "Heat changed the enzyme's shape, so the substrate no longer fit its active site".
- g10_012, g10_015, g10_017, g10_019: same approach. The keys of g10_015, g10_017 and g10_019 were trimmed.

Measurement: before 60/110 = 54.5%, after 28/109 = 25.7%. The remaining 28 are mostly items where the key exceeds the next-longest choice by fewer than 10 characters. I left those alone so that "never the longest" does not become a reverse cue.

## Doubts left as they are
- **Grade fit within 6-8 (California integrated model).** From memory, I believe MS-LS3-2 (asexual vs sexual reproduction, g6_009) and MS-LS1-5 (genes plus environment affect growth, g6_018) belong to grade 8 in the California Preferred Integrated Model. I also believe MS-PS1-4 (particle motion and state, g7_005 and g7_018) belongs to grade 6. I could not check the CDE document offline, so I left the tags as they are. All four are middle-school-band NGSS standards at an appropriate depth. `[REVIEW NEEDED: confirm CA grade placement of MS-LS3-2, MS-LS1-5, MS-PS1-4]`
- **science_g9_014** (Coriolis, tagged HS-ESS2-4): Earth science does not fit the grade 9 convention (biology plus physics), and the standard code is loosely matched. The item mostly tests reading the passage, and its key is correct, so I kept it.
- **science_g10_011** (EM spectrum ordering, HS-PS4-1): physics content in the grade 10 chemistry/genetics year. It is still reasonable for grade 10, so I kept it.
- **science_g5_005**: the explanation calls the Sun "a star of ordinary size". It is bigger than most stars, since red dwarfs dominate, but this is the standard grade-5 framing. The key is correct.
- **science_g6_013**: the key ("…spinal cord and brain, then muscles") simplifies a spinal reflex. It is still the only defensible choice, and the explanation now says so.
- **science_g9_015 / g9_016**: tagged HS-LS1-1 and HS-LS1-3. Enzyme temperature effects fit those standards only loosely. The content is correct.
- **FILL items without spelling tolerance** (for example "evaporation", "photosynthesis", "erosion"): a misspelling is marked wrong. That is by design, and I added no misspellings as variants.
