package it.ghismo.corso1.promoart.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.ghismo.corso1.promoart.adapters.LocalDateTimeSerializer;
import it.ghismo.corso1.promoart.dto.ResultDto;
import it.ghismo.corso1.promoart.errors.ResultEnum;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUnAuthorizedResponseAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
	private static final long serialVersionUID = -8970718410437077606L;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		/*
		log.warn("Errore di sicurezza: " + authException.getLocalizedMessage());
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		ResultDto out = ResultEnum.TokenAuthenticationError.getDto();
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
				.create();
		PrintWriter writer = response.getWriter();
		writer.println(gson.toJson(out));
		*/
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,	"DEVI INSERIRE UN TOKEN JWT VALIDO PER POTERTI AUTENTICARE");
	}
}
