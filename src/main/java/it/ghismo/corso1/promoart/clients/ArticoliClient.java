package it.ghismo.corso1.promoart.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ArticoliWebService", url = "localhost:5051")
public interface ArticoliClient {
	
	@GetMapping("/api/articoli/cerca/codice/{codArt}")
	ArticoliDto getArticolo(
			@RequestHeader("Authorization") String headerAuthStr, 
			@PathVariable("codArt") String codiceArticolo);

}
