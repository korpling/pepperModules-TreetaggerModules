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
package org.corpus_tools.peppermodules.treetagger.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.corpus_tools.peppermodules.treetagger.TreetaggerExporterProperties;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SAnnotationContainer;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.corpus_tools.salt.semantics.SLemmaAnnotation;
import org.corpus_tools.salt.semantics.SPOSAnnotation;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.TreetaggerFactory;

/**
 * TestCase for mapping from Salt to Treetagger
 * @author hildebax
 * @author Florian Zipser
 */
public class Salt2TreetaggerMapperTest{

	private String propertyFilename = "src/test/resources/salt2treetaggerMapperTest.properties";
	
	private PublicSalt2TreetaggerMapper fixture = null;
	
	private PublicSalt2TreetaggerMapper getFixture() {
		return fixture;
	}
	
	private void setFixture(PublicSalt2TreetaggerMapper fixture) {
		this.fixture = fixture;
	}
	
	@Before
	public void setUp() {
		this.setFixture(new PublicSalt2TreetaggerMapper());
		TreetaggerExporterProperties props= new TreetaggerExporterProperties();
		props.addProperties(URI.createFileURI(propertyFilename));
		getFixture().setProperties(props);
	}

	protected SDocument createSDocument() {
		SDocument      sDocument = SaltFactory.createSDocument();
		SDocumentGraph sDocGraph = SaltFactory.createSDocumentGraph();
		sDocument.setName("Salt2TreetaggerMapperTestDocument");
		sDocument.createMetaAnnotation("", "name", "Salt2TreetaggerMapperTestDocument");
		sDocument.setDocumentGraph(sDocGraph);

		{//creating the document structure
			//an object for the primary text
			STextualDS sTextualDS= null;
			
			{//creating the primary text
				sTextualDS= SaltFactory.createSTextualDS();
				sTextualDS.setText("Is this example more complicated than it appears to be?");
				//adding the text to the document-graph
				sDocGraph.addNode(sTextualDS);
			}//creating the primary text
			
			{//creating tokenization (token objects and relations between tokens and the primary data object)
				//placeholder object representing a token
				SToken sToken= null;
				//object to connect a token to a primary text
				STextualRelation sTextRel= null;
				
				//adding the created token to the document-graph
				sToken= SaltFactory.createSToken();
				sDocGraph.addNode(sToken);
				
				sTextRel= SaltFactory.createSTextualRelation();
				//adding the token as source of this relation
				sTextRel.setSource(sToken);
				//adding the primary text as target of this relation
				sTextRel.setTarget(sTextualDS);
				//adding the start-position of the token in the primary text 
				sTextRel.setStart(0);
				//adding the end-position of the token in the primary text (start-position of the token + length of the token) 
				sTextRel.setEnd(2);
				//adding the textual relation between token and primary text to document graph
				sDocGraph.addRelation(sTextRel);
				
				{//creating the rest of the tokenization, this can also be done automatically

					//creating tokenization for the token 'this' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);

					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(3);
					sTextRel.setEnd(7);	
					sDocGraph.addRelation(sTextRel);
				
					//creating tokenization for the token 'example' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(8);
					sTextRel.setEnd(15);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'more' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(16);
					sTextRel.setEnd(20);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'complicated' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(21);
					sTextRel.setEnd(32);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'than' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(33);
					sTextRel.setEnd(37);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'it' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(38);
					sTextRel.setEnd(40);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'appears' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(41);
					sTextRel.setEnd(48);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'to' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(49);
					sTextRel.setEnd(51);
					sDocGraph.addRelation(sTextRel);
					
					//creating tokenization for the token 'be' and adding it to the morphology layer
					sToken= SaltFactory.createSToken();
					sDocGraph.addNode(sToken);
					sTextRel= SaltFactory.createSTextualRelation();
					sTextRel.setSource(sToken);
					sTextRel.setTarget(sTextualDS);
					sTextRel.setStart(52);
					sTextRel.setEnd(54);
					sDocGraph.addRelation(sTextRel);
				}//creating the rest of the tokenization, this can also be done automatically
			}//creating tokenization (token objects and relations between tokens and the primary data object)
			
			// a synchronized list of all tokens to walk through
			List<SToken> sTokens= Collections.synchronizedList(sDocGraph.getTokens());
			
			{//adding some annotations, part-of-speech and lemma (for part-of speech and lemma annotations a special annotation in Salt exists)
				{//adding part-of speech annotations
					SPOSAnnotation sPOSAnno= null;
					
					//a list of all part-of-speech annotations for the words Is (VBZ), this (DT) ... be (VB)
					String[] posAnnotations={"VBZ", "DT", "NN", "ABR", "JJ", "IN", "PRP", "VBZ", "TO", "VB"}; 
					for (int i= 0; i< sTokens.size();i++)
					{
						sPOSAnno= SaltFactory.createSPOSAnnotation();
						sPOSAnno.setValue(posAnnotations[i]);
						sTokens.get(i).addAnnotation(sPOSAnno);
					}
				}//adding part-of speech annotations
				
				{//adding lemma annotations
					SLemmaAnnotation sLemmaAnno= null;
					
					//a list of all lemma annotations for the words Is (be), this (this) ... be (be)
					String[] lemmaAnnotations={"be", "this", "example", "more", "complicated", "than", "it", "appear", "to", "be"}; 
					for (int i= 0; i< sTokens.size();i++)
					{
						sLemmaAnno= SaltFactory.createSLemmaAnnotation();
						sLemmaAnno.setValue(lemmaAnnotations[i]);
						sTokens.get(i).addAnnotation(sLemmaAnno);
					}
				}//adding lemma annotations
				
				{//creating annotations for information structure with the use of spans: "Is"= contrast-focus,"this example more complicated than it appears to be"= Topic 
					SSpan sSpan= null;
					SSpanningRelation sSpanRel= null;
					SAnnotation sAnno= null;
					
					//creating a span node as placeholder for information-structure annotation
					sSpan= SaltFactory.createSSpan();
					//adding the created span to the document-graph
					sDocGraph.addNode(sSpan);
					//creating an annotation for information-structure
					sAnno= SaltFactory.createSAnnotation();
					//setting the name of the annotation
					sAnno.setName("Inf-Struct");
					//setting the value of the annotation
					sAnno.setValue("contrast-focus");
					//adding the annotation to the placeholder span
					sSpan.addAnnotation(sAnno);
					
					//creating a relation to connect a token with the span
					sSpanRel= SaltFactory.createSSpanningRelation();
					//setting the span as source of the relation
					sSpanRel.setSource(sSpan);
					//setting the first token as target of the relation
					sSpanRel.setTarget(sTokens.get(0));
					//adding the created relation to the document-graph
					sDocGraph.addRelation(sSpanRel);
					
					{//creating the second span
						sSpan= SaltFactory.createSSpan();
						sDocGraph.addNode(sSpan);
						sAnno= SaltFactory.createSAnnotation();
						sAnno.setName("Inf-Struct");
						sAnno.setValue("topic");
						sSpan.addAnnotation(sAnno);
						for (int i= 1; i< sTokens.size(); i++)
						{
							sSpanRel= SaltFactory.createSSpanningRelation();
							sSpanRel.setSource(sSpan);
							sSpanRel.setTarget(sTokens.get(i));
							sDocGraph.addRelation(sSpanRel);
						}
					}//creating the second span
					
				}//creating annotations for information structure with the use of spans
				
			}//adding some annotations, part-of-speech and lemma (for part-of speech and lemma annotations a special annotation in Salt exists)
			
			{//creating an anaphoric relation with the use of pointing relations between the Tokens {"it"} and {"this", "example"}
				//creating a span as placeholder, which contains the tokens for "this" and "example"
				SSpan sSpan= SaltFactory.createSSpan();
				//adding the created span to the document-graph
				sDocGraph.addNode(sSpan);
				
				//creating a relation between the span and the tokens
				SSpanningRelation sSpanRel= null;
				sSpanRel= SaltFactory.createSSpanningRelation();
				sSpanRel.setSource(sSpan);
				sSpanRel.setTarget(sTokens.get(1));
				sDocGraph.addRelation(sSpanRel);
				sSpanRel= SaltFactory.createSSpanningRelation();
				sSpanRel.setSource(sSpan);
				sSpanRel.setTarget(sTokens.get(2));
				sDocGraph.addRelation(sSpanRel);
				
				//creating a pointing relations
				SPointingRelation sPointingRelation= SaltFactory.createSPointingRelation();
				//setting token "it" as source of this relation
				sPointingRelation.setSource(sTokens.get(6));
				//setting span "this example" as target of this relation
				sPointingRelation.setTarget(sSpan);
				//adding the created relation to the document-graph
				sDocGraph.addRelation(sPointingRelation);
				//creating an annotation
				SAnnotation sAnno= SaltFactory.createSAnnotation();
				sAnno.setName("anaphoric");
				sAnno.setValue("antecedent");
				//adding the annotation to the relation
				sPointingRelation.addAnnotation(sAnno);
			}//creating an anaphoric relation with the use of pointing relations between the Tokens {"it"} and {"this", "example"}
		
		}//creating the document structure
		return sDocument;
	}
	
