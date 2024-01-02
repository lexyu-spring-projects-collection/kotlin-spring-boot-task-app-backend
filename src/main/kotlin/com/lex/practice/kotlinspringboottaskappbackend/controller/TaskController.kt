package com.lex.practice.kotlinspringboottaskappbackend.controller

import com.lex.practice.kotlinspringboottaskappbackend.model.TaskCreateRequest
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskDTO
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskUpdateRequest
import com.lex.practice.kotlinspringboottaskappbackend.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author : Lex Yu
 */
@RestController
@RequestMapping("/task")
class TaskController(private val taskService: TaskService) {

    @GetMapping("/list")
    fun getAllTasks(): ResponseEntity<List<TaskDTO>> =
        ResponseEntity(taskService.getAllTasks(), HttpStatus.OK)

    @GetMapping("/open-list")
    fun getOpenTasks(): ResponseEntity<List<TaskDTO>> =
        ResponseEntity(taskService.getOpenTasks(), HttpStatus.OK)

    @GetMapping("/closed-list")
    fun getClosedTasks(): ResponseEntity<List<TaskDTO>> =
        ResponseEntity(taskService.getClosedTasks(), HttpStatus.OK)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable(name = "id") id: Long): ResponseEntity<TaskDTO> =
        ResponseEntity(taskService.getTaskById(id), HttpStatus.OK)

    @PostMapping("/create")
    fun createTask(@Valid @RequestBody request: TaskCreateRequest): ResponseEntity<TaskDTO> =
        ResponseEntity(taskService.createTask(request), HttpStatus.CREATED)

    @PatchMapping("/update/{id}")
    fun updateTask(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody request: TaskUpdateRequest
    ): ResponseEntity<TaskDTO> = ResponseEntity(taskService.updateTask(id, request), HttpStatus.OK)

    @DeleteMapping("/delete/{id}")
    fun updateTask(
        @PathVariable(name = "id") id: Long
    ): ResponseEntity<String> = ResponseEntity(taskService.deleteTask(id), HttpStatus.OK)
}
