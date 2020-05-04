package stepDefs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import org.apache.commons.text.*;

import Api.Helpers.*;
import Utils.DIContext;
import io.cucumber.java8.En;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.assertj.core.api.Assertions.*;

public class RestAPISteps implements En {
	DIContext scenarioContext;
	private HashMap<String, String> creds = null;
	JSONArray universalJArray;
	JSONObject universalJObject;

	public RestAPISteps(DIContext context) {
		this.scenarioContext = context;

		Given("I store fake creds in context", () -> {
			try {
				// Fetch creds
				creds = scenarioContext.GetCreds();
				Iterator<String> keys = creds.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					System.out.println("Got Creds :" + key + " with password: " + creds.get(key));
				}
				this.scenarioContext.GetExtentTest().pass("Inside 'I store fake creds in context'");
			} catch (Exception e) {
				this.scenarioContext.GetExtentTest().fail("Inside 'I store fake creds in context'");
			}
		});

		Given("I transform an array of POJO to JSON file", () -> {
			try {
				// Create a String array to mimic inputs for POJO class object
				List<List<String>> inputList = new ArrayList<List<String>>();
				List<PojoClass> pojoList = new ArrayList<PojoClass>();
				for (int i = 0; i < 10; i++) {
					List<String> inputRow = new ArrayList<String>();
					inputRow.add("name" + String.valueOf(i));
					inputRow.add("Age" + String.valueOf(i + 50));
					inputList.add(inputRow);
				}
				System.out.println("Created String List to mimic input : " + inputList.toArray().toString());

				// Populate POJO list of objects
				inputList.stream().forEach(ir -> {
					PojoClass obj = new PojoClass();
					obj.setName(ir.get(0));
					obj.setAge(ir.get(1));
					pojoList.add(obj);
				});
				System.out.println("Created List of POJO class objects corresponding to the inputs");

				// Use Gson to create a JSON array object containing all the
				// items
				// in inputList
				Gson gson = new Gson();
				List<String> JsonArrayString = new ArrayList<String>();
				pojoList.stream().forEach(pojo -> {
					JsonArrayString.add(gson.toJson(pojo));
				});
				System.out.println("Constructed List of JSON strings using GSON from POJO objects : ");
				JsonArrayString.forEach(string -> System.out.println(string));

				// Use JSON Object to catch the values
				JSONObject jObj = new JSONObject();
				jObj.put("data", JsonArrayString);
				System.out.println("Constructed org.JSON object from the list of JSON strings : " + jObj.toString());

				// Create Object Mapper from Jackson to
				// ObjectMapper om = new ObjectMapper();
				File file = new File(System.getProperty("user.dir") + "\\src\\test\\java\\Api\\Templates\\Output.json");
				FileWriter outputJson = new FileWriter(file);
				outputJson.write(StringEscapeUtils.unescapeJava(jObj.toString()));
				outputJson.close();
				// om.writeValue(outputJson, jObj);
				System.out.println("Created JSON file to store output from GSON at : " + file.getCanonicalPath());
				this.scenarioContext.GetExtentTest().pass("Inside 'I transform an array of POJO to JSON file'");
			} catch (Exception e) {
				this.scenarioContext.GetExtentTest().fail("Inside 'I transform an array of POJO to JSON file'");
			}

		});

		Given("I read the file {string} and modify {string}, {string} values",
				(String fileName, String name, String job) -> {
					try {
					String curDir = System.getProperty("user.dir");
					File file = new File(curDir + "\\src\\test\\java\\Api\\Templates\\" + fileName);
					String fileContents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

					JSONObject jobj = new JSONObject(fileContents);
					jobj.put("name", name);
					jobj.put("job", job);

					this.scenarioContext.SetJsonObject(jobj);
					this.scenarioContext.GetExtentTest()
							.pass("'I read the file {string} and modify {string}, {string} values'");
					} catch (Exception e) {
						this.scenarioContext.GetExtentTest().fail("I read the file {string} and modify {string}, {string} values'");
					}
				});

		Given("I invoke REST call {string} on {string}", (String method, String url) -> {
			Response response = null;
			switch (method.toLowerCase()) {
			case "get":
				response = RestAssured.get(url);
				break;
			case "post":
				String body = this.scenarioContext.GetJsonObject().toString();
				response = RestAssured.given().contentType(ContentType.JSON).body(body).post(url);
				break;
			default:
				break;
			}
			// Store the response and the response's body
			scenarioContext.SetResponse(response);
			this.scenarioContext.SetJsonObject(new JSONObject(response.body().asString()));
			this.scenarioContext.GetExtentTest().info("inside 'I invoke REST call {string} on {string}'");
		});

