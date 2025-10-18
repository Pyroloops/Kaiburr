Kaiburr Task Management API

Project Overview

This project is a backend Task Management API built with Spring Boot 3.x and MongoDB. It implements standard CRUD (Create, Read, Update, Delete) operations and includes a core feature for secure shell command execution associated with a specific task, fulfilling the requirements for the Kaiburr Full-Stack Assessment.

The application adheres to modern REST principles and is configured to run on a non-default port to avoid conflicts.

Key Features

CRUD Operations: Full support for creating, reading, updating, and deleting tasks.

MongoDB Persistence: Uses Spring Data MongoDB for seamless data storage.

Task Execution: Securely executes predefined shell commands (command field in Task) and logs the output in the taskExecutions history.

Command Allowlist: Commands are checked against a fixed allowlist (ls, pwd, echo) to prevent shell injection attacks.

Search Functionality: Find tasks by name (case-insensitive search).

Prerequisites

Before running the application, ensure you have the following installed:

Java Development Kit (JDK) 21+ (or a recent LTS version).

Apache Maven (for building the project).

MongoDB Server running locally on the default port (27017).

Postman or another API client for testing the endpoints.

Setup and Running

1. Database Configuration

The application is configured to connect to a local MongoDB instance.

Ensure the following configuration is present in src/main/resources/application.properties:

# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=kaiburr-tasks

# Server Port Configuration (Set to 8081 to avoid conflicts)
server.port=8081




2. Build the Project

Open your terminal in the project's root directory (task-api/) and run the Maven clean install command to compile the code and package it into an executable JAR file:

mvn clean install




3. Run the Application

Start the Spring Boot application using the generated JAR file:

java -jar target/task-api-0.0.1-SNAPSHOT.jar




The application will start and should be accessible at http://localhost:8081. You will see confirmation in the console: Tomcat started on port 8081 (http) with context path '/'.

API Endpoints Reference

All endpoints use the base URL: http://localhost:8081/tasks

Method

Endpoint

Description

Request Body (JSON)

Success Response

POST

/tasks

Creates a new task and saves it to MongoDB.

{ "name": "Task Name", "owner": "User Name", "command": "ls -l" }

201 Created

GET

/tasks

Retrieves a list of all tasks.

None

200 OK, List of Tasks

GET

/tasks/{id}

Retrieves a single task by its unique ID.

None

200 OK, Single Task

GET

/tasks/find/by-name/{name}

Searches for tasks where the name contains the provided string (case-insensitive).

None

200 OK, List of Tasks

PUT

/tasks

Updates an existing task. Must include the id field.

{ "id": "...", "name": "...", "owner": "...", "command": "..." }

200 OK

DELETE

/tasks/{id}

Deletes a task by its unique ID.

None

204 No Content

PUT

/tasks/execute/{id}

Core Feature: Executes the shell command defined in the task and records the execution result and history.

None

200 OK (Task with updated taskExecutions)

Example Execution Request

The task execution endpoint is critical. For a task with ID 68f405870da6f8c914f5f794:

Request:

PUT http://localhost:8081/tasks/execute/68f405870da6f8c914f5f794




Expected Response:
The task object will be returned with a new entry added to the taskExecutions list, containing the timestamp, command, exit code, and output.

{
    "id": "68f405870da6f8c914f5f794",
    "name": "Implement user authentication feature",
    "owner": "Jane Doe",
    "command": "echo 'Authentication task started'",
    "taskExecutions": [
        {
            "timestamp": "2025-10-19T02:50:00.000Z",
            "commandExecuted": "echo 'Authentication task started'",
            "exitCode": 0,
            "output": "Authentication task started\n"
        }
    ]
}




Validation Screenshots

This section is for documenting the successful API interactions using Postman.

ACTION REQUIRED: For each test listed below, replace the placeholder text with the appropriate image link (![Image Description](Image_URL)) or embed your screenshot directly. Ensure your operating system's taskbar showing your name and the current date/time is visible in the screenshot.

1. Task Creation (POST /tasks)

Objective: Validate that a new task can be created and the database returns a unique ID.

Screenshot: 

Kaiburr Task Management API

Project Overview

This project is a backend Task Management API built with Spring Boot 3.x and MongoDB. It implements standard CRUD (Create, Read, Update, Delete) operations and includes a core feature for secure shell command execution associated with a specific task, fulfilling the requirements for the Kaiburr Full-Stack Assessment.

