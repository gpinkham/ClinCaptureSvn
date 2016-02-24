<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:ODM="http://www.cdisc.org/ns/odm/v1.3" 
  xmlns:OC="http://www.openclinica.org/ns/odm_ext_v130/v3.1" 
  xmlns:O="http://www.cdisc.org/ns/odm/v1.3" 
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3c.org/2001/XMLSchema-instance"
  exclude-result-prefixes="O OC ODM xs xsi">

  <xsl:output method="text" encoding="utf-8" />

  <xsl:strip-space elements="*" />

  <xsl:variable name="TAB" select="'&#x09;'" />
  <xsl:variable name="LF"  select="'&#x0A;'" />

  <!-- sub-map: a list of extra fields for subjects. -->
  <xsl:variable name="sub-map">
    <xsl:for-each select="/ODM:ODM">
      <xsl:if test="ODM:ClinicalData/ODM:SubjectData/@OC:Sex">
        <sex />
      </xsl:if>
      <xsl:if test="ODM:ClinicalData/ODM:SubjectData/@OC:UniqueIdentifier">
        <primary-id />
      </xsl:if>
      <xsl:if test="ODM:ClinicalData/ODM:SubjectData/@OC:DateOfBirth">
        <date-of-birth />
      </xsl:if>
      <xsl:if test="ODM:ClinicalData/ODM:SubjectData/@OC:Status">
        <status />
      </xsl:if>
      <xsl:if test="ODM:ClinicalData/ODM:SubjectData/@OC:SecondaryID">
        <secondary-id />
      </xsl:if>
      <xsl:for-each-group select="ODM:ClinicalData/ODM:SubjectData/
          OC:SubjectGroupData" group-by="@OC:StudyGroupClassName">
        <group name="{current-grouping-key()}" />
      </xsl:for-each-group>
    </xsl:for-each>
  </xsl:variable>

  <!-- evt-map: a list of extra fields for events. -->
  <xsl:variable name="evt-map">
    <xsl:for-each select="/ODM:ODM/ODM:ClinicalData">
    	<xsl:if test="ODM:SubjectData/ODM:StudyEventData/@OC:Status">
        	<event-status />
      	</xsl:if>
      <xsl:if test="ODM:SubjectData/ODM:StudyEventData/
          @OC:StudyEventLocation">
        <location />
      </xsl:if>
       <xsl:if test="ODM:SubjectData/ODM:StudyEventData/@OC:StartDate">
        <start-date />
      </xsl:if>
       <xsl:if test="ODM:SubjectData/ODM:StudyEventData/@OC:EndDate">
        <end-date />
      </xsl:if>
       <xsl:if test="ODM:SubjectData/ODM:StudyEventData/@OC:SubjectAgeAtEvent">
        <subject-age />
      </xsl:if>
    </xsl:for-each>
  </xsl:variable>

  <!-- raw-map: a tree of actually used events, forms, groups, and items. The
  raw tree is not sorted and the repeated elements are not expanded. I use the
  raw tree to generate sorted “ord-map.” -->

  <xsl:variable name="raw-map">
    <!-- Use the first study as a reference. -->
    <xsl:variable name="ref" 
      select="/ODM:ODM/ODM:Study[1]/ODM:MetaDataVersion" />
    <xsl:for-each-group select="/ODM:ODM/ODM:ClinicalData/ODM:SubjectData/
        ODM:StudyEventData" group-by="@StudyEventOID">
      <xsl:variable name="eg" select="current-group()" />
      <xsl:variable name="ei" select="current-grouping-key()" />
      <xsl:variable name="ed" select="$ref/ODM:StudyEventDef[@OID = $ei]" />
      <xsl:if test="not(boolean($ed))">
        <xsl:message terminate="yes">
          <xsl:text>No definition for event </xsl:text>
          <xsl:value-of select="$ei" />
          <xsl:text>.</xsl:text>
        </xsl:message>
      </xsl:if>
      <xsl:variable name="er" select="$ed/@Repeating = 'Yes'" />
      <event id="{$ei}" name="{$ed/@Name}" repeat="{$er}"
          no="{count($ed/preceding-sibling::ODM:StudyEventDef) + 1}"
          max-rep="{if ($er) then max($eg/@StudyEventRepeatKey) else 1}">
        <xsl:for-each-group select="$eg/ODM:FormData" group-by="@FormOID">
          <xsl:variable name="fg" select="current-group()" />
          <xsl:variable name="fi" select="current-grouping-key()" />
          <xsl:variable name="fd" select="$ref/ODM:FormDef[@OID = $fi]" />
          <xsl:if test="not(boolean($fd))">
            <xsl:message terminate="yes">
              <xsl:text>No definition for form </xsl:text>
              <xsl:value-of select="$fi" />
              <xsl:text>.</xsl:text>
            </xsl:message>
          </xsl:if>
          <form id="{$fi}" name="{$fd/@Name}"
              no="{count($fd/preceding-sibling::ODM:FormDef) + 1}">
            <xsl:if test="$fg/@OC:InterviewerName">
              <field name="Interviewer Name" code="InterviewerName" />
            </xsl:if>
            <xsl:if test="$fg/@OC:InterviewDate">
              <field name="Interview Date" code="InterviewDate" />
            </xsl:if>
            <xsl:if test="$fg/@OC:Status">
              <field name="Status" code="Status" />
            </xsl:if>
            <xsl:for-each-group select="$fg/ODM:ItemGroupData" 
                group-by="@ItemGroupOID">
              <xsl:variable name="gg" select="current-group()" />
              <xsl:variable name="gi" select="current-grouping-key()" />
              <xsl:variable name="gd" 
                  select="$ref/ODM:ItemGroupDef[@OID = $gi]" />
              <xsl:if test="not(boolean($gd))">
                <xsl:message terminate="yes">
                  <xsl:text>No definition for group </xsl:text>
                  <xsl:value-of select="$gi" />
                  <xsl:text>.</xsl:text>
                </xsl:message>
              </xsl:if>
              <xsl:variable name="gr" select="$gd/@Repeating = 'Yes'" />
              <group id="{$gi}" repeat="{$gr}" no="{count($gd/
                  preceding-sibling::ODM:ItemGroupDef) + 1}" max-rep="{if 
                  ($gr) then max($gg/@ItemGroupRepeatKey) else 1}">
                <xsl:for-each-group select="$gg/ODM:ItemData" 
                    group-by="@ItemOID">
                  <xsl:variable name="ii" select="current-grouping-key()" />
                  <xsl:variable name="id"
                      select="$ref/ODM:ItemDef[@OID = $ii]" />
                  <xsl:if test="not(boolean($id))">
                    <xsl:message terminate="yes">
                      <xsl:text>No definition for item </xsl:text>
                      <xsl:value-of select="$ii" />
                      <xsl:text>.</xsl:text>
                    </xsl:message>
                  </xsl:if>
                  <item id="{$ii}" code="{$id/@Name}"
                      name="{$id/@Comment}" no="{count(
                      $id/preceding-sibling::ODM:ItemDef) + 1}"/>
                </xsl:for-each-group>
              </group>
            </xsl:for-each-group>
          </form>
        </xsl:for-each-group>
      </event>
    </xsl:for-each-group>
  </xsl:variable>

  <!-- ord-map: sorted “raw-map.” -->
  <xsl:variable name="ord-map">
    <xsl:for-each select="$raw-map/event">
      <xsl:sort select="@no" data-type="number" />
      <xsl:copy>
        <xsl:copy-of select="@*" />
        <xsl:for-each select="form">
          <xsl:sort select="@no" data-type="number" />
          <xsl:copy>
            <xsl:copy-of select="@* | field" />
            <xsl:for-each select="group">
              <xsl:sort select="@no" data-type="number" />
              <xsl:copy>
                <xsl:copy-of select="@*" />
                <xsl:for-each select="item">
                  <xsl:sort select="@no" data-type="number" />
                  <xsl:sequence select="." />
                </xsl:for-each>
              </xsl:copy>
            </xsl:for-each>
          </xsl:copy>
        </xsl:for-each>
      </xsl:copy>
    </xsl:for-each>
  </xsl:variable>

  <!-- ext-map: “ord-map,” with expanded repeating elements. -->
  <xsl:variable name="ext-map">

    <!-- Sort the raw map and expand repeating items. -->
    <xsl:for-each select="$ord-map/event">
      <xsl:variable name="ed" select="." />
      <xsl:variable name="er" select="@repeat = 'true'" />
      <xsl:for-each select="1 to xs:integer(@max-rep)">
        <xsl:variable name="en" select="." />
        <xsl:for-each select="$ed">
          <xsl:copy>
            <xsl:copy-of select="@*" />
            <xsl:if test="$er">
              <xsl:attribute name="rep" select="$en" />
            </xsl:if>
            <xsl:for-each select="form">
              <xsl:copy>
                <xsl:copy-of select="@*" />
                <xsl:attribute name="global-no"
                    select="count(preceding::form) + 1" />
                <xsl:copy-of select="field" />
                <xsl:for-each select="group">
                  <xsl:variable name="gd" select="." />
                  <xsl:variable name="gr" select="@repeat = 'true'" />
                  <xsl:for-each select="1 to xs:integer(@max-rep)">
                    <xsl:variable name="gn" select="." />
                    <xsl:for-each select="$gd">
                      <xsl:copy>
                        <xsl:copy-of select="@*" />
                        <xsl:if test="$gr">
                          <xsl:attribute name="rep" select="$gn" />
                        </xsl:if>
                        <xsl:for-each select="item">
                          <xsl:sequence select="." />
                        </xsl:for-each>
                      </xsl:copy>
                    </xsl:for-each>
                  </xsl:for-each>
                </xsl:for-each>
              </xsl:copy>
            </xsl:for-each>
          </xsl:copy>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:variable>

  <xsl:template match="/">

    <!-- Output the summary header. -->
    <xsl:for-each select="ODM:ODM">

      <!-- Dataset name. -->
      <xsl:text>Dataset Name:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="substring-before(@FileOID, concat('D',
          substring(@CreationDateTime, 1, 4)))" />
      <xsl:copy-of select="$LF" />

      <xsl:text>Dataset Description:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="@Description" />
      <xsl:copy-of select="$LF" />

      <!-- The data file normally includes results of multiple studies from
      different establishments. Each such a series makes a separate study and
      has a separate, but related title, e.g.:

        Study 1: BCD-017-3
        Study 2: BCD-017-3 - Arkhangelsky Regional Oncology Dispensary
        Study 3: BCD-017-3 - Volgograd Oncology Dispensary #1

      For our purposes we only need the general title (“BCD-017-3”) and we
      assume this is the title of the first Study in the list. -->

      <xsl:variable name="BaseStudy" select="ODM:Study[1]" />

      <xsl:text>Study Name:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="$BaseStudy/ODM:GlobalVariables/ODM:StudyName" />
      <xsl:copy-of select="$LF" />

      <xsl:text>Protocol ID:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="$BaseStudy/ODM:GlobalVariables/ODM:ProtocolName" />
      <xsl:copy-of select="$LF" />

      <xsl:text>Date:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="format-dateTime(xs:dateTime(@CreationDateTime), 
          '[Y0001]-[MNn,*-3]-[D01]', 'en', (), ())" />
      <xsl:copy-of select="$LF" />

      <xsl:text>Subjects:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="count(ODM:ClinicalData/ODM:SubjectData)" />
      <xsl:copy-of select="$LF" />

      <xsl:text>Study Event Definitions:  </xsl:text>
      <xsl:copy-of select="$TAB" />
      <xsl:value-of select="count($ord-map/event)" />
      <xsl:copy-of select="$LF" />

      <xsl:for-each select="$ext-map/event">
        <xsl:text>Study Event Definition </xsl:text>
        <xsl:value-of select="@no" />
        <xsl:if test="@repeat = 'true'">
          <xsl:text> (Repeating) </xsl:text>
        </xsl:if>
        <xsl:copy-of select="$TAB" />
        <xsl:value-of select="@name" />
        <xsl:copy-of select="$TAB" />
        <xsl:text>E</xsl:text>
        <xsl:value-of select="@no" />
        <xsl:copy-of select="$LF" />

        <xsl:for-each select="form"> 
          <xsl:text>CRF</xsl:text>
          <xsl:copy-of select="$TAB" />
          <xsl:value-of select="@name" />
          <xsl:copy-of select="$TAB" />
          <xsl:text>C</xsl:text>
          <xsl:value-of select="@global-no" />
          <xsl:copy-of select="$LF" />
        </xsl:for-each>
      </xsl:for-each>

    </xsl:for-each>

    <xsl:for-each select="1 to 3">
      <xsl:text></xsl:text>
      <xsl:copy-of select="$LF" />
    </xsl:for-each>

    <!-- Output the body header. -->

    <!-- Compute the prefix. -->
    <xsl:variable name="header-prefix">
      <!-- Subject ID and Protocol Name -->
      <xsl:for-each select="1 to 2">
        <xsl:value-of select="$TAB" />
      </xsl:for-each>
      <!-- Variable OpenClinica extensions. -->
      <xsl:for-each select="$sub-map/*">
        <xsl:value-of select="$TAB" />
      </xsl:for-each>
    </xsl:variable>

    <!-- Body header, line 1. -->

    <xsl:copy-of select="$header-prefix" />
    <xsl:text>Event (Occurrence)</xsl:text>
    <!-- Event names. -->
    <xsl:for-each select="$ext-map/event">
      <xsl:variable name="event" select="." />
      <xsl:for-each select="$evt-map/*">
        <xsl:copy-of select="$TAB" />
        <xsl:for-each select="$event">
          <xsl:call-template name="event-name" />
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
    <!-- Extended event headers for items. -->
    <xsl:for-each select="$ext-map/event">
      <xsl:variable name="event-name">
        <xsl:copy-of select="$TAB" />
        <xsl:call-template name="event-name"/>
      </xsl:variable>
      <xsl:for-each select="form">
        <xsl:for-each select="field | group/item">
          <xsl:copy-of select="$event-name" />
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:copy-of select="$LF" />

    <!-- Body header, line 2. -->
    <xsl:copy-of select="$header-prefix" />
    <xsl:text>CRF - Version</xsl:text>
    <!-- Empty cells under event names. -->
    <xsl:for-each select="$ext-map/event">
      <xsl:for-each select="$evt-map/*">
        <xsl:text></xsl:text>
        <xsl:copy-of select="$TAB" />
      </xsl:for-each>
    </xsl:for-each>
    <!-- Extended form headers for items. -->
    <xsl:for-each select="$ext-map/event/form">
      <xsl:variable name="form-name">
        <xsl:text></xsl:text>
        <xsl:copy-of select="$TAB" />
        <xsl:value-of select="@name" />
      </xsl:variable>
      <xsl:for-each select="field | group/item">
        <xsl:copy-of select="$form-name" />
      </xsl:for-each>
    </xsl:for-each>
    <xsl:copy-of select="$LF" />

    <!-- Body header, line 3. -->
    <xsl:copy-of select="$header-prefix" />
    <xsl:text>Item Description (Occurrence)</xsl:text>
    <!-- Empty cells under event names. -->
    <xsl:for-each select="$ext-map/event">
      <xsl:for-each select="$evt-map/*">
        <xsl:text></xsl:text>
        <xsl:copy-of select="$TAB" />
      </xsl:for-each>
    </xsl:for-each>
    <!-- Human-readable form fields and item names. -->
    <xsl:for-each select="$ext-map/event/form">
      <xsl:for-each select="field">
        <xsl:copy-of select="$TAB" />
        <xsl:value-of select="@name" />
      </xsl:for-each>
      <xsl:for-each select="group">
        <xsl:variable name="group-suffix">
          <xsl:if test="@repeat = 'true'">
            <xsl:text> (</xsl:text>
            <xsl:value-of select="@rep" />
            <xsl:text>)</xsl:text>
          </xsl:if>
        </xsl:variable>
        <xsl:for-each select="item">
          <xsl:copy-of select="$TAB" />
          <xsl:value-of select="@name" />
          <xsl:copy-of select="$group-suffix" />
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:copy-of select="$LF" />

    <!-- Body Header, line 4. -->
    <xsl:text>Study Subject ID</xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:text>Protocol ID</xsl:text>
    <xsl:copy-of select="$TAB" />
    <!-- Extensions. -->
    <xsl:apply-templates mode="head" select="$sub-map/*" />
    <xsl:text>Item Name</xsl:text>
    <!-- Event status codes. -->
    <xsl:for-each select="$ext-map/event">
      <xsl:apply-templates mode="head" select="$evt-map/*">
        <xsl:with-param name="event" select="." />
      </xsl:apply-templates>
    </xsl:for-each>
    <!-- Field and item codes. -->
    <xsl:for-each select="$ext-map/event">
      <xsl:variable name="event-code">
        <xsl:text>_</xsl:text>
        <xsl:call-template name="event-code" />
      </xsl:variable>
      <xsl:for-each select="form">
        <xsl:variable name="form-code">
          <xsl:text>_C</xsl:text>
          <xsl:value-of select="@global-no" />
        </xsl:variable>
        <xsl:for-each select="field">
          <xsl:text></xsl:text>
          <xsl:copy-of select="$TAB" />
          <xsl:value-of select="@code" />
          <xsl:copy-of select="$event-code" />
          <xsl:copy-of select="$form-code" />
        </xsl:for-each>
        <xsl:for-each select="group">
          <xsl:variable name="group-code">
            <xsl:if test="@repeat = 'true'">
              <xsl:text>_</xsl:text>
              <xsl:value-of select="@rep" />
            </xsl:if>
          </xsl:variable>
          <xsl:for-each select="item">
            <xsl:text></xsl:text>
            <xsl:copy-of select="$TAB" />
            <xsl:value-of select="@code" />
            <xsl:copy-of select="$event-code" />
            <xsl:copy-of select="$form-code" />
            <xsl:copy-of select="$group-code" />
          </xsl:for-each>
        </xsl:for-each>
      </xsl:for-each>
    </xsl:for-each>
    <xsl:copy-of select="$LF" />

    <!-- Output data. -->
    <xsl:for-each select="ODM:ODM/ODM:ClinicalData/ODM:SubjectData">
      <xsl:variable name="this" select="." />

      <!-- Subject ID -->
      <xsl:value-of select="@OC:StudySubjectID" />
      <xsl:text></xsl:text>
      <xsl:copy-of select="$TAB" />
      <!-- Protocol ID -->
      <xsl:value-of select="key('study-protocol',
          parent::ODM:ClinicalData/@StudyOID)" />
      <xsl:copy-of select="$TAB" />
      <!-- Extensions -->
      <xsl:apply-templates mode="body" select="$sub-map/*">
        <xsl:with-param name="subject" select="." />
      </xsl:apply-templates>
      <!-- Event Statuses -->
      <xsl:for-each select="$ext-map/event">
        <xsl:variable name="event" select="$this/ODM:StudyEventData
            [@StudyEventOID = current()/@id and (current()/@repeat != 'true'
            or @StudyEventRepeatKey = current()/@rep)]" />
        <xsl:apply-templates mode="body" select="$evt-map/*">
          <xsl:with-param name="event" select="$event" />
        </xsl:apply-templates>
      </xsl:for-each>
      <!-- Primary data -->
      <xsl:for-each select="$ext-map/event">
        <xsl:variable name="event" select="$this/ODM:StudyEventData
            [@StudyEventOID = current()/@id and (current()/@repeat != 'true'
            or @StudyEventRepeatKey = current()/@rep)]" />
        <xsl:text></xsl:text>
        <xsl:for-each select="form">
          <xsl:variable name="form" select="$event/ODM:FormData
              [@FormOID = current()/@id]" />
          <xsl:apply-templates mode="body" select="field">
            <xsl:with-param name="form" select="$form" />
          </xsl:apply-templates>
          <xsl:text></xsl:text>
          <xsl:for-each select="group">
            <xsl:variable name="group" select="$form/ODM:ItemGroupData
                [@ItemGroupOID = current()/@id and (current()/@repeat != 
                'true' or @ItemGroupRepeatKey = current()/@rep)]" />
            <xsl:text></xsl:text>
            <xsl:for-each select="item">
              <xsl:variable name="item" select="$group/ODM:ItemData
                  [@ItemOID = current()/@id]" />
              <xsl:text></xsl:text>
              <xsl:copy-of select="$TAB" />
              <xsl:text></xsl:text>
              <xsl:value-of select="normalize-space($item/@Value)" />
              <xsl:text></xsl:text>
            </xsl:for-each>
            <xsl:text></xsl:text>
          </xsl:for-each>
          <xsl:text></xsl:text>
        </xsl:for-each>
        <xsl:text></xsl:text>
      </xsl:for-each>
      <xsl:text></xsl:text>
      <xsl:copy-of select="$LF" />
      <xsl:text></xsl:text>
    </xsl:for-each>

  </xsl:template>

  <xsl:key name="study-protocol" match="ODM:ODM/ODM:Study/ODM:GlobalVariables/
      ODM:ProtocolName" use="ancestor::ODM:Study/@OID" />



  <xsl:template name="event-name">
    <xsl:value-of select="@name" />
    <xsl:if test="@repeat = 'true'">
      <xsl:text> (</xsl:text>
      <xsl:value-of select="@rep" />
      <xsl:text>)</xsl:text>
    </xsl:if>
  </xsl:template>



  <xsl:template name="event-code">
    <xsl:text>E</xsl:text>
    <xsl:value-of select="@no" />
    <xsl:if test="@repeat = 'true'">
      <xsl:text>_</xsl:text>
      <xsl:value-of select="@rep" />
    </xsl:if>
  </xsl:template>

  <!-- [head] -->

  <xsl:template mode="head" match="sex">
    <xsl:text>Sex</xsl:text>
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="head" match="primary-id">
    <xsl:text>Primary ID</xsl:text>
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="head" match="date-of-birth">
    <xsl:text>Date of Birth</xsl:text>
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="head" match="status">
    <xsl:text>Status</xsl:text>
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="head" match="secondary-id">
    <xsl:text>Secondary ID</xsl:text>
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="head" match="group">
    <xsl:value-of select="@name" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="head" match="event-status">
    <xsl:param name="event" />

    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="'Event Status_'" />
    <xsl:for-each select="$event">
      <xsl:call-template name="event-code" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template mode="head" match="location">
    <xsl:param name="event" />

    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="'Location_'" />
    <xsl:for-each select="$event">
      <xsl:call-template name="event-code" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template mode="head" match="start-date">
    <xsl:param name="event" />

    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="'Start Date_'" />
    <xsl:for-each select="$event">
      <xsl:call-template name="event-code" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template mode="head" match="end-date">
    <xsl:param name="event" />

    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="'End Date_'" />
    <xsl:for-each select="$event">
      <xsl:call-template name="event-code" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template mode="head" match="subject-age">
    <xsl:param name="event" />

    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="'Subject Age at Event_'" />
    <xsl:for-each select="$event">
      <xsl:call-template name="event-code" />
    </xsl:for-each>
  </xsl:template>

  <!-- [body] -->

  <xsl:template mode="body" match="sex">
    <xsl:param name="subject" />

    <xsl:value-of select="$subject/@OC:Sex" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="body" match="primary-id">
    <xsl:param name="subject" />

    <xsl:value-of select="$subject/@OC:UniqueIdentifier" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="body" match="date-of-birth">
    <xsl:param name="subject" />

    <xsl:value-of select="$subject/@OC:DateOfBirth" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="body" match="status">
    <xsl:param name="subject" />

    <xsl:value-of select="$subject/@OC:Status" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="body" match="secondary-id">
    <xsl:param name="subject" />

    <xsl:value-of select="$subject/@OC:SecondaryID" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <xsl:template mode="body" match="group">
    <xsl:param name="subject" />

    <xsl:value-of select="$subject/OC:SubjectGroupData
        [@OC:StudyGroupClassName = current()/@name]/@OC:StudyGroupName" />
    <xsl:copy-of select="$TAB" />
  </xsl:template>

  <!-- [body] end-date -->
  <xsl:template mode="body" match="end-date">
    <xsl:param name="event" />

    <xsl:text></xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="$event/@OC:EndDate" />
  </xsl:template>

  <!-- [body] event-status -->
  <xsl:template mode="body" match="event-status">
    <xsl:param name="event" />

    <xsl:text></xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="$event/@OC:Status" />
  </xsl:template>

  <!-- [body] location -->
  <xsl:template mode="body" match="location">
    <xsl:param name="event" />

    <xsl:text></xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="$event/@OC:StudyEventLocation" />
  </xsl:template>

  <!-- [body] start-date -->
  <xsl:template mode="body" match="start-date">
    <xsl:param name="event" />

    <xsl:text></xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="$event/@OC:StartDate" />
  </xsl:template>

  <!-- [body] subject-age -->
  <xsl:template mode="body" match="subject-age">
    <xsl:param name="event" />

    <xsl:text></xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="$event/@OC:SubjectAgeAtEvent" />
  </xsl:template>

  <!-- [body] form/field -->

  <xsl:template mode="body" match="form/field">
    <xsl:param name="form" />

    <xsl:text></xsl:text>
    <xsl:copy-of select="$TAB" />
    <xsl:value-of select="$form/@OC:*[local-name() = current()/@code]" />
  </xsl:template>


  <xsl:template mode="log" match="*">
    <xsl:text>&lt;</xsl:text>
    <xsl:value-of select="name()" />
    <xsl:for-each select="@*">
      <xsl:text> </xsl:text>
      <xsl:value-of select="name()" />
      <xsl:text>="</xsl:text>
      <xsl:value-of select="." />
      <xsl:text>"</xsl:text>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test="not(*)">
        <xsl:text>/&gt;</xsl:text>
        <xsl:value-of select="$LF" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>&gt;</xsl:text>
        <xsl:value-of select="$LF" />
        <xsl:apply-templates mode="log" />
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="name()" />
        <xsl:text>&gt;</xsl:text>
        <xsl:value-of select="$LF" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


</xsl:stylesheet>
