# Social Studies Question Bank: Second Review

Scope: `src/main/resources/questions/social_studies/social_studies_grade_{K,1..10}.json`, 172 questions.
Method: I answered every item myself before reading its key. Then I checked each item for factual accuracy, answers that could go out of date, a single defensible answer, grade fit, and scoring mechanics (MC/MS key strings, FILL_IN_BLANK variants, CLOZE gap count and word bank, ORDERING uniqueness). I did not touch `piParameters`.
Validator: `python tools/validate_question_bank.py social_studies` reports **0 errors, 0 warnings**.

## Summary per file

"Cue" means the "longest choice is the key" giveaway (see the section below). "Content" means a factual, keying or scoring problem.

| File | Checked | Issues found | Issues fixed | Notes |
|---|---|---|---|---|
| grade_K | 15 | 2 (cue) | 2 | No content errors |
| grade_1 | 15 | 1 (content: missing FIB variant) | 1 | |
| grade_2 | 15 | 1 (cue) | 1 | No content errors |
| grade_3 | 16 | 2 (cue) | 2 | No content errors |
| grade_4 | 16 | 3 (cue) | 3 | No content errors |
| grade_5 | 15 | 4 (cue) | 4 | No content errors |
| grade_6 | 16 | 5 (4 cue, 1 FIB variant) | 5 | |
| grade_7 | 16 | 3 (cue) | 3 | No content errors |
| grade_8 | 16 | 8 (6 cue, 1 unverifiable figures, 1 FIB variant) | 8 | |
| grade_9 | 16 | 5 (4 cue, 1 FIB variant) | 5 | |
| grade_10 | 16 | 7 (cue; 1 of them also a factual error) | 7 | |
| **Total** | **172** | **41** | **41** | |

Only one item contained an outright factual error (g10_007). One passage had overly precise figures that sources do not agree on (g8_013). All other content was accurate. That includes constitutional facts (2 senators, the 2/3 override, ratification by 3/4 of the states, the 10th and 1st Amendments, the 13th/14th/15th Amendments dated 1865/1868/1870), the case holdings (Marbury, Dred Scott, Brown, Miranda), the dates (1776, 1787, 1788, 1791, 1803, 1820, 1846, 1861, 1865), the New Deal programs and the geography. No item depends on current officeholders, populations or GDP figures. The one population item (g6_002: Asia has the largest population) is stable.

## Longest-answer giveaway

Measured with the coordinator's script: MC and passage items whose longest choice has 15 or more characters, counting the items where the key is the single longest choice.

- **Before:** 50 / 96 = **52.1%**
- **After:** 14 / 96 = **14.6%**
- Check against over-correction: the key is the single *shortest* choice in 15 / 96 = 15.6% of items. Both figures are below chance (about 25-33%), so neither length rule helps a child.

Method: I lengthened distractors with plausible but wrong specifics of similar form. Where the key carried extra elaboration, I trimmed it instead. A first pass reached 0%. That would have created a reverse cue, so I reverted 14 cosmetic edits on short-term items where the key is barely longer (for example "Washington, D.C." and "population density"). Those items keep their original choices.

## Every change

### Content fixes

| id | Problem | Change | Source |
|---|---|---|---|
| g10_007 | **Factual error.** The key said the Civil Rights Act of 1964 outlawed discrimination based on "race, color, religion, **sex**, or national origin in employment **and public accommodations**". Title II (public accommodations) does not cover sex; only Title VII (employment) does. | Key is now "It banned segregation in public places and job discrimination". The explanation now states the categories each title covers. Distractors were lengthened and the explanation names what each distractor actually was (19th, 13th and 26th Amendments). | DOJ Title II page (justice.gov/crt/about/hce/title2.php); NPR "Behind the Civil Rights Act" |
| g8_013 | Erie Canal passage gave precise figures: "more than two weeks", "about $10", "cut by more than half". Sources agree on roughly $100 down to $10 per ton or less. They disagree on travel times: 20 to 10 days, 3 weeks to 8 days, 2 weeks to 6 days. | Passage rewritten: "cost roughly $100 and took weeks", then "about $10 per ton or less, and the trip took far less time". Construction dates (1817-1825) and the Lake Erie at Buffalo to Hudson link added. The explanation now notes that the figures are commonly cited estimates. The question and key are unchanged. | Britannica "Erie Canal"; eriecanal.org brief history; EBSCO Research Starters |
| g1_003 | FIB: typing "a map" would be marked wrong | correctAnswers is now ["map", "a map"] | n/a (scoring) |
| g6_004 | FIB: typing "Prime Meridian" in the blank would be marked wrong | added "Prime Meridian" | n/a |
| g8_003 | FIB: "Philadelphia, Pennsylvania" would be marked wrong | added "Philadelphia, Pennsylvania" and "Philadelphia, PA" | n/a |
| g9_002 | FIB: "Domestic Product" would be marked wrong | added "Domestic Product" | n/a |
| g8_006 | (Not an error.) The Emancipation Proclamation item was correct. | Replaced the weak distractor "That all men over 21 could vote" with the classic misconception "That slavery was abolished everywhere in the United States, including the border states". The explanation now says the Proclamation did not apply to border states or Union-held areas. | National Archives summary of the Proclamation (standard fact) |

