package Api;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
//import org.junit.runner.RunWith;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberFeatureWrapper;
//import io.cucumber.junit.Cucumber;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.PickleEventWrapper;

//@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "html:target/cucumber","json:target/jsonReports/CucumberTestReport.json" },
features = "src/test/resources", glue = {"Api.StepDefs" }, tags = { "@restapi" })
public class RunCucumberTest extends AbstractTestNGCucumberTests {

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

}