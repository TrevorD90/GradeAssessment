# Question Bank Research and Adaptive Engine Review

Date: 2026-11-13. Covers the rebuild of all nine question banks, the adaptive-engine fixes, and the sources behind them.

## 1. Result at a glance

| Subject | Before | After | Per grade (K-10) |
|---|---|---|---|
| Math | 31 | 176 | 16 |
| Reading comprehension | 24 | 195 | 17-19 |
| Writing | 24 | 188 | 16-19 |
| Science | 28 | 195 | 16-19 |
| Arts | 28 | 230 | 17-24 |
| World History | 28 | 186 | 16-18 |
| Social Studies | 28 | 172 | 15-16 |
| Critical Thinking | 28 | 155 | 14-15 |
| IQ Assessment | 28 | 168 | 15-16 |
| **Total** | **247** | **1,665** | |

- Every question now carries an exact `gradeLevel` (0 = K .. 10) and a `difficulty` (EASY / MEDIUM / HARD), with at least 4 of each difficulty per grade in the core subjects.
- Files are one per subject per grade: `src/main/resources/questions/<subject>/<prefix>_grade_<K|1..10>.json`, listed in `questions/index.json`.
- `python tools/validate_question_bank.py` checks structure; `--normalize` rewrites IRT parameters and the manifest. Run it after any bank edit.
- Simulated students with a known true level (40 per level per subject, starting age deliberately 2 years off) were placed within about half a grade on average (mean absolute error 0.34-0.51 grades) in 20-29 questions.

## 2. Is "start easy, then increase in difficulty" correct?

Only half right. Operational adaptive tests start at or somewhat below the expected level, then move both ways: harder after a correct answer, easier after an incorrect one.

- Star Reading / Star Math start "usually one or two grades below grade placement" so about 85% of students get the first item right, to reduce anxiety (Star technical manuals).
- MAP Growth starts from the prior score, or a default by grade and subject, then raises or lowers difficulty after each response (MAP Growth Technical Report).
- Smarter Balanced starts from a prior and picks randomly among the top candidates.
- A steadily climbing ladder describes individually administered basal/ceiling tests (Woodcock-Johnson), not a CAT.

What the app does now: first question one grade below the age expectation, Bayesian (EAP) ability estimate after every answer, most-informative unasked question near the current estimate, random pick among the best four, stop at 15-30 questions once the estimate is within about half a grade.

Sources read: MAP Growth Technical Report 2024-25 (nwea.org/uploads/MAP-Growth-Technical-Report-2025.pdf); Star Reading and Star Math Technical Manuals, Florida editions (flfast.org); Florida K-2 CAT FAQ (cdn.fldoe.org/file/20102/K2CompAdaptTestFAQ.pdf); Smarter Balanced CAT Algorithm summary (technicalreports.smarterbalanced.org/cat_algorithm/CAT_Algorithm_Summary.html); The Science Behind i-Ready's Adaptive Diagnostic (Curriculum Associates); IACAT Introduction to CAT (iacat.org/introduction-to-cat). Abstract or snippet only: Bock & Mislevy 1982; Stocking 1994 (ETS RR-94-5); Riverside WJ IV start-point pages.

## 3. Calibration convention

- Grade equivalent (GE) g.x = working like a typical student x tenths through grade g.
- theta = 0.6 x (GE - 5). One grade = 0.6 theta.
- A grade-g item sits at GE g+0.2 (EASY), g+0.5 (MEDIUM), g+0.8 (HARD).
- a = 1.2 for all items; c = 1/number of choices for multiple choice, 0 otherwise.
- Defined once in `GradeScale.kt` and mirrored in `tools/validate_question_bank.py`.

## 4. Sources by subject

"Read" means the agent opened the document; "snippet" means search summary only. Full per-item audit tables are in the session transcript.

