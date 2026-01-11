package co.com.techtest.sqs.listener.helper;

import co.com.techtest.sqs.listener.SQSProcessor;
import co.com.techtest.sqs.listener.config.SQSProperties;
import co.com.techtest.sqs.listener.handler.purchase.PurchaseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SQSListenerTest {

    @Mock
    private SqsAsyncClient asyncClient;

    @Mock
    private PurchaseHandler purchaseHandler;

    private SQSProperties sqsProperties;
    private SQSListener sqsListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sqsProperties = new SQSProperties(
                "us-east-1",
                "http://localhost:4566",
                "http://localhost:4566/00000000000/queueName",
                20,
                30,
                10,
                1
        );

        Message message = Message.builder().body("message").build();
        DeleteMessageResponse deleteMessageResponse = DeleteMessageResponse.builder().build();
        ReceiveMessageResponse messageResponse = ReceiveMessageResponse.builder().messages(message).build();

        when(asyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(messageResponse));
        when(asyncClient.deleteMessage(any(DeleteMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(deleteMessageResponse));
        when(purchaseHandler.handle(any(Message.class)))
                .thenReturn(Mono.empty());

        sqsListener = SQSListener.builder()
                .client(asyncClient)
                .properties(sqsProperties)
                .processor(new SQSProcessor(purchaseHandler))
                .operation("operation")
                .build();
    }

    @Test
    void shouldListenToSQSMessages() {
        Flux<Void> flow = ReflectionTestUtils.invokeMethod(sqsListener, "listen");

        StepVerifier.create(flow)
                .verifyComplete();
    }
}
