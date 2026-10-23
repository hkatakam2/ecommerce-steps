# ecommerce-steps

A minimal, modern Java 21 project using Maven and JUnit 5, isolated per-project with [SDKMAN!](https://sdkman.io/).

## Environment Management (SDKMAN!)

This project includes a `.sdkmanrc` file to pin its exact Java 21 and Maven versions without conflicting with other projects on your machine.

- **Automatic switching**: `sdkman_auto_env=true` is enabled in `~/.sdkman/etc/config`, so `cd`-ing into this directory automatically activates the project's Java and Maven versions.
- **Manual activation**:
  ```bash
  sdk env
  ```
- **Install pinned versions** (if on a new machine):
  ```bash
  sdk env install
  ```

## Build & Run

Compile and run tests:
```bash
mvn clean test
```

Run the main application:
```bash
mvn compile exec:java
```

Package a JAR:
```bash
mvn package
```
