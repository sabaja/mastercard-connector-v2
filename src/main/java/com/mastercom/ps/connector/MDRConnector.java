package com.mastercom.ps.connector;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetHeaders;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.log4j.Logger;

import com.mastercard.api.core.ApiConfig;
import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.model.map.SmartMap;
import com.mastercard.api.core.security.oauth.OAuthAuthentication;
import com.mastercard.api.mastercom.CaseFiling;
import com.mastercom.ps.connector.config.ServiceConfiguration;
import com.mastercom.ps.connector.config.TransactionLogConfig;
import com.mastercom.ps.connector.errorhandling.HelperException;
import com.mastercom.ps.connector.stub.manager.StubManager;
import com.mastercom.ps.connector.utils.JsonUtils;
import com.mastercom.ps.connector.utils.XmlUtils;
import com.peoplesoft.pt.integrationgateway.common.ConnectorData;
import com.peoplesoft.pt.integrationgateway.common.ConnectorDataCollection;
import com.peoplesoft.pt.integrationgateway.common.DuplicateMessageException;
import com.peoplesoft.pt.integrationgateway.common.ExternalApplicationException;
import com.peoplesoft.pt.integrationgateway.common.ExternalSystemContactException;
import com.peoplesoft.pt.integrationgateway.common.GeneralFrameworkException;
import com.peoplesoft.pt.integrationgateway.common.InvalidMessageException;
import com.peoplesoft.pt.integrationgateway.common.MessageMarshallingException;
import com.peoplesoft.pt.integrationgateway.common.MessageUnmarshallingException;
import com.peoplesoft.pt.integrationgateway.framework.ConnectorInfo;
import com.peoplesoft.pt.integrationgateway.framework.IBRequest;
import com.peoplesoft.pt.integrationgateway.framework.IBResponse;
import com.peoplesoft.pt.integrationgateway.framework.InternalIBResponse;
import com.peoplesoft.pt.integrationgateway.targetconnector.TargetConnector;
import com.thoughtworks.xstream.XStream;


import com.mastercard.api.core.model.Environment;
/**
 * Classe di connessione tra l'Integration Broker di Peoplesoft e l'API di
 * MasterCard.</br>
 * Il connettore implementa l'interfaccia {@link TargetConnection} che, a fronte
 * di ogni request da inviare a Mastercard PeopleSoft ne invoca il metodo
 * {@link MDRConnector#send(IBRequest)}. <br>
 * Tale meccanismo isola chiamate in modo tale da rendere il connettore
 * thread-safe.<br>
 * Il processo di elaborazione del connettore è suddivisibile in quattro stadi
 * <u>consecutivi</u>:<br>
 * <ol type="1">
 * <li>Ricezione request in formato xml</li>
 * <li>Elaborazione dati:
 * <ul>
 * <li>Conversione da xml in json</li>
 * <li>Implementazione oggetto Rest con i dati presenti nel json</li>
 * </ul>
 * </li>
 * <li>Apertura connessione tramite {@link SSLConnectionSocketFactory} verso
 * Mastercard:
 * <ul>
 * <li>Invio request</li>
 * <li>Ricezione response</li>
 * </ul>
 * <li>Elaborazione dati response, con conversione da oggetto Rest (URI di tipo
 * ) a xml</li>
 * <li>Invio a Peoplesoft della response</li>
 * </ol>
 * 
 * </br>
 * Il connettore è <em>thread-safe</em> </br>
 * In caso di eccezione, l'errore viene incapsulato in un tag
 * <code>faultcode</code> di un xml e spedito a PeopleSoft.
 * 
 */
public class MDRConnector implements TargetConnector {

	private static final Logger log = Logger.getLogger(MDRConnector.class);
	private ConnectorInfo connInfo;
	private ServiceConfiguration serviceConfiguration;
	private XStream xstream;
	
	class CaseFilingStatusBKP {

