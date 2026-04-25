# BudgetQuest

[![Generate APK AAB](https://github.com/RayMambani/group-assignment/actions/workflows/build.yml/badge.svg)](https://github.com/RayMambani/group-assignment/actions/workflows/build.yml)

BudgetQuest is a professional finance management application developed with a "Cyber-Noir" design language. It integrates gamification elements—such as levels, experience points (XP), and badges—to incentivize consistent financial tracking and goal attainment.

## Key Features

*   **Centralized Dashboard**: Provides real-time expenditure analysis and budget utilization metrics.
*   **Expense Management**: Streamlined entry system for logging daily transactions with receipt attachment capabilities.
*   **Budget Categorization**: Dynamic category management with monthly limit tracking and progress visualization.
*   **Gamified Achievement System**: Progress tracking through experience levels and achievement badges based on spending habits and streaks.
*   **Real-time Analytics**: Visual data representations for category-wise spending and monthly trends.

## Technical Architecture

The application is built using modern Android development standards:

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Declarative UI)
*   **Architecture Pattern**: MVVM (Model-View-ViewModel)
*   **Dependency Injection**: Hilt (Dagger)
*   **Persistence Layer**: Room Database (SQLite)
*   **Asynchronous Flow**: Kotlin Coroutines and StateFlow
*   **Security**: BCrypt for local credential hashing

## Development Setup

### Prerequisites

*   Android Studio Flamingo | 2022.2.1 or newer
*   JDK 17
*   Android SDK Level 26 (Minimum)

### Installation and Execution

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/RayMambani/group-assignment.git
    ```
2.  **Project Initialization**:
    Open the project in Android Studio and allow Gradle to synchronize dependencies.
3.  **Deployment**:
    Connect an Android device or emulator and use the `Run` command or execute via terminal:
    ```bash
    ./gradlew installDebug
    ```

## Infrastructure

### Automated Build Pipeline (CI/CD)

The project leverages GitHub Actions for continuous integration. The pipeline is configured to execute on every push to the default branch, performing the following operations:

1.  **Unit Testing**: Automated validation of repository and logic layers.
2.  **Compilation**: Static analysis and build verification.
3.  **Artifact Generation**: Production of Debug and Release APKs/AABs, archived as build artifacts.

## Project Contributors

*   **Ray Mambani** - Project Lead
*   **Group Members** - System Development

## Licensing

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for comprehensive details.
