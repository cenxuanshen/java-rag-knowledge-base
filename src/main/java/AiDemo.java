package main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class AiDemo {
    public static void main(String[] args) {
        String apiKey = "你的智谱API Key";

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", "用一句话解释什么是AI");

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "glm-4-flash");
        requestBody.add("messages", messages);

        String json = gson.toJson(requestBody);

        Request request = new Request.Builder()
                .url("https://open.bigmodel.cn/api/paas/v4/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("请求失败: " + response.code());
                System.err.println(response.body().string());
                return;
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            String content = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            System.out.println("🤖 AI 回复：");
            System.out.println(content);

        } catch (IOException e) {
            System.err.println("❌ 网络请求失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}