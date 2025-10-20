# Kaiburr Task Management API

## Project Overview

This project is a backend **Task Management API** built with **Spring Boot 3.x** and **MongoDB**. It implements standard CRUD (Create, Read, Update, Delete) operations and includes a core feature for secure shell command execution associated with a specific task, fulfilling the requirements for the Kaiburr Full-Stack Assessment.

The application adheres to modern REST principles and is configured to run on a non-default port to avoid conflicts.

## Key Features

* **CRUD Operations:** Full support for creating, reading, updating, and deleting tasks.
* **MongoDB Persistence:** Uses Spring Data MongoDB for seamless data storage.
* **Task Execution:** Securely executes predefined shell commands (`command` field in Task) and logs the output in the `taskExecutions` history.
* **Command Allowlist:** Commands are checked against a fixed allowlist (`ls`, `pwd`, `echo`) to prevent shell injection attacks.
* **Search Functionality:** Find tasks by name (case-insensitive search).

---

## Prerequisites

Before running the application, ensure you have the following installed:

* Java Development Kit (JDK) 21+ (or a recent LTS version).
* Apache Maven (for building the project).
* MongoDB Server running locally on the default port (27017).
* Postman or another API client for testing the endpoints.

---

# Task 1 - Engineering a Robust Java REST API 

## 1. Setup and Running

### 1.1 Database Configuration

The application is configured to connect to a local MongoDB instance.
Ensure the following configuration is present in `src/main/resources/application.properties`:

### 1.2 MongoDB Configuration

```properties
spring.data.mongodb.host = localhost
spring.data.mongodb.port = 27017
spring.data.mongodb.database = kaiburr-tasks
```

## 1.3 Server Port Configuration 

```properties
server.port=8081
```

## 2. Build the Project

Clone the repository:

```bash
git clone https://github.com/Pyroloops/Kaiburr.git
cd kaiburr-backend-assessment/task-api
```
Ensure MongoDB is running: Make sure your local MongoDB server is active on the default port 27017.

Open your terminal in the project's root directory (`task-api/`) and run the Maven clean install command to compile the code and package it into an executable JAR file:

```bash
mvn clean install
```

## 3. Run the Application

Start the Spring Boot application using the generated JAR file:

```bash
java -jar target/task-api-0.0.1-SNAPSHOT.jar
```
The application will start and should be accessible at `http://localhost:8081`. You will see confirmation in the console:

## 4. API Endpoints Reference

All endpoints use the base URL: `http://localhost:8081/tasks`

| Method | Endpoint | Description | Request Body (JSON) | Success Response |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/tasks` | Creates a new task and saves it to MongoDB. | `{ "name": "Task Name", "owner": "User Name", "command": "ls -l" }` | `201 Created` |
| **GET** | `/tasks` | Retrieves a list of all tasks. | None | `200 OK`, List of Tasks |
| **GET** | `/tasks/{id}` | Retrieves a single task by its unique ID. | None | `200 OK`, Single Task |
| **GET** | `/tasks/find/by-name/{name}` | Searches for tasks where the name contains the provided string (case-insensitive). | None | `200 OK`, List of Tasks |
| **PUT** | `/tasks` | Updates an existing task. Must include the `id` field. | `{ "id": "...", "name": "...", "owner": "...", "command": "..." }` | `200 OK` |
| **DELETE** | `/tasks/{id}` | Deletes a task by its unique ID. | None | `204 No Content` |
| **PUT** | `/tasks/execute/{id}` | **Core Feature:** Executes the shell command defined in the task and records the execution result and history. | None | `200 OK` (Task with updated taskExecutions) |

## 5. Example Task Execution Request

The task execution endpoint is a critical feature. For a task with ID `68f405870da6f8c914f5f794`:

**Request:**

```bash
PUT http://localhost:8081/tasks/execute/68f405870da6f8c914f5f794
```

**Expected Response (200 OK):**

The task object will be returned with a new entry added to the `taskExecutions` list, containing the timestamp, command, exit code, and output.

```json
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
```

## 6. Validation Screenshots

This section documents the successful API interactions using Postman. The operating system's taskbar showing your name and the current date/time should be visible in the screenshots.

### 1. Task Creation (POST /tasks)

**Objective:** Validate that a new task can be created and the database returns a unique ID.

**Screenshot:**
<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/923f903b-182f-44a2-97bf-b5914fa2ef18" />

### 2. Retrieve All Tasks (GET /tasks)

**Objective:** Validate the ability to retrieve all tasks saved in the MongoDB collection.

**Screenshot:**
<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/87ae1307-f654-4c1e-b050-cf69e837f515" />

### 3. Task Execution (PUT /tasks/execute/{id})

**Objective:** Validate that the shell command is executed and the execution result is logged in the `taskExecutions` array.

**Screenshot:**
<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/d4b55aca-1a3d-4dbe-95bb-0916eb8b53b7" />

_______________

# Task 2 - Deploying and Integrating with Kubernetes

## Containerizing the Application with Docker 

A **multi-stage build** is used to create a lightweight final image for the application. This approach separates the build environment (which has tools like Maven) from the runtime environment (which only needs the JRE), resulting in a much smaller and more secure final image.

### Dockerfile

```dockerfile
# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final, slim image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

