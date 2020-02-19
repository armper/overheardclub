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

import com.perea.overheard.domain.OverheardComment;
import com.perea.overheard.domain.*; // for static metamodels
import com.perea.overheard.repository.OverheardCommentRepository;
import com.perea.overheard.service.dto.OverheardCommentCriteria;

/**
 * Service for executing complex queries for {@link OverheardComment} entities in the database.
 * The main input is a {@link OverheardCommentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OverheardComment} or a {@link Page} of {@link OverheardComment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OverheardCommentQueryService extends QueryService<OverheardComment> {

    private final Logger log = LoggerFactory.getLogger(OverheardCommentQueryService.class);

    private final OverheardCommentRepository overheardCommentRepository;

    public OverheardCommentQueryService(OverheardCommentRepository overheardCommentRepository) {
        this.overheardCommentRepository = overheardCommentRepository;
    }

    /**
     * Return a {@link List} of {@link OverheardComment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OverheardComment> findByCriteria(OverheardCommentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OverheardComment> specification = createSpecification(criteria);
        return overheardCommentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link OverheardComment} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OverheardComment> findByCriteria(OverheardCommentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OverheardComment> specification = createSpecification(criteria);
        return overheardCommentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OverheardCommentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OverheardComment> specification = createSpecification(criteria);
        return overheardCommentRepository.count(specification);
    }

    /**
     * Function to convert {@link OverheardCommentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OverheardComment> createSpecification(OverheardCommentCriteria criteria) {
        Specification<OverheardComment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OverheardComment_.id));
            }
            if (criteria.getContent() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContent(), OverheardComment_.content));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), OverheardComment_.date));
            }
            if (criteria.getRanking() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRanking(), OverheardComment_.ranking));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(OverheardComment_.user, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getPostId() != null) {
                specification = specification.and(buildSpecification(criteria.getPostId(),
                    root -> root.join(OverheardComment_.post, JoinType.LEFT).get(Post_.id)));
            }
        }
        return specification;
    }
}
