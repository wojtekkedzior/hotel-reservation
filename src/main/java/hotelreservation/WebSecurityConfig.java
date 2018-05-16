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
      .authorizeRequests()
        .anyRequest().authenticated()
        .and()
      .formLogin()
      	.defaultSuccessUrl("/dashboard", true)
      	.loginPage("/login")
      	.failureUrl("/error.html")
      .and()
      	.logout()
      	.logoutUrl("/logout")
      	.logoutSuccessUrl("/login").permitAll()
      .and()
      	.exceptionHandling().accessDeniedHandler(accessDeniedHandler)
      .and()
      	.csrf().disable();
  }
	
	
//    @Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//	      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//          auth.userDetailsService(myAppUserDetailsService).passwordEncoder(passwordEncoder);
//}

}
