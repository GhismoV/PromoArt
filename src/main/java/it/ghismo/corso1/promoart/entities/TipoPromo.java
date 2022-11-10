package it.ghismo.corso1.promoart.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


/**
 * The persistent class for the "tipopromo" database table.
 * 
 */
@Entity
@Table(name="tipopromo")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class TipoPromo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NonNull
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="idtipopromo")
	private Integer id;

	@Column(name="descrizione")
	private String descrizione;
	
	/*
	@OneToMany(mappedBy="tipoPromo", fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	//@JsonBackReference
	@ToString.Exclude
	private List<DettaglioPromo> dettagliPromo;
	*/

}