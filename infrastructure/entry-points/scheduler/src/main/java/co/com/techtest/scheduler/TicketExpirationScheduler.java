package co.com.techtest.scheduler;

import co.com.techtest.usecase.orchestrator.TicketExpirationOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketExpirationScheduler {

    private final TicketExpirationOrchestrator orchestrator;

    private static final String EXPIRE_TICKETS_REQUEST = "Starting ticket expiration process";
    private static final String EXPIRE_TICKETS_RESPONSE = "Ticket expiration process completed successfully";
    private static final String EXPIRE_TICKETS_ERROR_RESPONSE = "Expire Tickets Scheduler Error";

    @Scheduled(fixedDelayString = "${scheduler.ticket-expiration.fixed-delay}")
    public void expireTickets() {
        orchestrator.expireReservedTickets()
                .doOnSubscribe(_ -> log.info(EXPIRE_TICKETS_REQUEST))
                .doOnNext(unused -> log.info(EXPIRE_TICKETS_RESPONSE))
                .doOnError(error -> log.error(EXPIRE_TICKETS_ERROR_RESPONSE))
                .subscribe();
    }
}
