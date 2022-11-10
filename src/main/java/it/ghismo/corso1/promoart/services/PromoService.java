package it.ghismo.corso1.promoart.services;

import java.util.List;

import it.ghismo.corso1.promoart.entities.Promo;


public interface PromoService {
	List<Promo> getAll();
	List<Promo> getAllActive();
	Promo readById(String key);
	Promo readByAnnoCodice(Short anno, String codice);
	Promo save(Promo p);
	void delete(String key);
}
