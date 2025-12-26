package com.gdg.backend.board.service;

import com.gdg.backend.board.domain.Board;
import com.gdg.backend.board.domain.BoardStatus;
import com.gdg.backend.board.dto.BoardDetailResponseDto;
import com.gdg.backend.board.dto.BoardRequestDto;
import com.gdg.backend.board.dto.BoardResponseDto;
import com.gdg.backend.board.dto.BoardUpdateRequest;
import com.gdg.backend.board.dto.MyBoardResponseDto;
import com.gdg.backend.board.exception.BoardNotFoundException;
import com.gdg.backend.board.repository.BoardRepository;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.global.security.UserPrincipal;
import com.gdg.backend.pet.domain.Pet;
import com.gdg.backend.pet.dto.PetProfileDto;
import com.gdg.backend.pet.repository.PetRepository;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardInterestService boardInterestService;
    private final PetRepository petRepository;

    public Long create(Long userId, BoardRequestDto dto) {
        User user = getUser(userId);
        Pet pet = getPetOrNull(dto.getPetId());

        Board board = Board.create(
                dto.getTitle(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getWalkTime(),
                dto.getWalkTimeType(),
                user,
                pet
        );

        return boardRepository.save(board).getId();
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> findAll(Long userId) {
        return boardRepository.findByStatusOrderByIdDesc(BoardStatus.OPEN)
                .stream()
                .map(board -> toResponseDto(board, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> findCompleted(Long userId) {
        return boardRepository.findByStatusOrderByIdDesc(BoardStatus.COMPLETED)
                .stream()
                .map(board -> toResponseDto(board, userId))
                .toList();
    }

    @Transactional
    public void update(
            Long boardId,
            Long userId,
            BoardUpdateRequest req
    ) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        // 작성자 검증 (관리자 로직은 여기서 확장)
        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("수정 권한 없음");
        }

        Pet pet = null;
        if (req.getPetId() != null) {
            pet = petRepository.getReferenceById(req.getPetId());
        }

        board.update(
                req.getTitle(),
                req.getDescription(),
                req.getLocation(),
                req.getWalkTime(),
                req.getWalkTimeType(),
                pet
        );
    }

    @Transactional
    public void delete(Long boardId, Long userId) {
        Board board = getBoard(boardId);
        validateOwner(board, userId);
        boardRepository.delete(board);
    }

    @Transactional
    public void completeBoard(Long boardId, Long userId) {
        Board board = getBoard(boardId);
        validateOwner(board, userId);
        board.complete();
    }

    @Transactional(readOnly = true)
    public List<MyBoardResponseDto> findMyBoards(Long userId) {
        return boardRepository.findByUserIdOrderByIdDesc(userId)
                .stream()
                .map(MyBoardResponseDto::from)
                .toList();
    }

    @Transactional
    public BoardDetailResponseDto findDetail(Long boardId, Long userId) {
        Board board = getBoard(boardId);
        board.increaseViewCount();

        boolean myBoard = userId != null && board.getUser().getId().equals(userId);

        boolean interested = userId != null &&
                boardInterestService.isInterested(userId, board.getId());

        long interestCount = boardInterestService.countInterest(board.getId());

        PetProfileDto dogProfile = board.getPet() != null
                ? PetProfileDto.from(board.getPet())
                : null;

        return BoardDetailResponseDto.from(board)
                .applyInterest(interested, interestCount)
                .withDogProfile(dogProfile)
                .withMyBoard(myBoard);
    }


    private BoardResponseDto toResponseDto(Board board, Long userId) {
        boolean interested = userId != null &&
                boardInterestService.isInterested(userId, board.getId());

        long interestCount = boardInterestService.countInterest(board.getId());

        return BoardResponseDto.from(board)
                .applyInterest(interested, interestCount);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
    }

    private Pet getPetOrNull(Long petId) {
        if (petId == null) return null;
        return petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("강아지를 찾을 수 없습니다."));
    }

    private Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
    }

    private void validateOwner(Board board, Long userId) {
        if (!board.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("작성자만 접근할 수 있습니다.");
        }
    }
}
