--------------------------------------------------------------
CLINCAPTURE RELEASE NOTES
ClinCapture Version 1.0.2
Document Version 1.0
--------------------------------------------------------------
Created 26-Jan-2013 by Tom Hickerson tom.hickerson@clinovo.com
--------------------------------------------------------------

Welcome to the first public release of ClinCapture.  This version 1.0.2 is based
on the OpenClinica code base of 3.1.2, and contains many of the features in 
OpenClinica 3.1.3.  It also contains many bug fixes and enhancements contributed from
Clinovo.

The most notable enhancements unique to ClinCapture include:
- One-click access to data entry screens, scheduling, SDV and signing from Subject Matrix
- LiveReports: Integration with the Pentaho Business Intelligence Server
-Wider browsers support 
-Direct access to ClinCapture’s community and forums
-Security enhancements:
- Account administrative lockout after a configurable period of inactivity
-Configurable password re-use policy
- Streamlining and simplification of the Discrepancy Notes interface and reporting logic
- Simplification of user roles between Sites and Studies
- An updated, intuitive Graphical User Interface (GUI)
- Expansion of dynamic CRFs, including Javascript calculations in CRFs
- Expanded Study Properties, allowing for further customization of CRF and Subject display
- Inclusion of many of the OpenClinica 3.1.3 features, such as: 
	- CRF Version Migration 
	- Rules running during Import Data
	- Options to require strong passwords
	- Referencing a CRF through an ODM-based URL
- Numerous bug fixes to the code base, including:
	- Reconciling Discrepancy Notes and Repeating Rows in CRF Data Entry
	- Bug fixes for i18n issues
	- Bug fixes for showing event status incorrectly
	- Bug fixes for scheduling Import Jobs and running Rules in Data Entry
- Expansion of an automated testing framework using Selenium and unit tests
	
The core, web and web services modules are contained in separate modules, but this document serves as the main 'readme' for each one.
 
Contents:

1. System Requirements
2. Overall Feature Set
3. Known Issues
4. Installing ClinCapture
5. Upgrading ClinCapture from OpenClinica 3.1.2
6. Technical Support and Troubleshooting
7. Contacting Clinovo Inc.
8. GNU LGPL License

1. SYSTEM REQUIREMENTS:
ClinCapture is currently tested and developed on the following platforms:

Server Operating Systems:

Redhat Enterprise Linux 4.0+
Windows Server 2003

Databases:
Postgres 8.4
Oracle 10g

Client Browsers:
Internet Explorer 8 or 9
Mozilla Firefox 
Chrome

Web Server:
Apache Jakarta Tomcat 6.0.x
Java: Java 2 SDK 1.6.x or 1.7.x

Hardware Requirements:
Memory: 256 MB minimum (2 GB Recommended)
Disk Space: 500 MB minimum (2 GB Recommended)

NOTE that ClinCapture can run on top of Postgres 9.x, but you will need to switch out the JDBC driver library in the ClinCapture/WEB-INF/lib folder under Tomcat.
Ideally, you will want to download the JDBC4 jar file from the site http://jdbc.postgresql.org/download.html.  This database version is not included in the current test plan, but will be in the near future.

We have been testing ClinCapture with Tomcat 7, but have encountered technical issues with its implementation, and hope to fix this in a future release.

The source code has been removed from the distribution package to make it easier to navigate the file structure.  To access the source code, please visit http://www.clinovo.com/clincapture/community.

2. OVERALL FEATURES SET:
The main functionality includes:
* Submit Data: Allows subject enrollment, data submission and validation for use by clinicians and research associates as well as Query Management and Bulk Data Import.
* Monitor and Manage Data: Enables ongoing data management and monitoring
* Extract Data: Enables data extraction and filtering of datasets for use by investigators and principal investigators.
* Study Build: Facilitates creation and management of studies (protocols), sites, CRFs, users and study event definitions by principal investigators and coordinators.
* Administration: Allows overall system oversight, auditing, configuration, and reporting by administrators.

Some key features of ClinCapture include: 

* Organization of clinical research by study protocol and site, each with its own set of authorized users, subjects, study event definitions, and CRFs. Support for sharing resources across studies in a secure and transparent manner. 
* Dynamic generation of web-based CRFs for electronic data capture via user-defined clinical parameters and validation logic specified in portable Excel templates.
* Management of longitudinal data for complex and recurring patient visits.
* Data import/export tools for migration of clinical datasets in excel spreadsheets, local databases and legacy data formats.
* Extensive interfaces for data query and retrieval, across subjects, time, and clinical parameters, with dataset export in common statistical analysis formats. 
* Compliance with 21 CFR Part 11 and HIPAA privacy and security guidelines including use of study-specific user roles and privileges, SSL encryption, and auditing to monitor access and changes by users.
* A robust and scalable technology infrastructure developed using the Java J2EE framework interoperable with relational databases including PostgreSQL (open source) and Oracle 10G, to support the needs of the clinical research enterprise. 

3. KNOWN ISSUES: 
Known issues that are currently being fixed can be viewed in the Trac issue manager, available through http://www.clinovo.com/clincapture/community.

Currently known issues include the following:

