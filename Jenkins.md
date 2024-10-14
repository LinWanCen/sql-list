# Jenkins 使用方式指南

- 可以`mvn package`生成或在这里下载 zip：https://github.com/LinWanCen/sql-list/releases
- 然后在服务器上执行以下命令创建目录上传解压
  ```shell script
  mkidr /var/lib/jenkins/workspace/sql-list
  cd /var/lib/jenkins/workspace/sql-list
  rz
  unzip sql-list.zip
  ```
- 登陆 Jenkins
- 点击 新建Item
- 创建自由风格（Freestyle project）的项目
- 描述
    1. 点击 Build with Parameters
    2. 选择文件后点击"开始构建"，等左下角上传进度完成
    3. 执行完毕后点击"工作区"或"工作空间"
    4. 点击打包下载全部文件
    5. 解压后用 IDEA 打开
    6. 选择两个文件夹，右键比较即可
    - [x] Discard old builds
        - 保持构建的最大个数 5
    - [x] This project is parameterized
        - File Parameter
            - file1
            - file2
- 构建环境
    - [x] Delete workspace before build starts
- 构建
    - Execute shell
      ```shell script
      mv file1 "1-$file1"
      mv file2 "2-$file2"
      java -jar /var/lib/jenkins/workspace/sql-list/sql-list.jar -Dgit=true diff "1-$file1" "2-$file2"
      ```