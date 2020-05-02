Feature: SoapAPI 


@soapapi @calcapi
Scenario Outline: Add Numbers using Calculator 
	Given I read the Xml file "CalculatorAdd.xml", modify "<itemA>" and "<itemB>" 
	And I invoke SOAP call "POST" on "http://www.dneonline.com/calculator.asmx" 
	Then I verify Soap ResponseCode=200 
	And I verify in Soap response "AddResult"="45" 
	
	Examples: 
		|itemA|itemB|
		|23   |22   |     
		

@xmlparse @soapapi 
Scenario: Parse and read XML values
Given I read the Xml file "SampleXMLtoParse.xml"
And I select for nodes with filterxpath = "//*[@tag]//text()" to verify "23,56,79,33" are present
And I select for nodes with filterxpath = "//*[@tag]//result/text()" to verify "79,79,33" are present