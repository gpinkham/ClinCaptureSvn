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



Scenario: 1. "Root" logs in first time

Given User logs in first time as "Root"
When User changes old password to new
Then User is on Home page


Scenario: 2. "Root" creates default "Study Admin"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "Study Admin"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "Study Admin"
When User changes old password to new
Then User is on Home page


Scenario: 3. "Root" creates default "Study Monitor"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "Study Monitor"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "Study Monitor"
When User changes old password to new
Then User is on Home page


Scenario: 4. "Root" creates default Sites

Given User logs in as "Root"
And User goes to Build Study page
And User is on Build Study page
And User clicks 'Add Site' button on Build Study page
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


Scenario: 5. "Root" creates default "Investigator"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "PI"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "PI"
When User changes old password to new
Then User is on Home page


Scenario: 6. "Root" creates default "CRC"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "CRC"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "CRC"
When User changes old password to new
Then User is on Home page


Scenario: 7. "Root" creates default "Site Monitor"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "Site Monitor"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "Site Monitor"
When User changes old password to new
Then User is on Home page


Scenario: 8. "Root" changes System properties and Study Details to enable 'Medical Coding' and 'CRF Evaluation'

Given User logs in as "Root"
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
And User clicks 'Update Study' button on Build Study page
And User fills in Update Study Details page:
|Brief Summary|Expected total enrollment|Allow CRF evaluation|Allow medical coding|Auto-code Dictionary Name|How to Generate Subject ID|Collect Date of Enrollment for Study|Collect Gender|Collect Subject Date of Birth|Collect Person ID|
|summary...   |144                      |yes                 |yes                 |alias                    |manual                    |no                                  |true          |1                            |optional         |

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'


Scenario: 9. "Root" creates default "Study Coder"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "Study Coder"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "Study Coder"
When User changes old password to new
Then User is on Home page


Scenario: 10. "Root" creates default "Study Evaluator"

Given User logs in as "Root"
And User is on Home page
And User goes to Administer Users page
And User clicks 'Create User' button
And User fills in data on Create User Account page to create default "Study Evaluator"
And User clicks 'Submit' button
And User is on View User Account page
And User remembers password of created user
And User logs out
And User logs in first time as "Study Evaluator"
When User changes old password to new
Then User is on Home page


Scenario: 11.1 "Root" uploads CRF

Given User logs in as "Root"
And User goes to Build Study page
And User clicks 'Add CRF' button on Build Study page
And User browses file on Create a New CRF page: <filepath>
And User clicks 'Continue' button on Create a New CRF page
And User is on Preview CRF page
When User clicks 'Submit' button
Then User is on Create a New CRF Version - Data Committed Successfully page
Examples:
{scope=Scenario}
|filepath                                                |
|.\\src\\test\\resources\\eCRFs\\CRF_w_basic_fields_1.xls|


Scenario: 11.2 "Root" uploads CRFs

Given User logs in as "Root"
And User goes to Administer CRFs page
When User uploads CRFs: 
|filepath                                                |
|.\\src\\test\\resources\\eCRFs\\CRF_w_basic_fields_2.xls|
|.\\src\\test\\resources\\eCRFs\\CRF_w_basic_fields_3.xls|
|.\\src\\test\\resources\\eCRFs\\CRF_w_file_1.xls        |
|.\\src\\test\\resources\\eCRFs\\CRF_w_group_1.xlsx      |
|.\\src\\test\\resources\\eCRFs\\CRF_w_sections_1.xls    |

Then CRFs are uploaded


Scenario: 12.1 "Root" creates Study Event Definitions

Given User logs in as "Root"
And User goes to Build Study page
And User is on Build Study page
And User clicks 'Add Event Definitions' button on Build Study page
And User is on Create Study Event Definition page
And User fills in data to create study event definition:
| Name | Description | Type | Category | Repeating | Reference Event | Day Schedule | Day Max | Day Min | Day Email | User Name |
|<Name>|<Description>|<Type>|<Category>|<Repeating>|<Reference Event>|<Day Schedule>|<Day Max>|<Day Min>|<Day Email>|<User Name>|
And User clicks 'Continue' button
And User is on Define Study Event - Select CRF(s) page
And User selects CRFs on Define Study Event page: 
| eCRFs |
|<eCRFs>|
And User clicks 'Continue' button
And User is on Define Study Event - Selected CRF(s) page
And User clicks 'Continue' button
And User is on Confirm Event Definition Creation page
When User clicks 'Submit' button
Then User is on Create Study Event Definition page

