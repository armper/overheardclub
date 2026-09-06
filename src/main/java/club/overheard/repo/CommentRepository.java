package club.overheard.repo;

import club.overheard.domain.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByPostIdOrderByCreatedAtAscIdAsc(Long postId);

    /** Comment counts per post id, as (postId, count) pairs. */
    @Query("select c.post.id, count(c) from Comment c where c.post.id in :postIds group by c.post.id")
    List<Object[]> countByPostIds(@Param("postIds") Collection<Long> postIds);

    @Modifying
    @Query("delete from Comment c where c.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
