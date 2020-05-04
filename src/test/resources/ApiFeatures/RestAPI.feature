@API @RestApi 
Feature: UA Demo 

@pico @restapi 
Scenario: Using PICO 
	Given I store fake creds in context 
	When I read and parse the Json file "FlightStatus.json" 
	And I read the Json file "Input.json", modify it with values 
	
		| name1 | Rupam       |
		| id1   |         001 |
		| name2 | Chakraborty |
		| id2   |         004 |
		
@pokeapi @get 
Scenario: Get call on pokeapi 
	Given I invoke REST call "GET" on "https://pokeapi.co/api/v2/pokemon?offset=100&limit=100" 
	Then I verify ResponseCode="200" 
	Then I filter all names starting with "s" from the response to print them 
	Then I store the url for name="marowak" in scenario context 
	When I invoke REST call "GET" on the url 
	Then I verify ResponseCode="200" 
	And I verify "stats[0].stat.url"="https://pokeapi.co/api/v2/stat/6/" 
	And I filter "stats[?].base_stat"=110 to store "stat.url" in scenario context 
	
@post @createuser @reqres 
Scenario Outline: Create Users via REST call 
	Given I read the file "<filename>" and modify "<name>", "<job>" values 
	Given I invoke REST call "POST" on "https://reqres.in/api/users" 
	Then I verify ResponseCode="201" 
	And I verify in response "name"="<name>" 
	
	Examples: 
		| filename         | name       | job     |
		| CreateUsers.json | FirstUser1 | Leader1 |
		| CreateUsers.json | FirstUser2 | Leader2 |
		| CreateUsers.json | FirstUser3 | Leader3 |
		
@restassured 
Scenario Outline: Using RestAssured 
	Given I invoke REST call "<Method>" on "<Url>" 
	Then I verify ResponseCode="<Code>" 
	And I print response content 
			
	Examples: 
		| Method | Url                                              | Code |
		| GET    | http://dummy.restapiexample.com/api/v1/employees |  200 |
		
@gson @jackson @POJOtoJSON 
Scenario: Convert POJOs to JSONs 
	Given I transform an array of POJO to JSON file 
