package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "comments")
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemId (Integer itemId);
}
