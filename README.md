# ToDoList / 待办事项 App

A simple Android to-do list app built with Java. 本项目是一个用 Java 编写的简单安卓待办事项应用。

## Features / 功能

- Add, edit and delete tasks / 添加、编辑、删除任务
- Set a due date and time for each task via a date/time picker / 通过日期时间选择器设置截止时间
- Mark tasks as completed with a visual check icon / 标记任务完成（圆形勾选图标）
- Completed tasks display strikethrough and reduced opacity / 已完成任务显示删除线和半透明效果
- Drag-and-drop reorder tasks via long-press / 长按拖拽排序
- Long-press to enter multi-select mode for batch deletion / 长按进入多选模式，可批量删除任意任务
- Bottom action bar with delete/cancel buttons in multi-select mode / 多选模式下底部操作栏（删除/取消）
- Toolbar "Select / Done" toggle for entering/exiting multi-select / 工具栏 Select/Done 切换多选模式
- Custom Material dialog with rounded corners for task creation/editing / 自定义 Material 圆角对话框用于创建/编辑任务
- Empty-state hint when no tasks exist / 无任务时显示空状态提示
- Dark theme support / 暗色主题支持
- Local storage with SQLite, no network required / 使用 SQLite 本地存储，无需联网

## Tech Stack / 技术栈

- **Language / 语言**: Java
- **Min SDK / 最低版本**: 28 (Android 9.0)
- **Target SDK / 目标版本**: 36
- **UI / 界面**: Material Components + RecyclerView + CoordinatorLayout
- **Storage / 存储**: SQLite (via `SQLiteOpenHelper`)
- **Testing / 测试**: JUnit 4 + Espresso

## Project Structure / 项目结构

app/src/main/java/io/github/ek2536/todolist/
├── MainActivity.java      # Main UI & logic / 主界面与逻辑
├── Task.java              # Task data model / 任务数据模型
├── TaskAdapter.java       # RecyclerView adapter / 列表适配器
└── DatabaseHelper.java    # SQLite helper / 数据库帮助类

app/src/main/res/
├── drawable/              # Vector drawables (check, unchecked icons) / 矢量图标（勾选、未勾选图标）
├── layout/                # XML layouts (activity_main, dialog_task, item_task) / XML 布局文件（主界面、任务对话框、任务列表项）
├── menu/                  # Toolbar menu (Select action) / 工具栏菜单（选择操作）
├── values/                # Colors, strings, themes (light) / 颜色、字符串、主题（亮色）
└── values-night/          # Colors, themes (dark) / 颜色、主题（暗色）

app/src/test/              # Unit tests / 单元测试
└── io/github/ek2536/todolist/
    └── TaskUnitTest.java  # Task model unit tests / 任务模型单元测试

app/src/androidTest/       # Instrumented tests / 仪器化测试
└── io/github/ek2536/todolist/
    ├── MainActivityUiTest.java              # UI smoke test / UI 冒烟测试
    └── DatabaseHelperInstrumentedTest.java  # DB CRUD tests / 数据库增删改查测试

## How to Run / 如何运行

1. Open the project in Android Studio / 用 Android Studio 打开项目
2. Sync Gradle and run on an emulator or device / 同步 Gradle，在模拟器或真机上运行

