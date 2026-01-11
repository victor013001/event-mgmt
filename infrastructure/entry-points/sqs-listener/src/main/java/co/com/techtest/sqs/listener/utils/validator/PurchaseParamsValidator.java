package co.com.techtest.sqs.listener.utils.validator;

import co.com.techtest.model.util.enums.TechnicalMessageType;
import co.com.techtest.sqs.listener.dto.log.ErrorLogDetail;
import co.com.techtest.sqs.listener.dto.purchase.PurchaseReqParams;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.ArrayList;
import java.util.List;

import static co.com.techtest.sqs.listener.utils.validator.FieldValidatorUtil.validateField;
import static co.com.techtest.validator.ValidatorHelper.isValidNotNullAndNotBlank;

@UtilityClass
public class PurchaseParamsValidator {

    public static Mono<List<ErrorLogDetail>> validatePlaceTicketParams(PurchaseReqParams params) {
        return Mono.zip(isValidNotNullAndNotBlank(params.userId()),
                        isValidNotNullAndNotBlank(params.flowId()),
                        isValidNotNullAndNotBlank(params.ticketId()))
                .map(PurchaseParamsValidator::validations);
    }

    private static List<ErrorLogDetail> validations(Tuple3<Boolean, Boolean, Boolean> validations) {
        List<ErrorLogDetail> errors = new ArrayList<>();
        validateField(validations.getT1(), errors, TechnicalMessageType.ERROR_MS_INVALID_EVENT_ID);
        validateField(validations.getT2(), errors, TechnicalMessageType.ERROR_MS_INVALID_QUANTITY);
        validateField(validations.getT3(), errors, TechnicalMessageType.ERROR_MS_INVALID_TICKET_ID);
        return errors;
    }
}
