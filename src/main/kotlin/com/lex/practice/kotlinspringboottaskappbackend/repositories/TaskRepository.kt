package com.lex.practice.kotlinspringboottaskappbackend.repositories

import com.lex.practice.kotlinspringboottaskappbackend.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * @author : Lex Yu
 */
@Repository
interface TaskRepository : JpaRepository<Task, Long> {

    fun findTaskById(id: Long): Task

    @Query(value = "SELECT * FROM tasks WHERE is_task_open = TRUE", nativeQuery = true)
    fun queryAllByOpenTask(): List<Task>

    @Query(value = "SELECT * FROM tasks WHERE is_task_open = FALSE", nativeQuery = true)
    fun queryAllByClosedTask(): List<Task>

    @Query(value = """
        SELECT
            CASE 
                WHEN COUNT(t) > 0
                THEN TRUE ELSE FALSE 
            END 
        FROM tasks AS t 
        WHERE t.description = ?1 
         """, nativeQuery = true)
    fun isDescriptionExist(description: String): Boolean
}