package com.gdg.backend.chat.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.service.ChatService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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

    @PostConstruct
    public void startServer() {
        registerEvents();
        server.start();
        log.info("Socket.IO server started");
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket.IO server stopped");
    }

    private void registerEvents() {
        // 연결 리스너
        server.addConnectListener(client -> {
            log.info("Client connected: {}", client.getSessionId());
        });

        // 연결 해제 리스너
        server.addDisconnectListener(client -> {
            log.info("Client disconnected: {}", client.getSessionId());
        });

        // 방 입장 이벤트
        server.addEventListener("joinRoom", String.class, (client, roomId, ackSender) -> {
            client.joinRoom(roomId);
            log.info("Client {} joined room {}", client.getSessionId(), roomId);
        });

        // 방 퇴장 이벤트
        server.addEventListener("leaveRoom", String.class, (client, roomId, ackSender) -> {
            client.leaveRoom(roomId);
            log.info("Client {} left room {}", client.getSessionId(), roomId);
        });

        // 메시지 전송 이벤트
        server.addEventListener("sendMessage", ChatMessageDto.class, (client, messageDto, ackSender) -> {
            log.info("Received message from {}: {}", messageDto.getSenderNickname(), messageDto.getMessage());
            
            // DB 저장
            ChatMessageDto saved = chatService.saveMessage(messageDto);
            
            // 해당 방에 있는 모든 클라이언트에게 'newMessage' 이벤트 전송
            server.getRoomOperations(saved.getChatRoomId()).sendEvent("newMessage", saved);
        });
    }
}
