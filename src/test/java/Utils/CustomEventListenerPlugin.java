package Utils;

import java.io.File;
import java.util.Calendar;
import java.util.*;

import StepDefinitions.Hooks;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.gherkin.model.Given;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import cucumber.api.HookTestStep;
import cucumber.api.event.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import cucumber.api.PickleStepTestStep;

public class CustomEventListenerPlugin implements ConcurrentEventListener {

    private ExtentSparkReporter spark;
    private ExtentReports featureExtent;

    private ExtentReports scenarioExtent;

    private Map<String, ExtentReports> scenarioExtentReportList = new HashMap<String, ExtentReports>();

    Map<String, ExtentTest> featureTestList = new HashMap<String, ExtentTest>();

    Map<String, ExtentTest> scenarioTestList = new HashMap<String, ExtentTest>();

    ExtentTest scenarioTest;
    ExtentTest stepTest_FeatureReport;

    ExtentTest stepTest_ScenarioReport;

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
        //publisher.registerHandlerFor(TestSourceRead.class, this::featureRead);
        publisher.registerHandlerFor(TestCaseStarted.class, this::ScenarioStarted);
        publisher.registerHandlerFor( TestCaseFinished.class, this::ScenarioFinished);
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
        featureExtent =  new ExtentReports();
        spark = new ExtentSparkReporter("test-output/AventReport/ExtentReportResults.html");
        spark.config().setReportName("My Reports");
        featureExtent.attachReporter(spark);
    }

    /**
     * TestRunFinished event is triggered when all feature file executions are completed
     *
     * @param event
     */
    private void runFinished(TestRunFinished event) {
        featureExtent.flush();
    }

    /**
     * This event is triggered when feature file is read. Here we create the feature node
     *
     * @param event
     */
    private void featureRead(TestSourceRead event) {
        String featureSource = event.uri;
        String featureName = featureSource.split(".*/")[1];
        if (featureTestList.get(featureSource) == null) {
            featureTestList.putIfAbsent(featureSource, featureExtent.createTest(featureName));
        }
    }

    /**
     * This event is triggered when Test Case is started. Here we create the scenario node.
     *
     * @param event
     */
    private void ScenarioStarted(TestCaseStarted event) {
        String scenarioName = event.getTestCase().getName();

        // Create Extent Report for Each Scenario
        scenarioExtent =  new ExtentReports();
        spark = new ExtentSparkReporter("test-output/AventReport/ScenarioReports/"+ scenarioName.replaceAll("[// ,//s, :]","_") + ".html");
        spark.config().setReportName("My Reports");
        scenarioExtent.attachReporter(spark);
        scenarioTestList.putIfAbsent(scenarioName, scenarioExtent.createTest(scenarioName));
        scenarioExtentReportList.putIfAbsent(scenarioName, scenarioExtent);

        // create Node in the Feature Level Parent Reporter
        String featureName = event.getTestCase().getUri().toString();
        if (featureTestList.get(featureName) == null) {
            featureTestList.putIfAbsent(featureName, featureExtent.createTest(featureName));
        }
        scenarioTest = featureTestList.get(featureName).createNode(scenarioName);
    }

    /**
     * Flush the Scenario Level Extent Report
     * @param event
     */
    private void ScenarioFinished(TestCaseFinished event) {
        String scenarioName = event.getTestCase().getName();
        scenarioExtentReportList.get(scenarioName).flush();
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
        stepTest_FeatureReport = scenarioTest.createNode(Given.class, stepName);
        stepTest_ScenarioReport = scenarioTestList.get(event.getTestCase().getName()).createNode(Given.class, stepName);
    }

    /**
     * This is triggered when TestStep is finished
     *
     * @param event
     */
    private void stepFinished(TestStepFinished event) {

        if (event.result.getStatus().toString() == "PASSED") {
            stepTest_FeatureReport.log(Status.PASS, "Step passed");
            stepTest_ScenarioReport.log(Status.PASS, "Step passed");
        } else if (event.result.getStatus().toString() == "SKIPPED") {
            stepTest_FeatureReport.log(Status.SKIP, "Step was skipped ");
            stepTest_ScenarioReport.log(Status.SKIP, "Step was skipped ");
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
                stepTest_FeatureReport.log(Status.FAIL, MediaEntityBuilder.createScreenCaptureFromPath(fileAbsPath).build());
                stepTest_ScenarioReport.log(Status.FAIL, MediaEntityBuilder.createScreenCaptureFromPath(fileAbsPath).build());
            } catch (Exception e) {
                stepTest_FeatureReport.log(Status.FAIL, "Test Step Failed. Exception while getting screenshot: " + e.getMessage());
                stepTest_ScenarioReport.log(Status.FAIL, "Test Step Failed. Exception while getting screenshot: " + e.getMessage());
            }
        }
    }

}
