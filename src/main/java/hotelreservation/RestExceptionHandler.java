package hotelreservation;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
//		ApiError apiError = new ApiError(NOT_FOUND);
//		apiError.setMessage(ex.getMessage());
//		return buildResponseEntity();
		System.err.println(ex);
		return null;
	}
	
//	@ExceptionHandler(java.util.NoSuchElementException.class)
	protected ResponseEntity<Object> handleNoSuchElementException(java.util.NoSuchElementException ex) {
//		ApiError apiError = new ApiError(NOT_FOUND);
//		apiError.setMessage(ex.getMessage());
		System.err.println(ex);
		return null;
	}
	

}