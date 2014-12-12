<?xml version="1.0" encoding="UTF-8"?>
<!-- Copyright (C) 2010, Akaza Research, LLC. All Changes Copyright (C) 2013, 
	Clinovo, Inc. -->
<xsl:stylesheet version="2.0"
	xmlns:odm="http://www.cdisc.org/ns/odm/v1.3" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3c.org/2001/XMLSchema-instance" 
	xmlns:def="http://www.cdisc.org/ns/def/v1.0"
	xmlns:xlink="http://www.w3c.org/1999/xlink"
	xmlns:OpenClinica="http://www.openclinica.org/ns/odm_ext_v130/v3.1"
	xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:myFunc="myFunc:functions">

	<xsl:output method="text" indent="yes" encoding="utf-8"
		standalone="yes" />
	<xsl:strip-space elements="*" />

	<!-- Separator/end of line characters for flexibility -->
	<xsl:variable name="delimiter" select="'&#x09;'" />
	<xsl:variable name="eol" select="'&#10;'" />
	<!--E to represent Events -->
	<xsl:variable name="E" select="'E'" />
	<xsl:variable name="C" select="'C'" />
	<xsl:variable name="itemNameAndEventSep" select="'x@x'" />

	<xsl:variable name="matchSep" select="'M_'" />
	<xsl:variable name="nonMatchSep" select="'*N'" />

	<xsl:key name="eventCRFs"
		match="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData/odm:FormData"
		use="@FormOID" />

	<xsl:key name="studyEvents"
		match="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData"
		use="@StudyEventOID" />

	<xsl:key name="form_OID"
		match="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:FormDef" use="@OID" />

	<xsl:key name="studyGroups" match="//odm:SubjectData/OpenClinica:SubjectGroupData"
		use="@OpenClinica:StudyGroupClassName" />

	<xsl:variable name="studyGroupsCount"
		select="count(//odm:SubjectData/OpenClinica:SubjectGroupData[generate-id() = generate-id(key('studyGroups',@OpenClinica:StudyGroupClassName)[1])])" />
	<xsl:variable name="studyGroupClasses"
		select="//odm:SubjectData/OpenClinica:SubjectGroupData[generate-id() = generate-id(key('studyGroups',@OpenClinica:StudyGroupClassName)[1])]" />
	<xsl:strip-space elements="*" />

	<xsl:variable name="sexExist" select="//odm:SubjectData/@OpenClinica:Sex" />
	<xsl:variable name="uniqueIdExist"
		select="//odm:SubjectData/@OpenClinica:UniqueIdentifier" />
	<xsl:variable name="dobExist"
		select="//odm:SubjectData/@OpenClinica:DateOfBirth" />
	<xsl:variable name="subjectStatusExist" select="//odm:SubjectData/@OpenClinica:Status" />
	<xsl:variable name="subjectSecondaryIdExist"
		select="//odm:SubjectData/@OpenClinica:SecondaryID" />
	
	<xsl:variable name="subjectGroupDataExist"
		select="count(//odm:SubjectData/OpenClinica:SubjectGroupData) &gt; 0" />

	<xsl:variable name="allEventDefs"
		select="//odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef" />
	<xsl:variable name="allStudyEventDataElements" select="//odm:StudyEventData" />
	<xsl:variable name="allFormDataElements" select="//odm:FormData" />
	<xsl:variable name="allFormDefElements" select="//odm:FormDef" />
	<xsl:variable name="allItemDefElements" select="//odm:ItemDef" />
	<xsl:variable name="allItemDataElements" select="//odm:ItemData" />
	<xsl:variable name="allItemGrpDataElements" select="//odm:ItemGroupData" />
	<xsl:variable name="allItemGroupDefElements" select="//odm:ItemGroupDef" />
	<xsl:variable name="allFormRefElements"
		select="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef/odm:FormRef" />
		
	<!-- Tokenization of column headers -->
	<xsl:variable name="eventColHeaders">
		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="getStudyEventInfoHeadersForDataOutput">
		</xsl:apply-templates>
	</xsl:variable>
	<xsl:variable name="tokenizedEventHeaders" select="tokenize($eventColHeaders,'_E')" />
	<xsl:variable name="crfAndDataItemsHeaders">
		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyFormAndDataItemsHeaders">
		</xsl:apply-templates>
	</xsl:variable>
	<xsl:variable name="tokenizedcrfAndDataItemsHeaders"
		select="tokenize($crfAndDataItemsHeaders,$itemNameAndEventSep)" />
	<xsl:variable name="mValSeparator1" select="'_][_1'" />
	<xsl:variable name="mValSeparator2" select="'_][_2'" />
	<xsl:variable name="mValSeparator3" select="'_][_3'" />
	<xsl:variable name="mValSeparator4" select="'_][_4'" />
	<xsl:variable name="mValSeparator5" select="'_][_5'" />

	<xsl:variable name="colHeaderEventName" select="'eventNameHeader'" />
	<xsl:variable name="colHeaderCRFName" select="'crfNameHeader'" />
	<xsl:variable name="colHeaderItemDesc" select="'itemDescHeader'" />
	<xsl:variable name="colHeaderItemName" select="'itemNameHeader'" />




	<xsl:template match="/odm:ODM">

		<xsl:variable name="fileName" select="/odm:ODM/@FileOID" />
		<xsl:variable name="year"
			select="substring(/odm:ODM/@CreationDateTime, 1, 4)" />
		<xsl:variable name="D_year" select="concat('D', $year)" />
		<xsl:variable name="datasetName" select="substring-before($fileName, $D_year)" />
		<xsl:variable name="desc" select="/odm:ODM/xs:string(@Description)" />
		<xsl:variable name="subject_count"
			select="count(/odm:ODM/odm:ClinicalData/odm:SubjectData)" />
		<xsl:variable name="study" select="/odm:ODM/odm:Study[1]" />
		<xsl:variable name="protocolNameStudy"
			select="$study/odm:GlobalVariables/odm:ProtocolName" />
		<xsl:variable name="eventDefCount"
			select="count(//odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[generate-id() = generate-id(key('studyEvents',@StudyEventOID)[1])])" />

		<xsl:variable name="displayEmptyColumnHeaders">
			<xsl:sequence 
				select="concat($delimiter, 
								$delimiter,
								if ($uniqueIdExist) then $delimiter else '',
								if ($subjectSecondaryIdExist) then $delimiter else '',
								if ($subjectStatusExist) then $delimiter else '',
								if ($sexExist) then $delimiter else '',
								if ($dobExist) then $delimiter else '',
								if ($subjectGroupDataExist) 
									then (string-join(for $counter in (1 to $studyGroupsCount) return $delimiter, '')) 
									else '')" />
		</xsl:variable>


		<xsl:value-of 
			select="concat('Dataset Name:  ', $delimiter, $datasetName, $eol,
							'Dataset Description:  ', $delimiter, $desc, $eol,
							'Study Name:  ', $delimiter, $study/odm:GlobalVariables/odm:StudyName, $eol,
							'Protocol ID:  ', $delimiter, $protocolNameStudy, $eol,
							'Date:  ', $delimiter)" />

		<xsl:call-template name="FormatDate">
			<xsl:with-param name="DateTime" select="/odm:ODM/@CreationDateTime" />
		</xsl:call-template>
		
		<xsl:value-of
			select="concat($eol,
							'Subjects:  ', $delimiter, $subject_count, $eol,
							'Study Event Definitions:  ', $delimiter, $eventDefCount, $eol)" />

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyEventDefinition" />

		<xsl:value-of select="$eol, $eol, $eol" />

		<!-- displaying event name column headers -->
		<xsl:value-of select="concat($displayEmptyColumnHeaders, 'Event (Occurrence)', $delimiter)" />

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyEventInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderEventName" />
		</xsl:apply-templates>

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyFormAndItemInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderEventName" />
		</xsl:apply-templates>

		<xsl:sequence select="$eol" />



		<!-- displaying CRF name column headers -->
		<xsl:value-of select="concat($displayEmptyColumnHeaders, 'CRF - Version', $delimiter)" />

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyEventInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderCRFName" />
		</xsl:apply-templates>

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyFormAndItemInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderCRFName" />
		</xsl:apply-templates>

		<xsl:sequence select="$eol" />



		<!-- displaying item description column headers -->
		<xsl:value-of select="concat($displayEmptyColumnHeaders, 'Item Description (Occurrence)', $delimiter)" />

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyEventInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderItemDesc" />
		</xsl:apply-templates>

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyFormAndItemInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderItemDesc" />
		</xsl:apply-templates>

		<xsl:sequence select="$eol" />



		<!-- displaying subject meta and item name column headers -->
		<xsl:value-of 
			select="concat('Study Subject ID', $delimiter, 'Protocol ID', $delimiter,
							if ($uniqueIdExist) then concat('Person ID', $delimiter) else '',
							if ($subjectSecondaryIdExist) then concat('Secondary ID', $delimiter) else '',
							if ($subjectStatusExist) then concat('Subject Status', $delimiter) else '',
							if ($sexExist) then concat('Sex', $delimiter) else '',
							if ($dobExist) then concat('Date of Birth', $delimiter) else '',
							if ($subjectGroupDataExist) 
								then string-join(for $subjectGroup in $studyGroupClasses 
													return concat($subjectGroup/xs:string(@OpenClinica:StudyGroupClassName), $delimiter), '')
								else '')" />
		
		<xsl:value-of select="concat('Item Name', $delimiter)" />

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyEventInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderItemName" />
		</xsl:apply-templates>

		<xsl:apply-templates
			select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef"
			mode="studyFormAndItemInfoHeaders">
			<xsl:with-param name="columnHeaderToDisplay" select="$colHeaderItemName" />
		</xsl:apply-templates>

		<xsl:sequence select="$eol" />



		<!-- displaying subject data -->
		<xsl:apply-templates select="/odm:ODM/odm:ClinicalData/odm:SubjectData"
			mode="allSubjectData">
			<xsl:with-param name="tokenizedEventHeaders" select="$tokenizedEventHeaders" />
			<xsl:with-param name="tokenizedcrfAndDataItemsHeaders"
				select="$tokenizedcrfAndDataItemsHeaders" />
		</xsl:apply-templates>

	</xsl:template>




	<xsl:template
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef"
		mode="studyEventDefinition">

		<xsl:variable name="eventDefOID" select="@OID" />
		<xsl:variable name="eventDefPosition" select="position()" />

		<xsl:if test="$allStudyEventDataElements[@StudyEventOID = $eventDefOID]">
			<xsl:value-of 
				select="concat('Study Event Definition ', $eventDefPosition,
								if (@Repeating = 'Yes') then ' (Repeating)' else '',
								$delimiter, xs:string(@Name), $delimiter, 'E', $eventDefPosition, $eol,
								string-join(for $formRefElement in (./odm:FormRef)
												return if ($allFormDataElements[../@StudyEventOID = $eventDefOID and @FormOID = $formRefElement/@FormOID])
															then concat('CRF', $delimiter, $allFormDefElements[@OID = $formRefElement/@FormOID]/@Name,
																	$delimiter, myFunc:getCRFCode($formRefElement), $eol)
															else '', ''))" />
		</xsl:if>
		
	</xsl:template>




	<xsl:template match="/odm:ODM/odm:ClinicalData/odm:SubjectData"
		mode="allSubjectData">
		<xsl:param name="tokenizedEventHeaders" />
		<xsl:param name="tokenizedcrfAndDataItemsHeaders" />
		<!-- possibly adding tokenized Study Group Class headers -->

		<xsl:variable name="studyOID" select="../@StudyOID" />
		<xsl:variable name="studyElement" select="//odm:Study[@OID = $studyOID]" />
		<xsl:variable name="protocolName"
			select="$studyElement/odm:GlobalVariables/odm:ProtocolName" />
		<xsl:variable name="subjectGroupValues" select="./OpenClinica:SubjectGroupData" />
		<xsl:apply-templates select="@OpenClinica:StudySubjectID" />
		<xsl:value-of select="$delimiter" />
		<xsl:apply-templates select="$protocolName" />
		<xsl:value-of select="$delimiter" />

		<xsl:if test="$uniqueIdExist">
			<xsl:value-of select="@OpenClinica:UniqueIdentifier" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>
		<xsl:if test="$subjectSecondaryIdExist">
			<xsl:value-of select="@OpenClinica:SecondaryID" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if test="$subjectStatusExist">
			<xsl:apply-templates select="(@OpenClinica:Status)" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if test="$sexExist">
			<xsl:apply-templates select="(@OpenClinica:Sex)" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>
		<xsl:if test="$dobExist">
			<xsl:value-of select="@OpenClinica:DateOfBirth" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if test="$subjectGroupDataExist">
			<xsl:choose>
				<xsl:when test="$subjectGroupValues">

					<xsl:for-each select="$studyGroupClasses">
						<xsl:variable name="groupClassName" select="@OpenClinica:StudyGroupClassName" />
						<xsl:variable name="subjectGroupValuesMatches"
							select="$subjectGroupValues[@OpenClinica:StudyGroupClassName = $groupClassName]" />

						<xsl:choose>
							<xsl:when test="$subjectGroupValuesMatches">
								<xsl:value-of
									select="$subjectGroupValuesMatches/@OpenClinica:StudyGroupName" />
								<xsl:value-of select="$delimiter" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$delimiter" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>

				</xsl:when>
				<xsl:otherwise>

					<xsl:for-each select="1 to $studyGroupsCount">
						<xsl:value-of select="$delimiter" />
					</xsl:for-each>

				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		
		<xsl:value-of select="$delimiter" />

		<xsl:variable name="subjectEvents" select="./odm:StudyEventData" />

		<xsl:variable name="subjectItems"
			select="./odm:StudyEventData/odm:FormData/odm:ItemGroupData/odm:ItemData" />

		<xsl:variable name="subjectForms" select="./odm:StudyEventData/odm:FormData" />

		<xsl:call-template name="studyEventInfoData2">
			<xsl:with-param name="subjectEvents" select="$subjectEvents" />
			<xsl:with-param name="tokenizedEventHeaders" select="$tokenizedEventHeaders" />
		</xsl:call-template>

		<xsl:call-template name="studyCRFAndItemsData">
			<xsl:with-param name="subjectEvents" select="$subjectEvents" />
			<xsl:with-param name="subjectForms" select="$subjectForms" />
			<xsl:with-param name="subjectItems" select="$subjectItems" />
			<xsl:with-param name="tokenizedcrfAndDataItemsHeaders"
				select="$tokenizedcrfAndDataItemsHeaders" />
		</xsl:call-template>

		<xsl:value-of select="$eol" />
	</xsl:template>
	
	
	
	
	<xsl:template name="FormatDate">
		<xsl:param name="DateTime" />
		
		<!-- <xsl:variable name="date" select="$DateTime" as="xs:dateTime"/>
		<xsl:sequence select="xs:string(format-dateTime($date, '[D01][MNn][Y0001]'))" /> -->
		
		<xsl:variable name="month">
			<xsl:value-of select="substring($DateTime, 6, 2)" />
		</xsl:variable>
		<xsl:variable name="days">
			<xsl:value-of select="substring($DateTime, 9, 2)" />
		</xsl:variable>

		<xsl:variable name="year_of_date">
			<xsl:value-of select="substring($DateTime, 1, 4)" />
		</xsl:variable>

		<xsl:value-of select="$year_of_date"
			disable-output-escaping="yes" />
		<xsl:value-of select="'-'" />
		<xsl:choose>
			<xsl:when test="$month = '01'">
				<xsl:text>Jan</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '02'">
				<xsl:text>Feb</xsl:text>

			</xsl:when>
			<xsl:when test="$month = '03'">
				<xsl:text>Mar</xsl:text>

			</xsl:when>
			<xsl:when test="$month = '04'">
				<xsl:text>Apr</xsl:text>

			</xsl:when>
			<xsl:when test="$month = '05'">

				<xsl:text>May</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '06'">

				<xsl:text>Jun</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '07'">

				<xsl:text>Jul</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '08'">

				<xsl:text>Aug</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '09'">

				<xsl:text>Sep</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '10'">

				<xsl:text>Oct</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '11'">

				<xsl:text>Nov</xsl:text>
			</xsl:when>
			<xsl:when test="$month = '12'">

				<xsl:text>Dec</xsl:text>
			</xsl:when>
		</xsl:choose>

		<xsl:value-of select="'-'" />
		<xsl:if test="(string-length($days) lt 2)">
			<xsl:value-of select="0" />
		</xsl:if>

		<xsl:value-of select="$days" />
	</xsl:template>




	<xsl:template
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef[@OID]"
		mode="studyFormAndDataItemsHeaders">

		<xsl:variable name="eventOID" select="@OID" />

		<!-- calculate event position -->
		<xsl:variable name="eventPosition">
			<xsl:for-each select="$allEventDefs">
				<xsl:if test="@OID = $eventOID">
					<xsl:copy-of select="position()" />
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<!-- maximum value of StudyEventRepeatKey for an event -->
		<xsl:variable name="MaxEventRepeatKey">
			<xsl:for-each
				select="//odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData/@StudyEventRepeatKey">
				<xsl:sort data-type="number" />
				<xsl:if test="position() = last()">
					<xsl:value-of select="." />
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:apply-templates select="."
			mode="studyFormColumnHeaders">
			<xsl:with-param name="eventPosition" select="$eventPosition" />
			<xsl:with-param name="isRepeatingEvent" select="@Repeating" />
			<xsl:with-param name="eventOID" select="@OID" />
			<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
		</xsl:apply-templates>

		<!-- apply template for item data columns -->
		<xsl:apply-templates select="."
			mode="studyItemDataColumnHeaders">
			<xsl:with-param name="eventOID" select="@OID" />
			<xsl:with-param name="isEventRepeating" select="@Repeating" />
			<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
		</xsl:apply-templates>

	</xsl:template>




	<xsl:template mode="studyFormColumnHeaders"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef[@OID]">
		
		<xsl:param name="eventOID" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isRepeatingEvent" />
		<xsl:param name="MaxEventRepeatKey" />
	
		<xsl:choose>
			<xsl:when test="$isRepeatingEvent = 'Yes'">
				<!-- create CRF columns for repeating event -->
				<xsl:apply-templates select="."
					mode="createCRFColForRepeatingEvent">
					<xsl:with-param name="eventOID" select="$eventOID" />
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="eventRepeatCnt" select="1" />
					<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="odm:FormRef">
					<xsl:variable name="formRefOID" select="@FormOID" />

					<!-- calculate form def position in metadata -->
					<xsl:variable name="formRefNodeId" select="generate-id()" />
					<xsl:variable name="crfPosition">
						<xsl:for-each select="$allFormRefElements">
							<xsl:if test="@FormOID = $formRefOID">
								<xsl:if test="$formRefNodeId = generate-id()">
									<xsl:copy-of select="position()" />
								</xsl:if>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="crfVersionExist"
						select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and 
					@OpenClinica:Version]) gt 0" />

					<xsl:variable name="interviewerNameExist"
						select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and 
						@OpenClinica:InterviewerName]) gt 0" />

					<xsl:variable name="interviewDateExist"
						select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and 
						@OpenClinica:InterviewDate]) gt 0" />

					<xsl:variable name="crfStatusExist"
						select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and 
					@OpenClinica:Status]) gt 0" />

					<xsl:if
						test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and odm:FormData/@FormOID = 
						$formRefOID]) gt 0">
						<xsl:if test="$interviewerNameExist">

							<xsl:value-of select="' '" />
							<xsl:text>Interviewer</xsl:text>
							<xsl:value-of select="$itemNameAndEventSep" />
							<xsl:value-of select="$eventPosition" />
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$C" />
							<xsl:value-of select="$crfPosition" />
							<xsl:value-of select="$delimiter" />
						</xsl:if>

						<xsl:if test="$interviewDateExist">

							<xsl:value-of select="' '" />
							<xsl:text>Interview Date</xsl:text>
							<xsl:value-of select="$itemNameAndEventSep" />
							<xsl:value-of select="$eventPosition" />
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$C" />
							<xsl:value-of select="$crfPosition" />
							<xsl:value-of select="$delimiter" />
						</xsl:if>

						<xsl:if test="$crfStatusExist">

							<xsl:value-of select="' '" />
							<xsl:text>CRF Version Status</xsl:text>
							<xsl:value-of select="$itemNameAndEventSep" />
							<xsl:value-of select="$eventPosition" />
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$C" />
							<xsl:value-of select="$crfPosition" />
							<xsl:value-of select="$delimiter" />
						</xsl:if>

						<xsl:if test="$crfVersionExist">

							<xsl:value-of select="' '" />
							<xsl:text>Version Name</xsl:text>
							<xsl:value-of select="$itemNameAndEventSep" />
							<xsl:value-of select="$eventPosition" />
							<xsl:text>_</xsl:text>
							<xsl:value-of select="$C" />
							<xsl:value-of select="$crfPosition" />
							<xsl:value-of select="$delimiter" />
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>




	<xsl:template name="createCRFColForRepeatingEvent" mode="createCRFColForRepeatingEvent"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef[@OID]">
		<xsl:param name="eventOID" />
		<xsl:param name="eventPosition" />
		<xsl:param name="eventRepeatCnt" />
		<xsl:param name="MaxEventRepeatKey" />
		
		<xsl:for-each select="odm:FormRef">
			<xsl:variable name="formRefOID" select="@FormOID" />

			<!-- calculate form def position in metadata -->
			<xsl:variable name="formRefNodeId" select="generate-id()" />
			<xsl:variable name="crfPosition">
				<xsl:for-each select="$allFormRefElements">
					<xsl:if test="@FormOID = $formRefOID">
						<xsl:if test="$formRefNodeId = generate-id()">
							<xsl:copy-of select="position()" />
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="crfVersionExist"
				select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and @OpenClinica:Version 
			and ../@StudyEventRepeatKey = $eventRepeatCnt]) gt 0" />

			<xsl:variable name="interviewerNameExist"
				select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and 
				@OpenClinica:InterviewerName  and ../@StudyEventRepeatKey = $eventRepeatCnt]) gt 0" />

			<xsl:variable name="interviewDateExist"
				select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and 
				@OpenClinica:InterviewDate  and ../@StudyEventRepeatKey = $eventRepeatCnt]) gt 0" />

			<xsl:variable name="crfStatusExist"
				select="count(//odm:FormData[../@StudyEventOID = $eventOID and @FormOID = $formRefOID and @OpenClinica:Status  and 
			../@StudyEventRepeatKey = $eventRepeatCnt]) gt 0" />

			<xsl:if test="$interviewerNameExist">

				<xsl:value-of select="' '" /><!-- added for tokenization 
					when displaying crf data -->
				<xsl:text>Interviewer</xsl:text>
				<xsl:value-of select="$itemNameAndEventSep" />
				<xsl:value-of select="$eventPosition" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$eventRepeatCnt" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$C" />
				<xsl:value-of select="$crfPosition" />
				<xsl:value-of select="$delimiter" />
			</xsl:if>

			<xsl:if test="$interviewDateExist">

				<xsl:value-of select="' '" />
				<xsl:text>Interview Date</xsl:text>
				<xsl:value-of select="$itemNameAndEventSep" />
				<xsl:value-of select="$eventPosition" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$eventRepeatCnt" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$C" />
				<xsl:value-of select="$crfPosition" />
				<xsl:value-of select="$delimiter" />
			</xsl:if>

			<xsl:if test="$crfStatusExist">

				<xsl:value-of select="' '" />
				<xsl:text>CRF Version Status</xsl:text>
				<xsl:value-of select="$itemNameAndEventSep" />
				<xsl:value-of select="$eventPosition" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$eventRepeatCnt" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$C" />
				<xsl:value-of select="$crfPosition" />
				<xsl:value-of select="$delimiter" />
			</xsl:if>

			<xsl:if test="$crfVersionExist">

				<xsl:value-of select="' '" />
				<xsl:text>Version Name</xsl:text>
				<xsl:value-of select="$itemNameAndEventSep" />
				<xsl:value-of select="$eventPosition" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$eventRepeatCnt" />
				<xsl:text>_</xsl:text>
				<xsl:value-of select="$C" />
				<xsl:value-of select="$crfPosition" />
				<xsl:value-of select="$delimiter" />
			</xsl:if>

		</xsl:for-each>
		<xsl:if test="($eventRepeatCnt+1) le number($MaxEventRepeatKey)">
			<xsl:call-template name="createCRFColForRepeatingEvent">
				<xsl:with-param name="eventOID" select="$eventOID" />
				<xsl:with-param name="eventPosition" select="$eventPosition" />
				<xsl:with-param name="eventRepeatCnt" select="$eventRepeatCnt+1" />
				<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>




	<xsl:template mode="studyItemDataColumnHeaders"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef[@OID]">
		<xsl:param name="eventOID" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="MaxEventRepeatKey" />


		<xsl:variable name="eventPosition">
			<xsl:for-each
				select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef">
				<xsl:if test="@OID = $eventOID">
					<xsl:copy-of select="position()" />
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$isEventRepeating = 'Yes'">
				<!-- create item data columns for repeating event -->
				<xsl:apply-templates select="."
					mode="createItemDataColForRepeatingEvent">
					<xsl:with-param name="eventOID" select="$eventOID" />
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="eventRepeatCnt" select="1" />
					<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
					<xsl:with-param name="isEventRepeating" select="$isEventRepeating" /><!-- 
						this is just need to pass on to further template which is common to repeating 
						and non-repeating events -->
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:for-each select="odm:FormRef">
					<xsl:variable name="formRefOID" select="@FormOID" />

					<xsl:variable name="formRefNodeId" select="generate-id()" />

					<xsl:variable name="crfPosition">
						<xsl:for-each select="$allFormRefElements">
							<xsl:if test="@FormOID = $formRefOID">
								<xsl:if test="$formRefNodeId = generate-id()">
									<xsl:copy-of select="position()" />
								</xsl:if>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:apply-templates mode="formRefToDefTemplateForHeaders"
						select="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:FormDef[@OID = $formRefOID]">
						<xsl:with-param name="crfPosition" select="$crfPosition" />
						<xsl:with-param name="eventPosition" select="$eventPosition" />
						<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
						<xsl:with-param name="eventOID" select="$eventOID" />
						<xsl:with-param name="StudyEventRepeatKey" select="$MaxEventRepeatKey" /><!-- 
							this param is of no use for non-repeating column further when creating the 
							columns -->
					</xsl:apply-templates>

				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>




	<xsl:template name="createItemDataColForRepeatingEvent"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef[@OID]"
		mode="createItemDataColForRepeatingEvent">
		<xsl:param name="eventOID" />
		<xsl:param name="eventPosition" />
		<xsl:param name="eventRepeatCnt" />
		<xsl:param name="MaxEventRepeatKey" />
		<xsl:param name="isEventRepeating" />

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = 
					$eventRepeatCnt]) &gt; 0">
			<xsl:for-each select="odm:FormRef">
				<xsl:variable name="formRefOID" select="@FormOID" />


				<xsl:variable name="formRefNodeId" select="generate-id()" />
				<xsl:variable name="crfPosition">
					<xsl:for-each select="$allFormRefElements">
						<xsl:if test="@FormOID = $formRefOID">
							<xsl:if test="$formRefNodeId = generate-id()">
								<xsl:copy-of select="position()" />
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
				</xsl:variable>

				<xsl:apply-templates
					select="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:FormDef[@OID = $formRefOID]"
					mode="formRefToDefTemplateForHeaders">
					<xsl:with-param name="crfPosition" select="$crfPosition" />
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
					<xsl:with-param name="eventOID" select="$eventOID" />
					<xsl:with-param name="StudyEventRepeatKey" select="$eventRepeatCnt" />
				</xsl:apply-templates>

			</xsl:for-each>
		</xsl:if>

		<!-- fix for issue 11832: corrected to repeat the process for next incremental 
			event repeat key until it reaches the value of "MaxEventRepeatKey" -->
		<!--<xsl:if test="count($allStudyEventDataElements[@StudyEventOID = $eventOID 
			and @StudyEventRepeatKey = ($eventRepeatCnt+1)]) &gt; 0"> -->

		<xsl:if test="($eventRepeatCnt+1) &lt;= number($MaxEventRepeatKey)">

			<xsl:apply-templates select="."
				mode="createItemDataColForRepeatingEvent">
				<xsl:with-param name="eventOID" select="$eventOID" />
				<xsl:with-param name="eventPosition" select="$eventPosition" />
				<xsl:with-param name="eventRepeatCnt" select="$eventRepeatCnt+1" />
				<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
				<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>




	<xsl:template mode="formRefToDefTemplateForHeaders"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:FormDef[@OID]">
		<xsl:param name="crfPosition" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="eventOID" />
		<xsl:param name="StudyEventRepeatKey" />

		<xsl:variable name="formOID" select="@OID" />
		<xsl:apply-templates select="odm:ItemGroupRef"
			mode="ItemGrpRefs">
			<xsl:with-param name="crfPosition" select="$crfPosition" />
			<xsl:with-param name="eventPosition" select="$eventPosition" />
			<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
			<xsl:with-param name="formOID" select="$formOID" />
			<xsl:with-param name="eventOID" select="$eventOID" />
			<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
		</xsl:apply-templates>
	</xsl:template>




	<xsl:template mode="ItemGrpRefs"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:FormDef/odm:ItemGroupRef[@ItemGroupOID]">
		<xsl:param name="crfPosition" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="formOID" />
		<xsl:param name="eventOID" />
		<xsl:param name="StudyEventRepeatKey" />

		<xsl:variable name="grpOID" select="@ItemGroupOID" />
		<xsl:apply-templates
			select="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemGroupDef[@OID = $grpOID]"
			mode="ItemGrpRefToDefTemplateForHeaders">
			<xsl:with-param name="crfPosition" select="$crfPosition" />
			<xsl:with-param name="eventPosition" select="$eventPosition" />
			<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
			<xsl:with-param name="formOID" select="$formOID" />
			<xsl:with-param name="grpOID" select="$grpOID" />
			<xsl:with-param name="eventOID" select="$eventOID" />
			<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
		</xsl:apply-templates>
	</xsl:template>




	<xsl:template mode="ItemGrpRefToDefTemplateForHeaders"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemGroupDef[@OID]">
		<xsl:param name="crfPosition" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="formOID" />
		<xsl:param name="grpOID" />
		<xsl:param name="eventOID" />
		<xsl:param name="StudyEventRepeatKey" />

		<xsl:variable name="isGrpRepeating" select="@Repeating" />
		<!--<xsl:variable name="itemGrpRepeatKey" select="1"/> -->

		<xsl:choose>
			<xsl:when test="$isGrpRepeating = 'Yes'">
				<xsl:apply-templates mode="createItemDataColForRepeatingGrps"
					select=".">
					<xsl:with-param name="crfPosition" select="$crfPosition" />
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
					<xsl:with-param name="formOID" select="$formOID" />
					<xsl:with-param name="grpOID" select="$grpOID" />
					<xsl:with-param name="eventOID" select="$eventOID" />
					<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
					<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
					<xsl:with-param name="itemGrpRepeatKey" select="1" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="odm:ItemRef" mode="GrpItemRefs">
					<xsl:with-param name="crfPosition" select="$crfPosition" />
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
					<xsl:with-param name="formOID" select="$formOID" />
					<xsl:with-param name="grpOID" select="$grpOID" />
					<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
					<xsl:with-param name="eventOID" select="$eventOID" />
					<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
					<xsl:with-param name="isLastItem" select="position()=last()" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>




	<xsl:template mode="createItemDataColForRepeatingGrps"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemGroupDef[@OID]">
		<xsl:param name="crfPosition" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="formOID" />
		<xsl:param name="grpOID" />
		<xsl:param name="eventOID" />
		<xsl:param name="StudyEventRepeatKey" />
		<xsl:param name="itemGrpRepeatKey" />
		<xsl:param name="isGrpRepeating" />

		<!--createItemDataColForRepeatingGrps:formOID:<xsl:value-of select="$formOID"/> -->
		<xsl:choose>
			<xsl:when test="$isEventRepeating = 'Yes'">
				<xsl:variable name="maxGrpRepeatKey">
					<xsl:for-each
						select="$allItemGrpDataElements[../../@StudyEventOID = $eventOID and ../../@StudyEventRepeatKey = $StudyEventRepeatKey 
							and ../@FormOID = $formOID and @ItemGroupOID = $grpOID ]/@ItemGroupRepeatKey">
						<xsl:sort data-type="number" />
						<xsl:if test="position() = last()">
							<xsl:value-of select="." />
						</xsl:if>
					</xsl:for-each>
				</xsl:variable>
				<xsl:if
					test="count($allItemGrpDataElements[../../@StudyEventOID = $eventOID and ../../@StudyEventRepeatKey = $StudyEventRepeatKey 
						and ../@FormOID = $formOID and @ItemGroupOID = $grpOID 
						and @ItemGroupRepeatKey = $itemGrpRepeatKey]) gt 0">
					<xsl:apply-templates select="odm:ItemRef" mode="GrpItemRefs">
						<xsl:with-param name="crfPosition" select="$crfPosition" />
						<xsl:with-param name="eventPosition" select="$eventPosition" />
						<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
						<xsl:with-param name="formOID" select="$formOID" />
						<xsl:with-param name="grpOID" select="$grpOID" />
						<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
						<xsl:with-param name="eventOID" select="$eventOID" />
						<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
						<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey" />
						<xsl:with-param name="isLastItem" select="position()=last()" />
					</xsl:apply-templates>
				</xsl:if>
				<xsl:if test="($itemGrpRepeatKey+1) le number($maxGrpRepeatKey)">
					<xsl:apply-templates mode="createItemDataColForRepeatingGrps"
						select=".">
						<xsl:with-param name="crfPosition" select="$crfPosition" />
						<xsl:with-param name="eventPosition" select="$eventPosition" />
						<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
						<xsl:with-param name="formOID" select="$formOID" />
						<xsl:with-param name="grpOID" select="$grpOID" />
						<xsl:with-param name="eventOID" select="$eventOID" />
						<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
						<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey+1" />
						<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
					</xsl:apply-templates>
				</xsl:if>
			</xsl:when>

			<xsl:otherwise>
				<xsl:variable name="maxGrpRepeatKey">
					<xsl:for-each
						select="$allItemGrpDataElements[../../@StudyEventOID = $eventOID  
							and ../@FormOID = $formOID and @ItemGroupOID = $grpOID ]/@ItemGroupRepeatKey">
						<xsl:sort data-type="number" />
						<xsl:if test="position() = last()">
							<xsl:value-of select="." />
						</xsl:if>
					</xsl:for-each>
				</xsl:variable>
				<xsl:if
					test="count($allItemGrpDataElements[../../@StudyEventOID = $eventOID 
						and ../@FormOID = $formOID and @ItemGroupOID = $grpOID 
						and @ItemGroupRepeatKey = $itemGrpRepeatKey]) gt 0">
					<xsl:apply-templates select="odm:ItemRef" mode="GrpItemRefs">
						<xsl:with-param name="crfPosition" select="$crfPosition" />
						<xsl:with-param name="eventPosition" select="$eventPosition" />
						<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
						<xsl:with-param name="formOID" select="$formOID" />
						<xsl:with-param name="grpOID" select="$grpOID" />
						<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
						<xsl:with-param name="eventOID" select="$eventOID" />
						<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
						<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey" />
						<xsl:with-param name="isLastItem" select="position()=last()" />
					</xsl:apply-templates>
				</xsl:if>
				<xsl:if test="($itemGrpRepeatKey+1) le number($maxGrpRepeatKey)">
					<xsl:apply-templates mode="createItemDataColForRepeatingGrps"
						select=".">
						<xsl:with-param name="crfPosition" select="$crfPosition" />
						<xsl:with-param name="eventPosition" select="$eventPosition" />
						<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
						<xsl:with-param name="formOID" select="$formOID" />
						<xsl:with-param name="grpOID" select="$grpOID" />
						<xsl:with-param name="eventOID" select="$eventOID" />
						<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
						<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey+1" />
						<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
					</xsl:apply-templates>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>




	<xsl:template mode="GrpItemRefs"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemGroupDef/odm:ItemRef[@ItemOID]">
		<xsl:param name="crfPosition" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="StudyEventRepeatKey" />
		<xsl:param name="formOID" />
		<xsl:param name="grpOID" />
		<xsl:param name="isGrpRepeating" />
		<xsl:param name="eventOID" />
		<xsl:param name="itemGrpRepeatKey" />
		<xsl:param name="isLastItem" />

		<xsl:variable name="itemOID" select="@ItemOID" />
		<xsl:choose>
			<xsl:when test="$isEventRepeating = 'Yes'">

				<xsl:choose>
					<xsl:when test="$isGrpRepeating = 'Yes'"><!--repeating grp -->

						<!--cnt123:<xsl:value-of select="count($allItemDataElements[@ItemOID 
							= $itemOID and ../@ItemGroupOID = $grpOID and ../@ItemGroupRepeatKey =$itemGrpRepeatKey 
							and ../../@FormOID = $formOID and ../../../@StudyEventOID = $eventOID and 
							../../../@StudyEventRepeatKey = $StudyEventRepeatKey]) "/> -->

						<xsl:if
							test="count($allItemDataElements[@ItemOID = $itemOID and ../@ItemGroupOID = $grpOID and ../@ItemGroupRepeatKey =$itemGrpRepeatKey and ../../@FormOID = 
						$formOID and ../../../@StudyEventOID = $eventOID and ../../../@StudyEventRepeatKey = $StudyEventRepeatKey]) gt 0"><!--create col -->
							<xsl:apply-templates
								select="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemDef[@OID=$itemOID]"
								mode="ItemDefColHeaders2">
								<xsl:with-param name="crfPosition" select="$crfPosition" />
								<xsl:with-param name="eventPosition" select="$eventPosition" />
								<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
								<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
								<xsl:with-param name="itemOID" select="$itemOID" />
								<xsl:with-param name="eventOID" select="$eventOID" />
								<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
								<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey" />
								<xsl:with-param name="isLastItem" select="$isLastItem" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if
							test="count($allItemDataElements[@ItemOID = $itemOID and ../@ItemGroupOID = $grpOID and ../../@FormOID = 
						$formOID and ../../../@StudyEventOID = $eventOID and ../../../@StudyEventRepeatKey = $StudyEventRepeatKey]) gt 0">
							<xsl:apply-templates
								select="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemDef[@OID=$itemOID]"
								mode="ItemDefColHeaders2">
								<xsl:with-param name="crfPosition" select="$crfPosition" />
								<xsl:with-param name="eventPosition" select="$eventPosition" />
								<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
								<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
								<xsl:with-param name="itemOID" select="$itemOID" />
								<xsl:with-param name="eventOID" select="$eventOID" />
								<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
								<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey" />
								<xsl:with-param name="isLastItem" select="$isLastItem" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$isGrpRepeating = 'Yes'"><!--repeating grp -->
						<xsl:if
							test="count($allItemDataElements[@ItemOID = $itemOID and ../@ItemGroupOID = $grpOID and ../@ItemGroupRepeatKey =$itemGrpRepeatKey and ../../@FormOID = 
						$formOID and ../../../@StudyEventOID = $eventOID]) gt 0"><!--create col -->
							<xsl:apply-templates
								select="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemDef[@OID=$itemOID]"
								mode="ItemDefColHeaders2">
								<xsl:with-param name="crfPosition" select="$crfPosition" />
								<xsl:with-param name="eventPosition" select="$eventPosition" />
								<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
								<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
								<xsl:with-param name="itemOID" select="$itemOID" />
								<xsl:with-param name="eventOID" select="$eventOID" />
								<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
								<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey" />
								<xsl:with-param name="isLastItem" select="$isLastItem" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:if
							test="count($allItemDataElements[@ItemOID = $itemOID and ../@ItemGroupOID = $grpOID and ../../@FormOID = 
						$formOID and ../../../@StudyEventOID = $eventOID]) gt 0">
							<xsl:apply-templates
								select="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemDef[@OID=$itemOID]"
								mode="ItemDefColHeaders2">
								<xsl:with-param name="crfPosition" select="$crfPosition" />
								<xsl:with-param name="eventPosition" select="$eventPosition" />
								<xsl:with-param name="isEventRepeating" select="$isEventRepeating" />
								<xsl:with-param name="isGrpRepeating" select="$isGrpRepeating" />
								<xsl:with-param name="itemOID" select="$itemOID" />
								<xsl:with-param name="eventOID" select="$eventOID" />
								<xsl:with-param name="StudyEventRepeatKey" select="$StudyEventRepeatKey" />
								<xsl:with-param name="itemGrpRepeatKey" select="$itemGrpRepeatKey" />
								<xsl:with-param name="isLastItem" select="$isLastItem" />
							</xsl:apply-templates>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>


		</xsl:choose>
	</xsl:template>




	<xsl:template mode="ItemDefColHeaders2"
		match="//odm:ODM/odm:Study/odm:MetaDataVersion/odm:ItemDef[@OID]">
		<xsl:param name="crfPosition" />
		<xsl:param name="eventPosition" />
		<xsl:param name="isEventRepeating" />
		<xsl:param name="isGrpRepeating" />
		<xsl:param name="itemOID" />
		<xsl:param name="eventOID" />
		<xsl:param name="StudyEventRepeatKey" />
		<xsl:param name="grpRepeatKey" />
		<xsl:param name="itemGrpRepeatKey" />
		<xsl:param name="isLastItem" />


		<xsl:value-of select="' '" />
		<xsl:value-of select="@Name" />
		<xsl:value-of select="$itemNameAndEventSep" />
		<xsl:value-of select="$eventPosition" />
		<xsl:if test="$isEventRepeating = 'Yes'">
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$StudyEventRepeatKey" />
		</xsl:if>
		<xsl:text>_</xsl:text>
		<xsl:value-of select="$C" />
		<xsl:value-of select="$crfPosition" />
		<xsl:if test="$isGrpRepeating ='Yes'">
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$itemGrpRepeatKey" />
		</xsl:if>
		<xsl:if test="$isLastItem">
			<xsl:value-of select="' '" />
		</xsl:if>
		<xsl:value-of select="$delimiter" />
	</xsl:template>




	<xsl:template name="studyEventInfoData2">
		<xsl:param name="subjectEvents" />

		<xsl:param name="tokenizedEventHeaders" />

		<xsl:for-each select="$tokenizedEventHeaders">
			<xsl:variable name="currentPos" select="position()" /><!--currentPos: 
				<xsl:value-of select="$currentPos"/> -->
			<xsl:variable name="currentToken" select="." />
			<!--{T<xsl:value-of select="position()"/>:<xsl:value-of select="."/>} -->
			<xsl:if test=". != $tokenizedEventHeaders[last()]"><!--not last -->

				<!-- get which event this is -->
				<xsl:variable name="nextToken"
					select="$tokenizedEventHeaders[$currentPos+1]" />
				<!--urrentToken:*<xsl:value-of select="$currentToken"/>* next token:*<xsl:value-of 
					select="$nextToken"/>* -->
				<xsl:variable name="numericStart">
					<xsl:if test="ends-with($nextToken,'Location')">
						<xsl:value-of
							select="substring-before($nextToken,concat($delimiter, 'Location'))" />
					</xsl:if>
					<xsl:if test="ends-with($nextToken,'StartDate')">
						<xsl:value-of
							select="substring-before($nextToken,concat($delimiter, 'StartDate'))" />
					</xsl:if>
					<xsl:if test="ends-with($nextToken,'EndDate')">
						<xsl:value-of
							select="substring-before($nextToken,concat($delimiter, 'EndDate'))" />
					</xsl:if>
					<xsl:if test="ends-with($nextToken,'Event Status')">
						<xsl:value-of
							select="substring-before($nextToken,concat($delimiter, 'Event Status'))" />
					</xsl:if>
					<xsl:if test="ends-with($nextToken,'Age')">
						<xsl:value-of
							select="substring-before($nextToken,concat($delimiter, 'Age'))" />
					</xsl:if>
					<xsl:if test="$currentToken = $tokenizedEventHeaders[last()-1]">
						<xsl:value-of select="$nextToken" />
					</xsl:if>
				</xsl:variable>
				<!--{numeric start: *<xsl:value-of select="$numericStart"/>*} -->
				<xsl:variable name="colEventPosition">
					<xsl:choose>
						<xsl:when test="contains($numericStart, '_')">
							<xsl:value-of select="substring-before($numericStart,'_')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$numericStart" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--colEventPosition:*<xsl:value-of select="$colEventPosition"/>* -->
				<xsl:variable name="isColForRepeatingEvent" select="contains($numericStart, '_')" /><!--isColForRepeatingEvent<xsl:value-of 
					select="$isColForRepeatingEvent"/> -->
				<xsl:variable name="colRepeatEventKey">
					<xsl:if test="contains($numericStart, '_')">
						<xsl:value-of select="substring-after($numericStart,'_')" />
					</xsl:if>
				</xsl:variable><!--colRepeatEventKey: <xsl:value-of select="$colRepeatEventKey"/> -->

				<xsl:variable name="colType">
					<xsl:if test="ends-with($currentToken,'Location')">
						<xsl:text>Location</xsl:text>
					</xsl:if>
					<xsl:if test="ends-with($currentToken,'StartDate')">
						<xsl:text>StartDate</xsl:text>
					</xsl:if>
					<xsl:if test="ends-with($currentToken,'EndDate')">
						<xsl:text>EndDate</xsl:text>
					</xsl:if>
					<xsl:if test="ends-with($currentToken,'Event Status')">
						<xsl:text>Status</xsl:text>
					</xsl:if>
					<xsl:if test="ends-with($currentToken,'Age')">
						<xsl:text>Age</xsl:text>
					</xsl:if>
				</xsl:variable>

				<!--colType:<xsl:value-of select="$colType"/>isColForRepeatingEvent:<xsl:value-of 
					select="$isColForRepeatingEvent"/> -->
				<xsl:variable name="ifMatch">
					<xsl:for-each select="$subjectEvents">
						<xsl:variable name="eventOID" select="@StudyEventOID" />
						<xsl:variable name="eventPosition">
							<xsl:for-each
								select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef">
								<xsl:if test="@OID = $eventOID">
									<xsl:copy-of select="position()" />
								</xsl:if>
							</xsl:for-each>
						</xsl:variable>
						<!--eventPosition:*<xsl:value-of select="$eventPosition"/> -->
						<xsl:choose>
							<xsl:when test="normalize-space($colEventPosition) = $eventPosition"><!--event matched -->
								<xsl:choose>
									<xsl:when test="$isColForRepeatingEvent"><!-- repeating event -->
										<!--colRepeatEventKey:*<xsl:value-of select="$colRepeatEventKey"/> -->
										<xsl:choose>
											<xsl:when
												test="@StudyEventRepeatKey = normalize-space($colRepeatEventKey)"><!--{event repeat key matched} -->
												<xsl:value-of select="$matchSep" />
												<xsl:value-of select="position()" /><!--_<xsl:value-of 
													select="@StudyEventRepeatKey"/> -->
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$nonMatchSep" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise><!--non repeating event -->
										<xsl:value-of select="$matchSep" />
										<xsl:value-of select="position()" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$nonMatchSep" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:variable>

				<!--ifMatch: *<xsl:value-of select="$ifMatch"/>* -->
				<xsl:choose>
					<xsl:when test="contains($ifMatch, $matchSep)">

						<xsl:variable name="StrAfterM"
							select="substring-after($ifMatch,$matchSep)" />
						<!--<xsl:variable name="StrB4N" select="substring-before($StrAfterM,'N')"/> 
							<xsl:variable name="evenPos" select="substring-before($StrB4N, '_')"/> <xsl:variable 
							name="evenRepeatKey" select="substring-aftere($StrB4N, '_')"/> -->


						<!--<xsl:variable name="StrAfterM" select="substring-after($ifMatch,'M')"/> -->
						<!--<xsl:variable name="StrB4N" select="substring-before($StrAfterM,'N')"/> -->
						<xsl:variable name="evenPos">
							<xsl:choose>
								<xsl:when test="contains($StrAfterM,$nonMatchSep)">
									<xsl:value-of select="substring-before($StrAfterM,$nonMatchSep)" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$StrAfterM" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<!--evenPos:<xsl:value-of select="$evenPos"/> -->
						<xsl:variable name="event"
							select="$subjectEvents[position() = number($evenPos)]" />
						<!-- write data -->
						<xsl:if test="$colType = 'Location'">
							<xsl:choose>
								<xsl:when test="$event/@OpenClinica:StudyEventLocation">
									<xsl:value-of select="$event/@OpenClinica:StudyEventLocation"></xsl:value-of>
									<xsl:value-of select="$delimiter" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$delimiter" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="$colType = 'StartDate'">
							<xsl:choose>
								<xsl:when test="$event/@OpenClinica:StartDate">
									<xsl:value-of select="$event/@OpenClinica:StartDate"></xsl:value-of>
									<xsl:value-of select="$delimiter" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$delimiter" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="$colType = 'EndDate'">
							<xsl:choose>
								<xsl:when test="$event/@OpenClinica:EndDate">
									<xsl:value-of select="$event/@OpenClinica:EndDate"></xsl:value-of>
									<xsl:value-of select="$delimiter" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$delimiter" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="$colType = 'Status'">
							<xsl:choose>
								<xsl:when test="$event/@OpenClinica:Status">
									<xsl:value-of select="$event/@OpenClinica:Status"></xsl:value-of>
									<xsl:value-of select="$delimiter" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$delimiter" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="$colType = 'Age'">
							<xsl:choose>
								<xsl:when test="$event/@OpenClinica:SubjectAgeAtEvent">
									<xsl:value-of select="$event/@OpenClinica:SubjectAgeAtEvent"></xsl:value-of>
									<xsl:value-of select="$delimiter" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$delimiter" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>



					</xsl:when>
					<xsl:otherwise>
						<xsl:if test="$colType = 'Location'">
							<xsl:value-of select="$delimiter" />
						</xsl:if>
						<xsl:if test="$colType = 'StartDate'">
							<xsl:value-of select="$delimiter" />
						</xsl:if>
						<xsl:if test="$colType = 'EndDate'">
							<xsl:value-of select="$delimiter" />
						</xsl:if>
						<xsl:if test="$colType = 'Status'">
							<xsl:value-of select="$delimiter" />
						</xsl:if>
						<xsl:if test="$colType = 'Age'">
							<xsl:value-of select="$delimiter" />
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>

			</xsl:if>
		</xsl:for-each>

	</xsl:template>




	<xsl:template name="studyCRFAndItemsData">
		<xsl:param name="subjectForms" />
		<xsl:param name="subjectItems" />

		<xsl:param name="subjectEvents" />

		<xsl:param name="tokenizedcrfAndDataItemsHeaders" />

		<xsl:for-each select="$tokenizedcrfAndDataItemsHeaders">
			<xsl:variable name="currentPos" select="position()" /><!--currentPos: 
				<xsl:value-of select="$currentPos"/> -->
			<xsl:variable name="currentToken" select="." />
			<!--{T<xsl:value-of select="position()"/>:<xsl:value-of select="."/>} -->
			<xsl:if test=". != $tokenizedcrfAndDataItemsHeaders[last()]"><!--not last -->
				<!-- ************** Steps ************************* -->
				<!-- get event posiotn and event repeat key (if repeating event) from 
					next token. -->
				<!-- get if this is crf or item column -->
				<!-- if crf -->
				<!-- extract event position, event repeat key (if repeating event) if 
					not fecthed commonly, crf position form column name -->
				<!-- iterate study events. for each study event; if repeating - check 
					if crf in column name is present and event info matched as well. If yes write 
					the data. same for non-repeating except the event repeat key will not be 
					matched. if matched write data else write empty column -->
				<!-- if item -->
				<!-- extract event position, event repeat key (if repeating event) if 
					not fecthed commonly, crf position, grp repeat key (if repeating grp) -->
				<!-- iterate study events. for each study event; if repeating - check 
					if item name, crf, grp repeat key in column name is present and event info 
					matched as well. If yes write the data. same for non-repeating except the 
					event repeat key will not be matched. if matched write data else write empty 
					column -->
				<!-- *************************************** -->
				<!-- get event posiotn and event repeat key (if repeating event) from 
					next token. -->
				<xsl:variable name="nextToken"
					select="$tokenizedcrfAndDataItemsHeaders[$currentPos+1]" />
				<!--currentToken:*<xsl:value-of select="$currentToken"/>* next token:*<xsl:value-of 
					select="$nextToken"/>* -->
				<xsl:variable name="numericStart">
					<xsl:choose>
						<!--<xsl:when test="position = 1" -->
						<xsl:when test="ends-with($nextToken,'Interviewer')">
							<xsl:value-of
								select="substring-before($nextToken,concat(concat($delimiter, ' '), 'Interviewer'))" />
						</xsl:when>
						<xsl:when test="ends-with($nextToken,'Interview Date')">
							<xsl:value-of
								select="substring-before($nextToken,concat(concat($delimiter, ' '), 'Interview Date'))" />
						</xsl:when>
						<xsl:when test="ends-with($nextToken,'CRF Version Status')">
							<xsl:value-of
								select="substring-before($nextToken,concat(concat($delimiter, ' '), 'CRF Version Status'))" />
						</xsl:when>
						<xsl:when test="ends-with($nextToken,'Version Name')">
							<xsl:value-of
								select="substring-before($nextToken,concat(concat($delimiter, ' '), 'Version Name'))" />
						</xsl:when>
						<xsl:otherwise>

							<!--<xsl:value-of select="substring-before($nextToken,concat(' ', 
								$delimiter))"/> -->
							<xsl:choose>
								<xsl:when test="contains($nextToken, concat(' ', $delimiter))">
									<xsl:value-of
										select="substring-before($nextToken,concat(' ', $delimiter))" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of
										select="substring-before($nextToken,concat($delimiter, ' '))" />
								</xsl:otherwise>
							</xsl:choose>

						</xsl:otherwise>
					</xsl:choose>

				</xsl:variable>
				<!--{numeric start: *<xsl:value-of select="$numericStart"/>*} -->
				<xsl:variable name="numericB4_C"
					select="substring-before($numericStart, '_C')" />
				<!--numericB4_C: <xsl:value-of select="$numericB4_C"/> -->
				<xsl:variable name="colEventPosition">
					<xsl:choose>
						<xsl:when test="contains($numericB4_C, '_')">
							<xsl:value-of select="substring-before($numericStart,'_')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$numericB4_C" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--lEventPosition: <xsl:value-of select="$colEventPosition"/> -->

				<xsl:variable name="isColForRepeatingEvent" select="contains($numericB4_C, '_')" /><!--isColForRepeatingEvent<xsl:value-of 
					select="$isColForRepeatingEvent"/> -->
				<xsl:variable name="colRepeatEventKey">
					<xsl:if test="contains($numericB4_C, '_')">
						<xsl:value-of select="substring-after($numericB4_C,'_')" />
					</xsl:if>
				</xsl:variable><!--colRepeatEventKey: <xsl:value-of select="$colRepeatEventKey"/> -->
				<!-- get if this is crf or item column -->
				<xsl:variable name="colType">
					<xsl:choose>
						<xsl:when test="ends-with($currentToken,'Interviewer')">
							<xsl:text>Interviewer</xsl:text>
						</xsl:when>
						<xsl:when test="ends-with($currentToken,'Interview Date')">
							<xsl:text>InterviewDate</xsl:text>
						</xsl:when>
						<xsl:when test="ends-with($currentToken,'CRF Version Status')">
							<xsl:text>CRF Version Status</xsl:text>
						</xsl:when>
						<xsl:when test="ends-with($currentToken,'Version Name')">
							<xsl:text>Version Name</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>ItemData</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--{colType: <xsl:value-of select="$colType"/> } -->
				<xsl:variable name="numericAfter_C"
					select="substring-after($numericStart, '_C')" />
				<!--numericAfter_C:<xsl:value-of select="$numericAfter_C"/> -->
				<xsl:variable name="colCrfPosition">
					<xsl:choose>
						<xsl:when test="contains($numericAfter_C, '_')">
							<xsl:value-of select="substring-before($numericAfter_C,'_')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$numericAfter_C" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<!--colCrfPosition: *<xsl:value-of select="$colCrfPosition"/>* -->
				<xsl:variable name="colItemName">
					<xsl:if test="$colType = 'ItemData'">
						<xsl:value-of select="substring-after($currentToken, ' ')" />
					</xsl:if>
				</xsl:variable>
				<!--{colItemName: *<xsl:value-of select="$colItemName"/>*} -->
				<xsl:variable name="isColForRepeatingGrp" select="contains($numericAfter_C, '_')" /><!--isColForRepeatingGrp<xsl:value-of 
					select="$isColForRepeatingGrp"/> -->
				<xsl:variable name="colRepeatGrpKey">
					<xsl:if test="contains($numericAfter_C, '_')">
						<xsl:value-of select="substring-after($numericAfter_C,'_')" />
					</xsl:if>
				</xsl:variable>

				<xsl:choose>
					<xsl:when test="$colType = 'ItemData'"><!--data column -->
						<xsl:variable name="ifMatch">
							<xsl:for-each select="$subjectEvents">
								<xsl:variable name="eventOID" select="@StudyEventOID" /><!--{eventOID:<xsl:value-of 
									select="$eventOID"/>} -->
								<xsl:variable name="eventRepeatKey" select="@StudyEventRepeatKey" /><!--{eventRepeatKey:<xsl:value-of 
									select="$eventRepeatKey"/>} -->
								<xsl:variable name="eventPosition">
									<xsl:for-each
										select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef">
										<xsl:if test="@OID = $eventOID">
											<xsl:copy-of select="position()" />
										</xsl:if>
									</xsl:for-each>
								</xsl:variable>

								<xsl:choose>
									<xsl:when test="$colEventPosition = $eventPosition"><!--{event matched} -->
										<xsl:choose>
											<xsl:when test="$isColForRepeatingEvent"><!--{repeating event } -->
												<xsl:choose>
													<xsl:when test="$colRepeatEventKey = $eventRepeatKey"><!--{event repeat match} -->
														<xsl:for-each select="./odm:FormData">
															<xsl:variable name="formOID" select="@FormOID" /><!--{formOID:<xsl:value-of 
																select="$formOID"/>} -->
															<!-- find crf position -->
															<xsl:variable name="matchingCRFRef"
																select="$allEventDefs/odm:FormRef[@FormOID = $formOID and ../@OID = $eventOID]" />
															<xsl:variable name="formRefNodeId"
																select="generate-id($matchingCRFRef)" />
															<xsl:variable name="crfPosition">
																<xsl:for-each select="$allFormRefElements">
																	<xsl:if test="@FormOID = $formOID">
																		<xsl:if test="$formRefNodeId = generate-id()">
																			<xsl:copy-of select="position()" />
																		</xsl:if>
																	</xsl:if>
																</xsl:for-each>
															</xsl:variable>
															<!--{crfPosition:<xsl:value-of select="$crfPosition"/>} -->
															<xsl:choose>
																<xsl:when
																	test="$crfPosition = normalize-space($colCrfPosition)"><!--{crf matched} -->
																	<xsl:for-each select="./odm:ItemGroupData">
																		<xsl:variable name="grpOID" select="@ItemGroupOID" /><!--{grp 
																			OID<xsl:value-of select="$grpOID"/>} -->
																		<xsl:variable name="grpRepeatKey" select="@ItemGroupRepeatKey" />
																		<xsl:choose>
																			<xsl:when test="$isColForRepeatingGrp"><!--{grp repeating}{grp RepeatKey:*<xsl:value-of 
																					select="$grpRepeatKey"/>*}{colRepeatGrpKey:*<xsl:value-of select="$colRepeatGrpKey"/>*} -->
																				<xsl:choose>
																					<xsl:when
																						test="$grpRepeatKey = normalize-space($colRepeatGrpKey)"><!--{both event and grp repeating } -->
																						<!-- check item name -->
																						<xsl:for-each select="./odm:ItemData">
																							<xsl:variable name="itemOID" select="@ItemOID" />
																							<xsl:variable name="itemName"
																								select="//odm:ItemDef[@OID = $itemOID]/@Name" />

																							<xsl:choose>
																								<xsl:when
																									test="normalize-space($colItemName) = $itemName"><!--{item name matched} -->
																									<xsl:value-of select="$matchSep" />
																									<xsl:value-of select="$eventOID" />
																									<xsl:value-of select="$mValSeparator1" />
																									<xsl:text>_</xsl:text>
																									<xsl:value-of select="$formOID" />
																									<xsl:value-of select="$mValSeparator2" />
																									<xsl:text>_</xsl:text>
																									<xsl:value-of select="$grpOID" />
																									<xsl:value-of select="$mValSeparator3" />
																									<xsl:text>_</xsl:text>
																									<xsl:value-of select="$itemOID" />
																									<xsl:value-of select="$mValSeparator4" />
																									<xsl:text>_</xsl:text>
																									<xsl:value-of select="$colRepeatEventKey" />
																									<xsl:value-of select="$mValSeparator5" />
																									<xsl:text>_</xsl:text>
																									<xsl:value-of select="$grpRepeatKey" />
																								</xsl:when>
																								<xsl:otherwise>
																									<xsl:value-of select="$nonMatchSep" />
																								</xsl:otherwise>
																							</xsl:choose>
																						</xsl:for-each>
																					</xsl:when>
																					<xsl:otherwise>
																						<xsl:value-of select="$nonMatchSep" />
																					</xsl:otherwise>
																				</xsl:choose>
																			</xsl:when>
																			<xsl:otherwise>
																				<!-- check item name -->
																				<xsl:for-each select="./odm:ItemData">
																					<xsl:variable name="itemOID" select="@ItemOID" />
																					<xsl:variable name="itemName"
																						select="//odm:ItemDef[@OID = $itemOID]/@Name" />
																					<xsl:choose>
																						<xsl:when
																							test="normalize-space($colItemName) = $itemName">
																							<xsl:value-of select="$matchSep" />
																							<xsl:value-of select="$eventOID" />
																							<xsl:value-of select="$mValSeparator1" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$formOID" />
																							<xsl:value-of select="$mValSeparator2" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$grpOID" />
																							<xsl:value-of select="$mValSeparator3" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$itemOID" />
																							<xsl:value-of select="$mValSeparator4" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$colRepeatEventKey" />
																						</xsl:when>
																						<xsl:otherwise>
																							<!--<xsl:text>N</xsl:text> -->
																							<xsl:value-of select="$nonMatchSep" />
																						</xsl:otherwise>
																					</xsl:choose>
																				</xsl:for-each>
																			</xsl:otherwise>
																		</xsl:choose>
																	</xsl:for-each>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="$nonMatchSep" />
																</xsl:otherwise>
															</xsl:choose>

														</xsl:for-each>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="$nonMatchSep" />
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise><!--non-repeating event -->

												<xsl:for-each select="./odm:FormData">
													<xsl:variable name="formOID" select="@FormOID" /><!--formOID:<xsl:value-of 
														select="$formOID"/> -->
													<!-- find crf position -->

													<xsl:variable name="matchingCRFRef"
														select="$allEventDefs/odm:FormRef[@FormOID = $formOID and ../@OID = $eventOID]" />

													<xsl:variable name="formRefNodeId"
														select="generate-id($matchingCRFRef)" />
													<xsl:variable name="crfPosition">
														<xsl:for-each select="$allFormRefElements">
															<xsl:if test="@FormOID = $formOID">
																<xsl:if test="$formRefNodeId = generate-id()">
																	<xsl:copy-of select="position()" />
																</xsl:if>
															</xsl:if>
														</xsl:for-each>
													</xsl:variable>
													<!--crfPosition:<xsl:value-of select="$crfPosition"/> -->
													<xsl:choose>
														<xsl:when
															test="$crfPosition = normalize-space($colCrfPosition)"><!--crf matched -->
															<xsl:for-each select="./odm:ItemGroupData">
																<xsl:variable name="grpOID" select="@ItemGroupOID" /><!--grpOID:<xsl:value-of 
																	select="$grpOID"/> -->
																<xsl:variable name="grpRepeatKey" select="@ItemGroupRepeatKey" /><!--grpRepeatKey:<xsl:value-of 
																	select="$grpRepeatKey"/> -->
																<xsl:choose>
																	<xsl:when test="$isColForRepeatingGrp">
																		<xsl:choose>
																			<xsl:when
																				test="$grpRepeatKey = normalize-space($colRepeatGrpKey)"><!--grp matched -->
																				<!-- check item name -->
																				<xsl:for-each select="./odm:ItemData">
																					<xsl:variable name="itemOID" select="@ItemOID" />
																					<xsl:variable name="itemName"
																						select="//odm:ItemDef[@OID = $itemOID]/@Name" />

																					<xsl:choose>
																						<xsl:when
																							test="normalize-space($colItemName) = $itemName"><!-- only grp repeating -->
																							<xsl:value-of select="$matchSep" />
																							<xsl:value-of select="$eventOID" />
																							<xsl:value-of select="$mValSeparator1" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$formOID" />
																							<xsl:value-of select="$mValSeparator2" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$grpOID" />
																							<xsl:value-of select="$mValSeparator3" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$itemOID" />
																							<xsl:value-of select="$mValSeparator5" />
																							<xsl:text>_</xsl:text>
																							<xsl:value-of select="$grpRepeatKey" />
																						</xsl:when>
																						<xsl:otherwise>
																							<xsl:value-of select="$nonMatchSep" />
																						</xsl:otherwise>
																					</xsl:choose>
																				</xsl:for-each>
																			</xsl:when>
																			<xsl:otherwise>
																				<xsl:value-of select="$nonMatchSep" />
																			</xsl:otherwise>
																		</xsl:choose>
																	</xsl:when>
																	<xsl:otherwise>
																		<!-- check item name -->
																		<xsl:for-each select="./odm:ItemData">
																			<xsl:variable name="itemOID" select="@ItemOID" /><!--itemOID:<xsl:value-of 
																				select="$itemOID"/> -->
																			<xsl:variable name="itemName"
																				select="//odm:ItemDef[@OID = $itemOID]/@Name" /><!--itemName:<xsl:value-of 
																				select="$itemName"/> -->
																			<xsl:choose>
																				<xsl:when
																					test="normalize-space($colItemName) = $itemName"><!-- nothing repeating -->
																					<xsl:value-of select="$matchSep" />
																					<xsl:value-of select="$eventOID" />
																					<xsl:value-of select="$mValSeparator1" />
																					<xsl:text>_</xsl:text>
																					<xsl:value-of select="$formOID" />
																					<xsl:value-of select="$mValSeparator2" />
																					<xsl:text>_</xsl:text>
																					<xsl:value-of select="$grpOID" />
																					<xsl:value-of select="$mValSeparator3" />
																					<xsl:text>_</xsl:text>
																					<xsl:value-of select="$itemOID" />
																				</xsl:when>
																				<xsl:otherwise>
																					<xsl:value-of select="$nonMatchSep" />
																				</xsl:otherwise>
																			</xsl:choose>
																		</xsl:for-each>
																	</xsl:otherwise>
																</xsl:choose>
															</xsl:for-each>
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="$nonMatchSep" />
														</xsl:otherwise>
													</xsl:choose>

												</xsl:for-each>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<!--<xsl:text>N</xsl:text> -->
										<xsl:value-of select="$nonMatchSep" />
									</xsl:otherwise>
								</xsl:choose>

							</xsl:for-each>	<!-- subjectEvents -->
						</xsl:variable>

						<!--ifMatch:-<xsl:value-of select="$ifMatch"/>* -->
						<xsl:choose>
							<xsl:when test="contains($ifMatch, $matchSep)">

								<xsl:variable name="eventOID"
									select="substring-before(substring-after($ifMatch, $matchSep), $mValSeparator1)" />
								<!--eventOID:*<xsl:value-of select="$eventOID"/>* -->

								<xsl:variable name="formOID"
									select="substring-before(substring-after($ifMatch, concat($mValSeparator1,'_')), concat($mValSeparator2,'_'))" />
								<!--formOID: *<xsl:value-of select="$formOID"/>* -->
								<xsl:variable name="grpOID"
									select="substring-before(substring-after($ifMatch, concat($mValSeparator2,'_')), concat($mValSeparator3,'_'))" />
								<!--grpOID: *<xsl:value-of select="$grpOID"/>* -->
								<!--<xsl:variable name="itemOID" select="substring-before(substring-after($ifMatch, 
									concat($mValSeparator3,'_')), concat($mValSeparator4,'_'))"/> -->

								<xsl:variable name="itemOID">
									<xsl:choose>
										<xsl:when test="$isColForRepeatingEvent"><!-- only event repeating or both event 
												and grp repeating -->
											<xsl:value-of
												select="substring-before(substring-after($ifMatch, concat($mValSeparator3,'_')), concat($mValSeparator4,'_'))" />
										</xsl:when>
										<xsl:when
											test="not($isColForRepeatingEvent) and $isColForRepeatingGrp">
											<xsl:value-of
												select="substring-before(substring-after($ifMatch, concat($mValSeparator3,'_')), concat($mValSeparator5,'_'))" />
										</xsl:when>
										<xsl:otherwise><!-- nothing repeating -->
											<xsl:variable name="afterSep3"
												select="substring-after($ifMatch, concat($mValSeparator3,'_'))" />
											<xsl:choose>
												<xsl:when test="contains($afterSep3,$nonMatchSep)">
													<xsl:value-of select="substring-before($afterSep3,$nonMatchSep)" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$afterSep3" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<!--itemOID: *<xsl:value-of select="$itemOID"/>* -->
								<xsl:variable name="eventRepeatKey">
									<xsl:if test="contains($ifMatch, $mValSeparator4)">
										<xsl:variable name="afterSep4"
											select="substring-after($ifMatch, concat($mValSeparator4,'_'))" />
										<xsl:choose>
											<xsl:when test="contains($afterSep4, $nonMatchSep)">
												<xsl:variable name="beforeN"
													select="substring-before($afterSep4, $nonMatchSep)" />
												<xsl:choose>
													<xsl:when test="contains($beforeN, $mValSeparator5)">
														<xsl:value-of
															select="substring-before($beforeN, $mValSeparator5)" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="$beforeN" />
													</xsl:otherwise>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise>
												<xsl:choose>
													<xsl:when test="contains($afterSep4, $mValSeparator5)">
														<xsl:value-of
															select="substring-before($afterSep4, concat($mValSeparator5,'_'))" />
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="$afterSep4" />
													</xsl:otherwise>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:if>
								</xsl:variable>
								<!-- *<xsl:value-of select="$eventRepeatKey"/>* -->
								<xsl:variable name="grpRepeatKey">
									<xsl:if test="contains($ifMatch, $mValSeparator5)">
										<xsl:variable name="afterSep5"
											select="substring-after($ifMatch, concat($mValSeparator5,'_'))" />
										<xsl:choose>
											<xsl:when test="contains($afterSep5, $nonMatchSep)">
												<xsl:value-of select="substring-before($afterSep5, $nonMatchSep)" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$afterSep5" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:if>
								</xsl:variable>
								<!--grpRepeatKey: *<xsl:value-of select="$grpRepeatKey"/>* -->

								<xsl:choose>
									<xsl:when test="$isColForRepeatingEvent and $isColForRepeatingGrp">
										<xsl:variable name="itemData"
											select="$subjectItems[@ItemOID = $itemOID  
									 and ../@ItemGroupOID=$grpOID  and ../@ItemGroupRepeatKey = $grpRepeatKey
									 and ../../@FormOID = $formOID 
									 and ../../../@StudyEventOID = $eventOID and ../../../@StudyEventRepeatKey = $eventRepeatKey]" />
										<!--itemData oid:<xsl:value-of select="$itemData/@ItemOID"/> -->
										<xsl:choose>
											<xsl:when test="$itemData/@Value">
												<xsl:value-of select="$itemData/@Value" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:when test="$itemData/@OpenClinica:ReasonForNull">
												<xsl:value-of select="$itemData/@OpenClinica:ReasonForNull" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$delimiter" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:when
										test="$isColForRepeatingEvent and not($isColForRepeatingGrp)">
										<xsl:variable name="itemData"
											select="$subjectItems[@ItemOID = $itemOID  
									 and ../@ItemGroupOID=$grpOID
									 and ../../@FormOID = $formOID 
									 and ../../../@StudyEventOID = $eventOID and ../../../@StudyEventRepeatKey = $eventRepeatKey]" />
										<!--itemData oid:<xsl:value-of select="$itemData/@ItemOID"/> -->
										<xsl:choose>
											<xsl:when test="$itemData/@Value">
												<xsl:value-of select="$itemData/@Value" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:when test="$itemData/@OpenClinica:ReasonForNull">
												<xsl:value-of select="$itemData/@OpenClinica:ReasonForNull" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$delimiter" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:when
										test="not($isColForRepeatingEvent) and $isColForRepeatingGrp">
										<xsl:variable name="itemData"
											select="$subjectItems[@ItemOID = $itemOID  
									 and ../@ItemGroupOID=$grpOID  and ../@ItemGroupRepeatKey = $grpRepeatKey
									 and ../../@FormOID = $formOID 
									 and ../../../@StudyEventOID = $eventOID]" />
										<!-- itemData oid:<xsl:value-of select="$itemData/@ItemOID"/> -->
										<xsl:choose>
											<xsl:when test="$itemData/@Value">
												<xsl:value-of select="$itemData/@Value" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:when test="$itemData/@OpenClinica:ReasonForNull">
												<xsl:value-of select="$itemData/@OpenClinica:ReasonForNull" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$delimiter" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="itemData"
											select="$subjectItems[@ItemOID = $itemOID  
									 and ../@ItemGroupOID=$grpOID 
									 and ../../@FormOID = $formOID 
									 and ../../../@StudyEventOID = $eventOID]" />
										<!-- itemData oid:<xsl:value-of select="$itemData/@ItemOID"/> -->
										<xsl:choose>
											<xsl:when test="$itemData/@Value">
												<xsl:value-of select="$itemData/@Value" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:when test="$itemData/@OpenClinica:ReasonForNull">
												<xsl:value-of select="$itemData/@OpenClinica:ReasonForNull" />
												<xsl:value-of select="$delimiter" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$delimiter" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>



							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$delimiter" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise><!--crf column col type:<xsl:value-of select="$colType"/> -->

						<!-- iterate study events. for each study event; if repeating - check 
							if crf in column name is present and event info matched as well. If yes write 
							the data. same for non-repeating except the event repeat key will not be 
							matched. if matched write data else write empty column -->
						<xsl:variable name="ifMatch">
							<xsl:for-each select="$subjectEvents">
								<xsl:variable name="eventOID" select="@StudyEventOID" /><!--eventOID:<xsl:value-of 
									select="$eventOID"/> -->
								<xsl:variable name="eventPosition">
									<xsl:for-each
										select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef">
										<xsl:if test="@OID = $eventOID">
											<xsl:copy-of select="position()" />
										</xsl:if>
									</xsl:for-each>
								</xsl:variable><!--eventPosition: <xsl:value-of select="$eventPosition"/> -->
								<xsl:choose>
									<xsl:when test="normalize-space($colEventPosition) = $eventPosition"><!--event matched -->
										<xsl:for-each select="./odm:FormData">
											<xsl:variable name="formOID" select="@FormOID" /><!--formOID:<xsl:value-of 
												select="$formOID"/> -->
											<!-- find crf position -->
											<xsl:variable name="matchingCRFRef"
												select="$allEventDefs/odm:FormRef[@FormOID = $formOID and ../@OID = $eventOID]" />
											<xsl:variable name="formRefNodeId"
												select="generate-id($matchingCRFRef)" />
											<xsl:variable name="crfPosition">
												<xsl:for-each select="$allFormRefElements">
													<xsl:if test="@FormOID = $formOID">
														<xsl:if test="$formRefNodeId = generate-id()">
															<xsl:copy-of select="position()" />
														</xsl:if>
													</xsl:if>
												</xsl:for-each>
											</xsl:variable><!--crfPosition: *<xsl:value-of select="$crfPosition"/>* -->

											<xsl:choose>
												<xsl:when test="$crfPosition = normalize-space($colCrfPosition)"><!--crf matched -->
													<xsl:choose>
														<xsl:when test="$isColForRepeatingEvent"><!--col for repeating event -->
															<xsl:choose>
																<xsl:when
																	test="../@StudyEventRepeatKey = normalize-space($colRepeatEventKey)"><!--event repeat key matched -->
																	<xsl:value-of select="$matchSep" />
																	<xsl:value-of select="$eventOID" />
																	<xsl:value-of select="$mValSeparator1" />
																	<xsl:value-of select="$formOID" />
																	<xsl:value-of select="$mValSeparator2" />
																	<xsl:value-of select="../@StudyEventRepeatKey" />
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="$nonMatchSep" /><!--event 
																		repeat key mismatch -->
																</xsl:otherwise>
															</xsl:choose>
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="$matchSep" />
															<xsl:value-of select="$eventOID" />
															<xsl:value-of select="$mValSeparator1" />
															<xsl:value-of select="$formOID" />
															<!--match for non-repeating event -->
														</xsl:otherwise>
													</xsl:choose>
												</xsl:when>
												<xsl:otherwise><!--crf mismatch -->
													<xsl:value-of select="$nonMatchSep" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$nonMatchSep" /><!--event mismatch -->
									</xsl:otherwise>
								</xsl:choose>

							</xsl:for-each>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="contains($ifMatch, $matchSep)">
								<xsl:variable name="eventOID"
									select="substring-before(substring-after($ifMatch, $matchSep), $mValSeparator1)" />
								<xsl:variable name="formOID">
									<xsl:choose>
										<xsl:when test="$isColForRepeatingEvent">
											<xsl:value-of
												select="substring-before(substring-after($ifMatch, $mValSeparator1), $mValSeparator2)" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:variable name="afterSep1"
												select="substring-after($ifMatch, $mValSeparator1)" />
											<xsl:choose>
												<xsl:when test="contains($afterSep1, $nonMatchSep)">
													<xsl:value-of select="substring-before($afterSep1, $nonMatchSep)" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$afterSep1" />
												</xsl:otherwise>
											</xsl:choose>

										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
								<!--formOID: *<xsl:value-of select="$formOID"/>* -->
								<xsl:variable name="eventRepeatKey">
									<xsl:if test="$isColForRepeatingEvent">
										<xsl:variable name="afterSep1"
											select="substring-after($ifMatch,$mValSeparator2 )" />
										<xsl:choose>
											<xsl:when test="contains($afterSep1, $nonMatchSep)">
												<xsl:value-of select="substring-before($afterSep1, $nonMatchSep)" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="$afterSep1" />
											</xsl:otherwise>
										</xsl:choose>

									</xsl:if>
								</xsl:variable><!--eventRepeatKey: <xsl:value-of select="$eventRepeatKey"/> -->
								<xsl:variable name="eventT"
									select="$subjectEvents[@StudyEventOID = $eventOID]" />

								<xsl:choose>
									<xsl:when test="$isColForRepeatingEvent">
										<xsl:variable name="formData"
											select="$subjectEvents/odm:FormData[@FormOID = $formOID and ../@StudyEventOID = $eventOID and ../@StudyEventRepeatKey = $eventRepeatKey]" />
										<xsl:if test="$colType = 'Interviewer'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:InterviewerName">
													<xsl:value-of select="$formData/@OpenClinica:InterviewerName"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
										<xsl:if test="$colType = 'InterviewDate'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:InterviewDate">
													<xsl:value-of select="$formData/@OpenClinica:InterviewDate"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
										<xsl:if test="$colType = 'CRF Version Status'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:Status">
													<xsl:value-of select="$formData/@OpenClinica:Status"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
										<xsl:if test="$colType = 'Version Name'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:Version">
													<xsl:value-of select="$formData/@OpenClinica:Version"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="formData"
											select="$subjectEvents/odm:FormData[@FormOID = $formOID and ../@StudyEventOID = $eventOID]" />

										<!--formData oid: *<xsl:value-of select="$formData/@FormOID"/>* -->
										<xsl:if test="$colType = 'Interviewer'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:InterviewerName">
													<xsl:value-of select="$formData/@OpenClinica:InterviewerName"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
										<xsl:if test="$colType = 'InterviewDate'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:InterviewDate">
													<xsl:value-of select="$formData/@OpenClinica:InterviewDate"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
										<xsl:if test="$colType = 'CRF Version Status'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:Status">
													<xsl:value-of select="$formData/@OpenClinica:Status"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
										<xsl:if test="$colType = 'Version Name'">
											<xsl:choose>
												<xsl:when test="$formData/@OpenClinica:Version">
													<xsl:value-of select="$formData/@OpenClinica:Version"></xsl:value-of>
													<xsl:value-of select="$delimiter" />
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="$delimiter" />
												</xsl:otherwise>
											</xsl:choose>
										</xsl:if>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="$colType = 'Interviewer'">
									<xsl:value-of select="$delimiter" />
								</xsl:if>
								<xsl:if test="$colType = 'InterviewDate'">
									<xsl:value-of select="$delimiter" />
								</xsl:if>
								<xsl:if test="$colType = 'CRF Version Status'">
									<xsl:value-of select="$delimiter" />
								</xsl:if>
								<xsl:if test="$colType = 'Version Name'">
									<xsl:value-of select="$delimiter" />
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>

					</xsl:otherwise>

				</xsl:choose>
			</xsl:if>
		</xsl:for-each>

	</xsl:template>




	<xsl:template mode="getStudyEventInfoHeadersForDataOutput"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef">

		<xsl:variable name="eventDefOID" select="@OID" />
		<xsl:variable name="isRepeating" select="@Repeating" />

		<xsl:variable name="eventPosition">
			<xsl:copy-of select="position()" />
		</xsl:variable>

		<!-- maximum value of StudyEventRepeatKey for an event -->
		<xsl:variable name="MaxEventRepeatKey">
			<xsl:for-each
				select="//odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData/@StudyEventRepeatKey">
				<xsl:sort data-type="number" />
				<xsl:if test="position() = last()">
					<xsl:value-of select="." />
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:choose>
			<xsl:when test="$isRepeating = 'Yes'">
				<!-- write event data header columns for repeating event -->
				<xsl:apply-templates select="."
					mode="createColHeadersForRepeatingEventForDataOutput">
					<xsl:with-param name="eventRepeatCnt" select="1" />
					<xsl:with-param name="eventOID" select="$eventDefOID" />
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<!-- write event data header columns for non repeating event -->
				<xsl:apply-templates select="."
					mode="createColHeadersForNonRepeatingEventForDataOutput">
					<xsl:with-param name="eventPosition" select="$eventPosition" />
					<xsl:with-param name="eventOID" select="$eventDefOID" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>




	<xsl:template name="createColHeadersForRepeatingEventForDataOutput"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef" mode="createColHeadersForRepeatingEventForDataOutput">
		<xsl:param name="eventOID" />
		<xsl:param name="eventPosition" />
		<xsl:param name="eventRepeatCnt" />
		<xsl:param name="MaxEventRepeatKey" />

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt and @OpenClinica:StudyEventLocation]) gt 0">
			<xsl:text>Location_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$eventRepeatCnt" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt and @OpenClinica:StartDate]) gt 0">
			<xsl:text>StartDate_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$eventRepeatCnt" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt and @OpenClinica:EndDate]) gt 0">
			<xsl:text>EndDate_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$eventRepeatCnt" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt and @OpenClinica:Status]) gt 0">
			<xsl:text>Event Status_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$eventRepeatCnt" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt and @OpenClinica:SubjectAgeAtEvent]) gt 0">
			<xsl:text>Age_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:text>_</xsl:text>
			<xsl:value-of select="$eventRepeatCnt" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<!-- fix for issue 11832: corrected to repeat the process for next incremental 
			event repeat key until it reaches the value of "MaxEventRepeatKey" -->
		<xsl:if test="($eventRepeatCnt+1) le number($MaxEventRepeatKey)">
			<xsl:call-template name="createColHeadersForRepeatingEventForDataOutput">
				<xsl:with-param name="eventRepeatCnt" select="$eventRepeatCnt+1" />
				<xsl:with-param name="eventOID" select="$eventOID" />
				<xsl:with-param name="eventPosition" select="$eventPosition" />
				<xsl:with-param name="MaxEventRepeatKey" select="$MaxEventRepeatKey" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>




	<xsl:template mode="createColHeadersForNonRepeatingEventForDataOutput"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef">
		<xsl:param name="eventPosition" />
		<xsl:param name="eventOID" />

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @OpenClinica:StudyEventLocation]) gt 0">
			<xsl:text>Location_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @OpenClinica:StartDate]) gt 0">
			<xsl:text>StartDate_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @OpenClinica:EndDate]) gt 0">
			<xsl:text>EndDate_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @OpenClinica:Status]) gt 0">
			<xsl:text>Event Status_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>

		<xsl:if
			test="count($allStudyEventDataElements[@StudyEventOID = $eventOID and @OpenClinica:SubjectAgeAtEvent]) gt 0">
			<xsl:text>Age_</xsl:text>
			<xsl:value-of select="$E" />
			<xsl:value-of select="$eventPosition" />
			<xsl:value-of select="$delimiter" />
		</xsl:if>
	</xsl:template>
	
	
	
	
	<xsl:template mode="studyEventInfoHeaders"
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef">

		<xsl:param name="columnHeaderToDisplay" as="xs:string"/>

		<xsl:choose>
			<xsl:when test="@Repeating = 'Yes'">
			
				<!-- maximum value of StudyEventRepeatKey for an event -->
				<xsl:variable name="eventOID" select="@OID" as="xs:string" />
				<xsl:variable name="MaxEventRepeatKey" 
						select="max($allStudyEventDataElements[@StudyEventOID = $eventOID]/xs:integer(@StudyEventRepeatKey))" 
						as="xs:integer?" />
			
				<!-- write event data header columns for repeating event -->
				<xsl:value-of 
					select="string-join(for $eventRepeatCnt in (1 to $MaxEventRepeatKey) 
											return myFunc:getColHeadersForEvent(@OID, @Name, position(), @Repeating, 
															$eventRepeatCnt, $columnHeaderToDisplay), '')" />
			</xsl:when>
			<xsl:otherwise>
				<!-- write event data header columns for non repeating event -->
				<xsl:value-of select="myFunc:getColHeadersForEvent(@OID, @Name, position(), @Repeating, 0,
											$columnHeaderToDisplay)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	
	
	<xsl:function name = "myFunc:getColHeadersForEvent" as = "xs:string">
		<xsl:param name="eventOID" as = "xs:string"/>
		<xsl:param name="eventName" as = "xs:string" />
		<xsl:param name="eventDefPos" as = "xs:integer" />
		<xsl:param name="isRepeatingEvent" as="xs:string" />
		<xsl:param name="eventRepeatCnt" as = "xs:integer" />
		<xsl:param name="columnHeaderToDisplay" as = "xs:string" />

		<xsl:variable name="itemCode" 
			select="concat('_E', $eventDefPos, 
							if ($isRepeatingEvent = 'Yes') then concat('_', $eventRepeatCnt) else '')" 
			as="xs:string"/>

		<xsl:sequence select="if ($isRepeatingEvent = 'Yes')
								then myFunc:createEventColumnHeaders(concat($eventName, ' (', $eventRepeatCnt, ')'), $itemCode,
										($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt]), 
										$columnHeaderToDisplay)
								else myFunc:createEventColumnHeaders($eventName, $itemCode,
										($allStudyEventDataElements[@StudyEventOID = $eventOID]), $columnHeaderToDisplay)" />	
						
	</xsl:function>
	
	
	
	
	<xsl:function name="myFunc:createEventColumnHeaders" as="xs:string">
		<xsl:param name="eventHeader" as="xs:string" />
		<xsl:param name="itemCode" as = "xs:string" />
		<xsl:param name="studyEventDataElementsFiltered" as="node()*" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />

		<xsl:sequence select =
			"concat((if (count($studyEventDataElementsFiltered[@OpenClinica:StudyEventLocation]) > 0)
						then (if ($columnHeaderToDisplay = $colHeaderEventName)
								then concat($eventHeader, $delimiter)
							  else if ($columnHeaderToDisplay = $colHeaderItemName)
							  	then concat('Location', $itemCode, $delimiter)
							  else $delimiter)
						else ''),
					(if (count($studyEventDataElementsFiltered[@OpenClinica:StartDate]) > 0)
						then (if ($columnHeaderToDisplay = $colHeaderEventName)
								then concat($eventHeader, $delimiter)
							  else if ($columnHeaderToDisplay = $colHeaderItemName)
							  	then concat('Start Date', $itemCode, $delimiter)
							  else $delimiter)
						else ''),
					(if (count($studyEventDataElementsFiltered[@OpenClinica:EndDate]) > 0)
						then (if ($columnHeaderToDisplay = $colHeaderEventName)
								then concat($eventHeader, $delimiter)
							  else if ($columnHeaderToDisplay = $colHeaderItemName)
							  	then concat('End Date', $itemCode, $delimiter)
							  else $delimiter)
						else ''),
					(if (count($studyEventDataElementsFiltered[@OpenClinica:Status]) > 0)
						then (if ($columnHeaderToDisplay = $colHeaderEventName)
								then concat($eventHeader, $delimiter)
							  else if ($columnHeaderToDisplay = $colHeaderItemName)
							  	then concat('Event Status', $itemCode, $delimiter)
							  else $delimiter)
						else ''),
					(if (count($studyEventDataElementsFiltered[@OpenClinica:SubjectAgeAtEvent]) > 0)
						then (if ($columnHeaderToDisplay = $colHeaderEventName)
								then concat($eventHeader, $delimiter)
							  else if ($columnHeaderToDisplay = $colHeaderItemName)
							  	then concat('Age', $itemCode, $delimiter)
							  else $delimiter)
						else ''))" />
						
	</xsl:function>
	
	
	
	
	<xsl:template
		match="/odm:ODM/odm:Study/odm:MetaDataVersion/odm:StudyEventDef"
		mode="studyFormAndItemInfoHeaders">

		<xsl:param name="columnHeaderToDisplay" as="xs:string" />

		<xsl:variable name="eventOID" select="@OID" as="xs:string" />

		<xsl:choose>
			<xsl:when test="@Repeating = 'Yes'">
			
				<!-- maximum value of StudyEventRepeatKey for an event -->
				<xsl:variable name="MaxEventRepeatKey" 
						select="max($allStudyEventDataElements[@StudyEventOID = $eventOID]/xs:integer(@StudyEventRepeatKey))" 
						as="xs:integer?" />
			
				<!-- write CRF data header columns for repeating event -->
				<xsl:value-of 
					select="string-join((for $eventRepeatCnt in (1 to $MaxEventRepeatKey) 
											return for $formRefNode in ./odm:FormRef 
														return myFunc:getFormColumnHeadersForEvent(@OID, @Name, position(), @Repeating, $eventRepeatCnt,
																		$formRefNode, $columnHeaderToDisplay)), '')" />
						
				<!-- write Item data header columns for repeating event -->
				<xsl:value-of 
					select="string-join((for $eventRepeatCnt in (1 to $MaxEventRepeatKey) 
											return for $formRefNode in ./odm:FormRef 
														return if ($allStudyEventDataElements[@StudyEventOID = $eventOID and @StudyEventRepeatKey = $eventRepeatCnt]
																	/odm:FormData[@FormOID = $formRefNode/@FormOID])
																	then (myFunc:getItemColumnHeadersForEvent(@OID, @Name, position(), @Repeating, $eventRepeatCnt,
																			$formRefNode, $columnHeaderToDisplay))
																	else ''), '')" />
						
			</xsl:when>
			<xsl:otherwise>
				
				<!-- write CRF data header columns for non repeating event -->
				<xsl:value-of 
					select="string-join(for $formRefNode in ./odm:FormRef 
											return myFunc:getFormColumnHeadersForEvent(@OID, @Name, position(), @Repeating, 0,
														$formRefNode, $columnHeaderToDisplay), '')" />
						
				<!-- write Item data header columns for non repeating event -->
				<xsl:value-of 
					select="string-join((for $formRefNode in ./odm:FormRef 
											return if ($allStudyEventDataElements[@StudyEventOID = $eventOID]/odm:FormData[@FormOID = $formRefNode/@FormOID])
														then (myFunc:getItemColumnHeadersForEvent(@OID, @Name, position(), @Repeating, 0,
																	$formRefNode, $columnHeaderToDisplay))
														else ''), '')" />		
						
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	
	
	
	<xsl:function name="myFunc:getFormColumnHeadersForEvent" as="xs:string">
		<xsl:param name="eventOID" as="xs:string" />
		<xsl:param name="eventName" as="xs:string" />
		<xsl:param name="eventDefPos" as="xs:integer" />
		<xsl:param name="isRepeatingEvent" as="xs:string" />
		<xsl:param name="eventRepeatCnt" as="xs:integer" />
		<xsl:param name="formRefNode" as="node()" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />
		
		<xsl:variable name="formDefNode" select="$allFormDefElements[@OID = $formRefNode/@FormOID]" as="node()"/>
		<xsl:variable name="formOID" select="$formDefNode/@OID" as="xs:string"/>
		<xsl:variable name="formName" select="$formDefNode/@Name" as="xs:string"/>
		
		<xsl:variable name="itemCode" 
			select="concat('_E', $eventDefPos, 
							if ($isRepeatingEvent = 'Yes') then concat('_', $eventRepeatCnt) else '',
							'_', myFunc:getCRFCode($formRefNode))" 
			as="xs:string"/>
			
		<xsl:sequence select="if ($isRepeatingEvent = 'Yes')
								then myFunc:createFormColumnHeaders(concat($eventName, ' (', $eventRepeatCnt, ')'), $formName, $itemCode,
											($allFormDataElements[../@StudyEventOID = $eventOID and ../@StudyEventRepeatKey = $eventRepeatCnt and @FormOID = $formOID]), 
											$columnHeaderToDisplay)
								else myFunc:createFormColumnHeaders($eventName, $formName, $itemCode,
											($allFormDataElements[../@StudyEventOID = $eventOID and @FormOID = $formOID]), 
											$columnHeaderToDisplay)" />	
	
	</xsl:function>
	
	
	
	
	<xsl:function name="myFunc:createFormColumnHeaders" as="xs:string">
		<xsl:param name="eventHeader" as="xs:string" />
		<xsl:param name="formHeader" as="xs:string" />
		<xsl:param name="itemCode" as="xs:string" />
		<xsl:param name="formDataElementsFiltered" as="node()*" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />
			
		<xsl:sequence select=
					"concat((if (count($formDataElementsFiltered[@OpenClinica:InterviewerName]) > 0)
								then (if ($columnHeaderToDisplay = $colHeaderEventName)
										then concat($eventHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderCRFName)
										then concat($formHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderItemName)
									  	then concat('Interviewer', $itemCode, $delimiter)
									  else $delimiter)
								else ''),
							(if (count($formDataElementsFiltered[@OpenClinica:InterviewDate]) > 0)
								then (if ($columnHeaderToDisplay = $colHeaderEventName)
										then concat($eventHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderCRFName)
										then concat($formHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderItemName)
									  	then concat('Interview Date', $itemCode, $delimiter)
									  else $delimiter)
								else ''),
							(if (count($formDataElementsFiltered[@OpenClinica:Status]) > 0)
								then (if ($columnHeaderToDisplay = $colHeaderEventName)
										then concat($eventHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderCRFName)
										then concat($formHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderItemName)
									  	then concat('CRF Version Status', $itemCode, $delimiter)
									  else $delimiter)
								else ''),
							(if (count($formDataElementsFiltered[@OpenClinica:Version]) > 0)
								then (if ($columnHeaderToDisplay = $colHeaderEventName)
										then concat($eventHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderCRFName)
										then concat($formHeader, $delimiter)
									  else if ($columnHeaderToDisplay = $colHeaderItemName)
									  	then concat('Version Name', $itemCode, $delimiter)
									  else $delimiter)
								else ''))" />

	</xsl:function>
	
	
	
	
	<xsl:function name="myFunc:getItemColumnHeadersForEvent" as="xs:string">
		<xsl:param name="eventOID" as="xs:string" />
		<xsl:param name="eventName" as="xs:string" />
		<xsl:param name="eventDefPos" as = "xs:integer" />
		<xsl:param name="isRepeatingEvent" as="xs:string" />
		<xsl:param name="eventRepeatCnt" as="xs:integer" />
		<xsl:param name="formRefNode" as="node()" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />
			
		<xsl:variable name="formDefNode" select="$allFormDefElements[@OID = $formRefNode/@FormOID]" as="node()"/>	
		<xsl:variable name="formOID" select="$formDefNode/@OID" as="xs:string"/>
		<xsl:variable name="formName" select="$formDefNode/@Name" as="xs:string"/>
			
		<xsl:sequence 
			select="string-join(for $itemGroupRefNode in $formDefNode/odm:ItemGroupRef
									return myFunc:getItemColumnHeadersForGroup($eventOID, $eventName, $eventDefPos, $isRepeatingEvent, $eventRepeatCnt,
												$formRefNode, $formOID, $formName, $allItemGroupDefElements[@OID = $itemGroupRefNode/@ItemGroupOID], 
												$columnHeaderToDisplay), '')" />

	</xsl:function>
	
	
	
	
	<xsl:function name="myFunc:getItemColumnHeadersForGroup" as="xs:string?">
		<xsl:param name="eventOID" as="xs:string" />
		<xsl:param name="eventName" as="xs:string" />
		<xsl:param name="eventDefPos" as = "xs:integer" />
		<xsl:param name="isRepeatingEvent" as="xs:string" />
		<xsl:param name="eventRepeatCnt" as="xs:integer" />
		<xsl:param name="formRefNode" as="node()" />
		<xsl:param name="formOID" as="xs:string" />
		<xsl:param name="formName" as="xs:string" />
		<xsl:param name="itemGroupDefNode" as="node()" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />
		
		<xsl:sequence 
			select="if ($itemGroupDefNode/@Repeating = 'Yes')
						then (for $maxGrpRepeatKey in if ($isRepeatingEvent = 'Yes')
															then max($allItemGrpDataElements[../../@StudyEventOID = $eventOID and ../../@StudyEventRepeatKey = $eventRepeatCnt 
																and ../@FormOID = $formOID and @ItemGroupOID = $itemGroupDefNode/@OID]/xs:integer(@ItemGroupRepeatKey))
															else max($allItemGrpDataElements[../../@StudyEventOID = $eventOID and ../@FormOID = $formOID 
																and @ItemGroupOID = $itemGroupDefNode/@OID]/xs:integer(@ItemGroupRepeatKey))
								return string-join((for $grpRepeatCnt in (1 to $maxGrpRepeatKey)
														return for $itemRefNode in $itemGroupDefNode/odm:ItemRef
																	return myFunc:getItemColumnHeaders($eventOID, $eventName, $eventDefPos, $isRepeatingEvent, $eventRepeatCnt, 
																				$formRefNode, $formOID, $formName, $itemGroupDefNode/@OID, $itemGroupDefNode/@Repeating, $grpRepeatCnt, 
																				$allItemDefElements[@OID = $itemRefNode/@ItemOID], $columnHeaderToDisplay)), ''))
						else (string-join((for $itemRefNode in $itemGroupDefNode/odm:ItemRef
												return myFunc:getItemColumnHeaders($eventOID, $eventName, $eventDefPos, $isRepeatingEvent, $eventRepeatCnt, $formRefNode,
																	$formOID, $formName, $itemGroupDefNode/@OID, $itemGroupDefNode/@Repeating, 0, 
																	$allItemDefElements[@OID = $itemRefNode/@ItemOID], $columnHeaderToDisplay)), ''))" />

	</xsl:function>
	
	
	
	
	<xsl:function name="myFunc:getItemColumnHeaders" as="xs:string">
		<xsl:param name="eventOID" as="xs:string" />
		<xsl:param name="eventName" as="xs:string" />
		<xsl:param name="eventDefPos" as = "xs:integer" />
		<xsl:param name="isRepeatingEvent" as="xs:string" />
		<xsl:param name="eventRepeatCnt" as="xs:integer" />
		<xsl:param name="formRefNode" as="node()" />
		<xsl:param name="formOID" as="xs:string" />
		<xsl:param name="formName" as="xs:string" />
		<xsl:param name="grpOID" as="xs:string" />
		<xsl:param name="isRepeatingGrp" as="xs:string" />
		<xsl:param name="grpRepeatCnt" as="xs:integer" />
		<xsl:param name="itemDefNode" as="node()" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />
			
		<xsl:sequence 
			select="if ($isRepeatingEvent = 'Yes')
						then (if ($isRepeatingGrp = 'Yes')
									then (if (count($allItemDataElements[@ItemOID = $itemDefNode/@OID and ../@ItemGroupOID = $grpOID 
													and ../@ItemGroupRepeatKey =$grpRepeatCnt and ../../@FormOID = $formOID 
													and ../../../@StudyEventOID = $eventOID and ../../../@StudyEventRepeatKey = $eventRepeatCnt]) > 0)
											then for $itemCode in concat('_E', $eventDefPos, '_', $eventRepeatCnt, '_', 
																		myFunc:getCRFCode($formRefNode), '_', $grpRepeatCnt)
													return myFunc:createItemColumnHeaders(concat($eventName, ' (', $eventRepeatCnt, ')'), $formName, 
																concat($itemDefNode/@Comment, ' (', $grpRepeatCnt, ')'), 
																concat($itemDefNode/@Name, $itemCode), $columnHeaderToDisplay)
											else '')
									else (if (count($allItemDataElements[@ItemOID = $itemDefNode/@OID and ../@ItemGroupOID = $grpOID 
													and ../../@FormOID = $formOID and ../../../@StudyEventOID = $eventOID 
													and ../../../@StudyEventRepeatKey = $eventRepeatCnt]) > 0)
											then for $itemCode in concat('_E', $eventDefPos, '_', $eventRepeatCnt, '_', myFunc:getCRFCode($formRefNode))
													return myFunc:createItemColumnHeaders(concat($eventName, ' (', $eventRepeatCnt, ')'), $formName, 
																$itemDefNode/@Comment, concat($itemDefNode/@Name, $itemCode), $columnHeaderToDisplay)
											else ''))
						else (if ($isRepeatingGrp = 'Yes')
									then (if (count($allItemDataElements[@ItemOID = $itemDefNode/@OID and ../@ItemGroupOID = $grpOID 
													and ../@ItemGroupRepeatKey =$grpRepeatCnt and ../../@FormOID = $formOID 
													and ../../../@StudyEventOID = $eventOID]) > 0)
											then for $itemCode in concat('_E', $eventDefPos, '_', myFunc:getCRFCode($formRefNode), '_', $grpRepeatCnt)
													return myFunc:createItemColumnHeaders($eventName, $formName, 
																concat($itemDefNode/@Comment, ' (', $grpRepeatCnt, ')'), 
																concat($itemDefNode/@Name, $itemCode), $columnHeaderToDisplay)
											else '')
									else (if (count($allItemDataElements[@ItemOID = $itemDefNode/@OID and ../@ItemGroupOID = $grpOID 
													and ../../@FormOID = $formOID 
													and ../../../@StudyEventOID = $eventOID]) > 0)
											then for $itemCode in concat('_E', $eventDefPos, '_', myFunc:getCRFCode($formRefNode))
													return myFunc:createItemColumnHeaders($eventName, $formName, $itemDefNode/@Comment, 
																concat($itemDefNode/@Name, $itemCode), $columnHeaderToDisplay)
											else ''))" />	
			
	</xsl:function>




	<xsl:function name="myFunc:createItemColumnHeaders" as="xs:string">
		<xsl:param name="eventHeader" as="xs:string" />
		<xsl:param name="formHeader" as="xs:string" />
		<xsl:param name="ItemDescriptionHeader" as="xs:string" />
		<xsl:param name="ItemNameHeader" as="xs:string" />
		<xsl:param name="columnHeaderToDisplay" as="xs:string" />
		
		
		
		<xsl:sequence select="if ($columnHeaderToDisplay = $colHeaderEventName)
									then concat($eventHeader, $delimiter)
								else if ($columnHeaderToDisplay = $colHeaderCRFName)
									then concat($formHeader, $delimiter)
								else if ($columnHeaderToDisplay = $colHeaderItemDesc)
							  		then concat($ItemDescriptionHeader, $delimiter)
								else concat($ItemNameHeader, $delimiter)" />	

	</xsl:function>	
	
	
	
	
	<xsl:function name="myFunc:getCRFCode" as="xs:string">
		<xsl:param name="formRefNode" as="node()" />
		
		<xsl:variable name="formRefPos"
			select="for $seq in (1 to count($allFormRefElements))
  						return $seq[$allFormRefElements[$seq] is $formRefNode]" 
  			as="xs:integer"/>
		
		<xsl:sequence 
			select="concat('C', $formRefPos)" />	

	</xsl:function>	
	
	
</xsl:stylesheet>
