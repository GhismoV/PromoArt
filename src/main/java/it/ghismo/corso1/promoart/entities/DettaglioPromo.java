package it.ghismo.corso1.promoart.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


/**
 * The persistent class for the "dettpromo" database table.
 * 
 */
@Entity
@Table(name="dettpromo")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class DettaglioPromo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NonNull
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@NonNull
	@Column(name="riga", nullable = false)
	private Short riga;
	
	@NonNull
	@Column(name="codart", nullable = false)
	private String codart;
	
	@Column(name="codfid")
	private String codfid;
	
	@Temporal(TemporalType.DATE)
	@Column(name="inizio")
	private Date inizio;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fine")
	private Date fine;
	
	@NonNull
	@Column(name="oggetto", nullable = false)
	private String oggetto;
	
	@Column(name="isfid")
	private String isfid;
	
	@NonNull
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name = "idpromo", referencedColumnName = "idpromo")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonBackReference
	private Promo promo;
	
	@NonNull
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.DETACH}, optional = false)
	@JoinColumn(name = "idtipopromo", referencedColumnName = "idtipopromo")
	@EqualsAndHashCode.Exclude
	//@ToString.Exclude
	//@JsonManagedReference
	private TipoPromo tipoPromo;

	/*
	 * 
	 */

	@ToString.Include(name = "promo")
	private String promoToString() {
		return promo != null ? String.valueOf(promo.getId()) : null;
	}

	@ToString.Include(name = "tipo promo")
	private String tipoPromoToString() {
		return tipoPromo != null ? String.valueOf(tipoPromo.getId()) : null;
	}
	
	/*
	 * Attributi non campi
	 */
	
	@Transient
	private String descrizione;
	
	@Transient
	private Double prezzo;
	
}