package club.overheard.web;

import club.overheard.config.CurrentUser;
import club.overheard.dto.Dtos.*;
import club.overheard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    @PostMapping("/auth/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return users.register(req);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return users.login(req);
    }

    @GetMapping("/me")
    public UserView me(@AuthenticationPrincipal CurrentUser me) {
        return UserService.view(me.user());
    }

    @PutMapping("/me")
    public UserView updateProfile(@AuthenticationPrincipal CurrentUser me, @Valid @RequestBody UpdateProfileRequest req) {
        return users.updateProfile(me.user(), req);
    }

    @PutMapping("/me/password")
    public void changePassword(@AuthenticationPrincipal CurrentUser me, @Valid @RequestBody ChangePasswordRequest req) {
        users.changePassword(me.user(), req);
    }
}
