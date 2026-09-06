package club.overheard.dto;

import club.overheard.domain.ReactionType;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** All request and response shapes for the JSON API, in one place. */
public final class Dtos {
    private Dtos() {}

    // ---- auth & account ----
    public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "letters, digits, dot, dash or underscore only") String username,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(min = 1, max = 50) String displayName) {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record AuthResponse(String token, UserView user) {}

    public record UserView(Long id, String username, String displayName, String email, String role, Instant createdAt) {}

    public record UpdateProfileRequest(@NotBlank @Size(min = 1, max = 50) String displayName,
                                       @NotBlank @Email @Size(max = 254) String email) {}

    public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank @Size(min = 8, max = 100) String newPassword) {}

    // ---- topics ----
    public record TopicRequest(@NotBlank @Size(min = 2, max = 80) String title) {}

    public record TopicView(Long id, String title, long postCount, AuthorView createdBy, Instant createdAt) {}

    // ---- posts ----
    public record AuthorView(Long id, String displayName) {}

    public record TopicRef(Long id, String title) {}

    public record PostRequest(@NotBlank @Size(min = 2, max = 80) String title,
                              @NotBlank @Size(min = 2, max = 4096) String content,
                              @NotNull Long topicId) {}

    public record PostView(Long id, String title, String content, AuthorView author, TopicRef topic,
                           Instant createdAt, Instant updatedAt, Map<ReactionType, Long> reactions,
                           ReactionType myReaction, long commentCount, boolean canEdit) {}

    public record PageView<T>(List<T> items, int page, int size, long total, boolean hasMore) {}

    public record ReactionRequest(@NotNull ReactionType type) {}

    // ---- comments ----
    public record CommentRequest(@NotBlank @Size(min = 1, max = 4096) String content) {}

    public record CommentView(Long id, String content, AuthorView author, Instant createdAt, boolean canDelete) {}

    // ---- admin ----
    public record AdminUserView(Long id, String username, String displayName, String email, String role, Instant createdAt, long postCount) {}

    public record RoleRequest(@NotBlank String role) {}

    public record ErrorView(String message, Map<String, String> fields) {}
}
