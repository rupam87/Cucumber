package Utils;

import org.openqa.selenium.WebDriver;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface IWebdriverFactory {
	
	public WebDriver GetDriver() throws FileNotFoundException, IOException;

	public void DisposeDriver();
}
