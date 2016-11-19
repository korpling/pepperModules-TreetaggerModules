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
package org.corpus_tools.peppermodules.treetagger.model.resources;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.corpus_tools.pepper.modules.exceptions.PepperModuleException;
import org.corpus_tools.peppermodules.treetagger.TreetaggerImporterProperties;
import org.corpus_tools.peppermodules.treetagger.model.AnnotatableElement;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.corpus_tools.peppermodules.treetagger.model.impl.Treetagger;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for loading and saving of treetagger data
 * 
 * @author hildebax
 * 
 */
public class TabReader {
	public static final String DEFAULT_ANNOTATION_NAME = "anyAnno";
	public static final String COLUMN_SEPARATOR = "\t";
	public static final String COLUMN_TOKEN_TEXT = "pos";
	public static final String COLUMN_POS = "pos";
	public static final String COLUMN_LEMMA = "lemma";

	private static final Logger logger = LoggerFactory.getLogger(TabReader.class);
	private static final Character utf8BOM = new Character((char) 0xFEFF);
	private String encoding = "UTF-8";
	private String metaTag = "meta";
	private URI location = null;
	private List<Document> documents = new ArrayList<>();
	private Document currentDocument = null;
	private List<Span> openSpans = new ArrayList<>();
	int lineNumber = 0;
	private boolean xmlDocumentOpen = false;

	List<String> columnNames = new ArrayList<>();

	public TabReader() {
		setDefaultColumnNames();
	}

	public void setDefaultColumnNames() {
		setColumnNames(Arrays.asList(COLUMN_TOKEN_TEXT, COLUMN_POS, COLUMN_LEMMA));
	}

	public void setColumnNames(List<String> annotationOrder) {
		this.columnNames = annotationOrder;
		if (this.columnNames == null) {
			this.columnNames = new ArrayList<>();
		}
	}

	List<Integer> rowsWithTooMuchColumns = new ArrayList<>();
	List<Integer> rowsWithTooLessColumns = new ArrayList<>();

