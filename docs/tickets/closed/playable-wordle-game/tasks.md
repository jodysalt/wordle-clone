# Tasks

## Scaffold the Android Gradle project (wrapper 8.7, AGP 8.6.1, Kotlin 2.0.20, Compose) with an empty `MainActivity` in package `com.jodysalt.wordclone`
- **category:** chore
- **status:** done
- **steps:**
  - Generate the wrapper with `gradle wrapper --gradle-version 8.7` at the repo root and commit `gradlew`, `gradlew.bat` and `gradle/wrapper/*`. Use only versions already in `~/.gradle/caches/modules-2/files-2.1` so nothing new downloads: AGP `8.6.1` (`com.android.application`), Kotlin `2.0.20` (`org.jetbrains.kotlin.android` and `org.jetbrains.kotlin.plugin.compose`), and explicit library versions instead of a Compose BOM (no BOM is cached): `androidx.core:core-ktx:1.13.1`, `androidx.activity:activity-compose:1.9.2`, `androidx.compose.ui:ui:1.7.2`, `androidx.compose.foundation:foundation:1.7.2`, `androidx.compose.material3:material3:1.3.0`, `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4`, and `junit:junit:4.13.2` as `testImplementation`. Declare them in `gradle/libs.versions.toml`.
  - Create `settings.gradle.kts` (single module `:app`, `google()` and `mavenCentral()` repositories), a root `build.gradle.kts` (plugins `apply false`), and `app/build.gradle.kts` with `namespace` and `applicationId` `com.jodysalt.wordclone`, `minSdk 26`, `compileSdk 34`, `targetSdk 34`, Java and Kotlin `jvmTarget` 17, `buildFeatures { compose = true }`. Add `gradle.properties` with `android.useAndroidX=true`.
  - Create `app/src/main/AndroidManifest.xml` with one launcher activity `.MainActivity`, the label `@string/app_name` = "Word Guess" (never "Wordle"), and no `<uses-permission>` entries. Create `app/src/main/java/com/jodysalt/wordclone/MainActivity.kt` as a `ComponentActivity` whose `setContent` shows a placeholder `Text("Word Guess")` inside `MaterialTheme`.
  - Extend `.gitignore` (keep the existing `.claude/worktrees/` line) with `.gradle/`, `build/`, `/app/build/`, `local.properties`, `.idea/`, `*.iml`, `.kotlin/`. Create an untracked `local.properties` with `sdk.dir=/Users/jody/Library/Android/sdk` if `ANDROID_HOME` is not picked up.
  - Run `./gradlew assembleDebug` with JDK 17 and confirm `app/build/outputs/apk/debug/app-debug.apk` exists.

## Bundle the word lists in `app/src/main/assets/` (`valid_guesses.txt` from ENABLE, `answers.txt` of common words) with a notice recording their source and licence
- **category:** chore
- **status:** done
- **steps:**
  - Download the ENABLE word list (public domain) from `https://raw.githubusercontent.com/dolph/dictionary/master/enable1.txt`, keep only words of exactly five lowercase ASCII letters, sort, de-duplicate, and write them one per line to `app/src/main/assets/valid_guesses.txt`. Never use the official Wordle/NYT answer or guess lists.
  - Hand-pick 300 to 500 common, familiar five-letter English words (no plurals ending in `s`, no proper nouns, no obscure or offensive words), each present in `valid_guesses.txt`, and write them sorted and de-duplicated one per line to `app/src/main/assets/answers.txt`.
  - Write `app/src/main/assets/WORDLISTS_NOTICE.md` stating: `valid_guesses.txt` is the five-letter subset of the ENABLE word list (source URL above), which is in the public domain; `answers.txt` is a hand-picked subset of that list, likewise public domain; neither is derived from the Wordle or NYT lists.
  - Run `comm -23 app/src/main/assets/answers.txt app/src/main/assets/valid_guesses.txt | wc -l` and confirm it prints `0`, and `grep -cvE '^[a-z]{5}$' app/src/main/assets/valid_guesses.txt app/src/main/assets/answers.txt` prints `0` for both files.

