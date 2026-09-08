package com.syncboard.card.application;

import com.syncboard.TestDataFactory;
import com.syncboard.board_column.domain.BoardColumn;
import com.syncboard.board_column.infrastructure.BoardColumnRepository;
import com.syncboard.card.api.*;
import com.syncboard.card.domain.Card;
import com.syncboard.card.infrastructure.CardRepository;
import com.syncboard.common.exception.AccessDeniedException;
import com.syncboard.common.exception.ResourceNotFoundException;
import com.syncboard.project_member.application.ProjectAuthorizationService;
import com.syncboard.project_member.domain.MemberRole;
import com.syncboard.project_member.domain.ProjectMember;
import com.syncboard.project_member.domain.ProjectPermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private ProjectAuthorizationService projectAuthorizationService;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void getCard_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> cardService.getCard(projectId, boardId, columnId, cardId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    void getCard_throws_resource_not_found_if_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getCard(projectId, boardId, columnId, cardId))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getCard_returns_card_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Card card = TestDataFactory.card(cardId, columnId);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.of(card));

        CardResponse response = cardService.getCard(projectId, boardId, columnId, cardId);

        assertThat(response.id()).isEqualTo(card.id());
    }

    @Test
    void getCards_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> cardService.getCards(projectId, boardId, columnId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    void getCards_returns_column_cards_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();


        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        UUID card1Id = UUID.randomUUID();
        Card card1 = TestDataFactory.card(card1Id, columnId);
        UUID card2Id = UUID.randomUUID();
        Card card2 = TestDataFactory.card(card2Id, columnId);

        List<Card> cards = List.of(card1, card2);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW))
                .thenReturn(owner);

        when(cardRepository.findAllInHierarchy(projectId, boardId, columnId))
                .thenReturn(cards);

        List<CardListResponse> responses = cardService.getCards(projectId, boardId, columnId);
        assertThat(responses).hasSize(2);
    }

    @Test
    void createCard_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_CREATE))
                .thenThrow(AccessDeniedException.class);

        CreateCardRequest request = TestDataFactory.createCardRequest();
        assertThatThrownBy(() -> cardService.createCard(projectId, boardId, columnId, request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    void createCard_throws_resource_not_found_if_column_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_CREATE))
                .thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
            .thenReturn(Optional.empty());

        CreateCardRequest request = TestDataFactory.createCardRequest();
        assertThatThrownBy(() -> cardService.createCard(projectId, boardId, columnId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void createCard_creates_card_at_last_position_in_column() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();

        BigDecimal lastPosition = BigDecimal.valueOf(12000);

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        BoardColumn column = TestDataFactory.column(columnId, boardId, projectId);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_CREATE))
                .thenReturn(owner);

        when(boardColumnRepository.findInHierarchy(projectId, boardId, columnId))
                .thenReturn(Optional.of(column));

        when(cardRepository.findMaxPositionByColumnId(columnId)).thenReturn(lastPosition);

        CreateCardRequest request = TestDataFactory.createCardRequest();
        CardResponse response = cardService.createCard(projectId, boardId, columnId, request);

        assertThat(response.position().compareTo(lastPosition) == 0);
    }

    @Test
    void updateCard_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenThrow(AccessDeniedException.class);

        UpdateCardRequest request = TestDataFactory.updateCardRequest();
        assertThatThrownBy(() -> cardService.updateCard(projectId, boardId, columnId, cardId, request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    void updateCard_throws_resource_not_found_if_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.empty());

        UpdateCardRequest request = TestDataFactory.updateCardRequest();
        assertThatThrownBy(() -> cardService.updateCard(projectId, boardId, columnId, cardId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void updateCard_updates_card_information_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Card card = TestDataFactory.card(cardId, columnId);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.of(card));


        UpdateCardRequest request = TestDataFactory.updateCardRequest();
        CardResponse response = cardService.updateCard(projectId, boardId, columnId, cardId, request);

        assertThat(response.title()).isSameAs(request.title());
        assertThat(response.description()).isSameAs(request.description());
    }

    @Test
    void updateCard_increments_version_of_card() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Card card = TestDataFactory.card(cardId, columnId);
        long previousVersion = card.version();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.of(card));

        UpdateCardRequest request = TestDataFactory.updateCardRequest();
        cardService.updateCard(projectId, boardId, columnId, cardId, request);

        assertThat(card.version()).isEqualTo(previousVersion + 1);
    }

    @Test
    void deleteCard_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_DELETE))
                .thenThrow(AccessDeniedException.class);

        assertThatThrownBy(() -> cardService.deleteCard(projectId, boardId, columnId, cardId))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    void deleteCard_throws_resource_not_found_if_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_DELETE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.deleteCard(projectId, boardId, columnId, cardId))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void deleteCard_deletes_card_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Card card = TestDataFactory.card(cardId, columnId);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_DELETE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.of(card));

        cardService.deleteCard(projectId, boardId, columnId, cardId);

        verify(cardRepository).delete(card);
    }

    @Test
    void moveCard_throws_access_denied_if_user_does_not_have_permission() {
        UUID projectId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenThrow(AccessDeniedException.class);

        UUID newColumnId = UUID.randomUUID();
        MoveCardRequest request = TestDataFactory.moveCardRequest(newColumnId);
        assertThatThrownBy(() -> cardService.moveCard(projectId, boardId, columnId, cardId, request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(cardRepository);
    }

    @Test
    void moveCard_throws_resource_not_found_if_card_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        UUID newColumnId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.empty());

        MoveCardRequest request = TestDataFactory.moveCardRequest(newColumnId);
        assertThatThrownBy(() -> cardService.moveCard(projectId, boardId, columnId, cardId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void moveCard_throws_resource_not_found_if_column_does_not_exist() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        UUID newColumnId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Card card = TestDataFactory.card(cardId, columnId);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.of(card));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, newColumnId))
                .thenReturn(Optional.empty());

        MoveCardRequest request = TestDataFactory.moveCardRequest(newColumnId);
        assertThatThrownBy(() -> cardService.moveCard(projectId, boardId, columnId, cardId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void moveCard_changes_parent_column_under_valid_conditions() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID boardId = UUID.randomUUID();
        UUID columnId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        UUID newColumnId = UUID.randomUUID();

        ProjectMember owner = TestDataFactory.projectMember(projectId, userId, MemberRole.OWNER);

        Card card = TestDataFactory.card(cardId, columnId);

        BoardColumn newColumn = TestDataFactory.column(newColumnId, boardId, projectId);

        when(projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE))
                .thenReturn(owner);

        when(cardRepository.findInHierarchy(projectId, boardId, columnId, cardId))
                .thenReturn(Optional.of(card));

        when(boardColumnRepository.findInHierarchy(projectId, boardId, newColumnId))
                .thenReturn(Optional.of(newColumn));

        MoveCardRequest request = TestDataFactory.moveCardRequest(newColumnId);
        CardResponse response = cardService.moveCard(projectId, boardId, columnId, cardId, request);

        assertThat(response.columnId()).isEqualTo(newColumnId);
    }


}
