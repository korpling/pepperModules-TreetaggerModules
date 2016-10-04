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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.corpus_tools.pepper.modules.exceptions.PepperModuleException;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.AnyAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for loading and saving of treetagger data
 * 
 * @author hildebax
 * 
 */
public class TabWriter {
	private static final Logger logger = LoggerFactory.getLogger(TabWriter.class);
	// column seperator
	private String separator = "\t";

	private String POSName = "pos";
	private String LemmaName = "lemma";

	/**
	 * property key for the meta tag of input
	 */
	public static final String propertyInputMetaTag = "treetagger.input.metaTag";

	/**
	 * property key for the encoding of input file
	 */
	public static final String propertyInputFileEncoding = "treetagger.input.fileEncoding";

	/**
	 * property key for the meta tag of output
	 */
	public static final String propertyOutputMetaTag = "treetagger.output.metaTag";

	/**
	 * property key for the encoding of output file
	 */
	public static final String propertyOutputFileEncoding = "treetagger.output.fileEncoding";

	/**
	 * property key for the option to export any annotation
	 */
	public static final String propertyExportAnyAnnotation = "treetagger.output.exportAnyAnnotation";

	private static final Pattern inputColumnPattern = Pattern.compile("treetagger\\.input\\.column");

	// property default values
	private static final String defaultOutputFileEncoding = "UTF-8";
	private static final String defaultMetaTag = "meta";
	private static final String defaultExportAnyAnnotation = "true";

	String currentFileName = "";
	URI location = null;

	private Properties properties = null;

