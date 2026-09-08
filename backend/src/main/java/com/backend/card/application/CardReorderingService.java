package com.backend.card.application;

import com.backend.card.api.CardResponse;
import com.backend.card.api.ReorderCardRequest;
import com.backend.card.domain.Card;
import com.backend.card.infrastructure.CardRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.reordering.RebalancedNeighbors;
import com.backend.common.reordering.ReorderingService;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CardReorderingService {

    private final ProjectAuthorizationService projectAuthorizationService;
    private final ReorderingService<Card> reorderingService;

    private final CardRepository cardRepository;

    public CardReorderingService(
            ProjectAuthorizationService projectAuthorizationService,
            CardRepository cardRepository,
            @Qualifier("cardReordering") ReorderingService<Card> reorderingService
    ) {
        this.projectAuthorizationService = projectAuthorizationService;
        this.cardRepository = cardRepository;
        this.reorderingService = reorderingService;
    }


    @Transactional
    public CardResponse reorderCard(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            UUID cardId,
            ReorderCardRequest request
    ) {
        projectAuthorizationService.requirePermission(projectId, ProjectPermission.CARD_UPDATE);

        Card card = requirePresence(projectId, boardId, columnId, cardId);

        Card before = request.beforeCardId() != null
                ? requirePresence(projectId, boardId, columnId, request.beforeCardId())
                : null;

        Card after = request.afterCardId() != null
                ? requirePresence(projectId, boardId, columnId, request.afterCardId())
                : null;

        reorderingService.validateNeighbors(card, before, after);

        int cardCount = cardRepository.countInHierarchy(projectId, boardId, columnId);

        reorderingService.reorder(
                card,
                before,
                after,
                cardCount,
                () -> rebalance(projectId, boardId, columnId, before, after)
        );

        return toResponse(card);
    }

    private RebalancedNeighbors<Card> rebalance(
            UUID projectId,
            UUID boardId,
            UUID columnId,
            Card before,
            Card after
    ) {
        List<Card> cards = cardRepository.findAllInHierarchy(projectId, boardId, columnId);

        int padding = reorderingService.padding();
        int position = padding;
        for (Card card : cards) {
            card.setPosition(BigDecimal.valueOf(position));
            position += padding;

            if(card.id().equals(before.id())) {
                before = card;
            }

            if(card.id().equals(after.id())) {
                after = card;
            }
        }
        return new RebalancedNeighbors<>(before, after);
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
                card.position(),
                card.createdAt(),
                card.updatedAt()
        );
    }

}
