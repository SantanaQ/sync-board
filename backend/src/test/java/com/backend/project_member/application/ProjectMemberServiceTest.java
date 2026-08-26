package com.backend.project_member.application;

import com.backend.project.infrastructure.ProjectRepository;
import com.backend.project_member.infrastructure.ProjectMemberRepository;
import com.backend.user.application.CurrentUserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectMemberServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository memberRepository;

    @InjectMocks
    private ProjectMemberService projectService;

}
