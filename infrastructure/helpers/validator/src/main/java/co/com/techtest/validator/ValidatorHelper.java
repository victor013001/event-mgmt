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
}
