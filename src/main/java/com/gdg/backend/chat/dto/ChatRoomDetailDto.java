package com.gdg.backend.chat.dto;

import com.gdg.backend.board.dto.BoardResponseDto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ChatRoomDetailDto {
    private Long chatRoomId;
    private String profileName;
    private String profilePhone;
    private BoardResponseDto post;          // 게시글 정보
    private AppointmentDto appointment;  // 예약 정보 (옵션)
    private List<ChatMessageDto> messages; // 채팅 메시지 내역
}
