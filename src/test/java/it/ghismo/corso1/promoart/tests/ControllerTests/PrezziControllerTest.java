package it.ghismo.corso1.promoart.tests.ControllerTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.ghismo.corso1.promoart.Application;
import it.ghismo.corso1.promoart.entities.DettaglioPromo;
import it.ghismo.corso1.promoart.entities.Promo;
import it.ghismo.corso1.promoart.entities.TipoPromo;
import it.ghismo.corso1.promoart.repository.DettagliPromoRepository;
import it.ghismo.corso1.promoart.repository.PromoRepository;
import lombok.SneakyThrows;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class PrezziControllerTest {
	 
    private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;
	
	private static String tokenJwt = "";
	private static final String authJwtUrl = "http://localhost:9100/auth";
	private static final String authJwtUid = "Admin";
	private static final String authJwtPwd = "megabanana";
	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private PromoRepository repoPromo;

	@Autowired
	private DettagliPromoRepository repoDettaglio;
	

    short Anno = (short)Year.now().getValue();
	String IdPromo = "";
	String Codice = "TEST01";
	String Descrizione = "PROMO TEST1";
	private static boolean isInitialized = false;
	private static boolean isTerminated = false;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	@BeforeEach
	@SneakyThrows
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(springSecurity()) // ghismo - attiva la sicurezza nei test
				.build();	
		
		if(isInitialized) return;

		getTokenJwt();
		
		UUID uuid = UUID.randomUUID();
		IdPromo = uuid.toString();
		TipoPromo tipopromo = new TipoPromo(1);
		
		Promo promo = Promo.builder()
						.id(IdPromo)
						.anno(Anno)
						.codice(Codice)
						.descrizione(Descrizione)
						.build();	
		repoPromo.save(promo);
		
		//La promo sarà valida l'intero anno corrente
		Date Inizio = sdf.parse(String.valueOf(Anno) + "-01-01");  
		Date Fine = sdf.parse(String.valueOf(Anno) + "-12-31");
		
		DettaglioPromo dettPromo;
		
		promo = repoPromo.findByAnnoAndCodice(Anno, Codice).get();
		
		//riga 1 promozione standard
		dettPromo = DettaglioPromo.builder()
						.id(-1L)
						.inizio(Inizio)
						.fine(Fine)
						.codart("049477701")
						.oggetto("1.10")
						.isfid("No")
						.riga((short)1)
						.tipoPromo(tipopromo)
						.promo(promo)
						.build();		
		repoDettaglio.save(dettPromo);
		
		//riga 2 promozione fidelity
		dettPromo = DettaglioPromo.builder()
				.id(-1L)
				.inizio(Inizio)
				.fine(Fine)
				.codart("004590201")
				.oggetto("1.99")
				.isfid("Si")
				.riga((short)2)
				.tipoPromo(tipopromo)
				.promo(promo)
				.build();		
		repoDettaglio.save(dettPromo);

		//riga 3 promozione fidelity Only You
		dettPromo = DettaglioPromo.builder()
				.id(-1L)
				.inizio(Inizio)
				.fine(Fine)
				.codart("008071001")
				.oggetto("2.19")
				.isfid("Si")
				.codfid("67000076")
				.riga((short)3)
				.tipoPromo(tipopromo)
				.promo(promo)
				.build();		
		repoDettaglio.save(dettPromo);

		
		Anno--; //assicuriamoci che la promo sia scaduta
		Inizio = sdf.parse(String.valueOf(Anno) + "-01-01");  
		Fine = sdf.parse(String.valueOf(Anno) + "-12-31");
		
		//riga 4 promozione standard scaduta
		dettPromo = DettaglioPromo.builder()
				.id(-1L)
				.inizio(Inizio)
				.fine(Fine)
				.codart("002001601")
				.oggetto("0.99")
				.isfid("No")
				.riga((short)4)
				.tipoPromo(tipopromo)
				.promo(promo)
				.build();		
		repoDettaglio.save(dettPromo);
				
		isInitialized = true;
	}
	
	
	
	
	@Test
	@Order(1)
	public void testGetPromoCodArt() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/prezzo/049477701")
				.header("Authorization", tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$").value("1.1")) 
		.andReturn();
	}
	
	@Test
	@Order(2)
	public void testGetPromoCodArtFid() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/prezzo/004590201?fidelity=true")
				.header("Authorization", tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$").value("1.99")) 
		.andReturn();
	}
	
	@Test
	@Order(3)
	public void testGetPromoCodArtCodFid() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/prezzo/008071001/67000076")
				.header("Authorization", tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$").value("2.19")) 
		.andReturn();
	}
	
	@Test
	@Order(4)
	public void testGetPromoScad() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/prezzo/002001601")
				.header("Authorization", tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andReturn();
	}
	

	@Test @Order(100)
	public void last() throws Exception	{
		assertTrue(true);
		isTerminated = true;
	}

	
	@AfterEach
	public void DelPromo() {
		if (isTerminated)
			repoPromo.delete(repoPromo.findByAnnoAndCodice(Anno, Codice).get());
	}	
	
	
	
	private void getTokenJwt() {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		JSONObject rq = new JSONObject();
		try {
			rq.put("username", authJwtUid);
			rq.put("password", authJwtPwd);
		} catch (JSONException e) {}
		
		HttpEntity<String> httpE = new HttpEntity<String>(rq.toString(), headers);
		String rs = rt.postForObject(authJwtUrl, httpE, String.class);
		
		try {
			this.tokenJwt = "Bearer " + om.readTree(rs).path("token").asText("");
		} catch (JsonProcessingException e) {
			this.tokenJwt = "sta ceppa";
		}
		
		System.out.println("ghismo - token ricavato dal test:" + this.tokenJwt);
		
	}
	
	
}
