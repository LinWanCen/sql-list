# SQL 清单 sql-list

[![Version](https://img.shields.io/jetbrains/plugin/v/23142-sql-list.svg)](https://plugins.jetbrains.com/plugin/23142-sql-list)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/23142-sql-list.svg)](https://plugins.jetbrains.com/plugin/23142-sql-list)

- [Maven package for Jenkins etc](Jenkins.md).
- Gradle build for IDEA plugin.

- 入参
  - 多个目录
  - 多个文件
  - gitHash..gitHash 多个 Git 根目录
  - gitBranch..gitBranch 多个 Git 根目录
  - diff 旧目录 新目录
  - 无入参时 GUI 弹框选择


## Plugin description 插件介绍

<!-- Plugin description -->

Check and export MyBatis *.xml SQL to Excel With Git Author.

检查 Mybatis *.xml 中的 SQL，涉及的表和条件列，Git 最后修改时间和作者，生成 Excel 表格。

## How to Use

- Select dir and right-click <kbd>Export MyBatis *.xml SQL</kbd>
- Select two dir and right-click <kbd>Export Mybatis *.xml diff SQL</kbd>

## My Plugin
- Show doc comment in the Project view Tree, line End, JSON etc.: [Show Comment]
- Method call usage graph and maven dependency graph: [Draw Graph]
- Find author/comment of multiple files or lines and export Find: [Find Author]
- Auto sync coverage and capture coverage during debug: [Sync Coverage]
- UnCompress and Delete same, use `javap -c` decompile class: [Compare Jar]
- Check and export MyBatis *.xml SQL to Excel With Git Author: [SQL List]

---

# 中文

## 用法

- 选择多个目录右键点击 <kbd>导出 Mybatis *.xml 的 SQL</kbd>
- 选择两个目录右键点击 <kbd>导出 MyBatis *.xml 差异的 SQL</kbd>
- 在服务器中用 Jenkins 运行详见 [Jenkins 使用方式指南][Jenkins]。

## 我的项目
- 在文件树、行末、JSON 显示注释：[Show Comment]
- 生成 方法调用图 和 Maven 依赖图：[Draw Graph]
- 查找多个文件或行的作者 与 导出搜索：[Find Author]
- 自动同步覆盖率 和 调试中抓取覆盖率：[Sync Coverage]
- 逐层解压，删除相同文件，反编译 class 对比：[Compare Jar]
- 检查 Mybatis *.xml 中的 SQL，涉及的表和条件列，Git 最后修改时间和作者，生成 Excel 表格：[SQL List]

---

# 支持

如果对你有所帮助，别忘了给 [本项目 GitHub 主页][GitHub] 一个 Star，您的支持是项目前进的动力。

[Show Comment]: https://plugins.jetbrains.com/plugin/18553-show-comment
[Draw Graph]: https://plugins.jetbrains.com/plugin/21242-draw-graph
[Find Author]: https://plugins.jetbrains.com/plugin/20557-find-author
[Sync Coverage]: https://plugins.jetbrains.com/plugin/20780-sync-coverage
[Compare Jar]: https://plugins.jetbrains.com/plugin/22356-compare-jar
[SQL List]: https://plugins.jetbrains.com/plugin/23142-sql-list
[GitHub]: https://github.com/LinWanCen/sql-list
[Jenkins]: https://github.com/LinWanCen/sql-list/blob/main/Jenkins.md

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "sql-list"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/LinWanCen/sql-list/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

[Changelog 更新说明](CHANGELOG.md)

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation


## TODO

Jdom 解析标签文本无法获取所有文本，SQL 不完整，目前采用正则处理