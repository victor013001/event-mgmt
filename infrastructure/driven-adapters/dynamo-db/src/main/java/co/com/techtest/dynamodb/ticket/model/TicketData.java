package co.com.techtest.dynamodb.ticket.model;

import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class TicketData {

    @Getter(onMethod = @__({@DynamoDbPartitionKey}))
    private String ticketId;

    @Getter(onMethod_ = {@DynamoDbAttribute("eventId")})
    private String eventId;

    @Getter(onMethod_ = {@DynamoDbAttribute("userId")})
    private String userId;

    @Getter(onMethod_ = {@DynamoDbAttribute("quantity")})
    private Integer quantity;

    @Getter(onMethod_ = {@DynamoDbAttribute("status")})
    private String status;

    @Getter(onMethod_ = {@DynamoDbAttribute("createdAt")})
    private Long createdAt;

    @Getter(onMethod_ = {@DynamoDbAttribute("expiresAt")})
    private Long expiresAt;
}