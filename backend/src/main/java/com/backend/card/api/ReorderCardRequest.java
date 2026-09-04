package com.backend.card.api;

import java.util.UUID;

public record ReorderCardRequest (
        UUID beforeCardId,
        UUID afterCardId
){
}
