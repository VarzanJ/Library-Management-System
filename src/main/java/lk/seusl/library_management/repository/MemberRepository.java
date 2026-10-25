package lk.seusl.library_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.seusl.library_management.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByUsernameAndPassword(String username, String password);
}