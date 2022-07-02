package Utils;

import java.util.HashMap;

import io.github.sridharbandi.AxeRunner;
import io.github.sridharbandi.HtmlCsRunner;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import cucumber.api.PickleStepTestStep;
import io.restassured.response.Response;

public class DIContext {
	private String username;
	private String password;
	private HashMap<String, String> creds = null;
	private String valueToStore;
	private JSONObject jobj;
	private String xmlRequest;
	CloseableHttpResponse soapResponse;
	private ExtentTest test = null;
	private Response response = null;
	private WebDriver driver = null;
	private int stepErrorCount = 0;
	private Exception stepError = null;
	private HtmlCsRunner htmlCsRunner = null;
	private AxeRunner axeRunner = null;

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

	public HtmlCsRunner getHtmlCsRunner(){
		return htmlCsRunner;
	}

	public void setHtmlCsRunner(HtmlCsRunner runner){
		htmlCsRunner = runner;
	}

	public AxeRunner getAxeRunner(){
		return axeRunner;
	}

	public void setAxeRunner(AxeRunner runner){
		axeRunner = runner;
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

	public void SetWebDriver(WebDriver driver) {
		this.driver = driver;
	}

	public WebDriver GetWebDriver() {
		return this.driver;
	}

	public void IncrementStepErrorCount(Exception e) {
		this.stepErrorCount += 1;
		this.stepError = e;
	}

	public int getStepErrorCount() {
		return this.stepErrorCount;
	}

	public Exception getStepError() {
		return this.stepError;
	}
}
