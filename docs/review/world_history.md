# World History question bank: second review

Scope: `src/main/resources/questions/world_history/world_history_grade_{K,1..10}.json` (186 items).
Method: I answered every item myself before reading the key. Then I checked facts, uniqueness, neutrality, grade fit and scoring mechanics. Primary-source excerpts were checked against the Avalon Project (Yale). Edits were made with scripted, id-targeted JSON rewrites, which round-trip the existing formatting byte for byte. `piParameters` were not touched.
Validator: `python tools/validate_question_bank.py world_history` reports **186 questions, 0 errors, 0 warnings**.

## Summary per file

"Content issues" means factual, uniqueness, scoring or neutrality problems. "Length-cue edits" are changes to choice wording made only to remove the longest-answer giveaway (see the last section).

| File | Checked | Content issues found | Content issues fixed | Length-cue edits |
|---|---|---|---|---|
| grade_K | 16 | 0 | 0 | 2 (gk_008, gk_016) |
| grade_1 | 16 | 2 | 2 | 2 |
| grade_2 | 17 | 1 | 1 | 6 |
| grade_3 | 16 | 1 | 1 | 8 |
| grade_4 | 17 | 1 | 1 | 4 |
| grade_5 | 17 | 1 | 1 | 5 |
| grade_6 | 17 | 2 | 2 | 8 |
| grade_7 | 18 | 1 | 1 | 7 |
| grade_8 | 17 | 2 | 2 | 6 |
| grade_9 | 17 | 2 | 2 | 7 |
| grade_10 | 18 | 1 | 1 | 8 |
| **Total** | **186** | **14** | **14** | **63** |

No answer key was wrong. No item had two defensible keys under its original wording, except the partial-truth issues listed below (g4_006, g9_013). No ORDERING item was out of order. No item was mis-graded under the grade convention, so no item was moved. Every grade still has at least 4 EASY, 4 MEDIUM and 4 HARD items.

## Content changes

| id | Problem | Change | Source |
|---|---|---|---|
| world_history_g2_015 | **Factual error in the explanation.** It said the Great Wall "is far from the ocean". In fact the Ming wall reaches the Bohai Sea at Shanhaiguan ("Old Dragon's Head"). | The explanation now says "a wall cannot hold back the ocean". | Encyclopaedia Britannica, "Great Wall of China"; Shanhaiguan |
| world_history_g7_001 | **Scoring.** The prompt reads "Historians call it the ___ Empire". "Eastern Roman" is an equally correct historians' name, but it was marked wrong. | Added `Eastern Roman`, `East Roman` and `the Eastern Roman` to correctAnswers. | Standard usage |
| world_history_g4_006 | **Partial truth / exclusivity.** The prompt asked which things "came from ancient Greece" and keyed "Philosophy". Philosophy also arose independently in China and India. | The prompt now reads "closely associated with ancient Greece". The explanation names Socrates, Plato and Aristotle and notes that Chinese and Indian philosophy developed separately. | General |
| world_history_g9_013 | **Partial truth.** The distractor "European powers built too little infrastructure before leaving" is a real historiographical argument, so it was not clearly wrong. | Replaced it with two distractors that the passage contradicts or does not mention: "Europeans left before finishing the railways…" and "Ethiopia and Liberia took control of their neighbors after 1914". Trimmed the key and rewrote the explanation. | Passage text |
| world_history_g8_001 | **Inaccuracy.** "Proposed in 1543": Copernicus circulated the heliocentric idea in the *Commentariolus* (c. 1514). 1543 is when *De revolutionibus* was published. | The prompt now reads "published a book in 1543 arguing that…". | Britannica, "Nicolaus Copernicus" |
| world_history_g6_013 | The attribution said "(translated by L. W. King, 1915)". King's translation dates from 1910; 1915 is the Horne edition. | The year is removed: "(translated by L. W. King)". The text of laws 196 and 198 was **verified verbatim** against Avalon `ancient/hamcode.asp`. The passage omits only the bracketed editorial gloss "[ An eye for an eye ]", which the "..." marks. | avalon.law.yale.edu/ancient/hamcode.asp |
| world_history_g6_005 | "Began about 10,000 years ago": farming in the Fertile Crescent began about 11,500 to 12,000 years ago. | Changed to "more than 10,000 years ago". | Britannica, "Neolithic" |
| world_history_g3_012 | The explanation had the same "about 10,000 years ago" issue. | Changed to "more than 10,000 years ago". | Same |
| world_history_g1_013 | The explanation said cars were invented "a little more than 100 years ago". Benz's car dates from 1886, about 140 years ago. | Changed to "more than 100 years ago", and clarified that rocket ships means spacecraft carrying people. | Britannica, "automobile" |
| world_history_g8_016 | The figure was stated flatly as "More than twelve million". | Hedged to "An estimated 12.5 million". | SlaveVoyages (Trans-Atlantic Slave Trade Database) estimate of about 12.5M embarked |
| world_history_g1_002 | Scoring: a child typing "a map" was marked wrong. | correctAnswers is now `["map","a map"]`. | n/a |
| world_history_g5_003 | Scoring: "Mexico City" was marked wrong, although the blank sits right before "City". | correctAnswers is now `["Mexico","Mexico City"]`. | n/a |
| world_history_g9_003 | Scoring: "Karl Marx" was marked wrong. | correctAnswers is now `["Marx","Karl Marx"]`. | n/a |
| world_history_g10_003 | Scoring: "Karamchand Gandhi" (his full name) was marked wrong. | correctAnswers is now `["Gandhi","Karamchand Gandhi"]`. | n/a |