## Add `LetterState` and `Scorer.score(guess, answer)` with Wordle's repeated-letter rules in `app/src/main/java/com/jodysalt/wordclone/game/`, covered by `ScorerTest`
- **category:** functional
- **status:** done
- **steps:**
  - Create `game/LetterState.kt` with `enum class LetterState { UNKNOWN, ABSENT, PRESENT, CORRECT }` (declared in rising order of knowledge so `maxOf`/ordinal comparison picks the best) and `game/Scorer.kt` with `object Scorer { fun score(guess: String, answer: String): List<LetterState> }` returning five of `CORRECT`/`PRESENT`/`ABSENT`. Algorithm: first pass marks exact matches `CORRECT` and counts the unmatched answer letters; second pass marks each remaining guess letter `PRESENT` while its count is above zero (decrementing), else `ABSENT`. No `android.*` or `androidx.*` imports.
  - Create `app/src/test/java/com/jodysalt/wordclone/game/ScorerTest.kt` (JUnit 4) covering an all-correct guess, an all-absent guess, a mixed guess, answer `abbey` guess `babes` = `[PRESENT, PRESENT, CORRECT, CORRECT, ABSENT]`, and answer `crane` guess `eerie` = `[ABSENT, ABSENT, PRESENT, ABSENT, CORRECT]`.
  - Run `./gradlew testDebugUnitTest --tests 'com.jodysalt.wordclone.game.ScorerTest'` and confirm it passes.

## Add pure-Kotlin `WordList` in `app/src/main/java/com/jodysalt/wordclone/game/WordList.kt` and a `WordListTest` that checks the bundled asset files
- **category:** functional
- **status:** done
- **steps:**
  - Create `data class WordList(val answers: List<String>, val validGuesses: Set<String>)` with `companion object { fun parse(text: String): List<String> }` that trims each line, drops blank lines and lowercases, and `fun fromText(answersText: String, validText: String): WordList`. No `android.*` or `androidx.*` imports.
  - Create `app/src/test/java/com/jodysalt/wordclone/game/WordListTest.kt` that reads `src/main/assets/answers.txt` and `src/main/assets/valid_guesses.txt` via `java.io.File` (the Gradle test working directory is the `app` module dir) and asserts: both lists non-empty; every entry matches `^[a-z]{5}$`; no duplicates in either list; every answer is in the valid-guess list. Also assert `parse` drops blank lines and trims whitespace.
  - Run `./gradlew testDebugUnitTest --tests 'com.jodysalt.wordclone.game.WordListTest'` and confirm it passes.

## Add immutable `Game` state with letter input, guess validation, win and loss in `app/src/main/java/com/jodysalt/wordclone/game/Game.kt`, covered by `GameTest`
- **category:** functional
- **status:** done
- **steps:**
  - Create `enum class GameStatus { IN_PROGRESS, WON, LOST }`, `enum class GuessError { TOO_SHORT, NOT_A_WORD }`, and `data class Game(val answer: String, val guesses: List<String> = emptyList(), val currentInput: String = "", val status: GameStatus = GameStatus.IN_PROGRESS, val error: GuessError? = null)` with constants `WORD_LENGTH = 5`, `MAX_GUESSES = 6` and pure functions returning a new `Game`: `typeLetter(c: Char)` (appends a lowercase a-z letter while input is shorter than five and the game is in progress, clears `error`), `deleteLetter()`, and `submit(validGuesses: Set<String>)`: input shorter than five sets `error = TOO_SHORT`, a word not in `validGuesses` sets `error = NOT_A_WORD`, both leaving `guesses` unchanged; otherwise appends the guess, clears input, sets `WON` if it equals `answer`, `LOST` after the sixth wrong guess. Any call after `WON` or `LOST` returns the game unchanged. Also `fun scoredGuesses(): List<List<LetterState>>` using `Scorer.score`. No `android.*` or `androidx.*` imports.
  - Create `app/src/test/java/com/jodysalt/wordclone/game/GameTest.kt` covering: too-short guess rejected with `TOO_SHORT` and guess count unchanged; unknown word rejected with `NOT_A_WORD` and guess count unchanged; correct guess gives `WON` and a further `typeLetter`/`submit` changes nothing; six wrong valid guesses give `LOST` with `answer` still readable.
  - Run `./gradlew testDebugUnitTest --tests 'com.jodysalt.wordclone.game.GameTest'` and confirm it passes.

## Add keyboard letter state (best known state wins) in `app/src/main/java/com/jodysalt/wordclone/game/KeyboardState.kt`, covered by `KeyboardStateTest`
- **category:** functional
- **status:** done
- **steps:**
  - Add `fun Game.keyboardState(): Map<Char, LetterState>` that folds every scored guess so each letter keeps its best state (`CORRECT` over `PRESENT` over `ABSENT` over `UNKNOWN`); letters never guessed are absent from the map (callers default to `UNKNOWN`). No `android.*` or `androidx.*` imports.
  - Create `app/src/test/java/com/jodysalt/wordclone/game/KeyboardStateTest.kt` covering: a letter `PRESENT` in one guess then `CORRECT` in a later one stays `CORRECT`; a letter `CORRECT` then scored `ABSENT` as a repeated extra copy stays `CORRECT`; a letter `PRESENT` then `ABSENT` stays `PRESENT`.
  - Run `./gradlew testDebugUnitTest --tests 'com.jodysalt.wordclone.game.KeyboardStateTest'` and confirm it passes.

