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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.corpus_tools.pepper.common.DOCUMENT_STATUS;
import org.corpus_tools.pepper.exceptions.PepperConvertException;
import org.corpus_tools.pepper.impl.PepperMapperImpl;
import org.corpus_tools.pepper.modules.exceptions.PepperModuleException;
import org.corpus_tools.peppermodules.treetagger.TreetaggerExporterProperties;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.corpus_tools.peppermodules.treetagger.model.serialization.TabWriter;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SOrderRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SMetaAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.semantics.SLemmaAnnotation;
import org.corpus_tools.salt.semantics.SPOSAnnotation;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is for mapping Salt to Treetagger
 *
 * @author hildebax
 * @author Florian Zipser
 */
public class Salt2TreetaggerMapper extends PepperMapperImpl {

	private static final Logger logger = LoggerFactory.getLogger(Salt2TreetaggerMapper.class);

	// ---------------------------------------------------------------------------------------------
	/**
	 * Getter for Properties
	 *
	 * @return the Properties
	 */
	public TreetaggerExporterProperties getProps() {
		return (TreetaggerExporterProperties) getProperties();
	}

	// ---------------------------------------------------------------------------------------------
	private int numOfSTokensWithMultiplePOSAnnos = 0;
	private int numOfSTokensWithMultipleLemmaAnnos = 0;

	private Document ttDocument = null;

	public Document getTTDocument() {
		return ttDocument;
	}

	public void setTTDocument(Document ttDocument) {
		this.ttDocument = ttDocument;
	}

	/**
	 * This method maps a Salt document to a Treetagger document
	 */
	@Override
	public DOCUMENT_STATUS mapSDocument() {

		if (getDocument().getDocumentGraph() != null) {

			if (getTTDocument() == null) {
				setTTDocument(TreetaggerFactory.eINSTANCE.createDocument());
			}
			getTTDocument().setName(getDocument().getName());
			this.addDocumentAnnotations(getDocument().getMetaAnnotations(), getTTDocument());
			this.addTokens(getDocument().getDocumentGraph(), getTTDocument());
			if (this.numOfSTokensWithMultiplePOSAnnos > 0) {
				logger.warn("There were " + this.numOfSTokensWithMultiplePOSAnnos
					+ " tokens with more than one POS annotation in the document. The first one found for each token was used for it´s POS annotation; the remainder was used for ordinary annotations.");
			}
			if (this.numOfSTokensWithMultipleLemmaAnnos > 0) {
				logger.warn("There were " + this.numOfSTokensWithMultipleLemmaAnnos
					+ " tokens with more than one lemma annotation in the document. The first one found for each token was used for it´s lemma annotation; the remainder was used for ordinary annotations.");
			}
			try {
				this.saveToFile(getResourceURI(), getTTDocument());
			} catch (IOException e) {
				throw new PepperConvertException("Cannot write document with id: '" + getDocument().getId() + "' to: '"
					+ getResourceURI() + "'.", e);
			}
		}
		return (DOCUMENT_STATUS.COMPLETED);
	}

	private void saveToFile(URI uri, Document tDocument) throws IOException {
		if (uri == null) {
			throw new PepperModuleException(this,
				"Cannot save o given uri, because its null for document '" + tDocument + "'.");
		}

		final TabWriter writer = new TabWriter();
		writer.save(tDocument, uri, getProperties().getProperties());

		// // create resource set and resource
		// ResourceSet resourceSet = new ResourceSetImpl();
		//
		// // Register XML resource factory
		// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("treetagger",
		// new XMIResourceFactoryImpl());
		// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(getProps().getFileEnding(),
		// new TabResourceFactory());
		// // load resource
		// Resource resource = resourceSet.createResource(uri);
		//
		// if (resource == null) {
		// throw new PepperConvertException("Cannot save treetagger file, the
		// resource '" + uri + "' is null.");
		// }
		// resource.getContents().add(tDocument);
		//
		// resource.save(getProperties().getProperties());
	}

