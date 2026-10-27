package com.library.service;

import com.library.domain.model.Member;
import com.library.dto.request.CreateMemberRequest;

import java.util.List;

/**
 * Interface nghiệp vụ quản lý Thành viên theo nguyên lý ISP.
 */
public interface MemberService {

    Member addMember(CreateMemberRequest request);

    Member getMemberById(Long id);

    List<Member> getAllMembers();
}
