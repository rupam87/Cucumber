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
            <value>  user input int value to set for Thread Count <\/value>
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
4. Add the following under pom.xml plugin section :
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