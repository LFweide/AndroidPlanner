## Agent skills

### Issue tracker

Local markdown under `.scratch/<feature>/`. See `docs/agents/issue-tracker.md`.

### Domain docs

Single-context: one `GLOSSARY.md` + `docs/adr/` at the repo root. See `docs/agents/domain.md`.

### Project plan

The build plan and hard constraints live in `app/docs/plan.md`. Read it before any work.
Commits carry no AI attribution lines (the plan requires this).

## Progress and how to continue

- **Done:** tickets 00 (git repo, remote `https://github.com/LFweide/AndroidPlanner`, branch `main`) and 01 (skeleton and navigation). Tickets live in `.scratch/planner-app/issues/`; next is **02 tasks CRUD**, then 03/04/07 can run in parallel, see each ticket's `Blocked by`. Mark a ticket's checkboxes and `Status: done` when finished.
- **Package** is `com.example.planner`. Project name "Planner". SDK: min 26, target and compile 35.
- **Versions** are pinned to ones that build with compileSdk 35 (see `gradle/libs.versions.toml`). Do not bump `core-ktx`, `activity-compose` or lifecycle to versions that need compileSdk 36. Remaining lint warnings (12) are all "newer version available" notices caused by this; they are accepted.
- **KSP is not added yet.** Add it in ticket 02 together with Room; its version must match Kotlin (`2.2.10`) and work with AGP 9.
- **The Android SDK and network work on this machine**, so verify with `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug` (about 1-3 min) before claiming anything builds. Emulator checks (rotation, rail vs bar) are still manual for the user.
- **Workflow:** implement one ticket per session interactively with `/implement`, review with the code-review skill, one commit per ticket, no AI attribution lines, push after review.
- **`implement-issues.sh` does not work unattended yet:** headless sessions stop to ask for permission and exit 0, so the script wrongly records tickets as done in `.scratch/planner-app/issues/.implemented`. Keep that file at only the really finished tickets, or do not use the script.
- **Never write credentials** (passwords, tokens) into any file; git authenticates through the user's own Git Credential Manager sign-in.
- Open choices for the user: whether to ignore the rest of `.idea/`, and whether to keep `.scratch/` in the repo. `implement-issues.sh` and `.idea/vcs.xml` were committed by accident in ticket 01's commit.
