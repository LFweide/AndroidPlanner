# MBDA Eindopdracht: Planner app (build plan for Claude Code)

Target grade: **7.0** = RV in every category (35/50 points), with Stability at G as buffer.
Solo project (no partner). The user will read, test and submit this themselves, so the code must be simple enough to explain.

---

## 0. How to work

- Work **one phase at a time**, in order. Each phase is a *vertical slice*: when it is done, the app builds, runs and shows something new that works end to end (UI → ViewModel → data).
- **No Android SDK or network may be available in your sandbox.** If you can't run `./gradlew`, don't claim it compiles. Re-read every file you wrote and check imports, signatures and nullability. At the end of each phase print a short "Please verify" list for the user (open in Android Studio → sync → run → try X).
- **Stop after each phase** and wait for the user to report back (build errors, Logcat output, "works"). Fix reported errors before starting the next phase.
- If the project is a git repo, make one commit per phase. Do not add AI attribution lines to commits. The user is submitting this as their own work.
- Language: **code identifiers and comments in English, all UI text in Dutch** (in `strings.xml`, never hardcoded in Compose).
- Code style, because the user must be able to explain every file:
    - small files, one responsibility each; clear names; no clever abstractions
    - a short comment on the *why* at the top of each file and above non-obvious logic
    - no dependency injection framework, no generics-heavy architecture. Plain `ViewModel` + repository.
    - **zero Android Studio warnings**: no unused imports/params/variables, no deprecated APIs, `@OptIn` where an experimental Compose API is used, no hardcoded strings (lint)

## 1. Hard constraints (from the assignment, breaking these means grade 1.0)

| Rule | What it means here |
|---|---|
| No external libraries without teacher approval | Allowed: Kotlin stdlib/coroutines, AndroidX Compose (BOM), Material3, Navigation Compose, Lifecycle ViewModel/runtime Compose, **Room** (+ KSP plugin), `org.json` (part of Android). **Forbidden**: Retrofit, OkHttp, Gson/Moshi, Coil/Glide, WorkManager, Hilt/Koin, any other 3rd-party lib. |
| Webservice | Holidays JSON over HTTPS via `HttpURLConnection`. |
| ≥ 2 screens with Navigation | Navigation Compose, 4+ routes. |
| LazyColumn with web data | Holidays list. |
| Multithreaded / UI always responsive | All network and DB work off the main thread (`viewModelScope` + `Dispatchers.IO`, Room suspend/Flow). |
| ≥ 1 "bewerking" | "Add to tasks" button on each holiday row, plus full task CRUD. |
| Implicit intent that returns a result | Contact picker (`ActivityResultContracts.PickContact`). |
| Setting saved in SharedPreferences | "Hide past holidays" boolean. |
| ≥ 1 dangerous permission asked at run time | `POST_NOTIFICATIONS`. |
| Portrait AND landscape work | Test both. Scrollable forms; NavigationRail in landscape. |
| Jetpack Compose for all screens | No XML layouts. |
| Android 15 | `compileSdk 35`, `targetSdk 35`, `minSdk 26`. |
| App must not crash or log exceptions | Handle: no internet, timeout, HTTP error, bad JSON, empty list, permission denied, rotation. |
| Imports into Android Studio with no extra steps | Include Gradle wrapper, `gradle/libs.versions.toml`, `.gitignore`. No local machine paths in `local.properties`. |

## 2. Settled design decisions

**App:** name "Planner", package `com.example.planner`, default launcher icon, Dutch UI, Kotlin, single `MainActivity`.

**Navigation:** bottom `NavigationBar` (portrait) with Home / Taken / Feestdagen. In landscape, `NavigationRail` instead. Extra routes: task form (add/edit) and Instellingen (opened from a top-bar icon).

**Task entity** (Room): `id` (auto), `title` (required), `description` (optional), `dueAt` (epoch millis), `done` (Boolean), `contactName` (String?), `reminder` (Boolean).

**Home:** one Room query of not-done tasks ordered by `dueAt`, split in the ViewModel:
- *Overdue* (`dueAt < now`): shown first, with a red "Verlopen" label
- *Volgende* (earliest non-overdue): prominent card
- *Aankomend*: the next 10 after it
- Empty state: "Niets gepland". Holidays are NOT shown on Home.

