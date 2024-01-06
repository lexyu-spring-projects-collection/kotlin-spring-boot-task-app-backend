package com.lex.practice.kotlinspringboottaskappbackend.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lex.practice.kotlinspringboottaskappbackend.exception.TaskNotFoundException
import com.lex.practice.kotlinspringboottaskappbackend.model.Priority
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskCreateRequest
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskDTO
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskUpdateRequest
import com.lex.practice.kotlinspringboottaskappbackend.service.TaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime

/**
 * @author : LEX_YU
 */
@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [TaskController::class])
class TaskControllerTest(
    @Autowired
    private val mockMvc: MockMvc
) {

    @MockBean
    private lateinit var mockService: TaskService

    private val taskId: Long = 777
    private val dummyTaskDTO = TaskDTO(
        taskId, "For Integration Test",
        isReminderSet = true, isTaskOpen = true,
        createdOn = LocalDateTime.now(), priority = Priority.LOW
    )

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun `given all tasks endpoint is called then check for number of tasks`() {
        val taskDTO = TaskDTO(
            111,
            "For Test DTO",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.HIGH
        )
        val tasks = listOf(dummyTaskDTO, taskDTO)

        `when`(mockService.getAllTasks()).thenReturn(tasks)

        val response: ResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/task/list"))

        response
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(tasks.size))
    }

    @Test
    fun `when task id does not exist then except is not found response`() {
        `when`(mockService.getTaskById(taskId)).thenThrow(TaskNotFoundException("Task $taskId Not Found!"))

        val response = mockMvc.perform(MockMvcRequestBuilders.get("/task/$taskId"))

        response.andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `when get task by id is called with an character in the url then expect a bad request message`() {
        val resp = mockMvc.perform(MockMvcRequestBuilders.get("/task/1111L"))

        resp.andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `given all opens tasks endpoint is called then check for number of tasks`() {
        val taskDTO = TaskDTO(
            1111,
            "For Test DTO",
            isReminderSet = false,
            isTaskOpen = true,
            createdOn = LocalDateTime.now(),
            priority = Priority.HIGH
        )
        val tasks = listOf(dummyTaskDTO, taskDTO)

        `when`(mockService.getOpenTasks()).thenReturn(tasks)
        val resp = mockMvc.perform(MockMvcRequestBuilders.get("/task/open-list"))


        resp.andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(tasks.size))
    }

    @Test
    fun `given one task when get task by id is called then check for correct description`() {
        `when`(mockService.getTaskById(dummyTaskDTO.id)).thenReturn(dummyTaskDTO)
        val resp = mockMvc.perform(MockMvcRequestBuilders.get("/task/${dummyTaskDTO.id}"))

        resp.andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dummyTaskDTO.id))
            .andExpect(jsonPath("$.description").value(dummyTaskDTO.description))
            .andExpect(jsonPath("$.isReminderSet").value(dummyTaskDTO.isReminderSet))
            .andExpect(jsonPath("$.isTaskOpen").value(dummyTaskDTO.isTaskOpen))
    }

    @Test
    fun `given all closed tasks endpoint is called then check for number of tasks`() {
        val taskDTO = TaskDTO(
            1111,
            "For Test DTO",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.HIGH
        )
        val tasks = listOf(dummyTaskDTO, taskDTO)

        `when`(mockService.getClosedTasks()).thenReturn(tasks)
        val resp = mockMvc.perform(MockMvcRequestBuilders.get("/task/closed-list"))


        resp.andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.size()").value(tasks.size))
    }


    @Test
    fun `given update task when task is updated then check for correct properties`() {
        val request = TaskUpdateRequest(
            dummyTaskDTO.description,
            dummyTaskDTO.isReminderSet,
            dummyTaskDTO.isTaskOpen,
            dummyTaskDTO.priority
        )

        `when`(mockService.updateTask(dummyTaskDTO.id, request)).thenReturn(dummyTaskDTO)
        val resultActions: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.patch("/task/update/${dummyTaskDTO.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        resultActions.andExpect(MockMvcResultMatchers.status().isOk)
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        resultActions.andExpect(jsonPath("$.description").value(dummyTaskDTO.description))
        resultActions.andExpect(jsonPath("$.isReminderSet").value(dummyTaskDTO.isReminderSet))
        resultActions.andExpect(jsonPath("$.isTaskOpen").value(dummyTaskDTO.isTaskOpen))
    }

    @Test
    fun `given create task request when task is created then check for the properties`() {
        val request = TaskCreateRequest(
            dummyTaskDTO.description,
            dummyTaskDTO.isReminderSet,
            dummyTaskDTO.isTaskOpen,
            LocalDateTime.now(),
            dummyTaskDTO.priority,
        )

        `when`(mockService.createTask(request)).thenReturn(dummyTaskDTO)
        val resp: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/task/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        resp.andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.description").value(dummyTaskDTO.description))
            .andExpect(jsonPath("$.isReminderSet").value(dummyTaskDTO.isReminderSet))
            .andExpect(jsonPath("$.isTaskOpen").value(dummyTaskDTO.isTaskOpen))
    }

    @Test
    fun `given id for delete request when task is deleted then check for the response message`() {
        val expectedMessage = "Task $taskId has been deleted."

        `when`(mockService.deleteTask(taskId)).thenReturn(expectedMessage)
        val resp: ResultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/task/delete/$taskId")
        )

        resp.andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(content().string(expectedMessage))
    }

}