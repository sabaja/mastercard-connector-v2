package com.mastercom.ps.connector.stub.manager;

import static com.mastercom.ps.connector.stub.CaseFilingServiceStub.CaseFilingStatus;

import org.apache.log4j.Logger;

import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.mastercom.CaseFiling;
import com.mastercom.ps.connector.exceptions.StubManagerException;
import com.mastercom.ps.connector.response.domain.casefiling.CaseFilingResponseHandler;
import com.mastercom.ps.connector.response.domain.casefiling.CaseFilingResponseHandlerImpl;
import com.mastercom.ps.connector.service.CaseFilingService;
import com.mastercom.ps.connector.service.CaseFilingServiceImpl;
import com.mastercom.ps.connector.stub.CaseFilingServiceStub;

/**
 * Classe di gestione flussi end-point Rest
 */
public class StubManager {

	private final Logger log = Logger.getLogger(StubManager.class);
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
	 * Metodo per la gestione e invio request vs MasterCard
	 * 
	 * @param classe
	 *            - nome servizio
	 * @param method
	 *            - nome end-point
	 * @return response da Mastercard
	 * @throws Exception
	 */
	public String send(RequestMap requestMap, String classe, String method, String fullMethodName) throws Exception {
		log.info("Inzio STUB_MANAGER | classe: [" + classe + "] metodo: [" + method + "] nome completo: ["
				+ fullMethodName + "]");
		if (null == classe || "".equals(classe)) {
			log.error(CLASS_ERR);
			throw new StubManagerException(CLASS_ERR);
		}
		if (null == method || "".equals(method)) {
			log.error(METHOD_ERR);
			throw new StubManagerException(METHOD_ERR);
		}
		log.info("Inizio gestione: " + classe + " metodo: " + method);
		String response = "";
		for (BaseServiceStub serviceClass : BaseServiceStub.values()) {
			log.debug("ciclo esterno con " + serviceClass.toString() + " processata=" + processed);
			switch (serviceClass) {
			case CaseFiling:
				if (classe.equalsIgnoreCase(serviceClass.toString())) {
					log.info("Inzio CaseFiling processata=" + processed);
					for (CaseFilingServiceStub caseFiling : CaseFilingServiceStub.values()) {
						if (!processed) {
							response = this.caseFilingResponseHandler(caseFiling, requestMap, classe, method,
									fullMethodName);
							log.debug("Response fine stub dentro ciclo interno con: " + response + " processata="
									+ processed);
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
				final String LOCAL_ERR = "Errore nella gestione flusso della classe STUB_MANAGER, dettaglio classe["
						+ classe + "] metodo[" + method + "] passati";
				log.error(LOCAL_ERR);
				throw new StubManagerException(LOCAL_ERR);
			}

		}

		return response;
	}

	private String caseFilingResponseHandler(CaseFilingServiceStub caseFiling, RequestMap requestMap, String classe,
			String method, String fullMethodName) throws Exception {
		String response = "";
		CaseFiling resource = null;
		CaseFilingService<CaseFiling, RequestMap> service = new CaseFilingServiceImpl();
		CaseFilingResponseHandler<CaseFiling> caseFilingResponse = new CaseFilingResponseHandlerImpl();
		switch (caseFiling) {
		case CaseFilingStatus:
			break;
		case Create:
			break;
		case RetrieveDocumentation:
			log.info("Inizio chiamata: " + CaseFilingStatus.RetrieveDocumentation.name());
			resource = service.retrieveDocumentation(requestMap);
			response = caseFilingResponse.getRetrieveDocumentationResponse(resource, fullMethodName);
			this.processed = true;
			log.debug("processata=" + this.processed);
			log.debug("RESPONSE dentro chiamata: " + response);
			break;
		case Update:
			break;
		default:
			break;
		}
		return response;
	}
}
