package Utils;

import java.io.File;
import java.util.Calendar;
import java.util.*;

import StepDefinitions.Hooks;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.gherkin.model.Given;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import cucumber.api.HookTestStep;
import cucumber.api.event.*;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import cucumber.api.PickleStepTestStep;
import cucumber.runtime.CucumberException;
import org.openqa.selenium.WebDriver;

public class CustomEventListenerPlugin implements ConcurrentEventListener {

    private ExtentSparkReporter spark;
    private ExtentReports extent;
    Map<String, ExtentTest> feature = new HashMap<String, ExtentTest>();
    ExtentTest scenario;
    ExtentTest step;

    DIContext scenarioContext;

    public CustomEventListenerPlugin() {   }

	/*@Override
	public void setEventPublisher(EventPublisher publisher) {
		publisher.registerHandlerFor(TestCaseStarted.class, testCaseStartedHandler);
		publisher.registerHandlerFor(TestStepFinished.class, testStepFinishedHandler);
		publisher.registerHandlerFor(TestCaseFinished.class, testCaseFinishedHandler);
	}*/

    /**
     * :: is method reference , so this::collecTag means collectTags method in
     * 'this' instance. Here we says runStarted method accepts or listens to
     * TestRunStarted event type
     */
    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, this::runStarted);
        publisher.registerHandlerFor(TestRunFinished.class, this::runFinished);
        publisher.registerHandlerFor(TestSourceRead.class, this::featureRead);
        publisher.registerHandlerFor(TestCaseStarted.class, this::ScenarioStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::stepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::stepFinished);
    }

    /**
     * Here we set argument type as TestRunStarted if you set anything else then the
     * corresponding register shows error as it doesn't have a listner method that
     * accepts the type specified in TestRunStarted.class
     */
    // Here we create the reporter
    private void runStarted(TestRunStarted event) {
        extent =  new ExtentReports();
        spark = new ExtentSparkReporter("test-output/AventReport/ExtentReportResults.html");
        spark.config().setReportName("My Reports");
        extent.attachReporter(spark);
    }

    /**
     * TestRunFinished event is triggered when all feature file executions are completed
     *
     * @param event
     */
    private void runFinished(TestRunFinished event) {
        extent.flush();
    }

    /**
     * This event is triggered when feature file is read. Here we create the feature node
     *
     * @param event
     */
    private void featureRead(TestSourceRead event) {
        String featureSource = event.uri;
        String featureName = featureSource.split(".*/")[1];
        if (feature.get(featureSource) == null) {
            feature.putIfAbsent(featureSource, extent.createTest(featureName));
        }
    }

    /**
     * This event is triggered when Test Case is started. Here we create the scenario node.
     *
     * @param event
     */
    private void ScenarioStarted(TestCaseStarted event) {
        String featureName = event.getTestCase().getUri().toString();
        scenario = feature.get(featureName).createNode(event.getTestCase().getName());
    }

    /**
     * step started event. Here we create the test node
     *
     * @param event
     */
    private void stepStarted(TestStepStarted event) {
        String stepName = " ";
        String keyword = "Triggered the hook :";
        // We checks whether the event is from a hook or step
        if (event.testStep instanceof PickleStepTestStep) {
            // TestStepStarted event implements PickleStepTestStep interface
            // Which have additional methods to interact with the event object
            // So we have to cast TestCase object to get those methods
            PickleStepTestStep steps = (PickleStepTestStep) event.testStep;
            stepName = steps.getPickleStep().getText();
        } else {
            // Same with Hook TestStep
            HookTestStep hoo = (HookTestStep) event.testStep;
            stepName = keyword + " " + hoo.getHookType().name();
        }
        step = scenario.createNode(Given.class, stepName);
    }

    /**
     * This is triggered when TestStep is finished
     *
     * @param event
     */
    private void stepFinished(TestStepFinished event) {

        if (event.result.getStatus().toString() == "PASSED") {
            step.log(Status.PASS, "Step passed");
        } else if (event.result.getStatus().toString() == "SKIPPED") {
            step.log(Status.SKIP, "Step was skipped ");
        } else {
            try {
                this.scenarioContext = Hooks.diContextThreadLocal.get();
                // Capture Screenshot, copy it, attach it to Extent test
                TakesScreenshot tks = (TakesScreenshot) this.scenarioContext.GetWebDriver();
                File source = tks.getScreenshotAs(OutputType.FILE);
                String fileAbsPath = System.getProperty("user.dir") + "//test-output//AventReport//Screenshots//"
                        + Calendar.getInstance().getTimeInMillis() + ".png";
                File destFile = new File(fileAbsPath);
                FileUtils.copyFile(source, destFile);
                step.log(Status.FAIL, MediaEntityBuilder.createScreenCaptureFromPath(fileAbsPath).build());
            } catch (Exception e) {
                step.log(Status.FAIL, "Test Step Failed. Exception while getting screenshot: " + e.getMessage());
            }
        }
    }

}
