package com.jodysalt.wordclone.game

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordListTest {
    private val wordPattern = Regex("^[a-z]{5}$")

    // Read raw lines (only trailing newline dropped) so the checks see the files as committed.
    private fun readAsset(name: String): List<String> =
        File("src/main/assets/$name").readLines().filter { it.isNotEmpty() }

    private val answers = readAsset("answers.txt")
    private val validGuesses = readAsset("valid_guesses.txt")

    @Test
    fun bothListsAreNonEmpty() {
        assertTrue(answers.isNotEmpty())
        assertTrue(validGuesses.isNotEmpty())
    }

    @Test
    fun everyEntryIsFiveLowercaseLetters() {
        assertEquals(emptyList<String>(), answers.filterNot { wordPattern.matches(it) })
        assertEquals(emptyList<String>(), validGuesses.filterNot { wordPattern.matches(it) })
    }

    @Test
    fun listsHaveNoDuplicates() {
        assertEquals(answers.size, answers.toSet().size)
        assertEquals(validGuesses.size, validGuesses.toSet().size)
    }

    @Test
    fun everyAnswerIsAValidGuess() {
        val valid = validGuesses.toSet()
        assertEquals(emptyList<String>(), answers.filterNot { it in valid })
    }

    @Test
    fun parseDropsBlankLinesAndTrimsWhitespace() {
        assertEquals(listOf("crane", "abbey"), WordList.parse("  crane \n\n   \nABBEY\t\n"))
    }

    @Test
    fun fromTextBuildsAnswersAndValidGuesses() {
        val list = WordList.fromText("crane\n", "crane\nabbey\n")
        assertEquals(listOf("crane"), list.answers)
        assertEquals(setOf("crane", "abbey"), list.validGuesses)
    }
}
