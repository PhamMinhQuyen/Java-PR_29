package com.library.service.impl;

import com.library.domain.model.Member;
import com.library.domain.repository.MemberRepository;
import com.library.dto.request.CreateMemberRequest;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Triển khai nghiệp vụ quản lý Thành viên.
 * - SRP: Chỉ đảm nhận logic liên quan tới thành viên độc giả.
 * - DIP: Phụ thuộc vào interface MemberRepository, dùng constructor injection.
 */
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member addMember(CreateMemberRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu thêm thành viên không được để null");
        }

        memberRepository.findByEmail(request.getEmail().trim().toLowerCase()).ifPresent(m -> {
            throw new BusinessException("Thành viên với email '" + request.getEmail() + "' đã tồn tại.");
        });

        Member member = new Member(
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getPhone() != null ? request.getPhone().trim() : null
        );

        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thành viên", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
