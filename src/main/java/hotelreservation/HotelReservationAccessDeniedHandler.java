package hotelreservation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class HotelReservationAccessDeniedHandler implements AccessDeniedHandler {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.info("Access Denied for user: " + request.getRemoteUser() + " while trying to hit: " + request.getContextPath());
		
		if(log.isDebugEnabled()) {
			log.debug(request.toString());
			log.debug(accessDeniedException.toString());
		}
		
		response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
	}

}
