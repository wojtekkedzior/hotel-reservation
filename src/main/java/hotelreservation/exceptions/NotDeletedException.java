package hotelreservation.exceptions;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ResponseStatus(value=HttpStatus.NO_CONTENT, reason="Nothing to delete")  // 204
public final class NotDeletedException extends RuntimeException {

	private static final long serialVersionUID = 7864223292536407819L;
	private final long id;
}