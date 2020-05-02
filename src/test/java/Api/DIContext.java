package Api;

import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import io.restassured.response.Response;

public class DIContext {
	private String username;
	private String password;
	private HashMap<String, String> creds = null;
	private String valueToStore;
	private JSONObject jobj;
	private String xmlRequest;
	CloseableHttpResponse soapResponse;
	ExtentTest test = null;
	Response response = null;

	public DIContext() {
		username = "abc";
		password = "tom";
	}

	public HashMap<String, String> GetCreds() {
		// set creds only if it has not been set before within the same context
		if (creds == null) {
			// API call to fetch Cookie/authentication code
			creds = new HashMap<String, String>();
			creds.put(username, password);
		}
		return creds;
	}

	public Response GetResponse() {
		return response;
	}

	public void SetResponse(Response toset) {
		response = toset;
	}

	public void SetValueToStore(String val) {
		valueToStore = val;
	}

	public String GetValueStored() {
		return this.valueToStore;
	}

	public void SetJsonObject(JSONObject obj) {
		this.jobj = obj;
	}

	public JSONObject GetJsonObject() {
		return this.jobj;
	}

	public void SetXmlRequest(String xml) {
		this.xmlRequest = xml;
	}

	public String GetXmlRequest() {
		return this.xmlRequest;
	}

	public void SetSoapResponse(CloseableHttpResponse response) {
		this.soapResponse = response;
	}

	public CloseableHttpResponse GetSoapResponse() {
		return this.soapResponse;
	}

	public ExtentTest GetExtentTest(ExtentReports report, String testName) {
		if (this.test == null) {
			System.out.println("Extent Test is NULL, initializing!");
			this.test = report.createTest(testName);
		}
		return this.test;
	}
	
	public ExtentTest GetExtentTest() {		
		return this.test;
	}
}
