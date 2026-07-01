package main;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PDFExtractor {

    public static String extractText(String filePath) throws IOException {
        File file = new File(filePath);
        PDDocument document = PDDocument.load(file);
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } finally {
            document.close();
        }
    }

    public static void main(String[] args) {
        try {
            // ⚠️ 改成你的PDF文件名
            String text = extractText("book.pdf");
            System.out.println("✅ 抽取完成，共 " + text.length() + " 个字符");

            // 保存到 book.txt 文件
            FileWriter writer = new FileWriter("book.txt");
            writer.write(text);
            writer.close();
            System.out.println("💾 已保存到 book.txt");

        } catch (IOException e) {
            System.err.println("❌ 读取PDF失败: " + e.getMessage());
        }
    }
}