package com.mastercom.ps.connector.response.domain.casefiling;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.mastercard.api.mastercom.CaseFiling;

/**
 * Classe che si occupa di fare il parsing delle response relative alle request
 * dellla classe {@link CaseFiling}
 * 
 * @author SabatiniJa
 *
 */
public class CaseFilingResponseHandlerImpl implements CaseFilingResponseHandler<CaseFiling> {

	private static final Logger log = Logger.getLogger(CaseFilingResponseHandlerImpl.class);

	@Override
	public String getCreateResponse(CaseFiling resource, String fullMethodName) throws Exception {
		log.trace("Marshalling dell'oggetto [" + fullMethodName + "]");
		ObjectFactory factory = new ObjectFactory();
		CaseFilingResponseData caseFiling = factory.createCaseFilingResponseData();
		/*
		 * (response, "caseId"); //-->536092
		 */
		caseFiling.setMethod(fullMethodName);
		// CaseID caseId = factory.createCaseID();
		// caseId.setCaseId((String) resource.get("caseId"));
		caseFiling.setCaseID((String) resource.get("caseId"));
		String response = this.createResponse(caseFiling, factory);
		log.trace("RESPONSE : " + response);
		return response;
	}

	@Override
	public String getRetrieveDocumentationResponse(CaseFiling resource, String fullMethodName) throws Exception {
		log.debug("Marshalling dell'oggetto [" + fullMethodName + "]");
		ObjectFactory factory = new ObjectFactory();
		CaseFilingResponseData caseFiling = factory.createCaseFilingResponseData();
		/*
		 * (response, "fileAttachment.filename"); //-->CS_536092.zip out(response,
		 * "fileAttachment.file");
		 */
		caseFiling.setMethod(fullMethodName);
		FileAttachment fileAttachment = factory.createFileAttachment();
		fileAttachment.setFile((String) resource.get("fileAttachment.file"));
		fileAttachment.setFilename((String) resource.get("fileAttachment.filename"));
		caseFiling.setFileAttachment(fileAttachment);

		// create Response
		String response = this.createResponse(caseFiling, factory);
		return response;
	}

	@Override
	public String getUpdateResponse(CaseFiling resource, String fullMethodName) throws Exception {
		log.trace("Marshalling dell'oggetto [" + fullMethodName + "]");
		ObjectFactory factory = new ObjectFactory();
		CaseFilingResponseData caseFiling = factory.createCaseFilingResponseData();
		/*
		 * (response, "caseId"); //-->536092
		 */
		caseFiling.setMethod(fullMethodName);
		caseFiling.setCaseID((String) resource.get("caseId"));
		String response = this.createResponse(caseFiling, factory);
		log.trace("RESPONSE : " + response);
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCaseFilingStatusResponse(CaseFiling resource, String fullMethodName) throws Exception {
		log.trace("Marshalling dell'oggetto [" + fullMethodName + "]");
		ObjectFactory factory = new ObjectFactory();
		CaseFilingResponseData caseFiling = factory.createCaseFilingResponseData();
		/*
		 * out(response, "caseFilingResponseList[0].caseId"); //-->536092 
		 * out(response, "caseFilingResponseList[0].status"); //-->COMPLETED
		 */
		caseFiling.setMethod(fullMethodName);
//		List<CaseFilingResponseList> caseFilingList = caseFiling.getCaseFilingResponseList(); 
		for(Map<String,Object> item : (List<Map<String, Object>>) resource.get("caseFilingResponseList")){
			log.trace("caseId-->" + item.get("caseId"));
			log.trace("status-->" + item.get("status"));
		}
		
		
		
		return null;
	}

	/**
	 * Restituisce il package della classe
	 * 
	 * @return package
	 */
	private String getThisPackage() {
		return this.getClass().getPackage().getName();
	}

	/**
	 * @throws JAXBException
	 * 
	 */
	private String createResponse(CaseFilingResponseData caseFiling, ObjectFactory factory) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(this.getThisPackage());
		JAXBElement<CaseFilingResponseData> element = factory.createResponseData(caseFiling);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.FALSE);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(element, out);
		String response = new String(out.toByteArray());
		return response;
	}
}
