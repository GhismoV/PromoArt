package it.ghismo.corso1.promoart.controllers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.ghismo.corso1.promoart.clients.ArticoliClient;
import it.ghismo.corso1.promoart.clients.ArticoliDto;
import it.ghismo.corso1.promoart.dto.ResultDto;
import it.ghismo.corso1.promoart.entities.DepositoRifPromo;
import it.ghismo.corso1.promoart.entities.DettaglioPromo;
import it.ghismo.corso1.promoart.entities.Promo;
import it.ghismo.corso1.promoart.errors.ResultEnum;
import it.ghismo.corso1.promoart.exceptions.BindingValidationException;
import it.ghismo.corso1.promoart.exceptions.DuplicateException;
import it.ghismo.corso1.promoart.exceptions.NoContentException;
import it.ghismo.corso1.promoart.exceptions.NotFoundException;
import it.ghismo.corso1.promoart.services.PromoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/promo")
//@CrossOrigin(value = {"http://localhost:4200"}) -- ghismo : aggiunto classe FiltersCorsConfig per gestire questa cosa
@Slf4j
@Validated
public class PromoController {
	
	@Autowired
	private PromoService svc;
	
	@Autowired
	private ArticoliClient articoliClient;
	
	@Autowired
	private ResourceBundleMessageSource rb;
	
	
	@GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@Validated
	@SneakyThrows
	public ResponseEntity<List<Promo>> getAll(
			@RequestHeader(name="Authorization", required = false) String authToken // ghismo - required a false per far funzionare senza token
			) {
		
		log.debug("**** Cerchiamo di ottenere tutte le promozioni ****");
		log.debug("Ghismo - authToken: [{}]",  authToken);
		
		List<Promo> promos = svc.getAll();
		log.info("Promozioni: {}", promos);
		if(Objects.isNull(promos) || promos.isEmpty()) throw new NoContentException();
		
		promos.stream().forEach(p -> recuperaArticolo(authToken, p));

		return new ResponseEntity<List<Promo>>(promos, HttpStatus.OK);
	}
	
