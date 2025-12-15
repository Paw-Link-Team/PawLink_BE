package com.gdg.backend.admin.service;

import com.gdg.backend.admin.dto.AdminSignupRequest;
import com.gdg.backend.global.code.ErrorCode;
import com.gdg.backend.global.exception.UserNotFoundException;
import com.gdg.backend.user.domain.Role;
import com.gdg.backend.user.domain.User;
import com.gdg.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    @Transactional
    public void createAdmin(AdminSignupRequest request){

        User user = userRepository.findByProviderAndProviderId(
                request.getProvider(),
                request.getProviderId()
        ).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        if(user.getRole() == Role.SUPER_ADMIN){
            return;
        }

        user.updateRole(Role.ADMIN);
    }
}