## 1. Build and Push Commands

Replace `your-dockerhub-username` with your actual Docker Hub username.

### 1.1 Build the Docker Image
This command builds the image locally and tags it with your Docker Hub repository name and the tag `latest`.

```bash
docker build -t your-dockerhub-username/kaiburr-task-app:latest .
```

### 1.2 Push the Docker Image (Section 3.5 Step 1)

This command uploads the built image to your Docker Hub repository, making it accessible for deployment on Kubernetes.

```bash
docker push your-dockerhub-username/kaiburr-task-app:latest
```

## 2. Deploying MongoDB with Persistent Storage

The database uses a **StatefulSet** for stability and a **PersistentVolumeClaim (PVC)** to ensure data persists across container restarts.

---

### 2.1 mongodb-statefulset.yaml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: mongo-service
spec:
  selector:
    app: mongo
  ports:
    - protocol: TCP
      port: 27017
      targetPort: 27017
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mongo
spec:
  serviceName: "mongo-service"
  replicas: 1
  selector:
    matchLabels:
      app: mongo
  template:
    metadata:
      labels:
        app: mongo
    spec:
      containers:
        - name: mongo
          image: mongo:latest
          ports:
            - containerPort: 27017
          volumeMounts:
            - name: mongo-persistent-storage
              mountPath: /data/db
  volumeClaimTemplates:
    - metadata:
        name: mongo-persistent-storage
      spec:
        accessModes:
          - ReadWriteOnce 
        resources:
          requests:
            storage: 1Gi
```

## 3. Crafting Kubernetes Manifests for the Application

The **Deployment** manages the application container, connecting to **MongoDB** via the service name (`mongo-service`).  
The **Service** exposes the application using **NodePort**.

---

### 3.1 deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-api-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: task-api
  template:
    metadata:
      labels:
        app: task-api
    spec:
      containers:
        - name: task-api
          image: your-dockerhub-username/kaiburr-task-app:latest
          ports:
            - containerPort: 8080
          env:
            - name: spring.data.mongodb.uri
              value: "mongodb://mongo-service:27017/kaiburr_db"
```

### 3.2 service.yaml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: task-api-service
spec:
  type: NodePort
  selector:
    app: task-api
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
      nodePort: 30080 
```

## 4. Programmatic Pod Creation

This section implements the **Operator Pattern** using the Kubernetes Java Client for task execution.

### 4.1 Add Dependency (in pom.xml)

```xml
<dependency>
  <groupId>io.kubernetes</groupId>
  <artifactId>client-java</artifactId>
  <version>19.0.0</version>
</dependency>
```

### 4.2 Configure RBAC

The application needs a **ServiceAccount**, a **Role** to manage pods (create, get, delete, etc.), and a **RoleBinding** to link them.

### 4.3 rbac.yaml

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: task-api-sa
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: pod-manager-role
rules:
  - apiGroups: [""]
    resources: ["pods", "pods/log"]
    verbs: ["create", "get", "list", "watch", "delete"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: pod-manager-binding
subjects:
  - kind: ServiceAccount
    name: task-api-sa
roleRef:
  kind: Role
  name: pod-manager-role
  apiGroup: rbac.authorization.k8s.io
```

