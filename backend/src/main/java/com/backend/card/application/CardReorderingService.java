package com.backend.card.application;

import com.backend.card.api.CardResponse;
import com.backend.card.api.ReorderCardRequest;
import com.backend.card.domain.Card;
import com.backend.card.infrastructure.CardRepository;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.reordering.PositionCalculator;
import com.backend.common.reordering.ReorderingService;
import com.backend.project_member.application.ProjectAuthorizationService;
import com.backend.project_member.domain.ProjectPermission;
import com.backend.user.application.CurrentUserService;
import com.backend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CardReorderingService {

    private static final BigDecimal MIN_POSITION_GAP = new BigDecimal("0.00001");
    private static final BigDecimal POSITION_PADDING = new BigDecimal("1000");
    private static final int POSITION_SCALE = 10; // numeric scale in db

    private final CurrentUserService currentUserService;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ReorderingService<Card> reorderingService;

    private final CardRepository cardRepository;


    public CardReorderingService(CurrentUserService currentUserService,
                                 ProjectAuthorizationService projectAuthorizationService,
                                 CardRepository cardRepository) {
        this.currentUserService = currentUserService;
        this.projectAuthorizationService = projectAuthorizationService;
        this.cardRepository = cardRepository;
        PositionCalculator posCalculator = new PositionCalculator(
                MIN_POSITION_GAP,
                POSITION_PADDING,
                POSITION_SCALE
        );
        this.reorderingService = new ReorderingService<>(posCalculator);
    }


    @Transactional
    public CardResponse reorderCard(UUID projectId,
                                    UUID boardId,
                                    UUID columnId,
                                    UUID cardId,
                                    ReorderCardRequest request) {
        User currentUser = currentUserService.get();

        projectAuthorizationService.requirePermission(
                projectId,
                currentUser,
                ProjectPermission.CARD_UPDATE
        );

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
                () -> rebalance(projectId, boardId, columnId)
        );

        return toResponse(card);
    }

    private void rebalance(UUID projectId, UUID boardId, UUID columnId) {
        List<Card> cards = cardRepository.findAllInHierarchy(projectId, boardId, columnId);

        int position = POSITION_PADDING.intValue();
        for (Card card : cards) {
            card.setPosition(BigDecimal.valueOf(position));
            position += POSITION_PADDING.intValue();
        }
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
