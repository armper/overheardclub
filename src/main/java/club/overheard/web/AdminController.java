package club.overheard.web;

import club.overheard.config.CurrentUser;
import club.overheard.dto.Dtos.AdminUserView;
import club.overheard.dto.Dtos.RoleRequest;
import club.overheard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService users;

    public AdminController(UserService users) {
        this.users = users;
    }

    @GetMapping("/users")
    public List<AdminUserView> users() {
        return users.listAll();
    }

    @PutMapping("/users/{id}/role")
    public AdminUserView setRole(@PathVariable Long id, @Valid @RequestBody RoleRequest req, @AuthenticationPrincipal CurrentUser me) {
        return users.setRole(id, req.role(), me.user());
    }
}
