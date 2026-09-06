package club.overheard.repo;

import club.overheard.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"author", "topic"})
    Optional<Post> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"author", "topic"})
    @Query("select p from Post p where (:topicId is null or p.topic.id = :topicId) order by p.createdAt desc, p.id desc")
    Page<Post> findLatest(@Param("topicId") Long topicId, Pageable pageable);

    /** Most-reacted posts since a given time. Ties fall back to newest first. */
    @EntityGraph(attributePaths = {"author", "topic"})
    @Query("select p from Post p left join Reaction r on r.post = p " +
           "where p.createdAt >= :since and (:topicId is null or p.topic.id = :topicId) " +
           "group by p order by count(r) desc, p.createdAt desc, p.id desc")
    Page<Post> findTop(@Param("since") Instant since, @Param("topicId") Long topicId, Pageable pageable);

    long countByTopicId(Long topicId);
    long countByAuthorId(Long authorId);
}
