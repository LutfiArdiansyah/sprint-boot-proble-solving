package kct.co.id.skilltest.repository;

import kct.co.id.skilltest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
