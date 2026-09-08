package com.backend.card.application;

import com.backend.TestDataFactory;
import com.backend.card.api.ReorderCardRequest;
import com.backend.card.domain.Card;
import com.backend.card.infrastructure.CardRepository;
import com.backend.common.exception.AccessDeniedException;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.reordering.ReorderingService;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardReorderingServiceTest {

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ReorderingService<Card> reorderingService;

    @InjectMocks
    private CardReorderingService cardReorderingService;

    @Test
    void reorderCard_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(
                projectId,
                ProjectPermission.CARD_UPDATE
        )).thenThrow(AccessDeniedException.class);

        ReorderCardRequest request =
                new ReorderCardRequest(null, null);

        assertThatThrownBy(() ->
                cardReorderingService.reorderCard(
                        projectId,
                        boardId,
                        columnId,
                        cardId,
                        request
                )
        )
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
        verifyNoInteractions(reorderingService);
    }

    @Test
    void reorderCard_throws_resource_not_found_if_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(cardRepository.findInHierarchy(
                projectId,
                boardId,
                columnId,
                cardId
        )).thenReturn(Optional.empty());

        ReorderCardRequest request =
                new ReorderCardRequest(null, null);

        assertThatThrownBy(() ->
                cardReorderingService.reorderCard(
                        projectId,
                        boardId,
                        columnId,
                        cardId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(reorderingService);
    }

    @Test
    void reorderCard_throws_resource_not_found_if_before_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID beforeId = UUID.randomUUID();

        Card card = TestDataFactory.card(cardId, columnId);

        when(cardRepository.findInHierarchy(
                projectId,
                boardId,
                columnId,
                cardId
        )).thenReturn(Optional.of(card));

        when(cardRepository.findInHierarchy(
                projectId,
                boardId,
                columnId,
                beforeId
        )).thenReturn(Optional.empty());

        ReorderCardRequest request =
                new ReorderCardRequest(beforeId, null);

        assertThatThrownBy(() ->
                cardReorderingService.reorderCard(
                        projectId,
                        boardId,
                        columnId,
                        cardId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(reorderingService);
    }

    @Test
    void reorderCard_throws_resource_not_found_if_after_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        Card card = TestDataFactory.card(cardId, columnId);

        when(cardRepository.findInHierarchy(
                projectId,
                boardId,
                columnId,
                cardId
        )).thenReturn(Optional.of(card));

        when(cardRepository.findInHierarchy(
                projectId,
                boardId,
                columnId,
                afterId
        )).thenReturn(Optional.empty());

        ReorderCardRequest request =
                new ReorderCardRequest(null, afterId);

        assertThatThrownBy(() ->
                cardReorderingService.reorderCard(
                        projectId,
                        boardId,
                        columnId,
                        cardId,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(reorderingService);
    }

    @Test
    void reorderCard_delegates_reordering_to_reordering_service() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID beforeId = UUID.randomUUID();
        UUID afterId = UUID.randomUUID();

        Card card = TestDataFactory.card(cardId, columnId, BigDecimal.valueOf(3000));
        Card before = TestDataFactory.card(beforeId, columnId, BigDecimal.valueOf(1000));
        Card after = TestDataFactory.card(afterId, columnId, BigDecimal.valueOf(2000));

        when(cardRepository.findInHierarchy(
                projectId, boardId, columnId, cardId
        )).thenReturn(Optional.of(card));

        when(cardRepository.findInHierarchy(
                projectId, boardId, columnId, beforeId
        )).thenReturn(Optional.of(before));

        when(cardRepository.findInHierarchy(
                projectId, boardId, columnId, afterId
        )).thenReturn(Optional.of(after));

        when(cardRepository.countInHierarchy(
                projectId, boardId, columnId
        )).thenReturn(3);

        ReorderCardRequest request =
                new ReorderCardRequest(beforeId, afterId);

        cardReorderingService.reorderCard(
                projectId,
                boardId,
                columnId,
                cardId,
                request
        );

        verify(reorderingService).validateNeighbors(
                card,
                before,
                after
        );

        verify(reorderingService).reorder(
                eq(card),
                eq(before),
                eq(after),
                eq(3L),
                any()
        );
    }
}