# HabitFlow

A simple, offline-first Android habit tracker built with **Kotlin + Jetpack Compose + Room**.

## Features
- Add daily/weekly habits with a name and emoji icon
- Mark habits complete with one tap; automatic streak counter
- Calendar view showing which habits were done on which day
- Weekly/monthly completion statistics
- Custom local reminder notification per habit (survives phone restart)
- Light and Dark mode ("Calm Wellness" theme)
- 100% local storage (Room database) — no internet or account required

---

## 1. How to Upload This Repo to GitHub (No PC Needed)

You can do this entirely from the **GitHub app** or **github.com in your phone's browser**:

1. Open the GitHub app (or github.com) and sign in.
2. Tap **+** → **New repository**. Name it `HabitFlow` (or anything you like) and create it.
3. Inside the new repo, use **Add file → Create new file**.
4. For each file below, type the **exact path** shown (e.g. `app/build.gradle.kts`) into the file name box — GitHub will automatically create the folders for you — then paste that file's content, and commit.
5. Repeat for every file in this project until the full folder structure matches what's listed here.
6. Once the last file is committed, go to the **Actions** tab in your repo — a workflow run will start automatically (or trigger it manually with **Run workflow** if it doesn't).

That's it — no PC, no Android Studio required to get a build going.

## 2. Where to Download the Built APK

After the Actions workflow finishes (usually 3–6 minutes):

- **Option A — Artifacts:** Go to the **Actions** tab → open the latest successful run → scroll to **Artifacts** → download `habitflow-debug-apk`. It downloads as a `.zip`; extract it to get the `.apk` file.
- **Option B — Releases:** Go to the **Releases** section of your repo (right sidebar on github.com, or the "Releases" tab in the app) → open the latest release → download the `.apk` file directly.

## 3. How to Install the App on Your Phone

1. Download the `.apk` file to your phone (from Artifacts or Releases, as above).
2. Tap the downloaded file to open it.
3. If prompted, allow "Install unknown apps" for your browser/file manager (Android will show this the first time — it's a one-time permission).
4. Tap **Install**, then **Open** once it finishes.
5. On first launch, allow the notification permission so habit reminders can appear.

---

## Project Structure

```
HabitFlow/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── .gitignore
├── .github/workflows/build.yml
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/habitflow/app/
        │   ├── HabitFlowApp.kt
        │   ├── MainActivity.kt
        │   ├── data/                (Room: Habit, HabitCompletion, HabitDao, HabitDatabase, HabitRepository)
        │   ├── notification/        (NotificationScheduler, ReminderReceiver, BootReceiver)
        │   └── ui/
        │       ├── theme/           (Color.kt, Theme.kt, Type.kt)
        │       ├── navigation/      (Screen.kt, HabitFlowNavHost.kt)
        │       ├── home/            (HomeScreen, HomeViewModel)
        │       ├── addhabit/        (AddHabitScreen, AddHabitViewModel)
        │       ├── calendar/        (CalendarScreen, CalendarViewModel)
        │       ├── stats/           (StatsScreen, StatsViewModel)
        │       └── GenericViewModelFactory.kt
        └── res/
            ├── values/ (strings.xml, themes.xml, colors.xml)
            ├── drawable/ic_launcher_foreground.xml
            ├── mipmap-anydpi-v26/ (ic_launcher.xml, ic_launcher_round.xml)
            └── xml/ (backup_rules.xml, data_extraction_rules.xml)
```

## Adding New Features Later
The code is split by feature (`home`, `addhabit`, `calendar`, `stats`), each with its own
ViewModel talking only to `HabitRepository`. To add a new feature: create a new package
under `ui/`, add a route in `Screen.kt` and `HabitFlowNavHost.kt`, and reuse
`HabitRepository` for data access — no other files need to change.
