package co.com.techtest.api;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.dto.response.inventory.InventoryResponse;
import co.com.techtest.api.dto.response.ticket.TicketResponse;
import co.com.techtest.api.handler.event.CreateEventHandler;
import co.com.techtest.api.handler.event.GetEventsHandler;
import co.com.techtest.api.handler.inventory.GetEventAvailabilityHandler;
import co.com.techtest.api.handler.ticket.PlaceTicketHandler;
import co.com.techtest.model.util.enums.OperationType;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static co.com.techtest.api.utils.DocumentationUtilApi.document;
import static co.com.techtest.api.utils.HeadersUtilApi.requireHeaders;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(CreateEventHandler createEventHandler, GetEventsHandler getEventsHandler,
                                                         GetEventAvailabilityHandler getEventAvailabilityHandler,
                                                         PlaceTicketHandler placeTicketHandler) {
        return SpringdocRouteBuilder.route()
                .POST(OperationType.CREATE_EVENT.getPath(), request -> requireHeaders(request, createEventHandler::handle),
                        document(OperationType.CREATE_EVENT, CreateEventRequest.class))
                .GET(OperationType.GET_EVENTS.getPath(), request -> requireHeaders(request, getEventsHandler::handle),
                        document(OperationType.GET_EVENTS, Void.class))
                .GET(OperationType.GET_EVENT_AVAILABILITY.getPath(), request -> requireHeaders(request, getEventAvailabilityHandler::handle),
                        document(OperationType.GET_EVENT_AVAILABILITY, InventoryResponse.class))
                .POST(OperationType.PLACE_EVENT_TICKET.getPath(), request -> requireHeaders(request, placeTicketHandler::handle),
                        document(OperationType.PLACE_EVENT_TICKET, TicketResponse.class))
                .build();
    }
}
