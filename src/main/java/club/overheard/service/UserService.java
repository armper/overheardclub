package club.overheard.service;

import club.overheard.config.JwtService;
import club.overheard.domain.Role;
import club.overheard.domain.User;
import club.overheard.dto.Dtos.*;
import club.overheard.repo.PostRepository;
import club.overheard.repo.UserRepository;
import club.overheard.web.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository users;
    private final PostRepository posts;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public UserService(UserRepository users, PostRepository posts, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.posts = posts;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        if (users.existsByUsernameIgnoreCase(req.username())) {
            throw ApiException.conflict("That username is taken.");
        }
        if (users.existsByEmailIgnoreCase(req.email())) {
            throw ApiException.conflict("That email is already registered.");
        }
        User u = new User();
        u.setUsername(req.username().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setDisplayName(req.displayName().trim());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.USER);
        u = users.save(u);
        return new AuthResponse(jwt.issue(u.getId(), u.getUsername()), view(u));
    }

    public AuthResponse login(LoginRequest req) {
        User u = users.findByUsernameIgnoreCase(req.username().trim())
            .or(() -> users.findByEmailIgnoreCase(req.username().trim()))
            .filter(user -> encoder.matches(req.password(), user.getPasswordHash()))
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Wrong username or password."));
        return new AuthResponse(jwt.issue(u.getId(), u.getUsername()), view(u));
    }

    public UserView updateProfile(User user, UpdateProfileRequest req) {
        String email = req.email().trim().toLowerCase();
        if (!email.equalsIgnoreCase(user.getEmail()) && users.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("That email is already registered.");
        }
        user.setEmail(email);
        user.setDisplayName(req.displayName().trim());
        return view(users.save(user));
    }

    public void changePassword(User user, ChangePasswordRequest req) {
        if (!encoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("Current password is incorrect.");
        }
        user.setPasswordHash(encoder.encode(req.newPassword()));
        users.save(user);
    }

    @Transactional(readOnly = true)
    public List<AdminUserView> listAll() {
        return users.findAll().stream()
            .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
            .map(u -> new AdminUserView(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(), u.getRole().name(),
                u.getCreatedAt(), posts.countByAuthorId(u.getId())))
            .toList();
    }

    public AdminUserView setRole(Long userId, String role, User actor) {
        Role newRole;
        try {
            newRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Unknown role.");
        }
        User target = users.findById(userId).orElseThrow(() -> ApiException.notFound("User"));
        if (target.getId().equals(actor.getId()) && newRole != Role.ADMIN) {
            throw ApiException.badRequest("You can't remove your own admin role.");
        }
        target.setRole(newRole);
        target = users.save(target);
        return new AdminUserView(target.getId(), target.getUsername(), target.getDisplayName(), target.getEmail(),
            target.getRole().name(), target.getCreatedAt(), posts.countByAuthorId(target.getId()));
    }

    public static UserView view(User u) {
        return new UserView(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(), u.getRole().name(), u.getCreatedAt());
    }
}
