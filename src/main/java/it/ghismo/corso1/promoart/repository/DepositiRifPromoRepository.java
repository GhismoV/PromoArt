package it.ghismo.corso1.promoart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.ghismo.corso1.promoart.entities.DepositoRifPromo;

public interface DepositiRifPromoRepository extends JpaRepository<DepositoRifPromo, Long> {
	Optional<DepositoRifPromo> findByPromoIdAndIdDeposito(String idPromo, Integer idDeposito);
}
