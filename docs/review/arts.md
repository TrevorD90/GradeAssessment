# Arts Question Bank: Second Review

- **Scope:** `src/main/resources/questions/arts/arts_grade_K.json` through `arts_grade_10.json` (230 items)
- **Method:** I answered every item myself before reading the key. Music theory was checked in Python: intervals and half-step counts, major-scale spellings and key signatures for C/G/D/A/E, relative minor (6th degree), V chords, the A harmonic-minor raised 7th, G7 spelling, and note-value sums. Then I checked each item for factual correctness, a unique answer, grade fit and difficulty order, and scoring mechanics against `AnswerEvaluator.kt`.
- **Validator:** `python tools/validate_question_bank.py arts` reports 230 questions, 0 errors and 0 warnings.
- **Not touched:** `piParameters`. The regeneration script owns them.

## Summary

| File | Checked | Issues found | Issues fixed | Notes |
|---|---|---|---|---|
| arts_grade_K.json | 17 | 0 | 0 | No problems found |
| arts_grade_1.json | 18 | 1 | 1 | Near-duplicate of a K item tagged HARD |
| arts_grade_2.json | 20 | 2 | 2 | 1 longest-answer cue, 1 missing typed variant |
| arts_grade_3.json | 20 | 4 | 4 | 3 longest-answer cues, 1 missing typed variant |
| arts_grade_4.json | 22 | 4 | 4 | 1 two-accepted-terms CLOZE, 3 longest-answer cues |
| arts_grade_5.json | 21 | 5 | 5 | 1 misleading explanation (6/8), 1 typed variant, 3 longest-answer cues |
| arts_grade_6.json | 21 | 5 | 5 | 1 typed variant, 4 longest-answer cues |
| arts_grade_7.json | 22 | 5 | 5 | 1 imprecise key wording, 4 longest-answer cues |
| arts_grade_8.json | 22 | 2 | 2 | 2 longest-answer cues |
| arts_grade_9.json | 23 | 5 | 5 | 5 longest-answer cues |
| arts_grade_10.json | 24 | 7 | 7 | 1 overstated explanation, 6 longest-answer cues |
| **Total** | **230** | **40** | **40** | |

**No keyed answer was factually wrong.** I independently checked every date, attribution, movement, composer and theatre/dance/film fact, and they all hold up:

- Brunelleschi and linear perspective
- the Sistine ceiling (1508–1512) and fresco
- Guernica (1937)
- David's *Death of Socrates* (1787) as Neoclassical and Delacroix's *Liberty* (1830) as Romantic
- Graham's contraction and release
- Balanchine and NYCB
- the Judson Dance Theater
- Brecht and Stanislavski
- hubris and catharsis
- the 180-degree rule
- Monet's haystacks
- Caravaggio and tenebrism

Every music-theory key also checks out:

- G major has 1 sharp
- the relative minor of G major is E minor
- C up to G is a perfect fifth (7 half steps)
- G7 is G-B-D-F
- A major has 3 sharps
- A harmonic minor raises G to G-sharp
- ii-V-I is the only progression offered that ends in an authentic cadence
- C-G-Am-F is I-V-vi-IV
- V-vi is a deceptive cadence

Most of the fixes deal with test-taking cues and scoring robustness, not wrong content.

## Longest-answer giveaway (coordinator request)

Measured over MULTIPLE_CHOICE and PASSAGE_BASED items whose longest choice is 15 or more characters, counting items where the key is the single longest choice:

- **Before:** 47 / 86 = **54.7%**
- **After:** 14 / 85 = **16.5%**. The denominator dropped by one because the rewritten g1_003 now has short choices.

I lengthened distractors with plausible but wrong details of similar form. I did not shorten any key. The 14 remaining cases are small gaps, mostly single names or terms (for example "Abstract Expressionism" vs "Minimalism", or "Konstantin Stanislavski" vs other names), where lengthening a distractor would look artificial. I also stopped well short of 0% so that "never the longest" does not become a cue of its own.

## Change log

### Content and scoring fixes

