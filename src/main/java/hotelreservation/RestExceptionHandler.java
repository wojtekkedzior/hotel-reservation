package hotelreservation;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import hotelreservation.exceptions.NotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
		// return buildResponseEntity();
		System.err.println(ex);
		return null;
	}

	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<Object> handleNoSuchElementException(NotFoundException ex) {
		System.err.println(ex);

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		// return null;
	}

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		// String errorMessage =
		// ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
		List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());

		StringBuffer output = new StringBuffer();

		//TODO get the names of the fields and log them along with the validation errors
		for (String string : validationList) {
			output.append(string);
			output.append("\n");
		}

		return new ResponseEntity<>(output, status);
	}
}