**Taken (CRUD):** list with done checkbox; tap row to edit; add via FAB; delete icon asks "Weet je het zeker?". Form: title, description, date picker, time picker, contact picker button, reminder switch.

**Feestdagen (web data):** `GET https://date.nager.at/api/v3/PublicHolidays/{year}/NL` → fields `date` ("yyyy-MM-dd"), `localName`, `name`, `global`, `types`. Year selector (prev/next arrows). Show `localName`, date, weekday. States: loading / success / error with **Opnieuw proberen** button; empty array → "Geen feestdagen gevonden". Pull-to-refresh (`PullToRefreshBox`, needs `@OptIn(ExperimentalMaterial3Api::class)`). No caching. Each row has an **"Toevoegen aan taken"** button that creates a task (title = `localName`, due that date 09:00 local time, no reminder). If a task with the same title and `dueAt` exists, the button shows disabled "Toegevoegd".

**Instellingen:** one switch "Verberg voorbije feestdagen" in SharedPreferences (`planner_prefs`), applied on the holidays list.

**Contact picker:** `PickContact` → read `ContactsContract.Contacts.DISPLAY_NAME` from the returned URI via `contentResolver.query` → store only the name string on the task. Does not need `READ_CONTACTS` for a URI the picker returned. Do **not** add that permission.

**Reminders:**
- `POST_NOTIFICATIONS` is asked **at app start** (once, if not granted, using a "already asked" flag in SharedPreferences).
- Reminder fires with `AlarmManager.setAndAllowWhileIdle` (inexact; do **not** use exact alarms or request `SCHEDULE_EXACT_ALARM`) → `BroadcastReceiver` → notification (own channel "Herinneringen"). `PendingIntent` with `FLAG_IMMUTABLE`, request code = task id. Tap opens `MainActivity`.
- Schedule on save, cancel on edit/delete/done, reschedule all future reminders at app start. No boot receiver.
- If the user switches a reminder on while permission is denied: switch goes back off, show a message with an **"Instellingen"** button that opens `Settings.ACTION_APP_NOTIFICATION_SETTINGS`. App keeps working fully without the permission.

## 3. Phases

### Phase 1: Skeleton that builds and navigates
**Goal:** an empty but correct project the user can open, sync and run.
- Gradle wrapper, `settings.gradle.kts`, root + app `build.gradle.kts`, `gradle/libs.versions.toml` (version catalog), `.gitignore`, `proguard` defaults.
- Use current stable, mutually compatible versions of AGP, Kotlin, the Compose compiler plugin, Compose BOM, Navigation Compose, Lifecycle and KSP. **KSP's version must match the Kotlin version.** If you can check official docs, do; otherwise pick versions you are certain about and say so.
- Manifest: `INTERNET`, `POST_NOTIFICATIONS`, `MainActivity` (no `configChanges`, let rotation recreate).
- Theme, `strings.xml` (Dutch), `MainActivity`, `PlannerApp` composable with Scaffold, bottom bar / rail switching on orientation, NavHost with placeholder screens: Home, Taken, Feestdagen, Instellingen.
- **Done when:** app launches, you can switch the 3 tabs and open Instellingen, and it works in portrait and landscape (rail vs bar).

### Phase 2: Tasks CRUD (vertical: Room → ViewModel → screens)
**Goal:** full create / read / update / delete of tasks.
- `Task` entity, `TaskDao` (`Flow<List<Task>>` ordered by `dueAt`, insert, update, delete, find by id), `AppDatabase`, `TaskRepository`.
- `TasksViewModel` exposing `StateFlow`, with all DB calls in `viewModelScope`.
- Taken screen (LazyColumn, done checkbox, delete with confirm dialog, FAB), `TaskFormScreen` (title, description, Material3 date + time pickers). Validation: empty title shows an error, not a crash.
- Form must survive rotation (state in `rememberSaveable`/ViewModel) and scroll in landscape.
- **Done when:** tasks persist after killing the app; edit and delete work; rotation keeps form input.

