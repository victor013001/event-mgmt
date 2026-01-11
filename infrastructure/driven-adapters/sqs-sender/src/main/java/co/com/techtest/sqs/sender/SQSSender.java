package co.com.techtest.sqs.sender;

import co.com.techtest.model.purchase.PurchaseParameter;
import co.com.techtest.model.purchase.gateway.PurchaseGateway;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.TechnicalException;
import co.com.techtest.parser.ParserUtilityApi;
import co.com.techtest.sqs.sender.config.SQSSenderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class SQSSender implements PurchaseGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    private static final String PURCHASE_PUBLISH_REQUEST = "Purchase SQS Publish Request";
    private static final String PURCHASE_PUBLISH_KEY_REQUEST = "purchaseSqsPublishRQ";
    private static final String PURCHASE_PUBLISH_RESPONSE = "Purchase SQS Publish Response";
    private static final String PURCHASE_PUBLISH_KEY_RESPONSE = "purchaseSqsPublishRS";
    private static final String PURCHASE_PUBLISH_ERROR_RESPONSE = "Purchase SQS Publish Error Response";
    private static final String PURCHASE_PUBLISH_ERROR_KEY_RESPONSE = "purchaseSqsPublishErrorRS";

    public SQSSender(SQSSenderProperties properties, @Qualifier("sqsClientSender") SqsAsyncClient client) {
        this.properties = properties;
        this.client = client;
    }

    @Override
    public Mono<PurchaseParameter> executePurchase(PurchaseParameter parameter) {
        return Mono.just(parameter)
                .map(ParserUtilityApi::parserToString)
                .map(this::buildRequest)
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .then(Mono.just(parameter))
                .doOnSubscribe(subscription -> log.info(PURCHASE_PUBLISH_REQUEST, kv(PURCHASE_PUBLISH_KEY_REQUEST, parameter)))
                .doOnNext(response -> log.info(PURCHASE_PUBLISH_RESPONSE, kv(PURCHASE_PUBLISH_KEY_RESPONSE, response)))
                .doOnError(error -> log.error(PURCHASE_PUBLISH_ERROR_RESPONSE, kv(PURCHASE_PUBLISH_ERROR_KEY_RESPONSE, parameter), error))
                .onErrorResume(exception -> Mono.error(new TechnicalException(exception, TechnicalMessageType.ERROR_EVENT_PUBLISH)));
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}
