package it.ghismo.corso1.promoart.security;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenAuthorizationOncePerRequestFilter extends OncePerRequestFilter {

	@Autowired
	@Qualifier("customUserDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${sicurezza.header}")
	private String tokenHeader;

	
	 @Autowired
	 @Qualifier("handlerExceptionResolver") // ghismo ho verificato che è necessario!!!
	 private HandlerExceptionResolver resolver;
	 
	 
	@Override
	@SneakyThrows
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
		log.info("Authentication Request For [{}] - [{}]", request.getMethod(), request.getRequestURL());
		
		if(HttpMethod.OPTIONS.matches(request.getMethod())) {
			chain.doFilter(request, response);
			return;
		}
		
		final String requestTokenHeader = request.getHeader(this.tokenHeader);
		log.warn("Token: " + requestTokenHeader);

		String username = null;
		String jwtToken = null;
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				log.error("IMPOSSIBILE OTTENERE LA USERID", e);
			} catch (ExpiredJwtException e) {
				log.warn("TOKEN SCADUTO", e);
			}
		} else {
			log.warn("TOKEN NON VALIDO");
		}
		log.warn("JWT_TOKEN_USERNAME_VALUE '{}'", username);
		
		if(username == null) { // serve per far funzionare le richieste senza autorizzazione (NOAUTH_MATCHER)
			chain.doFilter(request, response);
			return;
		}
		
		try {
			if (/*username != null && */SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
            log.error("Articoli Service: Spring Security Filter Chain Exception: {}", e.getLocalizedMessage());
            resolver.resolveException(request, response, null, e);
        }
		//chain.doFilter(request, response);
	}
}
