Feature: SoapAPI 


@soapapi 
Scenario Outline: Add Numbers using Calculator 
	Given I read the Xml file "CalculatorAdd.xml", modify "<itemA>" and "<itemB>" 
	And I invoke SOAP call "POST" on "http://www.dneonline.com/calculator.asmx" 
	Then I verify Soap ResponseCode=200 
	And I verify in Soap response "AddResult"="45" 
	
	Examples: 
		|itemA|itemB|
		|23   |22   |     