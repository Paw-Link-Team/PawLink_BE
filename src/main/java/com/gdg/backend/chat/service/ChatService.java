package com.gdg.backend.chat.service;

import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.ChatRoomDetailDto;
import com.gdg.backend.chat.dto.ChatRoomListDto;
import com.gdg.backend.chat.entity.ChatMessage;
import com.gdg.backend.chat.entity.ChatRoom;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import com.gdg.backend.chat.repository.ChatMessageRepository;
import com.gdg.backend.chat.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatRoomListDto> getChatRooms(Long userId, ChatRoomStatus filter) {
        List<ChatRoom> rooms;
        switch (filter) {
            case UNREAD:
                rooms = chatRoomRepository.findUnreadRoomsByUser(userId);
                break;
            case COMPLETED:
                rooms = chatRoomRepository.findByOwnerUserIdOrWalkerUserIdAndStatus(userId, userId, ChatRoomStatus.COMPLETED);
                break;
            default:
                rooms = chatRoomRepository.findByOwnerUserIdOrWalkerUserId(userId, userId);
        }
        return rooms.stream().map(this::toListDto).collect(Collectors.toList());
    }

    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId).orElseThrow();
        // post, appointment 조회 생략 (추가 구현 필요)
        return toDetailDto(room, null, null);
    }

    public ChatMessageDto saveMessage(ChatMessageDto dto) {
        ChatMessage entity = new ChatMessage();
        entity.setChatRoomId(dto.getChatRoomId());
        entity.setSenderUserId(dto.getSenderUserId());
        entity.setSenderNickname(dto.getSenderNickname());
        entity.setMessage(dto.getMessage());
        entity.setSentAt(LocalDateTime.now());
        entity.setRead(false);
        ChatMessage saved = chatMessageRepository.save(entity);
        return toMessageDto(saved);
    }

    private ChatRoomListDto toListDto(ChatRoom room) {
        ChatRoomListDto dto = new ChatRoomListDto();
        dto.setChatRoomId(room.getChatRoomId());
        // 프로필 사진, 제목, 최근 메시지, 마지막 전송 시간, 안읽음개수 세팅은 커스텀 로직 필요
        dto.setStatus(room.getStatus().name());
        return dto;
    }

    private ChatRoomDetailDto toDetailDto(ChatRoom room, Object post, Object appointment) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();
        dto.setChatRoomId(room.getChatRoomId());
        // profileName, profilePhone, post, appointment 매핑 필요
        return dto;
    }

    private ChatMessageDto toMessageDto(ChatMessage entity) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setChatRoomId(entity.getChatRoomId());
        dto.setSenderUserId(entity.getSenderUserId());
        dto.setSenderNickname(entity.getSenderNickname());
        dto.setMessage(entity.getMessage());
        dto.setSentAt(entity.getSentAt());
        dto.setRead(entity.isRead());
        return dto;
    }
}
