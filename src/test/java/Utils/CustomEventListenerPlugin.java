package Utils;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import cucumber.api.PickleStepTestStep;
import cucumber.api.event.ConcurrentEventListener;
import cucumber.api.event.EventHandler;
import cucumber.api.event.EventPublisher;
import cucumber.api.event.TestCaseFinished;
import cucumber.api.event.TestCaseStarted;
import cucumber.api.event.TestStepFinished;
import cucumber.runtime.CucumberException;

public class CustomEventListenerPlugin implements ConcurrentEventListener {

	public CustomEventListenerPlugin() {
	}

	/*
	 * Runs before every test case , i.e. scenario
	 */
	private EventHandler<TestCaseStarted> testCaseStartedHandler = new EventHandler<TestCaseStarted>() {
		@Override
		public void receive(TestCaseStarted event) {
			System.out.println("Inside Event Listner :" + event.getClass().getCanonicalName());

		}
	};

	/*
	 * 
	 */
	private EventHandler<TestCaseFinished> testCaseFinishedHandler = new EventHandler<TestCaseFinished>() {
		@Override
		public void receive(TestCaseFinished event) {

			System.out.println("Inside Event Listner :" + event.getClass().getCanonicalName());

		}
	};

	/*
	 * 
	 */
	private EventHandler<TestStepFinished> testStepFinishedHandler = new EventHandler<TestStepFinished>() {

		@Override
		public void receive(TestStepFinished event) {
			System.out.println("Inside Event Listner :" + event.getClass().getCanonicalName());

		}
	};

	@Override
	public void setEventPublisher(EventPublisher publisher) {
		publisher.registerHandlerFor(TestCaseStarted.class, testCaseStartedHandler);
		publisher.registerHandlerFor(TestStepFinished.class, testStepFinishedHandler);
		publisher.registerHandlerFor(TestCaseFinished.class, testCaseFinishedHandler);

	}

}
