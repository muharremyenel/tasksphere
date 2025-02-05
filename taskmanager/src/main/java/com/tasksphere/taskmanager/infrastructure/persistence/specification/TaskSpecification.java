package com.tasksphere.taskmanager.infrastructure.persistence.specification;

import com.tasksphere.taskmanager.application.dto.task.TaskSearchRequest;
import com.tasksphere.taskmanager.domain.entity.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import com.tasksphere.taskmanager.domain.entity.User;
import com.tasksphere.taskmanager.domain.enums.Role;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskSpecification {

    public static Specification<Task> buildSpecification(TaskSearchRequest request, User currentUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Yetki kontrolü - sadece yetkili olduğu task'leri görebilmeli
            if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
                Predicate isCreator = cb.equal(root.get("createdBy"), currentUser);
                Predicate isAssignee = cb.equal(root.get("assignedTo"), currentUser);
                Predicate isCollaborator = cb.isMember(currentUser, root.get("collaborators"));
                predicates.add(cb.or(isCreator, isAssignee, isCollaborator));
            }

            // Mevcut filtreler
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