Meta:
@backup_name 

Narrative:
In order to communicate effectively to the business some functionality
As a development team
I want to use Behaviour-Driven Development
 
Lifecycle:
After:
Outcome: ANY
Given User logs out



Scenario: 1. "Root" logs in first time

Given User logs in first time as "Root"
When User changes old password to new
Then User is on Home page
