package com.lex.practice.kotlinspringboottaskappbackend.repositories

import com.lex.practice.kotlinspringboottaskappbackend.entity.Task
import mu.two.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql

/**
 * @author : Lex Yu
 */
@DataJpaTest(properties = ["spring.jpa.properties.javax.persistence.validation.mode=none"])
class TaskRepositoryTest {

    @Autowired
    private lateinit var objectUnderTest: TaskRepository

    private val numberOfRecordsInTestDataSQL = 3
    private val numberOfOpenRecordsInTestDataSQL = 1
    private val numberOfClosedRecordsInTestDataSQL = 2

    private val logger = KotlinLogging.logger { }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task is saved then check for not null`() {
        // Arrange & Act
        val task: Task = objectUnderTest.findTaskById(111)

        // Assert
        logger.info("$task")
        assertThat(task).isNotNull
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when all tasks are fetched then check for the number of records`() {
        val tasks: List<Task> = objectUnderTest.findAll()

        logger.info("$tasks")

        assertThat(tasks).size().isEqualTo(numberOfRecordsInTestDataSQL)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when task is deleted then check for the size of records`() {
        objectUnderTest.deleteById(113)

        val tasks = objectUnderTest.findAll()
        logger.info("$tasks")

        assertThat(tasks).size().isEqualTo(numberOfRecordsInTestDataSQL - 1)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when all open tasks are queried then check for the correct number of open tasks`() {
        val openTasks = objectUnderTest.queryAllByOpenTask()

        logger.info("$openTasks, size = ${openTasks.size}")

        assertThat(openTasks).size().isEqualTo(numberOfOpenRecordsInTestDataSQL)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when all closed tasks are queried then check for the correct number of closed tasks`() {
        val closedTasks = objectUnderTest.queryAllByClosedTask()

        logger.info("$closedTasks, size = ${closedTasks.size}")

        assertThat(closedTasks).size().isEqualTo(numberOfClosedRecordsInTestDataSQL)
    }

    @Test
    @Sql("classpath:test-data.sql")
    fun `when description is queried then check if description already exists`() {
        val isDescExist1 = objectUnderTest.isDescriptionExist("second test todo")
        val isDescExist2 = objectUnderTest.isDescriptionExist("tttttttttttttttt")

        logger.info("$isDescExist1")
        logger.info("$isDescExist2")

        assertThat(isDescExist1).isTrue()
        assertThat(isDescExist2).isFalse()
    }
}