package com.jodysalt.wordclone.game

/** Best known [LetterState] per guessed letter; letters never guessed are absent from the map. */
fun Game.keyboardState(): Map<Char, LetterState> {
    val states = mutableMapOf<Char, LetterState>()
    guesses.zip(scoredGuesses()).forEach { (guess, scores) ->
        guess.toList().zip(scores).forEach { (letter, state) ->
            states[letter] = maxOf(states[letter] ?: LetterState.UNKNOWN, state)
        }
    }
    return states
}
