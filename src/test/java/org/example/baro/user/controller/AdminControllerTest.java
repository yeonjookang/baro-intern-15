package org.example.baro.user.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.example.baro.user.dto.SignInRequest;
import org.example.baro.user.dto.SignInResponse;
import org.example.baro.user.dto.SignUpRequest;
import org.example.baro.user.entity.UserRole;
import org.example.baro.user.repository.UserRepository;
import org.example.baro.user.service.UserService;
import org.example.baro.util.JwtUtil;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final String TEST_USER_EMAIL = "test_admin_user@example.com";
    private final String TEST_USER_PASSWORD = "password123";
    private final String ADMIN_USER_EMAIL = "admin_test@example.com";
    private final String ADMIN_USER_PASSWORD = "adminpass";

    private String userToken;
    private String adminToken;
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        SecurityContextHolder.clearContext();
        userRepository.clearUsers();

        userService.signUp(new SignUpRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD, UserRole.USER));
        MvcResult userSignInResult = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInRequest(TEST_USER_EMAIL, TEST_USER_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        SignInResponse userSignInResponse = objectMapper.readValue(userSignInResult.getResponse().getContentAsString(), SignInResponse.class);
        userToken = userSignInResponse.accessToken();

        userService.signUp(new SignUpRequest(ADMIN_USER_EMAIL, ADMIN_USER_PASSWORD, UserRole.ADMIN));
        MvcResult adminSignInResult = mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignInRequest(ADMIN_USER_EMAIL, ADMIN_USER_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        SignInResponse adminSignInResponse = objectMapper.readValue(adminSignInResult.getResponse().getContentAsString(), SignInResponse.class);
        adminToken = adminSignInResponse.accessToken();
    }

    @Test
    @DisplayName("관리자 권한 부여/변경 성공 - 관리자 사용자가 요청")
    void changeRole_AdminUserSuccess() throws Exception {
        String token= jwtUtil.substringToken(adminToken);
        Claims claims = jwtUtil.extractClaims(token);
        Long adminId = Long.parseLong(claims.getSubject());
        mockMvc.perform(patch("/admin/user/" +adminId + "/role")
                        .header("Authorization", adminToken))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("관리자 권한 부여/변경 실패 - 일반 사용자가 요청 (권한 없음)")
    void changeRole_NormalUserAccessDenied() throws Exception {
        String token= jwtUtil.substringToken(userToken);
        Claims claims = jwtUtil.extractClaims(token);
        Long userId = Long.parseLong(claims.getSubject());
        mockMvc.perform(patch("/admin/user/" +userId +"/role")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 정보 조회 성공 - 관리자 사용자가 요청")
    void getAdminInfo_AdminUserSuccess() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(ADMIN_USER_EMAIL))
                .andExpect(jsonPath("$.role").value(UserRole.ADMIN.name()));
    }

    @Test
    @DisplayName("관리자 정보 조회 실패 - 일반 사용자가 요청 (권한 없음)")
    void getAdminInfo_NormalUserAccessDenied() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 정보 조회 실패 - 인증되지 않은 사용자")
    void getAdminInfo_Unauthorized() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isUnauthorized());
    }
}

