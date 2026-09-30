package com.jodysalt.wordclone.game

/** Declared in rising order of knowledge, so ordinal comparison picks the best known state. */
enum class LetterState { UNKNOWN, ABSENT, PRESENT, CORRECT }
