package com.lex.practice.kotlinspringboottaskappbackend.service

import com.lex.practice.kotlinspringboottaskappbackend.entity.Task
import com.lex.practice.kotlinspringboottaskappbackend.exception.BadRequestException
import com.lex.practice.kotlinspringboottaskappbackend.exception.TaskNotFoundException
import com.lex.practice.kotlinspringboottaskappbackend.model.Priority
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskCreateRequest
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskDTO
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskUpdateRequest
import com.lex.practice.kotlinspringboottaskappbackend.repositories.TaskRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import mu.two.KotlinLogging
import org.apache.coyote.http11.Constants.a
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception
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
        every { mockRepository.existsById(any()) } returns false
        val exception: TaskNotFoundException =
            assertThrows<TaskNotFoundException> { objectUnderTest.getTaskById(taskId) }
        assertThat(exception.message).isEqualTo("Task $taskId Not Found!")
    }

    @Test
    fun `when all open tasks are fetched check the property is task open is true`() {
        task.isTaskOpen = true
        val expectedTask = listOf(task)

        every { mockRepository.queryAllByOpenTask() } returns expectedTask.toMutableList()

        val actualList: List<TaskDTO> = objectUnderTest.getOpenTasks()

        assertThat(actualList[0].isTaskOpen).isEqualTo(expectedTask[0].isTaskOpen)
    }

    @Test
    fun `when all open tasks are fetched check the property is task open is false`() {
        task.isTaskOpen = false
        val expectedTask = listOf(task)

        every { mockRepository.queryAllByClosedTask() } returns expectedTask.toMutableList()

        val actualList: List<TaskDTO> = objectUnderTest.getClosedTasks()

        assertThat(actualList[0].isTaskOpen).isEqualTo(expectedTask[0].isTaskOpen)
    }

    @Test
    fun `when save task is called then check if argument could be captured`() {
        val taskSlot: CapturingSlot<Task> = slot<Task>()
        task.description = createRequest.description
        task.isTaskOpen = createRequest.isTaskOpen
        task.isReminderSet = createRequest.isReminderSet
        task.priority = createRequest.priority

        every { mockRepository.save(capture(taskSlot)) } returns task
        val actualTaskDTO: TaskDTO = objectUnderTest.createTask(createRequest)

        verify { mockRepository.save(capture(taskSlot)) }

        assertThat(taskSlot.captured.description).isEqualTo(actualTaskDTO.description)
        assertThat(taskSlot.captured.isTaskOpen).isEqualTo(actualTaskDTO.isTaskOpen)
        assertThat(taskSlot.captured.isReminderSet).isEqualTo(actualTaskDTO.isReminderSet)
        assertThat(taskSlot.captured.priority).isEqualTo(actualTaskDTO.priority)

    }

    @Test
    fun `when get task by id is called then check for a specific description`(){
        task.description = "Buy Food"

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task

        val actualTaskDTO: TaskDTO =  objectUnderTest.getTaskById(taskId)
        assertThat(actualTaskDTO.description).isEqualTo(task.description)
    }
    @Test
    fun `when get task by id is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(capture(taskIdSlot)) } returns task
        objectUnderTest.getTaskById(taskId)

        verify { mockRepository.findTaskById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(taskId)
    }
    @Test
    fun `when delete task is called then check the response message`() {
        every { mockRepository.existsById(any()) } returns true

        val actualMessage =  objectUnderTest.deleteTask(taskId)

        assertThat(actualMessage).isEqualTo("Task $taskId has been deleted.")
    }
    @Test
    fun `when delete task is called then check if argument could be captured`() {
        val taskIdSlot = slot<Long>()

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.deleteById(capture(taskIdSlot)) } returns Unit
        objectUnderTest.deleteTask(taskId)

        verify { mockRepository.deleteById(capture(taskIdSlot)) }
        assertThat(taskIdSlot.captured).isEqualTo(taskId)
    }

    @Test
    fun `when update task is called then check for the request properties`() {
        task.description = "Got to restaurant"
        task.isReminderSet = false
        task.isTaskOpen = true
        task.priority = Priority.HIGH

        val req = TaskUpdateRequest(
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.priority
        )

        every { mockRepository.existsById(any()) } returns true
        every { mockRepository.findTaskById(any()) } returns task
        every { mockRepository.save(any()) } returns task
        val actualTaskDTO = objectUnderTest.updateTask(taskId, req)

        assertThat(actualTaskDTO.id).isEqualTo(task.id)
        assertThat(actualTaskDTO.description).isEqualTo(task.description)
        assertThat(actualTaskDTO.isTaskOpen).isEqualTo(task.isTaskOpen)
        assertThat(actualTaskDTO.isReminderSet).isEqualTo(task.isReminderSet)
        assertThat(actualTaskDTO.createdOn).isEqualTo(task.createdOn)
        assertThat(actualTaskDTO.priority).isEqualTo(task.priority)
    }
}