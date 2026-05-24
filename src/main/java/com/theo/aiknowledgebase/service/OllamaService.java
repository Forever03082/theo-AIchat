package com.theo.aiknowledgebase.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final WebClient webClient = WebClient.create("http://localhost:11434");

    public String chat(List<Map<String, Object>> messages) {
        Map<String, Object> request = Map.of(
                "model", "qwen3:8b",
                "messages", messages,
                "stream", false
        );
        return webClient.post()
                .uri("/api/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> (String) ((Map<String, Object>) res.get("message")).get("content"))
                .block();
    }

    public Flux<String> chatStream(List<Map<String, Object>> messages) {

        Map<String, Object> request = Map.of(
                "model", "qwen3:8b",
                "messages", messages,
                "stream", true
        );

        return webClient.post()
                .uri("/api/chat")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(Map.class)
                .filter(res -> res.get("message") != null && ((Map<String, Object>) res.get("message")).get("content") != null)
                .map(res -> (String) ((Map<String, Object>) res.get("message")).get("content"));
    }
}