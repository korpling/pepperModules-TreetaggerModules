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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;

import org.corpus_tools.pepper.modules.PepperModuleProperty;
import org.corpus_tools.peppermodules.treetagger.TreetaggerImporterProperties;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.LemmaAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.POSAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

/**
 * TestCase for mapping Treetagger to Salt
 * 
 * @author hildebax
 * @author Florian Zipser
 *
 */
public class Treetagger2SaltMapperTest {

	private String propertyFilename = "src/test/resources/treetagger2saltMapperTest.properties";

	private String exampleText = "Is this example more complicated than it appears to be";

	private Treetagger2SaltMapper fixture = null;

	private Treetagger2SaltMapper getFixture() {
		return fixture;
	}

	private void setFixture(Treetagger2SaltMapper fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() {
		this.setFixture(new Treetagger2SaltMapper());
		TreetaggerImporterProperties props = new TreetaggerImporterProperties();
		props.addProperties(URI.createFileURI(propertyFilename));
		getFixture().setProperties(props);
	}

	/**
	 * Creates a test Document for mapping
	 * 
	 * @return a Document with Tokens
	 *         {Is,this,example,more,complicated,than,it,appears,to,be},
	 *         POS/Lemma annotations, and two spans
	 */
	protected Document createDocument() {
		// create the Document
		Document tDocument = TreetaggerFactory.eINSTANCE.createDocument();
		tDocument.setName("treetagger2saltTestDocument");
		Annotation anno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
		anno.setAnnotatableElement(tDocument);
		anno.setName("docAnnotation");
		anno.setValue("docAnnotationValue");

		// create the Tokens and it´s Annotations
		String[] tokens = exampleText.split(" ");
		String[] posAnnotations = { "VBZ", "DT", "NN", "ABR", "JJ", "IN", "PRP", "VBZ", "TO", "VB" };
		String[] lemmaAnnotations = { "be", "this", "example", "more", "complicated", "than", "it", "appear", "to",
				"be" };

		for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
			Token token = TreetaggerFactory.eINSTANCE.createToken();
			POSAnnotation posAnno = TreetaggerFactory.eINSTANCE.createPOSAnnotation();
			LemmaAnnotation lemmaAnno = TreetaggerFactory.eINSTANCE.createLemmaAnnotation();

			tDocument.getTokens().add(token);
			posAnno.setAnnotatableElement(token);
			lemmaAnno.setAnnotatableElement(token);

			token.setText(tokens[tokenIndex]);
			posAnno.setValue(posAnnotations[tokenIndex]);
			lemmaAnno.setValue(lemmaAnnotations[tokenIndex]);
		}

		// create the Spans
		Span span = TreetaggerFactory.eINSTANCE.createSpan();
		span.setName("FirstSpan");
		anno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
		anno.setName("Inf-Struct");
		anno.setValue("contrast-focus");
		anno.setAnnotatableElement(span);
		span.getTokens().add(tDocument.getTokens().get(0));

		span = TreetaggerFactory.eINSTANCE.createSpan();
		span.setName("SecondSpan");
		anno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
		anno.setName("Inf-Struct");
		anno.setValue("topic");
		anno.setAnnotatableElement(span);
		for (int tokenIndex = 1; tokenIndex < tokens.length; tokenIndex++) {
			span.getTokens().add(tDocument.getTokens().get(tokenIndex));
		}

		return tDocument;
	}

	/**
	 * Compares the names of the documents and calls the method for further
	 * comparions
	 */
	@Test
	public final void testConvert() {
		Document tDoc = this.createDocument();
		SDocument sDoc = SaltFactory.createSDocument();
		sDoc.setDocumentGraph(SaltFactory.createSDocumentGraph());

		getFixture().setTTDocument(tDoc);
		getFixture().setDocument(sDoc);
		getFixture().mapSDocument();
		assertEquals(tDoc.getName(), sDoc.getName());
		assertEquals(tDoc.getName() + "_graph", sDoc.getDocumentGraph().getName());
		this.testAddSMetaAnnotation();
		this.testCreateSTextualDS();
	}

	/**
	 * compares the document (=meta) annotations
	 */
	@Test
	public final void testAddSMetaAnnotation() {
		Document tDoc = this.createDocument();
		SDocument sDoc = SaltFactory.createSDocument();
		fixture.setDocument(sDoc);
		sDoc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		assertTrue(sDoc.getMetaAnnotations().isEmpty());
		getFixture().addMetaAnnotation(tDoc.getAnnotations());
		assertEquals(tDoc.getAnnotations().size(), sDoc.getMetaAnnotations().size());
		for (int i = 0; i < tDoc.getAnnotations().size(); i++) {
			Annotation tAnno = tDoc.getAnnotations().get(i);
			SMetaAnnotation sAnno = sDoc.getMetaAnnotation(tAnno.getName());
			assertEquals(tAnno.getName(), sAnno.getName());
			assertEquals(tAnno.getValue(), sAnno.getValue_STEXT());
		}
	}

