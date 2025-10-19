package com.kaiburr.task_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Main MongoDB Document for the Task API.
 * It stores task details (name, owner, command) and embeds a history of executions.
 */
@Data // Generates all required getters, setters, equals, and hashCode.
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks") // Maps this class to the 'tasks' collection in MongoDB
public class Task {
    @Id
    private String id;
    private String name;
    private String owner;
    private String command;

    /**
     * Embeds a list of TaskExecution objects, storing the history of command runs.
     * Initialized to an empty ArrayList to prevent NullPointerExceptions.
     */
    private List<TaskExecution> taskExecutions = new ArrayList<>();
}
