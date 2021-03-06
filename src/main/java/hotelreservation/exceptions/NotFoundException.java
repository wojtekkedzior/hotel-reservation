package hotelreservation.exceptions;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Element")  // 404
public final class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7864223292536407819L;

	private final long id;

}