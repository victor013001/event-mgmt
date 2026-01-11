package co.com.techtest.dynamodb.inventory.model;

import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
public class InventoryData {

    @Getter(onMethod = @__({@DynamoDbPartitionKey}))
    private String eventId;

    @Getter(onMethod_ = {@DynamoDbAttribute("capacity")})
    private Long capacity;

    @Getter(onMethod_ = {@DynamoDbAttribute("available")})
    private Long available;

    @Getter(onMethod_ = {@DynamoDbAttribute("reserved")})
    private Long reserved;

    @Getter(onMethod_ = {@DynamoDbAttribute("sold")})
    private Long sold;
}
