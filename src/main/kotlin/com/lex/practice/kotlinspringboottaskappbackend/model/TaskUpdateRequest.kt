package com.lex.practice.kotlinspringboottaskappbackend.model

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

/**
 * @author : Lex Yu
 */
data class TaskUpdateRequest(
    val description: String?,
    val isReminderSet: Boolean?,
    val isTaskOpen: Boolean?,
    val priority: Priority?
)
