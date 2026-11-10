#!/usr/bin/env bash
# Runs `/implement <issue>` for the next N issues in .scratch/planner-app/issues.
# Each issue gets its own fresh Claude session (one ticket per session).
#
# Usage:   ./implement-issues.sh N
# Example: ./implement-issues.sh 3
#
# Optional env vars:
#   ISSUES_DIR    folder with the issues   (default: .scratch/planner-app/issues)
#   CLAUDE_FLAGS  extra flags for claude   (default: --permission-mode acceptEdits)
#                 For fully unattended runs you may need broader permissions,
#                 e.g. CLAUDE_FLAGS="--dangerously-skip-permissions" (only in a safe/sandboxed repo).

set -euo pipefail

ISSUES_DIR="${ISSUES_DIR:-.scratch/planner-app/issues}"
CLAUDE_FLAGS="${CLAUDE_FLAGS:---permission-mode acceptEdits}"
DONE_FILE="$ISSUES_DIR/.implemented"   # remembers finished issues so reruns continue where you left off

if [[ $# -ne 1 || ! "$1" =~ ^[1-9][0-9]*$ ]]; then
  echo "Usage: $0 <number-of-issues>" >&2
  exit 1
fi
N="$1"

if [[ ! -d "$ISSUES_DIR" ]]; then
  echo "Issues folder not found: $ISSUES_DIR" >&2
  exit 1
fi
command -v claude >/dev/null || { echo "claude CLI not found in PATH" >&2; exit 1; }

touch "$DONE_FILE"

# The ticket file is the source of truth: an issue is done only if it says "Status: done".
is_done() { grep -qiE '^\*\*Status:\*\*[[:space:]]*done' "$1"; }

# Issue files in natural order (01, 02, ... 10), skipping ones already implemented.
mapfile -t ALL < <(find "$ISSUES_DIR" -maxdepth 1 -type f -not -name '.*' | sort -V)
PENDING=()
for f in "${ALL[@]}"; do
  is_done "$f" || PENDING+=("$f")
done

if [[ ${#PENDING[@]} -eq 0 ]]; then
  echo "No pending issues in $ISSUES_DIR"
  exit 0
fi

COUNT=$(( N < ${#PENDING[@]} ? N : ${#PENDING[@]} ))
echo "Implementing $COUNT of ${#PENDING[@]} pending issue(s)"

for (( i = 0; i < COUNT; i++ )); do
  issue="${PENDING[$i]}"
  echo
  echo "=== [$((i + 1))/$COUNT] /implement $issue ==="

  # shellcheck disable=SC2086  # CLAUDE_FLAGS is intentionally word-split
  # A session can exit 0 after stopping for a permission prompt, so also check the ticket itself.
  if claude -p "/implement $issue" $CLAUDE_FLAGS && is_done "$issue"; then
    grep -qxF "$issue" "$DONE_FILE" || echo "$issue" >> "$DONE_FILE"
    echo "--- done: $issue"
  else
    echo "!!! $issue was not completed (error, or Status not set to done), stopping." >&2
    exit 1
  fi
done

echo
echo "Finished $COUNT issue(s)."