package co.com.techtest.api;

import co.com.techtest.api.handler.event.CreateEventHandler;
import co.com.techtest.model.util.enums.OperationType;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static co.com.techtest.api.utils.HeadersUtilApi.requireHeaders;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
  @RouterOperations({@RouterOperation(path = "/api/usecase/path", beanClass = Handler.class, beanMethod = "listenGETUseCase"),
      @RouterOperation(path = "/api/usecase/otherpath", beanClass = Handler.class, beanMethod = "listenPOSTUseCase"),
      @RouterOperation(path = "/api/otherusercase/path", beanClass = Handler.class, beanMethod = "listenGETOtherUseCase")})
  @Bean
  public RouterFunction<ServerResponse> routerFunction(CreateEventHandler createEventHandler) {
    return route(POST(OperationType.CREATE_EVENT.getPath()), request ->
        requireHeaders(request, req -> createEventHandler.handle(req)));
  }
}