	/*
	 * auxiliary method
	 */
	protected void addDocumentAnnotations(Set<SMetaAnnotation> sMetaAnnotations, Document tDocument) {
		for (SMetaAnnotation metaAnno : sMetaAnnotations) {
			Annotation tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
			tAnno.setName(metaAnno.getName());
			tAnno.setValue(metaAnno.getValue_STEXT());
			tDocument.getAnnotations().add(tAnno);
		}
	}

	/*
	 * auxiliary method
	 */
	protected void addTokens(SDocumentGraph sDocumentGraph, Document tDocument) {
		Hashtable<SToken, ArrayList<SSpan>> token2SpansTable = new Hashtable<SToken, ArrayList<SSpan>>();
		for (int i = 0; i < sDocumentGraph.getSpanningRelations().size(); i++) {
			SToken sToken = sDocumentGraph.getSpanningRelations().get(i).getTarget();
			SSpan sSpan = sDocumentGraph.getSpanningRelations().get(i).getSource();
			if (!token2SpansTable.containsKey(sToken)) {
				token2SpansTable.put(sToken, new ArrayList<SSpan>());
			}
			token2SpansTable.get(sToken).add(sSpan);
		}

		Hashtable<SSpan, Span> sSpan2SpanTable = new Hashtable<SSpan, Span>();
		long tokenIndex = 0;
		String segmentationName = getProps().getSegmentationName();	
		List<SToken> relevantTokens;
		if (segmentationName == null) {	
			relevantTokens = sDocumentGraph.getSortedTokenByText();
		} else {
			List<SOrderRelation> orderRels = sDocumentGraph.getOrderRelations().stream().filter((SOrderRelation r) -> segmentationName.equals(r.getType())).collect(Collectors.toList());
			Set<SToken> tokens = new HashSet<>();
			for (SOrderRelation rel : orderRels) {
				SNode from = rel.getSource();
				SNode to = rel.getTarget();
				SNode[] nodes = {from, to};
				for (SNode node : nodes) {
					if (node instanceof SSpan) {
						sDocumentGraph.getOverlappedTokens(node).stream().forEach(tokens::add);
					} 
					else if (from instanceof SToken) {
						tokens.add((SToken) node);
					}
				}
			}
			relevantTokens = sDocumentGraph.getSortedTokenByText(new ArrayList<SToken>(tokens));
		}
		for (SToken sToken : relevantTokens) {
			for (SRelation rel : sToken.getOutRelations()) {
				if (rel instanceof STextualRelation) {
					STextualRelation sTexRel = (STextualRelation) rel;
					Token token = TreetaggerFactory.eINSTANCE.createToken();
					token.setLine(tokenIndex++);
					token.setText(sTexRel.getTarget().getText().substring(sTexRel.getStart(), sTexRel.getEnd()));
					
					addTokenAnnotations(sToken, token);
					
					tDocument.getTokens().add(token);
					if (token2SpansTable.containsKey(sToken)) {
						for (int j = 0; j < token2SpansTable.get(sToken).size(); j++) {
							SSpan sSpan = token2SpansTable.get(sToken).get(j);
							if (!sSpan2SpanTable.containsKey(sSpan)) {
								sSpan2SpanTable.put(sSpan, this.createSpan(sSpan));
							}
							Span tSpan = sSpan2SpanTable.get(sSpan);
							token.getSpans().add(tSpan);
							tSpan.getTokens().add(token);
						}
					}
				}
			}
		}
	}

