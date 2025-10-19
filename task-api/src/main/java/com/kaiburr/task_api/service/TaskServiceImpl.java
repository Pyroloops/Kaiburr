package com.kaiburr.task_api.service;

import com.kaiburr.task_api.model.Task;
import com.kaiburr.task_api.model.TaskExecution;
import com.kaiburr.task_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service Layer Implementation. Contains the business logic, especially the
 * critical security check and command execution logic (Section 2.5).
 */
@Service
public class TaskServiceImpl implements TaskService {

    // --- CRITICAL SECURITY: ALLOWLIST (Whitelist) OF SAFE COMMANDS ---
    // Only commands explicitly listed here are permitted to run. This prevents 
    // Command Injection attacks (e.g., trying to run 'bash' or 'rm -rf /').
    private static final Set<String> ALLOWED_COMMANDS = Set.of(
            "ls", "ping", "echo", "pwd", "date", "whoami"
    );

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // --- Standard CRUD Operations ---

    @Override
    public Task saveTask(Task task) {
        // Simple save operation
        return taskRepository.save(task);
    }

    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> findTaskById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public void deleteTaskById(String id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
    }

    // --- Search Operation (Section 2.4) ---

    @Override
    public List<Task> searchTasksByName(String name) {
        // Uses the custom method defined in the MongoRepository interface for case-insensitive partial match
        return taskRepository.findByNameContainingIgnoreCase(name);
    }

    // --- Secure Execution Logic (Section 2.5) ---

    @Override
    public Task executeTask(String id) throws IOException, InterruptedException, SecurityException {
        // 1. Fetch the Task
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + id));

        String commandString = task.getCommand();
        Instant startTime = Instant.now();
        StringBuilder output = new StringBuilder();

        // Tokenize the command string into a list of arguments (REQUIRED for ProcessBuilder security)
        List<String> commandTokens = Arrays.asList(commandString.split("\\s+"));

        if (commandTokens.isEmpty()) {
            output.append("Error: Command string is empty.");
        } else {
            // 2. CRITICAL SECURITY CHECK: Allowlist Validation
            String baseCommand = commandTokens.get(0);
            if (!ALLOWED_COMMANDS.contains(baseCommand)) {
                // Reject request if command is not explicitly allowed
                throw new SecurityException("Command execution failed: '" + baseCommand + "' is not on the secure allowlist. Access denied.");
            }

            // 3. Execute using ProcessBuilder (avoids shell interpretation)
            ProcessBuilder processBuilder = new ProcessBuilder(commandTokens);
            processBuilder.redirectErrorStream(true); // Combine stdout and stderr
            Process process = processBuilder.start();

            // 4. Capture Output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // Wait for process to complete (with a 10-second timeout for robustness)
            boolean finished = process.waitFor(10, TimeUnit.SECONDS); 
            if (!finished) {
                 process.destroyForcibly();
                 output.append("\n--- COMMAND TIMEOUT (Process forcefully terminated) ---");
            }
        }

        // 5. Record History and Persist
        Instant endTime = Instant.now();

        TaskExecution execution = new TaskExecution();
        execution.setStartTime(startTime);
        execution.setEndTime(endTime);
        execution.setOutput(output.toString());

        task.getTaskExecutions().add(execution);

        // Save the updated Task (with the new execution record) back to the database
        return taskRepository.save(task);
    }
}