### 4.3 Update deployment.yaml

Add the **ServiceAccount** to the Deployment spec:

```yaml
# ... inside spec.template.spec
spec:
  serviceAccountName: task-api-sa # ADD THIS LINE
  containers:
# ...
```

### 4.4 Implement in Java (Refactor TaskService)

The execution logic is refactored to: 
1. Initialize the client,  
2. Define a temporary `V1Pod` with `busybox` and `restartPolicy: Never`,  
3. Create the Pod,  
4. Poll its status until `Succeeded` or `Failed`,  
5. Read logs, and  
6. Crucially, delete the Pod in a `finally` block.

```java
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public String executeTaskCommand(String command) throws Exception {
    // 1. Authentication (in-cluster config)
    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);
    CoreV1Api api = new CoreV1Api();

    final String podName = "command-runner-" + System.currentTimeMillis();
    final String namespace = "default";
    String result = "";

    // 2. Pod Definition
    V1Pod pod = new V1PodBuilder()
            .withNewMetadata().withName(podName).endMetadata()
            .withNewSpec()
                .addNewContainer()
                    .withName("executor")
                    .withImage("busybox")
                    .withCommand("sh", "-c")
                    .withArgs(command) 
                .endContainer()
                .withRestartPolicy("Never")
            .endSpec()
            .build();

    try {
        // 3. Execution Flow: Create Pod
        api.createNamespacedPod(namespace, pod, null, null, null, null);
        
        // 3. Execution Flow: Wait for Completion
        String phase;
        do {
            TimeUnit.SECONDS.sleep(3);
            V1Pod currentPod = api.readNamespacedPod(podName, namespace, null);
            phase = currentPod.getStatus().getPhase();
        } while (phase.equals("Pending") || phase.equals("Running"));

        // 3. Execution Flow: Retrieve Logs
        if (phase.equals("Succeeded")) {
            result = api.readNamespacedPodLog(podName, namespace, "executor", null, null, null, null, null, null, null, null, null);
        } else {
            result = "Pod failed with phase: " + phase;
        }
    } catch (ApiException e) {
        result = "Kubernetes API Error: " + e.getResponseBody();
    } finally {
        // 4. Crucial: Clean up the temporary pod
        try {
            api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            System.err.println("Error deleting pod: " + e.getMessage());
        }
    }
    return result;
}
```

## 5. Launch and Verification

1. Push Docker Image (Completed in Section 3.1)  
2. Deploy to Kubernetes

```bash
kubectl apply -f mongodb-statefulset.yaml
kubectl apply -f rbac.yaml
kubectl apply -f deployment.yaml # Updated with serviceAccountName
kubectl apply -f service.yaml
```

### 5.1 Verification Commands

| Command              | Purpose                  | Expected Status                                  |
|----------------------|--------------------------|-------------------------------------------------|
| kubectl get pods      | Verify container health  | mongo-0 and task-api-deployment-... must be Running |
| kubectl get svc       | Verify network exposure  | task-api-service must show NodePort 30080       |
| kubectl get nodes     | Check cluster readiness  | Nodes must be Ready                              |

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/719d88ed-e917-4e37-8510-22c785c859d1" />


### 5.2 Full Testing and Validation (CRUD & Operator)

Use `curl` to test the API via the NodePort `30080`.

#### A. POST (Create Task)

```bash
curl -X POST http://localhost:30080/tasks \
     -H "Content-Type: application/json" \
     -d '{
            "name": "Kubernets Deployment and Integration",  
            "owner": "Jane Doe",
            "command": "echo 'System Deployed'"
         }
# Expected: Returns the created JSON object.
```

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/e7a4066f-51be-4894-8fd9-e42dc00c20e4" />


### B. GET (Read Tasks)

```bash
curl http://localhost:30080/tasks
# Expected: Returns the JSON array containing the task T101.
```

<img width="2880" height="1800" alt="image" src="https://github.com/user-attachments/assets/e9b08fc6-b8d6-4861-8b22-bf5ea9ab6cfc" />

