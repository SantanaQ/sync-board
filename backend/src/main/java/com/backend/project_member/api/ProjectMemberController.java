package com.backend.project_member.api;

import com.backend.project_member.application.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping
    public List<ProjectMemberResponse> getMembers(@PathVariable UUID projectId) {
        return projectMemberService.getMembers(projectId);
    }

    @GetMapping("/{userId}")
    public ProjectMemberResponse getMember(@PathVariable UUID projectId,
                                           @PathVariable UUID userId) {
        return projectMemberService.getMember(projectId, userId);
    }

    @PostMapping
    public ProjectMemberResponse addMember(@PathVariable UUID projectId,
                                           @Valid @RequestBody AddMemberRequest request) {
        return projectMemberService.addMember(projectId, request);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID projectId,
                                       @PathVariable UUID userId) {
        projectMemberService.removeMember(projectId, userId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}")
    public ProjectMemberResponse updateMember(@PathVariable UUID projectId,
                                              @PathVariable UUID userId,
                                              @Valid @RequestBody UpdateMemberRequest request) {
        return projectMemberService.updateMember(projectId, userId, request);
    }

}
