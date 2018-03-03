package hotelreservation;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan({"hotelreservation"})
public class Application extends SpringBootServletInitializer {

   
  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public DateConvertor dateConvertor() {
	  return new DateConvertor();
  }
  
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
  
}