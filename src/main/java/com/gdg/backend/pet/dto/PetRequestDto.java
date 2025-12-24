package com.gdg.backend.pet.dto;

import com.gdg.backend.pet.domain.PetSex;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PetRequestDto {
    private String petName;
    private String petAge;
    @Setter
    private PetSex petSex;
    private String petType;
    @Setter
    private String petProfileImageUrl;
}
