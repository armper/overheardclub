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
 * Criteria class for the {@link com.perea.overheard.domain.Post} entity. This class is used
 * in {@link com.perea.overheard.web.rest.PostResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /posts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PostCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter content;

    private InstantFilter date;

    private LongFilter rankingId;

    private LongFilter commentId;

    private LongFilter userId;

    private LongFilter topicId;

    public PostCriteria() {
    }

    public PostCriteria(PostCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.rankingId = other.rankingId == null ? null : other.rankingId.copy();
        this.commentId = other.commentId == null ? null : other.commentId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.topicId = other.topicId == null ? null : other.topicId.copy();
    }

    @Override
    public PostCriteria copy() {
        return new PostCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
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

    public LongFilter getRankingId() {
        return rankingId;
    }

    public void setRankingId(LongFilter rankingId) {
        this.rankingId = rankingId;
    }

    public LongFilter getCommentId() {
        return commentId;
    }

    public void setCommentId(LongFilter commentId) {
        this.commentId = commentId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getTopicId() {
        return topicId;
    }

    public void setTopicId(LongFilter topicId) {
        this.topicId = topicId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PostCriteria that = (PostCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(content, that.content) &&
            Objects.equals(date, that.date) &&
            Objects.equals(rankingId, that.rankingId) &&
            Objects.equals(commentId, that.commentId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(topicId, that.topicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        title,
        content,
        date,
        rankingId,
        commentId,
        userId,
        topicId
        );
    }

    @Override
    public String toString() {
        return "PostCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (title != null ? "title=" + title + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (date != null ? "date=" + date + ", " : "") +
                (rankingId != null ? "rankingId=" + rankingId + ", " : "") +
                (commentId != null ? "commentId=" + commentId + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (topicId != null ? "topicId=" + topicId + ", " : "") +
            "}";
    }

}
