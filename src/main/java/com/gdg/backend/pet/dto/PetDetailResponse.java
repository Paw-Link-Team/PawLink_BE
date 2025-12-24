package com.gdg.backend.pet.dto;

import com.gdg.backend.pet.domain.Pet;
import com.gdg.backend.pet.domain.PetSex;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetDetailResponse {

    private Long petId;
    private String petName;
    private String petAge;
    private PetSex petSex;
    private String petType;
    private String petProfileImageUrl;
    private boolean isRepresentative;

    public static PetDetailResponse from(Pet pet) {
        return PetDetailResponse.builder()
                .petId(pet.getId())
                .petName(pet.getPetName())
                .petAge(pet.getPetAge())
                .petSex(pet.getPetSex())
                .petType(pet.getPetType())
                .petProfileImageUrl(pet.getPetProfileImageUrl())
                .isRepresentative(pet.isRepresentative())
                .build();
    }
}
