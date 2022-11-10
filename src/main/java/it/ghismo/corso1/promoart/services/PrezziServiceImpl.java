package it.ghismo.corso1.promoart.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.ghismo.corso1.promoart.entities.DettaglioPromo;
import it.ghismo.corso1.promoart.entities.TipoPromo;
import it.ghismo.corso1.promoart.repository.DettagliPromoRepository;
import it.ghismo.corso1.promoart.repository.specifications.DettagliPromoSpec;
import it.ghismo.corso1.promoart.repository.specifications.DettaglioPromoSpecInput;
import lombok.extern.slf4j.Slf4j;


@Service
@Transactional(readOnly = true)
@Slf4j
public class PrezziServiceImpl implements PrezziService {
	@Autowired
	private DettagliPromoRepository rep;

	@Override
	public Double getPrezzoByCodart(String codart, boolean isFidelity) {
		List<DettaglioPromo> res = rep
				.findAll(DettagliPromoSpec.getAllSpec(
								DettaglioPromoSpecInput.builder()
								.codart(codart)
								.isfid(isFidelity)
								.active(true)
								.build() ));
		return extractPrice(res);
	}
	
	@Override
	public Double getPrezzoByCodartCodFid(String codart, String codFid) {
		List<DettaglioPromo> res = rep
				.findAll(DettagliPromoSpec.getAllSpec(
								DettaglioPromoSpecInput.builder()
								.codart(codart)
								.codfid(codFid)
								.active(true)
								.build() ));
		return extractPrice(res);
	}
	
	/*
	 * 
	 * 
	 */
	
	private Double extractPrice(List<DettaglioPromo> res) {
		return res != null && !res.isEmpty()
				? getDouble(res.get(0).getTipoPromo(), res.get(0).getOggetto())
				: null;
		
	}
	private Double getDouble(TipoPromo tipoPromo, String oggetto) {
		Double retVal = null;
		if(tipoPromo != null && tipoPromo.getId().intValue() == 1) {
			try {
				retVal = Double.parseDouble(oggetto.replace(",", "."));
			} catch(NumberFormatException ex) {
				log.error(ex.getMessage(), ex);
			}
		} else {
			log.error("Non posso prendere il prezzo perché il tipo promo è diverso da TAGLIO PREZZO");
		}
		return retVal;
	}




}
