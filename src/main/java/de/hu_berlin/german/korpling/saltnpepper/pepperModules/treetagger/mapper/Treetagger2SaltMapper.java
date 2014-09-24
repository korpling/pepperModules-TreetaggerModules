/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper;

import java.util.Hashtable;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.DOCUMENT_STATUS;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperMapperImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.TreetaggerImporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;

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

		if (getSDocument().getSDocumentGraph() == null){
			getSDocument().setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		}
		getSDocument().getSDocumentGraph().setSName(getTtDocument().getName() + "_graph");
		getSDocument().setSName(getTtDocument().getName());
		this.addSMetaAnnotation(getTtDocument().getAnnotations(), getSDocument());
		this.createSTextualDS(getTtDocument().getTokens(), getSDocument());
		return (DOCUMENT_STATUS.COMPLETED);
	}

	/*
	 * auxiliary method
	 */
	protected void addSMetaAnnotation(EList<Annotation> tAnnotations, SDocument sDocument) {
		for (Annotation tAnno: tAnnotations){
			sDocument.createSMetaAnnotation(null, tAnno.getName(), tAnno.getValue());
		}
	}

	/*
	 * auxiliary method
	 */
	protected STextualDS createSTextualDS(EList<Token> tTokens, SDocument sDocument) {
		boolean annotateUnannotatedSpans = this.getProps().getAnnotateUnannotatedSpans();

		boolean annotateAllSpansWithSpanName = this.getProps().getAnnotateAllSpansWithName();

		// creating and adding STextualDS
		STextualDS sText = SaltFactory.eINSTANCE.createSTextualDS();
		sDocument.getSDocumentGraph().addSNode(sText);

		Hashtable<Span, SSpan> spanTable = new Hashtable<Span, SSpan>();

		String text = null;
		int start = 0;
		int end = 0;
		// for (Token tToken: tTokens) {
		for (int tokenIndex = 0; tokenIndex < tTokens.size(); tokenIndex++) {
			Token tToken = tTokens.get(tokenIndex);
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
			sDocument.getSDocumentGraph().addSNode(sToken);

			// creating and adding spans and spanning relations
			for (int i = 0; i < tToken.getSpans().size(); i++) {
				Span tSpan = tToken.getSpans().get(i);
				SSpan sSpan = null;
				if (!spanTable.containsKey(tSpan)) {
					sSpan = SaltFactory.eINSTANCE.createSSpan();
					spanTable.put(tSpan, sSpan);
					sSpan.setGraph(sDocument.getSDocumentGraph());
					sSpan.setSName(tSpan.getName());
					EList<Annotation> tAnnotations = tSpan.getAnnotations();
					if ((annotateAllSpansWithSpanName) || ((tAnnotations.size() == 0) && (annotateUnannotatedSpans))) {
						sSpan.createSAnnotation(null, tSpan.getName().toLowerCase(), tSpan.getName().toLowerCase());
					}
					for (int j = 0; j < tAnnotations.size(); j++) {
						SAnnotation anno = this.createSAnnotation(tSpan.getAnnotations().get(j));
						sSpan.addSAnnotation(anno);
					}
				} else {
					sSpan = spanTable.get(tSpan);
				}
				SSpanningRelation sSpanningRelation = SaltFactory.eINSTANCE.createSSpanningRelation();
				sSpanningRelation.setSDocumentGraph(sDocument.getSDocumentGraph());
				sSpanningRelation.setSSpan(sSpan);
				sSpanningRelation.setSToken(sToken);
			}

			STextualRelation sTextRel = this.createSTextualRelation(sToken, sText, start, end);
			sDocument.getSDocumentGraph().addSRelation(sTextRel);
		}
		sText.setSText(text);
		return (sText);
	}

	/*
	 * auxiliary method
	 */
	protected SToken createSToken(Token tToken) {
		SToken retVal = SaltFactory.eINSTANCE.createSToken();
		for (Annotation tAnnotation : tToken.getAnnotations()) {
			retVal.addSAnnotation(this.createSAnnotation(tAnnotation));
		}
		return (retVal);
	}

	/*
	 * auxiliary method
	 */
	protected SAnnotation createSAnnotation(Annotation tAnnotation) {
		SAnnotation retVal = null;
		if (tAnnotation instanceof POSAnnotation)
			retVal = SaltSemanticsFactory.eINSTANCE.createSPOSAnnotation();
		else if (tAnnotation instanceof LemmaAnnotation)
			retVal = SaltSemanticsFactory.eINSTANCE.createSLemmaAnnotation();
		else {
			retVal = SaltFactory.eINSTANCE.createSAnnotation();
			retVal.setSName(tAnnotation.getName());
		}
		retVal.setSValue(tAnnotation.getValue());
		return (retVal);
	}

	/*
	 * auxiliary method
	 */
	protected STextualRelation createSTextualRelation(SToken sToken, STextualDS sText, int start, int end) {
		STextualRelation retVal = null;
		retVal = SaltFactory.eINSTANCE.createSTextualRelation();
		retVal.setSTextualDS(sText);
		retVal.setSToken(sToken);
		retVal.setSStart(start);
		retVal.setSEnd(end);
		return (retVal);
	}

}
