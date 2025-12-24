package com.gdg.backend.pet.repository;

import com.gdg.backend.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findAllByUserId(Long userId);
    Optional<Pet> findByIdAndUserId(Long petId, Long userId);
    Optional<Pet> findByUserIdAndIsRepresentativeTrue(Long userId);
}