		@SuppressWarnings("unchecked")
		public void test() throws Exception {

			final String P12 = "C:\\Users\\sabatinija\\Desktop\\Devspace\\PeopleSoft\\Mastercards\\MCD_Sandbox_MasterCom_API_TEST_API_Keys\\MasterCom_API_TEST-sandbox.p12";
			
			String consumerKey = "4zoJ6bSBi2I10kY2__njjwSB4YMaQIa7Xj0_OW2G7243f6b5!a6b6fa1d5324471b9bebb0e96f7ad0a00000000000000000"; // You should copy this from "My Keys" on your project page e.g.
														// UTfbhDCSeNYvJpLL5l028sWL9it739PYh6LU5lZja15xcRpY!fd209e6c579dc9d7be52da93d35ae6b6c167c174690b72fa
			String keyAlias = "keyalias"; // For production: change this to the key alias you chose when you created your
											// production key
			String keyPassword = "keystorepassword"; // For production: change this to the key alias you chose when you
														// created your production key
			InputStream is = new FileInputStream(P12); // e.g.
																						// /Users/yourname/project/sandbox.p12
																						// |
																						// C:\Users\yourname\project\sandbox.p12
			ApiConfig.setAuthentication(new OAuthAuthentication(consumerKey, is, keyAlias, keyPassword)); // You only need
																											// to set this
																											// once
			ApiConfig.setDebug(true); // Enable http wire logging
			// This is needed to change the environment to run the sample code. For
			// production: use ApiConfig.setSandbox(false);
			ApiConfig.setEnvironment(Environment.parse("sandbox"));

			try {
				RequestMap map = new RequestMap();
				map.set("caseFilingList[0].caseId", "536092");
				CaseFiling response = new CaseFiling(map).caseFilingStatus();

				out(response, "caseFilingResponseList[0].caseId"); // -->536092
				out(response, "caseFilingResponseList[0].status"); // -->COMPLETED
				// This sample shows looping through caseFilingResponseList
				System.out.println("This sample shows looping through caseFilingResponseList");
				for (Map<String, Object> item : (List<Map<String, Object>>) response.get("caseFilingResponseList")) {
					out(item, "caseId");
					out(item, "status");
					
				}

			} catch (ApiException e) {
				err("HttpStatus: " + e.getHttpStatus());
				err("Message: " + e.getMessage());
				err("ReasonCode: " + e.getReasonCode());
				err("Source: " + e.getSource());
			}
		}

		public void out(SmartMap response, String key) {
			System.out.println(key + "-->" + response.get(key));
		}

		public void out(Map<String, Object> map, String key) {
			System.out.println(key + "--->" + map.get(key));
		}

		public void err(String message) {
			System.err.println(message);
		}
	}
//TODO
	public void init(ConnectorInfo connInfo) {
		this.serviceConfiguration = new ServiceConfiguration(connInfo);
		this.xstream = new XStream();
		XStream.setupDefaultSecurity(xstream);

	}

	@Override
	public ConnectorDataCollection introspectConnector() {
		final ConnectorDataCollection conCollection = new ConnectorDataCollection();
		// TODO
		// Da concordare con IB
		final ConnectorData conData = new ConnectorData("MASTERCOM");
		conData.addConnectorField("URL", false, "", "");
		conData.addConnectorField("HEADER", "TimeOut", false, "", "");
		conCollection.addConnectorData(conData);
		return conCollection;
	}

	@Override
	public IBResponse ping(IBRequest request) throws GeneralFrameworkException, DuplicateMessageException,
			InvalidMessageException, ExternalSystemContactException, ExternalApplicationException,
			MessageMarshallingException, MessageUnmarshallingException {
		return send(request);
	}

	@Override
	public IBResponse send(IBRequest request) throws GeneralFrameworkException, DuplicateMessageException,
			InvalidMessageException, ExternalSystemContactException, ExternalApplicationException,
			MessageMarshallingException, MessageUnmarshallingException {
		// TODO
		log.info("SEND | Inzio richiesta");
		String responseString = null;
		String xml = "", xmlObjectRequest = "", jsonObjectRequest = "", serviceName = "";
		XmlUtils xmlUtils = null;
		JsonUtils jsonUtils = null;
		RequestMap requestMap = null;
		XStream xstream = new XStream();
		XStream.setupDefaultSecurity(xstream);
		TransactionLogConfig transactionLogConfig = null;
		StubManager stubManager = new StubManager();
		try {
			this.setConnInfo(request.getConnectorInfo());
			this.init(this.getConnInfo());

			final String xmlRequest = request.getContentSectionAt(0);
			log.info("Request from PS: " + xmlRequest);

		} catch (Exception e) {
			// TODO - da inseririre la variabile stringa per il metodo da passare all'errore
			// vedi main
			responseString = HelperException.getMessageError(e.getClass().getSimpleName(), "", e.getMessage());
			log.error("Exception: " + responseString);
		}
		final InternalIBResponse response = new InternalIBResponse();
		final InternetHeaders internetHeaders = new InternetHeaders();

		try {
			response.addContentSection(internetHeaders, responseString);
		} catch (final Exception xe) {
			log.error("Exception: " + xe.getMessage() + " -> " + xe.toString() + " STACKTRACE " + xe.getStackTrace());
		} finally {
			if (transactionLogConfig != null) {
				transactionLogConfig.requestDestroyed();
			}
		}
		return null;
	}

