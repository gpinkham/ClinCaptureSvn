Scenario: 1. "Study Admin" sets study properties "Collect Event Location" - "optional", "Collect Start Date" - "no", "Collect Stop Date" - "no"
Meta: @id1 scenario_1

Given User logs in as "Study Admin"
And User changes Study properties:
|Collect Event Location|Collect Start Date|Collect Stop Date|
|optional              |no                |no               |

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'


Scenario: 2. "Study Admin" sets study properties "Interviewer Name", "Interview Date" to optional
Meta: @id2 scenario_2

Given User logs in as "Study Admin"
And User changes Study properties:
|Collect Interviewer Name|Collect Interview Date|
|no                      |no                    |

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'


Scenario: 3. "Study Admin" sets study properties "Interviewer Name", "Interview Date" to required
Meta: @id3 scenario_3

Given User logs in as "Study Admin"
And User changes Study properties:
|Collect Interviewer Name|Collect Interview Date|
|yes                     |yes                   |

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'