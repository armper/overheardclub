package com.perea.overheard.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.perea.overheard.domain.OverheardComment} entity. This class is used
 * in {@link com.perea.overheard.web.rest.OverheardCommentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /overheard-comments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OverheardCommentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter content;

    private InstantFilter date;

    private IntegerFilter ranking;

    private LongFilter userId;

    private LongFilter postId;

    public OverheardCommentCriteria() {
    }

    public OverheardCommentCriteria(OverheardCommentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.ranking = other.ranking == null ? null : other.ranking.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.postId = other.postId == null ? null : other.postId.copy();
    }

    @Override
    public OverheardCommentCriteria copy() {
        return new OverheardCommentCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getContent() {
        return content;
    }

    public void setContent(StringFilter content) {
        this.content = content;
    }

    public InstantFilter getDate() {
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public IntegerFilter getRanking() {
        return ranking;
    }

    public void setRanking(IntegerFilter ranking) {
        this.ranking = ranking;
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
        final OverheardCommentCriteria that = (OverheardCommentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(content, that.content) &&
            Objects.equals(date, that.date) &&
            Objects.equals(ranking, that.ranking) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        content,
        date,
        ranking,
        userId,
        postId
        );
    }

    @Override
    public String toString() {
        return "OverheardCommentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (ranking != null ? "ranking=" + ranking + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (postId != null ? "postId=" + postId + ", " : "") +
            "}";
    }

}
