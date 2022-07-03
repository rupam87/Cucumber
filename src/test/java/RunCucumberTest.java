
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
//import org.junit.runner.RunWith;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;

import Utils.ExtentManager;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberFeatureWrapper;
//import io.cucumber.junit.Cucumber;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.PickleEventWrapper;

//@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber",
		"json:test-output/jsonReports/CucumberTestReport.json", "Utils.CustomEventListenerPlugin" }, features = "src/test/resources", glue = {
				"StepDefinitions" }, tags = { "@UI" })
public class RunCucumberTest extends AbstractTestNGCucumberTests {

	ExtentReports eReport = null;

	@BeforeSuite
	public void CreateExtentReports() {
		eReport = ExtentManager.ExtentReportsInstance();
	}

	@Override
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		super.setUpClass();
	}

	/*@Override
	@Test(groups = "", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
	public void runScenario(PickleEventWrapper pickleWrapper, CucumberFeatureWrapper featureWrapper) throws Throwable {
		super.runScenario(pickleWrapper, featureWrapper);
	}*/

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

	@AfterSuite
	public void FlushExtentReports() {
		eReport.flush();
	}

}