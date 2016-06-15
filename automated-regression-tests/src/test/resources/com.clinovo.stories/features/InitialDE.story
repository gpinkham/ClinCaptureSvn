Meta:
@backup_name InitialDE_db

Narrative:
In order to communicate effectively to the business some functionality
As a development team
I want to use Behaviour-Driven Development
 
Lifecycle:
After:
Outcome: ANY
Given User logs out



Scenario: 6.1 User is not able to save CRF, if required items are not filled

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name                    |input1(R)|input2(T)|
|TestSubject_4   |TestEvent A|CRF_Required_items_Ungrouped|2        |test      |
Then Verify error message "Missing data in a required field" on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin| 


Scenario: 6.2 User is able to save CRF, if Annotation is created for required field

Given User logs in as "<User>"
And User goes to SM page
And User fills in CRF:
| Study Subject ID |Event Name |CRF Name                    |input1(R)|input2(T)|
|<Study Subject ID>|TestEvent A|CRF_Required_items_Ungrouped|2        |test      |
And User creates DNs in CRF:
|Item  |Type      |Description    |Detailed Note   |
|input3|Annotation|Information ...|peace of text...|
When User clicks 'Save' button on CRF page
Then User is on SM page
Examples:
{scope=Scenario}
|User   	|<Study Subject ID>|
|CRC    	|TestSubject_1     |
|PI   		|TestSubject_2     |
|Study Admin|TestSubject_3     | 


Scenario: 6.3 User is able to save CRF, if required field is hidden

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
| Study Subject ID |Event Name |CRF Name|input19(C)|
|<Study Subject ID>|TestEvent A|CRF_SCD |(3)       |
Then User is on SM page
Examples:
{scope=Scenario}
|User   	|<Study Subject ID>|
|CRC    	|TestSubject_1     |
|PI   		|TestSubject_2     |
|Study Admin|TestSubject_3     | 


Scenario: 6.4 User is not able to save CRF, if data of incorrect type is entered to some field

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name               |input29(T)|
|TestSubject_4   |TestEvent A|CRF_Data_type_Ungrouped|test       |
Then Verify error message "The input you provided is not an integer." on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|


Scenario: 6.5 User is not able to save CRF, if date format is invalid

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name               |input23(T)|
|TestSubject_4   |TestEvent A|CRF_Data_type_Ungrouped|05-Mar-    |
Then Verify error message "The input you provided is not a valid date in DD-MMM-YYYY format." on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|


Scenario: 6.6 User is not able to save CRF, if required item is not filled in repeating group

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name                  |IG_CRF_R_2ND_0input5(T)|IG_CRF_R_2ND_0input6(T)|
|TestSubject_4   |TestEvent A|CRF_Required_items_Grouped|test                   |05-Mar-2014            |
Then Verify error message "Missing data in a required field" on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|


Scenario: 6.7 User is not able to save CRF, if data does not match item's regexp

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name                        |input11(T)|
|TestSubject_4   |TestEvent A|CRF_Regular_expression_Ungrouped|1234       |
Then Verify error message "Must be in the range between (00.00) and (23.59)" on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|


Scenario: 6.8 User is not able to save CRF, if length of entered data is > then limit

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name                        |input12(T)|
|TestSubject_4   |TestEvent A|CRF_Regular_expression_Ungrouped|a test     |
Then Verify error message "Must be String 2-5 character long." on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|


Scenario: 6.9 User is not able to save CRF, if data is present in some item, that should be hidden

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name|(2)input19(C)|(1)input20(T)|
|TestSubject_4   |TestEvent A|CRF_SCD |(3)          |test          |
Then Verify error message "Third option is not selected in check boxes but value is provided." on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|