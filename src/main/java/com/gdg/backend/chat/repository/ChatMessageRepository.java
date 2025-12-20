package com.gdg.backend.chat.repository;

import com.gdg.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방(chatRoomId) 메시지를 전송 시간 순으로 모두 가져오기
    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(String chatRoomId);

    // 채팅방에서 읽지 않은 메시지만 조회 (필요하면 추가 가능)
    List<ChatMessage> findByChatRoomIdAndReadFalse(String chatRoomId);

}
