/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.peppermodules.treetagger.mapper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.impl.PepperMapperImpl;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleDataException;
import org.corpus_tools.peppermodules.treetagger.TreetaggerImporterProperties;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.LemmaAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.POSAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;

/**
 * This class is for mapping Treetagger to Salt
 * 
 * @author hildebax
 */
public class Treetagger2SaltMapper extends PepperMapperImpl implements PepperMapper {
	/**
	 * Returns the specific {@link TreetaggerImporterProperties} object.
	 * 
	 * @return the Properties
	 */
	public TreetaggerImporterProperties getProps() {
		return (TreetaggerImporterProperties) this.getProperties();
	}

	private Document ttDocument = null;

	public Document getTtDocument() {
		return ttDocument;
	}

	public void setTTDocument(Document ttDocument) {
		this.ttDocument = ttDocument;
	}

	/**
	 * The method maps a Treetagger document to a Salt document
	 * 
	 * @param tDocument
	 *            the Treetagger document
	 * @param sDocument
	 *            the Salt document
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {

		if (getDocument().getDocumentGraph() == null) {
			getDocument().setDocumentGraph(SaltFactory.createSDocumentGraph());
		}
		getDocument().getDocumentGraph().setName(getTtDocument().getName() + "_graph");
		getDocument().setName(getTtDocument().getName());
		this.addMetaAnnotation(getTtDocument().getAnnotations());
		this.createSTextualDS(getTtDocument().getTokens(), getDocument());
		return (DOCUMENT_STATUS.COMPLETED);
	}

	/*
	 * auxiliary method
	 */
	protected void addMetaAnnotation(List<Annotation> tAnnotations) {
		for (Annotation tAnno : tAnnotations) {
			getDocument().createMetaAnnotation(null, tAnno.getName(), tAnno.getValue());
		}
	}

