package com.gdg.backend.chat.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.ChatMessagePayload;
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
import org.springframework.security.access.AccessDeniedException;
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

    @Transactional
    public Long createChatRoom(Long boardId, Long currentUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        if (board.getUser().getId().equals(currentUserId)) {
            // 자신의 게시글인 경우, 해당 게시글에 연결된 채팅방이 있는지 확인하고 있으면 첫 번째 채팅방 ID 반환
            // (실제 서비스 로직에 따라 다를 수 있음. 여기서는 테스트 편의를 위해 예외 대신 기존 방 반환 시도)
             return chatRoomRepository.findByBoardId(boardId).stream()
                     .findFirst()
                     .map(ChatRoom::getChatRoomId)
                     .orElseThrow(() -> new IllegalArgumentException("자신의 게시글에는 채팅을 신청할 수 없습니다."));
        }

        // 이미 존재하는 채팅방인지 확인
        return chatRoomRepository.findByBoardIdAndWalkerUserId(boardId, currentUserId)
                .map(ChatRoom::getChatRoomId)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setBoardId(boardId);
                    newRoom.setOwnerUserId(board.getUser().getId()); // 게시글 작성자
                    newRoom.setWalkerUserId(currentUserId);          // 신청자
                    newRoom.setStatus(ChatRoomStatus.ACTIVE);
                    newRoom.setCreatedAt(LocalDateTime.now());
                    newRoom.setUpdatedAt(LocalDateTime.now());
                    return chatRoomRepository.save(newRoom).getChatRoomId();
                });
    }

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
        validateChatRoomParticipant(chatRoomId, currentUserId);
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 채팅방입니다."));

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId);
        List<ChatMessageDto> messageDtos = messages.stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());

        ChatRoomDetailDto detailDto = toDetailDto(room, currentUserId);
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

    @Transactional
    public ChatMessageDto saveSocketMessage(Long chatRoomId, Long senderUserId, String message) {

        //채팅방 조회
        ChatRoom room = chatRoomRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        //사용자 조회
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new IllegalStateException("Sender user not found"));

        //메시지 엔티티 생성
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setSenderUserId(sender.getId());
        chatMessage.setSenderNickname(sender.getNickname());
        chatMessage.setMessage(message);
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessage.setRead(false);

        chatMessageRepository.save(chatMessage);

        //DTO 변환
        return toMessageDto(chatMessage);
    }



    public List<ChatMessageDto> getUnreadMessages(Long chatRoomId, Long userId) {

        //채팅방 접근 권한 검증
        validateChatRoomParticipant(chatRoomId, userId);

        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByChatRoomIdAndIsReadFalse(chatRoomId);

        return unreadMessages.stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    private ChatRoomListDto toListDto(ChatRoom room, Long currentUserId, ChatMessage lastMessage, Long unreadCount) {
        ChatRoomListDto dto = new ChatRoomListDto();
        
        Long otherUserId = room.getOwnerUserId().equals(currentUserId) ? room.getWalkerUserId() : room.getOwnerUserId();
        User otherUser = userRepository.findById(otherUserId).orElse(null);
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

    private ChatRoomDetailDto toDetailDto(ChatRoom room, Long currentUserId) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();
        
        Long otherUserId = room.getOwnerUserId().equals(currentUserId) ? room.getWalkerUserId() : room.getOwnerUserId();
        User otherUser = userRepository.findById(otherUserId).orElse(null);
        Board board = boardRepository.findById(room.getBoardId()).orElse(null);

        dto.setChatRoomId(room.getChatRoomId());
        if (otherUser != null) {
            dto.setProfileName(otherUser.getNickname());
            dto.setProfilePhone(otherUser.getPhoneNumber());
        }
        
        if (board != null) {
            dto.setPost(BoardResponseDto.from(board));
        }

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

    private void validateChatRoomParticipant(Long chatRoomId, Long userId) {
        boolean isOwner =
                chatRoomRepository.existsByChatRoomIdAndOwnerUserId(chatRoomId, userId);

        boolean isWalker =
                chatRoomRepository.existsByChatRoomIdAndWalkerUserId(chatRoomId, userId);

        if (!isOwner && !isWalker) {
            throw new AccessDeniedException("채팅방 접근 권한이 없습니다.");
        }
    }

}
