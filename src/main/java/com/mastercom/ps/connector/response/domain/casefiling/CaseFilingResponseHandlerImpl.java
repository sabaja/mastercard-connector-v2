package com.mastercom.ps.connector.response.domain.casefiling;

import java.io.ByteArrayOutputStream;

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

	private final Logger log = Logger.getLogger(CaseFilingResponseHandlerImpl.class);

	@Override
	public String getCreateResponse(CaseFiling resource, String fullMethodName) throws Exception {
		log.debug("Marshalling dell'oggetto [" + fullMethodName + "]");
		ObjectFactory factory = new ObjectFactory();
		CaseFilingData caseFiling = factory.createCaseFilingResponseData();
		/*
		 * (response, "caseId"); //-->536092
		 */
		caseFiling.setMethod(fullMethodName);
		CaseID caseId = factory.createCaseID();
		caseId.setCaseId((String) resource.get("caseId"));
		caseFiling.setCaseID(caseId);
		String response = this.createResponse(caseFiling, factory);
		log.debug("RESPONSE : " + response);
		return response;
	}

	@Override
	public String getRetrieveDocumentationResponse(CaseFiling resource, String fullMethodName) throws Exception {
		log.debug("Marshalling dell'oggetto [" + fullMethodName + "]");
		ObjectFactory factory = new ObjectFactory();
		CaseFilingData caseFiling = factory.createCaseFilingResponseData();
		/*
		 * (response, "fileAttachment.filename"); //-->CS_536092.zip out(response,
		 * "fileAttachment.file");
		 */
		caseFiling.setMethod(fullMethodName);
		FileAttachment fileAttachment = factory.createFileAttachment();
		fileAttachment.setFile((String) resource.get("fileAttachment.file"));
		fileAttachment.setFilename((String) resource.get("fileAttachment.filename"));
		caseFiling.setFileAttachment(fileAttachment);

		//create Response
		String response = this.createResponse(caseFiling, factory);
		return response;
	}

	@Override
	public String getUpdateResponse(CaseFiling resource, String fullMethodName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCaseFilingStatusResponse(CaseFiling resource, String fullMethodName) throws Exception {
		// TODO Auto-generated method stub
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
	private String createResponse(CaseFilingData caseFiling, ObjectFactory factory) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(this.getThisPackage());
		JAXBElement<CaseFilingData> element = factory.createResponseData(caseFiling);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.FALSE);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		marshaller.marshal(element, out);
		String response = new String(out.toByteArray());
		return response;
	}
}
