Status: ready-for-agent

# Planner app

## Problem Statement

A student needs a simple Android planner for their MBDA final assignment. They want to keep track of tasks with due dates, see at a glance what is overdue and what comes next, browse Dutch public holidays and turn them into tasks, and get reminders. The app must satisfy the assignment's hard constraints (no external libraries beyond the allowed set, a web service, Navigation, Room, SharedPreferences, an implicit intent that returns a result, a runtime permission, portrait and landscape, no crashes). The code must be small and plain enough that the student can explain every file.

## Solution

A Dutch-language, Jetpack Compose app called "Planner" with three top-level destinations (Home, Taken, Feestdagen) plus a task form and Instellingen. Tasks are stored locally. Home shows overdue tasks, the next task and the following upcoming tasks. Feestdagen loads public holidays for a chosen year from the web and lets the user add a holiday as a task. A task can have an attached contact (picked from the system contact picker) and an optional reminder notification. One setting hides past holidays. Navigation uses a bottom bar in portrait and a rail in landscape.

## User Stories

1. As a user, I want to launch the app and land on Home, so that I immediately see what needs my attention.
2. As a user, I want to switch between Home, Taken and Feestdagen from a bottom navigation bar in portrait, so that I can move around with my thumb.
3. As a user, I want a navigation rail instead of the bottom bar in landscape, so that the layout uses the wide screen well.
4. As a user, I want to open Instellingen from an icon in the top bar, so that settings are reachable but out of the way.
5. As a user, I want all text in Dutch, so that the app is in my language.
6. As a user, I want to see overdue tasks first on Home with a red "Verlopen" label, so that I notice what I missed.
7. As a user, I want the earliest non-overdue task shown as a prominent "Volgende" card, so that I know what to do next.
8. As a user, I want the next 10 tasks after it listed under "Aankomend", so that I can look ahead.
9. As a user, I want to see "Niets gepland" when I have no open tasks, so that an empty Home is not confusing.
10. As a user, I want to tick a task done on Home and see it disappear from the lists, so that Home only shows open work.
11. As a user, I want Home in landscape to show the next card on the left and the list on the right, so that wide screens are used well.
12. As a user, I want Home to update immediately when I add or edit a task, so that it never shows stale data.
13. As a user, I want holidays not to appear on Home, so that Home only shows my own tasks.
14. As a user, I want to see all my tasks in the Taken list, so that I can manage them.
15. As a user, I want a done checkbox on each task row, so that I can complete tasks quickly.
16. As a user, I want to tap a task row to edit it, so that I can fix mistakes.
17. As a user, I want a floating add button on Taken, so that I can create a task quickly.
18. As a user, I want a delete icon on each row that asks "Weet je het zeker?", so that I do not delete by accident.
19. As a user, I want to cancel the delete confirmation, so that nothing is lost when I change my mind.
20. As a user, I want a task form with title, description, date, time, contact and reminder, so that I can describe a task fully.
21. As a user, I want to pick the due date with a Material date picker, so that I do not type dates.
22. As a user, I want to pick the due time with a Material time picker, so that I can set an exact time.
23. As a user, I want an empty title to show an error instead of saving or crashing, so that I know what to fix.
24. As a user, I want the description to be optional, so that simple tasks stay quick.
25. As a user, I want the form to keep my input when I rotate the device, so that I never lose typing.
26. As a user, I want the form to scroll in landscape, so that all fields are reachable on a short screen.
27. As a user, I want tasks to persist after the app is killed, so that my plan is not lost.
28. As a user, I want very long titles to be handled gracefully in lists, so that layouts do not break.
29. As a user, I want to open Feestdagen and see Dutch public holidays for the current year, so that I know the days off.
30. As a user, I want to step to the previous or next year with arrows, so that I can plan ahead or look back.
31. As a user, I want each holiday to show its Dutch name, date and weekday, so that I can read it at a glance.
32. As a user, I want a loading indicator while holidays load, so that I know something is happening.
33. As a user, I want a clear error message with an "Opnieuw proberen" button when loading fails, so that I can retry after fixing my connection.
34. As a user, I want no crash on no internet, timeout, HTTP error or malformed data, so that the app is dependable.
35. As a user, I want "Geen feestdagen gevonden" when the list is empty, so that an empty result is explained.
36. As a user, I want to pull down to refresh the holidays, so that I can reload without a button.
37. As a user, I want an "Toevoegen aan taken" button on each holiday, so that I can turn a holiday into a task.
38. As a user, I want the created task titled with the holiday name and due 09:00 local time on that date, so that it is useful without editing.
39. As a user, I want the button to become a disabled "Toegevoegd" when that task already exists, so that I do not create duplicates.
40. As a user, I want an added holiday to appear in Taken and Home right away, so that the action visibly worked.
41. As a user, I want a setting "Verberg voorbije feestdagen", so that the holiday list only shows upcoming days.
42. As a user, I want that setting to survive an app restart, so that I set it only once.
43. As a user, I want the setting to change the holiday list immediately, so that its effect is visible.
44. As a user, I want a "Kies contact" button on the task form, so that I can attach a person to a task.
45. As a user, I want only the contact name stored, so that the app needs no contacts permission.
46. As a user, I want cancelling the contact picker to change nothing, so that I can back out safely.
47. As a user, I want a contact without a name to be handled without crashing, so that odd contacts do not break the form.
48. As a user, I want the contact name shown on the form, in the Taken row and on the Home card, so that I see who a task involves.
49. As a user, I want a button to clear the attached contact, so that I can undo my choice.
50. As a user, I want a reminder switch on the task form, so that I can be notified about a task.
51. As a user, I want to be asked once at first start for notification permission, so that reminders can work without nagging me.
52. As a user, I want the app to keep working fully if I deny notifications, so that denial is never punishing.
53. As a user, I want switching a reminder on while permission is denied to turn the switch back off and show a message, so that I understand why it did not apply.
54. As a user, I want that message to have an "Instellingen" button opening the app's notification settings, so that I can grant permission.
55. As a user, I want a reminder to produce a notification in the "Herinneringen" channel at the due time, so that I am reminded.
56. As a user, I want tapping the notification to open the app, so that I can act on it.
57. As a user, I want a reminder cancelled when I edit it off, delete the task or mark it done, so that I get no stale notifications.
58. As a user, I want future reminders rescheduled when the app starts, so that they survive the process being killed.
59. As a user, I want rotation to work on every screen without losing state or crashing, so that the app feels solid.
60. As a user, I want the UI to stay responsive during network and database work, so that the app never freezes.
61. As the student submitting this, I want no forbidden libraries in any Gradle file, so that I do not fail the assignment.
62. As the student, I want the project to open in Android Studio with no extra steps, so that the teacher can run it.
63. As the student, I want zero Android Studio warnings, so that code quality is visibly clean.
64. As the student, I want small single-purpose files with a why-comment at the top, so that I can explain each file.
65. As the student, I want a walkthrough document mapping requirements to files, so that I can prepare for questions.

