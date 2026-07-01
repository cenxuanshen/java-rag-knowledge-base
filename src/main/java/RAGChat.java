package main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class RAGChat {

    private main.TFIDFSearch searchEngine;
    private OkHttpClient httpClient;
    private Gson gson;
    private String apiKey;

    public RAGChat(String bookPath, int chunkSize, String apiKey) throws IOException {
        this.searchEngine = new main.TFIDFSearch(bookPath, chunkSize);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.apiKey = apiKey;
        System.out.println("🤖 RAG 问答系统初始化完成！");
    }

    /**
     * 基于知识库回答问题
     */
    public String ask(String question) throws IOException {
        // 1. 检索相关段落
        List<String> relevantChunks = searchEngine.search(question, 3);
        if (relevantChunks.isEmpty()) {
            return "❌ 在知识库中未找到相关内容，请换个问题试试。";
        }

        // 2. 拼接上下文
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < relevantChunks.size(); i++) {
            context.append("【参考").append(i+1).append("】\n");
            context.append(relevantChunks.get(i));
            context.append("\n\n");
        }

        // 3. 构造 Prompt
        String prompt = "请根据以下参考材料回答问题。如果参考材料中没有相关信息，请明确告知用户。\n\n"
                + "【参考材料】\n"
                + context.toString()
                + "【问题】\n"
                + question + "\n\n"
                + "请用简洁、准确的语言回答。";

        // 4. 调用 AI
        return callAI(prompt);
    }

    /**
     * 调用智谱AI API
     */
    private String callAI(String prompt) throws IOException {

        // 构建请求体 JSON
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "glm-4-flash");
        requestBody.add("messages", messages);

        String json = gson.toJson(requestBody);

        // 构建 HTTP 请求
        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        // 发送请求
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "❌ API 调用失败: " + response.code();
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();
        }
    }

    /**
     * 启动交互式问答
     */
    public void startChat() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📚 本地知识库问答系统已启动");
        System.out.println("💡 输入问题按回车，输入 'exit' 退出");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        while (true) {
            System.out.print("\n👤 你：");
            String question = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(question)) {
                System.out.println("👋 再见！");
                break;
            }

            if (question.isEmpty()) {
                continue;
            }

            try {
                System.out.println("⏳ AI 正在思考...");
                long startTime = System.currentTimeMillis();
                String answer = ask(question);
                long endTime = System.currentTimeMillis();

                System.out.println("🤖 AI 回答：");
                System.out.println(answer);
                System.out.println("⏱️ 耗时: " + (endTime - startTime) + " 毫秒");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            } catch (Exception e) {
                System.err.println("❌ 出错: " + e.getMessage());
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        try {
            // ⚠️ 替换成你的完整 API Key
            String apiKey = "你的智谱API Key";

            RAGChat chat = new RAGChat("book.txt", 500, apiKey);
            chat.startChat();

        } catch (IOException e) {
            System.err.println("❌ 初始化失败: " + e.getMessage());
        }
    }
}