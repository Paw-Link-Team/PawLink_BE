package com.gdg.backend.chat.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.ChatRoomDetailDto;
import com.gdg.backend.chat.dto.ChatRoomListDto;
import com.gdg.backend.chat.entity.ChatMessage;
import com.gdg.backend.chat.entity.ChatRoom;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import com.gdg.backend.chat.repository.ChatMessageRepository;
import com.gdg.backend.chat.repository.ChatRoomRepository;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public List<ChatRoomListDto> getChatRooms(Long currentUserId, ChatRoomStatus filter) {
        List<ChatRoom> rooms = switch (filter) {
            case UNREAD -> chatRoomRepository.findUnreadRoomsByUser(currentUserId);
            case COMPLETED ->
                    chatRoomRepository.findByOwnerUserIdOrWalkerUserIdAndStatus(currentUserId, currentUserId, ChatRoomStatus.COMPLETED);
            default -> chatRoomRepository.findByOwnerUserIdOrWalkerUserId(currentUserId, currentUserId);
        };

        Map<Long, ChatMessage> latestMessages = chatMessageRepository.findLatestMessages().stream()
                .collect(Collectors.toMap(ChatMessage::getChatRoomId, Function.identity()));
        Map<Long, Long> unreadCounts = chatMessageRepository.countUnreadMessagesByRoom().stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        return rooms.stream()
                .map(room -> toListDto(room, currentUserId, latestMessages.get(room.getChatRoomId()), unreadCounts.getOrDefault(room.getChatRoomId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId, Long currentUserId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("채팅방을 찾을 수 없습니다."));

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
        List<ChatMessageDto> messageDtos = messages.stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());

        ChatRoomDetailDto detailDto = toDetailDto(room, currentUserId, null);
        detailDto.setMessages(messageDtos);

        return detailDto;
    }

    @Transactional
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

    public List<ChatMessageDto> getUnreadMessages(Long chatRoomId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndIsReadFalse(chatRoomId);
        return unreadMessages.stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    private ChatRoomListDto toListDto(ChatRoom room, Long currentUserId, ChatMessage lastMessage, Long unreadCount) {
        ChatRoomListDto dto = new ChatRoomListDto();
        
        // 상대방 정보 조회
        Long otherUserId = room.getOwnerUserId().equals(currentUserId) ? room.getWalkerUserId() : room.getOwnerUserId();
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        // 게시글 정보 조회
        Board board = boardRepository.findById(room.getBoardId()).orElse(null);

        dto.setChatRoomId(room.getChatRoomId());
        dto.setStatus(room.getStatus().name());
        if (otherUser != null) {
            dto.setProfileImgUrl(otherUser.getProfileImageUrl());
        }
        if (board != null) {
            dto.setTitle(board.getTitle());
        }

        if (lastMessage != null) {
            dto.setLastMessage(lastMessage.getMessage());
            dto.setLastSentAt(lastMessage.getSentAt());
        }
        dto.setUnreadCount(unreadCount.intValue());

        return dto;
    }

    private ChatRoomDetailDto toDetailDto(ChatRoom room, Long currentUserId, Object appointment) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();
        
        // 상대방 정보 조회
        Long otherUserId = room.getOwnerUserId().equals(currentUserId) ? room.getWalkerUserId() : room.getOwnerUserId();
        User otherUser = userRepository.findById(otherUserId).orElse(null);

        // 게시글 정보 조회
        Board board = boardRepository.findById(room.getBoardId()).orElse(null);

        dto.setChatRoomId(room.getChatRoomId());
        if (otherUser != null) {
            dto.setProfileName(otherUser.getNickname());
            // User 엔티티에 전화번호 필드가 없으므로 주석 처리
            // dto.setProfilePhone(otherUser.getPhoneNumber());
        }
        
        if (board != null) {
            dto.setPost(BoardResponseDto.from(board));
        }

        // appointment 매핑은 기존 로직 유지
        // dto.setAppointment(appointment);

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
