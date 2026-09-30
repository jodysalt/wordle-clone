package com.jodysalt.wordclone.game

data class WordList(val answers: List<String>, val validGuesses: Set<String>) {
    companion object {
        fun parse(text: String): List<String> =
            text.lineSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { it.lowercase() }
                .toList()

        fun fromText(answersText: String, validText: String): WordList =
            WordList(answers = parse(answersText), validGuesses = parse(validText).toSet())
    }
}
