package hotelreservation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;



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
//      .authorizeRequests()
////          .antMatchers("/**").hasAnyRole("ADMIN", "MANAGER", "RECEPTIONIST")
//          .anyRequest().authenticated()
//          .and()
      .formLogin()
      .loginPage("/login.html")
      .failureUrl("/error.html")
//          .permitAll()
          .and()
      .logout()
      .logoutSuccessUrl("/index.html");
//          .permitAll();
      
//      
//      http
//      .formLogin()
//      .loginPage("/login.html")
//      .failureUrl("/login-error.html")
//    .and()
//      .logout()
//      .logoutSuccessUrl("/index.html");
      
      http.csrf().disable();
      
      http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandlerImpl() {
          @Override
          public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
              super.handle(request, response, accessDeniedException);

              System.err.println(request.getRemoteUser());
              System.err.println(accessDeniedException);
              
            //This is the correct place to check users getting denied Access Excetions 
          }

          @Override
          public void setErrorPage(String errorPage) {
              super.setErrorPage(errorPage);

              System.err.println();
          }
      });
  }
}
