package org.example.baro.user.service;

import lombok.RequiredArgsConstructor;
import org.example.baro.exception.BizException;
import org.example.baro.exception.ErrorDescription;
import org.example.baro.user.dto.GetUserResponse;
import org.example.baro.user.entity.User;
import org.example.baro.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public void changeRole(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new BizException(ErrorDescription.NOT_FOUND_USER);
        }

        findUser.get().changeRole();
    }

    public GetUserResponse getAdmin(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new BizException(ErrorDescription.NOT_FOUND_USER);
        }

        return new GetUserResponse(findUser.get().getEmail(), findUser.get().getRole());
    }
}
