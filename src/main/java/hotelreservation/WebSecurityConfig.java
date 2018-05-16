package hotelreservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private HotelReservationAccessDeniedHandler accessDeniedHandler;

	@Override
  protected void configure(HttpSecurity http) throws Exception {
      http
//      .authorizeRequests()
////          .antMatchers("/**").hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
//          .anyRequest().authenticated()
//          .and()
      .formLogin()
      .defaultSuccessUrl("/reservationDashBoard", true)
      .loginPage("/login.html")
      .failureUrl("/error.html")
   .and()
      .logout()
      .logoutSuccessUrl("/index.html")
   .and()
      .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
   .and()
      .csrf().disable();
  }
	

}
