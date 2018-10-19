package com.mastercom.ps.connector.service;

import java.util.Map;

import org.apache.log4j.Logger;

import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.mastercom.CaseFiling;

/**
 * Classe Service per le chiamate Rest vs MastrerCard
 * 
 * @author SabatiniJa
 *
 */
public class CaseFilingServiceImpl implements CaseFilingService<CaseFiling, RequestMap> {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 8480424631637684504L;
	private static final Logger log = Logger.getLogger(CaseFilingServiceImpl.class);

	@Override
	public CaseFiling create(RequestMap map) throws Exception {
		log.debug("Prima create Service");
		CaseFiling caseFiling = CaseFiling.create(map);

		if (null != caseFiling && !caseFiling.isEmpty()) {
			for (Map.Entry<String, Object> entry : caseFiling.entrySet()) {
				log.debug("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}
		}else {
			log.debug("caseFiling NUll");
		}
		return caseFiling;
	}

	@Override
	public CaseFiling retrieveDocumentation(RequestMap map) throws ApiException {
		return CaseFiling.retrieveDocumentation(map);
	}

	@Override
	public CaseFiling update(RequestMap map) throws ApiException {
		return new CaseFiling(map).update();
	}

	@Override
	public CaseFiling caseFilingStatus(RequestMap map) throws ApiException {
		return new CaseFiling(map).caseFilingStatus();
	}

}
