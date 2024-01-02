package com.lex.practice.kotlinspringboottaskappbackend.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author : Lex Yu
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
data class BadRequestException(override val message: String) : RuntimeException()