### Primary sources verified

- **g10_015 Truman Doctrine.** Verified **word for word** against avalon.law.yale.edu/20th_century/trudoc.asp. The passage reads: "I believe that it must be the policy of the United States to support free peoples who are resisting attempted subjugation by armed minorities or by outside pressures." The date, March 12, 1947, also matches. No change needed.
- **g8_015 Declaration of the Rights of Man.** Verified against avalon.law.yale.edu/18th_century/rightsof.asp. Article 1 is quoted in full. Article 3's first sentence matches; the second sentence is omitted, and the "..." marks the omission. No change needed.
- **g6_013 Code of Hammurabi.** See the table above.

### Facts checked and found correct

Some facts were spot-verified against standard references. The rest are well established:

- Great Pyramid about 2560 BCE
- First Olympics 776 BCE
- Athenian democracy 508/507 BCE
- Republic 509 BCE; Augustus 27 BCE; 476 CE
- Hammurabi 1792–1750 BCE; Confucius 551–479 BCE
- Paper invented under the Han
- Machu Picchu built in the 1400s
- da Gama 1498; Magellan's expedition 1519–1522; Aztec fall 1521; Inca conquest from 1532
- Black Death reached Messina in 1347
- Mansa Musa's hajj 1324; Magna Carta 1215; Luther's theses 1517; Constantinople 1453
- Glorious Revolution 1688; Bastille 1789; Haiti 1804
- Locke's *Two Treatises* 1689; Montesquieu 1748
- Watt's patent 1769; *Communist Manifesto* 1848; Meiji Restoration 1868; Suez Canal 1869
- Berlin Conference 1884–85: 14 states, no African rulers; Ethiopia and Liberia independent in 1914
- Sarajevo, 28 June 1914; Brest-Litovsk, March 1918
- Versailles, June 1919; Poland, 1 Sept 1939; UN 1945; UDHR 1948
- Nazi vote 2.6% (1928) and 37.3% (July 1932)
- Mandela: 27 years in prison, president in 1994
- Algeria's war 1954–62

## Doubts left in place (reviewer judgment)

- **g7_016 (Crusades).** How much the Crusades transmitted to Europe is debated; much transfer came through Spain and Sicily. The item frames it as "A historian argues…" and asks which evidence supports the claim. That is a valid reasoning item, so I kept it.
- **g9_013 / g10_013 / g10_016.** These are interpretive: colonial borders and post-independence tension; the Depression versus Versailles; Versailles resentment as a long-term cause. Each is framed as based on the passage, or as evidence for or against a stated claim, so the keyed answer is defensible. None asserts "the main cause" as fact.
- **g7_012 (decline of Rome).** This is multi-select and framed as "causes historians identify", not "the reason Rome fell". OK.
- **g4_014.** "Mountains led to separate city-states" is a standard textbook simplification, not a full explanation. It is fine at grade 4.
- **g5_005.** The Renaissance is dated "1300s to the 1500s"; many sources extend it to about 1600. This is acceptable, so I left it.
- **g7_011.** "The Magna Carta established the idea that even the king must obey the law" is a textbook simplification of its later significance. It is the only defensible choice.
- **g8_010.** "He built a grand palace at Versailles": Louis XIV vastly expanded his father's hunting lodge. This is the standard phrasing, so I left it.
- **g10_009.** Mandela "was elected president in the country's first fully democratic election in 1994". Strictly, the National Assembly elected him after the April 1994 general election. This is the common phrasing, so I left it.
- **g7_003 / g7_018.** "Feudalism" is a term some historians reject. It is standard in grades 6–8 curricula.
- **Neutrality.** The religion, slavery, conquest, Holocaust and apartheid items (g6_006, g6_007, g7_002, g7_007, g5_014, g8_008, g8_016, g10_009, g10_010) are factual and neutral, with no graphic content. Religious claims are attributed ("Muslims believe", "regarded as").

## Longest-answer giveaway

Metric (coordinator's script): among MULTIPLE_CHOICE and PASSAGE_BASED items whose longest choice is 15 or more characters, the share where the key is the single longest choice.

- **Before: 83 / 108 = 76.9%**
- **After: 22 / 108 = 20.4%**

How:

- Distractors were lengthened with specific but clearly wrong detail. Keys were trimmed where that read naturally, e.g. g9_016, which was 165 characters, and g10_017, g10_018, g8_017.
- All trimmed keys keep the same meaning and remain the only defensible answer.
- A first pass reached 1.9%. That would create the opposite cue ("never pick the longest"), so I restored the original choices on 20 low-margin items, where the key was only a few characters longer or the choices were short names.
- I then checked the key's length rank, because many keys had become exactly the second-longest choice. For 20 more items I lengthened a second distractor.

Final rank of the key by length, counting distinct lengths: longest 25, 2nd 48, 3rd 31, 4th 4.

Remaining cue: the key is somewhat over-represented in the 2nd-longest position (about 44%). The key is also rarely the shortest choice. A child could only exploit this by measuring choice lengths, but it is not perfectly flat. `[REVIEW NEEDED: optionally shorten more keys so some become the shortest choice]`
