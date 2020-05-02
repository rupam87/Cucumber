package Api;

import org.joda.time.DateTime;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	private static ExtentReports reports = null;

	public static ExtentReports ExtentReportsInstance() {
		if (reports == null) {
			reports = new ExtentReports();
			String fileName = DateTime.now().toString("MMddYYYY_HHmmss") + "_report.html";
			ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/AventReport/" + fileName);
			sparkReporter.config().setTheme(Theme.DARK);
			sparkReporter.config().setDocumentTitle("Cucumber TestNG Tests");
			sparkReporter.config().setReportName("Cucumber TestNG Report");
			reports.attachReporter(sparkReporter);			
		}
		return reports;
	}

}
