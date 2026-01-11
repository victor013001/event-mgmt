package co.com.techtest.dynamodb.event.mapper;

import co.com.techtest.dynamodb.event.model.EventData;
import co.com.techtest.model.event.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventDataMapper {

  @Mapping(source = "date", target = "date", qualifiedByName = "stringToLocalDateTime")
  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "place", target = "place")
  @Mapping(source = "capacity", target = "capacity")
  @Mapping(source = "createdBy", target = "createdBy")
  @Mapping(source = "createdAt", target = "createdAt")
  Event toDomain(EventData eventData);

  @Mapping(source = "date", target = "date", qualifiedByName = "localDateTimeToString")
  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "place", target = "place")
  @Mapping(source = "capacity", target = "capacity")
  @Mapping(source = "createdBy", target = "createdBy")
  @Mapping(source = "createdAt", target = "createdAt")
  EventData toData(Event event);

  @Named("stringToLocalDateTime")
  default LocalDateTime stringToLocalDateTime(String value) {
    return Objects.isNull(value) ? null : LocalDateTime.parse(value);
  }

  @Named("localDateTimeToString")
  default String localDateTimeToString(LocalDateTime value) {
    return Objects.isNull(value) ? null : value.toString();
  }
}
