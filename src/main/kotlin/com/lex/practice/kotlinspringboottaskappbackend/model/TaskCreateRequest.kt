package com.lex.practice.kotlinspringboottaskappbackend.model

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

/**
 * @author : Lex Yu
 */
data class TaskCreateRequest(

    @NotBlank(message = "Task description can't be empty")
    val description: String,

    val isReminderSet: Boolean,

    val isTaskOpen: Boolean,

    val createdOn: LocalDateTime,

    val priority: Priority
)
