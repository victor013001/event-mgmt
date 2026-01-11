package co.com.techtest.model.purchase.gateway;

import co.com.techtest.model.purchase.PurchaseParameter;
import reactor.core.publisher.Mono;

public interface PurchaseGateway {
    Mono<PurchaseParameter> executePurchase(PurchaseParameter parameter);
}
