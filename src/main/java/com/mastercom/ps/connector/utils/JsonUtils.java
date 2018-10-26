package com.mastercom.ps.connector.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mastercard.api.core.model.RequestMap;

/**
 * Classe di Utilità con metodi per la gestione delle request in formato
 * <code>xml/json</code>
 * 
 * <p>
 * 
 * @author SabatiniJa
 * @since 25/09/2018
 */
public final class JsonUtils {

	private static final Logger log = Logger.getLogger(JsonUtils.class);

	/**
	 * Costante valorizzata in fase di creazione oggetto, rappresenta la stringa
	 * json in input
	 */
	private final String json;
	private JSONObject xmlJSONObj;

	/**
	 * Crea un JsonUtils e trasforma l'xml in input in una stringa json, tramite un
	 * oggetto {@link JSONObject}. Il json viene formattato in base agli argomenti
	 * passati metodo {@link XML#toJSONObject(String, boolean)}
	 * 
	 * @param xml
	 *            - stringa che rappresenta l'xml in input
	 * @param keepStrings
	 *            - Se true, allora i valori dei tag non saranno forzati in valori
	 *            booleani o numerici e saranno invece lasciati come stringhe
	 * @throws IOException
	 */
	public JsonUtils(final String xml, final boolean keepStrings) {
		// XmlMapper xmlMapper = new XmlMapper();
		// JsonNode node = xmlMapper.readTree(xml.getBytes());

		// ObjectMapper jsonMapper = new ObjectMapper();
		// String json = jsonMapper.writeValueAsString(node);
		xmlJSONObj = XML.toJSONObject(xml, keepStrings);
		this.json = xmlJSONObj.toString();
		log.debug("json intermedio: " + this.getJson());
	}

	/**
	 * getter del json
	 * 
	 * @return json valorizzato e formattato tramite costruttore di classe
	 */
	public String getJson() {
		return json;
	}

	/**
	 * Genera una stringa json formattata in base ai valori passati come argomento.
	 * 
	 * @param xml
	 * @param PRETTY_PRINT_INDENT_FACTOR
	 *            - argomento passato al metodo {@link JSONObject#toString(int)}
	 * @return json formattato.
	 */
	public String getJsonFromXml(final String xml, final boolean keepStrings, final int PRETTY_PRINT_INDENT_FACTOR) {

		JSONObject xmlJSONObj = XML.toJSONObject(xml, keepStrings);
		return xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
	}

	/**
	 * Genera la risorsa rest in formato json
	 * 
	 * @param json
	 * @param headName
	 * @return la risorsa rest
	 */
	public String createRestJson(final String json, final String headName) {
		final int LEN = ("{\"" + headName + "\":").length();
		log.debug("headName: " + headName + " LEN: " + LEN);
		String tmp = json.substring(LEN, json.lastIndexOf("}"));
		log.info("json: " + tmp);
		return tmp;
	}

	/**
	 * Metodo da usare in caso di Array singolo.</br> il metodo restituisce una stringa
	 * senza l'elemento dell'array clonato.
	 * 
	 * @param xml
	 * @param caseFilingStatus
	 * @return json corretto senza la parte clonata
	 */
	public String normalizingJson() {
		String tempJson = this.json;
		log.trace("FASE NORMALIZZAZIONE | json originale: " + this.json);
		StringBuffer text = new StringBuffer(tempJson);
		int start = text.indexOf("[");
		int end = text.indexOf("]");
		String temp = text.substring(start + 1, end);
		log.trace("FASE NORMALIZZAZIONE | temp: " + temp);
		String[] spl = temp.split(",");
		String temp2 = spl[0];
		log.trace("FASE NORMALIZZAZIONE | temp 2: " + temp2);
		String normalizedStr = StringUtils.remove(tempJson, ("," + temp2));
		log.trace("FASE NORMALIZZAZIONE | stringa normailizzata: " + normalizedStr);
		return normalizedStr;
	}
}
