package co.com.techtest.sqs.sender;

import co.com.techtest.sqs.sender.config.SQSSenderProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Slf4j
@Service
public class SQSSender /*implements SomeGateway*/ {
  private final SQSSenderProperties properties;
  private final SqsAsyncClient client;

  public SQSSender(SQSSenderProperties properties, @Qualifier("sqsClientSender") SqsAsyncClient client) {
    this.properties = properties;
    this.client = client;
  }

  public Mono<String> send(String message) {
    return Mono.fromCallable(() -> buildRequest(message))
        .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
        .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
        .map(SendMessageResponse::messageId);
  }

  private SendMessageRequest buildRequest(String message) {
    return SendMessageRequest.builder()
        .queueUrl(properties.queueUrl())
        .messageBody(message)
        .build();
  }
}
