package it.ghismo.corso1.promoart.security;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ServiceConfig {
	String url;
	String securityUid;
	String securityPwd;
	
	
	
}