	/*
	 * auxiliary method
	 */
	protected STextualDS createSTextualDS(List<Token> tTokens, SDocument sDocument) {
		boolean annotateUnannotatedSpans = this.getProps().getAnnotateUnannotatedSpans();
		boolean makePointingRelations = this.getProps().getMakePointingRelations();
		boolean invertPointingRelations = this.getProps().getInvertPointingRelations();
		boolean suppressPointingRelationID = this.getProps().getSuppressPRID();
		boolean suppressPointingRelationTarget = this.getProps().getSuppressPRTarget();
		boolean suppressPointingRelationLabel = this.getProps().getSuppressPRLabel();
		boolean usePRHash = this.getProps().getPRUseHash();
		String pointingTargetAnno = this.getProps().getPointingTargetAnno();
		String pointingIDAnno = this.getProps().getPointingIDAnno();
		String pointingEdgeAnno = this.getProps().getPointingEdgeAnno();
		String pointingType = this.getProps().getPointingType();
		String pointingNS = this.getProps().getPointingNS();
		String spanAnnoNS = this.getProps().getSpanAnnotationNamespace();
		boolean annotateAllSpansWithSpanName = this.getProps().getAnnotateAllSpansWithName();
		boolean prefixSpanAnnotation = this.getProps().getPrefixSpanAnnotation();
		String prefixSpanSeparator = this.getProps().getPrefixSpanSeparator();

		// creating and adding STextualDS
		STextualDS sText = SaltFactory.createSTextualDS();
		sDocument.getDocumentGraph().addNode(sText);

		SLayer prLayer = SaltFactory.createSLayer();
		if (makePointingRelations){ // Create a layer to put PRs in
            prLayer.setName(pointingNS);
            prLayer.setGraph(getDocument().getDocumentGraph());
		}
		
		Hashtable<Span, SSpan> spanTable = new Hashtable<Span, SSpan>();

		
		Hashtable<SSpan,String[]> prTable = new Hashtable<>(); // Maps SSpans to a list containing [target, edge-anno]
		Hashtable<String,SSpan> id2span = new Hashtable<>(); // Maps ids to SSpan

		
		String text = null;
		int start = 0;
		int end = 0;
		Map<String, String> replMap = ((TreetaggerImporterProperties) getProperties()).getReplacementMapping();

		// for (Token tToken: tTokens) {
		for (int tokenIndex = 0; tokenIndex < tTokens.size(); tokenIndex++) {
			Token tToken = tTokens.get(tokenIndex);
			if (replMap != null) {
				for (Map.Entry<String, String> entry : replMap.entrySet()) {
					tToken.setText(tToken.getText().replace(entry.getKey(), entry.getValue()));
					if (this.getProps().getReplaceInAnnos()) {
						for (Annotation tAnnotation : tToken.getAnnotations()) {
							tAnnotation.setValue(tAnnotation.getValue().replace(entry.getKey(), entry.getValue()));
						}
					}
				}
			}
			if (text == null) {
				start = 0;
				end = tToken.getText().length();
				text = tToken.getText();
			} else {
				start = text.length() + this.getProps().getSeparatorAfterToken().length();
				end = start + tToken.getText().length();
				text = text + this.getProps().getSeparatorAfterToken() + tToken.getText();
			}

			// creating and adding token
			SToken sToken = this.createSToken(tToken);
			sDocument.getDocumentGraph().addNode(sToken);

			// creating and adding spans and spanning relations
			for (int i = 0; i < tToken.getSpans().size(); i++) {
				Span tSpan = tToken.getSpans().get(i);
				SSpan sSpan = null;
				if (!spanTable.containsKey(tSpan)) {
					sSpan = SaltFactory.createSSpan();
					spanTable.put(tSpan, sSpan);
					sSpan.setGraph(sDocument.getDocumentGraph());
					sSpan.setName(tSpan.getName());
					List<Annotation> tAnnotations = tSpan.getAnnotations();
					if ((annotateAllSpansWithSpanName) || ((tAnnotations.size() == 0) && (annotateUnannotatedSpans))) {
						sSpan.createAnnotation(spanAnnoNS, tSpan.getName().toLowerCase(), tSpan.getName().toLowerCase());
					}
					for (int j = 0; j < tAnnotations.size(); j++) {
						SAnnotation anno = this.createAnnotation(tSpan.getAnnotations().get(j));
						if (prefixSpanAnnotation) {
							anno.setName(tSpan.getName() + prefixSpanSeparator + anno.getName());
						}
						if (spanAnnoNS != null){
							anno.setNamespace(spanAnnoNS);
						}
						// Create the span annotation, unless it is a pointing relation edge annotation
						if (!(makePointingRelations && 
							((anno.getName().equals(pointingTargetAnno) && suppressPointingRelationTarget) || 
							(anno.getName().equals(pointingEdgeAnno)&& suppressPointingRelationLabel)|| 
							(anno.getName().equals(pointingIDAnno) && suppressPointingRelationID ))))
						{
							sSpan.addAnnotation(anno);
						}
						if (makePointingRelations && (anno.getName().equals(pointingTargetAnno) || 
							anno.getName().equals(pointingEdgeAnno) || 
							anno.getName().equals(pointingIDAnno) ))
						{
							if (!prTable.containsKey(sSpan)){ // Initialize PR entry for this sspan
								String[] initPR = new String[2];
								prTable.put(sSpan,initPR);
							}
							String[] prInfo = prTable.get(sSpan);							
							if (anno.getName().equals(pointingTargetAnno)){ // Target marker
								prInfo[0] = anno.getValue_STEXT();
								if (prInfo[0].length() > 0){
									if (usePRHash && prInfo[0].startsWith("#")){  // Trim # if using href syntax
										prInfo[0] = prInfo[0].substring(1);
									}
								}
								prTable.put(sSpan,prInfo);
							}
							else if (anno.getName().equals(pointingIDAnno)){ // ID marker
								id2span.put(anno.getValue_STEXT(),sSpan);
							}
							else{ // Edge annotation
								prInfo[1] = anno.getValue_STEXT();
								prTable.put(sSpan,prInfo);								
							}
						}
					}
				} else {
					sSpan = spanTable.get(tSpan);
				}
				SSpanningRelation sSpanningRelation = SaltFactory.createSSpanningRelation();
				sSpanningRelation.setSource(sSpan);
				sSpanningRelation.setTarget(sToken);
				sSpanningRelation.setGraph(sDocument.getDocumentGraph());
			}
			
			STextualRelation sTextRel = this.createSTextualRelation(sToken, sText, start, end);
			sDocument.getDocumentGraph().addRelation(sTextRel);
		}

		for (SSpan src : prTable.keySet()){
			String[] prInfo = prTable.get(src);
			String trgID = prInfo[0];
			String edgeAnno = prInfo[1];
			if (trgID != null){
				if (id2span.containsKey(trgID)){
					SSpan trg = id2span.get(trgID);
					SPointingRelation rel = SaltFactory.createSPointingRelation();
					if (edgeAnno != null && edgeAnno.length() > 0){
						SAnnotation anno = SaltFactory.createSAnnotation();
						anno.setName(pointingEdgeAnno);
						anno.setValue(edgeAnno);
						anno.setNamespace(pointingNS);
						rel.addAnnotation(anno);
					}
					if (!invertPointingRelations){
						rel.setSource(src);
						rel.setTarget(trg);
					}
					else{
						rel.setSource(trg);
						rel.setTarget(src);
					}
					rel.setType(pointingType);
					rel.addLayer(prLayer);
					sDocument.getDocumentGraph().addRelation(rel);
					if (true){
						//throw new PepperModuleDataException(this,"Added rel:" + trgID +"->"+pointingNS+"::"+pointingEdgeAnno+"=" +edgeAnno);
					}

				}
				else{
					throw new PepperModuleDataException(this,"Input error: pointing relation target ID " + pointingTargetAnno + "=" + trgID + " refers to a non-existent span annotation "+pointingIDAnno+"="+ trgID +"\n" );
				}
			}
		}

		sText.setText(text);
		return (sText);
	}

	/*
	 * auxiliary method
	 */
	protected SToken createSToken(Token tToken) {
		SToken retVal = SaltFactory.createSToken();
		for (Annotation tAnnotation : tToken.getAnnotations()) {
			retVal.addAnnotation(this.createAnnotation(tAnnotation));
		}
		return (retVal);
	}

	/*
	 * auxiliary method
	 */
	protected SAnnotation createAnnotation(Annotation tAnnotation) {
		SAnnotation retVal = null;
		if (tAnnotation instanceof POSAnnotation)
			retVal = SaltFactory.createSPOSAnnotation();
		else if (tAnnotation instanceof LemmaAnnotation)
			retVal = SaltFactory.createSLemmaAnnotation();
		else {
			retVal = SaltFactory.createSAnnotation();
			retVal.setName(tAnnotation.getName());
		}
		retVal.setValue(tAnnotation.getValue());
		return (retVal);
	}

	/*
	 * auxiliary method
	 */
	protected STextualRelation createSTextualRelation(SToken sToken, STextualDS sText, int start, int end) {
		STextualRelation retVal = null;
		retVal = SaltFactory.createSTextualRelation();
		retVal.setTarget(sText);
		retVal.setSource(sToken);
		retVal.setStart(start);
		retVal.setEnd(end);
		return (retVal);
	}

}
