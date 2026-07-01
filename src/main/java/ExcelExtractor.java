package main;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ExcelExtractor {

    public static String extractText(String filePath) throws IOException {
        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                text.append("【").append(sheet.getSheetName()).append("】\n");
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String cellValue = getCellValue(cell);
                        if (!cellValue.isEmpty()) {
                            text.append(cellValue).append(" ");
                        }
                    }
                    text.append("\n");
                }
                text.append("\n");
            }
        }
        return text.toString();
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    public static void main(String[] args) {
        try {
            String text = extractText("book.xlsx");
            System.out.println("✅ 抽取完成，共 " + text.length() + " 个字符");

            FileWriter writer = new FileWriter("book.txt");
            writer.write(text);
            writer.close();
            System.out.println("💾 已保存到 book.txt");

        } catch (IOException e) {
            System.err.println("❌ 读取 Excel 失败: " + e.getMessage());
        }
    }
}