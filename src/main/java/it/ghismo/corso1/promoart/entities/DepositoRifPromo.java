package it.ghismo.corso1.promoart.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


/**
 * The persistent class for the "deprifpromo" database table.
 * 
 */
@Entity
@Table(name="deprifpromo")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
//@AllArgsConstructor
public class DepositoRifPromo implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@NonNull
	@Column(name="iddeposito", nullable = false)
	private Integer idDeposito;
	
	@NonNull
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name = "idpromo", referencedColumnName = "idpromo")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonBackReference
	private Promo promo;

	/*
	 * 
	 */

	@ToString.Include(name = "promo")
	private String promoToString() {
		return promo != null ? String.valueOf(promo.getId()) : null;
	}

}