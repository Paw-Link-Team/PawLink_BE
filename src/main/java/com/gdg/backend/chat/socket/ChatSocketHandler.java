package com.gdg.backend.chat.socket;

import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.service.ChatService;
import org.springframework.stereotype.Component;

@Component
public class ChatSocketHandler<SocketIOServer> {

    private final SocketIOServer server;
    private final ChatService chatService;

    public ChatSocketHandler(SocketIOServer server, ChatService chatService) {
        this.server = server;
        this.chatService = chatService;
        registerEvents();
    }

    private void registerEvents() {
        server.addEventListener("joinRoom", String.class, (client, roomId, ackSender) -> {
            client.joinRoom(roomId);
        });

        server.addEventListener("leaveRoom", String.class, (client, roomId, ackSender) -> {
            client.leaveRoom(roomId);
        });

        server.addEventListener("sendMessage", ChatMessageDto.class, (client, messageDto, ackSender) -> {
            ChatMessageDto saved = chatService.saveMessage(messageDto);
            server.getRoomOperations(saved.getChatRoomId()).sendEvent("newMessage", saved);
        });
    }
}
