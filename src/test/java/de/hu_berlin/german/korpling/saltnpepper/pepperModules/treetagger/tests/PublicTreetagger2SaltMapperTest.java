package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.tests;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.TreetaggerFactory;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentStructurePackage;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;

public class PublicTreetagger2SaltMapperTest extends TestCase {

	private String propertyFilename = "src/test/resources/treetagger2saltMapperTest.properties";

	private String exampleText = "Is this example more complicated than it appears to be";

	private PublicTreetagger2SaltMapper fixture = null;
	
	private PublicTreetagger2SaltMapper getFixture() {
		return fixture;
	}
	
	private void setFixture(PublicTreetagger2SaltMapper fixture) {
		this.fixture = fixture;
	}
	
	public void setUp() {
		this.setFixture(new PublicTreetagger2SaltMapper());
		Properties properties = new Properties();
		try { properties.load(new InputStreamReader(new FileInputStream(propertyFilename)));}
		catch (IOException e) {}
		this.getFixture().setProperties(properties);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected Document createDocument() {
		//create the Document
		Document tDocument = TreetaggerFactory.eINSTANCE.createDocument();
 		tDocument.setName("treetagger2saltTestDocument");
		Annotation anno = TreetaggerFactory.eINSTANCE.createAnnotation();
		anno.setAnnotatableElement(tDocument);
		anno.setName("docAnnotation");
		anno.setValue("docAnnotationValue");
		
		//create the Tokens and it´s Annotations
		String[] tokens           = exampleText.split(" ");
		String[] posAnnotations   = {"VBZ" , "DT"   , "NN"      , "ABR"  , "JJ"          , "IN"   , "PRP" , "VBZ"     , "TO" , "VB"};
		String[] lemmaAnnotations = {"be"  , "this" , "example" , "more" , "complicated" , "than" , "it"  , "appear"  , "to" , "be"};
		
		for (int tokenIndex=0;tokenIndex<tokens.length;tokenIndex++) {
			Token           token     = TreetaggerFactory.eINSTANCE.createToken();
			POSAnnotation   posAnno   = TreetaggerFactory.eINSTANCE.createPOSAnnotation();
			LemmaAnnotation lemmaAnno = TreetaggerFactory.eINSTANCE.createLemmaAnnotation(); 
			
			tDocument.getTokens().add(token);
			posAnno.setAnnotatableElement(token);
			lemmaAnno.setAnnotatableElement(token);
			
			token.setText(tokens[tokenIndex]);
			posAnno.setValue(posAnnotations[tokenIndex]);
			lemmaAnno.setValue(lemmaAnnotations[tokenIndex]);
		}

		//create the Spans
		Span span = TreetaggerFactory.eINSTANCE.createSpan();
		span.setName("FirstSpan");
		anno = TreetaggerFactory.eINSTANCE.createAnnotation();
		anno.setName("Inf-Struct");
		anno.setValue("contrast-focus");
		anno.setAnnotatableElement(span);
		span.getTokens().add(tDocument.getTokens().get(0));

		span = TreetaggerFactory.eINSTANCE.createSpan();
		span.setName("SecondSpan");
		anno = TreetaggerFactory.eINSTANCE.createAnnotation();
		anno.setName("Inf-Struct");
		anno.setValue("topic");
		anno.setAnnotatableElement(span);
		for (int tokenIndex=1;tokenIndex<tokens.length;tokenIndex++) {
			span.getTokens().add(tDocument.getTokens().get(tokenIndex));
		}
		
		return tDocument;
	}
	
	@SuppressWarnings("unused")
	private void saveDocument() {
		Document tDoc = this.createDocument();
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(SDocumentStructurePackage.eINSTANCE.getNsURI(), SDocumentStructurePackage.eINSTANCE);
		TabResourceFactory tabResourceFactory = new TabResourceFactory();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tab",tabResourceFactory);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tt",tabResourceFactory);
		Resource resourceOut = resourceSet.createResource(URI.createFileURI("./PublicTreetagger2SaltTest.tab"));
		resourceOut.getContents().add(tDoc);
		try { 
			resourceOut.save(null);
		}
		catch (IOException e) {
		}
	}
	
	/**
	 * 
	 */
	public final void testConvert() {
		Document  tDoc = this.createDocument();
		SDocument sDoc = SaltFactory.eINSTANCE.createSDocument();
		sDoc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		
		this.getFixture().map(tDoc,sDoc);
		assertEquals(tDoc.getName(), sDoc.getSName());
		assertEquals(tDoc.getName() + "_graph", sDoc.getSDocumentGraph().getSName());
		this.testAddSMetaAnnotation();
		this.testCreateSTextualDS();
	}

	public final void testAddSMetaAnnotation() {
		Document  tDoc = this.createDocument();
		SDocument sDoc = SaltFactory.eINSTANCE.createSDocument();
		sDoc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());
		
		assertTrue(sDoc.getSMetaAnnotations().isEmpty());
		
		this.getFixture().addSMetaAnnotation(tDoc.getAnnotations(), sDoc);
		
		for (int i=0;i<tDoc.getAnnotations().size();i++) {
			Annotation      tAnno = tDoc.getAnnotations().get(i);
			SMetaAnnotation sAnno = sDoc.getSMetaAnnotations().get(i);
			assertEquals(tAnno.getName(),  sAnno.getSName());			
			assertEquals(tAnno.getValue(), sAnno.getSValueSTEXT());
		}
	}

	public final void testCreateSTextualDS() {
		Document  tDoc = this.createDocument();
		SDocument sDoc = SaltFactory.eINSTANCE.createSDocument();
		sDoc.setSDocumentGraph(SaltFactory.eINSTANCE.createSDocumentGraph());

		this.getFixture().createSTextualDS(tDoc.getTokens(), sDoc);

		SDocumentGraph sDocGraph = sDoc.getSDocumentGraph();

		assertEquals(exampleText, sDocGraph.getSTextualDSs().get(0).getSText());
		
		this.compareTokens(tDoc.getTokens(), sDocGraph);
	}
	
	/**
	 * 
	 * @param tTokens
	 * @param sTokens
	 */
	private void compareTokens(List<Token> tTokens, SDocumentGraph sDocGraph) {
		List<SToken> sTokens = sDocGraph.getSTokens();
		for (int index=0; index<tTokens.size(); index++) {
			Token  tTok = tTokens.get(index);
			SToken sTok = sTokens.get(index);

		//TODO
		
		}

	}
	
	/**
	 * TODO
	 * @param tAnnos
	 * @param sAnnos
	 */
	private void compareAnnotations(List<Annotation> tAnnos, List<SAnnotation> sAnnos) {
		for (int index=0;index<tAnnos.size();index++) {
			
		}
	}
	
	
	
	
	
	
	
}
