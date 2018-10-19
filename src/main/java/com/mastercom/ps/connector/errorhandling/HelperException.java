package com.mastercom.ps.connector.errorhandling;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.mastercard.api.core.exception.ApiException;

import com.mastercard.api.core.model.map.CaseInsensitiveSmartMap;
import com.mastercom.ps.connector.utils.MDR;

/**
 * Gestisce le eccezioni MDR
 * 
 * @author SabatiniJa
 *
 */
public class HelperException {

	/**
	 * Cattura il dettaglio del messaggio d'errore a fronte di un'ApiException sulla
	 * risorsa Rest invocata
	 * 
	 * @param ae
	 * @return
	 */
	public static ApiException apiException;

	public static String getApiExceptioMessage(final ApiException ae) {
		apiException = ae;
		List<CaseInsensitiveSmartMap> errors = ae.getErrors();
		Iterator<CaseInsensitiveSmartMap> i = errors.iterator();
		StringBuilder sb = new StringBuilder();
		while (i.hasNext()) {
			CaseInsensitiveSmartMap errMap = i.next();
			{
				for (Map.Entry<String, Object> entry : errMap.entrySet()) {
					sb.append("" + entry.getKey() + "=" + entry.getValue() + "|");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Generazione del messaggio d'errore
	 * 
	 * @param clazz
	 * 
	 * @param message
	 * @return
	 */
	public static String getMessageError(final String clazz, final String message) {
		String status = "500";
		if (null != apiException) {
			status = String.valueOf(apiException.getHttpStatus());
		}
		return String.format(MDR.ERROR.getMsg(), status, clazz, message);
	}

}
