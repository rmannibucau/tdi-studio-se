package com.sforce.soap.partner;

/**
 * Generated by ComplexTypeCodeGenerator.java. Please do not edit.
 */
public interface IDescribeCompactLayoutsResult  {

      /**
       * element : compactLayouts of type {urn:partner.soap.sforce.com}DescribeCompactLayout
       * java type: com.sforce.soap.partner.DescribeCompactLayout[]
       */

      public com.sforce.soap.partner.IDescribeCompactLayout[] getCompactLayouts();

      public void setCompactLayouts(com.sforce.soap.partner.IDescribeCompactLayout[] compactLayouts);

      /**
       * element : defaultCompactLayoutId of type {urn:partner.soap.sforce.com}ID
       * java type: java.lang.String
       */

      public java.lang.String getDefaultCompactLayoutId();

      public void setDefaultCompactLayoutId(java.lang.String defaultCompactLayoutId);

      /**
       * element : recordTypeCompactLayoutMappings of type {urn:partner.soap.sforce.com}RecordTypeCompactLayoutMapping
       * java type: com.sforce.soap.partner.RecordTypeCompactLayoutMapping[]
       */

      public com.sforce.soap.partner.IRecordTypeCompactLayoutMapping[] getRecordTypeCompactLayoutMappings();

      public void setRecordTypeCompactLayoutMappings(com.sforce.soap.partner.IRecordTypeCompactLayoutMapping[] recordTypeCompactLayoutMappings);


}