	/**
	 * Test method for {@link org.corpus_tools.peppermodules.treetagger.tests.PublicSalt2TreetaggerMapper#addDocumentAnnotations(org.eclipse.emf.common.util.EList, de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document)}.
	 */
	@Test
	public void testAddDocumentAnnotations() {
		Document  tDoc = TreetaggerFactory.eINSTANCE.createDocument();
		SDocument sDoc = this.createSDocument();

		assertTrue(tDoc.getAnnotations().isEmpty());
				
		getFixture().addDocumentAnnotations(sDoc.getMetaAnnotations(), tDoc);
		
		Set<SMetaAnnotation> metaAnnoList = sDoc.getMetaAnnotations();
		List<Annotation>      annoList     = tDoc.getAnnotations();
		
		for (int annoIndex=0; annoIndex<metaAnnoList.size(); annoIndex++) {
			Annotation      anno     = annoList.get(annoIndex);
			SMetaAnnotation metaAnno =sDoc.getMetaAnnotation(anno.getName());
			assertEquals(metaAnno.getName(),       anno.getName());			
			assertEquals(metaAnno.getValue_STEXT(), anno.getValue());
		}
		
	}

	/**
	 * Test method for {@link org.corpus_tools.peppermodules.treetagger.tests.PublicSalt2TreetaggerMapper#addTokens(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph, de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document)}.
	 */
	@Test
	public void testAddTokens() {
		Document  tDoc = TreetaggerFactory.eINSTANCE.createDocument();
		SDocument sDoc = this.createSDocument();

		SDocumentGraph sDocGraph = sDoc.getDocumentGraph();
		
		getFixture().addTokens(sDocGraph, tDoc);
		
		Hashtable<SToken, ArrayList<SSpan>> token2spansTable = new Hashtable<SToken, ArrayList<SSpan>>();
		for (SSpanningRelation spanRel : sDocGraph.getSpanningRelations()) {
			SToken sTok = spanRel.getTarget();
			if (!token2spansTable.containsKey(sTok)) {
				token2spansTable.put(sTok, new ArrayList<SSpan>());	
			}
			token2spansTable.get(sTok).add(spanRel.getSource());
		}
		
		Hashtable<SToken, STextualRelation> token2textrelTable = new Hashtable<SToken, STextualRelation>();
		for (STextualRelation textRel : sDocGraph.getTextualRelations()) {
			token2textrelTable.put(textRel.getSource(), textRel);
		}
		
		for (int tokIndex=0; tokIndex<sDocGraph.getTokens().size(); tokIndex++) {
			SToken sTok = sDocGraph.getTokens().get(tokIndex);
			Token  tTok = tDoc.getTokens().get(tokIndex);
			STextualRelation textRel = token2textrelTable.get(sTok); 
			
			int start = textRel.getStart();
			int end   = textRel.getEnd();
			String sText = textRel.getTarget().getText().substring(start, end);
			String tText = tTok.getText();
			assertEquals(sText,tText);

			//compare spans
			this.compareSpans(token2spansTable.get(sTok), tTok.getSpans());
			
			//compare token annotations
			this.compareAnnotations(sTok, tTok.getAnnotations());
		}
	}