## Implementation Decisions

- Single-activity app "Planner" (package per the plan), Kotlin, Compose only, Dutch UI strings in resources, English identifiers and comments. Note: the existing template package differs from the package named in the plan; reconcile this in the skeleton phase.
- Target Android 15 (compile and target 35, min 26). No dependency injection framework; plain ViewModel plus repository.
- Allowed dependencies only: Kotlin and coroutines, AndroidX Compose BOM, Material3, Navigation Compose, Lifecycle ViewModel and runtime Compose, Room with KSP, and the platform JSON classes. KSP version must match the Kotlin version.
- Navigation: routes for Home, Taken, Feestdagen, task form (add and edit) and Instellingen. Bottom bar versus rail chosen by orientation.
- Task is a Room entity with: id, title (required), description (optional), due time as epoch millis, done flag, optional contact name, reminder flag. The DAO exposes an ordered Flow of tasks plus insert, update, delete and find-by-id. A repository wraps it; a tasks ViewModel exposes StateFlow and runs DB calls in the viewModelScope.
- Home split is a pure function (separate from Compose) taking open tasks and "now" and returning overdue, next and upcoming (next 10 after it). A Home ViewModel applies it.
- Holiday is a plain data class. A holiday API module does one HTTPS GET for the NL public holidays of a year, with 10 s connect and read timeouts, a status-code check, always disconnecting, and parsing with the platform JSON classes. A holiday repository and ViewModel expose a Loading / Success / Error state plus the selected year. No caching, no Room for holidays.
- Duplicate guard: a holiday counts as already added when a task exists with the same title and due time.
- Settings repository wraps SharedPreferences (file name per the plan) holding "hide past holidays" and the "already asked notification permission" flag. Past is determined by comparing the holiday date to today.
- Contact picker uses the platform PickContact contract and reads only the display name from the returned URI. The READ_CONTACTS permission must not be added.
- Reminders: a scheduler (schedule, cancel, reschedule all), a non-exported broadcast receiver, and a notification channel created at startup. Inexact alarms that allow while idle; no exact-alarm permission; immutable pending intents with request code equal to task id; no boot receiver. Scheduling happens on save, cancelling on edit-off/delete/done, rescheduling at app start.
- POST_NOTIFICATIONS is requested once at first start on API 33+ only.
- All network and DB work is off the main thread.
- Delivery follows the plan's phases in order (skeleton, tasks CRUD, Home, holidays, settings, contact picker, reminders, hardening audit, walkthrough doc), one vertical slice per phase, one commit per phase with no AI attribution lines in the repo's commits per project instructions.

## Testing Decisions

- A good test exercises external behaviour only: what the user sees and what persists, not ViewModel or DAO internals.
- Seam 1 (highest): Compose UI tests against the app's top-level composable with an in-memory Room database and a fake holiday API, covering task CRUD, Home states, holiday states (loading, success, error, retry, empty, duplicate guard), the hide-past setting, and form validation. Anything that is only reachable via system UI (contact picker, permission dialog, alarm firing) is verified manually and listed in a "Please verify" checklist per phase.
- Seam 2: plain JVM unit tests on the Home split pure function (overdue, next, upcoming cap of 10, empty, boundary where due equals now).
- Prior art: only the template's example unit and instrumented tests exist; there is no real prior art in the codebase.
- No Android SDK may be available while implementing, so claims of compiling or passing must not be made without running the build.

## Out of Scope

Caching, Room on holidays, images, widgets, sensors, services, CRUD on the web service, boot receiver, exact alarms, dependency injection frameworks, extra settings, a country selector, dark-mode customisation beyond the Material3 default, and READ_CONTACTS.

## Further Notes

- Source of truth for constraints and phases is the project's build plan document; this spec restates it in user terms.
- The domain glossary and triage-labels files referenced by the tooling do not exist yet; the `ready-for-agent` label is recorded in the Status line above.
- Seams were proposed without confirmation; adjust them if the student prefers manual-only verification.
