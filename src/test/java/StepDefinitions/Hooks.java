package StepDefinitions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import com.aventstack.extentreports.Status;
import io.github.sridharbandi.AxeRunner;
import io.github.sridharbandi.HtmlCsRunner;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import Utils.DIContext;
import Utils.ExtentManager;
import Utils.WebdriverFactory;
import cucumber.api.TestCase;
import io.cucumber.core.api.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;

public class Hooks {

	DIContext scenarioContext;
	ExtentTest test;
	WebdriverFactory driverFactory;
	TestCase cukesTestCase = null;

	public Hooks(DIContext context) {
		this.scenarioContext = context;
	}

	@Before("@API")
	public void beforeHooks(Scenario scenario) throws Exception {
		System.out.println("Scenario ID :" + scenario.getId());
		// Get Extent Test Object
		this.test = this.scenarioContext.GetExtentTest(ExtentManager.ExtentReportsInstance(), scenario.getName());
		System.out.println("Extent Test Object stored in Scenario context!");

		// Get TestCase object for each Scenario
		/*Field cukesTestCaseField = scenario.getClass().getDeclaredField("testCase");
		cukesTestCaseField.setAccessible(true);
		cukesTestCase = (TestCase) cukesTestCaseField.get(scenario);*/

		this.test.info("INSIDE BEFORE API HOOKS!! Scenario name :" + scenario.getName());
	}

	/*
	 * If its UI test the set the Webdriver creation logic
	 */
	@Before("@UI")
	public void beforeUIHooks(Scenario scenario) throws Exception {
		// Get Extent Test Object
		this.test = this.scenarioContext.GetExtentTest(ExtentManager.ExtentReportsInstance(), scenario.getName());
		System.out.println("Extent Test Object stored in Scenario context!");
		try {
			driverFactory = new WebdriverFactory(this.scenarioContext);
			WebDriver driver = driverFactory.GetDriver();
			// Set the html cs runner for AY11  ADA testing
			HtmlCsRunner htmlCsRunner = new HtmlCsRunner(driver);
			this.scenarioContext.setHtmlCsRunner(htmlCsRunner);
			AxeRunner axeRunner = new AxeRunner(driver);
			this.scenarioContext.setAxeRunner(axeRunner);

		} catch (FileNotFoundException e) {
			this.test.log( Status.FAIL, "Exception caught from WebdriverFactory!" + e.getMessage());
			throw e;
		} catch (IOException e) {
			this.test.log(Status.FAIL,"Exception caught from WebdriverFactory!" + e.getMessage());
			throw e;
		}

		// Get TestCase object for each Scenario
		/*Field cukesTestCaseField = scenario.getClass().getDeclaredField("testCase");
		cukesTestCaseField.setAccessible(true);
		cukesTestCase = (TestCase) cukesTestCaseField.get(scenario);*/

		this.test.info("INSIDE BEFORE UI HOOKS!! Scenario name :" + scenario.getName());
	}

	@AfterStep
	public void afterStepHooks(Scenario scenario) throws Exception {

		// Check if there are any errors
		if (this.scenarioContext.getStepErrorCount() > 0) {

			// Check if it is a @UI tagged test, if yes, attach screenshot with
			// the step
			if (scenario.getSourceTagNames().stream().anyMatch(m -> m.toLowerCase().contains("ui"))) {
				// Capture Screenshot, copy it, attach it to Extent test
				TakesScreenshot tks = (TakesScreenshot) this.scenarioContext.GetWebDriver();
				File source = tks.getScreenshotAs(OutputType.FILE);
				String fileAbsPath = System.getProperty("user.dir") + "//test-output//AventReport//Screenshots//"
						+ Calendar.getInstance().getTimeInMillis() + ".png";
				File destFile = new File(fileAbsPath);
				FileUtils.copyFile(source, destFile);
				this.test.fail("Exception",MediaEntityBuilder.createScreenCaptureFromPath(fileAbsPath).build());
			} 

			// rethrow the exception to terminate scenario execution
			//throw this.scenarioContext.getStepError();
		} 
		
		this.test.info("INSIDE AFTER STEP HOOKS!! Scenario name :" + scenario.getName());
	}

	@After(value = "@UI", order = 1)
	public void afterUIHooks(Scenario scenario) {
		try {
			this.scenarioContext.getHtmlCsRunner().generateHtmlReport();
			this.scenarioContext.getAxeRunner().generateHtmlReport();
		}catch (Throwable t) {
			this.test.log(Status.FAIL,"Exception while generating ADA report htmls :" + t.getMessage());
		}
		driverFactory.DisposeDriver();
		this.test.log(Status.INFO, "INSIDE AFTER UI HOOKS!! Scenario name :" + scenario.getName());
		ExtentManager.ExtentReportsInstance().flush();
		System.out.println("Flushed Reports");
	}

}