	/**
	 * Getter for the Properties
	 * 
	 * @return properties
	 */
	public Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	/**
	 * Stores a treetagger model into tab separated file
	 * 
	 * @param options
	 *            a map that may contain an instance of LogService and an
	 *            instance of Properties, with {@link #logServiceKey} and
	 *            {@link #propertiesKey} respectively as keys
	 */
	public void save(Document document, URI location, java.util.Map<?, ?> options) throws java.io.IOException {
		if (options != null) {
			getProperties().putAll(options);
		}
		if (document == null) {
			throw new PepperModuleException("Cannot treetagger document, because the passed document was empty.");
		}
		this.location = location;
		this.currentFileName = location.toFileString();

		String metaTag = getProperties().getProperty(propertyOutputMetaTag, defaultMetaTag);
		logger.info(String.format("using meta tag '%s'", metaTag));

		String fileEncoding = getProperties().getProperty(propertyOutputFileEncoding, defaultOutputFileEncoding);
		logger.info(String.format("using output file encoding '%s'", fileEncoding));

		boolean exportAnyAnnotation = getProperties()
				.getProperty(propertyExportAnyAnnotation, defaultExportAnyAnnotation).equalsIgnoreCase("true");
		logger.info("exporting any annotation = " + exportAnyAnnotation);

		BufferedWriter fileWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(this.currentFileName), fileEncoding));
		try {

			// this list and this hashmap are used if any annotation is
			// to be exported
			// the list will contain the sorted names of all annotation
			// names occuring on all tokens in the document
			// the map will map from token index to another map, which
			// maps from annotation name to AnyAnotation
			ArrayList<String> columnNamesList = null;
			HashMap<Integer, HashMap<String, AnyAnnotation>> tokenMap = null;

			if (exportAnyAnnotation) {
				// calculate number of columns and collect their names
				HashSet<String> annoNamesSet = new HashSet<String>();
				tokenMap = new HashMap<Integer, HashMap<String, AnyAnnotation>>();
				for (Integer tokenIndex = 0; tokenIndex < document.getTokens().size(); tokenIndex++) {
					Token token = document.getTokens().get(tokenIndex);
					List<Annotation> annotationList = token.getAnnotations();
					if ((token.getPosAnnotation() != null) && (token.getLemmaAnnotation() != null)
							&& (annotationList.size() > 2)) {
						tokenMap.put(tokenIndex, new HashMap<String, AnyAnnotation>());
						HashMap<String, AnyAnnotation> annoMap = tokenMap.get(tokenIndex);
						for (int annotationIndex = 0; annotationIndex < annotationList.size(); annotationIndex++) {
							Annotation annotation = annotationList.get(annotationIndex);
							if (annotation instanceof AnyAnnotation) {
								String annotationName = annotation.getName();
								annoNamesSet.add(annotationName);
								annoMap.put(annotationName, (AnyAnnotation) annotation);
							}
						}
					}
				}
				// sort columns
				columnNamesList = new ArrayList<String>(annoNamesSet);
				Collections.sort(columnNamesList);
				logger.info(
						"The following columns appear in the output file additionally to word form, part-of-speech and lemma: "
								+ columnNamesList.toString());
			}

			if (documentHasMetaAnnotations(document)) {
				fileWriter.write(String.format("<%s", metaTag));
				for (int i = 0; i < document.getAnnotations().size(); i++) {
					Annotation annotation = document.getAnnotations().get(i);
					fileWriter.write(String.format(" %s=\"%s\"", annotation.getName(), annotation.getValue()));
				}
				fileWriter.write(">\n");
			}
			ArrayList<Span> spanList = new ArrayList<Span>();
			HashMap<String, Integer> spanNamesCounts = new HashMap<String, Integer>();

			for (Integer tokenIndex = 0; tokenIndex < document.getTokens().size(); tokenIndex++) {
				Token token = document.getTokens().get(tokenIndex);

				// write end tags
				for (int spanIndex = spanList.size() - 1; spanIndex >= 0; spanIndex--) {
					Span span = spanList.get(spanIndex);
					if (!token.getSpans().contains(span)) {
						String spanName = span.getName();
						fileWriter.write("</" + spanName + ">\n");
						spanList.remove(span);
						Integer spanNameCount = spanNamesCounts.get(spanName);
						if (spanNameCount == 1) {
							spanNamesCounts.remove(spanName);
						} else {
							spanNamesCounts.put(spanName, spanNameCount - 1);
						}
					}
				}

				// calculate order for new opening tags by number of
				// tokens contained in spans ("size" of span)
				// if new opening spans have different sizes, the bigger
				// ones must open before the smaller

				// for each occuring span size, a key is put into this
				// map, mapping to a list of spans with this size
				HashMap<Integer, ArrayList<Span>> size2SpanlistMap = new HashMap<Integer, ArrayList<Span>>();
				// this list is used to have all occuring sizes sortable
				// without much converting of the maps keySet
				ArrayList<Integer> sizeList = new ArrayList<Integer>();
				for (int spanIndex = token.getSpans().size() - 1; spanIndex >= 0; spanIndex--) {
					Span span = token.getSpans().get(spanIndex);
					Integer spanSize = span.getTokens().size();
					if (!spanList.contains(span)) {
						if (!size2SpanlistMap.containsKey(spanSize)) {
							size2SpanlistMap.put(spanSize, new ArrayList<Span>());
							sizeList.add(spanSize);
						}
						size2SpanlistMap.get(spanSize).add(span);
					}
				}
				Collections.sort(sizeList);
				Collections.reverse(sizeList);
				// write opening tags in xml conform order
				for (int sizeIndex = 0; sizeIndex < sizeList.size(); sizeIndex++) {
					int size = sizeList.get(sizeIndex);

					ArrayList<String> currentSpannames = new ArrayList<String>();
					HashMap<String, ArrayList<Span>> currentSpanMap = new HashMap<String, ArrayList<Span>>();
					{
						ArrayList<Span> currentSpanlist = size2SpanlistMap.get(size);
						for (int spanIndex = 0; spanIndex < currentSpanlist.size(); spanIndex++) {
							Span span = currentSpanlist.get(spanIndex);
							String spanName = span.getName();
							if (!currentSpanMap.containsKey(spanName)) {
								currentSpannames.add(spanName);
								currentSpanMap.put(spanName, new ArrayList<Span>());
							}
							currentSpanMap.get(spanName).add(span);
						}
					}
					Collections.sort(currentSpannames);

					for (int spanNameIndex = 0; spanNameIndex < currentSpannames.size(); spanNameIndex++) {
						String spansName = currentSpannames.get(spanNameIndex);
						ArrayList<Span> currentSpanlist = currentSpanMap.get(spansName);

						if (!spanNamesCounts.containsKey(spansName)) {
							spanNamesCounts.put(spansName, currentSpanlist.size());
						} else {
							spanNamesCounts.put(spansName, spanNamesCounts.get(spansName) + currentSpanlist.size());
						}
						if (spanNamesCounts.get(spansName) > 1) {
							logger.warn("There are " + spanNamesCounts.get(spansName) + " spans named " + spansName
									+ " open at the same time!");
						}
						for (int spanIndex = 0; spanIndex < currentSpanlist.size(); spanIndex++) {
							Span span = currentSpanlist.get(spanIndex);
							spanList.add(span);
							fileWriter.write("<" + span.getName());
							for (Annotation anno : span.getAnnotations()) {
								fileWriter.write(" " + anno.getName() + "=\"" + anno.getValue() + "\"");
							}
							fileWriter.write(">\n");
						}
					}
				}

				// write token data
				fileWriter.write(token.getText());

				fileWriter.write(this.separator);

				Annotation anno = token.getPosAnnotation();
				if (anno != null) {
					fileWriter.write(anno.getValue());
				}

				fileWriter.write(this.separator);

				anno = token.getLemmaAnnotation();
				if ((anno != null) && (anno.getValue() != null)) {
					fileWriter.write(anno.getValue());
				}

				if (exportAnyAnnotation) {
					for (int colIndex = 0; colIndex < columnNamesList.size(); colIndex++) {
						fileWriter.write(this.separator);
						String columnName = columnNamesList.get(colIndex);
						HashMap<String, AnyAnnotation> annoMap = tokenMap.get(tokenIndex);
						if (annoMap != null) {
							anno = annoMap.get(columnName);
							if (anno != null) {
								fileWriter.write(anno.getValue());
							}
						}
					}
				}

				fileWriter.write("\n");
			}

			// write final end tags
			for (int spanIndex = spanList.size() - 1; spanIndex >= 0; spanIndex--) {
				Span span = spanList.get(spanIndex);
				fileWriter.write("</" + span.getName() + ">\n");
				spanList.remove(span);
			}

			if (documentHasMetaAnnotations(document)) {
				fileWriter.write(String.format("</%s>\n", metaTag));
			}

		} catch (RuntimeException e) {
			throw e;
		} finally {
			fileWriter.flush();
			fileWriter.close();
			fileWriter = null;
		}
	}

	private boolean documentHasMetaAnnotations(Document document) {
		if (document == null) {
			return false;
		}
		return !document.getAnnotations().isEmpty();
	}

	/**
	 * validates and return the input columns definition from the properties
	 * file
	 */
	protected HashMap<Integer, String> getColumns() {
		HashMap<Integer, String> retVal = new HashMap<Integer, String>();
		Object[] keyArray = this.getProperties().keySet().toArray();
		int numOfKeys = this.getProperties().size();
		String errorMessage = null;

		for (int keyIndex = 0; keyIndex < numOfKeys; keyIndex++) {

			String key = (String) keyArray[keyIndex];
			if (inputColumnPattern.matcher(key).find()) {

				// try to extract the number at the end of the key
				String indexStr = key.substring("treetagger.input.column".length());
				String name = this.getProperties().getProperty(key);
				Integer index = null;

				try {
					index = Integer.valueOf(indexStr);
				} catch (NumberFormatException e) {
					errorMessage = "Invalid property name '" + key + "': " + indexStr + " is not a valid number!";
					logger.error(errorMessage);
					throw new PepperModuleException(errorMessage, e);
				}

				// minimal index is 1
				if (index <= 0) {
					errorMessage = "Invalid settings in properties file: no column index less than 1 allowed!";
					logger.error(errorMessage);
					throw new PepperModuleException(errorMessage);
				}

				// with the standard Properties class, this can never happen...
				if (retVal.containsKey(index)) {
					errorMessage = "Invalid settings in properties file:  More than one column is defined for index '"
							+ index + "'";
					logger.error(errorMessage);
					throw new PepperModuleException(errorMessage);
				}

				if (retVal.containsValue(name)) {
					errorMessage = "Invalid settings in properties file:  More than one column is defined for name '"
							+ name + "'";
					logger.error(errorMessage);
					throw new PepperModuleException(errorMessage);
				}

				retVal.put(index, name);
			}
		}

		// return defaults if nothing is set in the properties file
		if (retVal.size() == 0) {
			retVal.put(1, this.POSName);
			retVal.put(2, this.LemmaName);
			return retVal;
		}

		// check consecutivity of indexes
		for (int index = 1; index <= retVal.size(); index++) {
			if (!retVal.containsKey(index)) {
				errorMessage = "Invalid settings in properties file: column indexes are not consecutive, column" + index
						+ " missing!";
				logger.error(errorMessage);
				throw new PepperModuleException(errorMessage);
			}
		}
		return retVal;
	}
}
