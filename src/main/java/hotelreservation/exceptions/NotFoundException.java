package hotelreservation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Element")  // 404
public class NotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7864223292536407819L;
    // ...
	
	private long id;
	
	public NotFoundException(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "NotFoundException [id=" + id + "]";
	}
}