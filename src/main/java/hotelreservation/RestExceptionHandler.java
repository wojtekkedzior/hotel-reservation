package hotelreservation;

import hotelreservation.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex) {
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
		List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.toString()).collect(Collectors.toList());

		ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getArguments).collect(Collectors.toList());

		StringBuilder output = new StringBuilder();

		for (String string : validationList) {
			output.append(string);
			output.append("\n");
		}
		
		log.info(ex.getMessage());
		log.info("Validations: {}", output);
		
		//log the field and exception here otherwise it's hard to figure out what is missing
		return new ResponseEntity<>(output, status);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	private ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
		log.info(ex.getMessage());
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		log.info(ex.getMessage());
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}