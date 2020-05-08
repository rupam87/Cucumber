package stepDefs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.aventstack.extentreports.ExtentTest;

import Utils.DIContext;
import Utils.ExtentManager;
import Utils.WebdriverFactory;
import io.cucumber.core.api.Scenario;
import io.cucumber.core.event.Status;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
import io.cucumber.java.After;
import io.cucumber.java.Before;

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
		System.out.println("Extent Test Object stored in Scenario context!");
		System.out.println("INSIDE BEFORE API HOOKS!! Scenario name :" + scenario.getName());
	}

	/*
	 * If its UI test the set the Webdriver creation logic
	 */
	@Before("@UI")
	public void beforeUIHooks(Scenario scenario) throws IOException {

		// Read the Docker public IP to pass it on to Webdriver Factory
		try (BufferedReader bufReader = new BufferedReader(
				new FileReader(System.getProperty("user.dir") + "//output.txt"))) {
			System.out.println("Waiting to find an IP match in output.txt");
			String dockerPublicIP = bufReader.readLine();
			this.scenarioContext.SetValueToStore(dockerPublicIP);
		}

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

	@After("@UI")
	public void afterUIHooks() {
		driverFactory.DisposeDriver();
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
