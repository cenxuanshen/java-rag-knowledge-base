package main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final RAGChat ragChat;

    public WebController() throws Exception {
        String apiKey = "你的智谱API Key";
        this.ragChat = new RAGChat("book.txt", 500, apiKey);
        System.out.println("🌐 Web 服务已启动！");
        System.out.println("👉 访问地址: http://localhost:8080");
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestParam("question") String question, Model model) {
        try {
            String answer = ragChat.ask(question);
            model.addAttribute("question", question);
            model.addAttribute("answer", answer);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("answer", "❌ 出错了: " + e.getMessage());
        }
        return "index";
    }
}