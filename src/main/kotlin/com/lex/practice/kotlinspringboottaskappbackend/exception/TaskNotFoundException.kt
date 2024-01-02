package com.lex.practice.kotlinspringboottaskappbackend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author : Lex Yu
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
data class TaskNotFoundException(override val message: String) : RuntimeException()
