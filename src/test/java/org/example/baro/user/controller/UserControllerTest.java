package org.example.baro.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.baro.user.dto.SignInRequest;
import org.example.baro.user.dto.SignInResponse;
import org.example.baro.user.dto.SignUpRequest;
import org.example.baro.user.entity.UserRole;
import org.example.baro.user.repository.UserRepository;
import org.example.baro.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final String TEST_USER_EMAIL = "test_user@example.com";
    private final String TEST_USER_PASSWORD = "password123";
    private final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
    private final String WRONG_PASSWORD = "wrongpassword";

    private String testUserToken;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRepository.clearUsers();
    }

    @Test
    @DisplayName("회원가입 성공 - 정상적인 사용자 정보")
    void signUp_Success() throws Exception {
        SignUpRequest request = new SignUpRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.USER);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.findByEmail(TEST_USER_EMAIL).isPresent(), "회원가입 후 사용자가 저장되지 않았습니다.");
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 가입된 이메일")
    void signUp_DuplicateEmail() throws Exception {
        userService.signUp(new SignUpRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.USER));

        SignUpRequest duplicateRequest = new SignUpRequest(TEST_USER_EMAIL, "anotherpass", UserRole.USER);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인 성공 - 올바른 자격 증명")
    void signIn_Success() throws Exception {
        userService.signUp(new SignUpRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.USER));

        SignInRequest request = new SignInRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD);

        MvcResult result = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn();

        SignInResponse signInResponse = objectMapper.readValue(result.getResponse().getContentAsString(), SignInResponse.class);
        testUserToken = signInResponse.accessToken();
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void signIn_InvalidPassword() throws Exception {
        userService.signUp(new SignUpRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.USER));

        SignInRequest request = new SignInRequest(TEST_USER_EMAIL, WRONG_PASSWORD);

        mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void signIn_NotFoundUser() throws Exception {
        SignInRequest request = new SignInRequest(NON_EXISTENT_EMAIL, TEST_USER_PASSWORD);

        mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자 정보 조회 성공 - 인증된 사용자")
    void getUser_Success() throws Exception {
        userService.signUp(new SignUpRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.USER));
        MvcResult signInResult = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        SignInResponse signInResponse = objectMapper.readValue(signInResult.getResponse().getContentAsString(), SignInResponse.class);
        testUserToken = signInResponse.accessToken();

        mockMvc.perform(get("/user")
                        .header("Authorization", testUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.role").value(UserRole.USER.name()));
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 - 인증되지 않은 사용자")
    void getUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized());
    }
}
