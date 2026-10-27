package com.library.infrastructure.persistence;

import com.library.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository cho Member.
 */
@Repository
public interface SpringDataMemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
