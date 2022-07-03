
Feature: UI feature for test

  @ADA @UI
  Scenario: F1S1 :: Dummy UI scenario - Practice
    Given I navigate to "https://www.rahulshettyacademy.com/AutomationPractice/"
     When I click on "Login" button
     #Then I should be on "https://www.rahulshettyacademy.com/AutomationPractice/"
     And I perform ADA testing on the page

    @UI
  Scenario: F1S2 :: Dummy UI scenario - Home
    Given I navigate to "https://www.rahulshettyacademy.com/AutomationPractice/"
     When I click on "Home" button
     Then I should be on "https://www.rahulshettyacademy.com/#/index"



