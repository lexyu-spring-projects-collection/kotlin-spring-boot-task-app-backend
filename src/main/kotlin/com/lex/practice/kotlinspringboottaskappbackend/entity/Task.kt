package com.lex.practice.kotlinspringboottaskappbackend.entity

import com.lex.practice.kotlinspringboottaskappbackend.model.Priority
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

/**
 * @author : Lex Yu
 */
@Entity
@Table(
    name = "tasks",
    uniqueConstraints = [UniqueConstraint(name = "uk_task_description", columnNames = ["description"])]
)
class Task {
    @Id
    @GeneratedValue(generator = "task_sequence_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_sequence_generator", sequenceName = "task_sequence", allocationSize = 1)
    val id: Long = 0

    @NotBlank
    @Column(name = "description", nullable = false, unique = true)
    var description: String = ""

    @Column(name = "is_reminder_set")
    var isReminderSet: Boolean = false

    @Column(name = "is_task_open", nullable = false)
    var isTaskOpen: Boolean = true

    @Column(name = "created_on", nullable = false)
    val createdOn: LocalDateTime = LocalDateTime.now()

    @NotNull
    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW
    override fun toString(): String {
        return "Task(id=$id, description='$description', isReminderSet=$isReminderSet, isTaskOpen=$isTaskOpen," +
                " createdOn=$createdOn, priority=$priority)"
    }


}
