package hotelreservation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.PRECONDITION_REQUIRED, reason="No charges for payment")  // 428
public class PaymentNotCreatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7864223292536407819L;
    // ...
	
	private long id;
	
	public PaymentNotCreatedException(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Nothing to delete with [id=" + id + "]";
	}
}