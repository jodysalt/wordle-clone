# Vision

A Wordle clone built as a native Android app in Kotlin and Jetpack Compose (minSdk 26, target and compileSdk 34). It plays like Wordle: guess a hidden five-letter word in six tries, with each guess scored letter by letter as correct, present or absent and an on-screen keyboard that shows what is known. Each game picks a random word from a word list bundled in the app, so it works fully offline, and a new-game button starts another game at any time. For now the aim is a debug build the owner sideloads onto their own phone to play and test. It is a personal project, not a Play Store release.

## Target users

The project owner, who wants to play and test the game on their own Android phone. They are the first user and, for now, the only one. The app reaches them as a sideloaded debug build, not through a store.

## Core problems

**No Wordle of their own on the phone.** The owner wants a Wordle-style game they built and control, installed natively on their Android phone.
**One puzzle a day is not enough.** The daily-word format stops after one game, and the owner wants to start a fresh random word whenever they like.
**Testing needs a real device.** The owner needs to build and install it on their own phone to try it out, without a store listing or a backend.

## Product principles

**Plays like Wordle.** The layout and rules follow Wordle: a 6×5 grid, five-letter words, six guesses, green, yellow and grey scoring that handles repeated letters the way Wordle does, guesses that must be real words, and a keyboard coloured by what has been learned.
**Never Wordle's brand.** The app copies how Wordle looks and behaves, but never its name, logo, artwork or word lists. Every word list it bundles comes from a source whose licence allows it.
**Offline and self-contained.** Everything the game needs ships inside the APK. There is no network access, account or server.
**Native Kotlin and Compose.** The UI is Jetpack Compose on minSdk 26 and target and compileSdk 34, with no cross-platform framework.
**Game logic apart from UI.** Guess scoring, word checking and game state live in plain Kotlin that unit tests can cover without a device.
**Easy to put on the phone.** The debug APK builds from the command line with the Gradle wrapper and installs by sideloading or `adb install`. Nothing gets in the way of that.

## Strategic bets

The few pushes that move the vision forward, one `###` section per bet. An initiative cites a bet by its exact title.

### Playable Wordle on Android

Changes the repo from an empty scaffold into a Kotlin and Compose Android app that plays full games of Wordle. Each game picks a random answer from a bundled word list and checks guesses against a bundled list of valid words. The app has the grid, the on-screen keyboard, win and loss states that reveal the answer, and a new-game button. Why now: nothing exists yet, and this is the whole brief. Success: `./gradlew assembleDebug` produces an APK that installs on the owner's phone, where they can play a game to a win or a loss and start another one, all offline, with unit tests covering the scoring rules, repeated letters included.

## Non-goals

**Daily word.** There is no shared daily puzzle or date-based answer. Every game gets a random word.
**Play Store release.** No store listing, release signing or publishing pipeline. A sideloaded debug build is the target.
**Wordle branding or licensed material.** No NYT or Wordle name, logo, artwork or official word lists.
**Online features.** No accounts, leaderboards, sharing, sync, analytics or ads.
**Other platforms.** No iOS, web or desktop builds.
**Variants.** No hard mode, other word lengths or languages besides English.
