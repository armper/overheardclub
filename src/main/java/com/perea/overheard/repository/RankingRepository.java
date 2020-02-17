package com.perea.overheard.repository;

import com.perea.overheard.domain.Ranking;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Ranking entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long>, JpaSpecificationExecutor<Ranking> {

    @Query("select ranking from Ranking ranking where ranking.user.login = ?#{principal.username}")
    List<Ranking> findByUserIsCurrentUser();

}
