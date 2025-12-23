package com.gdg.backend.pet.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PetSex {
    MALE,
    FEMALE;

    @JsonCreator
    public static PetSex from(String value) {
        return PetSex.valueOf(value.toUpperCase());
    }
}
