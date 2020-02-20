package com.perea.overheard.repository;

import com.perea.overheard.domain.OverheardComment;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the OverheardComment entity.
 */
@Repository
public interface OverheardCommentRepository extends JpaRepository<OverheardComment, Long>, JpaSpecificationExecutor<OverheardComment> {

    @Query("select overheardComment from OverheardComment overheardComment where overheardComment.user.login = ?#{principal.username}")
    List<OverheardComment> findByUserIsCurrentUser();

}
