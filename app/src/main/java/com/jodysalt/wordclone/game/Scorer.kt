package com.jodysalt.wordclone.game

object Scorer {
    /** Scores [guess] against [answer] with Wordle's repeated-letter rules. */
    fun score(guess: String, answer: String): List<LetterState> {
        require(guess.length == answer.length) { "guess and answer must have the same length" }
        val result = MutableList(guess.length) { LetterState.ABSENT }
        val remaining = mutableMapOf<Char, Int>()
        for (i in guess.indices) {
            if (guess[i] == answer[i]) {
                result[i] = LetterState.CORRECT
            } else {
                remaining[answer[i]] = (remaining[answer[i]] ?: 0) + 1
            }
        }
        for (i in guess.indices) {
            if (result[i] == LetterState.CORRECT) continue
            val count = remaining[guess[i]] ?: 0
            if (count > 0) {
                result[i] = LetterState.PRESENT
                remaining[guess[i]] = count - 1
            }
        }
        return result
    }
}
