
import static org.testng.Assert.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.time.StopWatch;
import org.codehaus.plexus.util.StringUtils;
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
				"StepDefinitions" }, tags = { "@UI" })
public class RunCucumberTest extends AbstractTestNGCucumberTests {

	ExtentReports eReport = null;

	@BeforeSuite
	public void CreateExtentReports() throws IOException, InterruptedException, URISyntaxException {

		// Execute DockerUp.bat to bring up containers
		String dockerPublicIp = "192.168.99.106";
		Process dockerUp = Runtime.getRuntime().exec("cmd /c start dockerUp.bat", null, new File(System.getProperty("user.dir")));
		Thread.sleep(60000);
		System.out.println("Executed DockerUp bat");
		/*
		 * try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
		 * System.out.println("Buffered Writer instantiated"); try (BufferedReader br =
		 * new BufferedReader(new InputStreamReader(dockerUp.getInputStream()))) {
		 * System.out.println("Buffered Reader instantiated"); String line; while ((line
		 * = br.readLine()) != null) { bw.write(line + "\n"); System.out.println(line +
		 * "\n"); boolean match = StringUtils.isNotBlank(line) ?
		 * line.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+") : false; if (match) {
		 * dockerPublicIp = line; System.out.println("!!Match Found for IP address!!");
		 * } if (line.toLowerCase().contains("registered a node")) {
		 * System.out.println("!!Selenium Grid is up and Running!!"); break; } } }
		 * bw.flush();
		 * System.out.println("Buffered Writer flushed to underlying file : output.txt"
		 * ); }
		 */

		/*
		 * boolean match = false; int counter = 0; // Execute Docker scale to increase
		 * Nodes Process dockerScale = Runtime.getRuntime().exec("dockerScale.bat",
		 * null, new File(System.getProperty("user.dir")));
		 * dockerScale.waitFor(Long.parseLong("30"),
		 * java.util.concurrent.TimeUnit.SECONDS);
		 * System.out.println("dockerScale.bat executed");
		 * 
		 * // Wait for Node Counts to match expected count // Call the API -
		 * http://<dockerPublicIP>:4444/grid/api/hub while (match != true && counter++ <
		 * 30) {
			boolean match = false; int counter = 0; 
			while (match != true && counter++ <30)
			{
				Response response = RestAssured.given().get(new URI("http://" + dockerPublicIp + ":4444/grid/api/hub"));
				JSONObject jObj = new JSONObject(response.getBody().asString());
				int slots = jObj.getJSONObject("slotCounts").getInt("total");
				// Verify Slots matched the expected count
				if (counter == 30) {
					System.out.println("response :: " + response.getBody().asString());
					assertEquals(slots, 2);
				} else {
					match = (slots == 2) ? true : false;
					System.out.println(
							"Match Not Found, got back Slots = " + slots + " .Waiting for 2 sec. Attempt :: " + counter);
					Thread.sleep(2000);
				}
		    }
		 */

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
		Process p = Runtime.getRuntime().exec("cmd /c start dockerDown.bat", null, new File(System.getProperty("user.dir")));
		Thread.sleep(20000);
		System.out.println("Executed Dockerdown bat");
		/*
		 * p.waitFor(Long.parseLong("5"), TimeUnit.SECONDS); try (BufferedReader br =
		 * new BufferedReader(new InputStreamReader(p.getInputStream()))) { String line
		 * = ""; while ((line = br.readLine()) != null) { System.out.println(line +
		 * "\n"); } } System.out.println("dockerDown.bat executed"); // Kill cmd //
		 * Runtime.getRuntime().exec("taskkill /f /im cmd.exe"); //
		 * System.out.println("Closed cmd");
		 * 
		 * // Delete output file if it exists File f = new
		 * File(System.getProperty("user.dir") + "//output.txt"); int counter = 0; while
		 * (!f.delete() && counter++ < 10) { if (f.delete()) {
		 * System.out.println("Deleted output file"); return; } else { if (counter ==
		 * 10) {
		 * System.out.println("Could not delete output file. Retry Attempt exhausted!");
		 * return; } else { System.out.
		 * println("Could not delete output file. Retrying after 1 sec. Attempt :: " +
		 * counter); Thread.sleep(1000); } } }
		 * System.out.println("Deleted output file"); */
	}
}