### Cue rebalancing (key text unchanged unless noted; all keys remain the only defensible answer)

| id | Change |
|---|---|
| gk_001 | Key trimmed to "It tells us what to do"; distractors rephrased to "It is a game we play at recess" and "It is a snack we eat" |
| gk_012 | Distractors: "So we get to class late", "So the hall is very loud" |
| g2_007 | All three distractors lengthened (dinner / job / movies) |
| g3_009 | All three distractors lengthened |
| g3_015 | All three distractors lengthened ("Land near rivers was too wet to be used for farming", etc.) |
| g4_009 | Key trimmed from "American Indians (Native Americans)" to "Native Americans"; explanation mentions both terms |
| g4_015 | "To get away from the noise of new railroads" |
| g4_016 | "Trade forces every state to produce exactly the same goods" |
| g5_001 | "Senate, House of Representatives, and Court" |
| g5_006 | Two distractors lengthened (three branches; peace treaty) |
| g5_010 | Key reworded to "They had no elected members in the Parliament that taxed them"; two distractors lengthened |
| g5_013 | "So that the President alone could make all of the nation's laws" |
| g6_001 | "Distance between any two cities measured in miles" |
| g6_011 | "All businesses and farms are owned and run by the national government" |
| g6_013 | Key trimmed to "Citizens freely choose leaders in a democracy but not in a dictatorship" |
| g6_015 | "Its mountains will grow taller because of the new dam" |
| g7_005 | "The total amount of money spent on the item you chose" (a common misconception) |
| g7_015 | Key trimmed to "the power of judicial review"; the explanation still defines it |
| g7_016 | "More people move from cities back to farms in the countryside" |
| g8_006 | See the content table |
| g8_007 | "whether the nation would have a single president or a council" |
| g8_010 | "to give up all of their existing colonies in Africa and Asia" |
| g8_014 | "Cotton farming moved north to New England, and slavery in the South declined" |
| g8_015 | "enslaved people in every state must be freed within ten years" |
| g8_016 | "Voters across the country elect all members of the House every two years" |
| g9_001 | "The separation of powers among the legislative, executive, and judicial branches" |
| g9_010 | "provide a twelve-person jury in every criminal and civil trial in the country" |
| g9_013 | "...a system of government identical to the British Parliament" |
| g9_015 | "Demand increases, so the equilibrium price rises and the quantity sold rises" (wrong: a frost is a supply shock) |
| g10_007 | See the content table |
| g10_010 | "withdraw the United States from alliances and other world affairs" |
| g10_012 | "Raising interest rates to discourage borrowing and slow price growth" |
| g10_013 | "...lower their prices to remain competitive with foreign mills" |
| g10_014 | Key trimmed to "literacy tests and other barriers to African American voting"; one distractor lengthened |
| g10_015 | Key trimmed to "limit the President's power to send troops into combat without Congress"; one distractor lengthened |
| g10_016 | "Very high inflation that drove prices up sharply throughout the early 1930s" (wrong: the early 1930s saw deflation) |

## Doubts left as they are

- **g4_010 (city < county < state < nation):** some cities span several counties (New York City, Houston), and Louisiana and Alaska use parishes and boroughs. The item uses the standard elementary model, and the explanation says "usually". Kept.
- **g7_006 and g9_009 (bill path, case path):** both are simplified. Committee review happens in each chamber, and Supreme Court cases can also come from state supreme courts. Both prompts say "usual" or describe the typical path, and the given order is undisputed under that model. Kept.
- **g8_001 (Lincoln, President during the Civil War):** Andrew Johnson was President during the last Confederate surrenders (April-June 1865). Johnson is not a choice, so the item has one defensible answer. Kept.
- **g6_010 (Asia > Africa > Europe > Australia):** "Australia" is the continent, not Oceania; Europe is larger either way. Kept.
- **g7_009 (legal duties: jury service, obeying laws, paying taxes):** obeying laws binds non-citizens too, and tax duty depends on income. All three are standard civics answers for "required by law", and the two distractors (voting, running for office) are clearly voluntary. Kept.
- **g2_014 (fire department paid by government):** volunteer fire companies exist. The prompt says "usually". Kept.
- **Difficulty ordering in grade K:** some HARD items (gk_012 "walk, not run", gk_015 travel by horse) are not obviously harder than the MEDIUM ones. This is a calibration question, not an error, and the bank-wide script assigns the IRT parameters. Kept. [REVIEW NEEDED: check against pilot data]
- **Grade 7 mixes civics with economics and geography** (g7_005, g7_010, g7_011, g7_014, g7_016). That is broader than the author's "7 = civics" label, but it matches C3 6-8 standards. Kept.
- **Remaining 14 cue items:** all are short-term items where the key is 1-6 characters longer than the next choice (for example "The compass rose" vs "The map scale"). I judged these not conspicuous.