## Add `Game.newGame(answers, random)` picking a random answer with an injectable `kotlin.random.Random`, covered by `NewGameTest`
- **category:** functional
- **status:** done
- **steps:**
  - Add `companion object { fun newGame(answers: List<String>, random: Random = Random.Default): Game }` to `Game` in `app/src/main/java/com/jodysalt/wordclone/game/Game.kt`, returning a fresh `Game(answer = answers.random(random))` with no guesses, empty input, `IN_PROGRESS` and no error.
  - Create `app/src/test/java/com/jodysalt/wordclone/game/NewGameTest.kt` that plays a game to `WON` or `LOST`, calls `Game.newGame(answers, Random(42))`, and asserts guesses empty, input empty, status `IN_PROGRESS`, error null, the answer is in `answers`, and two calls with `Random(42)` give the same answer.
  - Run `./gradlew testDebugUnitTest --tests 'com.jodysalt.wordclone.game.NewGameTest'` and confirm it passes.
  - Run `grep -rnE '^import (android|androidx)\.' app/src/main/java/com/jodysalt/wordclone/game/` and confirm it prints nothing.

## Add `GameViewModel` in `app/src/main/java/com/jodysalt/wordclone/ui/GameViewModel.kt` that loads the asset word lists once and drives the pure `Game`
- **category:** functional
- **status:** done
- **steps:**
  - Create `class GameViewModel(application: Application) : AndroidViewModel(application)` that reads `answers.txt` and `valid_guesses.txt` via `application.assets.open(...)` into `WordList.fromText(...)` once, holds `Game.newGame(wordList.answers)` in a Compose `mutableStateOf` (or `StateFlow`), and exposes `onLetter(c: Char)`, `onBackspace()`, `onEnter()` (calls `submit(wordList.validGuesses)`) and `onNewGame()`. Rotation survival comes from the ViewModel; no persistence across process death.
  - Run `./gradlew compileDebugKotlin` and confirm it succeeds.

## Build the Compose game screen (6x5 grid, QWERTY keyboard, messages, new-game button) in `app/src/main/java/com/jodysalt/wordclone/ui/` and wire it into `MainActivity`
- **category:** functional
- **status:** done
- **steps:**
  - Create `ui/Grid.kt`: a 6-row by 5-column grid of square tiles showing submitted guesses coloured by `Scorer` result (green `CORRECT`, yellow `PRESENT`, grey `ABSENT`), the current input in the next row uncoloured with an outline, and empty tiles below. Create `ui/Keyboard.kt`: three QWERTY rows (`qwertyuiop`, `asdfghjkl`, `Enter zxcvbnm Backspace`), each letter key coloured by `game.keyboardState()` with the same palette (default Material surface for `UNKNOWN`). Create `ui/GameScreen.kt` combining them with: a short message for `GuessError.TOO_SHORT` ("Not enough letters") and `GuessError.NOT_A_WORD` ("Not in word list"); on `WON` "You won! The word was X" and on `LOST` "The word was X" (uppercase answer); and a "New game" button always visible and enabled. No animations, Material default theme.
  - Replace the placeholder in `app/src/main/java/com/jodysalt/wordclone/MainActivity.kt` with `GameScreen(viewModel())` inside `MaterialTheme`.
  - Run `./gradlew assembleDebug` and confirm `app/build/outputs/apk/debug/app-debug.apk` exists.

## Verify the debug APK requests no `INTERNET` permission and, when an emulator is attached, installs and launches without a crash
- **category:** non-functional
- **status:** done
- **steps:**
  - After `./gradlew assembleDebug`, run `grep -r "android.permission.INTERNET" app/build/intermediates/merged_manifest/debug/` and confirm it prints nothing; if a dependency adds the permission, remove it with `tools:node="remove"` in `app/src/main/AndroidManifest.xml` and rebuild.
  - Only if `adb devices` lists a device (start the `Medium_Phone` AVD with `$ANDROID_HOME/emulator/emulator -avd Medium_Phone -no-window &` if possible, otherwise skip this step and say so in the commit): `adb install -r app/build/outputs/apk/debug/app-debug.apk`, `adb logcat -c`, `adb shell am start -n com.jodysalt.wordclone/.MainActivity`, wait a few seconds, and confirm `adb logcat -d | grep -E "FATAL EXCEPTION|AndroidRuntime: .*com.jodysalt.wordclone"` prints nothing.

## Run full test suite for playable-wordle-game
- **category:** chore
- **status:** done
- **steps:**
  - Run the full test suite (`./gradlew testDebugUnitTest`) and confirm every suite passes.
  - Run `./gradlew clean assembleDebug` and confirm `app/build/outputs/apk/debug/app-debug.apk` is produced.
