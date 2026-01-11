package co.com.techtest.usecase.purchase;

import co.com.techtest.model.purchase.PurchaseParameter;
import co.com.techtest.model.purchase.gateway.PurchaseGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseUseCaseTest {

    @Mock
    private PurchaseGateway purchaseGateway;

    @InjectMocks
    private PurchaseUseCase purchaseUseCase;

    @Test
    void shouldExecutePurchaseSuccessfully() {
        PurchaseParameter parameter = new PurchaseParameter("flow-123", "user-123", "ticket-123");

        when(purchaseGateway.executePurchase(parameter)).thenReturn(Mono.just(parameter));

        StepVerifier.create(purchaseUseCase.executePurchase(parameter))
                .expectNext(parameter)
                .verifyComplete();
    }

    @Test
    void shouldPropagateGatewayError() {
        PurchaseParameter parameter = new PurchaseParameter("flow-123", "user-123", "ticket-123");
        RuntimeException error = new RuntimeException("Purchase failed");

        when(purchaseGateway.executePurchase(parameter)).thenReturn(Mono.error(error));

        StepVerifier.create(purchaseUseCase.executePurchase(parameter))
                .expectError(RuntimeException.class)
                .verify();
    }
}
