package it.ghismo.corso1.promoart.repository.specifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DettaglioPromoSpecInput {
	private String codart;
	private Boolean isfid;
	private String codfid;
	@Builder.Default private Boolean active = true;
}
