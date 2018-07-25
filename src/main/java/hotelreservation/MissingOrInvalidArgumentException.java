package hotelreservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.PRECONDITION_REQUIRED, reason="No charges for payment")  // 428
public class MissingOrInvalidArgumentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7864223292536407819L;
    // ...
	
	private long id;
	private String message;
	
	public MissingOrInvalidArgumentException(long id) {
		this.id = id;
	}
	
	public MissingOrInvalidArgumentException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "MissingOrInvalidArgumentException [id=" + id + ", message=" + message + "]";
	}

	
	
}