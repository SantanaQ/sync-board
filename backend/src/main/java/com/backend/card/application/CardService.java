package com.backend.card.application;

import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.card.api.CardListResponse;
import com.backend.card.api.CardResponse;
import com.backend.card.api.CreateCardRequest;
import com.backend.card.api.UpdateCardRequest;
import com.backend.card.domain.Card;
import com.backend.card.infrastructure.CardRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    private final CurrentUserService currentUserService;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final BoardColumnRepository boardColumnRepository;
    private final CardRepository cardRepository;

    public CardService(CurrentUserService currentUserService,
                       ProjectAuthorizationService projectAuthorizationService,
                       BoardColumnRepository boardColumnRepository,
                       CardRepository cardRepository) {
        this.currentUserService = currentUserService;
        this.projectAuthorizationService = projectAuthorizationService;
        this.boardColumnRepository = boardColumnRepository;
        this.cardRepository = cardRepository;
    }

    public CardResponse getCard(UUID projectId,
                                UUID boardId,
                                UUID columnId,
                                UUID cardId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

        Card card = requirePresence(projectId, boardId, columnId, cardId);

        return toResponse(card);
    }

    public List<CardListResponse> getCards(UUID projectId,
                                           UUID boardId,
                                           UUID columnId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requireMembership(projectId, currentUser);

        return cardRepository
                .findAllInHierarchy(projectId, boardId, columnId)
                .stream()
                .map(card ->
                        new CardListResponse(
                                card.id(),
                                card.title(),
                                card.description(),
                                card.position()
                        )
                ).toList();
    }

    public CardResponse createCard(UUID projectId,
                                   UUID boardId,
                                   UUID columnId,
                                   CreateCardRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.CARD_CREATE
        );

        BoardColumn column = boardColumnRepository
                .findInHierarchy(projectId, boardId, columnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Column with id " + columnId + " not found.")
                );

        BigDecimal position = cardRepository.findMaxPositionByColumnId(columnId);

        Card card = new Card(column, request.title(), request.description(), position);

        Card saved = cardRepository.save(card);

        return toResponse(saved);
    }

    public CardResponse updateCard(UUID projectId,
                                   UUID boardId,
                                   UUID columnId,
                                   UUID cardId,
                                   UpdateCardRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.CARD_UPDATE
        );

        Card card = requirePresence(projectId, boardId, columnId, cardId);
        card.setTitle(request.title());
        card.setDescription(request.description());
        card.setVersion(card.version().add(BigInteger.ONE));
        card.setUpdatedAt(Instant.now());

        Card saved = cardRepository.save(card);
        return toResponse(saved);
    }

    public void deleteCard(UUID projectId,
                           UUID boardId,
                           UUID columnId,
                           UUID cardId) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.CARD_DELETE
        );

        Card card = requirePresence(projectId, boardId, columnId, cardId);

        cardRepository.delete(card);
    }

    private Card requirePresence(UUID projectId, UUID boardId, UUID columnId, UUID cardId) {
        return cardRepository
                .findInHierarchy(projectId, boardId, columnId, cardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card with id " + cardId + " not found.")
                );
    }

    private CardResponse toResponse(Card card) {
        return new CardResponse(
                card.id(),
                card.column().id(),
                card.title(),
                card.description(),
                card.createdAt(),
                card.updatedAt()
        );
    }

}
