package it.ghismo.corso1.promoart.exceptions;

import org.springframework.http.HttpStatus;

import it.ghismo.corso1.promoart.errors.ResultEnum;


public class NoContentException extends BaseResultException {
	private static final long serialVersionUID = -2404717895107950786L;
	
	public NoContentException() { 
		super(ResultEnum.NoContent); 
	}

	@Override public HttpStatus getHttpStatus() { return HttpStatus.NO_CONTENT; }

}
