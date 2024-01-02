package com.lex.practice.kotlinspringboottaskappbackend

import com.lex.practice.kotlinspringboottaskappbackend.entity.Task
import com.lex.practice.kotlinspringboottaskappbackend.model.Priority
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringBootTaskAppBackendApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringBootTaskAppBackendApplication>(*args)
    println("Hello World!")
}
