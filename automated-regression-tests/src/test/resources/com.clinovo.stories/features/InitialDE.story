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



Scenario: 1.1 User is able to perform IDE into CRF with one section

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID  |Event Name |CRF Name               |input23(T) |input24(T)|input25(R)|input27(R)|input28(T)|input29(T)|
|<Study Subject ID>|TestEvent A|CRF_Data_type_Ungrouped|03-Mar-2014|22:00     |0         |0         |test      |44        |

Then User is on SM page
And User verifies "Your data has been saved." message in 'Alerts&Messages' section
Examples:
{scope=Scenario}
|User       |<Study Subject ID>|  
|CRC        |TestSubject_1     | 
|PI         |TestSubject_2     | 
|Study Admin|TestSubject_3     |


Scenario: 1.2 User is able to perform IDE into CRF with two sections

Given User logs in as "<User>"
And User goes to SM page
When User fills in CRF:
|Study Subject ID  |Event Name |CRF Name        |input39(R)|input40(T)|input41(Sv)|
|<Study Subject ID>|TestEvent A|CRF_Two_sections|1         |test 1    |2          |
And User clicks 'Save' button on CRF page
And User fills in data into CRF:
|Study Subject ID  |Event Name |CRF Name        |Add Rows|IG_CRF_T_PAIN_0input50(Sv)|IG_CRF_T_PAIN_0input52(Mv)|IG_CRF_T_PAIN_1input49(T)|IG_CRF_T_PAIN_2input51(T)|
|<Study Subject ID>|TestEvent A|CRF_Two_sections|2       |1                         |1,2,6                     |test 1                   |test 2                   |

And User clicks 'Save' button on CRF page
Then User is on SM page
And User verifies "Your data has been saved." message in 'Alerts&Messages' section
Examples:
|User       |<Study Subject ID>|
|CRC        |TestSubject_1     |
|PI         |TestSubject_2     |
|Study Admin|TestSubject_3     |


Scenario: 1.3.1 Correct Data is shown on DE page after saving CRF without groups

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF: 
|Study Subject ID  |Event Name |CRF Name       |input30(R)|input31(T)|input32(Sv)|input33(T)|input34(Mv)|input35(C)|input36(F)|input37(T) |input38(T)|
|<Study Subject ID>|TestEvent A|CRF_One_section|1         |test 21   |2          |test 22   |2,4,5      |0         |          |03-Jul-2010|Apr-2016  |    

Then User verifies saved CRF data on DE page
Examples:
{scope=Scenario}
|User       |<Study Subject ID>|
|CRC        |TestSubject_1     |
|PI         |TestSubject_2     |
|Study Admin|TestSubject_3     |


Scenario: 1.3.2 Correct Data is shown on DE page after saving grouped CRF

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:  
|Study Subject ID  |Event Name |CRF Name        |Section Name|Add Rows|item1                       |item2                         |item3                        |item4                         |item5                            |item6                       |item7                                 |item8                              |
|<Study Subject ID>|TestEvent A|CRF_Two_sections|Section_2   |2       |IG_CRF_T_PAIN_0input48(R): 2|IG_CRF_T_PAIN_0input49(T): ABC|IG_CRF_T_PAIN_0input50(Sv): 2|IG_CRF_T_PAIN_0input51(T): qwr|IG_CRF_T_PAIN_0input52(Mv): 1,3,5|IG_CRF_T_PAIN_0input53(C): 0|IG_CRF_T_PAIN_0input55(T): 21-Feb-2014|IG_CRF_T_PAIN_0input56(T): Apr-1997|
|                  |           |                |            |        |IG_CRF_T_PAIN_1input48(R): 1|IG_CRF_T_PAIN_1input49(T): CVB|IG_CRF_T_PAIN_1input50(Sv): 1|IG_CRF_T_PAIN_1input51(T): asd|IG_CRF_T_PAIN_1input52(Mv): 1,6,4|IG_CRF_T_PAIN_1input53(C):  |IG_CRF_T_PAIN_1input55(T): 03-Jan-2003|IG_CRF_T_PAIN_1input56(T): Jun-1993|

Then User verifies saved CRF data on DE page
Examples:
{scope=Scenario}
|User       |<Study Subject ID>|
|CRC        |TestSubject_4     |
|PI         |TestSubject_5     |
|Study Admin|TestSubject_6     |


Scenario: 1.4 Correct Data is shown on View Data Entry page after saving 

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID  |Event Name |CRF Name       |input30(R)|input31(T)                |input32(Sv)|input33(T)|input34(Mv)|input35(C)|input37(T)  |input38(T) |
|<Study Subject ID>|TestEvent A|CRF_One_section|2         |Testing Initial Data Entry|2          |Test      |2,5        |0         |20-Jun-2016 |05-Jul-2016|
Then User verifies saved CRF data on View CRF page
Examples:
{scope=Scenario}
|User       |<Study Subject ID>|
|CRC        |TestSubject_4     |
|PI         |TestSubject_5     |
|Study Admin|TestSubject_6     |


Scenario: 6.1 User is not able to save CRF, if required items are not filled

Given User logs in as "<User>"
And User goes to SM page
When User fills in and saves CRF:
|Study Subject ID|Event Name |CRF Name                    |input1(R)|input2(T)|
|TestSubject_4   |TestEvent A|CRF_Required_items_Ungrouped|2        |test     |
Then User verifies error message "Missing data in a required field" on CRF page
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
|<Study Subject ID>|TestEvent A|CRF_Required_items_Ungrouped|2        |test     |
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
|TestSubject_4   |TestEvent A|CRF_Data_type_Ungrouped|test      |
Then User verifies error message "The input you provided is not an integer." on CRF page
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
|TestSubject_4   |TestEvent A|CRF_Data_type_Ungrouped|05-Mar-   |
Then User verifies error message "The input you provided is not a valid date in DD-MMM-YYYY format." on CRF page
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
Then User verifies error message "Missing data in a required field" on CRF page
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
|TestSubject_4   |TestEvent A|CRF_Regular_expression_Ungrouped|1234      |
Then User verifies error message "Must be in the range between (00.00) and (23.59)" on CRF page
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
|TestSubject_4   |TestEvent A|CRF_Regular_expression_Ungrouped|a test    |
Then User verifies error message "Must be String 2-5 character long." on CRF page
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
|TestSubject_4   |TestEvent A|CRF_SCD |(3)          |test         |
Then User verifies error message "Third option is not selected in check boxes but value is provided." on CRF page
And User leaves CRF without saving
Examples:
{scope=Scenario}
|User   	|
|CRC    	|
|PI   		|
|Study Admin|