package hotelreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@SpringBootApplication
@ComponentScan({ "hotelreservation" })
public class Application extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer {
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public Utils dateConvertor() {
		return new Utils();
	}
	
	//The password encoder should be used when a newly created user signs in for the first time
	//since that part of the app doesn't exist yet this will be a //TODO
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
	
}