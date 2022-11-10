package it.ghismo.corso1.promoart.services;

public interface PrezziService {
	Double getPrezzoByCodart(String codart, boolean isFidelity);
	Double getPrezzoByCodartCodFid(String codart, String codFid);
	
	/*
	List<Promo> getAll();
	List<Promo> getAllActive();
	Promo readById(String key);
	Promo readByAnnoCodice(Short anno, String codice);
	Promo save(Promo p);
	void delete(String key);
	*/
}
