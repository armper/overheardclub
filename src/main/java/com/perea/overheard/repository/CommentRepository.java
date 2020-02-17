package com.perea.overheard.repository;

import com.perea.overheard.domain.Comment;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    @Query("select comment from Comment comment where comment.user.login = ?#{principal.username}")
    List<Comment> findByUserIsCurrentUser();

}
