package hotelreservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		WebAuthenticationDetails auth = (WebAuthenticationDetails) event.getAuthentication().getDetails();
		
		log.info("Successful failure for user: " + event.getAuthentication().getName() + " with authoritites: " + event.getAuthentication().getAuthorities());
	
		if(log.isDebugEnabled()) {
			log.debug(event.toString());
			log.debug(auth.toString());
		}
	}

}