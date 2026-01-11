package co.com.techtest.usecase.purchase;

import co.com.techtest.model.purchase.PurchaseParameter;
import co.com.techtest.model.purchase.gateway.PurchaseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PurchaseUseCase {

    private final PurchaseGateway purchaseGateway;

    public Mono<PurchaseParameter> executePurchase(PurchaseParameter parameter) {
        return purchaseGateway.executePurchase(parameter);
    }
}