	private ConnectorInfo getConnInfo() {
		return connInfo;
	}

	private void setConnInfo(ConnectorInfo connInfo) {
		this.connInfo = connInfo;
	}

	@SuppressWarnings("unused")
	private ServiceConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}

	private void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
		this.serviceConfiguration = serviceConfiguration;
	}

	public static void main(String[] args) throws Exception {
		final boolean CASE = true;
		MDRConnector connector = new MDRConnector();

		if (!CASE) {
			connector.test();
		} else {
			String file = "";
			// TODO
			// Configurazione da togliere
			// CaseFiling.retrieveDocumentation
//			file = "C:\\Users\\sabatinija\\Desktop\\Devspace\\PeopleSoft\\Mastercards\\XML\\Request\\CaseFiling.retrieveDocumentation.xml";
			// CaseFiling.create
			// file =
			// "C:\\Users\\sabatinija\\Desktop\\Devspace\\PeopleSoft\\Mastercards\\XML\\XSD\\xml\\CreateCaseFiling.xml";
			// Codice comune pre try

			// CaseFiling.Update.xml
			file = "src/main/resources/xml/CaseFiling.status.xml";
			String xml = "", xmlObjectRequest = "", jsonObjectRequest = "", serviceName = "";
			XmlUtils xmlUtils = null;
			JsonUtils jsonUtils = null;
			RequestMap requestMap = null;
			XStream xstream = new XStream();
			XStream.setupDefaultSecurity(xstream);
			TransactionLogConfig transactionLogConfig = null;
			StubManager stubManager = new StubManager();
			String response = "", clazz = "", method = "", fullMethodName = "";
			try {
				// TODO
				// Da togliere variabile xml già presente è: IBRequest request
				xml = new String(Files.readAllBytes(Paths.get(file)));

				xmlUtils = new XmlUtils(xml);
				serviceName = xmlUtils.getTagMethod();
				transactionLogConfig = new TransactionLogConfig(serviceName);
				log.debug("risorsa:" + serviceName);
				xmlObjectRequest = xmlUtils.createXmlRestObjectRequest();

				// keepStrings = true, i valori non vengono forzati a numerici/booleani ma
				// rimango
				// stringhe
				jsonUtils = new JsonUtils(xmlObjectRequest, true);

				jsonObjectRequest = jsonUtils.getJson();
				log.debug("1 - jsonObjectRequest: " + jsonObjectRequest);
				jsonObjectRequest = jsonUtils.createRestJson(jsonObjectRequest, xmlUtils.getHeadName());
				log.debug("2 - jsonObjectRequest: " + jsonObjectRequest);
				log.debug("rest: " + jsonObjectRequest);
				connector.setServiceConfiguration(new ServiceConfiguration());
				
				requestMap = new RequestMap(jsonObjectRequest);

				// elementi passati allo STUB utili alla selezione della risorsa corretta.
				clazz = xmlUtils.getClasse();
				method = xmlUtils.getMethod();
				fullMethodName = xmlUtils.getTagMethod();
				response = stubManager.send(requestMap, clazz, method, fullMethodName);
//				if (response == null || "".equals(response)) {
//					log.fatal("Errore interno non gestito");
//					throw new Exception("Errore interno non gestito");
//				}
				log.info("response: " + response);
			} catch (Exception e) {
				String responseString = HelperException.getMessageError(e.getClass().getSimpleName(), serviceName,
						e.getMessage());
				log.error(responseString);
			} finally {
				if (transactionLogConfig != null) {
					transactionLogConfig.requestDestroyed();
				}
			}
		}
	}
		

	public void test() throws Exception {
		// CaseFillingStatusReq caseFilling = new CaseFillingStatusReq();
		// caseFilling.send();
		MDRConnector c = new MDRConnector();
		CaseFilingStatusBKP caseF = c.new CaseFilingStatusBKP();
		caseF.test();
	}

	public static void out(Map<String, Object> map, String key) {
		System.out.println(key + "--->" + map.get(key));
	}

}
