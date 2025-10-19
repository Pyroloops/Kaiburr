package com.kaiburr.task_api.repository;

import com.kaiburr.task_api.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Layer Interface. Extends MongoRepository to inherit basic CRUD operations
 * (save, findById, findAll, deleteById, etc.) for the Task entity.
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    /**
     * Custom method for the search operation (Section 2.4).
     * Spring Data automatically generates the query to find Tasks where the
     * 'name' field contains the input string, ignoring case.
     *
     * @param name The name fragment to search for.
     * @return A list of matching Task entities.
     */
    List<Task> findByNameContainingIgnoreCase(String name);
}
