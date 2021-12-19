<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Helvetica, sans-serif">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin="1cm">
          <fo:region-body margin-top="2cm" margin-bottom="1cm"/>
          <fo:region-before extent="2cm"/>
          <fo:region-after extent="1cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="simpleA4" id="page_sequence">
        <fo:static-content flow-name="xsl-region-before">
          <xsl:call-template name="header"/>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
          <xsl:call-template name="footer"/>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <xsl:call-template name="body"/>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <xsl:template name="header">
    <fo:block-container background-color="#e8e8e8" padding="0.5cm" text-align="center" border-color="#333333" border-bottom-style="solid" border-top-style="solid">
      <fo:block>
        <xsl:value-of select="//header/title"/>
      </fo:block>
    </fo:block-container>
  </xsl:template>

  <xsl:template name="body">
    <xsl:apply-templates select="*" />
  </xsl:template>

  <xsl:template name="footer">
    <fo:block-container border-color="#333333" border-top-style="solid">
      <fo:table table-layout="fixed" width="100%" font-size="10pt" margin-top="2mm">
        <fo:table-column column-width="50%"/>
        <fo:table-column column-width="50%"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell>
              <fo:block>
                <xsl:value-of select="//footer/date"/>
              </fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block text-align="right">
                Seite
                <fo:page-number/>
                von
                <fo:page-number-citation-last ref-id="page_sequence"/>
              </fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
      </fo:table>
    </fo:block-container>
  </xsl:template>

  <xsl:template match="labeledcontent">
    <fo:table-row>
      <fo:table-cell font-weight="bold">
        <fo:block padding="2mm">
          <xsl:value-of select="label"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell>
        <fo:block padding="1.5mm">
          <xsl:value-of select="content"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="labeledimage">
    <fo:table-row>
      <fo:table-cell font-weight="bold">
        <fo:block padding="2mm">
          <xsl:value-of select="label"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell>
        <fo:block padding="1.5mm">
          <fo:external-graphic content-width="scale-to-fit">
            <xsl:attribute name="src">
              <xsl:value-of select="src" />
            </xsl:attribute>
            <xsl:attribute name="height">
              <xsl:value-of select="height" />
            </xsl:attribute>
          </fo:external-graphic>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="defect">
    <fo:block>
      <fo:table table-layout="fixed" width="100%" font-size="10pt">
        <fo:table-column column-width="30%"/>
        <fo:table-column column-width="70%"/>
        <fo:table-body margin-left="3mm">
          <xsl:apply-templates select="labeledcontent | labeledimage"/>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="comment">
    <fo:block page-break-inside="avoid">
      <fo:block font-size="12pt" margin-top="1cm" padding-top="0.2cm" padding-bottom="0.2cm"
              border-color="#333333" border-top-style="solid" border-bottom-style="solid">
        <xsl:value-of select="title" />
      </fo:block>
      <fo:table table-layout="fixed" width="100%" font-size="10pt">
        <fo:table-column column-width="30%"/>
        <fo:table-column column-width="70%"/>
        <fo:table-body margin-left="3mm">
          <xsl:apply-templates select="labeledcontent | labeledimage"/>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="defectrow">
    <fo:table-row >
      <fo:table-cell font-weight="bold">
        <fo:block padding="2mm">
          <xsl:value-of select="label1"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell>
        <fo:block padding="1.5mm">
          <xsl:value-of select="content1"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell font-weight="bold">
        <fo:block padding="2mm">
          <xsl:value-of select="label2"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell>
        <fo:block padding="1.5mm">
          <xsl:value-of select="content2"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>

  <xsl:template match="locationdefect">
    <fo:block page-break-inside="avoid" border-color="#333333" border-bottom-style="solid" margin-bottom="5mm">
      <fo:block margin-left="3mm" border-color="#333333" border-bottom-style="solid" margin-bottom="2mm">
        <xsl:value-of select="description" />
      </fo:block>
      <fo:table table-layout="fixed" width="100%" font-size="10pt">
        <fo:table-column column-width="20%"/>
        <fo:table-column column-width="30%"/>
        <fo:table-column column-width="20%"/>
        <fo:table-column column-width="30%"/>
        <fo:table-body margin-left="3mm">
          <xsl:apply-templates select="defectrow"/>
        </fo:table-body>
      </fo:table>
    </fo:block>
  </xsl:template>

  <xsl:template match="locationplan">
    <fo:block page-break-inside="avoid" margin-bottom="5mm">
      <fo:block margin-left="3mm" border-color="#333333" border-bottom-style="solid" margin-bottom="2mm">
        <xsl:value-of select="name" />
      </fo:block>
      <fo:block margin-left="3mm" border-color="#333333" border-bottom-style="solid" margin-bottom="2mm">
        <fo:external-graphic content-width="scale-to-fit" width="18cm">
            <xsl:attribute name="src">
              <xsl:value-of select="src" />
            </xsl:attribute>
          </fo:external-graphic>
      </fo:block>
    </fo:block>
  </xsl:template>

  <xsl:template match="location">
    <fo:block>
      <xsl:apply-templates select="locationdefect | locationplan"/>
    </fo:block>
  </xsl:template>

</xsl:stylesheet>
