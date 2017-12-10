package hotelreservation;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan({"hotelreservation"})
public class Application {

   
  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public DateConvertor dateConvertor() {
	  return new DateConvertor();
  }
  
}