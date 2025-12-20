package com.gdg.backend.chat.repository;

import com.gdg.backend.chat.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 특정 채팅방(chatRoomId)에 해당하는 약속 정보 조회
    Optional<Appointment> findByChatRoomId(Long chatRoomId);

}