| id | Problem | Change |
|---|---|---|
| arts_g1_003 | It nearly duplicated arts_gk_003 (red + yellow = orange), and the same color-mixing fact is tagged EASY in g1_007, so HARD made no sense. An adaptive session could also show both items. | Rewrote it as a two-step reasoning item: "Lee wants to mix green AND orange. Which color does he need for BOTH?" The key is Yellow and bloomsLevel is now 4. Choices: Red / Yellow / Blue. |
| arts_g4_021 | In the CLOZE, the second blank ("gradually gets softer") has two accepted terms, decrescendo and diminuendo. CLOZE blanks accept only one string, so a child who learned "diminuendo" would be marked wrong. | The second blank now asks for "Notes played short and detached are marked ___" with key `staccato`. Decrescendo stays in the word bank as a distractor, and the explanation names both decrescendo and diminuendo. |
| arts_g5_012 | The explanation said 6/8 has "6 beats per measure" with the eighth note getting "the beat". That is a common simplification, but 6/8 is compound duple and is usually felt in 2. The key (6) was correct. | The explanation now says 6/8 has 6 eighth-note counts and is often felt as two big beats of three eighths each. |
| arts_g7_009 | The key "The strong beats now come in groups of three instead of four" is imprecise. Beats are grouped in threes, and there is one strong beat per measure. | Key changed to "The beats are now grouped in threes instead of fours". The explanation was clarified, and one distractor was lengthened. |
| arts_g10_013 | The explanation called Balanchine's style "plotless". Many of his works are plotless, but he also made story ballets (*The Nutcracker*, *A Midsummer Night's Dream*). | Changed to "often plotless". |
| arts_g2_016 | This FILL_IN_BLANK has no spelling tolerance. A child who typed the whole phrase "primary colors" would be marked wrong. | Added `primary colors` to correctAnswers. |
| arts_g3_002 | Same problem: "treble clef" typed in full would fail. | Added `treble clef`. |
| arts_g5_013 | The prompt reads "self-___", so a child is likely to type "self-portrait". | Added `self-portrait` and `self portrait`. |
| arts_g6_013 | "string quartet" typed in full would fail. | Added `string quartet`. |

### Longest-answer rebalancing (distractor lengthened; key unchanged unless noted)

Every lengthened distractor was checked to make sure it is still clearly wrong.

| id | Distractor change (old → new) |
|---|---|
| arts_g2_012 | "The dancers walk onto the stage" → "… onto the stage together" |
| arts_g3_003 | "It is placed at the very top of the painting" → "… very top edge of the painting, above the sea" (the passage puts the boat in the middle) |
| arts_g3_015 | "Skipping across the stage and giggling" → "Skipping quickly across the stage and giggling loudly" |
| arts_g3_019 | "A pencil sketch" → "A pencil sketch drawn in a notebook"; "A clay pot" → "A clay pot shaped on a potter's wheel" |
| arts_g4_006 | "…next to each other on the color wheel" → "…, so they look almost alike" |
| arts_g4_013 | "ink pressed from a carved block" → "ink pressed onto paper from a carved wooden block" |
| arts_g4_020 | "(He leans back and laughs loudly.)" → "(He leans back in his chair and laughs loudly at a joke.)" |
| arts_g5_017 | "A list of the actors' names" → "… and the roles that each one will play" |
| arts_g5_018 | "Rosa trains for the race" → "Rosa trains hard for the race against her rival in the first scenes" |
| arts_g5_021 | "make up their own movements on the spot" → "each make up their own different movements on the spot" |
| arts_g6_001 | "how rough a surface feels" → "how rough or smooth the surface of a work feels" |
| arts_g6_002 | "To decide which colors to use in the background" → "… colors and values to use in the distant background of the scene" |
| arts_g6_003 | "…a rough texture" → "…a rough, bumpy texture like sandpaper or tree bark" |
| arts_g6_014 | "sits on three sides of a stage that juts forward" → "… into the seating area" |
| arts_g7_007 | "building and painting the scenery" → "building, painting, and moving the scenery between scenes" |
| arts_g7_009 | "The melody moves to a higher octave" → "… up to a higher octave for the rest of the piece" (the key was also reworded; see above) |
| arts_g7_012 | "The character is relaxed and content" → "…, content, and happy with how things are going" |
| arts_g7_018 | "The character seems small and weak" → "… small, weak, and vulnerable to others" |
| arts_g7_020 | "To give the dancers time to rest" → "… between the more difficult sections" |
| arts_g8_006 | "It shows off the family's wealth and success" → "…, success, and comfortable life in the city" |
| arts_g8_015 | "How to construct two-point perspective" → "How to construct accurate two-point perspective for the farm buildings and fields" |
| arts_g9_006 | "The main themes return in the home key" → "…, with the second theme now moved into the tonic" (this describes the recapitulation, so it is still wrong for the development) |
| arts_g9_015 | "…sold for at auction" → "…sold for at a major auction and how many bidders competed for it" |
| arts_g9_018 | "…because it is a documentary" → "…because documentaries record real people and real events" |
| arts_g9_019 | "a single shot that lasts for a whole scene" → "a single long, unbroken shot that follows the action for a whole scene without any cuts" (this is a long take, not a montage) |
| arts_g9_021 | "To make the movement match the mood of the music" → "… so the audience feels one single, unified emotion" |
| arts_g10_011 | "calm, stable, symmetrical compositions" → "… with soft, even lighting and restrained, idealized emotion" (these are Renaissance traits) |
| arts_g10_013 | "Butoh" → "Japanese Butoh dance theatre" |
| arts_g10_015 | "The portrait is painted in oil on canvas" → "…, the most common and costly medium used by court painters of that era" |
| arts_g10_017 | "a character says the opposite of what he or she means" → "… really means, often sarcastically" (verbal irony) |
| arts_g10_018 | "To keep the screen direction consistent" → "… so that viewers always know where each character stands" |
| arts_g10_020 | "Six dancers enter from stage left" → "…, one at a time, and form a diagonal line" (still a description) |
| arts_g10_023 | "requiring all dancers to perform on pointe" → "requiring all dancers, men and women alike, to perform on pointe in classical ballet technique" |

