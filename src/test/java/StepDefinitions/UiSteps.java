package StepDefinitions;

import Utils.DIContext;
import io.cucumber.java8.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class UiSteps implements En {
	DIContext scenarioContext;

	public UiSteps(DIContext context) {
		scenarioContext = context;
		/*
		 * Given("I navigate to {string}", (String url) -> { try {
		 * scenarioContext.GetWebDriver().get(url);
		 * scenarioContext.GetExtentTest().info("naviagted to " + url);
		 * scenarioContext.GetExtentTest().pass("I navigate to {string}"); } catch
		 * (Exception e) { this.scenarioContext.IncrementStepErrorCount(e); } });
		 */
	}

	@Given("I navigate to {string}")
	public void given1(String url) {
		try {
			scenarioContext.GetWebDriver().get(url);
			scenarioContext.GetExtentTest().info("naviagted to " + url);
			scenarioContext.GetExtentTest().pass("I navigate to {string}");
		} catch (Exception e) {
			this.scenarioContext.IncrementStepErrorCount(e);
		}
	}

	@When("I click on {string} button")
	public void IClickOnButton(String button) {
		try {
			this.scenarioContext.GetExtentTest().debug("This is dummy step");
			scenarioContext.GetExtentTest().pass("I click on {string} button");
		} catch (Exception e) {
			this.scenarioContext.IncrementStepErrorCount(e);
		}
	}

	@Then("I should be on {string}")
	public void IShouldBeOn(String string){
		try {
			this.scenarioContext.GetExtentTest().debug("This is dummy step throwing exception");
			throw new Exception("Dummy Exception");
		} catch (Exception e) {
			this.scenarioContext.IncrementStepErrorCount(e);
		}
	}
	
	@Then("I should be at {string}")
	public void IShouldBeAt(String string){
		try {
			this.scenarioContext.GetExtentTest().debug("This is dummy step");
			scenarioContext.GetExtentTest().pass("I should be at {string}");
		} catch (Exception e) {
			this.scenarioContext.IncrementStepErrorCount(e);
		}
	}
}
