# SQL 清单

解析 Mapper.xml 中的 SQL，涉及的表和条件列，Git 最后修改时间和作者，生成 Excel 表格

## 入参

- 多个目录
- 多个文件
- gitHash..gitHash 多个 Git 根目录
- gitBranch..gitBranch 多个 Git 根目录
- diff 旧目录 新目录
- 无入参时 GUI 弹框选择

## TODO

Jdom 解析标签文本无法获取所有文本，SQL 不完整，目前采用正则处理