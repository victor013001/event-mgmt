package co.com.techtest.dynamodb.ticket.mapper;

import co.com.techtest.dynamodb.ticket.model.TicketData;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.util.enums.ticket.TicketStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TicketDataMapper {

    @Mapping(source = "status", target = "currentStatus", qualifiedByName = "stringToTicketStatus")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "longToLocalDateTime")
    @Mapping(source = "expiresAt", target = "expiresAt", qualifiedByName = "longToLocalDateTime")
    @Mapping(target = "ticketStatusEvents", ignore = true)
    Ticket toDomain(TicketData ticketData);

    @Mapping(source = "currentStatus", target = "status", qualifiedByName = "ticketStatusToString")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "localDateTimeToLong")
    @Mapping(source = "expiresAt", target = "expiresAt", qualifiedByName = "localDateTimeToLong")
    TicketData toData(Ticket ticket);

    @Named("stringToTicketStatus")
    default TicketStatus stringToTicketStatus(String value) {
        return Objects.isNull(value) ? null : TicketStatus.valueOf(value);
    }

    @Named("ticketStatusToString")
    default String ticketStatusToString(TicketStatus value) {
        return Objects.isNull(value) ? null : value.name();
    }

    @Named("longToLocalDateTime")
    default LocalDateTime longToLocalDateTime(Long value) {
        return Objects.isNull(value) ? null : LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC);
    }

    @Named("localDateTimeToLong")
    default Long localDateTimeToLong(LocalDateTime value) {
        return Objects.isNull(value) ? null : value.toEpochSecond(ZoneOffset.UTC);
    }
}
