package com.bingotask.repository;

import com.bingotask.model.TaskAssignment;
import com.bingotask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

  List<TaskAssignment> findByAssignee(User assignee);

  List<TaskAssignment> findByAssigneeAndStatus(User assignee, TaskAssignment.AssignmentStatus status);

  List<TaskAssignment> findByAssigner(User assigner);

  List<TaskAssignment> findByVerifier(User verifier);

  List<TaskAssignment> findByAssigneeAndDueDateBeforeAndStatus(
    User assignee, LocalDate dueDate, TaskAssignment.AssignmentStatus status);

  @Query("SELECT ta FROM TaskAssignment ta WHERE ta.verifier = :verifier AND ta.status = 'COMPLETED'")
  List<TaskAssignment> findPendingVerifications(@Param("verifier") User verifier);

  boolean existsByAssigneeAndTaskAndStatus(
    User assignee, com.bingotask.model.Task task, TaskAssignment.AssignmentStatus status);
}
