package it.ghismo.corso1.promoart.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.ghismo.corso1.promoart.entities.Promo;
import it.ghismo.corso1.promoart.repository.PromoRepository;


@Service
@Transactional(readOnly = true)
public class PromoServiceImpl implements PromoService {
	@Autowired
	private PromoRepository rep;

	@Override
	public List<Promo> getAll() {
		return rep.findAll();
	}

	@Override
	public List<Promo> getAllActive() {
		return rep.selPromoActive();
	}

	@Override
	public Promo readById(String key) {
		Optional<Promo> e = rep.findById(key);
		return e.orElse(null);
	}

	@Override
	public Promo readByAnnoCodice(Short anno, String codice) {
		Optional<Promo> e = rep.findByAnnoAndCodice(anno, codice);
		return e.orElse(null);
	}

	@Override
	@Transactional
	public Promo save(Promo p) {
		return rep.saveAndFlush(p);
	}

	@Override
	@Transactional
	public void delete(String key) {
		rep.deleteById(key);
	}


}
