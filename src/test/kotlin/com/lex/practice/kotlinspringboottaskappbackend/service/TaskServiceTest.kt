package com.lex.practice.kotlinspringboottaskappbackend.service

import com.lex.practice.kotlinspringboottaskappbackend.entity.Task
import com.lex.practice.kotlinspringboottaskappbackend.exception.BadRequestException
import com.lex.practice.kotlinspringboottaskappbackend.model.Priority
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskCreateRequest
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskDTO
import com.lex.practice.kotlinspringboottaskappbackend.repositories.TaskRepository
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import mu.two.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

/**
 * @author : Lex Yu
 */
@ExtendWith(MockKExtension::class)
class TaskServiceTest {

    @RelaxedMockK
    private lateinit var mockRepository: TaskRepository

    @InjectMockKs
    private lateinit var objectUnderTest: TaskService

    private val task = Task()
    private lateinit var createRequest: TaskCreateRequest
    private val taskId: Long = 123

    private val logger = KotlinLogging.logger { }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        createRequest = TaskCreateRequest(
            "test",
            isReminderSet = false,
            isTaskOpen = false,
            createdOn = LocalDateTime.now(),
            priority = Priority.LOW,
        )
    }

    @Test
    fun `when all tasks get fetched then check if then given size is correct`() {

        val expectedTask = listOf(Task(), Task())

        // Mockito.when().thenReturn()
        every { mockRepository.findAll() } returns expectedTask.toMutableList()

        val actualTasks: List<TaskDTO> = objectUnderTest.getAllTasks();

        assertThat(actualTasks.size).isEqualTo(expectedTask.size)
    }

    @Test
    fun `when task is created then check for the task properties`() {
        // Given
        task.description = createRequest.description
        task.isTaskOpen = createRequest.isTaskOpen
        task.priority = createRequest.priority

        // When
        every { mockRepository.save(any()) } returns task
        val actualTaskDTO: TaskDTO = objectUnderTest.createTask(createRequest)

        // Then
        assertThat(actualTaskDTO.description).isEqualTo(task.description)
        assertThat(actualTaskDTO.isTaskOpen).isEqualTo(task.isTaskOpen)
        assertThat(actualTaskDTO.priority).isEqualTo(task.priority)
    }

    @Test
    fun `when task description already exist then check for bad request exception`() {
        every { mockRepository.isDescriptionExist(any()) } returns true
        val exception = assertThrows<BadRequestException> { objectUnderTest.createTask(createRequest) }

        logger.info("ex : ${exception.message}")
        logger.info("desc : ${createRequest.description}")

        assertThat(exception.message)
            .isEqualTo("There is already aa task with the description: ${createRequest.description}")
        verify { mockRepository.save(any()) wasNot called }
    }

    @Test
    fun `when get task by id is called then throw a task not found exception`() {

    }

    @Test
    fun `when2`() {

    }

    @Test
    fun `when3`() {

    }

    @Test
    fun `when4`() {

    }

}