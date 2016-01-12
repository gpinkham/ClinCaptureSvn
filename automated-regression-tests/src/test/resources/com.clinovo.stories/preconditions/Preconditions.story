Scenario: 1. "Study Admin" changes Study properties
Meta: @id1 scenario1

Given User logs in as "Study Admin"
And User changes Study properties:
| Collect Event Location | Collect Start Date | Collect Stop Date | Collect Interviewer Name | Collect Interview Date | Use autotabbing |
|<Collect Event Location>|<Collect Start Date>|<Collect Stop Date>|<Collect Interviewer Name>|<Collect Interview Date>|<Use autotabbing>|

When User clicks 'Submit' button
Then User is on Build Study page
And User sets Study status to 'Available'