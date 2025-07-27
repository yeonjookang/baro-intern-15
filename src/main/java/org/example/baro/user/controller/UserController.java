package org.example.baro.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.baro.user.dto.GetUserResponse;
import org.example.baro.user.dto.SignInRequest;
import org.example.baro.user.dto.SignInResponse;
import org.example.baro.user.dto.SignUpRequest;
import org.example.baro.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 인증 및 정보 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = SignInResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 비밀번호)",
                    content = @Content(schema = @Schema(example = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"INVALID_PASSWORD\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(example = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"NOT_FOUND_USER\"}")))
    })
    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest request) {
        SignInResponse response = userService.signIn(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(schema = @Schema(example = "{\"status\":409,\"error\":\"Conflict\",\"message\":\"DUPLICATE_EMAIL\"}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)",
                    content = @Content(schema = @Schema(example = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Validation failed\"}")))
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "현재 사용자 정보 조회", description = "인증된 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = GetUserResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 유효하지 않음)",
                    content = @Content(schema = @Schema(example = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"JWT 토큰이 필요합니다.\"}")))
    })
    @GetMapping("/user")
    public ResponseEntity<GetUserResponse> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        GetUserResponse response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }
}

