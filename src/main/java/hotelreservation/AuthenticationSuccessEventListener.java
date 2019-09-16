package hotelreservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		log.info("Successful login for user: {} with authorities: {}",  event.getAuthentication().getName(), event.getAuthentication().getAuthorities());
	}

}