	/**
	 * compares the texts of the documents and calls the token comparison method
	 */
	@Test
	public final void testCreateSTextualDS() {
		Document tDoc = this.createDocument();
		SDocument sDoc = SaltFactory.createSDocument();
		sDoc.setDocumentGraph(SaltFactory.createSDocumentGraph());
		getFixture().createSTextualDS(tDoc.getTokens(), sDoc);
		SDocumentGraph sDocGraph = sDoc.getDocumentGraph();
		assertEquals(exampleText, sDocGraph.getTextualDSs().get(0).getText());
		this.compareTokens(tDoc.getTokens(), sDocGraph);
	}

	/**
	 * Uses default separator settings
	 */
	@Test
	public void test_PROP_SEPARATOR_AFTER_TOKEN_DEFAULT() {
		Document doc = TreetaggerFactory.eINSTANCE.createDocument();
		Token tok1 = TreetaggerFactory.eINSTANCE.createToken();
		tok1.setText("Is");
		doc.getTokens().add(tok1);

		Token tok2 = TreetaggerFactory.eINSTANCE.createToken();
		tok2.setText("this");
		doc.getTokens().add(tok2);

		Token tok3 = TreetaggerFactory.eINSTANCE.createToken();
		doc.getTokens().add(tok3);
		tok3.setText("sample");

		getFixture().setTTDocument(doc);

		getFixture().setDocument(SaltFactory.createSDocument());
		getFixture().mapSDocument();

		assertEquals("Is this sample", getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
	}

	/**
	 * Uses no separator.
	 */
	@Test
	public void test_PROP_SEPARATOR_AFTER_TOKEN_NO() {
		Document doc = TreetaggerFactory.eINSTANCE.createDocument();
		Token tok1 = TreetaggerFactory.eINSTANCE.createToken();
		tok1.setText("Is");
		doc.getTokens().add(tok1);

		Token tok2 = TreetaggerFactory.eINSTANCE.createToken();
		tok2.setText("this");
		doc.getTokens().add(tok2);

		Token tok3 = TreetaggerFactory.eINSTANCE.createToken();
		doc.getTokens().add(tok3);
		tok3.setText("sample");

		getFixture().setTTDocument(doc);

		getFixture().setDocument(SaltFactory.createSDocument());

		PepperModuleProperty<String> prop = (PepperModuleProperty<String>) getFixture().getProperties()
				.getProperty(TreetaggerImporterProperties.PROP_SEPARATOR_AFTER_TOKEN);
		prop.setValue("");

		getFixture().mapSDocument();

		assertEquals("Isthissample", getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
	}

	/**
	 * Uses custom separator.
	 */
	@Test
	public void test_PROP_SEPARATOR_AFTER_TOKEN_CUSTOM() {
		String sep = "&";

		Document doc = TreetaggerFactory.eINSTANCE.createDocument();
		Token tok1 = TreetaggerFactory.eINSTANCE.createToken();
		tok1.setText("Is");
		doc.getTokens().add(tok1);

		Token tok2 = TreetaggerFactory.eINSTANCE.createToken();
		tok2.setText("this");
		doc.getTokens().add(tok2);

		Token tok3 = TreetaggerFactory.eINSTANCE.createToken();
		doc.getTokens().add(tok3);
		tok3.setText("sample");

		getFixture().setTTDocument(doc);

		getFixture().setDocument(SaltFactory.createSDocument());

		PepperModuleProperty<String> prop = (PepperModuleProperty<String>) getFixture().getProperties()
				.getProperty(TreetaggerImporterProperties.PROP_SEPARATOR_AFTER_TOKEN);
		prop.setValue(sep);

		getFixture().mapSDocument();

		assertEquals("Is" + sep + "this" + sep + "sample",
				getFixture().getDocument().getDocumentGraph().getTextualDSs().get(0).getText());
	}

	/**
	 * compares the texts of tokens and calls the method for the comparison of
	 * the token annotations
	 * 
	 * @param tTokens
	 * @param sDocGraph
	 */
	private void compareTokens(List<Token> tTokens, SDocumentGraph sDocGraph) {
		List<SToken> sTokens = sDocGraph.getTokens();
		assertEquals(tTokens.size(), sTokens.size());
		Hashtable<SToken, String> sTokenTextTable = new Hashtable<SToken, String>();
		for (STextualRelation sTextRel : sDocGraph.getTextualRelations()) {
			sTokenTextTable.put(sTextRel.getSource(),
					sTextRel.getTarget().getText().substring(sTextRel.getStart(), sTextRel.getEnd()));
		}
		for (int index = 0; index < tTokens.size(); index++) {
			Token tTok = tTokens.get(index);
			SToken sTok = sTokens.get(index);
			assertEquals(tTok.getText(), sTokenTextTable.get(sTok));
			Salt2TreetaggerMapperTest.compareAnnotations(sTok, tTok.getAnnotations());
		}
	}
}