Examples:
{scope=Scenario}
|<Name> |<Description>|<Type>     |<Category>|<Repeating>|<eCRFs>                           |
|Event A|             |Unscheduled|          |No         |CRF_w_basic_fields_1, CRF_w_file_1|


Scenario: 12.2 "Root" creates Study Event Definitions

Given User logs in as "Root"
And User goes to Create Study Event Definition page
When User creates study event definitions:
|Name   |Description|Type       |Category|Repeating|Reference Event|Day Schedule|Day Max|Day Min|Day Email|User Name|eCRFs                                     |
|Event B|           |Unscheduled|        |No       |               |            |       |       |         |         |CRF_w_basic_fields_1, CRF_w_group_1       |
|Event C|           |Unscheduled|        |No       |               |            |       |       |         |         |CRF_w_group_1, CRF_w_file_1               |
|Event D|           |Unscheduled|        |No       |               |            |       |       |         |         |CRF_w_basic_fields_2, CRF_w_sections_1    |
|Event E|           |Unscheduled|        |No       |               |            |       |       |         |         |CRF_w_basic_fields_2, CRF_w_basic_fields_3|
|Event F|           |Unscheduled|        |No       |               |            |       |       |         |         |CRF_w_group_1, CRF_w_sections_1           |

Then Study event definitions are created


Scenario: 13.1 "CRC" creates subject

Given User logs in as "CRC"
And User goes to Add Subject page
And User fills in data on Add Subject page to create subject:
| Study Subject ID | Person ID | Secondary ID | Date of Enrollment for Study | Gender | Date of Birth | Dynamic Group | 
|<Study Subject ID>|<Person ID>|<Secondary ID>|<Date of Enrollment for Study>|<Gender>|<Date of Birth>|<Dynamic Group>|
When User clicks 'Submit' button on Add Subject page
Then User is on SM page

Examples:
{scope=Scenario}
|<Study Subject ID>|<Person ID>|<Secondary ID>|<Date of Enrollment for Study>|<Gender>|<Date of Birth>|<Dynamic Group>|
|StSubj_1          |ss_1       |              |                              |Male    |03-Mar-1985    |               |


Scenario: 13.2 "CRC" creates subjects

Given User logs in as "CRC"
And User goes to Add Subject page
When User creates subjects:
|Study Subject ID|Person ID|Secondary ID|Date of Enrollment for Study|Gender|Date of Birth|Dynamic Group| 
|StSubj_2        |ss_2     |            |                            |Female|04-Jul-1987  |             |
|StSubj_3        |ss_3     |            |                            |Male  |16-Mar-1983  |             |
|StSubj_4        |ss_4     |            |                            |Female|06-Apr-1982  |             |
|StSubj_5        |ss_5     |            |                            |Female|23-Mar-1984  |             |
|StSubj_6        |ss_6     |            |                            |Male  |27-May-1986  |             |

Then Study subjects are created


Scenario: 14.1 "CRC" schedules event for subject

Given User logs in as "CRC"
And User goes to SM page
And User calls a popup for "StSubj_2", "Event C"
And User fills in popup to schedule event:
|Start Date/Time|End Date/Time|
|               |             |
When User clicks 'Schedule Event' button on popup
Then User is on SM page
And Event is scheduled


Scenario: 14.2 "CRC" schedules events for subjects

Given User logs in as "CRC"
And User goes to SM page
When User schedules events on SM:
|Study Subject ID|Event Name                        |
|StSubj_1        |Event A, Event C, Event B, Event E|
|StSubj_2        |Event A, Event B                  |
|StSubj_3        |Event B, Event F                  |
|StSubj_4        |Event A, Event D, Event E         |
|StSubj_5        |Event B, Event D, Event E, Event F|
|StSubj_6        |Event C, Event E, Event F         |

Then Events are scheduled


Scenario: 15. "Study Admin" changes Study properties to disable 'Collect Interviewer Name', 'Collect Interview Date' and 'use Autotabbing' properties

Given User logs in as "Study Admin"
And User changes Study properties:
|Collect Interviewer Name|Collect Interview Date|Use autotabbing|
|no                      |no                    |no             |

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'


Scenario: 16.1 "CRC" enters data into CRF for one subject

Given User logs in as "CRC"
And User goes to SM page
And User calls a popup for "StSubj_1", "Event B"
And User clicks 'Enter Data' button in popup for "CRF_w_basic_fields_1"
And User fills in data into CRF:
|Mark Complete|input1(T)  |input2(T)|input3(R)|input4(T)  |input5(R)|
|no           |20-Apr-2015|22:00    |1        |description|0        |

