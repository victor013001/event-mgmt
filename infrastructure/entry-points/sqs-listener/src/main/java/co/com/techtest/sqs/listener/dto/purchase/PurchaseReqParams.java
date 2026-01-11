package co.com.techtest.sqs.listener.dto.purchase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record PurchaseReqParams(String flowId, String userId, String ticketId) {
}
