package stepDefs;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.cucumber.java8.En;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.*;

import Api.Helpers.XmlHelper;
import Utils.DIContext;

import static org.assertj.core.api.Assertions.*;

public class SoapAPISteps implements En {
	DIContext scenarioContext;
	XmlHelper xhelper;

	public SoapAPISteps(DIContext context) {

		this.scenarioContext = context;
		

		Given("I read the Xml file {string}, modify {string} and {string}",
				(String fileName, String itmAVal, String itmBVal) -> {

					File xmlFile = new File(
							System.getProperty("user.dir") + "//src//test//java//Api//Templates//" + fileName);
					xhelper = new XmlHelper();
					Document xdoc = xhelper.CreateXDocument(xmlFile);
					xhelper.SetNodeValue(xdoc, "/ns:soapenv:Envelope/ns:soapenv:Body/ns:tem:Add/ns:tem:intA/text()",
							itmAVal);
					xhelper.SetNodeValue(xdoc, "/ns:soapenv:Envelope/ns:soapenv:Body/ns:tem:Add/ns:tem:intB/text()",
							itmBVal);
					String xmlContent = xhelper.ConvertToString(xdoc);
					this.scenarioContext.SetXmlRequest(xmlContent);
					xhelper.dispose();
					this.scenarioContext.GetExtentTest().pass("I read the Xml file {string}, modify {string} and {string}");
				});

		Given("I read the Xml file {string}", (String fileName) -> {
			File xmlFile = new File(System.getProperty("user.dir") + "//src//test//java//Api//Templates//" + fileName);
			xhelper = new XmlHelper();
			xhelper.CreateXDocument(xmlFile);
			this.scenarioContext.GetExtentTest().pass("I read the Xml file {string}, modify {string} and {string}");
		});

		Given("I select for nodes with filterxpath = {string} to verify {string} are present",
				(String queryXpath, String values) -> {

					List<String> valuesToVerify = Arrays.stream(values.split(",")).collect(Collectors.toList());
					List<String> valuesReceived = xhelper.GetNodeValues(xhelper.GetXDocument(), queryXpath);
					assertThat(valuesReceived).containsAll(valuesToVerify);
					this.scenarioContext.GetExtentTest().pass("I select for nodes with filterxpath = {string} to verify {string} are present");
				});

		Given("I invoke SOAP call {string} on {string}", (String method, String url) -> {

			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(new URI(url));
			request.addHeader("Content-Type", "text/xml");
			request.setEntity(new StringEntity(this.scenarioContext.GetXmlRequest()));
			CloseableHttpResponse response = client.execute(request);
			this.scenarioContext.SetSoapResponse(response);
			this.scenarioContext.GetExtentTest().pass("I invoke SOAP call {string} on {string}");
		});

		Then("I verify Soap ResponseCode={int}", (Integer respCode) -> {

			int code = this.scenarioContext.GetSoapResponse().getStatusLine().getStatusCode();
			assertThat(respCode).isEqualTo(code);
			this.scenarioContext.GetExtentTest().pass("I verify Soap ResponseCode={int}");
		});

		Then("I verify in Soap response {string}={string}", (String field, String value) -> {

			String xmlFromResponse = EntityUtils.toString(this.scenarioContext.GetSoapResponse().getEntity());
			xhelper = new XmlHelper();
			Document xRespDoc = xhelper.CreateXDocument(xmlFromResponse);
			String actualVal = xhelper.GetNodeValues(xRespDoc,
					"/ns:soap:Envelope/ns:soap:Body/ns:tem:AddResponse/ns:tem:" + field + "/text()").get(0);
			assertThat(actualVal).isEqualTo(value);
			this.scenarioContext.GetExtentTest().pass("I verify in Soap response {string}={string}");
			xhelper.dispose();
		});
	}

}
