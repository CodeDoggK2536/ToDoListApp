# Test Plan / 测试计划

## 1. Objective / 测试目标

Verify that the ToDoList App meets functional requirements, covering data model, database operations, and basic UI interaction.

## 2. Test Environment / 测试环境

| Item | Value |
|---|---|
| OS | Android 9.0+ (API 28+) |
| Device | Pixel 6 emulator (or any physical device) |
| Framework | JUnit 4 + AndroidX Test + Espresso |

## 3. Test Types / 测试类型

- **Unit Tests** — `Task.java` model (pure JUnit, no device needed)
- **Instrumented Tests** — `DatabaseHelper.java` CRUD (runs on device/emulator)
- **UI Smoke Test** — Activity launch + key elements visible (runs on device/emulator)

## 4. Test Cases / 测试用例

### 4.1 Task Model (Unit Test)

| ID | Description | Steps | Expected |
|---|---|---|---|
| TC-TASK-001 | Constructor sets all fields | Create Task with all params | All fields match input |
| TC-TASK-002 | Default completed is false | Create Task without completed=true | isCompleted() == false |
| TC-TASK-003 | Getters and setters | Set each field, then get | Values match |
| TC-TASK-004 | Sort order modification | Create Task, setSortOrder(5) | getSortOrder() == 5 |
| TC-TASK-005 | Completed status toggle | Create Task, setCompleted(true/false) | Toggles correctly |

### 4.2 DatabaseHelper (Instrumented Test)

| ID | Description | Steps | Expected |
|---|---|---|---|
| TC-DB-001 | Insert and retrieve task | Insert one Task, call getAllTasks | Returns list of size 1 with matching fields |
| TC-DB-002 | Empty database | Call getAllTasks on fresh DB | Returns empty list |
| TC-DB-003 | Update task fields | Insert, update title & completed, retrieve again | Fields updated |
| TC-DB-004 | Delete task | Insert then delete, call getAllTasks | List is empty |
| TC-DB-005 | Update sort order after drag | Insert 3 tasks, reorder via updateTaskOrder | sort_order values match new positions |
| TC-DB-006 | Multiple inserts preserve order | Insert 3 tasks with sort_order 2,0,1 | Returned in order 0,1,2 |

### 4.3 UI Smoke Test (Instrumented)

| ID | Description | Steps | Expected |
|---|---|---|---|
| TC-UI-001 | Activity launches with toolbar | Launch MainActivity | Toolbar with title "ToDoList" visible |
| TC-UI-002 | RecyclerView is visible | Launch MainActivity | recycler_tasks is displayed |
| TC-UI-003 | FAB add button is visible | Launch MainActivity | fab_add is displayed |

## 5. Bug Tracking / 错误追踪

| Bug ID | Test Case | Description | Severity | Status |
|---|---|---|---|---|
| — | — | — | — | — |

## 6. Pass Criteria / 通过标准

All test cases pass with no crashes, data loss, or UI rendering errors.
