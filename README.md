# theo-AIchat

基于 Spring Boot + Ollama 的本地 AI 对话系统。
这是一个大学生首次自我完成的项目，旨在练习企业开发，首次开发耗时约12小时，其中借助了ChatGPT、Claude、Gemini、豆包等AI工具，也体会到了现代企业开发中AI对人工能力的大量代替。

## 项目简介

本项目是一个支持多会话管理的 AI 聊天应用，使用本地部署的大语言模型（qwen3:8b）提供对话能力，数据完全本地存储，不依赖任何云端 AI 服务。

## 技术栈

- **后端**：Spring Boot 3.x、MyBatis-Plus、JWT、BCrypt
- **数据库**：MySQL 8.x
- **AI 引擎**：Ollama（本地部署 qwen3:8b）（后续会支持多模型切换）
- **前端**：原生 HTML / CSS / JavaScript

## 功能

- 用户注册 / 登录（JWT 鉴权 + BCrypt 密码加密）
- 多会话管理（新建、切换、删除会话）
- AI 对话（接入本地 Ollama）
- 聊天记录持久化存储
- 历史消息加载

## 本地运行

1. 安装并启动 MySQL，创建数据库
2. 安装并启动 Ollama，拉取模型：
```bash
   ollama pull qwen3:8b
```
3. 复制配置文件并填写参数：
```bash
   cp src/main/resources/application-example.properties src/main/resources/application.properties
```
4. 启动项目：
```bash
   mvn spring-boot:run
```
5. 浏览器访问：`http://localhost:8080/login.html`

## 环境变量

| 变量名 | 说明 |
|--------|------|
| DB_PASSWORD | 数据库密码 |
| JWT_SECRET | JWT 签名密钥 |



命令行（CMD）单点销户步骤
1. 打开 CMD 并进入 MySQL
   按下快捷键 Win + R 输入 cmd 打开命令行，然后输入以下命令登录你的本地数据库（根据你的实际情况修改用户名 root 和密码）：

Bash
mysql -u root -p
回车后输入你的数据库密码。

2. 切换到你的项目数据库
   登录成功后，先看看你的项目数据库叫什么（假设叫 ai_knowledge_base），进入它：

SQL
USE ai_knowledge_base;

   查看当前存在的账号：

SQL
SELECT id, username, password FROM user;

3. 顺次执行基础 SQL 擦除数据
   假设你要删除的违规或测试账号的用户名是 test123@gmail.com，请在命令行中依次复制并执行这三行最基本的 SQL：

SQL
-- 步骤 A：先清空该用户的所有聊天历史记录
DELETE FROM chat_record WHERE username = 'test123@gmail.com';

-- 步骤 B：再清空该用户创建的所有聊天会话
DELETE FROM session WHERE username = 'test123@gmail.com';

-- 步骤 C：最后，彻底抹去这个用户的账号
DELETE FROM user WHERE username = 'test123@gmail.com';
当看到终端提示 Query OK, 1 row affected（或几条记录被影响），就说明这个账号以及他产生的所有痕迹已经被你在底层用基础 SQL 定点清空了！

   为什么必须按照 A ➔ B ➔ C 的顺序删？
因为聊天记录（chat_record）和会话（session）在业务逻辑上是依附于用户（user）存在的。如果先删除了 user，那么遗留在 chat_record 表里的数
据就会变成找不到主人的“孤儿数据”（占用数据库空间且无法通过前端正常解包展示）。所以从底层的 CMD 操作时，由内向外依次剥离才是最稳妥的做法。