package com.bingotask.repository.specification;

import com.bingotask.model.Task;
import com.bingotask.model.UserTask;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    // Private constructor to prevent instantiation
    private TaskSpecification() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Task> withFilters(
            Boolean activeOnly,
            String category,
            Integer minPoints,
            Integer maxPoints,
            Long userId,
            Boolean completedToday) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (activeOnly != null && activeOnly) {
                predicates.add(criteriaBuilder.isTrue(root.get("isActive")));
            }

            if (category != null && !category.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            if (minPoints != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("points"), minPoints));
            }

            if (maxPoints != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("points"), maxPoints));
            }

            if (userId != null && completedToday != null) {
                Join<Task, UserTask> userTasks = root.join("userTasks", JoinType.LEFT);
                predicates.add(criteriaBuilder.equal(userTasks.get("user").get("id"), userId));

                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

                if (completedToday) {
                    predicates.add(criteriaBuilder.between(
                            userTasks.get("completedAt"), startOfDay, endOfDay));
                } else {
                    predicates.add(criteriaBuilder.not(
                            criteriaBuilder.between(userTasks.get("completedAt"), startOfDay, endOfDay)));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}