When User clicks 'Save' button
Then User is on SM page


Scenario: 16.2 "CRC" enters data into CRF and completes it for some subjects

Given User logs in as "CRC"
And User goes to SM page
When User fills in, completes and saves CRF: 
|Study Subject ID|Event Name|CRF Name            |Mark Complete|input1(T)  |input2(T)|input3(R)|input4(T)|input5(R)|
|StSubj_2        |Event B   |CRF_w_basic_fields_1|yes          |24-Apr-2015|17:45    |1        |some text|0        |
|StSubj_3        |Event B   |CRF_w_basic_fields_1|yes          |21-Apr-2014|12:00    |0        |         |1        |
|StSubj_1        |Event B   |CRF_w_basic_fields_1|yes          |           |         |         |         |         |
|StSubj_1        |Event B   |CRF_w_basic_fields_1|yes          |           |         |         |         |         |

Then User is on SM page


Scenario: 16.3 "CRC" enters data into CRFs and completes it for some subjects

Given User logs in as "CRC"
And User goes to SM page
When User fills in, completes and saves CRF: 
|Study Subject ID|Event Name|CRF Name            |Mark Complete|item1                                          |item2                                          |item3           |item4                 |item5        |
|StSubj_1        |Event B   |CRF_w_group_1       |yes          |IG_CRF_W_CONCOMITANTMEDICATIONS_0input35(T): 21|IG_CRF_W_CONCOMITANTMEDICATIONS_0input36(T): 22|                |                      |             |
|StSubj_2        |Event B   |CRF_w_group_1       |yes          |IG_CRF_W_CONCOMITANTMEDICATIONS_0input35(T): 14|IG_CRF_W_CONCOMITANTMEDICATIONS_0input36(T): 15|                |                      |             |
|StSubj_2        |Event C   |CRF_w_group_1       |no           |IG_CRF_W_CONCOMITANTMEDICATIONS_0input35(T): 34|IG_CRF_W_CONCOMITANTMEDICATIONS_0input36(T): 25|                |                      |             |
|StSubj_3        |Event B   |CRF_w_group_1       |yes          |IG_CRF_W_CONCOMITANTMEDICATIONS_0input35(T): 83|IG_CRF_W_CONCOMITANTMEDICATIONS_0input36(T): 12|                |                      |             |
|StSubj_1        |Event A   |CRF_w_basic_fields_1|no           |input1(T): 13-Apr-2014                         |input2(T): 22:45                               |input3(R): 1    |input4(T): discrepancy|input5(R): 1 |
|StSubj_4        |Event E   |CRF_w_basic_fields_2|yes          |input6(R): 1                                   |input7(T): 17-Feb-2013                         |input8(T): 13:35|input9(R): 1          |input15(R): 1|
|StSubj_4        |Event E   |CRF_w_basic_fields_3|yes          |input31(T): 18-May-2014                        |input32(T): 13:22                              |input33(T): 100 |                      |             |
|StSubj_5        |Event F   |CRF_w_group_1       |yes          |IG_CRF_W_CONCOMITANTMEDICATIONS_0input35(T): 54|IG_CRF_W_CONCOMITANTMEDICATIONS_0input36(T): 15|                |                      |             |
|StSubj_5        |Event E   |CRF_w_basic_fields_2|yes          |input6(R): 1                                   |input7(T): 27-Feb-2013                         |input8(T): 17:25|input9(R): 0          |input15(R): 1|
|StSubj_5        |Event E   |CRF_w_basic_fields_3|yes          |input31(T): 28-May-2013                        |input32(T): 11:24                              |input33(T): 23  |                      |             |

Then User is on SM page


Scenario: 17. "Study Monitor" performs SDV on SDV page
 
Given User logs in as "Study Monitor"
And User goes to SDV page
When User filters SDV table and performs SDV: 
|Study Subject ID|Event Name|CRF Name                                  |
|StSubj_2        |Event B   |CRF_w_basic_fields_1, CRF_w_group_1       |
|StSubj_1        |Event B   |CRF_w_basic_fields_1, CRF_w_group_1       |
|StSubj_5        |Event F   |CRF_w_group_1                             |
|StSubj_4        |Event E   |CRF_w_basic_fields_2, CRF_w_basic_fields_3|
 
Then CRFs are SDVed


