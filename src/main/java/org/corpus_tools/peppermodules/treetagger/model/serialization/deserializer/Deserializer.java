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
package org.corpus_tools.peppermodules.treetagger.model.serialization.deserializer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.corpus_tools.pepper.modules.exceptions.PepperModuleException;
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

public class Deserializer {
	public static final String DEFAULT_ANNOTATION_NAME = "anyAnno";
	public static final String COLUMN_SEPARATOR = "\t";
	public static final String COLUMN_TOKEN_TEXT = "pos";
	public static final String COLUMN_POS = "pos";
	public static final String COLUMN_LEMMA = "lemma";

	private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);
	private static final Character utf8BOM = new Character((char) 0xFEFF);
	private String fileEncoding = "UTF-8";
	private String metaTagName = "meta";
	private URI location = null;
	private List<Document> documents = new ArrayList<>();
	private Document currentDocument = null;
	private List<Span> openSpans = new ArrayList<>();
	int lineNumber = 0;
	private boolean documentTagIsOpen = false;
	List<Integer> rowsWithTooMuchColumns = new ArrayList<>();
	List<Integer> rowsWithTooLessColumns = new ArrayList<>();
	List<String> columnNames = new ArrayList<>();

	Deserializer() {
		setDefaultColumnNames();
	}

	public void setDefaultColumnNames() {
		setColumnNames(Arrays.asList(COLUMN_TOKEN_TEXT, COLUMN_POS, COLUMN_LEMMA));
	}

	public void setMetaTagName(String metaTagName) {
		this.metaTagName = metaTagName;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
		if (this.columnNames == null) {
			this.columnNames = new ArrayList<>();
		}
	}

	public void setLocation(URI location) {
		this.location = location;
	}

	public List<Document> deserialize() {
		if (location == null) {
			throw new PepperModuleException("Cannot load any resource, because no uri is given.");
		}
		try (BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(location.toFileString()), fileEncoding));) {
			String line = null;
			lineNumber = 1;
			while ((line = fileReader.readLine()) != null) {
				mapLine(line, lineNumber);
				lineNumber++;
			}
			endDocument();
		} catch (IOException e) {
			throw new PepperModuleException("Cannot read treetagger file '" + location + "'. ", e);
		}
		setAllDocumentNames();
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

	private String extractDocumentName(URI location) {
		return location.lastSegment().split("[.]")[0];
	}

	private void mapLine(String line, long lineNr) {
		if (line.trim().length() == 0) {
			return;
		}
		line = removeBOM(line);
		if (XMLUtils.isProcessingInstructionTag(line)) {
			// do nothing; ignore processing instructions
		} else if (XMLUtils.isStartTag(line)) {
			final String startTagName = XMLUtils.extractTagName(line);
			if (startTagName.equalsIgnoreCase(metaTagName)) {
				beginDocument(line);
			} else {
				beginSpan(startTagName, line);
			}
		} else if (XMLUtils.isEndTag(line)) {
			String endTagName = XMLUtils.extractTagName(line);
			if (endTagName.equalsIgnoreCase(metaTagName)) {
				documentTagIsOpen = false;
				endDocument();
			} else {
				endSpan(endTagName);
			}
		} else {
			if (currentDocument == null) {
				beginDocument(null);
			}
			final Token token = createTokenFromLine(line, lineNr);
			connectTokenWithOpenSpans(token);
			currentDocument.getTokens().add(token);
		}
	}

	private String removeBOM(String line) {
		if ((lineNumber == 0) && (line.startsWith(utf8BOM.toString()))) {
			line = line.substring(utf8BOM.toString().length());
			logger.trace("recognised BOM and ignored for file '" + location + "'");
		}
		return line;
	}

	private void setAllDocumentNames() {
		final String documentName = extractDocumentName(location);
		if (documents.size() == 1) {
			documents.get(0).setName(documentName);
		} else {
			int numberOfDocuments = 1;
			for (Document document : documents) {
				document.setName(documentName + "_" + numberOfDocuments);
				numberOfDocuments++;
			}
		}
	}

	private void beginDocument(String startTag) {
		if (currentDocument != null) {
			endDocument();
		}
		currentDocument = TreetaggerFactory.eINSTANCE.createDocument();
		documentTagIsOpen = startTag != null;
		if (documentTagIsOpen) {
			addAttributesAsAnnotations(startTag, currentDocument);
		}
	}

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
			if (documentTagIsOpen) {
				logger.warn(
						String.format("input file '%s' (line %d): missing document end tag. document will be ignored!",
								location.lastSegment(), lineNumber));
			} else {
				documents.add(currentDocument);
			}

			currentDocument = null;
			documentTagIsOpen = false;
		}
		openSpans.clear();
	}

	private void addAttributesAsAnnotations(String tag, AnnotatableElement annotatableElement) {
		final Map<String, String> attributeValuePairs = XMLUtils.extractAttributeValuePairs(tag);
		for (Entry<String, String> attributeValuePair : attributeValuePairs.entrySet()) {
			final Annotation annotation = TreetaggerFactory.eINSTANCE.createAnnotation(attributeValuePair.getKey(),
					attributeValuePair.getValue().replace("&lt;", "<").replace("&gt;",">").replace("&amp;","&"));
			annotatableElement.getAnnotations().add(annotation);
		}
	}

	private void beginSpan(String spanName, String startTag) {
		if (currentDocument == null) {
			beginDocument(null);
		}
		Span span = TreetaggerFactory.eINSTANCE.createSpan();
		openSpans.add(0, span);
		span.setName(spanName);
		addAttributesAsAnnotations(startTag, span);
	}

	private void endSpan(String spanName) {
		if (currentDocument == null) {
			logger.warn(
					String.format("input file '%s' (line '%d'): end tag '</%s>' out of nowhere. tag will be ignored!",
							location.lastSegment(), lineNumber, spanName));
			return;
		}
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

	private Token createTokenFromLine(String line, long lineNr) {
		final String[] tuple = line.split(COLUMN_SEPARATOR);
		doesTupleHasExpectedNumOfColumns(tuple);
		final Token token = Treetagger.buildToken().withLine(lineNr).withText(tuple[0].trim()).build();
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

	public static class Builder {
		private Deserializer deserializer = new Deserializer();

		public Builder withColumnNames(List<String> columnNames) {
			deserializer.setColumnNames(columnNames);
			return this;
		}

		public Builder withFileEncoding(String fileEncoding) {
			deserializer.setFileEncoding(fileEncoding);
			return this;
		}

		public Builder withMetaTagName(String metaTagName) {
			deserializer.setMetaTagName(metaTagName);
			return this;
		}

		public List<Document> from(URI location) {
			deserializer.setLocation(location);
			return deserializer.deserialize();
		}
	}
}
