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

	private static final Logger log = Logger.getLogger(CaseFilingServiceImpl.class);

	@Override
	public CaseFiling create(RequestMap map) throws Exception {
		log.trace("Dentro Service CaseFilingServiceImpl.create");
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
		log.trace("Dentro Service CaseFilingServiceImpl.retrieveDocumentation");
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
		CaseFiling caseFiling = null;
		log.trace("Dentro Service CaseFilingServiceImpl.update");
		try {
			caseFiling = new CaseFiling(map).update();
		} catch (ApiException ae) {
			String err = HelperException.getApiExceptioMessage(ae);
			log.error(err);
			throw new Exception(err);
		}
		return caseFiling;
	}

	@Override
	public CaseFiling caseFilingStatus(RequestMap map) throws Exception {
		CaseFiling caseFiling = null;
		log.trace("Dentro Service CaseFilingServiceImpl.caseFilingStatus");
		try {
			caseFiling = new CaseFiling(map).caseFilingStatus();
		} catch (ApiException ae) {
			String err = HelperException.getApiExceptioMessage(ae);
			log.error(err);
			throw new Exception(err);
		}
		return caseFiling;
	}

}
