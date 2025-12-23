package com.gdg.backend.pet.dto;

import com.gdg.backend.pet.domain.Pet;
import com.gdg.backend.pet.domain.PetSex;
import com.gdg.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class PetResponseDto {
    private Long id;
    private String petName;
    private String petAge;
    private PetSex petSex;
    private String petType;
    private String petProfileImageUrl;
    private Long userId;

    @Builder
    public PetResponseDto(Long id, String petName, String petAge, PetSex petSex, String petType, String petProfileImageUrl, Long userId){
        this.id = id;
        this.petName = petName;
        this.petAge = petAge;
        this.petSex = petSex;
        this.petType = petType;
        this.petProfileImageUrl = petProfileImageUrl;
        this.userId = userId;
    }

    public static PetResponseDto from(Pet pet) {
        return PetResponseDto.builder()
                .id(pet.getId())
                .petName(pet.getPetName())
                .petAge(pet.getPetAge())
                .petSex(pet.getPetSex())
                .petType(pet.getPetType())
                .petProfileImageUrl(pet.getPetProfileImageUrl())
                .userId(pet.getUser().getId())
                .build();
    }

}
