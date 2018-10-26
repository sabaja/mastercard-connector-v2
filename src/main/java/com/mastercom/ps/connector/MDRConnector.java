package com.mastercom.ps.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetHeaders;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
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

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.*;

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

	// di seguito costanti che rappresentano casi di request contententi array:
	private static final String CASE_FILING_STATUS = "CaseFiling.caseFilingStatus";

	public void init(ConnectorInfo connInfo) {
		this.serviceConfiguration = new ServiceConfiguration(connInfo);
		this.xstream = new XStream();
		XStream.setupDefaultSecurity(xstream);

	}

	@Override
	public ConnectorDataCollection introspectConnector() {
		final ConnectorDataCollection conCollection = new ConnectorDataCollection();
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
			responseString = HelperException.getMessageError(e.getClass().getSimpleName(), "", e.getMessage());
			log.error("Exception: " + responseString);
		}
		final InternalIBResponse response = new InternalIBResponse();
		final InternetHeaders internetHeaders = new InternetHeaders();

		try {
			response.addContentSection(internetHeaders, responseString);
		} catch (final Exception e) {
			responseString = HelperException.getMessageError(e.getClass().getSimpleName(), serviceName, e.getMessage());
			log.error("Exception: " + e.getMessage() + " -> " + e.toString() + " STACKTRACE " + e.getStackTrace());
		} finally {
			if (transactionLogConfig != null) {
				transactionLogConfig.requestDestroyed();
			}
		}
		return response;
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
		String file = "";
		if (!CASE) {
			file = "src/main/resources/xml/CaseFiling.create.xml";
			connector.test(file);
		} else {

			file = "src/main/resources/xml/CaseFiling.status.xml";
			// TODO
			// Da qui in poi inserire nel metodo send
			String xml = "", xmlObjectRequest = "", jsonObjectRequest = "", serviceName = "";
			XmlUtils xmlUtils = null;
			JsonUtils jsonUtils = null;
			RequestMap requestMap = null;
			XStream xstream = new XStream();
			XStream.setupDefaultSecurity(xstream);
			TransactionLogConfig transactionLogConfig = null;
			StubManager stubManager = new StubManager();
			String response = "", clazz = "", method = "", fullMethodName = "";
			connector.setServiceConfiguration(new ServiceConfiguration());
			log.info("****** SETTING SERVICE CONFIGURATION *******");
			try {
				xml = new String(Files.readAllBytes(Paths.get(file)));
				xmlUtils = new XmlUtils(xml);
				String root = xmlUtils.getHeadName();
				log.trace("xml root: " + root);
				serviceName = xmlUtils.getTagMethod();
				transactionLogConfig = new TransactionLogConfig(serviceName);
				//Gestione casi speciali con array
				if (CASE_FILING_STATUS.equalsIgnoreCase(serviceName)
						&& StringUtils.countMatches(xml, "caseFilingList") == 2) {
					log.info("Dentro caso array singolo - risorsa: " + serviceName);
					String xmlTmp = xmlUtils.createXmlRestObjectRequest();
					log.trace("xml temporaneo: " + xmlTmp);
					Document doc = xmlUtils.parseStringToXML(xmlTmp);
					NodeList nodeFilingList = doc.getElementsByTagName("caseFilingList");
					Element eleFilingList = (Element) nodeFilingList.item(0);
					String eleFilingListName = eleFilingList.getNodeName();
					log.trace("trovato: " + eleFilingListName);
					Element cloneFilingList = (Element) eleFilingList.cloneNode(true);

					NodeList rootNode = doc.getElementsByTagName(root);
					Element eleFilingRoot = (Element) rootNode.item(0);
					log.trace("trovato root: " + eleFilingRoot.getNodeName());

					eleFilingRoot.appendChild(cloneFilingList);
					String xmlCloned = xmlUtils.parseXMLToString(eleFilingRoot.getOwnerDocument());
					log.trace("Xml clonato: " + xmlCloned);
					xmlObjectRequest = xmlCloned;
					jsonUtils = new JsonUtils(xmlObjectRequest, true);
					log.trace("Json temporaneo clonato " + jsonUtils.getJson());
					jsonObjectRequest = jsonUtils.normalizingJson();
				} else {
					log.debug("Casi di DEFAULT - risorsa:" + serviceName);
					xmlObjectRequest = xmlUtils.createXmlRestObjectRequest();
					jsonUtils = new JsonUtils(xmlObjectRequest, true);
					jsonObjectRequest = jsonUtils.getJson();
					//jsonObjectRequest = jsonUtils.createRestJson(jsonObjectRequest, xmlUtils.getHeadName());
				}

				log.debug("1 - jsonObjectRequest: " + jsonObjectRequest);
				
				String json = jsonUtils.createRestJson(jsonObjectRequest, xmlUtils.getHeadName());
				log.debug("2 - jsonObjectRequest: " + json);
				log.debug("rest: " + json);
				requestMap = new RequestMap(json);
				// elementi passati allo STUB utili alla selezione della risorsa corretta.
				clazz = xmlUtils.getClasse();
				method = xmlUtils.getMethod();
				fullMethodName = xmlUtils.getTagMethod();
				response = stubManager.send(requestMap, clazz, method, fullMethodName);
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

	public void test(String xml) throws Exception {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new File(xml));

		// normalize text representation
		doc.getDocumentElement().normalize();
		System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());

		NodeList nodeList = doc.getDocumentElement().getChildNodes();
		for (int k = 0; k < nodeList.getLength(); k++) {
			printTags((Node) nodeList.item(k));
		}
	}// end of main

	public void printTags(Node nodes) {
		if (nodes.hasChildNodes() || nodes.getNodeType() != 3) {
			System.out.println(nodes.getNodeName() + " : " + nodes.getTextContent());
			NodeList nl = nodes.getChildNodes();
			for (int j = 0; j < nl.getLength(); j++)
				printTags(nl.item(j));
		}
	}

	public static void out(Map<String, Object> map, String key) {
		System.out.println(key + "--->" + map.get(key));
	}

}
