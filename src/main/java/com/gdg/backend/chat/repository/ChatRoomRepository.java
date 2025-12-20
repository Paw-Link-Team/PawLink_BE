package com.gdg.backend.chat.repository;

import com.gdg.backend.chat.entity.ChatRoom;
import com.gdg.backend.chat.entity.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 유저별 전체 채팅방
    List<ChatRoom> findByOwnerUserIdOrWalkerUserId(Long ownerUserId, Long walkerUserId);

    // 안 읽은 메시지 있는 채팅방만 조회 (커스텀 쿼리 필요함 예시)
    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.ownerUserId = ?1 OR cr.walkerUserId = ?1) AND cr.status = 'UNREAD'")
    List<ChatRoom> findUnreadRoomsByUser(Long userId);

    // 완료 상태 채팅방 조회
    List<ChatRoom> findByOwnerUserIdOrWalkerUserIdAndStatus(Long ownerUserId, Long walkerUserId, ChatRoomStatus status);
}
