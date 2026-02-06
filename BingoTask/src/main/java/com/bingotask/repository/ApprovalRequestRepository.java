package com.bingotask.repository;

import com.bingotask.model.ApprovalRequest;
import com.bingotask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {

  List<ApprovalRequest> findByRequester(User requester);

  List<ApprovalRequest> findByApprover(User approver);

  List<ApprovalRequest> findByApproverAndStatus(User approver, ApprovalRequest.ApprovalStatus status);

  List<ApprovalRequest> findByRequesterAndStatus(User requester, ApprovalRequest.ApprovalStatus status);
}
