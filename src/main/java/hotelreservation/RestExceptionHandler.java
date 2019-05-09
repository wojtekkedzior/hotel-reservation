package hotelreservation;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import hotelreservation.exceptions.NotFoundException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	//TODO add logging
	
	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
		// return buildResponseEntity();
		log.info(ex.getMessage());
		return null;
	}

	@ExceptionHandler(NotFoundException.class)
	protected ResponseEntity<Object> handleNoSuchElementException(NotFoundException ex) {
		log.info(ex.getMessage());
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
		
		log.info(ex.getMessage());
		
		//log the field and exception here otherwise it's hard to figure out what is missing
		return new ResponseEntity<>(output, status);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
		log.info(ex.getMessage());
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		log.info(ex.getMessage());
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}