## Doubts left in place

- **arts_g1_006 (HARD):** "Clap, clap, stomp" AAB pattern continuation is easy for grade 1 HARD. It is correct and unique, so I left it. A harder ABB pattern would fit the tag better. `[REVIEW NEEDED: difficulty tag]`
- **arts_g1_007 (EASY) vs the old g1_003:** this is resolved by the g1_003 rewrite. g1_007 (blue + yellow = green, EASY) stays as is.
- **arts_g5_018 (climax):** the key, "side by side near the finish line", is the moment of greatest tension. Some teachers would call the instant Rosa crosses the line the climax, but that moment is not offered as a choice, and the other choices (training = rising action, handshake = resolution, curtain opening = exposition) are clearly wrong. The answer stays unique.
- **arts_g9_007:** "The two main genres … comedy and ___". The satyr play was a third genre, but "two main" makes `tragedy` the only defensible fill.
- **arts_g9_021 / arts_g10_018:** these interpretation items ask for the "most likely" purpose. Only one choice is defensible because the distractors are plainly wrong, so I accept them as unique.
- **CLOZE items generally:** the engine accepts exactly one string per blank. Every CLOZE now has one bank word per blank with no common synonym. The remaining risk is spelling, since there is no spelling tolerance. The hardest blank words ("rhythm" at grade 3, "protagonist"/"antagonist" at grade 7, "monophonic"/"homophonic" at grade 9) are shown in the word bank, so a child can copy them.
- **gk_003 / g1 color mixing across grades:** red + yellow = orange is asked at K (HARD) and blue + yellow = green at grade 1 (EASY). The overlap between grades is intentional for adaptive bridging and is not an error.
- **Accents:** "plie", "Dali", "Miro" and "Rene Magritte" are written without diacritics. That is fine for MC display and does not affect scoring.
- **14 items** still have the key as the single longest choice (16.5%). They are listed by the measurement script. The gaps are small, and I judged further padding to be artificial.

## Decision Log

- [DECISION: lengthen distractors rather than trim keys] Trimming keys tends to make them vaguer, which can create a second defensible answer. Lengthening a wrong option with a wrong specific keeps exactly one answer correct.
- [DECISION: stop at ~16% rather than 0%] If the key were never the longest, that would become a cue of its own.
- [DECISION: replace the g4_021 second blank instead of keeping decrescendo] CLOZE cannot list alternates, and the task explicitly flags decrescendo/diminuendo.
- [ASSUMPTION] The JSON was rewritten with `json.dump(indent=2, ensure_ascii=False)`, the same format the validator's `--normalize` writes, so formatting churn should be nil.

## Length-position rebalance

An earlier pass removed a "key is the longest" cue by lengthening one distractor per item. That pushed the key into 2nd-longest place in about half of the sentence-length MC/passage items, which is a new guessing cue. This pass spreads the key's length rank across positions.

Scope: MULTIPLE_CHOICE and PASSAGE_BASED items whose longest choice is at least 15 characters (n = 85). Items where the key's length ties another choice are not ranked (15 items).

| Key length rank | Before | After |
|---|---|---|
| Longest | 14 (16%) | 23 (27%) |
| 2nd-longest | 45 (53%) | 23 (27%) |
| 3rd | 5 (6%) | 16 (19%) |
| 4th (shortest) | 3 (4%) | 5 (6%) |

`python -I tools/validate_question_bank.py arts`: 0 errors, 0 warnings, both before the doc edit and after. No position is above 35%, and "shortest" stays low (6%), so no reverse cue was created.

22 items were edited (24 choice strings). Only one key's text changed (arts_g3_019); the rest edit distractors. Choice order, keys' positions and `piParameters` are unchanged.

### Key becomes the longest (shorten the longest distractor)

Several of these partly undo the earlier padding. The padding they remove was extra wording on a wrong answer, so taking it out cannot create a second correct answer.

