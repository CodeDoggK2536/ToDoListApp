# ToDoList / 待办事项 App

[ek2536/ToDoListApp: Android ToDoList APP](https://github.com/ek2536/ToDoListApp)

A simple Android ToDoList app written in Java.

## Features / 功能

- Add, edit, delete, drag-and-drop reorder / 添加、编辑、删除、拖拽排序任务
- Due date & time picker / 日期时间选择器设置截止时间
- Multi-select batch deletion / 多选批量删除
- Dark theme & SQLite local storage / 暗色主题 & SQLite 本地存储

## Tech Stack / 技术栈

- **Language**: Java
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **UI**: Material Components + RecyclerView
- **Storage**: SQLite

## Project Structure / 项目结构

```
app/src/main/java/io/github/ek2536/todolist/
├── MainActivity.java      # main activity, handles UI and business logic
├── Task.java              # task data model
├── TaskAdapter.java       # RecyclerView adapter
└── DatabaseHelper.java    # SQLite database helper class

app/src/main/res/          # drawable / layout / menu / values / values-night
app/src/test/              # unit tests
app/src/androidTest/       # instrumentation tests
```

## How to Run / 如何运行

Android Studio Open Project → Sync Gradle → Run
