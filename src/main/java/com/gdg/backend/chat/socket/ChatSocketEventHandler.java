package com.gdg.backend.chat.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.ChatMessagePayload;
import com.gdg.backend.chat.service.ChatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSocketEventHandler {

    private final SocketIOServer server;
    private final ChatService chatService;

    @PostConstruct
    public void register() {

        server.addEventListener("joinRoom", Long.class, (client, roomId, ack) -> {
            if (roomId == null) return;
            client.joinRoom(roomId.toString());
            log.info("👤 {} joined room {}", client.getSessionId(), roomId);
        });

        server.addEventListener("leaveRoom", Long.class, (client, roomId, ack) -> {
            if (roomId == null) return;
            client.leaveRoom(roomId.toString());
            log.info("🚪 {} left room {}", client.getSessionId(), roomId);
        });

        server.addEventListener(
                "sendMessage",
                ChatMessagePayload.class,
                this::handleSendMessage
        );
    }

    private void handleSendMessage(
            SocketIOClient client,
            ChatMessagePayload payload,
            Object ack
    ) {
        if (payload == null ||
                payload.getChatRoomId() == null ||
                payload.getSenderUserId() == null ||
                payload.getMessage() == null ||
                payload.getMessage().isBlank()) {
            log.warn("❌ invalid payload {}", payload);
            client.sendEvent("sendError", "Invalid message payload");
            return;
        }

        Long roomId = payload.getChatRoomId();
        Long senderUserId = payload.getSenderUserId();

        try {
            ChatMessageDto saved =
                    chatService.saveSocketMessage(roomId, senderUserId, payload.getMessage());

            server.getRoomOperations(roomId.toString())
                    .sendEvent("newMessage", saved);

            log.info("📤 newMessage room={} sender={}",
                    roomId, saved.getSenderUserId());
        } catch (Exception e) {
            log.error("❌ Failed to send message", e);
            client.sendEvent("sendError", "Failed to send message: " + e.getMessage());
        }
    }
}
