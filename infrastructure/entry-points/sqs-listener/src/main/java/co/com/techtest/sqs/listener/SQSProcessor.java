package co.com.techtest.sqs.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    // private final MyUseCase myUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.empty();
        // return myUseCase.doAny(message.body());
    }
}
