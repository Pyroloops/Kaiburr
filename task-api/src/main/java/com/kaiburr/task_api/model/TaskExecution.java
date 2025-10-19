package com.kaiburr.task_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Embedded document representing a single execution record of a Task.
 */
@Data // Generates all getters, setters, equals, and hashCode.
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecution {
    private Instant startTime;
    private Instant endTime;
    private String output;
}
