package com.gdg.backend.chat.controller;

import com.gdg.backend.chat.dto.ChatRoomDetailDto;
import com.gdg.backend.chat.dto.ChatRoomListDto;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import com.gdg.backend.chat.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/rooms")
    public List<ChatRoomListDto> getRooms(@RequestParam Long userId,
                                          @RequestParam(defaultValue = "ALL") ChatRoomStatus filter) {
        return chatService.getChatRooms(userId, filter);
    }

    @GetMapping("/rooms/{roomId}")
    public ChatRoomDetailDto getRoomDetail(@PathVariable String roomId) {
        return chatService.getChatRoomDetail(roomId);
    }
}
