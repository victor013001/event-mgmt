package co.com.techtest.sqs.listener.handler.purchase;

import co.com.techtest.sqs.listener.processor.purchase.PurchaseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import static co.com.techtest.model.util.enums.OperationType.PROCESS_PURCHASE;

@Component
@RequiredArgsConstructor
public class PurchaseHandler {

    private final PurchaseProcessor purchaseProcessor;

    public Mono<Void> handle(Message message) {
        return purchaseProcessor.execute(message, PROCESS_PURCHASE);
    }
}
