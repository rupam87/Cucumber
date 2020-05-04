package stepDefs;

import Utils.DIContext;
import io.cucumber.java8.En;

public class UiSteps implements En {
	DIContext scenarioContext;

	public UiSteps(DIContext context) {

		scenarioContext = context;

		Given("I navigate to {string}", (String url) -> {
			scenarioContext.GetWebDriver().get(url);
			scenarioContext.GetExtentTest().info("naviagted to " + url);
		});

		When("I click on {string} button", (String string) -> {
			
		});

		Then("I should be on {string}", (String string) -> {
			
		});
	}
}
