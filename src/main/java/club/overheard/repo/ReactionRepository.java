package club.overheard.repo;

import club.overheard.domain.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByPostIdAndUserId(Long postId, Long userId);

    /** (postId, type, count) triples for a set of posts. */
    @Query("select r.post.id, r.type, count(r) from Reaction r where r.post.id in :postIds group by r.post.id, r.type")
    List<Object[]> countByPostIds(@Param("postIds") Collection<Long> postIds);

    /** (postId, type) pairs for the given user's reactions on a set of posts. */
    @Query("select r.post.id, r.type from Reaction r where r.user.id = :userId and r.post.id in :postIds")
    List<Object[]> findMine(@Param("userId") Long userId, @Param("postIds") Collection<Long> postIds);

    @Modifying
    @Query("delete from Reaction r where r.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