		When("I read and parse the Json file {string}", (String fileName) -> {

			String currentDirectory = System.getProperty("user.dir");
			File file = new File(currentDirectory + "\\src\\test\\java\\Api\\Templates\\" + fileName);
			String contents;
			try {
				// JSON file to String
				contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

				// Read JSON file as JSONObject
				JSONObject obj = new JSONObject(contents);
				System.out.println("Printing Pretty format:" + obj.toString(4));

				// Get FlightLegs Array from the object
				JSONArray flightLegs = obj.getJSONArray("flightLegs");
				System.out.println("Printing Pretty format:" + flightLegs.toString(4));

				// Get operationalFlightSegments Array
				JSONArray operationalFlightSegments = flightLegs.getJSONObject(0)
						.getJSONArray("operationalFlightSegments");
				System.out.println("Printing Pretty format:" + operationalFlightSegments.toString(4));

				// Get FLight Status Array
				JSONArray flightStatuses = operationalFlightSegments.getJSONObject(0).getJSONArray("flightStatuses");
				System.out.println("Printing Pretty format:" + flightStatuses.toString(4));
				
				// Use Stream to fetch the Status Type for the node whose code =
				// ERL
				List<JSONObject> flightStatus = StreamSupport.stream(flightStatuses.spliterator(), false)
						.map(val -> (JSONObject) val).filter(val -> val.getString("code").equals("ERL"))
						.collect(Collectors.toList());
				System.out.println("statusType for code 'ERL' :" + flightStatus.get(0).getString("statusType"));
				System.out.println("Flight Status for code 'ERL' :" + flightStatus.get(0).toString(4));

				// Get Airport details
				JSONObject departureAirport = operationalFlightSegments.getJSONObject(0)
						.getJSONObject("departureAirport");
				System.out.println("Departure Airport Code: " + departureAirport.getString("iataCode"));
				this.scenarioContext.GetExtentTest().pass("inside'I read and parse the Json file {string}'");

			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		When("I read the Json file {string}, modify it with values",
				(String fileName, io.cucumber.datatable.DataTable dataTable) -> {

					String currentDirectory = System.getProperty("user.dir");
					File file = new File(currentDirectory + "\\src\\test\\java\\Api\\Templates\\" + fileName);

					// JSON file to String
					String contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

					List<List<String>> rows = dataTable.asLists(String.class);
					for (List<String> row : rows) {
						contents = contents.replace("{" + row.get(0) + "}", row.get(1));
					}
					System.out.println("Input JSON after string replacement :" + new JSONObject(contents).toString(4));
					this.scenarioContext.GetExtentTest()
							.pass("inside 'I read the Json file {string}, modify it with values'");
				});

		When("I invoke REST call {string} on the url", (String method) -> {
			Response response = null;
			switch (method.toLowerCase()) {
			case "get":
				response = RestAssured.get(scenarioContext.GetValueStored());
				break;
			case "post":
				response = RestAssured.post(scenarioContext.GetValueStored());
				break;
			default:
				break;
			}
			scenarioContext.SetResponse(response);
			this.scenarioContext.SetJsonObject(new JSONObject(response.body().asString()));
			this.scenarioContext.GetExtentTest().pass("I invoke REST call {string} on the url");
		});

		Then("I print response content", () -> {
			JSONArray jArr = new JSONObject(scenarioContext.GetResponse().asString()).getJSONArray("data");
			StreamSupport.stream(jArr.spliterator(), false).map(m -> (JSONObject) m)
					.filter(f -> f.getInt("employee_age") == 22).collect(Collectors.toList()).forEach(empl -> {
						System.out.println("Employees with age 22:" + empl.toString());
					});
			this.scenarioContext.GetExtentTest().pass("I print response content");
		});

		Then("I filter all names starting with {string} from the response to print them", (String inputName) -> {
			String contents = scenarioContext.GetResponse().getBody().asString();
			JSONObject jobj = new JSONObject(contents);
			JSONArray results = jobj.getJSONArray("results");
			System.out.println("Printing Pretty format:" + results.toString(4));
			List<JSONObject> filteredNames = StreamSupport.stream(results.spliterator(), false).map(r -> (JSONObject) r)
					.filter(n -> n.getString("name").startsWith(inputName)).collect(Collectors.toList());

			filteredNames.forEach(fName -> System.out.println(fName.toString()));
			this.scenarioContext.GetExtentTest().pass("I filter all names starting with {string} from the response to print them");
		});

		Then("I store the url for name={string} in scenario context", (String name) -> {
			String contents = scenarioContext.GetResponse().getBody().asString();
			JSONObject jobj = new JSONObject(contents);
			JSONArray results = jobj.getJSONArray("results");

			List<JSONObject> filteredName = StreamSupport.stream(results.spliterator(), false).map(r -> (JSONObject) r)
					.filter(u -> u.getString("name").toLowerCase().equals(name.toLowerCase()))
					.collect(Collectors.toList());
			scenarioContext.SetValueToStore(filteredName.get(0).getString("url"));
			System.out.println("Url of the name :" + name + " saved as :" + filteredName.get(0).getString("url"));
			this.scenarioContext.GetExtentTest().pass("I store the url for name={string} in scenario context");
		});

		Then("I verify ResponseCode={string}", (String expCode) -> {

			assertThat(Integer.parseInt(expCode)).isEqualTo(this.scenarioContext.GetResponse().statusCode());
			this.scenarioContext.GetExtentTest().pass("I verify ResponseCode={string}");
		});

		Then("I verify {string}={string}", (String path, String value) -> {
			String actual_val = new JsonHelper(scenarioContext).ParseJsonFromResponseToGetValue(path, null);
			assertThat(actual_val).isEqualTo(value);
			this.scenarioContext.GetExtentTest().pass("I verify {string}={string}");
		});

		Then("I verify in response {string}={string}", (String path, String value) -> {
			String actual_val = new JsonHelper(scenarioContext).ParseJsonFromResponseToGetValue(path,
					this.scenarioContext.GetJsonObject());
			assertThat(actual_val).isEqualTo(value);
			this.scenarioContext.GetExtentTest().pass("I verify in response {string}={string}");
		});

		Then("I filter {string}={int} to store {string} in scenario context",
				(String path, Integer filterVal, String pathToStore) -> {
					String actual_val = new JsonHelper(scenarioContext).ParseJsonFromResponseToFilterValue(path,
							filterVal, pathToStore);
					scenarioContext.SetValueToStore(actual_val);
					System.out.println("Value Stored :" + scenarioContext.GetValueStored());
					this.scenarioContext.GetExtentTest().pass("I filter {string}={int} to store {string} in scenario context");
				});
	}
}
