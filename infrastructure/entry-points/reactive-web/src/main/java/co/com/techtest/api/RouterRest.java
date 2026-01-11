package co.com.techtest.api;

import co.com.techtest.api.dto.request.event.CreateEventRequest;
import co.com.techtest.api.handler.event.CreateEventHandler;
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
    public RouterFunction<ServerResponse> routerFunction(CreateEventHandler createEventHandler) {
        return SpringdocRouteBuilder.route()
                .POST(OperationType.CREATE_EVENT.getPath(), request -> requireHeaders(request, createEventHandler::handle),
                        document(OperationType.CREATE_EVENT, CreateEventRequest.class))
                .build();
    }
}
