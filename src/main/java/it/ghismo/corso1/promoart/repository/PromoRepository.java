package it.ghismo.corso1.promoart.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.ghismo.corso1.promoart.entities.Promo;

public interface PromoRepository extends JpaRepository<Promo, String> {
	Optional<Promo> findByAnnoAndCodice(Short anno, String codice);
	
	//@Query("SELECT DISTINCT p FROM Promo p JOIN p.dettagli d WHERE CURRENT_DATE BETWEEN d.inizio AND d.fine") --- ghismo - problema CURRENT_DATE sul server Postgres
	@Query("SELECT DISTINCT p FROM Promo p JOIN p.dettagli d WHERE TO_DATE(:rifDt,'YYYY-MM-DD') BETWEEN d.inizio AND d.fine")
	List<Promo> selPromoActiveDateRif(@Param("rifDt")String formattedDt);
	
	default List<Promo> selPromoActive() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return selPromoActiveDateRif(sdf.format(new Date()));
	}
	
}
