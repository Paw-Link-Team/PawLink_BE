package com.gdg.backend.chat.controller;

import com.gdg.backend.chat.dto.ChatRoomDetailDto;
import com.gdg.backend.chat.dto.ChatRoomListDto;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import com.gdg.backend.chat.service.ChatService;
import com.gdg.backend.global.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<ChatRoomListDto> getRooms(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                          @RequestParam(defaultValue = "ALL") ChatRoomStatus filter) {
        return chatService.getChatRooms(userPrincipal.userId(), filter);
    }

    @GetMapping("/rooms/{chatRoomId}")
    public ChatRoomDetailDto getRoomDetail(@PathVariable Long chatRoomId) {
        return chatService.getChatRoomDetail(chatRoomId);
    }
}
