package Api;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;


public class GoRestApiTests {

    String bearerToken = "Bearer 75d58bf991b7ad2730b72881d7de268d410101b9bbab38ee963fe6abd98f6108";
    String baseUrl = "https://gorest.co.in/public/";
    Map<String, String> requestHeaders = new HashMap<>();

    String useridCreated;

    @BeforeTest
    public void setup() {
        RestAssured.baseURI = baseUrl;
        requestHeaders.put("Authorization", bearerToken);
        requestHeaders.put("Content-type", "application/json");
    }

    @Test
    public Response getUsers() {
        Response response = given().headers(requestHeaders)
                .when()
                .get("v2/users")
                .then()
                .statusCode(200).extract().response();
        //System.out.println(response.body().prettyPrint());
        List<HashMap> respList = response.getBody().jsonPath().getList("");
        respList.forEach(node -> {
            System.out.println("Id : " + node.get("id").toString());
        });

        return response;
    }

    @Test
    public void postUser() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("email", java.util.UUID.randomUUID().toString() + "@ww.com");
        requestParams.put("name", java.util.UUID.randomUUID().toString());
        requestParams.put("gender", "female");
        requestParams.put("status", "inactive");

        System.out.println(requestParams.toString());
        Response response = given()
                .headers(requestHeaders)
                .body(requestParams.toString())
                .when()
                .post("v2/users").then()
                .statusCode(201).extract().response();

        useridCreated = response.body().jsonPath().getString("id");

        // Call Get Users to validate the content created above
        Response getUsersResp = getUsers();
        List<HashMap> respAsMap = getUsersResp.getBody().jsonPath().getList("");
        Assert.assertTrue(respAsMap.stream().filter(f -> f.get("id").toString().equals(useridCreated)).count() == 1);

    }

}
