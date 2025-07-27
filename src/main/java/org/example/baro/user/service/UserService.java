package org.example.baro.user.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.baro.exception.BizException;
import org.example.baro.exception.ErrorDescription;
import org.example.baro.user.dto.GetUserResponse;
import org.example.baro.user.dto.SignInRequest;
import org.example.baro.user.dto.SignInResponse;
import org.example.baro.user.dto.SignUpRequest;
import org.example.baro.user.entity.User;
import org.example.baro.user.repository.TokenRepository;
import org.example.baro.user.repository.UserRepository;
import org.example.baro.util.JwtUtil;
import org.example.baro.util.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignInResponse signIn(@Valid SignInRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BizException(ErrorDescription.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BizException(ErrorDescription.INVALID_PASSWORD);
        }
        String accessToken = jwtUtil.createAccessToken(user);

        return new SignInResponse(accessToken);
    }

    public void signUp(SignUpRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BizException(ErrorDescription.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .email(request.email())
                .password(encodedPassword)
                .role(request.role())
                .build();

        userRepository.save(user);
    }

    public GetUserResponse getUser(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new BizException(ErrorDescription.NOT_FOUND_USER);
        }

        return new GetUserResponse(findUser.get().getEmail(), findUser.get().getRole());
    }
}
