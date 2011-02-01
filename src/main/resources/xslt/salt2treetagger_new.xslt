<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xmi="http://www.omg.org/XMI" xmlns:sDocumentStructure="sDocumentStructure">
	
	<xsl:output method="text"/>
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<!-- Matching template for nodes -->	
	<xsl:template match="sDocumentStructure:SDocumentGraph">
		<xsl:for-each select="nodes">
			<xsl:if test="@xsi:type = 'sDocumentStructure:SToken'">
				<!-- print text -->
				<!-- compute posistion of token in list of tokens -->
				<xsl:variable name="tokPos">
					<xsl:call-template name="getTokPos">
						<xsl:with-param name="tokenId" select="identifier/@id" />
					</xsl:call-template>
				</xsl:variable>
				<!-- compute reference to token -->
				<xsl:variable name="tokRef"> 
					<xsl:value-of select="concat('//@nodes.', $tokPos)"></xsl:value-of>
				</xsl:variable>
				<!-- compute start position of text -->
				<xsl:variable name="startPos"> 
					<xsl:value-of select="//edges[@xsi:type='sDocumentStructure:STextualRelation'][@source=$tokRef]/labels[@xsi:type='saltCore:SFeature'][@namespace='saltCommon'][@name='SSTART']/@valueString"></xsl:value-of>
				</xsl:variable>
				<!-- compute end position of text -->
				<xsl:variable name="endPos"> 
					<xsl:value-of select="//edges[@xsi:type='sDocumentStructure:STextualRelation'][@source=$tokRef]/labels[@xsi:type='saltCore:SFeature'][@namespace='saltCommon'][@name='SEND']/@valueString"></xsl:value-of>
				</xsl:variable>
				<xsl:variable name="text"> 
					<xsl:call-template name="getText">
						<xsl:with-param name="sTextualDS" select="//nodes[@xsi:type='sDocumentStructure:STextualDS']" />
						<xsl:with-param name="startPos" select="$startPos" />
						<xsl:with-param name="endPos" select="$endPos" />
					</xsl:call-template>
		  		</xsl:variable>
		  		<xsl:value-of select="$text"></xsl:value-of>
				<!-- print pos- and lemma-annotations -->
				<xsl:for-each select="labels">
					<xsl:if test="@xsi:type = 'saltCore:SAnnotation'">
						<xsl:if test="@name = 'pos'">
							<xsl:text>&#9;</xsl:text>
							<xsl:value-of select="@valueString"></xsl:value-of>
						</xsl:if>
						<xsl:if test="@name = 'lemma'">
							<xsl:text>&#9;</xsl:text>
							<xsl:value-of select="@valueString"></xsl:value-of>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
				<!-- print linefeed -->
				<xsl:text>&#xA;</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<!-- Function to return the position of a given token in list of tokens-->
	<!-- parameter: tokPos= position of token in list of all tokens -->
	<xsl:template name= "getTokPos">
		<xsl:param name="tokenId"/>
		<!--  <xsl:value-of select="$tokenId"></xsl:value-of>-->
		<xsl:variable name="tokPosPre">
			<xsl:number/>
		</xsl:variable>
		<xsl:value-of select="$tokPosPre - 1"></xsl:value-of>
	</xsl:template>
	
	<!-- Function to return a text for a token -->
	<!-- parameter: startPos= start position of text -->
	<!-- parameter: endPos= end position of text -->
	<xsl:template name= "getText">
		<xsl:param name="sTextualDS"/>
		<xsl:param name="startPos"/>
		<xsl:param name="endPos"/>
		<xsl:variable name="text"> 
			<xsl:value-of select="substring($sTextualDS/labels[@xsi:type='saltCore:SFeature'][@namespace='saltCommon'][@name='SDATA']/@valueString, $startPos+1, $endPos - $startPos)"/>
		</xsl:variable>
		<xsl:value-of select="$text"/>
	</xsl:template>
</xsl:stylesheet>
