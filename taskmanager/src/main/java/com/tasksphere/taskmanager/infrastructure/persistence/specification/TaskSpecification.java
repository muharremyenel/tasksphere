package com.tasksphere.taskmanager.infrastructure.persistence.specification;

import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.domain.entity.Task;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    public static Specification<Task> withSearchCriteria(TaskSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getSearchTerm() != null) {
                String pattern = "%" + request.getSearchTerm().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), request.getPriority()));
            }

            if (request.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }

            if (request.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getAssignedToId() != null) {
                predicates.add(cb.equal(root.get("assignedTo").get("id"), request.getAssignedToId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 