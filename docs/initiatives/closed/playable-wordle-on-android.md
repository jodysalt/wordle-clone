---
title: Playable Wordle on Android
started: 2026-09-30
---

## Outcome
The owner installs a sideloaded debug APK on their own Android phone and plays full games of a Wordle-style game offline. A game has a 6×5 grid and six guesses at a hidden five-letter word. Each guess is scored green, yellow or grey per letter, with repeated letters handled the way Wordle handles them. Guesses that are not in the bundled valid-word list are rejected. An on-screen keyboard is coloured by what the player has learned. A win or a loss ends the game and shows the answer, and a new-game button starts another game with a fresh random answer from the bundled list at any time.

## Why now
Serves the strategic bet **Playable Wordle on Android**. The repo is an empty scaffold and this is the whole brief, so nothing else can start until the app exists. The approach is settled: native Kotlin and Jetpack Compose, minSdk 26, target and compileSdk 34, a random answer per game from an offline bundled list, and a debug build that is sideloaded rather than released. The machine already has the toolchain for this: JDK 17, Android SDK platform android-34 with build-tools 34.0.0, Gradle, and an emulator AVD (`Medium_Phone`).

## Success metrics
Checks an agent can run from the repo:
- `./gradlew assembleDebug` succeeds with the committed Gradle wrapper and produces `app/build/outputs/apk/debug/app-debug.apk`.
- `./gradlew testDebugUnitTest` passes. Its JVM unit tests cover guess scoring, including repeated letters in the guess and in the answer; rejection of invalid and wrong-length guesses; win after a correct guess; loss after six wrong guesses; keyboard letter state, where the best known state wins; and new game resetting state and picking an answer from the bundled list.
- The bundled answer list and valid-guess list are five-letter lowercase English words, every answer is also a valid guess, and each list has a recorded source whose licence allows bundling. A unit test checks the list rules.
- The merged manifest requests no `INTERNET` permission.
- Optional smoke check when the emulator is available: the APK installs on the `Medium_Phone` AVD with `adb install` and the main activity launches without crashing.

Check for a person:
- On the owner's phone, the sideloaded debug APK installs, a game plays through to a win and another to a loss, the answer is shown at the end, and new game starts a fresh word, all with networking turned off.

## Non-goals
- A daily word, or any date-based or shared puzzle.
- Play Store release, release signing, or a publishing or CI pipeline.
- Wordle or NYT names, logos, artwork, or official word lists.
- Online features: accounts, statistics sync, sharing, leaderboards, analytics, or ads.
- Other platforms, a hard mode, other word lengths, or languages other than English.
- Persisted statistics or streaks, and saving a game in progress across process death.
- Instrumented UI or Compose tests as a required gate. The game logic is covered by JVM unit tests, and the UI is checked on a device.

## Tickets
- docs/tickets/closed/playable-wordle-game/index.md
