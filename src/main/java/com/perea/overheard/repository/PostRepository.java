package com.perea.overheard.repository;

import com.perea.overheard.domain.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Post entity.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Query("select post from Post post where post.user.login = ?#{principal.username}")
    List<Post> findByUserIsCurrentUser();

    @Query(value = "select distinct post from Post post left join fetch post.userUpranks",
        countQuery = "select count(distinct post) from Post post")
    Page<Post> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct post from Post post left join fetch post.userUpranks")
    List<Post> findAllWithEagerRelationships();

    @Query("select post from Post post left join fetch post.userUpranks where post.id =:id")
    Optional<Post> findOneWithEagerRelationships(@Param("id") Long id);

    String SCORE = "(coalesce(post.rankOne, 0) + coalesce(post.rankTwo, 0) + coalesce(post.rankThree, 0)"
        + " + coalesce(post.rankFour, 0) + coalesce(post.rankFive, 0))";

    @Query(value = "select post from Post post where post.date > :since order by " + SCORE + " desc, post.date desc",
        countQuery = "select count(post) from Post post where post.date > :since")
    Page<Post> findTopSince(@Param("since") Instant since, Pageable pageable);

    @Query(value = "select post from Post post where lower(post.title) like lower(concat('%', :q, '%'))"
        + " or lower(post.content) like lower(concat('%', :q, '%')) order by post.date desc",
        countQuery = "select count(post) from Post post where lower(post.title) like lower(concat('%', :q, '%'))"
        + " or lower(post.content) like lower(concat('%', :q, '%'))")
    Page<Post> search(@Param("q") String q, Pageable pageable);

    @Query("select count(post) > 0 from Post post join post.userUpranks u where post.id = :postId and u.login = :login")
    boolean hasUserReacted(@Param("postId") Long postId, @Param("login") String login);

    long countByTopicId(Long topicId);


}
