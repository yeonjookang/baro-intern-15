package org.example.baro.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.baro.user.dto.GetUserResponse;
import org.example.baro.user.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "관리자 권한 관련 API (관리자만 접근 가능)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "사용자 역할 변경", description = "인증된 관리자가 사용자의 역할을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "역할 변경 요청 수락"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 권한 필요)",
                    content = @Content(schema = @Schema(example = "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Access Denied\"}"))),
            @ApiResponse(responseCode = "404", description = "사용자 없음",
                    content = @Content(schema = @Schema(example = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"NOT_FOUND_USER\"}")))
    })
    @PatchMapping("/admin/user/{targetUserId}/role")
    public ResponseEntity<Void> changeRole(@PathVariable Long targetUserId) {
        adminService.changeRole(targetUserId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "관리자 정보 조회", description = "인증된 관리자 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = GetUserResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (관리자 권한 필요)")
    })
    @GetMapping("/admin")
    public ResponseEntity<GetUserResponse> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        GetUserResponse response = adminService.getAdmin(userId);
        return ResponseEntity.ok(response);
    }
}
