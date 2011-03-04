package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper;

import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltCommonFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;


/**
 * @author hildebax
 */
public class Treetagger2SaltMapper {
	
	private String defaultSeparator = " ";
	//TODO: default = false
	private String defaultAnnotateUnannotatedSpans = "true";
	
	//---------------------------------------------------------------------------------------------
	private Properties properties = new Properties();
	
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	//---------------------------------------------------------------------------------------------

	public void convert(Document tDocument, SDocument sDocument) {
		sDocument.setSDocumentGraph(SaltCommonFactory.eINSTANCE.createSDocumentGraph());
		sDocument.getSDocumentGraph().setSName(tDocument.getName()+"_graph");
		sDocument.setSName(tDocument.getName());
		this.addSMetaAnnotation(tDocument.getAnnotations(), sDocument);
		this.createSTextualDS(tDocument.getTokens(), sDocument);
	}
	
	private void addSMetaAnnotation(EList<Annotation> tAnnotations, SDocument sDocument) {
		for (int i=0;i<tAnnotations.size();i++) {
			Annotation tAnno = tAnnotations.get(i);
			SMetaAnnotation sMetaAnnotation = SaltCommonFactory.eINSTANCE.createSMetaAnnotation();
			sMetaAnnotation.setSName(tAnno.getName());
			sMetaAnnotation.setSValue(tAnno.getValue());
			sDocument.addSMetaAnnotation(sMetaAnnotation);
		}
	}
	
	private STextualDS createSTextualDS(EList<Token> tTokens, SDocument sDocument)
	{
		String separator = properties.getProperty("treetagger2saltmapper.separator", defaultSeparator);
		String annotateUnannotatedSpansString = properties.getProperty("treetagger2saltmapper.annotateUnannotatedSpans",defaultAnnotateUnannotatedSpans);
		boolean annotateUnannotatedSpans;
		if (annotateUnannotatedSpansString.equalsIgnoreCase("true")) {
			annotateUnannotatedSpans = true;
		} 
		else {
			annotateUnannotatedSpans = false;
		}
		
		//creating and adding STextualDS
		STextualDS sText= SaltCommonFactory.eINSTANCE.createSTextualDS();
		sDocument.getSDocumentGraph().addSNode(sText);
		
		Hashtable<Span,SSpan> spanTable = new Hashtable<Span,SSpan>();
		
		String text= null;
		int start= 0;
		int end= 0; 
		//for (Token tToken: tTokens)	{
		for (int tokenIndex=0;tokenIndex<tTokens.size();tokenIndex++) {
			Token tToken = tTokens.get(tokenIndex);
			if (text== null)
			{
				start= 0;
				end= tToken.getText().length();
				text= tToken.getText();
			}
			else 
			{
				start= text.length() + separator.length();
				end= start + tToken.getText().length();				
				text= text+ separator + tToken.getText();
			}
			//creating and adding token
			SToken sToken= this.createSToken(tToken);
			sDocument.getSDocumentGraph().addSNode(sToken);
			
			//creating and adding spans and spanning relations
			for (int i=0;i<tToken.getSpans().size();i++) {
				Span tSpan = tToken.getSpans().get(i);
				SSpan sSpan = null;
				if (!spanTable.containsKey(tSpan)) {
					sSpan = SaltCommonFactory.eINSTANCE.createSSpan();
					spanTable.put(tSpan, sSpan);
					sSpan.setGraph(sDocument.getSDocumentGraph());
					EList<Annotation> tAnnotations = tSpan.getAnnotations();
					if ((tAnnotations.size()==0)&&(annotateUnannotatedSpans)) {
						SAnnotation anno = SaltCommonFactory.eINSTANCE.createSAnnotation();
						anno.setName(tSpan.getName().toLowerCase());
						anno.setValue(tSpan.getName().toLowerCase());
						sSpan.addSAnnotation(anno);
					} 
					else {
						for (int j=0;j<tAnnotations.size();j++) {
							SAnnotation anno = this.createSAnnotation(tSpan.getAnnotations().get(j));
							sSpan.addSAnnotation(anno);
						}
					}
				} 
				else {
					sSpan = spanTable.get(tSpan);
				}
				SSpanningRelation sSpanningRelation = SaltCommonFactory.eINSTANCE.createSSpanningRelation();
				sSpanningRelation.setSDocumentGraph(sDocument.getSDocumentGraph());
				sSpanningRelation.setSSpan(sSpan);
				sSpanningRelation.setSToken(sToken);
			}

			STextualRelation sTextRel= this.createSTextualRelation(sToken, sText, start, end);
			sDocument.getSDocumentGraph().addSRelation(sTextRel);
		}	
		sText.setSText(text);
		return(sText);
	}

	private SToken createSToken(Token tToken)
	{
		SToken retVal= null;
		retVal= SaltCommonFactory.eINSTANCE.createSToken();
		for (Annotation tAnnotation: tToken.getAnnotations())
		{
			retVal.addSAnnotation(this.createSAnnotation(tAnnotation));
		}	
		return(retVal);
	}
	
	private SAnnotation createSAnnotation(Annotation tAnnotation)
	{
		SAnnotation retVal= null;
		if (tAnnotation instanceof POSAnnotation)
			retVal= SaltSemanticsFactory.eINSTANCE.createSPOSAnnotation();
		else if (tAnnotation instanceof LemmaAnnotation)
			retVal= SaltSemanticsFactory.eINSTANCE.createSLemmaAnnotation();
		else 
		{
			retVal= SaltCommonFactory.eINSTANCE.createSAnnotation();
			retVal.setSName(tAnnotation.getName());
		}
		retVal.setSValue(tAnnotation.getValue());
		return(retVal);
	}
	
	private STextualRelation createSTextualRelation(SToken sToken, STextualDS sText, int start, int end)
	{
		STextualRelation retVal= null;
		retVal= SaltCommonFactory.eINSTANCE.createSTextualRelation();
		retVal.setSTextualDS(sText);
		retVal.setSToken(sToken);
		retVal.setSStart(start);
		retVal.setSEnd(end);
		return(retVal);
	}
	
}
