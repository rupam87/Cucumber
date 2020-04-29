package Api;


import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {"pretty", "html:target/cucumber"},
		features = "src/test/resources",
		glue = { "Api.StepDefs" },
		tags = {"@soapapi"}
		)
public class RunCucumberTest {
}