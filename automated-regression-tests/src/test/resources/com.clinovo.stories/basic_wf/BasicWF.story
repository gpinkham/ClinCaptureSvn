!--Meta:
!--@driver firefox

Narrative:
In order to communicate effectively to the business some functionality
As a development team
I want to use Behaviour-Driven Development
 
Lifecycle:
After:
Outcome: ANY   
Given User logs out



Scenario: 1. Root logs in first time

Given User logs in first time as Root
When User changes old password to new
Then User is on Home page


Scenario: 2. Root creates default Study Admin

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default Study Admin
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as Study Admin
When User changes old password to new
Then User is on Home page


Scenario: 3. Root creates default Study Monitor

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default Study Monitor
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as Study Monitor
When User changes old password to new
Then User is on Home page


Scenario: 4. Root creates default Sites

Given User logs in as Root
And User goes to Build Study page
And User is on Build Study page
And User clicks 'Add Site' button
And User is on Create a New Site page
And User fills in data to create site:
| Site Name | Unique Protocol ID | Principal Investigator | Expected total enrollment | Interview Date Default | Collect Interview Date | Interviewer Name Default | Collect Interviewer Name | Collect Person ID |
|<Site Name>|<Unique Protocol ID>|<Principal Investigator>|<Expected total enrollment>|<Interview Date Default>|<Collect Interview Date>|<Interviewer Name Default>|<Collect Interviewer Name>|<Collect Person ID>|
And User clicks 'Continue' button
And User is on Confirm Site page
When User clicks 'Submit' button
Then User is on Manage Sites page

Examples:
{scope=Scenario}
|<Site Name>|<Unique Protocol ID>|<Principal Investigator>|<Expected total enrollment>|<Interview Date Default>|<Collect Interview Date>|<Interviewer Name Default>|<Collect Interviewer Name>|<Collect Person ID>|
|Test Site1 |TS1                 |Mark Smit               |150                        |blank                   |no                      |blank                     |no                        |optional           |
|Test Site2 |TS2                 |Mark Smit               |150                        |blank                   |no                      |blank                     |no                        |optional           |


Scenario: 5. Root creates default Investigator

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default PI
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as PI
When User changes old password to new
Then User is on Home page


Scenario: 6. Root creates default CRC

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default CRC
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as CRC
When User changes old password to new
Then User is on Home page


Scenario: 7. Root creates default Site Monitor

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default Site Monitor
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as Site Monitor
When User changes old password to new
Then User is on Home page


Scenario: 8. Root changes System properties and Study Details to enable Medical Coding and CRF Evaluation

Given User logs in as Root
And User is on Home page
And User goes to Configure System Properties page
And User fills in Configure System Properties page:
|Allow CRF evaluation|
|yes                 |

And User clicks 'Continue' button
And User is on Confirm System Properties page
And User clicks 'Submit' button
And User is on Configure System Properties page
And User goes to Build Study page
And User sets Study status to 'Design'
And User clicks 'Update Study' button
And User fills in Update Study Details page:
|Brief Summary|Expected total enrollment|Allow CRF evaluation|Allow medical coding|Auto-code Dictionary Name|
|summary...   |144                      |yes                 |yes                 |alias                    |

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'


Scenario: 9. Root creates default Study Coder

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default Study Coder
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as Study Coder
When User changes old password to new
Then User is on Home page


Scenario: 10. Root creates default Study Evaluator

Given User logs in as Root
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default Study Evaluator
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as Study Evaluator
When User changes old password to new
Then User is on Home page


Scenario: 11. Root uploads CRFs

Given User logs in as Root
And User goes to Build Study page
And User clicks 'Add CRF' button
And User browses file on Create a New CRF page: <filepath>
And User clicks 'Continue' button on Create a New CRF page
And User is on Preview CRF page
When User clicks 'Submit' button
Then User is on Create a New CRF Version - Data Committed Successfully page
Examples:
{scope=Scenario}
|filepath                                                |
|.\\src\\test\\resources\\eCRFs\\CRF_w_basic_fields_1.xls|
|.\\src\\test\\resources\\eCRFs\\CRF_w_file_1.xls        |
|.\\src\\test\\resources\\eCRFs\\CRF_w_group_1.xlsx       |