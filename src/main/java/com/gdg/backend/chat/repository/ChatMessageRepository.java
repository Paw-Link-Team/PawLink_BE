package com.gdg.backend.chat.repository;

import com.gdg.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomIdOrderBySentAtAsc(Long chatRoomId);

    List<ChatMessage> findByChatRoomIdAndIsReadFalse(Long chatRoomId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.id IN " +
           "(SELECT MAX(m.id) FROM ChatMessage m GROUP BY m.chatRoomId)")
    List<ChatMessage> findLatestMessages();

    @Query("SELECT cm.chatRoomId, COUNT(cm) FROM ChatMessage cm WHERE cm.isRead = false GROUP BY cm.chatRoomId")
    List<Object[]> countUnreadMessagesByRoom();
}
