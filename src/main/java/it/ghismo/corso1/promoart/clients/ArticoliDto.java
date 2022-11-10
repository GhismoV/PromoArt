package it.ghismo.corso1.promoart.clients;

import java.util.Date;

import lombok.Data;

@Data
public class ArticoliDto {
	private String codArt;
	private String descrizione;	
	private String um;
	private String codStat;
	private Integer pzCart;
	private Double pesoNetto;
	private String idStatoArt;
	private Date dataCreazione;
	private Double prezzo = 0d;
	
	/*
	private Set<BarcodeDto> barcode = new HashSet<>();
	private IngredientiDto ingredienti;
	private CategoriaDto famAssort;
	private IvaDto iva;
	*/
}
