package it.ghismo.corso1.promoart.tests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import it.ghismo.corso1.promoart.Application;
import it.ghismo.corso1.promoart.entities.DettaglioPromo;
import it.ghismo.corso1.promoart.entities.Promo;
import it.ghismo.corso1.promoart.entities.TipoPromo;
import it.ghismo.corso1.promoart.repository.DettagliPromoRepository;
import it.ghismo.corso1.promoart.repository.PromoRepository;
import it.ghismo.corso1.promoart.repository.specifications.DettagliPromoSpec;
import it.ghismo.corso1.promoart.repository.specifications.DettaglioPromoSpecInput;
import lombok.SneakyThrows;

@SpringBootTest()
@ContextConfiguration(classes = Application.class)
@TestMethodOrder(OrderAnnotation.class)
public class RepositoryTest {
	
	@Autowired
	private PromoRepository promoRepository;

	@Autowired
	private DettagliPromoRepository dtlPromoRepository;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	String IdPromo = "";
	short Anno = (short)Year.now().getValue();
	String Codice = "TEST01";
	String Descrizione = "PROMO TEST1";
	
	private static boolean isInitialized = false;
	private static boolean isTerminated = false;
	
	@BeforeEach
	@SneakyThrows
	public void setup() {
		if (isInitialized) return;
		
		Promo promo = promoRepository.findByAnnoAndCodice(Anno, Codice).orElse(null);
		if(promo != null) {
			System.out.println("ghismo --------- dati di partenza già esistenti...:" + promo.getId());
			isInitialized = true;
			return;
		}

		UUID uuid = UUID.randomUUID();
		IdPromo = uuid.toString();
		
		System.out.println("ghismo --------- uso promo id:" + IdPromo);
		promo = Promo.builder()
						.id(IdPromo)
						.anno(Anno)
						.codice(Codice)
						.descrizione(Descrizione)
						.build();
		
		
		//La promo sarà valida l'intero anno corrente
		String strAnno = String.valueOf(Anno);
		Date Inizio = sdf.parse(strAnno + "-01-01");  
		Date Fine = sdf.parse(strAnno + "-12-31");
		
		promoRepository.save(promo);
		
		promo = promoRepository.findByAnnoAndCodice(Anno, Codice).orElse(null);

		DettaglioPromo dettPromo = DettaglioPromo.builder()
										.id(-1l)
										.inizio(Inizio)
										.fine(Fine)
										.codart("049477701")
										.oggetto("1.10")
										.isfid("No")
										.riga((short)1)
										.tipoPromo(new TipoPromo(1)) //riga 1 promozione standard
										.promo(promo)
										.build();
		dtlPromoRepository.save(dettPromo);
		
		//riga 2 promozione fidelity
		dettPromo = DettaglioPromo.builder()
				.id(-1l)
				.inizio(Inizio)
				.fine(Fine)
				.codart("004590201")
				.oggetto("1.99")
				.isfid("Si")
				.riga((short)2)
				.tipoPromo(new TipoPromo(1)) //riga 1 promozione standard
				.promo(promo)
				.build();
		dtlPromoRepository.save(dettPromo);
		
		//riga 3 promozione fidelity Only You
		dettPromo = DettaglioPromo.builder()
				.id(-1l)
				.inizio(Inizio)
				.fine(Fine)
				.codart("008071001")
				.oggetto("2.19")
				.isfid("Si")
				.codfid("67000076")
				.riga((short)3)
				.tipoPromo(new TipoPromo(1)) //riga 1 promozione standard
				.promo(promo)
				.build();
		dtlPromoRepository.save(dettPromo);
		
		--Anno; //assicuriamoci che la promo sia scaduta
		strAnno = String.valueOf(Anno);
		Inizio = sdf.parse(strAnno + "-01-01");  
		Fine = sdf.parse(strAnno + "-12-31");
		
		//riga 4 promozione standard scaduta
		dettPromo = DettaglioPromo.builder()
				.id(-1l)
				.inizio(Inizio)
				.fine(Fine)
				.codart("002001601")
				.oggetto("0.99")
				.riga((short)4)
				.tipoPromo(new TipoPromo(1)) //riga 1 promozione standard
				.promo(promo)
				.build();
		dtlPromoRepository.save(dettPromo);
		
		isInitialized = true;
	}
	
