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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.tests;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.TreetaggerFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.TreetaggerExporterProperties;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SLemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SPOSAnnotation;

/**
 * TestCase for mapping from Salt to Treetagger
 * @author hildebax
 * @author Florian Zipser
 */
public class PublicSalt2TreetaggerMapperTest extends TestCase {

	private String propertyFilename = "src/test/resources/salt2treetaggerMapperTest.properties";
	
	private PublicSalt2TreetaggerMapper fixture = null;
	
	private PublicSalt2TreetaggerMapper getFixture() {
		return fixture;
	}
	
	private void setFixture(PublicSalt2TreetaggerMapper fixture) {
		this.fixture = fixture;
	}
	
	public void setUp() {
		this.setFixture(new PublicSalt2TreetaggerMapper());
		TreetaggerExporterProperties props= new TreetaggerExporterProperties();
		props.addProperties(URI.createFileURI(propertyFilename));
		getFixture().setProperties(props);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected SDocument createSDocument() {
		SDocument      sDocument = SaltFactory.eINSTANCE.createSDocument();
		SDocumentGraph sDocGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		sDocument.setSName("Salt2TreetaggerMapperTestDocument");
		sDocument.createSMetaAnnotation("", "name", "Salt2TreetaggerMapperTestDocument");
		sDocument.setSDocumentGraph(sDocGraph);

		{//creating the document structure
			//an object for the primary text
			STextualDS sTextualDS= null;
			
			{//creating the primary text
				sTextualDS= SaltFactory.eINSTANCE.createSTextualDS();
				sTextualDS.setSText("Is this example more complicated than it appears to be?");
				//adding the text to the document-graph
				sDocGraph.addSNode(sTextualDS);
			}//creating the primary text
			
			{//creating tokenization (token objects and relations between tokens and the primary data object)
				//placeholder object representing a token
				SToken sToken= null;
				//object to connect a token to a primary text
				STextualRelation sTextRel= null;
				
				//adding the created token to the document-graph
				sToken= SaltFactory.eINSTANCE.createSToken();
				sDocGraph.addSNode(sToken);
				
				sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
				//adding the token as source of this relation
				sTextRel.setSToken(sToken);
				//adding the primary text as target of this relation
				sTextRel.setSTextualDS(sTextualDS);
				//adding the start-position of the token in the primary text 
				sTextRel.setSStart(0);
				//adding the end-position of the token in the primary text (start-position of the token + length of the token) 
				sTextRel.setSEnd(2);
				//adding the textual relation between token and primary text to document graph
				sDocGraph.addSRelation(sTextRel);
				
				{//creating the rest of the tokenization, this can also be done automatically

					//creating tokenization for the token 'this' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);

					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(3);
					sTextRel.setSEnd(7);	
					sDocGraph.addSRelation(sTextRel);
				
					//creating tokenization for the token 'example' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(8);
					sTextRel.setSEnd(15);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'more' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(16);
					sTextRel.setSEnd(20);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'complicated' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(21);
					sTextRel.setSEnd(32);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'than' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(33);
					sTextRel.setSEnd(37);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'it' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(38);
					sTextRel.setSEnd(40);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'appears' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(41);
					sTextRel.setSEnd(48);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'to' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(49);
					sTextRel.setSEnd(51);
					sDocGraph.addSRelation(sTextRel);
					
					//creating tokenization for the token 'be' and adding it to the morphology layer
					sToken= SaltFactory.eINSTANCE.createSToken();
					sDocGraph.addSNode(sToken);
					sTextRel= SaltFactory.eINSTANCE.createSTextualRelation();
					sTextRel.setSToken(sToken);
					sTextRel.setSTextualDS(sTextualDS);
					sTextRel.setSStart(52);
					sTextRel.setSEnd(54);
					sDocGraph.addSRelation(sTextRel);
				}//creating the rest of the tokenization, this can also be done automatically
			}//creating tokenization (token objects and relations between tokens and the primary data object)
			
			// a synchronized list of all tokens to walk through
			List<SToken> sTokens= Collections.synchronizedList(sDocGraph.getSTokens());
			
			{//adding some annotations, part-of-speech and lemma (for part-of speech and lemma annotations a special annotation in Salt exists)
				{//adding part-of speech annotations
					SPOSAnnotation sPOSAnno= null;
					
					//a list of all part-of-speech annotations for the words Is (VBZ), this (DT) ... be (VB)
					String[] posAnnotations={"VBZ", "DT", "NN", "ABR", "JJ", "IN", "PRP", "VBZ", "TO", "VB"}; 
					for (int i= 0; i< sTokens.size();i++)
					{
						sPOSAnno= SaltFactory.eINSTANCE.createSPOSAnnotation();
						sPOSAnno.setSValue(posAnnotations[i]);
						sTokens.get(i).addSAnnotation(sPOSAnno);
					}
				}//adding part-of speech annotations
				
				{//adding lemma annotations
					SLemmaAnnotation sLemmaAnno= null;
					
					//a list of all lemma annotations for the words Is (be), this (this) ... be (be)
					String[] lemmaAnnotations={"be", "this", "example", "more", "complicated", "than", "it", "appear", "to", "be"}; 
					for (int i= 0; i< sTokens.size();i++)
					{
						sLemmaAnno= SaltFactory.eINSTANCE.createSLemmaAnnotation();
						sLemmaAnno.setSValue(lemmaAnnotations[i]);
						sTokens.get(i).addSAnnotation(sLemmaAnno);
					}
				}//adding lemma annotations
				
				{//creating annotations for information structure with the use of spans: "Is"= contrast-focus,"this example more complicated than it appears to be"= Topic 
					SSpan sSpan= null;
					SSpanningRelation sSpanRel= null;
					SAnnotation sAnno= null;
					
					//creating a span node as placeholder for information-structure annotation
					sSpan= SaltFactory.eINSTANCE.createSSpan();
					//adding the created span to the document-graph
					sDocGraph.addSNode(sSpan);
					//creating an annotation for information-structure
					sAnno= SaltFactory.eINSTANCE.createSAnnotation();
					//setting the name of the annotation
					sAnno.setSName("Inf-Struct");
					//setting the value of the annotation
					sAnno.setSValue("contrast-focus");
					//adding the annotation to the placeholder span
					sSpan.addSAnnotation(sAnno);
					
					//creating a relation to connect a token with the span
					sSpanRel= SaltFactory.eINSTANCE.createSSpanningRelation();
					//setting the span as source of the relation
					sSpanRel.setSSpan(sSpan);
					//setting the first token as target of the relation
					sSpanRel.setSToken(sTokens.get(0));
					//adding the created relation to the document-graph
					sDocGraph.addSRelation(sSpanRel);
					
					{//creating the second span
						sSpan= SaltFactory.eINSTANCE.createSSpan();
						sDocGraph.addSNode(sSpan);
						sAnno= SaltFactory.eINSTANCE.createSAnnotation();
						sAnno.setSName("Inf-Struct");
						sAnno.setSValue("topic");
						sSpan.addSAnnotation(sAnno);
						for (int i= 1; i< sTokens.size(); i++)
						{
							sSpanRel= SaltFactory.eINSTANCE.createSSpanningRelation();
							sSpanRel.setSSpan(sSpan);
							sSpanRel.setSToken(sTokens.get(i));
							sDocGraph.addSRelation(sSpanRel);
						}
					}//creating the second span
					
				}//creating annotations for information structure with the use of spans
				
			}//adding some annotations, part-of-speech and lemma (for part-of speech and lemma annotations a special annotation in Salt exists)
			
			{//creating an anaphoric relation with the use of pointing relations between the Tokens {"it"} and {"this", "example"}
				//creating a span as placeholder, which contains the tokens for "this" and "example"
				SSpan sSpan= SaltFactory.eINSTANCE.createSSpan();
				//adding the created span to the document-graph
				sDocGraph.addSNode(sSpan);
				
				//creating a relation between the span and the tokens
				SSpanningRelation sSpanRel= null;
				sSpanRel= SaltFactory.eINSTANCE.createSSpanningRelation();
				sSpanRel.setSSpan(sSpan);
				sSpanRel.setSToken(sTokens.get(1));
				sDocGraph.addSRelation(sSpanRel);
				sSpanRel= SaltFactory.eINSTANCE.createSSpanningRelation();
				sSpanRel.setSSpan(sSpan);
				sSpanRel.setSToken(sTokens.get(2));
				sDocGraph.addSRelation(sSpanRel);
				
				//creating a pointing relations
				SPointingRelation sPointingRelation= SaltFactory.eINSTANCE.createSPointingRelation();
				//setting token "it" as source of this relation
				sPointingRelation.setSStructuredSource(sTokens.get(6));
				//setting span "this example" as target of this relation
				sPointingRelation.setSStructuredTarget(sSpan);
				//adding the created relation to the document-graph
				sDocGraph.addSRelation(sPointingRelation);
				//creating an annotation
				SAnnotation sAnno= SaltFactory.eINSTANCE.createSAnnotation();
				sAnno.setSName("anaphoric");
				sAnno.setSValue("antecedent");
				//adding the annotation to the relation
				sPointingRelation.addSAnnotation(sAnno);
			}//creating an anaphoric relation with the use of pointing relations between the Tokens {"it"} and {"this", "example"}
		
		}//creating the document structure
		return sDocument;
	}
	
	/**
	 * Test method for {@link de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.tests.PublicSalt2TreetaggerMapper#addDocumentAnnotations(org.eclipse.emf.common.util.EList, de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document)}.
	 */
	public final void testAddDocumentAnnotations() {
		Document  tDoc = TreetaggerFactory.eINSTANCE.createDocument();
		SDocument sDoc = this.createSDocument();

		assertTrue(tDoc.getAnnotations().isEmpty());
				
		this.getFixture().addDocumentAnnotations(sDoc.getSMetaAnnotations(), tDoc);
		
		EList<SMetaAnnotation> metaAnnoList = sDoc.getSMetaAnnotations();
		EList<Annotation>      annoList     = tDoc.getAnnotations();
		
		for (int annoIndex=0; annoIndex<metaAnnoList.size(); annoIndex++) {
			SMetaAnnotation metaAnno = metaAnnoList.get(annoIndex);
			Annotation      anno     = annoList.get(annoIndex);
			assertEquals(metaAnno.getSName(),       anno.getName());			
			assertEquals(metaAnno.getSValueSTEXT(), anno.getValue());
		}
		
	}

	/**
	 * Test method for {@link de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.tests.PublicSalt2TreetaggerMapper#addTokens(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph, de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document)}.
	 */
	public final void testAddTokens() {
		Document  tDoc = TreetaggerFactory.eINSTANCE.createDocument();
		SDocument sDoc = this.createSDocument();

		SDocumentGraph sDocGraph = sDoc.getSDocumentGraph();
		
		this.getFixture().addTokens(sDocGraph, tDoc);
		
		Hashtable<SToken, ArrayList<SSpan>> token2spansTable = new Hashtable<SToken, ArrayList<SSpan>>();
		for (SSpanningRelation spanRel : sDocGraph.getSSpanningRelations()) {
			SToken sTok = spanRel.getSToken();
			if (!token2spansTable.containsKey(sTok)) {
				token2spansTable.put(sTok, new ArrayList<SSpan>());	
			}
			token2spansTable.get(sTok).add(spanRel.getSSpan());
		}
		
		Hashtable<SToken, STextualRelation> token2textrelTable = new Hashtable<SToken, STextualRelation>();
		for (STextualRelation textRel : sDocGraph.getSTextualRelations()) {
			token2textrelTable.put(textRel.getSToken(), textRel);
		}
		
		for (int tokIndex=0; tokIndex<sDocGraph.getSTokens().size(); tokIndex++) {
			SToken sTok = sDocGraph.getSTokens().get(tokIndex);
			Token  tTok = tDoc.getTokens().get(tokIndex);
			STextualRelation textRel = token2textrelTable.get(sTok); 
			
			int start = textRel.getSStart();
			int end   = textRel.getSEnd();
			String sText = textRel.getSTextualDS().getSText().substring(start, end);
			String tText = tTok.getText();
			assertEquals(sText,tText);

			//compare spans
			this.compareSpans(token2spansTable.get(sTok), tTok.getSpans());
			
			//compare token annotations
			this.compareAnnotations(sTok.getSAnnotations(), tTok.getAnnotations());
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
			assertEquals(sSpans.get(spanIndex).getSName(), tSpans.get(spanIndex).getName());
			//compare span annotations
			this.compareAnnotations(sSpans.get(spanIndex).getSAnnotations(), tSpans.get(spanIndex).getAnnotations());
		}
	}
	
	/**
	 * This is called by testAddTokens and compareSpans
	 * @param sAnnos
	 * @param tAnnos
	 */
	protected void compareAnnotations(List<SAnnotation> sAnnos, List<Annotation> tAnnos) {
		assertEquals(sAnnos.size(), tAnnos.size());
		for (int annoIndex=0; annoIndex<sAnnos.size(); annoIndex++) {
			SAnnotation sAnno = sAnnos.get(annoIndex);
			Annotation  tAnno = tAnnos.get(annoIndex);
			if (tAnno instanceof POSAnnotation) {
				assertTrue(sAnno instanceof SPOSAnnotation); 
				//do not compare SName and Name, they are set automatically
				assertEquals(sAnno.getSValueSTEXT(), tAnno.getValue());
			}
			else if (tAnno instanceof LemmaAnnotation) {
				assertTrue(sAnno instanceof SLemmaAnnotation); 
				//do not compare SName and Name, they are set automatically
				assertEquals(sAnno.getSValueSTEXT(), tAnno.getValue());
			} 
			else {
				assertFalse((sAnno instanceof SPOSAnnotation)||(sAnno instanceof SLemmaAnnotation));
				assertEquals(sAnno.getSName(),       sAnno.getName() );
				assertEquals(sAnno.getSValueSTEXT(), tAnno.getValue());
			}
		}
	}
	
	/**
	 * Test method for {@link de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper.Salt2TreetaggerMapper#map(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument, de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document)}.
	 */
	public final void testMap() {
		Document  tDoc = TreetaggerFactory.eINSTANCE.createDocument();
		SDocument sDoc = this.createSDocument();
		getFixture().setSDocument(sDoc);
		getFixture().setTTDocument(tDoc);
		File file= new File(System.getProperty("java.io.tmpdir")+"/treetaggerModule_exportTest/");
		file.mkdirs();
		URI uri= URI.createFileURI(file.getAbsolutePath()+"/out.tt");
		getFixture().setResourceURI(uri);
		this.getFixture().mapSDocument();
		
		assertEquals(sDoc.getSName(),tDoc.getName());
		
		this.testAddDocumentAnnotations();
		this.testAddTokens();
	}

}