### Math
Read: Common Core math grade introductions K, 1, 4, 6, 8 and HS Algebra/Geometry (thecorestandards.org); NAEP Mathematics Framework 2024 (nationsreportcard.gov); Texas TEKS grades 3 and 7 via IXL standards pages; IXL skill lists grades 2 and 6. Snippet: TEA STAAR released questions; NWEA MAP Growth instructional areas; Renaissance Star Math description (Colorado DOE).
Convention: grade 9 = Algebra I, grade 10 = Geometry plus a little Algebra II and statistics.

### Reading
Read: Common Core RI.9-10 (thecorestandards.org); Supplemental Information for Appendix A, text complexity bands (cde.ca.gov); CCSS three-factor text complexity model; Lexile grade level charts (hub.lexile.com); NAEP 2017 Reading Framework abridged (nagb.gov); Star Reading description (Colorado DOE); Acadience Reading K-6 crosswalk (in.gov); IXL grade 3 language arts; i-Ready adaptive diagnostic paper.

### Writing
Read: Common Core Language Progressive Skills and Language standards grades 2, 8, 9-10 (thecorestandards.org); IXL Common Core alignment grade 4; Purdue OWL comma rules; ACT English test description; NWEA MAP Growth Language Usage fact sheet. Summary only: SAT Reading and Writing (College Board); Smarter Balanced Claim 2 (Connecticut SDE); TEKS ELAR grade 3 (teksguide.org).

### Science
Read: NGSS DCI arrangements (nextgenscience.org/overview-dci); California Preferred Integrated Model grades 6-8 (cde.ca.gov); TIMSS 2023 Science Framework; NWEA MAP Growth Science sample items fact sheet; IXL grade 3 science. Snippet: NAEP Science Framework; STAAR grade 8 science blueprint; Khan Academy NGSS courses.
Convention: grades 6-8 follow California's integrated model; grade 9 = biology plus introductory physics; grade 10 = chemistry plus genetics and evolution.

### Arts
Read: National Core Arts Standards at-a-glance PDFs for Visual Arts, Music, Theatre, Dance, Media Arts (nationalartsstandards.org); NCCAS performance standards index; Texas Fine Arts TEKS 19 TAC Ch. 117 (law.cornell.edu); NAEP Arts "what does it measure" and 2008 grade 8 sample page; Getty elements of art and principles of design; Core Knowledge Music Grade 4 activity book.

### World History
Read: C3 Framework (socialstudies.org); California History-Social Science Content Standards (cde.ca.gov); Texas TEKS 113.42 World History Studies (law.cornell.edu); New York 9-12 Social Studies Framework (nysed.gov); Core Knowledge Sequence at-a-glance and History & Geography unit list; National Standards for History world eras (UCLA NCHS); Avalon Project primary sources (Yale). Snippet: AP World History, OER Project, NY Regents, Massachusetts framework.
Convention: grade 6 ancient, grade 7 medieval through Reformation, grade 8 early modern (1450-1825), grade 9 1750-1918, grade 10 1918-present.

### Social Studies
Read: C3 Framework Dimension 2 indicators, K-2 and all-band PDF; California HSS Content Standards; USCIS 100 civics questions; NAEP U.S. History and Geography frameworks; Voluntary National Content Standards in Economics (via FTE); IXL social studies grades 2, 4, 7. Snippet: Texas TEKS 113.20.
Convention: expanding-communities sequence; US history to the Constitution in 5, through Reconstruction in 8, modern in 10.

### Critical Thinking
All via search summaries (no full documents opened): Cornell Critical Thinking Test Level X; Facione APA Delphi Report; Watson-Glaser; Insight Assessment K-12 (CCTST family); Common Core RI.x.8 progression; Mathematical Practice 3; Markovits on conditional reasoning development; Goswami / Richland on analogical reasoning; Civic Online Reasoning; Building Thinking Skills scope; Paul-Elder framework.

