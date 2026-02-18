
package com.example.patternlock

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest

enum class PatternMode { CREATE, CONFIRM, UNLOCK }

class PatternViewModel : ViewModel() {

    var mode by mutableStateOf(PatternMode.CREATE)
        private set

    var message by mutableStateOf("Создайте графический ключ")
        private set

    var isLocked by mutableStateOf(false)
        private set

    private var firstPattern: List<Int>? = null
    private var savedHash: String? = null
    private var failedAttempts = 0
    private var lockLevel = 0

    fun onPatternEntered(pattern: List<Int>) {
        if (isLocked) return

        when (mode) {

            PatternMode.CREATE -> {
                firstPattern = pattern
                mode = PatternMode.CONFIRM
                message = "Подтвердите ключ"
            }

            PatternMode.CONFIRM -> {
                if (pattern == firstPattern) {
                    savedHash = hash(pattern)
                    mode = PatternMode.UNLOCK
                    message = "Ключ сохранён"
                } else {
                    mode = PatternMode.CREATE
                    message = "Ключи не совпадают"
                }
            }

            PatternMode.UNLOCK -> {
                if (hash(pattern) == savedHash) {
                    message = "Разблокировано"
                    failedAttempts = 0
                } else {
                    failedAttempts++
                    if (failedAttempts >= 5) lock()
                    else message = "Неверный ключ ($failedAttempts/5)"
                }
            }
        }
    }

    private fun lock() {
        isLocked = true
        lockLevel++
        val delayTime = 5000L * lockLevel
        message = "Блокировка на ${delayTime / 1000} сек."

        viewModelScope.launch {
            delay(delayTime)
            isLocked = false
            failedAttempts = 0
            message = "Введите ключ"
        }
    }

    private fun hash(pattern: List<Int>): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(pattern.joinToString("-").toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
