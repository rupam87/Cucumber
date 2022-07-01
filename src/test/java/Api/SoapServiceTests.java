package Api;

import io.restassured.config.RestAssuredConfig;
import io.restassured.config.XmlConfig;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.Node;
import io.restassured.path.xml.element.NodeChildren;
import io.restassured.response.Response;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.RestAssured;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.config.XmlConfig.xmlConfig;

public class SoapServiceTests {

    String baseUrl = "http://webservices.oorsprong.org";
    Map<String, String> requestHeaders = new HashMap<>();

    @BeforeTest
    public void setup() {
        baseURI = baseUrl;
        requestHeaders.put("Content-Type", "text/xml; charset=utf-8");
    }

    @Test
    public void getFullCountryInfo() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        namespaces.put("m", "http://www.oorsprong.org/websamples.countryinfo");

        Map<String, String> authhdrs = new HashMap<String, String>();
        authhdrs.put("SOAPAction", "FullCountryInfoAllCountries");

        String bodyXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "    <FullCountryInfoAllCountries xmlns=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                "    </FullCountryInfoAllCountries>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
        Response soapResp = given()
                .config(RestAssuredConfig.config()
                        .xmlConfig(XmlConfig.xmlConfig()
                                .namespaceAware(true)
                                .declareNamespaces(namespaces)))
                .headers(authhdrs)
                .contentType("application/soap+xml; charset=UTF-8;")
                .body(bodyXml)
                .when()
                .post("/websamples.countryinfo/CountryInfoService.wso")
                .then()
                .statusCode(200)
                .extract().response();

        XmlPath xpath = soapResp.getBody().xmlPath();
        List<Node> countryInfoList = xpath.getList("Envelope.Body.FullCountryInfoAllCountriesResponse.FullCountryInfoAllCountriesResult.tCountryInfo", Node.class);

        Map<String, String> langCodes = new HashMap();
        for (Node map : countryInfoList) {
            if (StringUtils.equalsIgnoreCase("in", map.children().get("sISOCode").toString())) {
                System.out.println("Capital City: " + map.children().get("sCapitalCity").toString());
            }

            map.children().getNode("Languages").getNodes("tLanguage").forEach(lang -> {
                langCodes.put(lang.children().get("sISOCode").toString(), lang.children().get("sName").toString());
            });
        }

        langCodes.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " :: " + entry.getValue());
        });

    }
}
