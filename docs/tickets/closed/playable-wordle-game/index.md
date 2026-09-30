---
title: Playable Wordle Game
type: feat
initiative: playable-wordle-on-android
priority: high
---

# Goal
The owner builds a debug APK with `./gradlew assembleDebug`, sideloads it onto their Android phone, and plays full Wordle-style games offline. Each game runs from a random answer to a win or a loss, shows the answer at the end, and a new-game button starts another game. This one ticket takes the repo from an empty scaffold to an installable, playable game, so the owner has something on the phone before any polish.

# Context
- The repo has docs only: no Gradle project, no source, no wrapper. Everything in this ticket is new.
- Toolchain on the machine: JDK 17 via `JAVA_HOME`, `ANDROID_HOME=/Users/jody/Library/Android/sdk` with platform `android-34` and build-tools `34.0.0`, Gradle 9.6 on `PATH`, emulator AVD `Medium_Phone`. The local Gradle cache already holds AGP `8.6.1`, Kotlin Gradle plugin `2.0.20` and the Gradle `8.7` distribution, so pinning those avoids new downloads.
- Settled by `docs/vision.md` and the initiative: native Kotlin and Jetpack Compose, minSdk 26, target and compileSdk 34, game logic in plain Kotlin apart from the UI, word lists bundled in the APK, no network access, sideloaded debug build only.
- Initiative: `docs/initiatives/open/playable-wordle-on-android.md`. Its success metrics are this ticket's acceptance criteria.

# Scope
In:
- Gradle project: committed Gradle wrapper, `settings.gradle.kts`, root and `app` build files, a single `app` module, `local.properties` ignored by git, `.gitignore` for build output.
- Pure Kotlin game logic with no Android imports: guess scoring (correct, present, absent) with Wordle's repeated-letter rules, guess validation (length and valid-word list), game state (guesses so far, current input, won, lost, answer), keyboard letter state where the best known state wins (correct over present over absent over unknown), and new game picking a random answer from the answer list.
- Bundled word lists as assets or raw resources: an answer list and a valid-guess list, five-letter lowercase English words, every answer also a valid guess, with each list's source and licence recorded in the repo.
- Compose UI in one activity: the 6×5 grid with tile colours, the on-screen QWERTY keyboard with Enter and Backspace coloured by keyboard letter state, a short message when a guess is rejected (too short or not a word), an end-of-game message that says win or loss and shows the answer, and a new-game button that is always available.
- JVM unit tests for the logic and the word-list rules.

Out:
- Tile flip, shake or other animations; a physical-keyboard input path; dark-theme tuning beyond Material defaults.
- Statistics, streaks, and saving a game across process death (initiative non-goal). Surviving a screen rotation is in if a `ViewModel` gives it for free, not otherwise required.
- Instrumented or Compose UI tests (initiative non-goal).
- Daily word, hard mode, other lengths or languages, any Wordle or NYT name, logo or official word list, release signing, CI.

# Acceptance criteria
- `./gradlew assembleDebug` succeeds from a clean checkout with the committed wrapper and produces `app/build/outputs/apk/debug/app-debug.apk`.
- `./gradlew testDebugUnitTest` passes, with JVM tests covering:
  - scoring an all-correct, all-absent and mixed guess;
  - repeated letters: a guess letter repeated more often than in the answer scores the extra copies absent, correct matches are claimed before present ones (for example answer `abbey`, guess `babes`; answer `crane`, guess `eerie`);
  - rejection of a guess that is too short or not in the valid-guess list, with the guess count unchanged;
  - win after a correct guess, and no further guesses accepted;
  - loss after six wrong guesses, with the answer exposed;
  - keyboard letter state keeping the best known state across guesses;
  - new game resetting guesses, input and end state and picking an answer from the answer list (with an injectable `Random` so the test is deterministic).
- A unit test checks the bundled lists: every entry is five lowercase ASCII letters, no duplicates, both lists non-empty, and every answer is in the valid-guess list.
- Each bundled list has a recorded source and licence in the repo (for example a `NOTICE` or a README next to the lists), and the licence allows bundling.
- The merged debug manifest has no `android.permission.INTERNET`.
- The game logic package imports nothing from `android.*` or `androidx.*`.
- Optional smoke check when the emulator is available: the APK installs on the `Medium_Phone` AVD with `adb install -r` and `adb shell am start` launches the main activity without a crash in `logcat`.

# Implementation notes
- Suggested versions (all in the local cache): Gradle wrapper `8.7` (`gradle wrapper --gradle-version 8.7`), AGP `8.6.1`, Kotlin `2.0.20` with the `org.jetbrains.kotlin.plugin.compose` plugin, a Compose BOM that works with them, `jvmTarget` 17. Use a version catalog (`gradle/libs.versions.toml`) if convenient.
- Application id and package: something neutral such as `com.jodysalt.wordclone`; the launcher label must not say "Wordle". Suggested app name: "Word Guess".
- Suggested layout: `app/src/main/java/.../game/` for `Scorer`, `WordList`, `Game` (immutable state plus reducer-style functions or a small class) and `LetterState`; `.../ui/` for `GameViewModel`, `GameScreen`, `Grid`, `Keyboard`; `MainActivity`. The ViewModel loads the lists from assets once and hands them to the pure logic.
- Scoring algorithm: first pass marks exact matches and counts the remaining answer letters; second pass marks present while that count stays above zero, absent otherwise.
- Word lists (decided by the owner): valid guesses are the five-letter words of the ENABLE word list (public domain); answers are a few hundred common words chosen from it. Both lists are committed into the repo as files; nothing is fetched at build or run time. Record the ENABLE source and its public-domain status in the notice file. Never use the official Wordle answer or guess lists.
- Load lists from `app/src/main/assets/` so the unit test can read the same files from the project path.

# Test plan
- Unit: the JVM tests listed under acceptance criteria, run with `./gradlew testDebugUnitTest`.
- Build: `./gradlew assembleDebug`, then inspect `app/build/intermediates/merged_manifest/debug/` (or `aapt dump permissions`) for no `INTERNET`.
- Smoke (optional, agent): install and launch on the `Medium_Phone` emulator via `adb`.
- Manual (owner): sideload `app-debug.apk` on the phone with networking off; play one game to a win and one to a loss, check tile and keyboard colours, check the answer shows at the end, and start a new game.

# Rollout
No flags or migrations. The deliverable is the debug APK at `app/build/outputs/apk/debug/app-debug.apk`, installed with `adb install -r` or by copying it to the phone and opening it (the owner must allow installs from unknown sources). Fallback is uninstalling the app.

## Strategic fit
Advances the initiative **Playable Wordle on Android** by delivering its whole outcome as one vertical slice: installable APK, full games, scoring, keyboard, win and loss, new game. Serves the target-user problems *No Wordle of their own on the phone*, *One puzzle a day is not enough* and *Testing needs a real device*, and the principles *Plays like Wordle*, *Offline and self-contained*, *Native Kotlin and Compose*, *Game logic apart from UI* and *Easy to put on the phone*. Respects the non-goals: no daily word, no Play Store release, no Wordle branding or official word lists, no online features, no variants.
