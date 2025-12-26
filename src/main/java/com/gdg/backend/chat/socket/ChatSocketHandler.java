package com.gdg.backend.chat.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.SendMessagePayload;
import com.gdg.backend.chat.service.ChatService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatSocketHandler {

    private final SocketIOServer server;
    private final ChatService chatService;

    public ChatSocketHandler(SocketIOServer server, ChatService chatService) {
        this.server = server;
        this.chatService = chatService;
    }

    /**
     * 이벤트만 등록 (서버 start/stop은 Config에서 관리)
     */
    @PostConstruct
    public void registerEvents() {

        // 연결
        server.addConnectListener(client ->
                log.info("Client connected: {}", client.getSessionId())
        );

        // 연결 해제
        server.addDisconnectListener(client ->
                log.info("Client disconnected: {}", client.getSessionId())
        );

        // 방 입장
        server.addEventListener("joinRoom", Long.class, (client, chatRoomId, ack) -> {
            client.joinRoom(chatRoomId.toString());
            log.info("Client {} joined room {}", client.getSessionId(), chatRoomId);
        });

        // 방 퇴장
        server.addEventListener("leaveRoom", Long.class, (client, chatRoomId, ack) -> {
            client.leaveRoom(chatRoomId.toString());
            log.info("Client {} left room {}", client.getSessionId(), chatRoomId);
        });

        // 메시지 전송
        server.addEventListener("sendMessage", SendMessagePayload.class,
                (client, payload, ack) -> {

                    log.info("Received message in room {}: {}",
                            payload.getChatRoomId(),
                            payload.getMessage()
                    );

                    // 🔥 sender 정보는 서버에서 채움 (임시: Service에서 처리)
                    ChatMessageDto saved = chatService.saveMessage(
                            ChatMessageDto.of(
                                    payload.getChatRoomId(),
                                    null,          // senderUserId → 이후 인증 연계
                                    null,          // senderNickname → 이후 인증 연계
                                    payload.getMessage()
                            )
                    );

                    // 해당 방 전체 브로드캐스트
                    server.getRoomOperations(payload.getChatRoomId().toString())
                            .sendEvent("newMessage", saved);
                }
        );

        log.info("Socket.IO events registered");
    }

    @PostConstruct
    public void startServer() {
        registerEvents();
        server.start(); // ✅ 여기서 단 한 번
    }
}
