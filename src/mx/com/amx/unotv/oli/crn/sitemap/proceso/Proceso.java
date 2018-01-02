/**
 * 
 */
package mx.com.amx.unotv.oli.crn.sitemap.proceso;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import mx.com.amx.unotv.oli.crn.sitemap.bo.GeneraSiteMapBO;




/**
 * @author Jesus A. Macias Benitez
 *
 */
public class Proceso {
	private static Logger LOG = Logger.getLogger(Proceso.class);	
	
	@Autowired
	private GeneraSiteMapBO generaSiteMapBO;
	
	
	
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		LOG.info("***Entrando al contexto****") ;
	}
	
	
	
	public void writeNewsML() 
	{
		LOG.info("************ INICIA generarXML ***********");
		try {
			generaSiteMapBO.generarXML();			
		LOG.info("************ FIN generarXML ***********");
		} catch (Exception e) {
			LOG.error("Exception en generarXML [ CRN ]",e);
			
			//throw new ProcesoException(e.getMessage());
		}
	}
}
