package co.com.techtest.model.util.exception;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import lombok.Getter;

@Getter
public class StandardException extends RuntimeException {

  private final TechnicalMessageType technicalMessage;

  public StandardException(TechnicalMessageType technicalMessage) {
    this(technicalMessage.getMessage(), technicalMessage);
  }

  public StandardException(String message, TechnicalMessageType technicalMessage) {
    super(message);
    this.technicalMessage = technicalMessage;
  }

  public StandardException(Throwable cause, TechnicalMessageType technicalMessage) {
    super(cause);
    this.technicalMessage = technicalMessage;
  }
}
