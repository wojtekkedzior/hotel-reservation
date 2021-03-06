package hotelreservation.exceptions;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ResponseStatus(value=HttpStatus.PRECONDITION_REQUIRED, reason="No charges for payment")  // 428
public final class PaymentNotCreatedException extends RuntimeException {

	private static final long serialVersionUID = 7864223292536407819L;

	private final long id;
}