The application adheres to modern REST principles and is configured to run on a non-default port to avoid conflicts.

Key Features

CRUD Operations: Full support for creating, reading, updating, and deleting tasks.

MongoDB Persistence: Uses Spring Data MongoDB for seamless data storage.

Task Execution: Securely executes predefined shell commands (command field in Task) and logs the output in the taskExecutions history.

Command Allowlist: Commands are checked against a fixed allowlist (ls, pwd, echo) to prevent shell injection attacks.

Search Functionality: Find tasks by name (case-insensitive search).

Prerequisites

Before running the application, ensure you have the following installed:

Java Development Kit (JDK) 21+ (or a recent LTS version).

Apache Maven (for building the project).

MongoDB Server running locally on the default port (27017).

Postman or another API client for testing the endpoints.

Setup and Running

1. Database Configuration

The application is configured to connect to a local MongoDB instance.

Ensure the following configuration is present in src/main/resources/application.properties:

# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=kaiburr-tasks

# Server Port Configuration (Set to 8081 to avoid conflicts)
server.port=8081




2. Build the Project

Open your terminal in the project's root directory (task-api/) and run the Maven clean install command to compile the code and package it into an executable JAR file:

mvn clean install




3. Run the Application

Start the Spring Boot application using the generated JAR file:

java -jar target/task-api-0.0.1-SNAPSHOT.jar




The application will start and should be accessible at http://localhost:8081. You will see confirmation in the console: Tomcat started on port 8081 (http) with context path '/'.

API Endpoints Reference

All endpoints use the base URL: http://localhost:8081/tasks

Method

Endpoint

Description

Request Body (JSON)

Success Response

POST

/tasks

Creates a new task and saves it to MongoDB.

{ "name": "Task Name", "owner": "User Name", "command": "ls -l" }

201 Created

GET

/tasks

Retrieves a list of all tasks.

None

200 OK, List of Tasks

GET

/tasks/{id}

Retrieves a single task by its unique ID.

None

200 OK, Single Task

GET

/tasks/find/by-name/{name}

Searches for tasks where the name contains the provided string (case-insensitive).

None

200 OK, List of Tasks

PUT

/tasks

Updates an existing task. Must include the id field.

{ "id": "...", "name": "...", "owner": "...", "command": "..." }

200 OK

DELETE

/tasks/{id}

Deletes a task by its unique ID.

None

204 No Content

PUT

/tasks/execute/{id}

Core Feature: Executes the shell command defined in the task and records the execution result and history.

None

200 OK (Task with updated taskExecutions)

Example Execution Request

The task execution endpoint is critical. For a task with ID 68f405870da6f8c914f5f794:

Request:

PUT http://localhost:8081/tasks/execute/68f405870da6f8c914f5f794




Expected Response:
The task object will be returned with a new entry added to the taskExecutions list, containing the timestamp, command, exit code, and output.

{
    "id": "68f405870da6f8c914f5f794",
    "name": "Implement user authentication feature",
    "owner": "Jane Doe",
    "command": "echo 'Authentication task started'",
    "taskExecutions": [
        {
            "timestamp": "2025-10-19T02:50:00.000Z",
            "commandExecuted": "echo 'Authentication task started'",
            "exitCode": 0,
            "output": "Authentication task started\n"
        }
    ]
}




Validation Screenshots

This section is for documenting the successful API interactions using Postman.

ACTION REQUIRED: For each test listed below, replace the placeholder text with the appropriate image link (![Image Description](Image_URL)) or embed your screenshot directly. Ensure your operating system's taskbar showing your name and the current date/time is visible in the screenshot.

1. Task Creation (POST /tasks)

Objective: Validate that a new task can be created and the database returns a unique ID.

Screenshot: 

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/923f903b-182f-44a2-97bf-b5914fa2ef18" />

2. Retrieve All Tasks (GET /tasks)

Objective: Validate the ability to retrieve all tasks saved in the MongoDB collection.

Screenshot: 

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/87ae1307-f654-4c1e-b050-cf69e837f515" />

3. Task Execution (PUT /tasks/execute/{id})

Objective: Validate that the shell command is executed and the execution result is logged in the taskExecutions array.

Screenshot: 

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/d4b55aca-1a3d-4dbe-95bb-0916eb8b53b7" />

