package com.perea.overheard.repository;

import com.perea.overheard.domain.Rank;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Rank entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {

}