	/**
	 * Loads a resource into treetagger model from tab separated file.
	 * 
	 * @param options
	 *            a map that may contain an instance of LogService and an
	 *            instance of Properties, with {@link #logServiceKey} and
	 *            {@link #propertiesKey} respectively as keys
	 */
	public List<Document> load(URI location, TreetaggerImporterProperties properties) {
		if (location == null) {
			throw new PepperModuleException("Cannot load any resource, because no uri is given.");
		}
		this.location = location;

		if (properties != null) {
			metaTag = properties.getProperty(TreetaggerImporterProperties.PROP_META_TAG).getValue().toString();
			logger.info("using meta tag '{}'", metaTag);
			encoding = properties.getProperty(TreetaggerImporterProperties.PROP_FILE_ENCODING).getValue().toString();
			logger.info("using input file encoding '{}'", encoding);
		}

		try (BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(location.toFileString()), encoding));) {
			String line = null;
			lineNumber = 0;
			while ((line = fileReader.readLine()) != null) {
				if (line.trim().length() > 0) {
					// delete BOM if exists
					if ((lineNumber == 0) && (line.startsWith(utf8BOM.toString()))) {
						line = line.substring(utf8BOM.toString().length());
						logger.info("BOM recognised and ignored");
					}
					lineNumber++;
					if (XMLUtils.isProcessingInstructionTag(line)) {
						// do nothing; ignore processing instructions
					} else if (XMLUtils.isStartTag(line)) {
						String startTagName = XMLUtils.getName(line);
						if (startTagName.equalsIgnoreCase(metaTag)) {
							beginDocument(line);
						} else {
							beginSpan(startTagName, line);
						}
					} else if (XMLUtils.isEndTag(line)) {
						String endTagName = XMLUtils.getName(line);
						if (endTagName.equalsIgnoreCase(metaTag)) {
							xmlDocumentOpen = false;
							endDocument();
						} else {
							endSpan(endTagName);
						}
					} else {
						if (currentDocument == null) {
							beginDocument(null);
						}
						final Token token = createTokenFromLine(line);
						connectTokenWithOpenSpans(token);
						currentDocument.getTokens().add(token);
					}
				}
			}
			endDocument();
		} catch (IOException e) {
			throw new PepperModuleException("Cannot read treetagger file '" + location + "'. ", e);
		}

		setDocumentNames();

		if (rowsWithTooLessColumns.size() > 0) {
			logger.warn(String.format("%s rows in input file had less data columns than expected! (Rows %s)",
					rowsWithTooLessColumns.size(), rowsWithTooLessColumns.toString()));
		}
		if (rowsWithTooMuchColumns.size() > 0) {
			logger.warn(String.format(
					"%s rows in input file had more data columns than expected! Additional data was ignored! (Rows %s)",
					rowsWithTooMuchColumns.size(), rowsWithTooMuchColumns.toString()));
		}
		return documents;
	}

	/*
	 * auxilliary method for processing input file
	 */
	private void addAttributesAsAnnotations(String tag, AnnotatableElement annotatableElement) {
		List<SimpleEntry<String, String>> attributeValueList = XMLUtils.getAttributeValueList(tag);
		for (int i = 0; i < attributeValueList.size(); i++) {
			SimpleEntry<String, String> entry = attributeValueList.get(i);
			Annotation annotation = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
			annotation.setName(entry.getKey());
			annotation.setValue(entry.getValue());
			annotatableElement.getAnnotations().add(annotation);
		}
	}

	/*
	 * auxilliary method for processing input file
	 */
	private void beginDocument(String startTag) {
		if (currentDocument != null) {
			endDocument();
		}
		currentDocument = TreetaggerFactory.eINSTANCE.createDocument();
		xmlDocumentOpen = (startTag != null);
		if (xmlDocumentOpen) {
			addAttributesAsAnnotations(startTag, currentDocument);
		}
	}

	/*
	 * auxilliary method for processing input file
	 */
	private void endDocument() {
		if (currentDocument != null) {
			if (!openSpans.isEmpty()) {
				String openSpanNames = "";
				for (int spanIndex = 0; spanIndex < openSpans.size(); spanIndex++) {
					Span span = openSpans.get(spanIndex);
					openSpanNames += ",</" + span.getName() + ">";
					for (int tokenIndex = span.getTokens().size() - 1; tokenIndex >= 0; tokenIndex--) {
						Token token = span.getTokens().get(tokenIndex);
						if (token.getSpans().contains(span)) {
							token.getSpans().remove(span);
						} else {
							break;
						}
					}
				}
				logger.warn(String.format("input file '%s' (line %d): missing end tag(s) '%s'. tag(s) will be ignored!",
						location.lastSegment(), lineNumber, openSpanNames.substring(1)));
			}
			if (xmlDocumentOpen) {
				logger.warn(
						String.format("input file '%s' (line %d): missing document end tag. document will be ignored!",
								location.lastSegment(), lineNumber));
			} else {
				documents.add(currentDocument);
			}

			currentDocument = null;
			xmlDocumentOpen = false;
		}
		openSpans.clear();
	}

	/*
	 * auxilliary method for processing input file
	 */
	private void beginSpan(String spanName, String startTag) {
		if (currentDocument == null) {
			beginDocument(null);
		}
		Span span = TreetaggerFactory.eINSTANCE.createSpan();
		openSpans.add(0, span);
		span.setName(spanName);
		addAttributesAsAnnotations(startTag, span);
	}

	/*
	 * auxilliary method for processing input file
	 */
	private void endSpan(String spanName) {
		if (currentDocument == null) {
			logger.warn(
					String.format("input file '%s' (line '%d'): end tag '</%s>' out of nowhere. tag will be ignored!",
							location.lastSegment(), lineNumber, spanName));
		} else {
			boolean matchingStartTagExists = false;
			for (int i = 0; i < openSpans.size(); i++) {
				Span openSpan = openSpans.get(i);
				if (openSpan.getName().equalsIgnoreCase(spanName)) {
					matchingStartTagExists = true;
					if (openSpan.getTokens().isEmpty()) {
						logger.warn(String.format(
								"input file '%s' (line %d): no tokens contained in span '<%s>'. span will be ignored!",
								location.lastSegment(), lineNumber, openSpan.getName()));
					}
					openSpans.remove(i);
					break;
				}
			}
			if (!matchingStartTagExists) {
				logger.warn(String.format(
						"input file '%s' (line %d): no corresponding opening tag found for end tag '</%s>'. tag will be ignored!",
						location.lastSegment(), lineNumber, spanName));
			}
		}
	}

	private void setDocumentNames() {
		String documentBaseName = location.lastSegment().split("[.]")[0];
		int documentCount = documents.size();

		switch (documentCount) {
		case 0:
			logger.warn(String.format("no valid document data contained in file '%s'", location.toFileString()));
			break;
		case 1:
			// set simple document name
			documents.get(0).setName(documentBaseName);
			break;
		default:
			// set document names with leading zeros for number extensions
			int documentCountDigits = String.valueOf(documentCount).length();
			for (int docIndex = 0; docIndex < documentCount; docIndex++) {
				String docNumber = Integer.toString(docIndex);
				while (docNumber.length() < documentCountDigits) {
					docNumber = "0" + docNumber;
				}
				documents.get(docIndex).setName(documentBaseName + "_" + docNumber);
			}
			break;
		}
	}

	Token createToken(final String... tuple) {
		final Token token = TreetaggerFactory.eINSTANCE.createToken();
		currentDocument.getTokens().add(token);
		token.setText(tuple[0].trim());
		return token;
	}

	private Token createTokenFromLine(String line) {
		final String[] tuple = line.split(COLUMN_SEPARATOR);
		doesTupleHasExpectedNumOfColumns(tuple);
		final Token token = Treetagger.buildToken().withText(tuple[0].trim()).build();
		createAnnotationsForToken(token, tuple);
		return token;
	}

	void doesTupleHasExpectedNumOfColumns(String... tuple) {
		if (tuple.length > columnNames.size()) {
			rowsWithTooMuchColumns.add(lineNumber);
		} else if (tuple.length < columnNames.size()) {
			rowsWithTooLessColumns.add(lineNumber);
		}
	}

	void connectTokenWithOpenSpans(Token token) {
		for (Span span : openSpans) {
			token.getSpans().add(span);
			span.getTokens().add(token);
		}
	}

	void createAnnotationsForToken(Token token, String... tuple) {
		for (int columnNumber = 1; columnNumber < tuple.length; columnNumber++) {
			final Annotation anno = TreetaggerFactory.eINSTANCE.createAnnotation(findColumnName(columnNumber),
					tuple[columnNumber].trim());
			token.getAnnotations().add(anno);
		}
	}

	String findColumnName(int colNumber) {
		final String annoName;
		if (colNumber >= columnNames.size()) {
			annoName = DEFAULT_ANNOTATION_NAME;
		} else {
			annoName = columnNames.get(colNumber);
		}
		return annoName;
	}
}
