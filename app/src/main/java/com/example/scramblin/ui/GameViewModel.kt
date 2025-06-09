package com.example.scramblin.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private val wordList = listOf(
        "banana",
        "orange",
        "apple",
        "grape",
        "melon",
        "lemon",
        "strawberry",
        "coconut",
        "pineapple",
        "pear"
        )
    private val usedWords = mutableSetOf<String>()
    private lateinit var currentWord: String

    var userGuess by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        reset()
    }

    fun updateUserGuess(guess: String) {
        userGuess = guess
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val nextCount = _uiState.value.wordCount + 1
            if (nextCount == MAX_NO_OF_WORDS) {
                _uiState.update {
                    it.copy(
                        score = it.score + SCORE_INCREASE,
                        wordCount = nextCount,
                        isGuessedWordWrong = false,
                        isGameOver = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        score = it.score + SCORE_INCREASE,
                        wordCount = nextCount,
                        isGuessedWordWrong = false
                    )
                }
                nextWord()
            }
        } else {
            _uiState.update {
                it.copy(isGuessedWordWrong = true)
            }
        }
        userGuess = ""
    }

    fun skipWord() {
        val nextCount = _uiState.value.wordCount + 1
        if (nextCount == MAX_NO_OF_WORDS) {
            _uiState.update {
                it.copy(
                    wordCount = nextCount,
                    isGuessedWordWrong = false,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    wordCount = nextCount,
                    isGuessedWordWrong = false
                )
            }
            nextWord()
        }
        userGuess = ""
    }

    fun reset() {
        usedWords.clear()
        userGuess = ""
        _uiState.value = GameUiState(
            currentScrambledWord = "",
            isGuessedWordWrong = false,
            score = 0,
            wordCount = 0,
            isGameOver = false
        )
        nextWord()
    }

    private fun nextWord() {
        if (usedWords.size == wordList.size) return
        currentWord = wordList.filterNot { usedWords.contains(it) }.random()
        usedWords.add(currentWord)
        val scrambled = currentWord.toCharArray().apply { shuffle() }.concatToString()
        _uiState.update { it.copy(currentScrambledWord = scrambled) }
    }

    companion object {
        private const val SCORE_INCREASE = 10
        private const val MAX_NO_OF_WORDS = 10
    }
}

