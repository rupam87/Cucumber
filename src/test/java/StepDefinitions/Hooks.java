package StepDefinitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import Utils.DIContext;
import Utils.ExtentManager;
import Utils.WebdriverFactory;
import io.cucumber.core.api.Scenario;
import io.cucumber.core.event.Status;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.AfterStep;

public class Hooks {

	DIContext scenarioContext;
	ExtentTest test;
	WebdriverFactory driverFactory;

	public Hooks(DIContext context) {
		this.scenarioContext = context;
	}

	@Before("@API")
	public void beforeHooks(Scenario scenario) throws IOException {
		System.out.println("Scenario ID :" + scenario.getId());
		// Get Extent Test Object
		this.test = this.scenarioContext.GetExtentTest(ExtentManager.ExtentReportsInstance(), scenario.getName());
		this.test.info("Extent Test Object stored in Scenario context!");
		this.test.info("INSIDE BEFORE API HOOKS!! Scenario name :" + scenario.getName());
	}

	/*
	 * If its UI test the set the Webdriver creation logic
	 */
	@Before("@UI")
	public void beforeUIHooks(Scenario scenario) throws IOException {

		// Read the Docker public IP to pass it on to Webdriver Factory
		/*try (BufferedReader bufReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "//output.txt"))) {
			System.out.println("Waiting to find an IP match in output.txt");*/
			String dockerPublicIP = "192.168.99.101"; //bufReader.readLine();
			this.scenarioContext.SetValueToStore(dockerPublicIP);
			System.out.println("INSIDE BEFORE HOOKS!! Scenario name :" + scenario.getName());
		//}

		// Get Extent Test Object
		this.test = this.scenarioContext.GetExtentTest(ExtentManager.ExtentReportsInstance(), scenario.getName());
		System.out.println("Extent Test Object stored in Scenario context!");
		try {
			driverFactory = new WebdriverFactory(this.scenarioContext);
			driverFactory.GetDriver();
		} catch (FileNotFoundException e) {
			this.test.error("Exception caught from WebdriverFactory!" + e.getMessage());
			throw e;
		} catch (IOException e) {
			this.test.error("Exception caught from WebdriverFactory!" + e.getMessage());
			throw e;
		}
		System.out.println("INSIDE BEFORE UI HOOKS!! Scenario name :" + scenario.getName());
	}

	@AfterStep
	public void afterStepHooks(Scenario scenario) throws Exception {

		// Check if there are any errors
		if (this.scenarioContext.getStepErrorCount() > 0) {

			// Check if it is a @UI tagged test, if yes, attach screenshot with
			// the step
			if (scenario.getSourceTagNames().stream().anyMatch(m -> m.toLowerCase().contains("ui"))) {
				// Capture Screenshot
				TakesScreenshot tks = (TakesScreenshot) this.scenarioContext.GetWebDriver();
				File source = tks.getScreenshotAs(OutputType.FILE);
				String fileAbsPath = System.getProperty("user.dir") + "//test-output//AventReport//Screenshots//"
						+ Calendar.getInstance().getTimeInMillis() + ".png";
				File destFile = new File(fileAbsPath);
				FileUtils.copyFile(source, destFile);
				this.test.fail("Exception", MediaEntityBuilder.createScreenCaptureFromPath(fileAbsPath).build());
			}
			// rethrow the exception to terminate scenario execution
			throw this.scenarioContext.getStepError();
		}

		this.test.info("INSIDE AFTER STEP HOOKS!! Scenario name :" + scenario.getName());
	}

	@After("@UI")
	public void afterUIHooks(Scenario scenario) {
		driverFactory.DisposeDriver();
		this.test.info("INSIDE AFTER UI HOOKS!! Scenario name :" + scenario.getName());
	}

	@After
	public void afterHooks(Scenario scenario) {
		if (scenario.getStatus().equals(Status.SKIPPED))
			this.test.skip(scenario.getName());
		else if (scenario.getStatus().equals(Status.PENDING))
			this.test.warning(scenario.getName());
		else if (scenario.getStatus().equals(Status.AMBIGUOUS))
			this.test.warning(scenario.getName());
		else if (scenario.getStatus().equals(Status.UNUSED))
			this.test.skip(scenario.getName());
		else if (scenario.getStatus().equals(Status.FAILED))
			this.test.fail(scenario.getName());
		else if (scenario.getStatus() == Status.PASSED)
			this.test.pass(scenario.getName());
		this.test.info("INSIDE AFTER HOOKS NOW!!  Scenario name: " + scenario.getName());
	}

}
