package hotelreservation;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppConfig extends WebSecurityConfigurerAdapter {

	@Override
  protected void configure(HttpSecurity http) throws Exception {
//      http.authorizeRequests()
//          .antMatchers("/**").hasRole("ADMIN")
//          .anyRequest()
//          .authenticated()
//          .and()
//          .formLogin();
      
      http
      .authorizeRequests()
          .antMatchers("/**").hasAnyRole("ADMIN", "MANAGER")
          .anyRequest().authenticated()
          .and()
      .formLogin()
          .permitAll()
          .and()
      .logout()
          .permitAll();
  }
}
