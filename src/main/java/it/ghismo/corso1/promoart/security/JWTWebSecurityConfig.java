package it.ghismo.corso1.promoart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class JWTWebSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

	private static final String[] NOAUTH_MATCHER = { 
			"/api/promo/active",
			"/api/promo/all"
	};
	private static final String[] USER_SVC = { 
			"/api/promo/cerca/**",
			"/api/promo/prezzo/**"
	};
	private static final String[] ADMIN_SVC = { 
			"/api/promo/elimina/**", 
			"/api/promo/modifica/**", 
			"/api/promo/inserisci/**"
	};
	
	
	@Autowired
	@Qualifier("customUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtUnAuthorizedResponseAuthenticationEntryPoint jwtUnAuthorizedResponseAuthenticationEntryPoint;
	
	@Autowired
	private JwtTokenAuthorizationOncePerRequestFilter jwtAuthenticationTokenFilter;

	@Autowired
	private AccessDeniedHandler accessDeniedHandler;

	/*
	@Value("${sicurezza.uri}")
	private String authenticationPath;
	*/
	
	@Bean
	public static PasswordEncoder passwordEncoderBean() { return new BCryptPasswordEncoder(); }
	
	
	/*
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception	{
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoderBean());
	}
	*/

	@Bean
	public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder/*, UserDetailsService userDetailService*/) 
	  throws Exception {
	    return http.getSharedObject(AuthenticationManagerBuilder.class)
	      .userDetailsService(this.userDetailsService)
	      .passwordEncoder(bCryptPasswordEncoder)
	      .and()
	      .build();
	}	

	/*
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests().anyRequest().authenticated();

	}
	*/
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	    .cors()
	    .and()
	    .csrf().disable()
	    .exceptionHandling().authenticationEntryPoint(jwtUnAuthorizedResponseAuthenticationEntryPoint)
	    .and()
	    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    .and()
	    .authorizeRequests()
			.antMatchers(NOAUTH_MATCHER).permitAll()
			.antMatchers(USER_SVC).hasAnyRole("USER")
			.antMatchers(ADMIN_SVC).hasAnyRole("ADMIN")
			.anyRequest().authenticated()
	    .and()
	    .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
	    
	    http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

	    http
	    	.headers()
	    	.frameOptions()
	    	.sameOrigin()
	    	.cacheControl();
	    
	    return http.build();
	}

	
	
	/*
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity
		.ignoring()
		.antMatchers(HttpMethod.POST, authenticationPath)
		.antMatchers(HttpMethod.OPTIONS, "/**")
		.and().ignoring()
		.antMatchers(HttpMethod.GET);
	}
	*/
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> 
	    	web
	    	.ignoring()
			//.antMatchers(HttpMethod.POST, authenticationPath)
	    	//.antMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico")
			.antMatchers(HttpMethod.OPTIONS, "/**")
			/*.and() // ghismo - ho tolto sto pezzo perché sembra che non serve
			.ignoring()
			.antMatchers(HttpMethod.GET)*/;
	}
	
}