	/**
	 * This is called by testAddTokens
	 * @param sSpans
	 * @param tSpans
	 */
	protected void compareSpans(List<SSpan> sSpans, List<Span> tSpans) {
		assertEquals(sSpans.size(),tSpans.size());
		for (int spanIndex=0; spanIndex<sSpans.size(); spanIndex++) {
			assertEquals(sSpans.get(spanIndex).getName(), tSpans.get(spanIndex).getName());
			//compare span annotations
			this.compareAnnotations(sSpans.get(spanIndex), tSpans.get(spanIndex).getAnnotations());
		}
	}
	
	/**
	 * This is called by testAddTokens and compareSpans
	 * @param sAnnos
	 * @param tAnnos
	 */
	protected void compareAnnotations(SAnnotationContainer container, List<Annotation> tAnnos) {
		assertEquals(container.getAnnotations().size(), tAnnos.size());
		for (int annoIndex=0; annoIndex<container.getAnnotations().size(); annoIndex++) {
//			SAnnotation sAnno = sAnnos.get(annoIndex);
			Annotation  tAnno = tAnnos.get(annoIndex);
			SAnnotation sAnno = container.getAnnotation(tAnno.getName());
			if (tAnno instanceof POSAnnotation) {
				assertTrue(sAnno instanceof SPOSAnnotation); 
				//do not compare SName and Name, they are set automatically
				assertEquals(sAnno.getValue_STEXT(), tAnno.getValue());
			}
			else if (tAnno instanceof LemmaAnnotation) {
				assertTrue(sAnno instanceof SLemmaAnnotation); 
				//do not compare SName and Name, they are set automatically
				assertEquals(sAnno.getValue_STEXT(), tAnno.getValue());
			} 
			else {
				assertFalse((sAnno instanceof SPOSAnnotation)||(sAnno instanceof SLemmaAnnotation));
				assertEquals(sAnno.getName(),       tAnno.getName() );
				assertEquals(sAnno.getValue_STEXT(), tAnno.getValue());
			}
		}
	}
	
	/**
	 * Test method for {@link org.corpus_tools.peppermodules.treetagger.mapper.Salt2TreetaggerMapper#map(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument, de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document)}.
	 */
	@Test
	public final void testMap() {
		Document  tDoc = TreetaggerFactory.eINSTANCE.createDocument();
		SDocument sDoc = this.createSDocument();
		getFixture().setDocument(sDoc);
		getFixture().setTTDocument(tDoc);
		File file= new File(System.getProperty("java.io.tmpdir")+"/treetaggerModule_exportTest/");
		file.mkdirs();
		URI uri= URI.createFileURI(file.getAbsolutePath()+"/out.tt");
		getFixture().setResourceURI(uri);
		getFixture().mapSDocument();
		
		assertEquals(sDoc.getName(),tDoc.getName());
		
		this.testAddDocumentAnnotations();
		this.testAddTokens();
	}

}


