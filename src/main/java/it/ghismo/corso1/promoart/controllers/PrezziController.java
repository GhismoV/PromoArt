package it.ghismo.corso1.promoart.controllers;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.ghismo.corso1.promoart.exceptions.NotFoundException;
import it.ghismo.corso1.promoart.services.PrezziService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/promo/prezzo")
//@CrossOrigin(value = {"http://localhost:4200"}) -- ghismo : aggiunto classe FiltersCorsConfig per gestire questa cosa
@Slf4j
@Validated
public class PrezziController {
	
	private static final String CODART_REGEX = "[A-Za-z0-9]{5,20}";
	
	@Autowired
	private PrezziService svc;
		
	/*
	 * 
	 */
	
	@GetMapping(value = "/{codart}", produces = MediaType.APPLICATION_JSON_VALUE)
	@SneakyThrows
	public ResponseEntity<Double> cercayByCodart(
			@PathVariable(name = "codart", required = true)
			@NotNull
			@Pattern(regexp = CODART_REGEX)
			String codart,
			
			@RequestParam(name = "fidelity", required = false, defaultValue = "false")
			boolean fidelity,
			
			@RequestHeader("Authorization") String authToken
			) {
		
		log.debug("**** Cerchiamo di ottenere il prezzo dell'articolo [{}] - fidelity [[{}] ****", codart, fidelity);
		
		Double prezzo = svc.getPrezzoByCodart(codart, fidelity);
		log.info("prezzo: {}", prezzo);
		
		if(prezzo == null) throw new NotFoundException("Il prezzo dell'articolo", codart);
		
		return new ResponseEntity<Double>(prezzo, HttpStatus.OK);
	}
	
	@GetMapping(value = "/{codart}/{codfis}", produces = MediaType.APPLICATION_JSON_VALUE)
	@SneakyThrows
	public ResponseEntity<Double> cercaByCodartCodfis(
			@PathVariable(name = "codart", required = true)
			@NotNull
			@Pattern(regexp = CODART_REGEX)
			String codart,
			
			@PathVariable(name = "codfis", required = true)
			@NotNull
			String codfis,

			@RequestHeader("Authorization") String authToken
			) {
		
		log.debug("**** Cerchiamo di ottenere il prezzo dell'articolo [{}] con Codice Fidelity [{}]****", codart, codfis);
		
		Double prezzo = svc.getPrezzoByCodartCodFid(codart, codfis);
		log.info("prezzo: {}", prezzo);
		
		if(prezzo == null) throw new NotFoundException("Il prezzo dell'articolo con codice fidelity", codart + "," + codfis);
		
		return new ResponseEntity<Double>(prezzo, HttpStatus.OK);
	}
	
}
