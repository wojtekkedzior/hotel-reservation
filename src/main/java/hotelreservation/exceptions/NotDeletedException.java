package hotelreservation.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ToString
@ResponseStatus(value=HttpStatus.NO_CONTENT, reason="Nothing to delete")  // 404
public class NotDeletedException extends RuntimeException {

	private static final long serialVersionUID = 7864223292536407819L;
	private final long id;
}