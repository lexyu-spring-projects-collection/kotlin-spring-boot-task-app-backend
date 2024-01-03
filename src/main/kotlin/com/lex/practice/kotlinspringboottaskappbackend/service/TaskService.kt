package com.lex.practice.kotlinspringboottaskappbackend.service

import com.lex.practice.kotlinspringboottaskappbackend.entity.Task
import com.lex.practice.kotlinspringboottaskappbackend.exception.BadRequestException
import com.lex.practice.kotlinspringboottaskappbackend.exception.TaskNotFoundException
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskCreateRequest
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskDTO
import com.lex.practice.kotlinspringboottaskappbackend.model.TaskUpdateRequest
import com.lex.practice.kotlinspringboottaskappbackend.repositories.TaskRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties

/**
 * @author : Lex Yu
 */
@Service
class TaskService(private val taskRepository: TaskRepository) {

    /*
    private fun mappingEntityToDTO(task: Task): TaskDTO {
        return TaskDTO(
            task.id,
            task.description,
            task.isReminderSet,
            task.isTaskOpen,
            task.createdOn,
            task.priority
        )
    }
     */

    private fun mappingEntityToDTO(task: Task) = TaskDTO(
        task.id,
        task.description,
        task.isReminderSet,
        task.isTaskOpen,
        task.createdOn,
        task.priority
    )

    private fun mappingRequestToEntity(task: Task, taskCreateRequest: TaskCreateRequest) {
        task.description = taskCreateRequest.description
        task.isReminderSet = taskCreateRequest.isReminderSet
        task.isTaskOpen = taskCreateRequest.isTaskOpen
        task.priority = taskCreateRequest.priority
    }

    private fun checkTaskForId(id: Long) {
        if (!taskRepository.existsById(id)) {
            throw TaskNotFoundException("Task $id Not Found!")
        }
    }

    fun getTaskById(id: Long): TaskDTO {
        checkTaskForId(id)
        val task = taskRepository.findTaskById(id)
        return mappingEntityToDTO(task)
    }

    fun getAllTasks(): List<TaskDTO> =
        taskRepository.findAll().stream().map(this::mappingEntityToDTO).toList()

    fun getOpenTasks(): List<TaskDTO> =
        taskRepository.queryAllByOpenTask().stream().map(this::mappingEntityToDTO).toList()

    fun getClosedTasks(): List<TaskDTO> =
        taskRepository.queryAllByClosedTask().stream().map(this::mappingEntityToDTO).toList()

    fun createTask(request: TaskCreateRequest): TaskDTO {
        if (taskRepository.isDescriptionExist(request.description)) {
            throw BadRequestException("There is already aa task with the description: ${request.description}")
        }
        val task = Task()
        mappingRequestToEntity(task, request)
        val savedTask = taskRepository.save(task)

        return mappingEntityToDTO(savedTask)
    }

    fun updateTask(id: Long, request: TaskUpdateRequest): TaskDTO {
        checkTaskForId(id)
        val existingTask = taskRepository.findTaskById(id)

        for (prop in TaskUpdateRequest::class.memberProperties) {
            if (prop.get(request) != null) {
                val field: Field? = ReflectionUtils.findField(Task::class.java, prop.name)
                field?.let {
                    it.isAccessible = true
                    ReflectionUtils.setField(it, existingTask, prop.get(request))
                }
            }
        }
        val savedTask = taskRepository.save(existingTask)

        return mappingEntityToDTO(savedTask)
    }

    fun deleteTask(id: Long): String {
        checkTaskForId(id)
        taskRepository.deleteById(id)
        return "Task $id has been deleted."
    }

}