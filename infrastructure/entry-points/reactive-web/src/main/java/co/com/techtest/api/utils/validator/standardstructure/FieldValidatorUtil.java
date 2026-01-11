package co.com.techtest.api.utils.validator.standardstructure;

import co.com.techtest.api.dto.response.standardstructure.ErrorDetail;
import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class FieldValidatorUtil {

  public static void validateField(Boolean isValid, List<ErrorDetail> errors, TechnicalMessageType technicalMessage) {
    if (Boolean.FALSE.equals(isValid)) {
      errors.add(buildErrorDetail(technicalMessage));
    }
  }

  private static ErrorDetail buildErrorDetail(TechnicalMessageType technicalMessageType) {
    return new ErrorDetail(technicalMessageType.getCode(), technicalMessageType.getMessage());
  }
}
