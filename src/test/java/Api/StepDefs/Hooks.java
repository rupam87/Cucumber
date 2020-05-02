package Api.StepDefs;

import com.aventstack.extentreports.ExtentTest;

import Api.DIContext;
import Api.ExtentManager;
import io.cucumber.core.api.Scenario;
import io.cucumber.core.event.Status;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
	
	DIContext scenarioContext;
	ExtentTest test;
	
	public Hooks(DIContext context)
	{
		this.scenarioContext = context;
	}

	@Before
	public void beforeHooks(Scenario scenario) {
		System.out.println("INSIDE BEFORE HOOKS NOW!! Scenario name :" + scenario.getName());
		System.out.println("Scenario ID :" + scenario.getId());
		scenario.getSourceTagNames().stream().forEach(name -> System.out.println("Scenario tags :" + name));
		
		this.test = this.scenarioContext.GetExtentTest(ExtentManager.ExtentReportsInstance(),scenario.getName());
		
		System.out.println("Extent Test Object stored in Scenario context!");
	}

	@After
	public void afterHooks(Scenario scenario) {
		if (scenario.getStatus().equals(Status.SKIPPED))
			System.out.println("Scenario has been skipped");
		else if (scenario.getStatus().equals(Status.PENDING))
			System.out.println("Scenario pending");
		else if (scenario.getStatus().equals(Status.AMBIGUOUS))
			System.out.println("Scenario AMBIGUOUS");
		else if (scenario.getStatus().equals(Status.UNUSED))
			System.out.println("Scenario UNUSED");
		else if (scenario.getStatus().equals(Status.FAILED))
			System.out.println("Scenario FAILED");
		else
			System.out.println("Scenario UNDEFINED");
		this.test.info("INSIDE AFTER HOOKS NOW!!  Scenario name: " + scenario.getName());
		
		if(scenario.getStatus() == Status.PASSED)
			this.test.pass(scenario.getName());
		else
			this.test.fail(scenario.getName());
	}
	

}
