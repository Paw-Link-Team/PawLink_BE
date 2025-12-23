package com.gdg.backend.pet.domain;

import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    private String petName;

    private Integer petAge;

    @Enumerated(EnumType.STRING)
    private PetSex petSex;

    private String petType;

    private String petProfileImageUrl;

}
