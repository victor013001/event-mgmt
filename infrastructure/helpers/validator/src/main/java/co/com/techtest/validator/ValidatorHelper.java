package co.com.techtest.validator;

import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.Objects;

@UtilityClass
public class ValidatorHelper {

    public static Mono<Boolean> isValidNotNullAndNotBlank(String field) {
        return Mono.defer(() -> Mono.just(Objects.nonNull(field) && StringUtils.isNotBlank(field)));
    }

    public static Mono<Boolean> isHigherThanZero(Long field) {
        return Mono.defer(() -> Mono.just(Objects.nonNull(field) && field > 0));
    }

    public static Mono<Boolean> isNotNull(Object field) {
        return Mono.defer(() -> Mono.just(Objects.nonNull(field)));
    }
}
