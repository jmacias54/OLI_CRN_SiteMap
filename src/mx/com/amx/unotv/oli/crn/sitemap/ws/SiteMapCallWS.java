/**
 * 
 */
package mx.com.amx.unotv.oli.crn.sitemap.ws;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import mx.com.amx.unotv.oli.crn.sitemap.dto.NotaDTO;
import mx.com.amx.unotv.oli.crn.sitemap.dto.ResponseNotaDTO;

/**
 * @author Jesus A. Macias Benitez
 *
 */
public class SiteMapCallWS {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private RestTemplate restTemplate;
	private String URL_WS_BASE = "";
	private HttpHeaders headers = new HttpHeaders();
	private final Properties props = new Properties();

	public SiteMapCallWS() {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();

		if (factory instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) factory).setConnectTimeout(15 * 1000);
			((SimpleClientHttpRequestFactory) factory).setReadTimeout(15 * 1000);
			logger.debug("Inicializando rest template 1");
		} else if (factory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout(15 * 1000);
			((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout(15 * 1000);
			logger.debug("Inicializando rest template 2");
		}

		restTemplate.setRequestFactory(factory);
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {
			props.load(this.getClass().getResourceAsStream("/general.properties"));
		} catch (Exception e) {
			logger.error("[ConsumeWS::init]Error al iniciar y cargar arhivo de propiedades." + e.getMessage());
			logger.debug("[ConsumeWS::init]Error al iniciar y cargar arhivo de propiedades." + e.getMessage());
		}
		String ambiente = props.getProperty("ambiente");
		URL_WS_BASE = props.getProperty(ambiente + ".url.ws.base");
	}

	public List<NotaDTO> getElementosNewsSiteMap(int numElementos) {

		logger.debug("  Llamada WS - getElementosNewsSiteMap  ");

		String metodo = "getElementosNewsSiteMap";
		String URL_WS = URL_WS_BASE + metodo;
		List<NotaDTO> listNotasRecibidas = null;

		ResponseNotaDTO request = new ResponseNotaDTO();

		try {
			logger.info("URL_WS: " + URL_WS);
			logger.debug("URL_WS: " + URL_WS);

			restTemplate = new RestTemplate();

			HttpEntity<String> entity = new HttpEntity<String>("Accept=application/json; charset=utf-8", headers);
			// estTemplate.getMessageConverters().add(new
			// MappingJackson2HttpMessageConverter());
			request = restTemplate.postForObject(URL_WS + "/" + numElementos, entity, ResponseNotaDTO.class);

			listNotasRecibidas = request.getLista();

		} catch (Exception e) {
			logger.error("Error getElementosNewsSiteMap [LlamadasWS]: ", e);
			logger.debug("Error getElementosNewsSiteMap [LlamadasWS]: ", e);
		}
		return listNotasRecibidas;
	}

	public Boolean actualizarEstatusElemento(String idContenido) {

		String metodo = "actualizarEstatusElemento";
		String URL_WS = URL_WS_BASE + metodo;
		boolean resultado = false;
		try {
			logger.info("URL_WS: " + URL_WS);
			logger.debug("URL_WS: " + URL_WS);
			HttpEntity<String> entity = new HttpEntity<String>(idContenido);
			resultado = restTemplate.postForObject(URL_WS, entity, Boolean.class);
		} catch (Exception e) {
			logger.error("Error actualizarEstatusElemento [LlamadasWS]: ", e);
			logger.debug("Error actualizarEstatusElemento [LlamadasWS]: ", e);
		}
		return resultado;
	}

	public int getSecuencia() {

		String metodo = "getSecuencia";
		String URL_WS = URL_WS_BASE + metodo;
		int secuencia = 0;
		try {
			logger.info("URL_WS: " + URL_WS);
			logger.debug("URL_WS: " + URL_WS);

			secuencia = restTemplate.getForObject(URL_WS, Integer.class);
		} catch (Exception e) {
			logger.error("Error getSecuencia [LlamadasWS]: ", e);
			logger.debug("Error getSecuencia [LlamadasWS]: ", e);
		}
		return secuencia;
	}

}
