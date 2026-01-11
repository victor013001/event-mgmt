package co.com.techtest.dynamodb.event.repository;

import co.com.techtest.dynamodb.event.mapper.EventDataMapper;
import co.com.techtest.dynamodb.event.model.EventData;
import co.com.techtest.dynamodb.repository.GenericOperationsRepository;
import co.com.techtest.model.event.Event;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class EventRepository extends GenericOperationsRepository<Event, String, EventData> {

  private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
  private final EventDataMapper eventDataMapper;
  private final String tableName;

  public EventRepository(@Qualifier("getDynamoDbEnhancedAsyncClient") DynamoDbEnhancedAsyncClient client,
                         EventDataMapper eventDataMapper,
                         @Value("aws.region.dynamodb.event.tableName") String tableName) {
    super(client, eventDataMapper::toDomain, eventDataMapper::toData, tableName);
    this.dynamoDbEnhancedAsyncClient = client;
    this.eventDataMapper = eventDataMapper;
    this.tableName = tableName;
  }
}
