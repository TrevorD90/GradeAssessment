# Child Assessment Platform — Project Memory
# Save this file to: your-project-root/CLAUDE.md
# This applies only to this project. Overrides global settings where they conflict.

---

## Project Identity

- **Name:** Child Academic Assessment Platform
- **Purpose:** Adaptive grade-level assessment for children across 7 subjects with behavioral monitoring
- **Inspired by:** Renaissance Learning, Acadience Reading, iReady, Edulastic
- **Stack:** React (frontend) · AWS (Lambda, S3, DynamoDB, Rekognition, Transcribe, API Gateway) · Node.js (Lambda functions)
- **Architecture:** Multi-agent AI system coordinated by an Orchestrator Agent

---

## Agent Roster (never change without noting why)

1. **Orchestrator Agent** — master coordinator, session lifecycle
2. **Assessment Engine Agent** — question delivery, timing, answer collection
3. **Adaptive Question Agent** — IRT/CAT grade calibration per subject
4. **Behavioral Monitor Agent** — camera + audio passive observation (silent, consent-gated)
5. **Results Analysis Agent** — aggregates all data into results bundle
6. **Report Generator Agent** — renders Results tab + Detail View + PDF export
7. **Help & Hint Agent** — 3-tier contextual hints via "?" button per question

---

## Subjects Assessed

Math · Science · Reading · Writing · Arts · World History · Social Studies
Grade bands: K–2 · 3–4 · 5–6 · 7–8 · 9–10

---

## Planning Mode Rules (project-level)

- Proceed autonomously on all architecture, code, and documentation tasks.
- Never ask permission to create files, write code, or draft plans within this project.
- Record every decision in a `## Decision Log` section at the bottom of any planning document.
- If a Phase boundary is reached (see phases below), produce a phase summary before starting the next.
- Flag anything requiring human judgment with `[REVIEW NEEDED: reason]` — don't block on it.

---

## Development Phases

- **Phase 1:** Assessment Engine + Adaptive Question Agent · Math, Reading, Writing only · Basic UI
- **Phase 2:** All 7 subjects · Results Analysis + Report Generator · Full Results tab + Detail View · PDF export
- **Phase 3:** Behavioral Monitor Agent · Rekognition + Transcribe · Consent flow · Behavioral observations panel
- **Phase 4:** Help & Hint Agent · Mascot animations · Accessibility pass · Performance optimization

---

## AWS Naming Conventions

- S3 buckets: `assessment-question-banks/`, `assessment-results/{session_id}/`, `assessment-exports/{session_id}/`
- DynamoDB tables: `AssessmentSessions`, `AssessmentResults`, `BehavioralEvents`, `StudentProfiles`
- Lambda functions: `startSession`, `submitAnswer`, `getHint`, `finalizeSession`, `generatePDF`, `getResults`, `getDetail`

---

## Hard Rules — Never Violate

- Raw camera or audio is NEVER written to S3 or any storage. Structured event logs only.
- Behavioral flags require `confidence > 0.70` before surfacing in UI.
- Every behavioral output must include the disclaimer: `"These observations are informational only and do not constitute a medical or psychological diagnosis."`
- COPPA-compliant: no student data retained beyond 12 months without renewed consent.
- Parent/guardian consent flag must be `true` before Behavioral Monitor Agent activates.

---

## UI Tab Structure

- **Tab 1 — New Assessment:** welcome → student profile → consent → instructions → active assessment → completion
- **Tab 2 — Results:** sortable table (name, age, date, per-subject grade level) + Detail button
- **Detail View:** header · grade summary chart · per-subject missed questions · behavioral observations · PDF export

---

## Session Log Instructions (auto memory)

At the end of every session, write a brief entry to memory with:
- Date and session focus
- Files created or modified
- Key decisions made
- Open `[REVIEW NEEDED]` items
- Next recommended action
