package com.library.controller;

import com.library.domain.model.Member;
import com.library.dto.request.CreateMemberRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.MemberResponse;
import com.library.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> addMember(@Valid @RequestBody CreateMemberRequest request) {
        Member created = memberService.addMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(MemberResponse.fromEntity(created), "Thêm thành viên mới thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> list = memberService.getAllMembers().stream()
                .map(MemberResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(list, "Lấy danh sách thành viên thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.ok(MemberResponse.fromEntity(member), "Lấy thông tin thành viên thành công"));
    }
}
