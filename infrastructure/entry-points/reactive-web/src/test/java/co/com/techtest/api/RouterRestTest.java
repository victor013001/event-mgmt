package co.com.techtest.api;

import co.com.techtest.api.handler.event.CreateEventHandler;
import co.com.techtest.api.handler.event.GetEventsHandler;
import co.com.techtest.api.handler.inventory.GetEventAvailabilityHandler;
import co.com.techtest.api.handler.ticket.PlaceTicketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private CreateEventHandler createEventHandler;
    @Mock
    private GetEventsHandler getEventsHandler;
    @Mock
    private GetEventAvailabilityHandler getEventAvailabilityHandler;
    @Mock
    private PlaceTicketHandler placeTicketHandler;

    @Test
    void shouldCreateRouterFunction() {
        RouterRest routerRest = new RouterRest();

        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(
                createEventHandler, getEventsHandler, getEventAvailabilityHandler, placeTicketHandler);

        assertNotNull(routerFunction);
    }
}
