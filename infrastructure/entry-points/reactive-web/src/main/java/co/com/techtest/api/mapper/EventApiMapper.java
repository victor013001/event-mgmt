package co.com.techtest.api.mapper;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.dto.response.event.EventResponse;
import co.com.techtest.model.event.Event;
import co.com.techtest.model.event.EventParameter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventApiMapper {

    EventApiMapper MAPPER = Mappers.getMapper(EventApiMapper.class);

    EventParameter toParameter(CreateEventRequest request);

    EventResponse toResponse(Event event);
}
