# theo-AIchat

### 基于 Spring Boot + Ollama 的本地 AI 对话系统。

这是一个大学生的首次独立完成项目，主要用于练习企业级开发流程与工程实践能力。本项目从零到一全程独自完整开发。从需求理解、代码实现到问题排
查均得到了有效帮助。通过此次实践，初步 体会到在现代软件开发中，AI 工具正在显著提升开发效率，并在一定程度上改变传统开发方式，使部分重复性
与实现层面的工作逐步被自动化支持所替代。

## 新版本内容

新增了docker打包功能以及通过cpolar进行服务器和网页部署，请求会自动发 到当前域名，公网可以正常使用（编写 Dockerfile + docker-compose，实
现一键本地/公网部署）。

## 项目简介

本项目是一个支持多会话管理的 AI 聊天应用，使用本地部署的大语言模型（qwen3:8b）提供对话能力，数据完全本地存储，不依赖任何云端 AI 服务。

## 技术栈

- **后端**：Spring Boot 3.x、MyBatis-Plus、JWT、BCrypt
- **数据库**：MySQL 8.x
- **AI 引擎**：Ollama（本地部署 qwen3:8b）
- **前端**：原生 HTML / CSS / JavaScript

## 功能

- 用户注册 / 登录（基于 JWT + BCrypt 实现无状态鉴权，Token 有效期可配置）
- 多会话管理（设计会话-消息一对多数据模型，支持历史上下文加载）
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