## Length-position rebalance

The earlier "key is the longest" fix lengthened one distractor per item, which pushed many keys into second place. As a result, "always pick the 2nd-longest choice" beat chance. This pass evens out which length position holds the key.

Scope: MULTIPLE_CHOICE and PASSAGE_BASED items whose longest choice is at least 15 characters (96 items). An item where the key's length ties with another choice gives no length cue, so it is counted as "tie".

| Key position | Before | After |
|---|---|---|
| Longest | 14 (15%) | 19 (20%) |
| 2nd-longest | 40 (42%) | 30 (31%) |
| 3rd-longest | 17 (18%) | 22 (23%) |
| 4th-longest | 9 (9%) | 9 (9%) |
| Tie (no cue) | 16 (17%) | 16 (17%) |

`python -I tools/validate_question_bank.py social_studies critical_thinking` now reports 0 errors and 0 warnings.

10 items were edited. Each edit shortened or lengthened one distractor. No key (correctAnswer) text changed, and piParameters were not touched.

| id | Change | New key position | Why it is still single-answer |
|---|---|---|---|
| g10_010 (containment) | Distractor "withdraw the United States from alliances and other world affairs" shortened to "withdraw the United States from world affairs" | Longest | Containment actively resisted Soviet expansion. Withdrawing from world affairs is the opposite (isolationism). |
| g10_015 (War Powers Resolution) | Distractor "give the President the sole constitutional power to declare war on other nations" shortened to "give the President the sole power to declare war" | Longest | The Constitution gives Congress the power to declare war, and the resolution limited presidential war-making. The distractor stays false. |
| g10_016 (crash to depression) | Distractor "Rapid increases in consumer spending" lengthened to "Rapid increases in consumer spending and borrowing during the early 1930s" | 3rd | Spending and credit shrank in the early 1930s, as the explanation already says. The new detail makes the distractor more plainly false, so bank failures are still the only valid factor. |
| g6_013 (democracy vs dictatorship) | Distractor "A democracy is always a small country; a dictatorship is always a large one" shortened to "A democracy is always small; a dictatorship is always large" | Longest | Same false claim in fewer words. Free choice of leaders is still the only real difference offered. |
| g8_006 (Emancipation Proclamation) | Distractor "That the Southern states could leave the Union" lengthened to "That the Southern states could leave the Union and form their own separate nation" | 3rd | The Proclamation did not recognize secession, and the Union never accepted the Confederacy as a separate nation. The distractor stays false. |
| g8_014 (cotton gin) | Distractor "Southern planters stopped exporting cotton" lengthened to "Southern planters stopped exporting cotton and switched to growing tobacco and rice" | 3rd | After the gin, cotton exports grew sharply and cotton became the South's main export. The distractor stays false. |
| g9_010 (Miranda v. Arizona) | Distractor "obtain a warrant before making any arrest" lengthened to "obtain a signed warrant from a judge before making any arrest of any kind" | 3rd | Miranda is about warnings before custodial questioning. Many lawful arrests need no warrant, and "of any kind" keeps the distractor false. |
| g5_006 (Declaration of Independence) | Distractor "To announce the end of the Revolutionary War with a peace treaty" shortened to "To announce the end of the Revolutionary War" | Longest | The Declaration (1776) came early in the war, which ended with the Treaty of Paris (1783). The distractor stays false. |
| g6_011 (mixed economy) | Distractor "People trade goods only by barter" lengthened to "People trade goods and services only by barter, without using any money" | 3rd | No modern mixed economy relies on barter alone. Private markets combined with some government involvement is still the only definition. |
| g8_016 (checks and balances) | Distractor "Voters across the country elect all members of the House every two years" shortened to "Voters elect all House members every two years" | Longest | Same fact in fewer words. A House election is not one branch limiting another, so Senate confirmation is still the only check and balance offered. |

Doubts:
- g6_013 now has two distractors of the same length (59 characters). This does not affect the key's position or whether it is unique.
- The 4th-longest share stayed at 9%. Moving a key to 4th usually means lengthening two distractors in one item, so I did not do it. A low 4th-longest share is not a usable cue, because ruling out the shortest choice still leaves three options.

Decision Log (length-position rebalance):
- [DECISION: Edited only distractors, never key text, so every correctAnswer is byte-identical and no key's claim drifts.]
- [DECISION: Moved 5 keys to "longest" and 5 to "3rd". This leaves 2nd-longest at 31% with no new dominant position.]
- [DECISION: Every lengthened distractor got a detail that is false on the facts (for example "during the early 1930s" or "of any kind"), never one that could be partly true.]
