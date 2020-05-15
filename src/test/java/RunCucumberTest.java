
import static org.testng.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import Utils.ExtentManager;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberFeatureWrapper;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.PickleEventWrapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
		"json:test-output/jsonReports/CucumberTestReport.json" }, features = "src/test/resources", glue = {
				"stepDefs" }, tags = { "@UI" })
public class RunCucumberTest extends AbstractTestNGCucumberTests {

	ExtentReports eReport = null;

	@BeforeSuite
	public void CreateExtentReports() throws IOException, InterruptedException, URISyntaxException {

		// Execute DockerUp.bat to bring up containers
		// Runtime.getRuntime().exec(System.getProperty("user.dir") +
		// "//dockerUp.bat");
		Runtime.getRuntime().exec("cmd.exe /c start dockerUp.bat", null, new File(System.getProperty("user.dir")));
		System.out.println("Executed dockerUp.bat");

		// Check if output file exists or not
		File oFile = new File(System.getProperty("user.dir") + "//output.txt");
		int counter = 0;
		while (!oFile.exists() && counter++ < 10) {
			System.out.println("Waiting for output.txt");
			if (counter == 10) {
				System.out.println("output.txt did not get generated. Aborting!");
				Assert.fail();
			} else
				Thread.sleep(3000);
		}
		// Wait for Docker Hub and Nodes to be ready. i.e.
		// read the output file and get Docker default machine's public IP
		boolean match = false;
		counter = 0;
		String dockerPublicIP = "";

		while (match != true && counter < 30) {
			try (BufferedReader bufReader = new BufferedReader(
					new FileReader(System.getProperty("user.dir") + "//output.txt"))) {
				System.out.println("Waiting to find an IP match in output.txt");
				dockerPublicIP = bufReader.readLine();
				match = dockerPublicIP.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
				counter++;
				System.out.println(
						"Match Not Found, got back: " + dockerPublicIP + " .Waiting for 2 sec. Attempt :: " + counter);
				Thread.sleep(2000);
			}
		}
		assertTrue(match);
		System.out.println("Match Found for IP address in output.txt!!");

		// Reset counters and match to wait for another text
		match = false;
		counter = 0;
		while (match != true && counter++ < 60) {
			StringBuilder buffer = new StringBuilder();
			try (BufferedReader bufReader = new BufferedReader(
					new FileReader(System.getProperty("user.dir") + "//output.txt"))) {
				System.out.println("Waiting for Hub and node containers to be up");
				String line = null;
				while ((line = bufReader.readLine()) != null) {
					buffer.append(line);
				}
				match = buffer.toString().contains("Registered a node");
				System.out.println("Match Not Found, Waiting for 2 sec. Attempt :: " + counter);
				if (counter == 60) {
					System.out.println("Contents of Output File: ");
					System.out.println(buffer);
				} else
					Thread.sleep(2000);
			}
		}
		assertTrue(match);
		System.out.println("Selenium Grid is up and Running!!");

		// Reset counters and match to wait for another text
		match = false;
		counter = 0;
		// Execute Docker scale to increase Nodes
		// MUST execute this is new cmd window, as executing dockerSacle on the
		// same window where dockerUp
		// is executing does not take effect
		Runtime.getRuntime().exec("cmd.exe /c start dockerScale.bat", null, new File(System.getProperty("user.dir")));
		System.out.println("Executed dockerScale.bat");
		// Wait for Node Counts to match expected count
		// Call the API - http://<dockerPublicIP>:4444/grid/api/hub to parser
		// JSON response
		while (match != true && counter++ < 30) {
			Response response = RestAssured.given().get(new URI("http://" + dockerPublicIP + ":4444/grid/api/hub"));
			JSONObject jObj = new JSONObject(response.getBody().asString());
			int slots = jObj.getJSONObject("slotCounts").getInt("total");
			// Verify Slots matched the expected count (user should know exp
			// count from dockerScale.bat file)
			if (counter == 30) {
				System.out.println("response :: " + response.getBody().asString());
				assertEquals(slots, 4);
			} else {
				match = (slots == 4) ? true : match;
				System.out.println(
						"Match Not Found, got back Slots = " + slots + " .Waiting for 2 sec. Attempt :: " + counter);
				Thread.sleep(2000);
			}
		}

		// Create Extent Report Instance to be used by tests
		eReport = ExtentManager.ExtentReportsInstance();
	}

	@Override
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		super.setUpClass();
	}

	@Override
	@Test(groups = "", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void runScenario(PickleEventWrapper pickleWrapper, CucumberFeatureWrapper featureWrapper) throws Throwable {
		super.runScenario(pickleWrapper, featureWrapper);
	}

	@Override
	@DataProvider(parallel = true)
	public Object[][] scenarios() {
		return super.scenarios();
	}

	@Override
	@AfterClass(alwaysRun = true)
	public void tearDownClass() {
		super.tearDownClass();
	}

	@AfterSuite(alwaysRun = true)
	public void FlushExtentReports() throws IOException, InterruptedException {
		// Flush the extent report first
		if (eReport != null)
			eReport.flush();

		// Execute dockerDown bat to shut down all containers
		Runtime runTime = Runtime.getRuntime();
		runTime.exec("cmd.exe /c start dockerDown.bat", null, new File(System.getProperty("user.dir")));
		Thread.sleep(5000);
		System.out.println("Executed dockerDown.bat");

		// Delete output file if it exists
		File f = new File(System.getProperty("user.dir") + "//output.txt");
		int counter = 0;
		while (!f.delete() && counter++ < 10) {
			if (f.delete()) {
				System.out.println("Deleted output file");
				return;
			} else {
				if (counter == 10) {
					System.out.println("Could not delete output file. Retry Attempt exhausted!");
					return;
				} else {
					System.out.println("Could not delete output file. Retrying after 1 sec. Attempt :: " + counter);
					Thread.sleep(1000);
				}
			}
		}

		System.out.println("Deleted output file");
	}

}