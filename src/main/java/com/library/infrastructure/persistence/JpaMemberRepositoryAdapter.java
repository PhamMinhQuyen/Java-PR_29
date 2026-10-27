package com.library.infrastructure.persistence;

import com.library.domain.model.Member;
import com.library.domain.repository.MemberRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter triển khai Domain MemberRepository sử dụng Spring Data JPA.
 */
@Component
public class JpaMemberRepositoryAdapter implements MemberRepository {

    private final SpringDataMemberRepository springDataMemberRepository;

    public JpaMemberRepositoryAdapter(SpringDataMemberRepository springDataMemberRepository) {
        this.springDataMemberRepository = springDataMemberRepository;
    }

    @Override
    public Member save(Member member) {
        return springDataMemberRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return springDataMemberRepository.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return springDataMemberRepository.findByEmail(email);
    }

    @Override
    public List<Member> findAll() {
        return springDataMemberRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return springDataMemberRepository.existsById(id);
    }
}
