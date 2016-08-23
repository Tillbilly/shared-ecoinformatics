package au.edu.aekos.shared.questionnaire.jaxb;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;

import org.junit.Test;

public class JAXBQuestionnaireV2UnmarshallTest {

	@Test
	public void testUnmarshallingQuestionnaire1() throws JAXBException{
		
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object o = un.unmarshal( new File( "src/test/resources/questionnaire_v2.xml" ) );
        Assert.assertTrue(true);	
        QuestionnaireConfig config = (QuestionnaireConfig) o;
        Assert.assertTrue(true);
        Assert.assertEquals(config.getItems().getEntryList().size(), 5);
		
	}
	
}

/*<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<questionnaire>
    <version>version X</version>
    <title>SHaRED AEKOS Submission Tool - Example Config 3</title>
    <subtitle>Ecological Research Data Repository</subtitle>
    <introduction>Introduction to Shared - Question Group and Page Break Example</introduction>
    <items>
        <question id="1">
            <text>What kind of data are you submitting?</text>
            <responseType>CONTROLLED_VOCAB</responseType>
            <traitName>Species</traitName>
            <description> a description for help or something</description>
            <responseMandatory>true</responseMandatory>
        </question>
        <question id="2">
            <text>Is the data awesome?</text>
            <responseType>YES_NO</responseType>
            <description> a description for help or something</description>
            <responseMandatory>true</responseMandatory>
            <displayCondition>
                <questionId>1</questionId>
                <responseNotNull>true</responseNotNull>
                <responseValue>Ecological Data</responseValue>
            </displayCondition>
        </question>
        <question id="3">
            <text>No really, what kind of data are you submitting?</text>
            <responseType>CONTROLLED_VOCAB</responseType>
            <traitName>genus-def</traitName>
            <description> a description for help or something</description>
            <responseMandatory>true</responseMandatory>
            <defaultVocabulary>
                <tag>
                    <value>Animal</value>
                </tag>
                <tag>
                    <value>Vegetable</value>
                    <desc>A genus definition</desc>
                </tag>
                <tag>
                    <value>Mineral</value>
                    <desc>A genus definition</desc>
                </tag>
            </defaultVocabulary>
        </question>
        <pageBreak pageTitle="Page 2"/>
        <questionGroup id="100">
            <groupTitle>Question Group 100</groupTitle>
            <groupDescription>Group Description</groupDescription>
            <items>
                <question id="101">
                    <text>Question number 101?</text>
                    <responseType>TEXT</responseType>
                    <description> a description for help or something</description>
                    <responseMandatory>true</responseMandatory>
                </question>
                <question id="102">
                    <text>Question number 102?</text>
                    <responseType>DATE</responseType>
                    <description> a description for help or something</description>
                    <responseMandatory>true</responseMandatory>
                </question>
                <question id="103">
                    <text>Question number 103?</text>
                    <responseType>COORDINATE</responseType>
                    <description> a description for help or something</description>
                    <responseMandatory>true</responseMandatory>
                </question>
                <question id="104">
                    <text>Question number 104?</text>
                    <responseType>BBOX</responseType>
                    <description> a description for help or something</description>
                    <responseMandatory>true</responseMandatory>
                    <displayCondition>
                        <questionId>103</questionId>
                        <responseNotNull>true</responseNotNull>
                    </displayCondition>
                </question>
            </items>
        </questionGroup>
        <questionGroup id="200">
            <groupTitle>Question Group 200</groupTitle>
            <groupDescription>Description - Mineral Specific Questions</groupDescription>
            <items>
                <questionGroup id="2001">
                    <groupTitle>Question Sub Group 20000</groupTitle>
                    <groupDescription> Sub Group Description</groupDescription>
                    <items>
                        <question id="20001">
                            <text>Question number 20001?</text>
                            <responseType>TEXT</responseType>
                            <description> a description for help or something</description>
                            <responseMandatory>true</responseMandatory>
                        </question>
                        <question id="20002">
                            <text>Question number 20002?</text>
                            <responseType>DATE</responseType>
                            <description> a description for help or something</description>
                            <responseMandatory>true</responseMandatory>
                        </question>
                    </items>
                </questionGroup>
            </items>
            <displayCondition>
                <questionId>3</questionId>
                <responseNotNull>true</responseNotNull>
                <responseValue>Mineral</responseValue>
            </displayCondition>
        </questionGroup>
    </items>
</questionnaire>*/
