package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.UnexpectedAlertBehaviour;

public class WebdriverFactory implements IWebdriverFactory {

	DIContext scenarioContext;

	public WebdriverFactory(DIContext context) {
		this.scenarioContext = context;
	}

	/*
	 * Reads the property value of browser from config.properties file
	 */
	private String readBrowserProperty() throws FileNotFoundException, IOException {
		String browser;
		Properties prop = new Properties();
		this.scenarioContext.GetExtentTest().info("Trying to read properties file");
		try (InputStream iStr = new FileInputStream(System.getProperty("user.dir") + "\\config.properties")) {
			prop.load(iStr);
			browser = prop.getProperty("browser");
		}
		return browser;
	}

	@Override
	public void GetDriver() throws FileNotFoundException, IOException {
		WebDriver driver = null;
		String browser = readBrowserProperty();
		this.scenarioContext.GetExtentTest().info("Read Properties File Browser set to :" + browser);
		URL huburl = new URL("http://" + this.scenarioContext.GetValueStored() +":4444/wd/hub");
		switch (browser.toLowerCase()) {
		case "chrome":
			/*ChromeOptions cOptions = new ChromeOptions();
			cOptions.addArguments("start-maximized");
			cOptions.addArguments("disable-infobars");
			cOptions.setHeadless(false);
			cOptions.setBinary(System.getProperty("user.dir") + "\\Binaries\\Chrome81\\Application\\chrome.exe");
			this.scenarioContext.GetExtentTest().info("Browser bianry set to :"+ System.getProperty("user.dir") + "\\Binaries\\Chrome81\\Application\\chrome.exe");
			*/
			DesiredCapabilities ccaps = DesiredCapabilities.chrome();
			ccaps.getCapabilityNames().forEach(name -> this.scenarioContext.GetExtentTest().info(name));	
			//ccaps.setAcceptInsecureCerts(true);
			//ccaps.setCapability(ChromeOptions.CAPABILITY, cOptions);
			//ccaps.setJavascriptEnabled(true);
			ccaps.setBrowserName("chrome");
			ccaps.setPlatform(Platform.LINUX); // Since the node is a Linux node inside docker container
			ccaps.setVersion("83.0.4103.61"); // MUST specify exact version
			driver = new RemoteWebDriver(huburl,ccaps);			
			this.scenarioContext.GetExtentTest().info("Created Remote Chrome Driver");
			break;
		case "firefox":
			FirefoxOptions fOptions = new FirefoxOptions();
			fOptions.setBinary(System.getProperty("user.dir") + "\\Binaries\\MozillaFirefox75\\firefox.exe");
			fOptions.setHeadless(false);
			fOptions.addArguments("start-maximized");
			DesiredCapabilities fcaps = DesiredCapabilities.firefox();
			fcaps.getCapabilityNames().forEach(name -> this.scenarioContext.GetExtentTest().info(name));	
			fcaps.setAcceptInsecureCerts(true);
			fcaps.merge(fOptions);
			fcaps.setJavascriptEnabled(true);
			fcaps.setBrowserName("FIREFOX");
			driver = new RemoteWebDriver(huburl,fcaps);			
			this.scenarioContext.GetExtentTest().info("Created Remote Firefox Driver");
			break;
		case "ie":
			//InternetExplorerOptions iOptions = new InternetExplorerOptions();
			///iOptions.takeFullPageScreenshot();
			//iOptions.ignoreZoomSettings();
			//iOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
			//iOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
			DesiredCapabilities icaps = DesiredCapabilities.internetExplorer();
			icaps.getCapabilityNames().forEach(name -> this.scenarioContext.GetExtentTest().info(name));			
			//icaps.setAcceptInsecureCerts(true);
			//icaps.merge(iOptions);
			//icaps.setJavascriptEnabled(true);
			//icaps.setBrowserName("IE");
			//icaps.setCapability("requireWindowFocus", true);			
			driver = new RemoteWebDriver(huburl,icaps);	
			this.scenarioContext.GetExtentTest().info("Created Remote IE Driver");
			break;
		}

		this.scenarioContext.SetWebDriver(driver);
		this.scenarioContext.GetExtentTest().info("Driver object added to Scenario Context");
	}

	@Override
	public void DisposeDriver() {
		this.scenarioContext.GetWebDriver().close();
		this.scenarioContext.GetExtentTest().info("WebDriver Closed");
		this.scenarioContext.GetWebDriver().quit();
		this.scenarioContext.GetExtentTest().info("WebDriver Quit!");
	}
}
