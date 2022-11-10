package it.ghismo.corso1.promoart.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.ghismo.corso1.promoart.dto.ResultDto;
import it.ghismo.corso1.promoart.errors.ResultEnum;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private ResourceBundleMessageSource rb;

	private ResponseEntity<ResultDto> _handle(BaseResultException e, HttpStatus defaultHttpStatus) {
		log.debug("Handling {}...", e.getClass().getName());
		return new ResponseEntity<>(e.getErr(), new HttpHeaders(), Objects.requireNonNullElse(e.getHttpStatus(), defaultHttpStatus) );
	}
	
	@ExceptionHandler(value = { NotFoundException.class })
	public ResponseEntity<ResultDto> handleNotFound(BaseResultException e) {
		return _handle(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { BindingValidationException.class })
	public ResponseEntity<ResultDto> handleBindingValidationError(BaseResultException e) {
		return _handle(e, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = { DuplicateException.class })
	public ResponseEntity<ResultDto> handleDuplicate(BaseResultException e) {
		return _handle(e, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@ExceptionHandler(value = { UsernameNotFoundException.class })
	public ResponseEntity<List<ResultDto>> handleUsernameNotFoundException(UsernameNotFoundException e) {
		log.debug("Handling {}...", e.getClass().getSimpleName());
		List<ResultDto> errors = new ArrayList<>();
		/*
		ResultDto dto = new ResultDto();
		dto.setCode("FORMAT_ERROR: " + "AUEEEEEEE");
		dto.setMessage("UEEEELAAAAAA");
		*/
		errors.add(ResultEnum.TokenAuthenticationError.getDto());
		return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ResponseEntity<List<ResultDto>> handleConstraintViolationException(ConstraintViolationException e) {
		log.debug("Handling {}...", e.getClass().getSimpleName());
		List<ResultDto> errors = new ArrayList<>();
		log.error("ghismo ConstraintViolationException", e);
		e.getConstraintViolations().forEach(violation -> {
			ResultDto dto = new ResultDto();
			dto.setCode("FORMAT_ERROR: " + violation.getPropertyPath().toString());
			dto.setMessage(rb.getMessage(violation.getMessage(), null, LocaleContextHolder.getLocale()));
			
			log.error("ghismo errore:" + dto);
			
			errors.add(dto);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { NoContentException.class })
	public ResponseEntity<ResultDto> handleNoContent(BaseResultException e) {
		return _handle(e, HttpStatus.NO_CONTENT);
	}
	
	
}
