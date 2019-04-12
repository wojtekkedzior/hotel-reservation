package hotelreservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.UserDetailsServiceFactoryBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import hotelreservation.service.MyUserDetailsService;
import hotelreservation.service.MyUserDetailsServiceForTest;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigForTest {
	
	@Bean
	@Profile("test")
	  WebSecurityConfigurerAdapter noAuth() {
       return new WebSecurityConfigurerAdapter() {
            @Override
			protected
            void configure(HttpSecurity http) throws Exception {
            	http.authorizeRequests().anyRequest().authenticated()
        		.and().formLogin()
        			.defaultSuccessUrl("/dashboard", true)
        			.loginPage("/login")
        			.failureUrl("/error.html")
        		.and().logout()
        			.logoutUrl("/logout")
        			.logoutSuccessUrl("/login")
        			.permitAll()
        		.and().exceptionHandling()
        			.accessDeniedHandler(accessDeniedHandler)
        		.and()
        			.csrf().disable();
            }
            
        	@Override
        	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        		auth.authenticationProvider(authProviderFoTest());
        	}
        };
    }

	@Autowired
	private HotelReservationAccessDeniedHandler accessDeniedHandler;
	
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests().anyRequest().authenticated()
//		.and().formLogin()
//			.defaultSuccessUrl("/dashboard", true)
//			.loginPage("/login")
//			.failureUrl("/error.html")
//		.and().logout()
//			.logoutUrl("/logout")
//			.logoutSuccessUrl("/login")
//			.permitAll()
//		.and().exceptionHandling()
//			.accessDeniedHandler(accessDeniedHandler)
//		.and()
//			.csrf().disable();
//	}
//
//	@Autowired(required=false)
//	private UserDetailsService userDetailsService;
//	
//	@Autowired
//	private MyUserDetailsServiceForTest userDetailsServiceForTest;

	@Bean
	@Profile("default")
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(new MyUserDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Bean
	@Profile("test")
	public DaoAuthenticationProvider authProviderFoTest() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(new MyUserDetailsServiceForTest());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}