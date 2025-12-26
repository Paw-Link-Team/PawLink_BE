package com.gdg.backend.chat.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.chat.dto.ChatMessageDto;
import com.gdg.backend.chat.dto.ChatRoomDetailDto;
import com.gdg.backend.chat.dto.ChatRoomListDto;
import com.gdg.backend.chat.entity.ChatMessage;
import com.gdg.backend.chat.entity.ChatMessageType;
import com.gdg.backend.chat.entity.ChatRoom;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import com.gdg.backend.chat.repository.ChatMessageRepository;
import com.gdg.backend.chat.repository.ChatRoomRepository;
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

    /* =========================
     * 채팅방 생성
     * ========================= */
    @Transactional
    public Long createChatRoom(Long boardId, Long currentUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));

        if (board.getUser().getId().equals(currentUserId)) {
            return chatRoomRepository.findByBoardId(boardId).stream()
                    .findFirst()
                    .map(ChatRoom::getChatRoomId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("자신의 게시글에는 채팅을 생성할 수 없습니다."));
        }

        return chatRoomRepository.findByBoardIdAndWalkerUserId(boardId, currentUserId)
                .map(ChatRoom::getChatRoomId)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom();
                    room.setBoardId(boardId);
                    room.setOwnerUserId(board.getUser().getId());
                    room.setWalkerUserId(currentUserId);
                    room.setStatus(ChatRoomStatus.ACTIVE);
                    room.setCreatedAt(LocalDateTime.now());
                    room.setUpdatedAt(LocalDateTime.now());
                    return chatRoomRepository.save(room).getChatRoomId();
                });
    }


    /* =========================
     * 채팅방 목록
     * ========================= */
    public List<ChatRoomListDto> getChatRooms(Long currentUserId, ChatRoomStatus filter) {
        List<ChatRoom> rooms = loadChatRoomsByFilter(currentUserId, filter);

        Map<Long, ChatMessage> lastMessages =
                chatMessageRepository.findLatestMessages().stream()
                        .collect(Collectors.toMap(
                                ChatMessage::getChatRoomId,
                                Function.identity()
                        ));

        Map<Long, Long> unreadCounts =
                chatMessageRepository.countUnreadMessagesByRoom().stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> (Long) row[1]
                        ));

        return rooms.stream()
                .map(room -> mapToListDto(
                        room,
                        currentUserId,
                        lastMessages.get(room.getChatRoomId()),
                        unreadCounts.getOrDefault(room.getChatRoomId(), 0L)
                ))
                .toList();
    }

    private List<ChatRoom> loadChatRoomsByFilter(Long userId, ChatRoomStatus filter) {
        return switch (filter) {
            case UNREAD -> chatRoomRepository.findUnreadRoomsByUser(userId);
            case COMPLETED ->
                    chatRoomRepository.findByOwnerUserIdOrWalkerUserIdAndStatus(
                            userId, userId, ChatRoomStatus.COMPLETED
                    );
            default ->
                    chatRoomRepository.findByOwnerUserIdOrWalkerUserId(userId, userId);
        };
    }

    /* =========================
     * 채팅방 상세
     * ========================= */
    @Transactional
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId, Long currentUserId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 채팅방입니다."));

        ChatRoomDetailDto detail = mapToDetailDto(room, currentUserId);

        List<ChatMessageDto> messages =
                chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatRoomId).stream()
                        .map(this::mapToMessageDto)
                        .toList();

        detail.setMessages(messages);
        return detail;
    }

    /* =========================
     * 메시지 저장
     * ========================= */
    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto dto) {
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(dto.getChatRoomId());
        message.setSenderUserId(dto.getSenderUserId());
        message.setSenderNickname(dto.getSenderNickname());
        message.setMessage(dto.getMessage());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        return mapToMessageDto(chatMessageRepository.save(message));
    }

    /* =========================
     * 안 읽은 메시지 조회
     * ========================= */
    @Transactional
    public List<ChatMessageDto> getUnreadMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdAndIsReadFalse(chatRoomId).stream()
                .map(this::mapToMessageDto)
                .toList();
    }

    /* =========================
     * DTO 매핑
     * ========================= */
    private ChatRoomListDto mapToListDto(
            ChatRoom room,
            Long currentUserId,
            ChatMessage lastMessage,
            Long unreadCount
    ) {
        ChatRoomListDto dto = new ChatRoomListDto();

        Long otherUserId = room.getOwnerUserId().equals(currentUserId)
                ? room.getWalkerUserId()
                : room.getOwnerUserId();

        userRepository.findById(otherUserId).ifPresent(user ->
                dto.setProfileImgUrl(user.getProfileImageUrl())
        );

        boardRepository.findById(room.getBoardId()).ifPresent(board ->
                dto.setTitle(board.getTitle())
        );

        dto.setChatRoomId(room.getChatRoomId());
        dto.setStatus(room.getStatus().name());
        dto.setUnreadCount(unreadCount.intValue());

        if (lastMessage != null) {
            dto.setLastMessage(lastMessage.getMessage());
            dto.setLastSentAt(lastMessage.getSentAt());
        }

        return dto;
    }

    private ChatRoomDetailDto mapToDetailDto(ChatRoom room, Long currentUserId) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();

        Long otherUserId = room.getOwnerUserId().equals(currentUserId)
                ? room.getWalkerUserId()
                : room.getOwnerUserId();

        userRepository.findById(otherUserId).ifPresent(user -> {
            dto.setProfileName(user.getNickname());
            dto.setProfilePhone(user.getPhoneNumber());
        });

        boardRepository.findById(room.getBoardId()).ifPresent(board ->
                dto.setPost(BoardResponseDto.from(board))
        );

        dto.setChatRoomId(room.getChatRoomId());
        return dto;
    }

    private ChatMessageDto mapToMessageDto(ChatMessage entity) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setChatRoomId(entity.getChatRoomId());
        dto.setSenderUserId(entity.getSenderUserId());
        dto.setSenderNickname(entity.getSenderNickname());
        dto.setMessage(entity.getMessage());
        dto.setSentAt(entity.getSentAt());
        dto.setRead(entity.isRead()); // DTO는 read 유지
        return dto;
    }


}
