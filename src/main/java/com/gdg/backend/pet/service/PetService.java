package com.gdg.backend.pet.service;

import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.pet.domain.Pet;
import com.gdg.backend.pet.dto.PetRequestDto;
import com.gdg.backend.pet.dto.PetResponseDto;
import com.gdg.backend.pet.repository.PetRepository;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Transactional
    public PetResponseDto setPet(Long userId, PetRequestDto petRequestDto){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("유저를 찾을 수 없습니다."));

        Pet pet = Pet.create(user, petRequestDto);

        petRepository.save(pet);

        return PetResponseDto.from(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponseDto> petInfo(Long userId){
        List<Pet> pets = petRepository.findAllByUserId(userId);

        return pets.stream()
                .map(PetResponseDto::from)
                .toList();
    }

    @Transactional
    public PetResponseDto updatePet(
            Long userId,
            Long petId,
            PetRequestDto petRequestDto
    ) {
        Pet pet = petRepository.findByIdAndUserId(petId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 수정할 권한이 없습니다."));

        pet.update(petRequestDto);

        return PetResponseDto.from(pet);
    }

    @Transactional
    public void deletePet(Long userId, Long petId) {

        Pet pet = petRepository.findByIdAndUserId(petId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 반려동물을 삭제할 권한이 없습니다.")
                );

        petRepository.delete(pet);
    }

}
