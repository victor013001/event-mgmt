package co.com.techtest.model.util.exception;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.Getter;

@Getter
public class TechnicalException extends StandardException {

  public TechnicalException(TechnicalMessageType technicalMessage) {
    super(technicalMessage);
  }

  public TechnicalException(Throwable cause, TechnicalMessageType technicalMessage) {
    super(cause, technicalMessage);
  }
}
