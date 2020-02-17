package com.perea.overheard.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.perea.overheard.domain.Ranking;
import com.perea.overheard.domain.*; // for static metamodels
import com.perea.overheard.repository.RankingRepository;
import com.perea.overheard.service.dto.RankingCriteria;

/**
 * Service for executing complex queries for {@link Ranking} entities in the database.
 * The main input is a {@link RankingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Ranking} or a {@link Page} of {@link Ranking} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RankingQueryService extends QueryService<Ranking> {

    private final Logger log = LoggerFactory.getLogger(RankingQueryService.class);

    private final RankingRepository rankingRepository;

    public RankingQueryService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    /**
     * Return a {@link List} of {@link Ranking} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Ranking> findByCriteria(RankingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Ranking> specification = createSpecification(criteria);
        return rankingRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Ranking} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Ranking> findByCriteria(RankingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Ranking> specification = createSpecification(criteria);
        return rankingRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RankingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Ranking> specification = createSpecification(criteria);
        return rankingRepository.count(specification);
    }

    /**
     * Function to convert {@link RankingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Ranking> createSpecification(RankingCriteria criteria) {
        Specification<Ranking> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Ranking_.id));
            }
            if (criteria.getRankType() != null) {
                specification = specification.and(buildSpecification(criteria.getRankType(), Ranking_.rankType));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Ranking_.date));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(Ranking_.user, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getPostId() != null) {
                specification = specification.and(buildSpecification(criteria.getPostId(),
                    root -> root.join(Ranking_.post, JoinType.LEFT).get(Post_.id)));
            }
        }
        return specification;
    }
}
