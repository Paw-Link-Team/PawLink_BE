package com.gdg.backend.pet.dto;

import com.gdg.backend.pet.domain.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PetProfileDto {
    private Long id;
    private String name;
    private String type;
    private String age;
    private String profileImageUrl;

    public static PetProfileDto from(Pet pet){
        return new PetProfileDto(
                pet.getId(),
                pet.getPetName(),
                pet.getPetType(),
                pet.getPetAge(),
                pet.getPetProfileImageUrl()
        );
    }
}
