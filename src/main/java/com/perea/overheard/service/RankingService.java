package com.perea.overheard.service;

import com.perea.overheard.domain.Ranking;
import com.perea.overheard.repository.RankingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Ranking}.
 */
@Service
@Transactional
public class RankingService {

    private final Logger log = LoggerFactory.getLogger(RankingService.class);

    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    /**
     * Save a ranking.
     *
     * @param ranking the entity to save.
     * @return the persisted entity.
     */
    public Ranking save(Ranking ranking) {
        log.debug("Request to save Ranking : {}", ranking);
        return rankingRepository.save(ranking);
    }

    /**
     * Get all the rankings.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Ranking> findAll() {
        log.debug("Request to get all Rankings");
        return rankingRepository.findAll();
    }

    /**
     * Get one ranking by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Ranking> findOne(Long id) {
        log.debug("Request to get Ranking : {}", id);
        return rankingRepository.findById(id);
    }

    /**
     * Delete the ranking by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Ranking : {}", id);
        rankingRepository.deleteById(id);
    }
}
