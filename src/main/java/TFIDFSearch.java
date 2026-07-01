package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TFIDFSearch {

    private List<String> chunks;
    private List<Map<String, Integer>> chunkTermFreq;
    private Map<String, Double> idfScores;

    public TFIDFSearch(String filePath, int chunkSize) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        chunks = new ArrayList<>();
        for (int i = 0; i < content.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, content.length());
            chunks.add(content.substring(i, end));
        }
        System.out.println("📦 切分成 " + chunks.size() + " 个文本块");

        buildIndex();
    }

    private void buildIndex() {
        chunkTermFreq = new ArrayList<>();
        Map<String, Integer> docFreq = new HashMap<>();

        for (String chunk : chunks) {
            Map<String, Integer> tf = getTermFreq(chunk);
            chunkTermFreq.add(tf);
            for (String term : tf.keySet()) {
                docFreq.put(term, docFreq.getOrDefault(term, 0) + 1);
            }
        }

        idfScores = new HashMap<>();
        int totalDocs = chunks.size();
        for (String term : docFreq.keySet()) {
            double idf = Math.log((double) totalDocs / (1 + docFreq.get(term)));
            idfScores.put(term, idf);
        }
    }

    private Map<String, Integer> getTermFreq(String text) {
        String[] words = text.split("[，。！？、；：\"\"''（）\n\r\t ]+");
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            if (word.length() > 1) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }
        return freq;
    }

    public List<String> search(String query, int topK) {
        Map<String, Integer> queryTF = getTermFreq(query);
        Map<String, Double> queryTFIDF = new HashMap<>();
        for (String term : queryTF.keySet()) {
            double tf = queryTF.get(term);
            double idf = idfScores.getOrDefault(term, 0.0);
            queryTFIDF.put(term, tf * idf);
        }

        List<ChunkScore> scores = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            double score = cosineSimilarity(queryTFIDF, chunkTermFreq.get(i));
            if (score > 0) {
                scores.add(new ChunkScore(i, score));
            }
        }

        scores.sort((a, b) -> Double.compare(b.score, a.score));
        List<String> results = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, scores.size()); i++) {
            results.add(chunks.get(scores.get(i).index));
        }
        return results;
    }

    private double cosineSimilarity(Map<String, Double> queryTFIDF, Map<String, Integer> docTF) {
        double dotProduct = 0;
        double queryNorm = 0;
        double docNorm = 0;

        for (String term : queryTFIDF.keySet()) {
            double qWeight = queryTFIDF.get(term);
            double dWeight = docTF.getOrDefault(term, 0) * idfScores.getOrDefault(term, 0.0);
            dotProduct += qWeight * dWeight;
            queryNorm += qWeight * qWeight;
        }

        for (String term : docTF.keySet()) {
            double dWeight = docTF.get(term) * idfScores.getOrDefault(term, 0.0);
            docNorm += dWeight * dWeight;
        }

        if (queryNorm == 0 || docNorm == 0) return 0;
        return dotProduct / (Math.sqrt(queryNorm) * Math.sqrt(docNorm));
    }

    private static class ChunkScore {
        int index;
        double score;
        ChunkScore(int index, double score) {
            this.index = index;
            this.score = score;
        }
    }

    // 测试
    public static void main(String[] args) {
        try {
            // 1. 加载知识库
            TFIDFSearch search = new TFIDFSearch("book.txt", 500);

            // 2. 输入查询
            String query = "改革开放"; // 你可以把 "TCP" 换成其他词试试
            System.out.println("🔍 搜索: " + query);

            // 3. 执行搜索
            List<String> results = search.search(query, 3);

            // 4. 打印结果
            if (results.isEmpty()) {
                System.out.println("❌ 没有找到相关内容。");
            } else {
                System.out.println("📄 找到 " + results.size() + " 个相关段落：");
                for (int i = 0; i < results.size(); i++) {
                    String chunk = results.get(i);
                    // 打印每个段落的前 100 个字，避免刷屏
                    String preview = chunk.length() > 100 ? chunk.substring(0, 100) + "..." : chunk;
                    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    System.out.println("第 " + (i + 1) + " 段 (" + chunk.length() + " 字):");
                    System.out.println(preview);
                }
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }

        } catch (IOException e) {
            System.err.println("❌ 加载知识库失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}