	@Test @Order(1)
	public void A_TestSelByCodArt() {
		//testOggetto(dtlPromoRepository.findByCodart("049477701"), "1.10");
		testOggetto(
				dtlPromoRepository.findAll(DettagliPromoSpec.getAllSpec(
					DettaglioPromoSpecInput
					.builder()
					.codart("049477701")
					.build()
					))
				, "1.10");
		
	}
	
	@Test @Order(2)
	public void B_TestSelByCodArtAndFid(){
		//testOggetto(dtlPromoRepository.selByCodartAndFid("004590201"), "1.99");
		testOggetto(
				dtlPromoRepository.findAll(DettagliPromoSpec.getAllSpec(
					DettaglioPromoSpecInput
					.builder()
					.codart("004590201")
					.isfid(true)
					.build()
					))
				, "1.99");
	}
	
	@Test @Order(3)
	public void C_TestSelByCodArtAndCodFid() {
		//testOggetto(dtlPromoRepository.findByCodartAndCodfid("008071001","67000076"), "2.19");
		testOggetto(
				dtlPromoRepository.findAll(DettagliPromoSpec.getAllSpec(
					DettaglioPromoSpecInput
					.builder()
					.codart("008071001")
					.codfid("67000076")
					.build()
					))
				, "2.19");
	}
	
	@Test @Order(4)
	public void D_TestSelPromoScad() {
		//assertThat(dtlPromoRepository.findByCodart("002001601"))
		assertThat(
				dtlPromoRepository.findAll(DettagliPromoSpec.getAllSpec(
						DettaglioPromoSpecInput
						.builder()
						.codart("002001601")
						.build()
						))
		)
		.isEmpty();
	}
	
	@Test @Order(5)
	public void E_TestfindByIdPromo() {
		String idPromo = promoRepository.findByAnnoAndCodice(Anno, Codice).get().getId();
		testInfo(promoRepository.findById(idPromo), Promo::getDescrizione, Descrizione);
	}	
	
	@Test @Order(6)
	public void F_TestfindByAnnoAndCodice() {
		testInfo(promoRepository.findByAnnoAndCodice(Anno, Codice), Promo::getDescrizione, Descrizione);
	}	

	@Test @Order(7)
	public void G_TestSelPromoActive() {
		assertThat(promoRepository.selPromoActive())
		.size()
		.isEqualTo(1);
	}
	
	@SneakyThrows
	private void testOggetto(List<? extends DettaglioPromo> list, String expectedOggetto) {
		testInfo(list, DettaglioPromo::getOggetto, expectedOggetto);
	}
	
	@SneakyThrows
	private <T> void testInfo(List<? extends T> list, Function<T, String> getter, String expectedInfoValue) {
		assertThat(list)
		.first()
		.extracting(getter)
		.isEqualTo(expectedInfoValue);
	}

	@SneakyThrows
	private <T> void testInfo(Optional<? extends T> list, Function<T, String> getter, String expectedInfoValue) {
		assertThat(list)
		.get()
		.extracting(getter)
		.isEqualTo(expectedInfoValue);
	}
	
	@Test @Order(100)
	public void last() throws Exception	{
		assertTrue(true);
		isTerminated = true;
	}

	
	@AfterEach
	public void delPromo() {
		if (isTerminated) {
			Optional<Promo> p = promoRepository.findByAnnoAndCodice(Anno, Codice);
			if(p.isPresent())
				promoRepository.delete(p.get());
		}
	}
	
}
