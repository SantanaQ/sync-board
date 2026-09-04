package com.backend.card.api;

import com.backend.card.application.CardReorderingService;
import com.backend.card.application.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/boards/{boardId}/columns/{columnId}/cards")
public class CardController {

    private final CardService cardService;
    private final CardReorderingService cardReorderingService;

    public CardController(CardService cardService, CardReorderingService cardReorderingService) {
        this.cardService = cardService;
        this.cardReorderingService = cardReorderingService;
    }

    @GetMapping("/{cardId}")
    public CardResponse getCard(@PathVariable("projectId") UUID projectId,
                                @PathVariable("boardId") UUID boardId,
                                @PathVariable("columnId") UUID columnId,
                                @PathVariable("cardId") UUID cardId) {
        return cardService.getCard(projectId, boardId, columnId, cardId);
    }

    @GetMapping
    public List<CardListResponse> getCards(@PathVariable("projectId") UUID projectId,
                                           @PathVariable("boardId") UUID boardId,
                                           @PathVariable("columnId") UUID columnId) {
        return cardService.getCards(projectId, boardId, columnId);
    }

    @PostMapping
    public CardResponse createCard(@PathVariable("projectId") UUID projectId,
                                   @PathVariable("boardId") UUID boardId,
                                   @PathVariable("columnId") UUID columnId,
                                   @Valid @RequestBody CreateCardRequest request) {
        return cardService.createCard(projectId, boardId, columnId, request);
    }

    @PutMapping("/{cardId}")
    public CardResponse updateCard(@PathVariable("projectId") UUID projectId,
                                   @PathVariable("boardId") UUID boardId,
                                   @PathVariable("columnId") UUID columnId,
                                   @PathVariable("cardId") UUID cardId,
                                   @Valid @RequestBody UpdateCardRequest request) {
        return cardService.updateCard(projectId, boardId, columnId, cardId, request);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable("projectId") UUID projectId,
                                           @PathVariable("boardId") UUID boardId,
                                           @PathVariable("columnId") UUID columnId,
                                           @PathVariable("cardId") UUID cardId) {
        cardService.deleteCard(projectId, boardId, columnId, cardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cardId}")
    public CardResponse reorderCard(@PathVariable("projectId") UUID projectId,
                                    @PathVariable("boardId") UUID boardId,
                                    @PathVariable("columnId") UUID columnId,
                                    @PathVariable("cardId") UUID cardId,
                                    @Valid @RequestBody ReorderCardRequest request) {
        return cardReorderingService.reorderCard(projectId, boardId, columnId, cardId, request);
    }

    @PostMapping("/{cardId}")
    public CardResponse switchColumn() {
        return null;
    }



}
