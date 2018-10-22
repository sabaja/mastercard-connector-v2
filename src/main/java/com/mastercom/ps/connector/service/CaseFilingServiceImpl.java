package com.mastercom.ps.connector.service;

import org.apache.log4j.Logger;

import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.mastercom.CaseFiling;
import com.mastercom.ps.connector.errorhandling.HelperException;

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
		CaseFiling caseFiling = null;
		try {
			caseFiling = CaseFiling.create(map);
		} catch (ApiException ae) {
			String err = HelperException.getApiExceptioMessage(ae);
			log.error(err);
			throw new Exception(err);
		}
		return caseFiling;
	}

	@Override
	public CaseFiling retrieveDocumentation(RequestMap map) throws Exception {
		CaseFiling caseFiling = null;
		try {
			caseFiling = CaseFiling.retrieveDocumentation(map);
		} catch (ApiException ae) {
			String err = HelperException.getApiExceptioMessage(ae);
			log.error(err);
			throw new Exception(err);
		}
		return caseFiling;
	}

	@Override
	public CaseFiling update(RequestMap map) throws Exception {
		return new CaseFiling(map).update();
	}

	@Override
	public CaseFiling caseFilingStatus(RequestMap map) throws Exception {
		return new CaseFiling(map).caseFilingStatus();
	}

}
