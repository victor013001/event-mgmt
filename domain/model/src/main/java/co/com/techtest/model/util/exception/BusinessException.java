package co.com.techtest.model.util.exception;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.Getter;

@Getter
public class BusinessException extends StandardException {

  public BusinessException(TechnicalMessageType technicalMessage) {
    super(technicalMessage.getMessage(), technicalMessage);
  }

  public BusinessException(String message, TechnicalMessageType technicalMessage) {
    super(message, technicalMessage);
  }
}
