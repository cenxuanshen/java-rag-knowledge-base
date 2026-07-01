package main;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class DocxExtractor {

    public static String extractText(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    public static void main(String[] args) {
        try {
            // 改成你的 Word 文件名
            String text = extractText("book.docx");
            System.out.println("✅ 抽取完成，共 " + text.length() + " 个字符");

            // 统一保存成 book.txt，后面的 RAG 完全不用改
            FileWriter writer = new FileWriter("book.txt");
            writer.write(text);
            writer.close();
            System.out.println("💾 已保存到 book.txt");

        } catch (IOException e) {
            System.err.println("❌ 读取 Word 失败: " + e.getMessage());
            System.err.println("💡 请确认文件是 .docx 格式（不支持 .doc）");
        }
    }
}