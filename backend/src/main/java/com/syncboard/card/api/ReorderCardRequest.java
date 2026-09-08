package com.syncboard.card.api;

import java.util.UUID;

public record ReorderCardRequest (
        @org.hibernate.validator.constraints.UUID
        UUID beforeCardId,
        @org.hibernate.validator.constraints.UUID
        UUID afterCardId
){
}
