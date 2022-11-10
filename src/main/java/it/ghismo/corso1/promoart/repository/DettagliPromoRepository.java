package it.ghismo.corso1.promoart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.ghismo.corso1.promoart.entities.DettaglioPromo;

public interface DettagliPromoRepository extends JpaRepository<DettaglioPromo, Long>, JpaSpecificationExecutor<DettaglioPromo> {
	List<DettaglioPromo> findByCodart(String codart);

	List<DettaglioPromo> findByCodartAndIsfid(String codart, String isfid);

	List<DettaglioPromo> findByCodartAndCodfid(String codart, String codfid);

	Optional<DettaglioPromo> findByPromoIdAndRiga(String idPromo, Short riga);

	default List<DettaglioPromo> selByCodartAndFid(String codart) {
		return findByCodartAndIsfid(codart, "Si");
	}
}
