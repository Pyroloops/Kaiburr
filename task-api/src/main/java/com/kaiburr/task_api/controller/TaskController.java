package com.kaiburr.task_api.controller;

import com.kaiburr.task_api.model.Task;
import com.kaiburr.task_api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

/**
 * Controller Layer (Section 2.4). Handles all incoming REST requests for the Task API.
 * Mapped to the base path "/tasks".
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // --- 1. CRUD: Create (POST /tasks) ---
    /**
     * Creates a new Task.
     * Changed from @PutMapping to @PostMapping to handle the client's request.
     */
    @PostMapping // Correctly mapped to POST for resource creation
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task savedTask = taskService.saveTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED); // 201 Created
    }
    
    // --- 1.1. CRUD: Update (PUT /tasks) ---
    /**
     * Updates an existing Task (if ID is present).
     * This remains as PUT, typically used for full replacement/update of a resource.
     */
    @PutMapping
    public ResponseEntity<Task> updateTask(@RequestBody Task task) {
        Task savedTask = taskService.saveTask(task);
        return new ResponseEntity<>(savedTask, HttpStatus.OK); // 200 OK for successful update
    }

    // --- 2. CRUD: Read All (GET /tasks) ---
    /**
     * Retrieves all tasks stored in the database.
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // --- 3. CRUD: Read by ID (GET /tasks/{id}) ---
    /**
     * Retrieves a single task by its unique ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        return taskService.findTaskById(id)
                .map(task -> new ResponseEntity<>(task, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + id));
    }

    // --- 4. Search: Find by Name (GET /tasks/find/by-name/{name}) ---
    /**
     * Searches for tasks where the name contains the provided string (case-insensitive).
     */
    @GetMapping("/find/by-name/{name}")
    public ResponseEntity<List<Task>> searchTasksByName(@PathVariable String name) {
        List<Task> tasks = taskService.searchTasksByName(name);
        // If the list is empty, return 204 No Content or 200 OK with empty list
        return new ResponseEntity<>(tasks, HttpStatus.OK); 
    }

    // --- 5. CRUD: Delete (DELETE /tasks/{id}) ---
    /**
     * Deletes a task by its unique ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable String id) {
        try {
            taskService.deleteTaskById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } catch (ResponseStatusException e) {
             // Re-throw if the underlying service layer throws a NOT_FOUND exception
             throw e; 
        }
    }
    
    // --- 6. Secure Execution (PUT /tasks/execute/{id}) ---
    /**
     * Executes the shell command associated with the task and records the execution history.
     * Implements the core requirement of Section 2.5.
     */
    @PutMapping("/execute/{id}")
    public ResponseEntity<Task> executeTaskCommand(@PathVariable String id) {
        try {
            Task updatedTask = taskService.executeTask(id);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (SecurityException e) {
            // Command is not on the Allowlist (Injection attempt)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ResponseStatusException e) {
            // Task Not Found
            throw e;
        } catch (IOException | InterruptedException e) {
            // General failure during command execution
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Command execution failed: " + e.getMessage());
        }
    }
}
