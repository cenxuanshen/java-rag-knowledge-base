package main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MarkdownExtractor {

    public static String extractText(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return content
                .replaceAll("^#+\\s*", "")
                .replaceAll("\\*\\*|\\*|__|_", "")
                .replaceAll("-\\s*", "")
                .replaceAll("```.*?```", "")
                .replaceAll("`", "");
    }

    public static void main(String[] args) {
        try {
            String text = extractText("book.md");
            System.out.println("✅ 抽取完成，共 " + text.length() + " 个字符");

            FileWriter writer = new FileWriter("book.txt");
            writer.write(text);
            writer.close();
            System.out.println("💾 已保存到 book.txt");

        } catch (IOException e) {
            System.err.println("❌ 读取 Markdown 失败: " + e.getMessage());
        }
    }
}