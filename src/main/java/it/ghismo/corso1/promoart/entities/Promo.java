package it.ghismo.corso1.promoart.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * The persistent class for the "promo" database table.
 * 
 */
@Entity
@Table(name="promo")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Promo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NonNull
	@Id
	@Column(name="idpromo")
	private String id;
	
	@NonNull
	@Column(name="anno", nullable = false)
	private Short anno;
	
	@NonNull
	@Column(name="codice", nullable = false)
	private String codice;
	
	@Column(name="descrizione")
	private String descrizione;
	
	@OneToMany(mappedBy="promo", fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	@EqualsAndHashCode.Exclude
	@JsonManagedReference
	private List<DepositoRifPromo> depositoPromo;
	
	@OneToMany(mappedBy="promo", fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	@EqualsAndHashCode.Exclude
	@JsonManagedReference
	private List<DettaglioPromo> dettagli;

}