# Task 4 - Automated CI/CD Pipeline

This project utilizes **GitHub Actions** to implement a Continuous Integration (CI) pipeline. This automation ensures that every code change pushed to the main branch is automatically built, tested, and packaged into a versioned Docker image, which is then pushed to Docker Hub.  

This approach separates the **CI (Build/Package)** phase from the **CD (Deployment)** phase, aligning with modern DevOps best practices.

## 1. Prerequisites

To ensure the pipeline runs successfully, you must complete the following setup steps:

### 1.1 Docker Hub Credentials

The pipeline needs credentials to log into Docker Hub and push the final image artifact. These credentials must be securely stored as GitHub Repository Secrets.

1. Generate a Personal Access Token (PAT) from your Docker Hub account settings with **Read & Write** permissions.  
2. In your GitHub repository, navigate to **Settings → Secrets and variables → Actions**.  
3. Add the following two repository secrets:  

- `DOCKERHUB_USERNAME`: Your Docker Hub username.  
- `DOCKERHUB_TOKEN`: The Personal Access Token you generated (used as the password).

### 1.2 Project Structure

The pipeline assumes the following file locations for the Java API project:

- The main Maven configuration file (`pom.xml`) is located in the **root directory** of the repository (or adjusted path).  
- The Docker packaging instructions (`Dockerfile`) are located in the **root directory** of the repository (or adjusted path).

## 2. CI Workflow (.github/workflows/build.yml)

The workflow file is defined at `.github/workflows/build.yml`. It executes a single job, `build-and-push`, which performs four main tasks:

| Step Name               | Purpose                                                         | Action Used / Command        |
|-------------------------|-----------------------------------------------------------------|-----------------------------|
| Set up JDK 17           | Configures the runner environment with the required Java 17 LTS version. | `actions/setup-java`        |
| Cache Maven packages    | Caches Maven dependencies to significantly speed up future builds. | `actions/cache`             |
| Build with Maven        | Compiles the Java application and packages it into a runnable JAR file. | `mvn -B package ...`        |
| Log in to Docker Hub    | Authenticates against Docker Hub using the configured GitHub Secrets. | `docker/login-action`       |
| Build and push Docker image | Builds the Docker image based on the local Dockerfile and pushes the final version to Docker Hub. | `docker/build-push-action` |

### Workflow Code

The current, functional version of the workflow file is:

```yaml
name: Java CI with Maven and Docker 

on: 
  push: 
    branches: [ "main" ] 

jobs: 
  build-and-push: 
    runs-on: ubuntu-latest 
    steps: 
        - name: Checkout repository 
          uses: actions/checkout@v3 

        - name: Set up JDK 17 
          uses: actions/setup-java@v3 
          with: 
            java-version: '17' 
            distribution: 'temurin' 

        - name: Cache Maven packages 
          uses: actions/cache@v3 
          with: 
            path: ~/.m2 
            key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }} 
            restore-keys: | 
              ${{ runner.os }}-m2- 

        - name: Build with Maven 
          run: mvn -B package --file pom.xml -DskipTests 

        - name: Log in to Docker Hub 
          uses: docker/login-action@v2 
          with: 
            username: ${{ secrets.DOCKERHUB_USERNAME }} 
            password: ${{ secrets.DOCKERHUB_TOKEN }} 

        - name: Build and push Docker image 
          uses: docker/build-push-action@v4 
          with: 
            context:. 
            push: true 
            tags: ${{ secrets.DOCKERHUB_USERNAME }}/kaiburr-task-app:latest
```

## 3. Usage and Verification

- **Trigger:** Push any code changes (or the workflow file itself) to the `main` branch.  
- **Monitor:** Go to the **Actions** tab on GitHub to monitor the workflow run status.  
- **Artifact:** Upon successful completion, the new Docker image will be available in your Docker Hub repository with the tag `latest` (and potentially other tags you define).  
- **Status:** The overall workflow status can be viewed directly on the **Actions** tab, showing a green checkmark for success.
<img width="2879" height="1717" alt="image" src="https://github.com/user-attachments/assets/b1a1f518-fa93-4b00-9916-58d87c6b4b34" />






