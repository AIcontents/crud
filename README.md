# JavaFX CRUD Application

This is a simple CRUD (Create, Read, Update, Delete) application built with JavaFX and Maven. It demonstrates a basic desktop application for managing a list of entities.

## Features

*   **Create, Read, Update, Delete (CRUD):** Manage a list of entities with a name and description.
*   **JavaFX UI:** A clean and simple user interface built with FXML.
*   **In-Memory Database:** Uses H2 in-memory database, so no external database setup is required. The data is reset every time the application starts.
*   **Data Validation:** Ensures that entities have a name.
*   **UUIDs and Timestamps:** Entities are identified by a UUID and have `createdAt` and `updatedAt` timestamps.
*   **Shaded JAR:** Packaged as a single, executable JAR file with all dependencies included.

## Requirements

*   **Java JDK 11 or higher:** Required to run the application.
*   **Apache Maven:** Required to build the project and package the application.

## How to Build and Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/AIcontents/crud
    cd crud
    ```

2.  **Build the project using Maven:**
    This command compiles the code, runs tests, and packages the application into a single executable JAR file in the `target` directory.
    ```bash
    mvn package
    ```

3.  **Run the application:**
    ```bash
    java -jar target/crudapp-1.0-SNAPSHOT.jar
    ```

## Project Structure

*   `src/main/java`: Contains the main Java source code.
    *   `com.example.crudapp.MainApp`: The main entry point of the application.
    *   `com.example.crudapp.Launcher`: A wrapper class to fix a common JavaFX issue with shaded JARs.
    *   `com.example.crudapp.controller`: Contains FXML controllers (`MainController`, `EntityDialogController`).
    *   `com.example.crudapp.dao`: Contains the Data Access Object (DAO) for database interaction (`EntityDAO`, `EntityDAOImpl`, `Database`).
    *   `com.example.crudapp.model`: Contains the data model (`Entity`, `ValidationException`).
*   `src/main/resources`: Contains FXML files and other resources.
*   `src/test/java`: Contains JUnit tests.
*   `pom.xml`: The Maven project configuration file.
