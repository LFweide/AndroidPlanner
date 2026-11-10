# 00: Make the project a git repo

**What to build:** The project becomes a git repository linked to the GitHub account `LFWeide`, so the per-phase commits in the plan have somewhere to go. Credentials are never stored in the repo, in this ticket, or in any file.

**Blocked by:** None (can start immediately). Do this before ticket 01 so every phase gets its own commit.

**Status:** ready-for-agent

- [ ] `git init` at the repo root with `main` as the default branch
- [ ] `.gitignore` excludes `local.properties`, `.gradle/`, `build/`, `.idea/` and other generated files, and `.scratch/` is kept or ignored as the user decides
- [ ] Remote `origin` points at a repository under the `LFWeide` GitHub account (user creates the empty repo or confirms its name)
- [ ] Authentication uses the user's own Git Credential Manager sign-in or a personal access token entered interactively; no password or token is written to any file, remote URL or commit
- [ ] Initial commit contains the current project state, with no AI attribution lines (per CLAUDE.md)
- [ ] Push to `origin main` succeeds

Parent spec: ../spec.md
