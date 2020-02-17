package com.perea.overheard.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import com.perea.overheard.domain.enumeration.RankType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.perea.overheard.domain.Ranking} entity. This class is used
 * in {@link com.perea.overheard.web.rest.RankingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rankings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RankingCriteria implements Serializable, Criteria {
    /**
     * Class for filtering RankType
     */
    public static class RankTypeFilter extends Filter<RankType> {

        public RankTypeFilter() {
        }

        public RankTypeFilter(RankTypeFilter filter) {
            super(filter);
        }

        @Override
        public RankTypeFilter copy() {
            return new RankTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private RankTypeFilter rankType;

    private InstantFilter date;

    private LongFilter userId;

    private LongFilter postId;

    public RankingCriteria() {
    }

    public RankingCriteria(RankingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.rankType = other.rankType == null ? null : other.rankType.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.postId = other.postId == null ? null : other.postId.copy();
    }

    @Override
    public RankingCriteria copy() {
        return new RankingCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public RankTypeFilter getRankType() {
        return rankType;
    }

    public void setRankType(RankTypeFilter rankType) {
        this.rankType = rankType;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPostId() {
        return postId;
    }

    public void setPostId(LongFilter postId) {
        this.postId = postId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RankingCriteria that = (RankingCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(rankType, that.rankType) &&
            Objects.equals(date, that.date) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        rankType,
        date,
        userId,
        postId
        );
    }

    @Override
    public String toString() {
        return "RankingCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (rankType != null ? "rankType=" + rankType + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (postId != null ? "postId=" + postId + ", " : "") +
            "}";
    }

}
