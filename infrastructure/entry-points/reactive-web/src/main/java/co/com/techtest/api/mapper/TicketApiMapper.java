package co.com.techtest.api.mapper;

import co.com.techtest.api.dto.request.ticket.PlaceTicketReqParams;
import co.com.techtest.api.dto.response.ticket.TicketResponse;
import co.com.techtest.model.ticket.Ticket;
import co.com.techtest.model.ticket.TicketParameter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TicketApiMapper {
    TicketApiMapper MAPPER = Mappers.getMapper(TicketApiMapper.class);

    @Mapping(target = "status", expression = "java(ticket.currentStatus().name())")
    TicketResponse toResponse(Ticket ticket);

    @Mapping(target = "userId", source = "xUserId")
    TicketParameter toDomain(PlaceTicketReqParams params);
}
