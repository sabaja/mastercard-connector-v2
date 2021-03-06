package com.mastercom.ps.connector.utils;

public enum MDR {
	ERROR("<?xmlversion=\"1.0\"?><Body><Fault><faultcode>%s</faultcode><errorType>%s</errorType><method>%s</method><detail><Message>%s</Message></detail></Fault></Body>");
	
	private final String msg;
	
	private MDR(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
