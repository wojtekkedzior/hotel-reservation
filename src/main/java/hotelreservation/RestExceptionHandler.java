package hotelreservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
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

	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> validationList = ex.getBindingResult().getFieldErrors().stream()
				.map(FieldError::toString).collect(Collectors.toList());

		StringBuilder output = new StringBuilder();

        validationList.forEach(s -> {
            output.append(s).append("\n");
        });

		log.info(ex.getMessage());
		log.info("Validations: {}", validationList);
		
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