### IQ Assessment
Read: Riverside CogAT structure page; TestPrep-Online CogAT pages; Pearson WISC-V product page; NAGC Assessments & Tests; Wikipedia CHC theory. Snippet: OLSAT descriptions; SB5 pages; Canivez et al. on index-score validity.

## 5. What the audit of the old questions found

- Structurally broken: 5 Science multi-select and 3 Science ordering items, all 4 Arts ordering items, and several Reading cloze items could never be scored correct.
- Wrong or self-contradictory keys: Critical Thinking `ct_k2_007`; Reading `RC-K2-004` (key said "flower", passage only reached "green plant"); phoneme count for "mule".
- Factual slips fixed: Berlin Conference "fourteen European nations"; Britain entering WWI via an "alliance with Russia"; Erie Canal linking directly to the Atlantic; a "plus-shaped net of 6 squares"; rhetoric scheme attributed to Aristotle.
- Two defensible answers: about 15 items across subjects, rewritten or dropped.
- Misgraded: common. Examples: 3 + 2 tagged grade 1 (kindergarten); semicolons tagged grade 6 (grade 9); rock types tagged grade 4 (grade 7); several "9-10" items at grade 5-8 level.
- Invented standard codes in Arts, World History and Social Studies replaced with real ones.
- Ten "nonsense words" in the fluency subtest were real words or one letter from an offensive one; replaced.

## 5b. Independent second review (2026-11-13)

No teacher was available, so every question was re-checked by a second agent that did not write it. Each reviewer solved every item before looking at the key, using Python for arithmetic and case enumeration for logic puzzles, and logged every change in `docs/review/<subject>.md`.

| Subject | Wrong keys | Content fixes (selected) | Longest-answer cue before -> after |
|---|---|---|---|
| Math | 0 | 11 typed answers with units now accepted; "x" used as both variable and times sign | 20% (few items) |
| Reading | 0 | Capital I next to lowercase l in letter-naming grids; nonsense words ending in v/j | 42% -> 32% |
| Writing | 0 | Cloze item with two valid word orders; disputed "cast ... their" agreement | 40% -> 20% |
| Science | 0 | 1880-2020 called "150 years"; hot-pan reflex attributed to the brain; article giveaway in a cloze | 55% -> 26% |
| Arts | 0 | Decrescendo/diminuendo blank had two right answers; 6/8 "6 beats" explanation; Balanchine "plotless" | 55% -> 27% |
| World History | 0 | Great Wall "far from the ocean"; Copernicus date; "Eastern Roman" rejected; partly-true distractor | 77% -> 20% |
| Social Studies | 1 | Civil Rights Act 1964: sex discrimination is banned in employment only, not public accommodations; Erie Canal figures hedged | 52% -> 20% |
| Critical Thinking | 0 | "Irrelevant" sentence that was arguably relevant; two race puzzles solvable by copying clue order | 57% -> 25% |
| IQ | 0 | Font-dependent p/d rotation item; ambiguous fold/punch and "who is in front" items | 52% -> 4% |

Length cue: the correct answer was the longest choice in 53% of sentence-length MC items (chance 25%). The first fix moved it to second-longest (53-55% in Arts and Reading), so a second pass spread it across every position. The validator now warns if the key sits at any one length position in more than 40% of items. Simulation: a 10-year-old who knows nothing is placed at GE about -0.3 whether guessing at random or by any length rule.

Answer checker changes made during the review: number words ("four"), typographic minus signs, and a leading "a/an/the" (except in Writing) are accepted.

## 5c. Blind answer-key check (2026-11-13)

Nine fresh solvers answered all 1,665 questions from copies with the keys removed. They never opened the project; their logs were checked. A script compared their answers using the app's own matching rules. **1,665 of 1,665 agreed with the keys.** The comparison was validated in both directions: real keys scored 100%, and planted wrong answers were caught. Five items with wording problems flagged by solvers were fixed. Details are in `docs/review/blind_check.md`.

## 6. Decision Log

