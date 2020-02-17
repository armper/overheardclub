package com.perea.overheard.repository;

import com.perea.overheard.domain.Rank;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Rank entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

    @Query("select rank from Rank rank where rank.user.login = ?#{principal.username}")
    List<Rank> findByUserIsCurrentUser();

}
