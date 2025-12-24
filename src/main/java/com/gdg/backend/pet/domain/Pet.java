package com.gdg.backend.pet.domain;

import com.gdg.backend.pet.dto.PetRequestDto;
import com.gdg.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "pet_age", nullable = false)
    private String petAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_sex", nullable = false)
    private PetSex petSex;

    @Column(name = "pet_type", nullable = false)
    private String petType;

    @Column(name = "pet_profile_image_url", nullable = false)
    private String petProfileImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter
    private boolean isRepresentative;

    @Builder
    public Pet(Long id, String petName, String petAge, String petType, String petProfileImageUrl, User user, PetSex petSex){
        this.id = id;
        this.petName = petName;
        this.petAge = petAge;
        this.petType = petType;
        this.petProfileImageUrl = petProfileImageUrl;
        this.user = user;
        this.petSex = petSex;
    }

    public void update(PetRequestDto dto) {
        this.petName = dto.getPetName();
        this.petAge = dto.getPetAge();
        this.petSex = dto.getPetSex();
        this.petType = dto.getPetType();
        this.petProfileImageUrl = dto.getPetProfileImageUrl();
    }

    public static Pet create(User user, PetRequestDto dto) {
        return Pet.builder()
                .petName(dto.getPetName())
                .petAge(dto.getPetAge())
                .petSex(dto.getPetSex())
                .petType(dto.getPetType())
                .petProfileImageUrl(dto.getPetProfileImageUrl())
                .user(user)
                .build();
    }


}