- [DECISION] One linear grade scale (`GradeScale`) replaces three inconsistent mappings.
- [DECISION] EAP estimation with a normal prior (SD 2 grades) replaces Newton-Raphson maximum likelihood, which jumped to the end of the scale after one answer.
- [DECISION] First question one grade below the age expectation.
- [DECISION] Stop at posterior SD < 0.36 theta (0.6 grade), minimum 15, maximum 30 questions.
- [DECISION] Randomesque selection among the 4 most informative of the 24 nearest unasked items; format and Bloom's level alternate when possible.
- [DECISION] Grade level = whole-number part of GE (GE 0.9 is Kindergarten, not 1st Grade).
- [DECISION] Critical Thinking percentile and IQ estimate are computed relative to the age expectation, not from the raw grade scale.
- [DECISION] Fluency subtests are adult-scored; pass = 60% of items marked correct; all three subtests are delivered for ages 5-7.
- [DECISION] The loader skips structurally defective questions and reports them in `QuestionBankLoader.lastLoadProblems`.
- [DECISION] Manifest file `questions/index.json` replaces the hard-coded file list.
- [DECISION] Typed answers ignore case, extra spaces, trailing punctuation, and number formatting.
- [ALT] Sympson-Hetter exposure control; not used because it needs simulation-tuned parameters per item.
- [ALT] Start from the student's previous score in the subject; not implemented (needs a stable student ID).

## 7. Open review items

- [REVIEW NEEDED: calibration] Grade and difficulty tags are expert-assigned from standards, not calibrated on real student responses. Reported grade levels are criterion-based estimates, not normed grade equivalents. Recalibrate once response data exists.
- [RESOLVED: second reader] An independent second review covered all 1,665 items (section 5b). No teacher review exists; this is personal use.
- [REVIEW NEEDED: source depth] Critical Thinking sources were search summaries only; several Math, Science and Writing sources were snippet-only. No released state test items were read.
- [REVIEW NEEDED: K-1 reading and typing load] All items are text. Kindergarten and grade 1 items assume read-aloud or an adult helper; a few need a typed short answer.
- [REVIEW NEEDED: text-only reasoning items] IQ "spatial" and "matrix" items are described in words and "memory" items stay on screen, so they are confounded with reading.
- [REVIEW NEEDED: IQ and CT norms] The age-relative SD (2 grade levels) is an assumption, not a norm.
- [REVIEW NEEDED: grade conventions] Science 6-8 (California integrated), World History grade 8 (early modern), Social Studies grade 10 (modern US, taught in 11-12 in some states), Math 9/10 (Algebra I / Geometry).
- [REVIEW NEEDED: reading passages] Lexile was not measured; narrative passages in grades 5-10 score below their Flesch-Kincaid band; six carried-over passages are short for their grade.
- [REVIEW NEEDED: Truman Doctrine excerpt] World History grade 10 passage only partly verified against the Avalon text.
- [REVIEW NEEDED: sensitive topics] Religion, slavery, the Holocaust and apartheid items are worded neutrally but need a human sensitivity read.
- [REVIEW NEEDED: pool depth] About 5 items per grade-and-difficulty cell. Tests run 20-29 questions; published guidance suggests pools of 5-10x test length (150-300), which the core subjects now meet at the low end. Retakes will see repeats.
- [REVIEW NEEDED: fluency pass mark] 60% is a placeholder, not a published benchmark.
- [REVIEW NEEDED: COPPA retention] The hard rule "no student data beyond 12 months without renewed consent" is not enforced in code. No purge or consent-renewal flow exists; not added here because it deletes data.
- [REVIEW NEEDED: student identity] Trends match students by first name only.
- [REVIEW NEEDED: old sessions] Results saved before this change reference retired question ids and the old score scale; their missed-question lists cannot be rebuilt and their scaled scores are not comparable.
- [REVIEW NEEDED: project CLAUDE.md] It describes a React / AWS Lambda stack; the code is a Kotlin Compose Desktop app with SQLite.
