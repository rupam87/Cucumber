# Cucumber
Cucumer Java project consisting of API REST and SOAP tests.


Maven commands to run tests :
1.  mvn clean test  :- This will invoke the maven surefire plugin which is configured to point to the TestNG xml file. Thus issuing
this command would run the Cucumber Runner class (which is configured in testNG xml file)

2. mvn verify -DskipTests   :-  This will generate the pretty cucumber reports at target\cucumber-reports\cucumber-html-reports 

3. mvn test -Dcucumber.options="--tags @soapapi"  :- If we want to override the cucumber options in Runner class then we can achieve the same via CLI