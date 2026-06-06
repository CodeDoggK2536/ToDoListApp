# ToDoList / 待办事项 App

A simple Android to-do list app built with Java. 本项目是一个用 Java 编写的简单安卓待办事项应用。

## Features / 功能

- Add, edit and delete tasks / 添加、编辑、删除任务
- Set a due date and time for each task / 为每个任务设置截止日期和时间
- Mark tasks as completed (sorted to the bottom) / 标记任务完成（自动排序到底部）
- Long-press to enter multi-select mode for batch deletion / 长按进入多选模式，可批量删除
- Local storage with SQLite, no network required / 使用 SQLite 本地存储，无需联网

## Tech Stack / 技术栈

- **Language / 语言**: Java
- **Min SDK / 最低版本**: 28 (Android 9.0)
- **Target SDK / 目标版本**: 36
- **UI / 界面**: Material Components + RecyclerView
- **Storage / 存储**: SQLite (via `SQLiteOpenHelper`)

## Project Structure / 项目结构

```
app/src/main/java/com/k2536/ToDoList/
├── MainActivity.java   # Main UI & logic / 主界面与逻辑
├── Task.java           # Task data model / 任务数据模型
├── TaskAdapter.java    # RecyclerView adapter / 列表适配器
└── DatabaseHelper.java # SQLite helper / 数据库帮助类
```

## How to Run / 如何运行

1. Open the project in Android Studio / 用 Android Studio 打开项目
2. Sync Gradle and run on an emulator or device / 同步 Gradle，在模拟器或真机上运行

## License / 许可证

Free to use. 随便用，反正乱写的。
