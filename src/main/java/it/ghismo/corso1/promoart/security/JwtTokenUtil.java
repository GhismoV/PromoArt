package it.ghismo.corso1.promoart.security;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "iat";
	private static final long serialVersionUID = -3301605591108950415L;
	private Clock clock = DefaultClock.INSTANCE;

	@Autowired
	private JwtConfig jwtConfig;


	public String getUsernameFromToken(String token) { return getClaimFromToken(token, Claims::getSubject); }
	public Date getIssuedAtDateFromToken(String token) { return getClaimFromToken(token, Claims::getIssuedAt); }
	public Date getExpirationDateFromToken(String token) { return getClaimFromToken(token, Claims::getExpiration); }

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		if (claims != null) {
			log.info(String.format("Emissione Token:  %s", claims.getIssuedAt().toString()));
			log.info(String.format("Scadenza Token:  %s", claims.getExpiration().toString()));
			return claimsResolver.apply(claims);
		} else return null;
	}

	private Claims getAllClaimsFromToken(String token) {
		Claims retVal = null;
		try	{
			retVal = Jwts.parser()
					.setSigningKey(jwtConfig.getSecret().getBytes())
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception ex) {
			log.warn(ex.getMessage());
		}
		return retVal;
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		boolean retVal = expiration != null && expiration.before(clock.now());
		if (retVal) {
			log.warn("Token Scaduto o non Valido!");
		} else {
			log.info("Token Ancora Valido!");
		}
		return retVal;
	}

	public Boolean validateToken(String token, UserDetails userDetails)  {
		//JwtUserDetails user = (JwtUserDetails) userDetails;
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	/* non serve per articoli ma per modulo AuthJwt
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails);
	}
	private String doGenerateToken(Map<String, Object> claims, UserDetails userDetails) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);
		final String secret = jwtConfig.getSecret();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userDetails.getUsername())
				.claim("authorities", userDetails.getAuthorities()
						.stream()
							.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}

	public Boolean canTokenBeRefreshed(String token)  {
		return (isTokenExpired(token));
	}

	public String refreshToken(String token) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);
		final String secret = jwtConfig.getSecret();
		final Claims claims = getAllClaimsFromToken(token);
		claims.setIssuedAt(createdDate);
		claims.setExpiration(expirationDate);
		//return buildToken(claims, secret);
		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
		
	}
	
	// TODO - ghismo - metodo mio per costruire generico
	@SuppressWarnings("unused")
	private String buildToken(Claims claims, String secret) {
		final Date createdDate = clock.now();
		final Date expirationDate = calculateExpirationDate(createdDate);

		return Jwts.builder()
		.setClaims(claims)
		.setIssuedAt(createdDate)
		.setExpiration(expirationDate)
		.signWith(SignatureAlgorithm.HS512, secret.getBytes())
		.compact();
	}

	private Date calculateExpirationDate(Date createdDate) {
		return new Date(createdDate.getTime() + jwtConfig.getExpiration() * 1000);
	}
	*/


}
