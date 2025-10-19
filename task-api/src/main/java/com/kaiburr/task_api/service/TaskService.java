package com.kaiburr.task_api.service;

import com.kaiburr.task_api.model.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer Interface. Defines the business operations required by the assessment:
 * CRUD (Create, Read All, Read by ID, Delete), Search, and Secure Execution.
 */
public interface TaskService {

    // CRUD Operations
    Task saveTask(Task task);
    List<Task> findAllTasks();
    Optional<Task> findTaskById(String id);
    void deleteTaskById(String id);

    // Search Operation (Section 2.4)
    List<Task> searchTasksByName(String name);

    /**
     * Securely executes the command associated with a Task (Section 2.5).
     * This is the core logic for command injection prevention and execution history tracking.
     *
     * @param id The ID of the Task to execute.
     * @return The updated Task object including the new TaskExecution history.
     * @throws IOException If the command execution fails.
     * @throws InterruptedException If the execution process is interrupted.
     * @throws SecurityException If the command is not in the allowlist.
     */
    Task executeTask(String id) throws IOException, InterruptedException, SecurityException;
}
