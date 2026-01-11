package co.com.techtest.parser;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.model.util.exception.TechnicalException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@UtilityClass
public class ParserUtilityApi {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String PARSE_TO_STRING_ERROR_RESPONSE = "Parse to String Error Response";
    private static final String PARSE_TO_STRING_ERROR_KEY_RESPONSE = "parserToStringErrorRS";

    public static String parserToString(Object object) {
        String parserToString = null;
        try {
            parserToString = MAPPER.writeValueAsString(object);
        } catch (Exception exception) {
            log.info(PARSE_TO_STRING_ERROR_RESPONSE, kv(PARSE_TO_STRING_ERROR_KEY_RESPONSE, exception));
        }
        return parserToString;
    }

    public static <T> Mono<T> jsonStringToObject(String jsonString, TypeReference<T> typeRef) {
        return Mono.fromCallable(() -> MAPPER.readValue(jsonString, typeRef))
                .onErrorMap(ex -> new TechnicalException(TechnicalMessageType.JSON_PROCESSING_ERROR));
    }
}
