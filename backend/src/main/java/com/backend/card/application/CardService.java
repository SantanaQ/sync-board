package com.backend.card.application;

import com.backend.board_column.domain.BoardColumn;
import com.backend.board_column.infrastructure.BoardColumnRepository;
import com.backend.card.api.*;
import com.backend.card.domain.Card;
import com.backend.card.infrastructure.CardRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CardService {

    private final ProjectAuthorizationService projectAuthorizationService;

    private final BoardColumnRepository boardColumnRepository;
    private final CardRepository cardRepository;

    public CardService(
            ProjectAuthorizationService projectAuthorizationService,
            BoardColumnRepository boardColumnRepository,
            CardRepository cardRepository
    ) {
        this.projectAuthorizationService = projectAuthorizationService;
        this.boardColumnRepository = boardColumnRepository;
        this.cardRepository = cardRepository;
    }

    public CardResponse getCard(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            UUID cardId
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW);

        Card card = requirePresence(projectId, boardId, columnId, cardId);

        return toResponse(card);
    }

    public List<CardListResponse> getCards(
            UUID projectId,
            UUID boardId,
            UUID columnId
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_VIEW);

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

    @Transactional
    public CardResponse createCard(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            CreateCardRequest request
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_CREATE);

        BoardColumn column = boardColumnRepository
                .findInHierarchy(projectId, boardId, columnId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Column with id " + columnId + " not found.")
                );

        BigDecimal position = cardRepository.findMaxPositionByColumnId(columnId);

        Card card = new Card(column, request.title(), request.description(), position);

        cardRepository.save(card);

        return toResponse(card);
    }

    @Transactional
    public CardResponse updateCard(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            UUID cardId,
            UpdateCardRequest request
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE);

        Card card = requirePresence(projectId, boardId, columnId, cardId);
        card.setTitle(request.title());
        card.setDescription(request.description());
        card.setVersion(card.version() + 1);
        card.setUpdatedAt(Instant.now());

        cardRepository.save(card);
        return toResponse(card);
    }

    @Transactional
    public void deleteCard(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            UUID cardId
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_DELETE);

        Card card = requirePresence(projectId, boardId, columnId, cardId);

        cardRepository.delete(card);
    }

    @Transactional
    public CardResponse moveCard(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            UUID cardId,
            MoveCardRequest request
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE);

        Card card = requirePresence(projectId, boardId, columnId, cardId);

        BoardColumn newColumn = boardColumnRepository
                .findInHierarchy(
                        projectId,
                        boardId,
                        request.newColumnId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Column with id " + request.newColumnId() + " not found."
                        )
                );

        card.setColumn(newColumn);

        return toResponse(card);
    }


    private Card requirePresence(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            UUID cardId
    ) {
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
                card.position(),
                card.createdAt(),
                card.updatedAt()
        );
    }

}
