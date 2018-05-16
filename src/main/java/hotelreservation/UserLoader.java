package hotelreservation;

import javax.transaction.Transactional;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	// Rename this class to UserLoader and make it so that it just loads the users as once the app is running we don't want to override all the users

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
	}
	
}