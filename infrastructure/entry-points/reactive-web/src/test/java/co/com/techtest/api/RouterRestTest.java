package co.com.techtest.api;

import co.com.techtest.api.handler.event.CreateEventHandler;
import co.com.techtest.api.handler.event.GetEventsHandler;
import co.com.techtest.api.handler.inventory.GetEventAvailabilityHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private CreateEventHandler createEventHandler;

    @Mock
    private GetEventsHandler getEventsHandler;

    @Mock
    private GetEventAvailabilityHandler getEventAvailabilityHandler;

    private WebTestClient webTestClient;
    private RouterRest routerRest;

    @BeforeEach
    void setUp() {
        routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(createEventHandler, getEventsHandler, getEventAvailabilityHandler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testCreateEventEndpoint() {
        when(createEventHandler.handle(any()))
                .thenReturn(ServerResponse.ok().bodyValue("Event created"));

        webTestClient.post()
                .uri("/api/v1/event")
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Event\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetEventsEndpoint() {
        when(getEventsHandler.handle(any()))
                .thenReturn(ServerResponse.ok().bodyValue("Events retrieved"));

        webTestClient.get()
                .uri("/api/v1/event?place=Test Place")
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetEventsEndpointWithoutPlace() {
        when(getEventsHandler.handle(any()))
                .thenReturn(ServerResponse.ok().bodyValue("All events retrieved"));

        webTestClient.get()
                .uri("/api/v1/event")
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateEventEndpointWithoutHeaders() {
        webTestClient.post()
                .uri("/api/v1/event")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetEventsEndpointWithoutHeaders() {
        webTestClient.get()
                .uri("/api/v1/event")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateEventEndpointWithMissingUserId() {
        webTestClient.post()
                .uri("/api/v1/event")
                .header("flowId", "flow123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateEventEndpointWithMissingFlowId() {
        webTestClient.post()
                .uri("/api/v1/event")
                .header("X-User-Id", "user123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Event\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUnsupportedMethod() {
        webTestClient.put()
                .uri("/api/v1/event")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetEventAvailabilityEndpoint() {
        when(getEventAvailabilityHandler.handle(any()))
                .thenReturn(ServerResponse.ok().bodyValue("Availability retrieved"));

        webTestClient.get()
                .uri("/api/v1/event/event-123/availability")
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetEventAvailabilityEndpointWithoutHeaders() {
        webTestClient.get()
                .uri("/api/v1/event/event-123/availability")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testInvalidPath() {
        webTestClient.post()
                .uri("/api/v1/invalid")
                .header("X-User-Id", "user123")
                .header("flowId", "flow123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Test Event\"}")
                .exchange()
                .expectStatus().isNotFound();
    }
}
