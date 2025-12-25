package com.gdg.backend.chat.controller;

import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.ChatRoomDetailDto;
import com.gdg.backend.chat.dto.ChatRoomListDto;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import com.gdg.backend.chat.service.ChatService;
import com.gdg.backend.global.code.SuccessCode;
import com.gdg.backend.global.response.ApiResponse;
import com.gdg.backend.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<Long>> createRoom(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @RequestParam Long boardId) {
        return ApiResponse.success(SuccessCode.CREATED, chatService.createChatRoom(boardId, userPrincipal.userId()));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomListDto>>> getRooms(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                          @RequestParam(defaultValue = "ALL") ChatRoomStatus filter) {
        return ApiResponse.success(SuccessCode.READ_SUCCESS, chatService.getChatRooms(userPrincipal.userId(), filter));
    }

    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomDetailDto>> getRoomDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable Long chatRoomId) {
        return ApiResponse.success(SuccessCode.READ_SUCCESS, chatService.getChatRoomDetail(chatRoomId, userPrincipal.userId()));
    }

    @GetMapping("/rooms/{chatRoomId}/unread")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getUnreadMessages(@PathVariable Long chatRoomId) {
        return ApiResponse.success(SuccessCode.READ_SUCCESS, chatService.getUnreadMessages(chatRoomId));
    }
}
