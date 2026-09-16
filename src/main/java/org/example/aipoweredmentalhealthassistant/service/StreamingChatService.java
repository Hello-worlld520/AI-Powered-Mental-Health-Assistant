package org.example.aipoweredmentalhealthassistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.aipoweredmentalhealthassistant.DTO.command.StreamChatCommandDTO;
import org.example.aipoweredmentalhealthassistant.config.SiliconFlowProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class StreamingChatService {

    private final RestClient siliconFlowRestClient;
    private final SiliconFlowProperties siliconFlowProperties;
    private final ConsultationMessageService consultationMessageService;
    private final ObjectMapper objectMapper;

    public SseEmitter stream(String username, Long sessionId, StreamChatCommandDTO commandDTO) {
        validateConfiguration();
        consultationMessageService.verifySession(username, sessionId);

        String userMessage = commandDTO.getMessage().trim();
        consultationMessageService.saveUserMessage(sessionId, userMessage);

        SseEmitter emitter = new SseEmitter(120_000L);
        CompletableFuture.runAsync(() -> streamFromSiliconFlow(emitter, sessionId, userMessage));
        return emitter;
    }

    private void streamFromSiliconFlow(SseEmitter emitter, Long sessionId, String userMessage) {
        StringBuilder assistantMessage = new StringBuilder();
        try {
            Map<String, Object> request = Map.of(
                    "model", siliconFlowProperties.getModel(),
                    "messages", List.of(Map.of("role", "user", "content", userMessage)),
                    "max_tokens", siliconFlowProperties.getMaxTokens(),
                    "stream", true
            );

            siliconFlowRestClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .body(request)
                    .exchange((clientRequest, clientResponse) -> {
                        if (!clientResponse.getStatusCode().is2xxSuccessful()) {
                            throw new IllegalStateException("硅基流动服务调用失败");
                        }
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                                clientResponse.getBody(), StandardCharsets.UTF_8))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (!line.startsWith("data:")) {
                                    continue;
                                }
                                String data = line.substring(5).trim();
                                if (data.isEmpty() || "[DONE]".equals(data)) {
                                    continue;
                                }
                                String text = extractText(data);
                                if (!text.isEmpty()) {
                                    assistantMessage.append(text);
                                    sendEvent(emitter, "message", text);
                                }
                            }
                        }
                        return null;
                    });

            if (!assistantMessage.isEmpty()) {
                consultationMessageService.saveAiMessage(
                        sessionId,
                        assistantMessage.toString(),
                        siliconFlowProperties.getModel()
                );
            }
            sendEvent(emitter, "done", "[DONE]");
            emitter.complete();
        } catch (Exception e) {
            sendEventSafely(emitter, "error", "AI服务暂时不可用");
            emitter.complete();
        }
    }

    private String extractText(String data) throws IOException {
        JsonNode root = objectMapper.readTree(data);
        JsonNode content = root.path("choices").path(0).path("delta").path("content");
        return content.isTextual() ? content.asText() : "";
    }

    private void validateConfiguration() {
        if (siliconFlowProperties.getApiKey() == null || siliconFlowProperties.getApiKey().isBlank()) {
            throw new IllegalStateException("未配置 SILICONFLOW_API_KEY");
        }
        if (siliconFlowProperties.getModel() == null || siliconFlowProperties.getModel().isBlank()) {
            throw new IllegalStateException("未配置硅基流动模型");
        }
        if (siliconFlowProperties.getMaxTokens() == null || siliconFlowProperties.getMaxTokens() <= 0) {
            throw new IllegalStateException("SILICONFLOW_MAX_TOKENS 必须为正数");
        }
    }

    private void sendEvent(SseEmitter emitter, String eventName, String data) throws IOException {
        emitter.send(SseEmitter.event().name(eventName).data(data));
    }

    private void sendEventSafely(SseEmitter emitter, String eventName, String data) {
        try {
            sendEvent(emitter, eventName, data);
        } catch (IOException ignored) {
        }
    }
}
