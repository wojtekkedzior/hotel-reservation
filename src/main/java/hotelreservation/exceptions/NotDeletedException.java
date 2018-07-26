package hotelreservation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NO_CONTENT, reason="Nothing to delete")  // 404
public class NotDeletedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7864223292536407819L;
    // ...
	
	private long id;
	
	public NotDeletedException(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Nothing to delete with [id=" + id + "]";
	}
}