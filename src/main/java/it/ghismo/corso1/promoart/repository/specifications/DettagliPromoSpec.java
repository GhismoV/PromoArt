package it.ghismo.corso1.promoart.repository.specifications;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import it.ghismo.corso1.promoart.entities.DettaglioPromo;

public class DettagliPromoSpec implements Specification<DettaglioPromo> {
	private static final long serialVersionUID = 4581230929581851834L;

	public static Specification<DettaglioPromo> getAllSpec(DettaglioPromoSpecInput input) {
		return codartEquals(input.getCodart())
				.and(codfidEquals(input.getCodfid()))
				.and(isfidEquals(input.getIsfid()))
				.and(dtBetween(input.getActive()))
				;
	}
	
	private static Specification<DettaglioPromo> codartEquals(String codart) {
		return (entity, cq, cb) -> codart != null ? cb.equal(entity.get("codart"), codart) : null;
	}
	private static Specification<DettaglioPromo> codfidEquals(String codfid) {
		return (entity, cq, cb) -> codfid != null ? cb.equal(entity.get("codfid"), codfid) : null;
	}
	private static Specification<DettaglioPromo> isfidEquals(Boolean isfid) {
		return (entity, cq, cb) -> isfid != null ? cb.equal(entity.get("isfid"), isfid ? "Si" : "No") : null;
	}
	private static Specification<DettaglioPromo> dtBetween(Boolean active) {
		return (entity, cq, cb) -> active != null && active.booleanValue() ? cb.between(cb.literal(new Date()), entity.get("inizio"), entity.get("fine")) : null;
	}
	
	
	@Override
	public Predicate toPredicate(Root<DettaglioPromo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return null;
	}

}
