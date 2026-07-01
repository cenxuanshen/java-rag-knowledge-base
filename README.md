# 📚 本地知识库问答系统（Java RAG）

基于 Java + Spring Boot + 智谱 AI 的本地知识库问答系统，支持 PDF、Word、Excel 等多种文档格式。

## 功能
- 📄 多格式文档解析（PDF/Word/Excel/Markdown/TXT）
- 🔍 TF-IDF 智能检索
- 🤖 大模型增强回答（RAG）
- 🌐 Web 交互界面
- 💻 命令行交互

## 技术栈
- Java 17
- Spring Boot 3.2.1
- Apache PDFBox / POI
- OkHttp + Gson
- Thymeleaf

## 快速开始
1. 将文档放到项目根目录，命名为 `book.pdf`
2. 运行 `PDFExtractor.main()` 生成 `book.txt`
3. 运行 `Application.main()` 启动 Web 服务
4. 访问 `http://localhost:8080`
