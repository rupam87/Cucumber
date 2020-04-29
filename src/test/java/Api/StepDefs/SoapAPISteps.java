package Api.StepDefs;

import java.io.File;
import java.net.URI;

import io.cucumber.java8.En;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.*;

import Api.DIContext;
import Api.Helpers.XmlHelper;

import static org.assertj.core.api.Assertions.*;

public class SoapAPISteps implements En {
	DIContext scenarioContext;

	public SoapAPISteps(DIContext context) {

		this.scenarioContext = context;

		Given("I read the Xml file {string}, modify {string} and {string}",
				(String fileName, String itmAVal, String itmBVal) -> {

					File xmlFile = new File(
							System.getProperty("user.dir") + "//src//test//java//Api//Templates//" + fileName);
					XmlHelper xhelper = new XmlHelper();
					Document xdoc = xhelper.GetXDocument(xmlFile);
					xhelper.SetNodeValue(xdoc, "/ns:soapenv:Envelope/ns:soapenv:Body/ns:tem:Add/ns:tem:intA/text()",
							itmAVal);
					xhelper.SetNodeValue(xdoc, "/ns:soapenv:Envelope/ns:soapenv:Body/ns:tem:Add/ns:tem:intB/text()",
							itmBVal);
					String xmlContent = xhelper.ConvertToString(xdoc);
					this.scenarioContext.SetXmlRequest(xmlContent);
					xhelper.dispose();
				});

		Given("I invoke SOAP call {string} on {string}", (String method, String url) -> {

			// URI uri = new URIBuilder().setPath(url).build();
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(new URI(url));
			request.addHeader("Content-Type", "text/xml");
			request.setEntity(new StringEntity(this.scenarioContext.GetXmlRequest()));
			CloseableHttpResponse response = client.execute(request);
			this.scenarioContext.SetSoapResponse(response);
		});

		Then("I verify Soap ResponseCode={int}", (Integer respCode) -> {

			int code = this.scenarioContext.GetSoapResponse().getStatusLine().getStatusCode();
			assertThat(respCode).isEqualTo(code);
		});

		Then("I verify in Soap response {string}={string}", (String field, String value) -> {

			String xmlFromResponse = EntityUtils.toString(this.scenarioContext.GetSoapResponse().getEntity());
			XmlHelper xhelper = new XmlHelper();
			Document xRespDoc = xhelper.GetXDocument(xmlFromResponse);
			String actualVal = xhelper.GetNodeValue(xRespDoc,
					"/ns:soap:Envelope/ns:soap:Body/ns:tem:AddResponse/ns:tem:" + field + "/text()");
			assertThat(actualVal).isEqualTo(value);
			xhelper.dispose();
		});
	}

}