Scenario: 18.1 "PI" signs event
 
Given User logs in as "PI"
And User goes to SM page
And User calls a popup for "StSubj_2", "Event B"
And User clicks 'Sign Event' button on popup
And User is on Sign Study Event page
And User enters credentials on Sign Study Event page
When User clicks 'Sign' button on Sign Study Event page
Then User is on View Subject Record page


Scenario: 18.2 "PI" signs events
 
Given User logs in as "PI"
And User goes to SM page
When User filters SM table and signs events: 
|Study Subject ID|Event Name|
|StSubj_1        |Event B   |
|StSubj_4        |Event E   |
 
Then Events are signed


Scenario: 19. "Root" uploads rule

Given User logs in as "Root"
And User goes to Manage Rules page
And User is on Manage Rules page
And User clicks 'Import Rules' button on Manage Rules page
And User is on Import Rule Data page
And User browses file on Import Rule Data page: <filepath>
And User clicks 'Continue' button on Import Rule Data page
And User is on Import Rule Data page
When User clicks 'Submit' button
And User clicks 'Yes' button in popup on Import Rule Data page
Then User is on Build Study page
And User sees '1 Rule(s) and 1 Rule Assignment(s) were uploaded successfully.' message
Examples:
|filepath                                  |
|.\\src\\test\\resources\\Rules\\Rule_1.xml|


Scenario: 20.1 "Study Admin" creates DN in CRF

Given User logs in as "Study Admin"
And User goes to SM page
When User creates DNs for the items from CRF: 
|Study Subject ID|Event Name|CRF Name            |Item  |Type      |Description|Detailed Note   |Assign to User|Email Assigned User|
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input1|Annotation|test DN 1  |peace of text...|              |                   |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input2|Query     |test DN 2  |peace of text...|demo_pi       |                   |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input3|Query     |test DN 3  |peace of text...|demo_crc      |                   |

Then DNs are created


Scenario: 20.2 "Study Admin" begins new thread DN in CRF

Given User logs in as "Study Admin"
And User goes to SM page
When User creates DNs for the items from CRF: 
|Study Subject ID|Event Name|CRF Name            |Item  |Type      |Description         |Detailed Note   |Assign to User|Email Assigned User|
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input3|Annotation|test new thread DN 1|peace of text...|              |                   |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input1|Query     |test new thread DN 2|peace of text...|demo_crc      |                   |

Then DNs are created


Scenario: 20.3 "Study Admin" updates Queries

Given User logs in as "Study Admin"
And User goes to SM page
When User updates Query DNs for the items from CRF: 
|Study Subject ID|Event Name|CRF Name            |Item  |Parent ID|Parent Description  |Parent Detailed Note|Description                  |Detailed Note   |Resolution Status|Assign to User|Email Assigned User|
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input1|9        |test new thread DN 2|peace of text...    |Other                        |peace of text...|Updated          |demo_crc      |                   |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input3|5        |test DN 3           |peace of text...    |Need additional clarification|peace of text...|Updated          |demo_pi       |                   |

Then DNs are updated


Scenario: 20.4 "Study Admin" closes Queries

Given User logs in as "Study Admin"
And User goes to SM page
When User closes Query DNs for the items from CRF: 
|Study Subject ID|Event Name|CRF Name            |Item  |Parent ID|Parent Description  |Parent Detailed Note|Description              |Detailed Note   |Resolution Status|Assign to User|Email Assigned User|
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input1|9        |test new thread DN 2|peace of text...    |Other                    |peace of text...|Closed           |demo_crc      |                   |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|input2|3        |test DN 2           |peace of text...    |CRF data change monitored|peace of text...|Closed           |demo_pi       |                   |

Then DNs are closed


Scenario: 20.5 "CRC" enters data into completed CRF and creates RFC DN

Given User logs in as "CRC"
And User goes to SM page
And User fills in CRF: 
|Study Subject ID|Event Name|CRF Name            |input1(T)  |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|27-Apr-2015|

And User creates DNs in CRF: 
|Item  |Type|Description            |Detailed Note   |
|input1|RFC |Source data was missing|peace of text...|

When User clicks 'Save' button
Then User is on SM page
And DNs are created


Scenario: 20.6 "CRC" enters data into completed CRF, clicks 'Save' button and creates RFC DN

Given User logs in as "CRC"
And User goes to SM page
And User fills in CRF: 
|Study Subject ID|Event Name|CRF Name            |input2(T)  |
|StSubj_2        |Event B   |CRF_w_basic_fields_1|13:15      |

