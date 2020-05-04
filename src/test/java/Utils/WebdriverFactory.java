package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;


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
		switch (browser.toLowerCase()) {
		case "chrome":
			ChromeOptions cOptions = new ChromeOptions();
			cOptions.addArguments("start-maximized");
			cOptions.addArguments("disable-infobars");
			cOptions.setHeadless(false);
			cOptions.setBinary(System.getProperty("user.dir") + "\\Binaries\\Chrome81\\Application\\chrome.exe");
			this.scenarioContext.GetExtentTest().info("bianry set to :"+ System.getProperty("user.dir") + "\\Binaries\\Chrome81\\Application\\chrome.exe");
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\Binaries\\chromedriver_win32\\chromedriver.exe");
			this.scenarioContext.GetExtentTest().info("webdriver.chrome.driver set at :"+ System.getProperty("webdriver.chrome.driver"));
			driver = new ChromeDriver(cOptions);
			this.scenarioContext.GetExtentTest().info("Created Chrome Driver");
			break;
		case "firefox":
			FirefoxOptions fOptions = new FirefoxOptions();
			fOptions.setBinary(System.getProperty("user.dir") + "\\Binaries\\MozillaFirefox75\\firefox.exe");
			fOptions.setHeadless(false);
			fOptions.addArguments("start-maximized");
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\Binaries\\geckodriver-v0.25.0-win64\\geckodriver.exe");
			this.scenarioContext.GetExtentTest().info("webdriver.gecko.driver set at :"+ System.getProperty("webdriver.gecko.driver"));
			driver = new FirefoxDriver(fOptions);
			this.scenarioContext.GetExtentTest().info("Created Firefox Driver");
			break;
		case "ie":
			InternetExplorerOptions iOptions = new InternetExplorerOptions();
			iOptions.takeFullPageScreenshot();
			iOptions.ignoreZoomSettings();
			iOptions.requireWindowFocus();
			iOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
			iOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
			System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "\\Binaries\\IEDriverServer_Win32_3.141.59\\IEDriverServer.exe");
			this.scenarioContext.GetExtentTest().info("webdriver.ie.driver set at :"+ System.getProperty("webdriver.ie.driver"));
			
			driver = new InternetExplorerDriver(iOptions);
			this.scenarioContext.GetExtentTest().info("Created IE Driver");
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
