# Recruitment - Registration, Login and Applicant Inventory

Java project with Ant (NetBeans), JavaFX + Scene Builder, JDBC (MySQL) and BCrypt.

## Features
- **Registration**: username, email and password (stored as a BCrypt hash). The assigned role is always `USER`.
- **Login**: validates email and password against the DB, shows an alert with the role and opens the inventory.
- **Applicant inventory**: table with ID, first name, last name, email, phone, address and age.
  Allows **adding, updating, deleting and listing**; the table is refreshed after every action.

## Database
The database does NOT live inside the project. Your `proyecto de taller.sql` script is used as-is (DB `recruitment_db_in4bv`, tables `role`, `user`, `applicant`) and is run separately in MySQL Workbench.
The `role` table comes empty: the app creates the `USER` role automatically on the first registration.

## Architecture (src/main/java/com/kinal/recruitment)
| Package | Responsibility |
|---|---|
| `config` | `Credentials` (MySQL data) and `DataBaseConnection` (JDBC connection) |
| `model` | Entities: `Role`, `User`, `Applicant` (they never reach the view) |
| `dto` | `LoginRequest`, `RegisterRequest`, `UserResponse`, `ApplicantRequest`, `ApplicantResponse` |
| `repository` / `repository.jdbc` | Interfaces and their JDBC implementation with `PreparedStatement` and try-with-resources |
| `service` / `service.impl` | Interfaces and business rules (validations, BCrypt, entity -> DTO mapping) |
| `controller` | `LoginController`, `RegisterController`, `ApplicantController` (receive their dependencies through the constructor) |
| `exception` | `AppException` and its children: `AuthException`, `ValidationException`, `ResourceNotFoundException`, `DatabaseException` |
| `security` | `BCrypt` and `PasswordHasher` |
| `util` | `SceneManager`, `View`, `SessionManager`, `Alerts` |

`Main` is the composition root: this is where repositories, services and controllers are created with `new`.

Flow: controller -> service (interface) -> repository (interface) -> MySQL. Only DTOs travel between layers.
`SceneManager` loads the FXML files with a *controller factory*, so the controllers get their services injected.

Views (Scene Builder): `src/main/resources/view`. Styles: `src/main/resources/css/styles.css`.

## How to run it
1. In MySQL Workbench, run your `proyecto de taller.sql` script (stored outside the project).
2. In `config/Credentials.java`, set your MySQL user and password.
3. In `nbproject/project.properties`, check the paths to `mysql-connector-j-9.4.0.jar` and the JavaFX 21 SDK.
4. Open the project in NetBeans and press **Run**.

## Push to GitHub (with several commits)
```
git init
git add .
git commit -m "Initial Ant project structure and database"
git branch -M main
git remote add origin https://github.com/YOUR_USER/YOUR_REPOSITORY.git
git push -u origin main
```
