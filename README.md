# theo-AIchat

### 基于 Spring Boot + Ollama 的本地 AI 对话系统。

本项目从零到一全程独自完整开发。是一个开源 AI 智能聊天项目，集成主流大模型接口，具备上下文理解、意图识别、多场景对话适配等核心能力，可
实现日常闲聊、知识解答、需求咨询等多种交互需求，界面简洁、响应快速，兼顾实用性与扩展性。

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
- docker打包功能以及通过cpolar进行服务器和网页部署，请求会自动发 到当前域名，公网可以正常使用（编写 Dockerfile + docker-compose，实
现一键本地/公网部署）

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