| id | Change | Why still single-answer |
|---|---|---|
| arts_g10_011 | "calm, stable, symmetrical compositions with soft, even lighting and restrained, idealized emotion" → "calm, stable, symmetrical compositions with soft, even lighting" | Still describes High Renaissance traits, the opposite of Baroque drama. |
| arts_g10_015 | "…oil on canvas, the most common and costly medium used by court painters of that era" → "…oil on canvas, the usual medium for court portraits" | Medium alone says nothing about propaganda intent (explanation already says so). |
| arts_g10_017 | "…the opposite of what he or she really means, often sarcastically" → "…the opposite of what he or she means" | This is verbal irony, not dramatic irony. |
| arts_g10_018 | "To keep the screen direction consistent so that viewers always know where each character stands" → "To keep the screen direction consistent for viewers" | The shot breaks consistency, so this purpose is plainly wrong. |
| arts_g3_015 | "Skipping quickly across the stage and giggling loudly" → "Skipping quickly and giggling loudly" | Still the opposite of an old, tired grandfather. |
| arts_g4_006 | "…next to each other on the color wheel, so they look almost alike" → "…next to each other on the color wheel" | Orange and blue are opposite (complementary), not adjacent. |
| arts_g6_014 | "sits on three sides of a stage that juts forward into the seating area" → "sits on three sides of a stage that juts forward" | Still clearly a thrust stage, not a proscenium. |
| arts_g8_015 | "How to construct accurate two-point perspective for the farm buildings and fields" → "How to construct accurate two-point perspective" | Monet's haystack series is about light and atmosphere, not perspective. |
| arts_g9_018 | "The film must be objective because documentaries record real people and real events" → "The film must be objective because it shows real people" | Still the naive conclusion a media-literate viewer rejects. |

### Key becomes 3rd-longest (lengthen a distractor with wrong specifics, or trim the key)

| id | Change | Why still single-answer |
|---|---|---|
| arts_g2_012 | "The dancers warm up" → "The dancers warm up by stretching their arms" | Warming up is still a beginning, not an ending. |
| arts_g3_019 | Key "An animated video made on a computer" → "An animated video made on a tablet" (correctAnswer updated) | A tablet is digital technology, so this is still media arts; the explanation ("cameras and computers") still covers it. The other three choices are not media arts. |
| arts_g5_017 | "The music played in the background" → "…in the background of each scene" | A soundtrack is not a storyboard. |
| arts_g5_021 | "take turns performing one after another" → "…one after another in a line" | Taking turns is the opposite of unison. |
| arts_g6_001 | "how much an artwork costs" → "how much an artwork costs when it is sold" | Price is not an art element. |
| arts_g6_003 | "The circle appears to be moving quickly" → "The circle appears to be spinning and moving quickly across the page" | Gradual shading makes form, not motion. |
| arts_g7_012 | "The character is joking with a friend" → "…joking with a friend and trying to make everyone laugh" | Clenched fists and a tight voice rule out joking. |
| arts_g7_018 | "The character seems to be far away" → "…far away in the distance" | A low angle makes a subject seem powerful, not distant. |
| arts_g7_020 | "To avoid having to use any music" → "To avoid having to use any music or sound during the dance" | Motif repetition has nothing to do with music use. |
| arts_g8_006 | "It was made to advertise children's clothing" → "It was made to advertise new children's clothing for a department store" | Worn clothes and hidden faces contradict an advertisement. |
| arts_g9_022 | "performing a phrase at a lower level" → "…at a lower level, closer to the floor" | Changing level is a different device from retrograde. |

### Key becomes 4th (shortest)

| id | Change | Why still single-answer |
|---|---|---|
| arts_g4_015 | "Gentle swaying in place" → "Gentle swaying from side to side"; "Lying still on the floor" → "Lying still and quiet on the floor" | Neither swaying nor stillness shows lightning; only sharp, sudden, fast movement does. |
| arts_g8_019 | "a piece of scenery falls during the show" → "…falls over during the show"; "the director changes the script" → "…changes the script during rehearsals" | Neither involves addressing the audience; only the key does. |

### Rebalance decision log

- [DECISION: mix of shortening and lengthening] Shortening the longest distractor (9 items) lets the key become longest without padding the key. Lengthening a distractor (13 items) moves the key down. Trimming keys was used once (g3_019, a same-meaning noun swap) to avoid making keys vaguer.
- [DECISION: stop at 27% / 27% / 19% / 6%] Every position is under 35%, and the "shortest" share stays low, so no new cue is created. Pushing more items into 4th place would have needed heavy padding of two distractors per item.
- [DECISION: skip name/term items] arts_g10_001, g10_013, g6_009, g6_011, g8_005, g8_007, g8_014 have short name or term choices where adding words would look artificial or (g10_013: Balanchine also worked on Broadway) risk a second defensible answer.
- [ASSUMPTION] The tie-excluded items (15) are left as they are. They give no length-rank cue.