	@GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
	@Validated
	@SneakyThrows
	public ResponseEntity<List<Promo>> getActive(
			@RequestHeader("Authorization") String authToken			
			) {
		
		log.debug("**** Cerchiamo di ottenere tutte le promozioni attive ****");
		
		List<Promo> promos = svc.getAllActive();
		log.info("Promozioni Attive: {}", promos);
		if(Objects.isNull(promos) || promos.isEmpty()) throw new NoContentException();
		
		promos.stream().forEach(p -> recuperaArticolo(authToken, p));
		
		return new ResponseEntity<List<Promo>>(promos, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/cerca/id/{promoId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@SneakyThrows
	public ResponseEntity<Promo> cercaByPromoId(
			@PathVariable(name = "promoId", required = true)
			@NotNull
			String promoId,
			
			@RequestHeader("Authorization") String authToken
			) {
		
		log.debug("**** Cerchiamo di ottenere la promo attraverso ID [{}] ****", promoId);
		
		Promo promo = svc.readById(promoId);
		log.info("Promo: {}", promo);
		
		if(promo == null) throw new NotFoundException("La Promozione", promoId);
		
		recuperaArticolo(authToken, promo);

		return new ResponseEntity<Promo>(promo, HttpStatus.OK);
	}
	
	@GetMapping(value = "/cerca/codice", produces = MediaType.APPLICATION_JSON_VALUE)
	@SneakyThrows
	public ResponseEntity<Promo> cercaByCodiceAnno(
			@RequestParam("codice")
			@NotNull
			String codice,
			
			@RequestParam("anno")
			@NotNull
			Short anno,

			@RequestHeader("Authorization") String authToken
			) {
		
		log.debug("**** Cerchiamo di ottenere la promo attraverso il codice e anno [{},  {}] ****", codice, anno);
		
		Promo promo = svc.readByAnnoCodice(anno, codice);
		log.info("Promo: {}", promo);
		
		if(promo == null) throw new NotFoundException("La Promozione con codice e anno [{}] non è stata trovata", codice + " , " + anno);
		
		recuperaArticolo(authToken, promo);

		return new ResponseEntity<Promo>(promo, HttpStatus.OK);
	}
	
	@PostMapping(value = "/inserisci", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@SneakyThrows
	public ResponseEntity<ResultDto> create(
			@RequestBody(required = true)
			@Valid
			Promo in,
			
			BindingResult bindingResult
			) {
		log.debug("**** Cerchiamo di inserire la promo [{}]", in.getId());

		Promo promoChk = checkInput(in, bindingResult, true);
		if(promoChk != null) {
			log.warn("Promo [{}] già esistente... Usare servizio modifica", promoChk.getId());
			throw new DuplicateException(promoChk.getId());
		}

		Promo outEntity = svc.save(in);
		
		return new ResponseEntity<ResultDto>(ResultEnum.OkParam1.getDto(outEntity.getId()), HttpStatus.CREATED);
	}
	
	@PutMapping(value = "/modifica", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@SneakyThrows
	public ResponseEntity<ResultDto> modify(
			@RequestBody(required = true)
			@Valid
			Promo in,
			
			BindingResult bindingResult
			) {
		log.debug("**** Cerchiamo di modificare la promo [{}]", in.getId());

		Promo promoChk = checkInput(in, bindingResult, false);
		if(promoChk == null) {
			log.warn("Promo [{}] non trovata!!!", in.getId());
			throw new NotFoundException("Promozione", in.getId());
		}

		Promo outEntity = svc.save(in);
		
		return new ResponseEntity<ResultDto>(ResultEnum.OkParam1.getDto(outEntity.getId()), HttpStatus.CREATED);
	}

	
	@RequestMapping(value = "/elimina/{idPromo}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
	@SneakyThrows
	public ResponseEntity<ResultDto> delete(
			@PathVariable(name = "idPromo", required = true)
			@NotNull
			String idPromo) {
		log.debug("**** Cerchiamo di eliminare la promo [{}]", idPromo);

		Promo promoChk = svc.readById(idPromo);
		if(promoChk == null) {
			log.warn("Promo [{}] non trovata.", idPromo);
			throw new NotFoundException("Promo", idPromo);
		}
		svc.delete(idPromo);
		
		return new ResponseEntity<ResultDto>(ResultEnum.OkParam1.getDto(idPromo), HttpStatus.OK);
	}
	
	@SneakyThrows
	private Promo checkInput(Promo in, BindingResult bindingResult, boolean isNew) {
		if(isNew) {
			String promoId = UUID.randomUUID().toString();
			in.setId(promoId);
		}
		List<DettaglioPromo> dettagli = in.getDettagli();
		if(dettagli != null) {
			dettagli.stream().forEach(d -> d.setPromo(in));
		}
		
		List<DepositoRifPromo> deps = in.getDepositoPromo();
		if(deps != null) {
			deps.stream().forEach(d -> d.setPromo(in));
		}
		
		log.debug("Promo: {}", in.toString());
		
		if(bindingResult.hasErrors()) {
			FieldError f = bindingResult.getFieldError();
			String errTranslated = rb.getMessage(f, LocaleContextHolder.getLocale());
			log.warn(errTranslated);
			throw new BindingValidationException(f);
		}
		if(in.getDepositoPromo() == null) {
			throw new BindingValidationException("DepositoPromo", "empty");
		}
		if(in.getDettagli() == null || in.getDettagli().isEmpty()) {
			throw new BindingValidationException("Dettagli", "empty");
		}
		return svc.readByAnnoCodice(in.getAnno(), in.getCodice());
	}
	
	private void recuperaArticolo(String authToken, Promo promo) {
		if(Objects.nonNull(promo) && Objects.nonNull(promo.getDettagli())) {
			promo.getDettagli().stream().forEach(d -> recuperaArticolo(authToken, d));
		}
	}
	private void recuperaArticolo(String authToken, DettaglioPromo dtl) {
		ArticoliDto articolo = null;
		try {
			articolo = articoliClient.getArticolo(authToken, dtl.getCodart());
		} catch (Exception e) {
			//log.error("ghismo - ha scattaaaaaaaaat", e);
		}
		log.info("Articolo restituito da ArticoliWebService per {}", articolo);
		if(Objects.nonNull(articolo)) {
			dtl.setDescrizione(articolo.getDescrizione());
			dtl.setPrezzo(articolo.getPrezzo());
		}
	}

	
	
}
