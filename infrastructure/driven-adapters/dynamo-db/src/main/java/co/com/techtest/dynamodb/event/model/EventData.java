package co.com.techtest.dynamodb.event.model;

import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class EventData {

  @Getter(onMethod = @__({@DynamoDbPartitionKey}))
  private String id;

  @Getter(onMethod_ = {@DynamoDbAttribute("name")})
  private String name;

  @Getter(onMethod_ = {@DynamoDbAttribute("date")})
  private Long date;

  @Getter(onMethod_ = {@DynamoDbAttribute("place")})
  private String place;

  @Getter(onMethod_ = {@DynamoDbAttribute("capacity")})
  private Long capacity;

  @Getter(onMethod_ = {@DynamoDbAttribute("createdBy")})
  private String createdBy;

  @Getter(onMethod_ = {@DynamoDbAttribute("createdAt")})
  private Long createdAt;
}

