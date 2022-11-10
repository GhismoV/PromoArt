package it.ghismo.corso1.promoart.security;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UtentiDto {
	private String id;
	private String userId;
	private String password;
	private String attivo;
	private List<String> ruoli;

}