### Phase 3: Home
**Goal:** Home shows overdue / next / upcoming from the Room data.
- `HomeViewModel` with the split described in section 2 (pure function, easy to explain; keep it separate from Compose).
- Home UI: "Volgende" card, "Aankomend" LazyColumn, "Verlopen" section, empty state. Landscape: two columns (next card left, list right) as the layout difference.
- Checking a task done on Home removes it from the lists.
- **Done when:** adding/editing tasks in Taken changes Home immediately; all three states look right.

### Phase 4: Holidays from the web
**Goal:** the required webservice + LazyColumn + operation.
- `Holiday` data class, `HolidayApi` (`HttpURLConnection`, 10 s connect/read timeouts, status-code check, always disconnect, parse with `org.json`), `HolidayRepository`, `HolidaysViewModel` with `UiState` (Loading / Success / Error) and year state.
- Screen: year prev/next, list, retry button, pull-to-refresh, empty state.
- "Toevoegen aan taken" with duplicate guard (compare against existing tasks).
- **Done when:** airplane mode shows the error state and Retry works; no crash on timeout, HTTP error or malformed JSON; adding a holiday shows it in Taken and Home.

### Phase 5: Settings (SharedPreferences)
**Goal:** a saved setting that visibly changes behaviour.
- `SettingsRepository` wrapping `SharedPreferences`, `SettingsScreen` with one switch, holidays list hides past dates when on (compare `LocalDate` with today).
- **Done when:** the switch survives an app restart and changes the holidays list.

### Phase 6: Contact picker (implicit intent)
**Goal:** attach a contact to a task.
- In `TaskFormScreen`: "Kies contact" button using `rememberLauncherForActivityResult(PickContact())`. Handle cancel (null result) and a contact without a name. Show the name on the form, in the Taken row and on the Home card. A button clears it.
- **Done when:** picking a contact stores the name; cancelling the picker changes nothing; no extra permission needed.

### Phase 7: Reminders + permission
**Goal:** notification extra and the dangerous permission.
- Ask `POST_NOTIFICATIONS` at first app start (Android 13+ only; skip the check below API 33).
- `ReminderScheduler` (schedule / cancel / rescheduleAll), `ReminderReceiver` (registered in manifest, `exported=false`), notification channel created at startup, notification opens the app.
- Wire into save / edit / delete / done and app start.
- Denied-permission flow from section 2.
- **Done when:** a reminder set 1–2 minutes ahead produces a notification; deleting or completing the task prevents it; denying the permission never crashes and the Instellingen button works.

### Phase 8: Hardening and rubric audit
**Goal:** Stability G and clean code.
- Review every file for: unused code/imports, deprecated calls, hardcoded strings, main-thread work, unhandled exceptions, missing `@OptIn`.
- Walk the checklist: no internet, airplane mode mid-load, permission denied, rotate on every screen, empty states, very long titles, past-due dates, year with no data, double-tapping buttons.
- Confirm no forbidden library is in any Gradle file.
- Lint: report anything you can't verify without running Android Studio, so the user can check **Analyze → Inspect Code**.
- Tick off every row of the rubric and of section 1 in a table, with the file that proves it.
- **Done when:** the table has no gaps.

### Phase 9: Walkthrough doc for the user
**Goal:** the user can explain every file. Write `docs/WALKTHROUGH.md` in English:
1. One-page overview: screens, data flow diagram in text, where threads are used.
2. **Requirement → file table** (assignment minimum requirements + rubric rows).
3. File-by-file explanation in plain words, in the order the app runs.
4. "Questions a teacher might ask" with short answers (e.g. why Room, why coroutines on `Dispatchers.IO`, what happens on rotation, why inexact alarms, why `PickContact` needs no permission, how the duplicate guard works).
5. Known limitations (no reboot persistence for reminders, no caching).
6. Final "how to run" steps and a pre-submission checklist (clean build, run on an Android 15 emulator, portrait and landscape, ZIP opens in Android Studio without extra steps).

## 4. Out of scope (do not add)
Caching, Room on the holidays, images, widgets, sensors, services, CRUD on the web service, boot receiver, exact alarms, DI frameworks, extra settings, a country selector, dark-mode customisation beyond the Material3 default.