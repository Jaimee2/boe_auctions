package com.example.boe_auction.auction_web_scraping.ollama.controller;

import com.example.boe_auction.auction_web_scraping.ollama.service.OllamaService;
import lombok.AllArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ollama")
@AllArgsConstructor
public class ChatController {

    private final OllamaChatModel chatModel;
    private OllamaService ollamaService;

    @GetMapping
    public String chatWithOllama(String prompt) {
        return chatModel.call(prompt);
    }

    @GetMapping("/improve-address")
    public String improveAddressForGeoCoding(String prompt) {
        return ollamaService.improveAddressForGeoDataApi(prompt);
    }
}
