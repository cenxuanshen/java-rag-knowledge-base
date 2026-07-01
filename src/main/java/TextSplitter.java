package main;

import java.util.ArrayList;
import java.util.List;

public class TextSplitter {

    /**
     * 按固定字数切分文本
     * @param text 原始文本
     * @param chunkSize 每块字数（建议 300-500）
     * @return 文本块列表
     */
    public static List<String> splitBySize(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();

        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(i + chunkSize, length);
            chunks.add(text.substring(i, end));
        }

        return chunks;
    }

    /**
     * 按章节切分文本（按 "第X章" 或 "第X卷" 等关键词）
     * @param text 原始文本
     * @return 章节块列表
     */
    public static List<String> splitByChapter(String text) {
        // 按 "第" 和 "章" 分割（简单实现）
        String[] parts = text.split("第[一二三四五六七八九十百千万]+章");
        List<String> chunks = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                chunks.add(trimmed);
            }
        }
        return chunks;
    }

    // 测试方法
    public static void main(String[] args) {
        // 先把之前从PDF里抽出的文本复制过来测试
        String sampleText = "第一卷\n全世界无产者，联合起来！\n江泽民文选\n..."; // 可以粘贴更多

        System.out.println("📋 原始文本长度: " + sampleText.length());

        // 按字数切分（每块300字）
        List<String> chunks = splitBySize(sampleText, 300);
        System.out.println("📦 切分成 " + chunks.size() + " 块");

        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("第" + (i+1) + "块 (" + chunks.get(i).length() + "字):");
            System.out.println(chunks.get(i).substring(0, Math.min(chunks.get(i).length(), 50)) + "...");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }
}