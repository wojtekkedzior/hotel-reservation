package hotelreservation.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ResponseStatus(value=HttpStatus.PRECONDITION_REQUIRED, reason="No charges for payment")  // 428
@ToString
public class PaymentNotCreatedException extends RuntimeException {

	private static final long serialVersionUID = 7864223292536407819L;

	private final long id;
}