package it.ghismo.corso1.promoart.tests.ControllerTests;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;

import org.json.JSONException;
import org.json.JSONObject;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.ghismo.corso1.promoart.Application;
import it.ghismo.corso1.promoart.entities.DepositoRifPromo;
import it.ghismo.corso1.promoart.entities.DettaglioPromo;
import it.ghismo.corso1.promoart.entities.Promo;
import it.ghismo.corso1.promoart.repository.DepositiRifPromoRepository;
import it.ghismo.corso1.promoart.repository.DettagliPromoRepository;
import it.ghismo.corso1.promoart.repository.PromoRepository;
import lombok.SneakyThrows;

@ContextConfiguration(classes = Application.class)
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class PromoControllerTest {
	 
    private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;
	
	private String tokenJwt = "";
	private static final String authJwtUrl = "http://localhost:9100/auth";
	private static final String authJwtUid = "Admin";
	private static final String authJwtPwd = "megabanana";
	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private PromoRepository repoPromo;

	@Autowired
	private DettagliPromoRepository repoDettaglio;
	
	@Autowired
	private DepositiRifPromoRepository repoDepositi;

	@BeforeEach
	public void setup() throws JSONException, IOException {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(springSecurity()) // ghismo - attiva la sicurezza nei test
				.build();	
		
		if(this.tokenJwt.length() < 10) {
			getTokenJwt();
		}
		
	}
	
	
	String JsonData =
			" {\r\n" + 
			"        \"id\": \"\",\r\n" + 
			"        \"anno\": 2019,\r\n" + 
			"        \"codice\": \"UT01\",\r\n" + 
			"        \"descrizione\": \"CAMPAGNA TEST INSERIMENTO\",\r\n" + 
			"        \"dettagli\": [\r\n" + 
			"			{\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 1,\r\n" + 
			"                \"codart\": \"058310201\",\r\n" + 
			"                \"codfid\": \"\",\r\n" + 
			"                \"inizio\": \"2019-01-01\",\r\n" + 
			"                \"fine\": \"2019-12-31\",\r\n" + 
			"                \"oggetto\": \"1.99\",\r\n" + 
			"                \"isfid\": \"No\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 2,\r\n" + 
			"                \"codart\": \"000020030\",\r\n" + 
			"                \"codfid\": \"\",\r\n" + 
			"                \"inizio\": \"2019-01-01\",\r\n" + 
			"                \"fine\": \"2019-12-31\",\r\n" + 
			"                \"oggetto\": \"0.39\",\r\n" + 
			"                \"isfid\": \"Si\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            }\r\n" + 
			"        ],\r\n" + 
			"        \"depositoPromo\": [\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"idDeposito\": 526\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"idDeposito\": 525\r\n" + 
			"            }\r\n" + 
			"        ]\r\n" + 
			"}";
	
	private String GetNewData() {
		Promo p = repoPromo.findByAnnoAndCodice((short)2019, "UT01").get();
		DettaglioPromo firstDtl = repoDettaglio.findByPromoIdAndRiga(p.getId(), (short)1).get();
		long idDtl0 = firstDtl.getId().longValue() - 1;
		
		DepositoRifPromo firstDep = repoDepositi.findByPromoIdAndIdDeposito(p.getId(), 526).get();
		long idDep0 = firstDep.getId().longValue() - 1;
		
		String retVal =
			" {\r\n" + 
			"        \"id\": \"" + p.getId() + "\",\r\n" + 
			"        \"anno\": 2019,\r\n" + 
			"        \"codice\": \"UT01\",\r\n" + 
			"        \"descrizione\": \"CAMPAGNA TEST MODIFICA\",\r\n" + 
			"        \"dettagli\": [\r\n" + 
			"			{\r\n" + 
			"                \"id\": " + (++idDtl0) + ",\r\n" + 
			"                \"riga\": 1,\r\n" + 
			"                \"codart\": \"058310201\",\r\n" + 
			"                \"codfid\": \"\",\r\n" + 
			"                \"inizio\": \"2019-01-01\",\r\n" + 
			"                \"fine\": \"2019-12-31\",\r\n" + 
			"                \"oggetto\": \"1.89\",\r\n" +  //Modificato oggetto
			"                \"isfid\": \"No\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": " + (++idDtl0) + ",\r\n" + 
			"                \"riga\": 2,\r\n" + 
			"                \"codart\": \"000020030\",\r\n" + 
			"                \"codfid\": \"\",\r\n" + 
			"                \"inizio\": \"2019-01-01\",\r\n" + 
			"                \"fine\": \"2019-12-31\",\r\n" + 
			"                \"oggetto\": \"0.39\",\r\n" + 
			"                \"isfid\": \"No\",\r\n" +  //Modificato isfid
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            }\r\n" + 
			"        ],\r\n" + 
			"        \"depositoPromo\": [\r\n" + 
			"            {\r\n" + 
			"                \"id\": " + (++idDep0) + ",\r\n" + 
			"                \"idDeposito\": 526\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": " + (++idDep0) + ",\r\n" + 
			"                \"idDeposito\": 525\r\n" + 
			"            }\r\n" + 
			"        ]\r\n" + 
			"}";
	
		return retVal;
	}
	
	int year = Year.now().getValue();
	
	String Anno = String.valueOf(year);
	String Inizio = Anno + "-01-01";
	String Fine = Anno + "-12-31";
	
	String JsonData2 = "    {\r\n" + 
			"        \"id\": \"\",\r\n" + 
			"        \"anno\": " + Anno + ",\r\n" + 
			"        \"codice\": \"TEST01\",\r\n" + 
			"        \"descrizione\": \"PROMO FIDELITY ONLY YOU TEST\",\r\n" + 
			"        \"dettagli\": [\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 1,\r\n" + 
			"                \"codart\": \"049477701\",\r\n" + 
			"                \"codfid\": \"67000056\",\r\n" + 
			"                \"inizio\": \"" + Inizio + "\",\r\n" + 
			"                \"fine\": \"" + Fine + "\",\r\n" + 
			"                \"oggetto\": \"2,99\",\r\n" + 
			"                \"isfid\": \"Si\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 2,\r\n" + 
			"                \"codart\": \"058310201\",\r\n" + 
			"                \"codfid\": \"67000056\",\r\n" + 
			"                \"inizio\": \"" + Inizio + "\",\r\n" + 
			"                \"fine\": \"" + Fine + "\",\r\n" + 
			"                \"oggetto\": \"1,99\",\r\n" + 
			"                \"isfid\": \"Si\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 3,\r\n" + 
			"                \"codart\": \"000001501\",\r\n" + 
			"                \"codfid\": \"\",\r\n" + 
			"                \"inizio\": \"" + Inizio + "\",\r\n" + 
			"                \"fine\": \"" + Fine + "\",\r\n" + 
			"                \"oggetto\": \"3,90\",\r\n" + 
			"                \"isfid\": \"Si\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 4,\r\n" + 
			"                \"codart\": \"000001501\",\r\n" + 
			"                \"codfid\": \"\",\r\n" + 
			"                \"inizio\": \"" + Inizio + "\",\r\n" + 
			"                \"fine\": \"" + Fine + "\",\r\n" + 
			"                \"oggetto\": \"3,90\",\r\n" + 
			"                \"isfid\": \"Si\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            },\r\n" + 
			"            {\r\n" + 
			"                \"id\": -1,\r\n" + 
			"                \"riga\": 5,\r\n" + 
			"                \"codart\": \"049477701\",\r\n" + 
			"                \"codfid\": \"67000056\",\r\n" + 
			"                \"inizio\": \"" + Inizio + "\",\r\n" + 
			"                \"fine\": \"" + Fine + "\",\r\n" + 
			"                \"oggetto\": \"2,89\",\r\n" + 
			"                \"isfid\": \"Si\",\r\n" + 
			"                \"tipoPromo\": {\r\n" + 
			"                    \"id\": \"1\",\r\n" + 
			"                    \"descrizione\": \"TAGLIO PREZZO\"\r\n" + 
			"                }\r\n" + 
			"            }\r\n" + 
			"        ],\r\n" + 
			"        \"depositoPromo\": []\r\n" + 
			"    }";
	
	
	@Test
	@Order(1)
	public void testlistAllPromo() throws Exception {
		testAll(1);
	}
	
	@Test
	@Order(2)
	public void testCreatePromo() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.post("/api/promo/inserisci")
				.header("Authorization", this.tokenJwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andDo(print());
	}
	
	@Test
	@Order(3)
	public void testlistAllPromo2() throws Exception {
		testAll(2);
	}
	
	@Test
	@Order(4)
	public void testlistPromoById() throws Exception {
		Promo promo = repoPromo.findByAnnoAndCodice((short)2019, "UT01").orElse(null);
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/cerca/id/" + promo.getId())
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		
		.andExpect(jsonPath("$.id").exists())
		.andExpect(jsonPath("$.id").value(promo.getId()))
		
		//Prima riga Promozione
		.andExpect(jsonPath("$.dettagli[0].id").exists())
		.andExpect(jsonPath("$.dettagli[0].riga").exists())
		.andExpect(jsonPath("$.dettagli[0].riga").value("1"))
		.andExpect(jsonPath("$.dettagli[0].codart").exists())
		.andExpect(jsonPath("$.dettagli[0].codart").value("058310201"))
		.andExpect(jsonPath("$.dettagli[0].oggetto").exists())
		.andExpect(jsonPath("$.dettagli[0].oggetto").value("1.99")) 
		.andExpect(jsonPath("$.dettagli[0].isfid").exists())
		.andExpect(jsonPath("$.dettagli[0].isfid").value("No")) 
		
		//Tipo Promozione
		.andExpect(jsonPath("$.dettagli[0].tipoPromo.descrizione").exists())
		.andExpect(jsonPath("$.dettagli[0].tipoPromo.descrizione").value("TAGLIO PREZZO")) 
		
		//Seconda riga Promozione
		.andExpect(jsonPath("$.dettagli[1].id").exists())
		.andExpect(jsonPath("$.dettagli[1].riga").exists())
		.andExpect(jsonPath("$.dettagli[1].riga").value("2"))
		.andExpect(jsonPath("$.dettagli[1].codart").exists())
		.andExpect(jsonPath("$.dettagli[1].codart").value("000020030"))
		.andExpect(jsonPath("$.dettagli[1].oggetto").exists())
		.andExpect(jsonPath("$.dettagli[1].oggetto").value("0.39")) 
		.andExpect(jsonPath("$.dettagli[1].isfid").exists())
		.andExpect(jsonPath("$.dettagli[1].isfid").value("Si")) 
		.andExpect(jsonPath("$.dettagli[1].tipoPromo.descrizione").exists())
		.andExpect(jsonPath("$.dettagli[1].tipoPromo.descrizione").value("TAGLIO PREZZO")) 
		.andReturn();
	}
	
	@Test
	@Order(5)
	public void testUpdatePromo() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders.put("/api/promo/modifica")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", this.tokenJwt)
				.content(GetNewData())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andDo(print());
	}
	
	@Test
	@Order(6)
	public void listPromoByCodice() throws Exception {
		Promo promo = repoPromo.findByAnnoAndCodice((short)2019, "UT01").orElse(null);
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/cerca/codice?anno=2019&codice=UT01")
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		
		.andExpect(jsonPath("$.id").exists())
		.andExpect(jsonPath("$.id").value(promo.getId()))
		
		.andExpect(jsonPath("$.dettagli[0].id").exists())
		.andExpect(jsonPath("$.dettagli[0].riga").exists())
		.andExpect(jsonPath("$.dettagli[0].riga").value("1"))
		.andExpect(jsonPath("$.dettagli[0].codart").exists())
		.andExpect(jsonPath("$.dettagli[0].codart").value("058310201"))
		.andExpect(jsonPath("$.dettagli[0].oggetto").exists())
		.andExpect(jsonPath("$.dettagli[0].oggetto").value("1.89")) 
		.andExpect(jsonPath("$.dettagli[0].isfid").exists())
		.andExpect(jsonPath("$.dettagli[0].isfid").value("No")) 
		
		.andExpect(jsonPath("$.dettagli[0].tipoPromo.descrizione").exists())
		.andExpect(jsonPath("$.dettagli[0].tipoPromo.descrizione").value("TAGLIO PREZZO")) 
		
		.andExpect(jsonPath("$.dettagli[1].id").exists())
		.andExpect(jsonPath("$.dettagli[1].riga").exists())
		.andExpect(jsonPath("$.dettagli[1].riga").value("2"))
		.andExpect(jsonPath("$.dettagli[1].codart").exists())
		.andExpect(jsonPath("$.dettagli[1].codart").value("000020030"))
		.andExpect(jsonPath("$.dettagli[1].oggetto").exists())
		.andExpect(jsonPath("$.dettagli[1].oggetto").value("0.39")) 
		.andExpect(jsonPath("$.dettagli[1].isfid").exists())
		.andExpect(jsonPath("$.dettagli[1].isfid").value("No")) 
		
		.andExpect(jsonPath("$.dettagli[1].tipoPromo.descrizione").exists())
		.andExpect(jsonPath("$.dettagli[1].tipoPromo.descrizione").value("TAGLIO PREZZO")) 
		
		.andDo(print());
	}
	

	@Test
	@Order(7)
	public void testDeletePromo() throws Exception {
		Promo promo = repoPromo.findByAnnoAndCodice((short)2019, "UT01").get();
		mockMvc
		.perform(
			MockMvcRequestBuilders
			.delete("/api/promo/elimina/" + promo.getId())
			.header("Authorization", this.tokenJwt)
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andDo(print());
	}
	
	
	@Test
	@Order(8)
	public void testCreatePromo2() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.post("/api/promo/inserisci")
				.header("Authorization", this.tokenJwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonData2)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated())
		.andDo(print());
	}
	
	@Test
	@Order(9)
	public void testListPromoActiveArticoli() throws Exception {
		Promo promo = repoPromo.findByAnnoAndCodice((short)year, "TEST01").get();
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/active")
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect(jsonPath("$[0].id").exists())
		.andExpect(jsonPath("$[0].id").value(promo.getId()))
		.andExpect(jsonPath("$[0].dettagli", hasSize(5)))
		
		.andExpect(jsonPath("$[0].dettagli[0].riga").exists())
		.andExpect(jsonPath("$[0].dettagli[0].riga").value("1"))
		.andExpect(jsonPath("$[0].dettagli[0].codart").exists())
		.andExpect(jsonPath("$[0].dettagli[0].codart").value("049477701"))
		.andExpect(jsonPath("$[0].dettagli[0].oggetto").exists())
		.andExpect(jsonPath("$[0].dettagli[0].oggetto").value("2,99")) 
		.andExpect(jsonPath("$[0].dettagli[0].isfid").exists())
		.andExpect(jsonPath("$[0].dettagli[0].isfid").value("Si")) 
		// dati provenienti dal servizio articoli
		.andExpect(jsonPath("$[0].dettagli[0].descrizione").exists())
		.andExpect(jsonPath("$[0].dettagli[0].descrizione").value("PANTE.SHAMPOO RICCI ML250")) // Descrizione ricavata dal servizio articoli
		.andExpect(jsonPath("$[0].dettagli[0].prezzo").exists())
		.andExpect(jsonPath("$[0].dettagli[0].prezzo").value("4.05")) // Prezzo ricavato dal servizio articoli
		
		.andDo(print());
	}
	
	@Test
	@Order(10)
	public void ErrListPromoById() throws Exception {
		mockMvc
		.perform(
			MockMvcRequestBuilders
			.get("/api/promo/cerca/id/ABC")
			.header("Authorization", this.tokenJwt)
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.code").value(2))
		.andExpect(jsonPath("$.message").value("La Promozione con chiave di ricerca [ABC] non trovato"))
		.andDo(print());
	}
	
	@Test
	@Order(11)
	public void testDeletePromo2() throws Exception {
		Promo promo = repoPromo.findByAnnoAndCodice((short)year, "TEST01").get();
		mockMvc
		.perform(
			MockMvcRequestBuilders
			.delete("/api/promo/elimina/" + promo.getId())
			.header("Authorization", this.tokenJwt)
			.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andDo(print());
	}
	
	
	@SneakyThrows
	private void testAll(int expectedSize) {
		mockMvc
		.perform(
			MockMvcRequestBuilders
				.get("/api/promo/all")
				.header("Authorization", this.tokenJwt)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$", hasSize(expectedSize)))
		.andDo(print());
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
