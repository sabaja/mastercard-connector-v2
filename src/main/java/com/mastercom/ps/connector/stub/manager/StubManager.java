package com.mastercom.ps.connector.stub.manager;

import org.apache.log4j.Logger;

import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.mastercom.CaseFiling;
import com.mastercom.ps.connector.errorhandling.StubManagerException;
import com.mastercom.ps.connector.response.domain.casefiling.CaseFilingResponseHandler;
import com.mastercom.ps.connector.response.domain.casefiling.CaseFilingResponseHandlerImpl;
import com.mastercom.ps.connector.service.CaseFilingService;
import com.mastercom.ps.connector.service.CaseFilingServiceImpl;
import com.mastercom.ps.connector.stub.CaseFilingServiceStub;

/**
 * Classe di gestione flussi end-point Rest
 */
public class StubManager {

	private static final Logger log = Logger.getLogger(StubManager.class);
	private final String CLASS_ERR = "Classe non è valorizzata";
	private final String METHOD_ERR = "Metodo non valorizzato";
	private boolean processed = false;

	/**
	 * Rappresentazione dei servizi esposti da Mastercard:
	 * 
	 * <pre>
	 * CaseFiling, Chargebacks, Claims, Fees, Fraud, HealthCheck, Queues,Retrievals,
	 * Transactions
	 * </pre>
	 * 
	 * @author SabatiniJa
	 *
	 */
	public static enum BaseServiceStub {
		/**
		 *
		 */
		CaseFiling, Chargebacks, Claims, Fees, Fraud, HealthCheck, MigratedDisputes, Queues, Retrievals, Transactions;
	};

	public StubManager() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * Metodo per la selezione della risorsa e invio request vs MasterCard
	 * 
	 * @param clazz
	 *            - nome servizio
	 * @param method
	 *            - nome end-point
	 * @return response da Mastercard
	 * @throws Exception
	 */
	public String send(RequestMap requestMap, String clazz, String method, String fullMethodName) throws Exception {
		log.info(
				"Inzio STUB_MANAGER Classe: " + clazz + " | metodo: " + method + " | nome completo: " + fullMethodName);
		if (null == clazz || "".equals(clazz)) {
			log.error(CLASS_ERR);
			throw new StubManagerException(CLASS_ERR);
		}
		if (null == method || "".equals(method)) {
			log.error(METHOD_ERR);
			throw new StubManagerException(METHOD_ERR);
		}
		String response = "";
		// Selezione classe/Risorsa
		for (BaseServiceStub serviceClass : BaseServiceStub.values()) {
			log.trace(" -*- Risorsa: " + serviceClass.toString() + " | metodo: " + method + " |  processata? "
					+ processed + " | clazz: " + clazz + " | nome completo: " + fullMethodName);
			switch (serviceClass) {
			case CaseFiling:
				if (clazz.equalsIgnoreCase(serviceClass.toString())) {
					log.info(" -**- Selezione Risorsa: " + serviceClass.toString() + " | metodo: " + method
							+ " |  processata? " + processed + " | clazz: " + clazz + " | nome completo: "
							+ fullMethodName);
					for (CaseFilingServiceStub caseFiling : CaseFilingServiceStub.values()) {
						if (!processed) {
							log.trace(" -***- Metodo " + caseFiling.toString() + " |  processata? " + processed);
							response = this.caseFilingHandler(caseFiling, requestMap, clazz, method, fullMethodName);
							log.trace(" -***- Fine-Metodo " + caseFiling.toString() + " |  processata? " + processed);
						}
					}
				}
				break;
			case Chargebacks:
				break;
			case Claims:
				break;
			case Fees:
				break;
			case Fraud:
				break;
			case HealthCheck:
				break;
			case MigratedDisputes:
				break;
			case Queues:
				break;
			case Retrievals:
				break;
			case Transactions:
				break;
			default:
				final String LOCAL_ERR = "Errore nella gestione flusso della clazz STUB_MANAGER, dettaglio clazz["
						+ clazz + "] metodo[" + method + "] passati";
				log.error(LOCAL_ERR);
				throw new StubManagerException(LOCAL_ERR);
			}

		}

		return response;
	}

	private String caseFilingHandler(final CaseFilingServiceStub caseFiling, final RequestMap requestMap,
			final String clazz, final String method, final String fullMethodName) throws Exception {
		String response = "";
		CaseFiling resource = null;
		CaseFilingService<CaseFiling, RequestMap> service = new CaseFilingServiceImpl();
		CaseFilingResponseHandler<CaseFiling> caseFilingResponse = new CaseFilingResponseHandlerImpl();
		log.trace(" -****- Inizio Handler Servizio - metodo passato: " + method + "| clazz: " + clazz + " | full name method: "
				+ fullMethodName + " | oggetto map: " + requestMap);
		try {
			switch (caseFiling) {
			case Create:
				if (CaseFilingServiceStub.Create.name().equalsIgnoreCase(method)) {
					log.info(" -*****- " + CaseFilingServiceStub.Create.name() + " - Inizio chiamata! - " ) ;
					resource = service.create(requestMap);
					response = caseFilingResponse.getCreateResponse(resource, fullMethodName);
					this.processed = true;
					log.trace("processata = " + this.processed);
					break;
				}
			case RetrieveDocumentation:
				if (CaseFilingServiceStub.RetrieveDocumentation.name().equalsIgnoreCase(method)) {
					log.info(" -*****- " + CaseFilingServiceStub.RetrieveDocumentation.name()+ " - Inizio chiamata! - " );
					resource = service.retrieveDocumentation(requestMap);
					response = caseFilingResponse.getRetrieveDocumentationResponse(resource, fullMethodName);
					this.processed = true;
					log.trace(" -*****- Processata = " + this.processed);
					break;
				}
			case Update:
				if (CaseFilingServiceStub.Update.name().equalsIgnoreCase(method)) {
					log.info(" -*****- " + CaseFilingServiceStub.Update.name()+ " - Inizio chiamata! - " );
					resource = service.update(requestMap);
					response = caseFilingResponse.getUpdateResponse(resource, fullMethodName);
					this.processed = true;
					log.trace(" -*****- Processata = " + this.processed);
					break;
				}
			case CaseFilingStatus:
				if (CaseFilingServiceStub.CaseFilingStatus.name().equalsIgnoreCase(method)) {
					log.info(" -*****- " + CaseFilingServiceStub.CaseFilingStatus.name()
							+ " - Inizio chiamata! - " );
					resource = service.caseFilingStatus(requestMap);
					response = caseFilingResponse.getCaseFilingStatusResponse(resource, fullMethodName);
					this.processed = true;
					log.trace(" -*****- Processata = " + this.processed);
					break;
				}
			default:
				this.processed = false;
				final String ERR = fullMethodName + " | " + METHOD_ERR;
				log.error(ERR);
				throw new Exception(ERR);
			}
		} catch (ApiException ex) {
			log.error("Errore nella chiamata al servizio " + fullMethodName);
			throw new Exception(ex);
		}
		return response;
	}
}
