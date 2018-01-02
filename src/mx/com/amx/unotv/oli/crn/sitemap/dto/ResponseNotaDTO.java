package mx.com.amx.unotv.oli.crn.sitemap.dto;

import java.util.List;



public class ResponseNotaDTO {
	
	private List<NotaDTO> lista;

	/**
	 * Obtiene el valor de lista.
	 * @return valor de lista.
	 */
	public List<NotaDTO> getLista() {
		return lista;
	}

	/**
	 * Asigna el valor de lista.
	 * @param lista valor de lista.
	 */
	public void setLista(List<NotaDTO> lista) {
		this.lista = lista;
	}
	

}
