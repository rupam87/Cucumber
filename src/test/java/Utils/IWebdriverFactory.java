package Utils;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface IWebdriverFactory {
	
	public void GetDriver() throws FileNotFoundException, IOException;

	public void DisposeDriver();
}
