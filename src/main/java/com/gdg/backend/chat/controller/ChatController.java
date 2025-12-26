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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    private final ChatService chatService;

    /* =========================
     * 채팅방 생성
     * ========================= */

    /**
     * 게시글 기준 채팅방 생성 (또는 기존 방 반환)
     */
    @PostMapping("/rooms/by-board/{boardId}")
    public ResponseEntity<ApiResponse<Long>> createRoomByBoard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long boardId
    ) {
        Long userId = principal.userId();
        Long chatRoomId = chatService.createChatRoom(boardId, userId);
        return ApiResponse.success(SuccessCode.CREATED, chatRoomId);
    }

    /* =========================
     * 채팅방 목록
     * ========================= */

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomListDto>>> getRooms(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "ALL") ChatRoomStatus status
    ) {
        Long userId = principal.userId();
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                chatService.getChatRooms(userId, status)
        );
    }

    /* =========================
     * 채팅방 상세
     * ========================= */

    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatRoomDetailDto>> getRoomDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long chatRoomId
    ) {
        Long userId = principal.userId();
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                chatService.getChatRoomDetail(chatRoomId, userId)
        );
    }

    /* =========================
     * 안 읽은 메시지 조회
     * ========================= */

    @GetMapping("/rooms/{chatRoomId}/messages/unread")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getUnreadMessages(
            @PathVariable Long chatRoomId
    ) {
        return ApiResponse.success(
                SuccessCode.READ_SUCCESS,
                chatService.getUnreadMessages(chatRoomId)
        );
    }
}
