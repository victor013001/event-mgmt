package co.com.techtest.sqs.listener;

import co.com.techtest.sqs.listener.handler.purchase.PurchaseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

import static co.com.techtest.sqs.listener.utils.CleanBodyUtil.cleanBody;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final PurchaseHandler purchaseHandler;

    @Override
    public Mono<Void> apply(Message message) {
        String cleanBody = cleanBody(message.body());
        Message cleanMessage = message.toBuilder().body(cleanBody).build();
        return purchaseHandler.handle(cleanMessage);
    }
}
