# Cucumber
Cucumer Java project consisting of API REST and SOAP tests.


Maven commands to run tests :
=============================
1.  mvn clean test  :- This will invoke the maven surefire plugin which is configured to point to the TestNG xml file. Thus issuing
this command would run the Cucumber Runner class (which is configured in testNG xml file)

2. mvn verify -DskipTests   :-  This will generate the pretty cucumber reports at target\cucumber-reports\cucumber-html-reports 

3. mvn test -Dcucumber.options="--tags @soapapi"  :- If we want to override the cucumber options in Runner class then we can achieve the same via CLI


Runing Cucumber tests in Parallel:
=================================

1. In the Cucumber Runner class override the DataProvider method from AbstractTestNGCucumberTests to set 'parallel=true'.
2. The default thread count is 10, to configure it, add the below Configuration in SureFire plugin in POM under plugins section
	``` xml
	<properties>
        <property>
            <name>dataproviderthreadcount</name>
            <value>  user input int value to set for Thread Count </value>
        </property>
    </properties>
	```
	
Default Cucumber pretty format reports
======================================
1. Add the following to the Cucumber.Options -> plugins section. "pretty", "html:target/cucumber" to generate reports in ../target/cucumber folder

"maven-cucumber-reporting" Reports
==================================
1. Add dependency for "maven-cucumber-reporting" 
2. Add the followinf to the Cucumber.Options -> plugins section.  "json:target/jsonReports/CucumberTestReport.json"
3. run mvn clean test, once build succeeds, observe that the json file is created
4. Add the following under pom.xml plugin section

	``` xml
	<plugin>
		<groupId>net.masterthought</groupId>
		<artifactId>maven-cucumber-reporting</artifactId>
		<version>5.0.0</version>
		<executions>
			<execution>
				<id>execution</id>
				<phase>verify</phase>
				<goals>
					<goal>generate</goal>
				</goals>
				<configuration>
					<projectName>APITestProject</projectName>
					<inputDirectory>${project.build.directory}/jsonReports</inputDirectory>
						<jsonFiles>
							<param>**/*.json</param>
						</jsonFiles>
					<outputDirectory>${project.build.directory}/cucumber-reports</outputDirectory>
					<cucumberOutput>${project.build.directory}/jsonReports/CucumberTestReport.json</cucumberOutput>	 			
				</configuration>
			</execution>
		</executions>
	</plugin>
	```

5. Run mvn verify -DskipTests to kick off maven "verify" stage without running tests, this will generate the html reports at target\cucumber-reports\cucumber-html-reports

	
Adding Extent Reports Support for parallel runs
===============================================
1. Include the maven dependency for Avent Extent Reports 4
2. Create Static class to create "ExtentReports" object and instantiate ExtentSparkReporter (Html Reporter has been replaced in latest version)
3. Add TestNG @BeforeSuite method in Cucumber Runner class and create the ExtentReports instance and store it in DIContext class.(We will have only 1 html per test suite)
4. Add TestNG @AfterSuite method in Cucumber Runner class to flush Extent Report
5. In Hooks file, inside @Before method, create the Extent Test object (i.e. per scenario we will always have a new Extent Test objec) and store it in DIContext class.
6. Inside individual Step Definition method, retrieve the Extent Test object to log statements.


Selenium Downloads Links
========================
1. IEdriver server exe - https://selenium-release.storage.googleapis.com/index.html?path=3.141/
2. Chrome driver exe - https://chromedriver.storage.googleapis.com/index.html
3. Firefox (geckodriver exe) - https://github.com/mozilla/geckodriver/releases
4. Selenium-java version - https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java/3.141.59
5. Chrome Browser version - 81 (32 bit)
6. IE version - 11 (32 bit)
7. Mozilla Firefox - 75 (64 bit)

Special Steps for IE - https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver

Running UI Tests (in parallel mode)
===================================
The Parallel mode is by default enabled for UI or API in the Test Runner class (i.e. in the @DataProvider override) in conjunction with the 
thread count specified in pom.xml plugins section for maven-surefire-plugin

Imp -  The framework expects Chrome Browser binaries to be present at \\Binaries\\Chrome81\\Application\\chrome.exe
and Firefox Browser binaries to be present at \\Binaries\\MozillaFirefox75\\firefox.exe.  This is configured in WebDriverFactory.java file.
You need to either place the resp binaries (matching the path) or modify the code in WebDriverFactory to reflect your system's binary location.

Command to run UI tests -  mvn test -Dcucumber.options="--tags @UI"


NOTE -  Presently IE and Chrome works fine. But Firefox has issues, this is due to versiom compatibility issue.