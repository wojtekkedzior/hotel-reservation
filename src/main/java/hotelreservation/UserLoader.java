package hotelreservation;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Profile("dev")
public class UserLoader implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// Rename this class to UserLoader and make it so that it just loads the users as once the app is running we don't want to override all the users
	}
	
}