- Tomcat memory leaks do persist when the ClinCapture application is re-deployed (Trac #171)
- If a CRF required extra HTML in generation for Submit Data, this extra HTML is shown when printing the CRF (Trac #134)
- Certain encoding problems searching on Rules in a CRF with non-Latin characters in the CRF Name (Trac #149)
- CRF Version Migration does not show existing data being transferred back into the CRF, if it was there originally (Trac #198)

4. INSTALLING CLINCAPTURE:
Installation procedures are documented in the ClinCapture Administrator’s Guide.
Please visit http://www.clinovo.com/clincapture/community to download it.

5. UPGRADING FROM OPENCLINICA 3.1.X TO CLINCAPTURE 1.0.2:
Installation procedures are documented in the ClinCapture Administrator’s Guide.
Please visit http://www.clinovo.com/clincapture/community to download it.

6. TECHNICAL ISSUES AND TROUBLESHOOTING:
If you don't see an issue that is addressed below, we certainly recommend posting a question to the forums through http://www.clinovo.com/clincapture/forum.

- Starting up the application

The first time you start up, ClinCapture will look for the correct database URL, username and password that is set in the datainfo.properties file.  If that is not set correctly, ClinCapture will not be available when you start Tomcat.  Ways to determine if this is the case is to look in your Tomcat logs, especially the log files entitled 'clincapture' and 'localhost', usually suffixed with today's dates.  The log files are typically located under $CATALINA_HOME/logs.

Once you have the correct database properties set, ClinCapture will connect to the database and make sure that all the tables and other database objects are created.
It does this by using a software library called Liquibase (http://www.liquibase.org).  Liquibase will generate all the tables and then make sure this 'state' is stored in the database.  It will also 'lock' the database while it is performing this operation.  Often, if you crash while doing this, starting over again will require you to manually unlock the database by erasing all information in the table called 'databasechangeloglock'.  You can tell if you are in a locked state if your Tomcat startup prints the following line: "Waiting for database..." multiple times.

- Running the application

If you are running the application on your own server, please keep in mind that ClinCapture runs on top of the Java Virtual Machine, which is set with low memory requirements by default.  A number of operations can take your application over that limit, and may cause it to crash.  We recommend running your JVM with the memory settings (usually in your JAVA_OPTS variable, which is set in your Environment Variables in Windows):

-Xms128m -Xmx1024m -XX:MaxPermSize=512m -XX:MaxHeapSize=512m

There are a number of opinions about JVM memory management on the Internet; some worth checking out include the article on OpenClinica memory management here: 

http://en.wikibooks.org/wiki/OpenClinica_User_Manual/OutOfMemoryError

- Maintaining the application

ClinCapture is a moderately complex application with many features and moving parts; we strongly recommend reviewing some training materials before getting started with the application install, administration, and daily usage.  Also, as it is an application which may handle sensitive and important data within your organization, we strongly recommend working with your local IT staff to determine the best way to host and operate your instance of ClinCapture securely and safely.  

Things to keep in mind include:

- Regular backups of your application
- Secure access to your physical servers
- Ways you will organize your patients' IDs and Secondary IDs
- How you will organize patient visits and CRFs
- How best to submit your data (CRF data entry, automated import, etc)
- How best to organize and run your edit checks
- What kind of reporting you will require on the data
- What kind of exports you will need to run
- Which Live Reports to develop

Clinovo can assist you with all these points, and more.

7. CONTACTING CLINOVO INC.

Please feel free to reach out to us either through the Contact page within ClinCapture, or our website at http://www.clinovo.com.

Our mailing address and phone are the following:
1208 East Arques Avenue
Sunnyvale, CA 94085
(408) 773-6251
(888) 317-7517

8. GNU LGPL LICENSE:

ClinCapture is distributed under the GNU Lesser General Public License (GNU LGPL), summarized in the Creative Commons text here:

http://creativecommons.org/licenses/LGPL/2.1/

The GNU Lesser General Public License is a Free Software license. Like any Free Software license, it grants to you the four following freedoms:

1. The freedom to run the program for any purpose.
2. The freedom to study how the program works and adapt it to your needs.
3. The freedom to redistribute copies so you can help your neighbor.
4. The freedom to improve the program and release your improvements to the public, so that the whole community benefits.

You may exercise the freedoms specified here provided that you comply with the express conditions of this license. The LGPL is intended for software libraries, rather than executable programs.

The principal conditions are:

* You must conspicuously and appropriately publish on each copy distributed an appropriate copyright notice and disclaimer of warranty and keep intact all the notices that refer to this License and to the absence of any warranty; and give any other recipients of the Program a copy of the GNU Lesser General Public License along with the Program. Any translation of the GNU Lesser General Public License must be accompanied by the GNU Lesser General Public License.
* If you modify your copy or copies of the library or any portion of it, you may distribute the resulting library provided you do so under the GNU Lesser General Public License. However, programs that link to the library may be licensed under terms of your choice, so long as the library itself can be changed. Any translation of the GNU Lesser General Public License must be accompanied by the GNU Lesser General Public License.
* If you copy or distribute the library, you must accompany it with the complete corresponding machine-readable source code or with a written offer, valid for at least three years, to furnish the complete corresponding machine-readable source code. You need not provide source code to programs which link to the library.

Any of these conditions can be waived if you get permission from the copyright holder.
Your fair use and other rights are in no way affected by the above.

For the full GNU LGPL License text, see LICENSE.txt included in this package.

--
