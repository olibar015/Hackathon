package com.bingotask.repository;

import com.bingotask.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll(Sort sort);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    @Query("SELECT u FROM User u ORDER BY u.totalPoints DESC")
    List<User> findAllOrderByTotalPointsDesc();

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();

    @Query("SELECT COUNT(u) FROM User u WHERE u.lastActivityDate = :date")
    Long countActiveUsersByDate(@Param("date") LocalDate date);

    @Query("SELECT u FROM User u WHERE u.lastActivityDate >= :startDate")
    List<User> findActiveUsersSince(@Param("startDate") LocalDate startDate);

    @Query(value = "SELECT * FROM users ORDER BY total_points DESC LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<User> findTopUsers(@Param("offset") int offset, @Param("limit") int limit);
    long countByLastActivityDateAfter(LocalDate date);
    long countByCreatedAtAfter(LocalDateTime date);
}