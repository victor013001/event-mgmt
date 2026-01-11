package co.com.techtest.model.util.enums.ticket;

import java.util.EnumSet;
import java.util.Set;

public enum TicketStatus {
    RESERVED,
    PENDING_CONFIRMATION,
    SOLD,
    COMPLIMENTARY,
    EXPIRED;

    private static final Set<TicketStatus> NON_MODIFIABLE_STATUSES = EnumSet.of(SOLD, COMPLIMENTARY, EXPIRED);
    private static final Set<TicketStatus> NON_REVERSIBLE_RESERVED = EnumSet.of(SOLD, COMPLIMENTARY);

    public static boolean isModifiableStatus(TicketStatus ticketStatus) {
        return !NON_MODIFIABLE_STATUSES.contains(ticketStatus);
    }

    public static boolean isNonReversibleReversed(TicketStatus ticketStatus) {
        return NON_REVERSIBLE_RESERVED.contains(ticketStatus);
    }
}