	/*
	 * auxiliary method
	 */
	protected void addTokenAnnotations(SToken sToken, Token tToken) {
		boolean donePOSAnno = false;
		ArrayList<SAnnotation> possiblePOSAnnos = new ArrayList<SAnnotation>();

		boolean doneLemmaAnno = false;
		ArrayList<SAnnotation> possibleLemmaAnnos = new ArrayList<SAnnotation>();

		Iterator<SAnnotation> it = sToken.getAnnotations().iterator();
		while (it.hasNext()) {
			SAnnotation sAnno = it.next();
			Annotation tAnno = null;

			if (sAnno instanceof SPOSAnnotation) {
				if (!donePOSAnno) {
					tAnno = TreetaggerFactory.eINSTANCE.createPOSAnnotation();
					donePOSAnno = true;
				} else {
					this.numOfSTokensWithMultiplePOSAnnos++;
					tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
				}
			} else if (sAnno instanceof SLemmaAnnotation) {
				if (!doneLemmaAnno) {
					tAnno = TreetaggerFactory.eINSTANCE.createLemmaAnnotation();
					doneLemmaAnno = true;
				} else {
					this.numOfSTokensWithMultipleLemmaAnnos++;
					tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
				}
			} else {
				// try to set the right type of Annotation by SALT_SEMANTICS
				if (SaltUtil.SEMANTICS_POS.equalsIgnoreCase(sAnno.getName())) {
					if (!donePOSAnno) {
						possiblePOSAnnos.add(sAnno);
					} else {
						tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
					}
				} else if (SaltUtil.SEMANTICS_LEMMA.equalsIgnoreCase(sAnno.getName())) {
					if (!doneLemmaAnno) {
						possibleLemmaAnnos.add(sAnno);
					} else {
						tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
					}
				} else {
					tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
				}
			}

			if (tAnno != null) {
				// setting the name will only affect instances of AnyAnnotation:
				// POSAnnotations get the name "pos", LemmaAnnotations get the
				// name "lemma"
				tAnno.setName(sAnno.getName());
				tAnno.setValue(sAnno.getValue_STEXT());
				tToken.getAnnotations().add(tAnno);
			}
		}

		for (int i = 0; i < possiblePOSAnnos.size(); i++) {
			SAnnotation sAnno = possiblePOSAnnos.get(i);
			Annotation tAnno = null;
			if (!donePOSAnno) {
				tAnno = TreetaggerFactory.eINSTANCE.createPOSAnnotation();
				donePOSAnno = true;
			} else {
				tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
			}
			// setting the name will only affect instances of AnyAnnotation:
			// POSAnnotations get the name "pos", LemmaAnnotations get the name
			// "lemma"
			tAnno.setName(sAnno.getName());
			tAnno.setValue(sAnno.getValue_STEXT());
			tToken.getAnnotations().add(tAnno);
		}

		for (int i = 0; i < possibleLemmaAnnos.size(); i++) {
			SAnnotation sAnno = possibleLemmaAnnos.get(i);
			Annotation tAnno = null;
			if (!doneLemmaAnno) {
				tAnno = TreetaggerFactory.eINSTANCE.createLemmaAnnotation();
				doneLemmaAnno = true;
			} else {
				tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
			}
			// setting the name will only affect instances of AnyAnnotation:
			// POSAnnotations get the name "pos", LemmaAnnotations get the name
			// "lemma"
			tAnno.setName(sAnno.getName());
			tAnno.setValue(sAnno.getValue_STEXT());
			tToken.getAnnotations().add(tAnno);
		}
	}

	/*
	 * auxiliary method
	 */
	protected Span createSpan(SSpan sSpan) {
		Span retVal = TreetaggerFactory.eINSTANCE.createSpan();
		String alternativeSpanName = null;
		Iterator<SAnnotation> it = sSpan.getAnnotations().iterator();
		while (it.hasNext()) {
			SAnnotation sAnno = it.next();
			Annotation tAnno = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
			if (alternativeSpanName == null) {
				alternativeSpanName = sAnno.getName();
			}
			tAnno.setName(sAnno.getName());
			tAnno.setValue(sAnno.getValue_STEXT());
			retVal.getAnnotations().add(tAnno);
		}
		retVal.setName(sSpan.getName());

		if ((sSpan.getName().startsWith("sSpan")) && (alternativeSpanName != null)) {

			if (this.getProps().isReplaceGenericSpanNamesProperty()) {
				retVal.setName(alternativeSpanName);
			}
		}
		return retVal;
	}

}
