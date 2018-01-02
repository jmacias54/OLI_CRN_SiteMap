/**
 * 
 */
package mx.com.amx.unotv.oli.crn.sitemap.bo;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mx.com.amx.unotv.oli.crn.sitemap.dto.NotaDTO;
import mx.com.amx.unotv.oli.crn.sitemap.dto.ParametrosDTO;
import mx.com.amx.unotv.oli.crn.sitemap.util.CargaProperties;
import mx.com.amx.unotv.oli.crn.sitemap.util.Constants;
import mx.com.amx.unotv.oli.crn.sitemap.ws.SiteMapCallWS;

/**
 * @author Jesus A. Macias Benitez
 *
 */
public class GeneraSiteMapBO {

	private final Logger log = Logger.getLogger(GeneraSiteMapBO.class);

	ParametrosDTO parametrosDTO = new ParametrosDTO();

	@Autowired
	SiteMapCallWS siteMapCallWS = null;

	public void generarXML() throws Exception {

		DOMSource sourceRet = new DOMSource();
		// ObtenerProperties obtenerProperties = new ObtenerProperties();
		// String linkseccion = "";
		try {
			// Obtengo las noticias de la base
			CargaProperties cargaProperties = new CargaProperties();
			parametrosDTO = cargaProperties.obtenerPropiedades("ambiente.resources.properties");

			String rutaArchivo = parametrosDTO.getRutaArchivo() + parametrosDTO.getNombreSiteMap() + ".xml";
			// PROD
			// rutaArchivo="/var/dev-repos/shared_www/web02/pyeongchang/sitemaps/news-sitemap.xml";
			// DEV
			// rutaArchivo="/var/dev-repos/shared_www/pyeongchang/sitemaps/news-sitemap.xml";

			List<NotaDTO> rssList = siteMapCallWS.getElementosNewsSiteMap(100);
			log.info("Num de Notas para el SiteMap: " + rssList.size());
			log.debug("Num de Notas para el SiteMap: " + rssList.size());

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			org.w3c.dom.Document docXML = docBuilder.newDocument();
			docXML.createTextNode("<?xml version='1.0' encoding='UTF-8'?>");
			org.w3c.dom.Element rootElement = docXML.createElement("urlset");

			rootElement.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
			rootElement.setAttribute("xmlns:news", "http://www.google.com/schemas/sitemap-news/0.9");

			docXML.appendChild(rootElement);

			if (rssList != null && rssList.size() > 0) {

				// SE RECORRE EL ARREGLO DE NOTICIAS
				for (int i = 0; i < rssList.size(); i++) {

					StringBuffer sb = new StringBuffer();

					org.jdom.Element question = new org.jdom.Element("item");

					org.w3c.dom.Element url = docXML.createElement("url");
					rootElement.appendChild(url);

					org.w3c.dom.Element loc = docXML.createElement("loc");

					String linkDetail = rssList.get(i).getFcLinkDetalle();

					if (parametrosDTO.getAmbiente().equals(Constants.ENVIROMENT_DEV)) {
						linkDetail = linkDetail.replace(Constants.URL_PROD, Constants.URL_DEV);
					}

					loc.appendChild(docXML.createCDATASection(linkDetail));

					org.w3c.dom.Element noticiaXML = docXML.createElement("news:news");
					rootElement.appendChild(noticiaXML);

					sb.delete(0, sb.length());

					org.jdom.Element item = new org.jdom.Element("item");

					// PUBLICATION
					org.w3c.dom.Element publication = docXML.createElement("news:publication");

					// TITLE PUBLICATION
					org.w3c.dom.Element titlePub = docXML.createElement("news:name");
					String tituloPub = "Uno TV Noticias";
					titlePub.appendChild(docXML.createCDATASection(tituloPub));
					publication.appendChild(titlePub);
					//
					org.w3c.dom.Element lg = docXML.createElement("news:language");
					// LANGUAGE
					lg.appendChild(docXML.createCDATASection("es"));
					//
					publication.appendChild(lg);

					// GENRES
					org.w3c.dom.Element genres = docXML.createElement("news:genres");
					genres.appendChild(docXML.createCDATASection("UserGenerated"));
					//
					// TITLE
					org.w3c.dom.Element title = docXML.createElement("news:title");
					String titulo = rssList.get(i).getFcTitulo();
					title.appendChild(docXML.createCDATASection(titulo));

					//
					org.w3c.dom.Element pubDate = docXML.createElement("news:publication_date");
					// PUBLICATION DATE
					pubDate.appendChild(docXML.createCDATASection(rssList.get(i).getFcFechaPubli()));
					//
					org.w3c.dom.Element key = docXML.createElement("news:keywords");
					// KEYWORDS
					key.appendChild(docXML.createCDATASection(rssList.get(i).getFcKeyWords()));

					url.appendChild(loc);
					noticiaXML.appendChild(publication);
					noticiaXML.appendChild(genres);
					noticiaXML.appendChild(pubDate);
					noticiaXML.appendChild(title);
					noticiaXML.appendChild(key);
					url.appendChild(noticiaXML);

					question.addContent(item);

				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(docXML);

				if (createFolders(parametrosDTO.getRutaArchivo())) {
					sourceRet = source;

					File f = new File(rutaArchivo);
					StreamResult result = new StreamResult(f);
					transformer.transform(sourceRet, result);
					log.info("Archivo " + rutaArchivo + " generado Satisfactoriamente ");

					log.debug("Archivo " + rutaArchivo + " generado Satisfactoriamente ");
				}

			}
			// end if

		} catch (Exception e) {
			log.error("Error generarXML: ", e);
			log.debug("Error generarXML: ", e);
		}
	}

	public void inicializaProceso() throws Exception {
		log.info("En inicializaProceso ");
		log.debug("En inicializaProceso ");

		try {
			generarXML();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}

	public void transfiereWebServer(ParametrosDTO parametrosDTO) throws Exception {
		try {
			// Properties propiedades = new Properties();
			// propiedades.load(this.getClass().getResourceAsStream(
			// "/ApplicationResources.properties" ));
			String rutaShell = parametrosDTO.getRutaShell();
			String pathLocal = parametrosDTO.getRutaArchivo();
			String pathRemote = parametrosDTO.getRutaWebServer();
			String comando = rutaShell + " " + pathLocal + " " + pathRemote;
			log.debug("Comando: " + comando);
			log.info("Comando: " + comando);
			Runtime r = Runtime.getRuntime();
			r.exec(comando);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}

	/*
	 * public void inciaSecuencia() throws Exception { try { llamadasWS = new
	 * LlamadasWS(); llamadasWS.getSecuencia(); } catch (Exception e) { throw new
	 * Exception(e.getMessage()); } }
	 * 
	 * public void obtieneSecuenciaActual() throws Exception { try { llamadasWS =
	 * new LlamadasWS(); llamadasWS.getSecuenciaActual(); } catch (Exception e) {
	 * throw new Exception(e.getMessage()); } }
	 */
	public boolean createFolders(String carpetaContenido) {
		boolean success = false;
		try {
			File carpetas = new File(carpetaContenido);
			if (!carpetas.exists()) {
				success = carpetas.mkdirs();
			} else
				success = true;
		} catch (Exception e) {
			success = false;
			log.error("Ocurrio error al crear las carpetas: ", e);
			log.debug("Ocurrio error al crear las carpetas: ", e);
		}
		return success;
	}

}
