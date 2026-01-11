package co.com.techtest.api.utils.validator.standardstructure;

import co.com.techtest.api.dto.request.standardstructure.HeaderRequest;
import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;

import static co.com.techtest.api.utils.validator.standardstructure.FieldValidatorUtil.validateField;
import static co.com.techtest.validator.ValidatorHelper.isValidNotNullAndNotBlank;

@UtilityClass
public class HeaderValidateUtil {
  public static Mono<List<ErrorDetail>> validateHeaders(HeaderRequest headers) {
    return Mono.zip(
        isValidNotNullAndNotBlank(headers.xUserId()),
        isValidNotNullAndNotBlank(headers.flowId())
    ).map(HeaderValidateUtil::validationsMessage);
  }

  private List<ErrorDetail> validationsMessage(Tuple2<Boolean, Boolean> validations) {
    List<ErrorDetail> errors = new ArrayList<>();
    validateField(validations.getT1(), errors, TechnicalMessageType.ERROR_MS_INVALID_X_USER_ID);
    validateField(validations.getT1(), errors, TechnicalMessageType.ERROR_MS_INVALID_FLOW_ID);
    return errors;
  }
}
