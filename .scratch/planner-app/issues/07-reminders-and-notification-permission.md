# 07: Reminders and notification permission

**What to build:** Per-task reminders delivered as notifications, with the POST_NOTIFICATIONS runtime permission asked once at first start.

**Blocked by:** 02

**Status:** ready-for-agent

- [ ] Permission asked once on API 33+ only, using an already-asked flag
- [ ] Inexact alarm fires a notification in the 'Herinneringen' channel; tap opens the app
- [ ] Reminder cancelled on edit-off, delete and done; future ones rescheduled at start
- [ ] Switching reminder on while denied turns it off and shows a message with an 'Instellingen' button
- [ ] App works fully when permission is denied; no exact alarms, no boot receiver

Parent spec: ../spec.md