And User clicks 'Save' button
And User creates DNs in CRF: 
|Item  |Type|Description                  |Detailed Note   |
|input2|RFC |Information was not available|peace of text...|

When User clicks 'Save' button
Then User is on SM page
And DNs are created


Scenario: 20.7 "CRC" enters data into CRF, clicks 'Save' button and creates FVC DN

Given User logs in as "CRC"
And User goes to SM page
And User fills in CRF: 
|Study Subject ID|Event Name|CRF Name            |input6(R)|input7(T)  |input8(T)|input9(R)|input11(R)|
|StSubj_1        |Event E   |CRF_w_basic_fields_2|1        |05-Feb-2014|00:00    |1        |0         |

And User clicks 'Save' button
And User creates DNs in CRF: 
|Item  |Type|Description                  |Detailed Note   |
|input8|FVC |Information was not available|peace of text...|

When User clicks 'Save' button
Then User is on SM page
And DNs are created


Scenario: 20.8 "CRC" enters wrong data into CRF, clicks 'Save' button twice and CC creates auto FVC

Given User logs in as "CRC"
And User goes to SM page
And User fills in CRF: 
|Study Subject ID|Event Name|CRF Name            |input6(R)|input7(T)  |input8(T)|input9(R)|input11(R)|
|StSubj_5        |Event D   |CRF_w_basic_fields_2|1        |20-Apr-2015|00:00    |2        |0         |

And User clicks 'Save' button
When User clicks 'Save' button
Then User is on SM page
And DN is created:
|Study Subject ID|Event Name|CRF Name            |Type|Detailed Note|Entity Name|
|StSubj_5        |Event D   |CRF_w_basic_fields_2|FVC |             |           |


Scenario: 20.9 "CRC" enters data into completed CRF, clicks 'Save' button and creates RFC DN instead of FVC

Given User logs in as "CRC"
And User goes to SM page
And User fills in CRF: 
|Study Subject ID|Event Name|CRF Name            |input8(T)|
|StSubj_5        |Event E   |CRF_w_basic_fields_2|00:00    |

And User clicks 'Save' button
And User creates DNs in CRF: 
|Study Subject ID|Item  |Type|Description            |Detailed Note    |
|StSubj_5        |input8|RFC |Source data was missing|Scenario 20.9 ...|

When User clicks 'Save' button
Then User is on SM page
And DNs are created


Scenario: 20.10 "Study Admin" creates DNs for Study Event Definition 

GivenStories: com.clinovo.stories/basic_wf/Preconditions.story#{0}

Given User goes to SM page
When User creates DNs for Events using popup:
|Study Subject ID|Event Name|Type      |Description|Detailed Note    |Entity Name|
|StSubj_5        |Event C   |Query     |event_dn_1 |Scenario 20.10...|Start Date |
|                |          |Annotation|event_dn_2 |Scenario 20.10...|End Date   |
|                |          |Query     |event_dn_3 |Scenario 20.10...|Location   |
|StSubj_3        |Event C   |Query     |event_dn_1 |Scenario 20.10...|Start Date |

Then DNs are created
 
Examples:
|<Collect Event Location>|<Collect Start Date>|<Collect Stop Date>|<Collect Interviewer Name>|<Collect Interview Date>|<Use autotabbing>|
|optional                |no                  |no                 |                          |                        |                 |


Scenario: 20.11 "Study Admin" creates DNs for Study Subject 

Given User logs in as "Study Admin"
And User goes to SM page
And User clicks 'View' icon for StSubj_5 on SM page
And User is on View Subject Record page
And User clicks 'Study Subject Record' link on View Subject Record page
When User creates DNs for Study Subject:
|Type |Description |Detailed Note    |Entity Name    |
|Query|st_subj_dn_1|Scenario 20.11...|Enrollment Date|

Then DNs are created


Scenario: 20.12 "Study Admin" creates DNs for Subject

Given User logs in as "Study Admin"
And User goes to Administer Subjects page
And User clicks 'Edit' icon for TS1-StSubj_3 on Administer Subjects page
And User is on Update Subject Details page
When User creates DNs for Subject:
|Type      |Description |Detailed Note    |Entity Name      |
|Query     |subj_dn_1   |Scenario 20.12...|Unique Identifier|
|Annotation|subj_dn_2   |Scenario 20.12...|Sex              |
|Query     |subj_dn_3   |Scenario 20.12...|Date of Birth    |

Then DNs are created


