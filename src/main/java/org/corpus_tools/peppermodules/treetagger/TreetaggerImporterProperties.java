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
package org.corpus_tools.peppermodules.treetagger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;
import org.corpus_tools.salt.common.SToken;

public class TreetaggerImporterProperties extends PepperModuleProperties {

	private static final long serialVersionUID = -7528434389946019271L;

	public static final String PREFIX = "treetagger.input.";

	public static final String PROP_ANNOTATE_UNANNOTATED_SPANS = PREFIX + "annotateUnannotatedSpans";

	public static final String PROP_MAKE_POINTING_RELATIONS = PREFIX + "makePointingRelations";

	public static final String PROP_POINTING_RELATION_TARGET_ANNOTATION = PREFIX + "pointingRelationTargetAnnotation";
	
	public static final String PROP_POINTING_RELATION_ID_ANNOTATION = PREFIX + "pointingRelationIDAnnotation";
	
	public static final String PROP_POINTING_RELATION_NS = PREFIX + "pointingRelationNamespace";

	public static final String PROP_POINTING_RELATION_TYPE = PREFIX + "pointingRelationType";

	public static final String PROP_INVERT_POINTING_RELATIONS = PREFIX + "invertPointingRelations";

	public static final String PROP_POINTING_RELATION_EDGE_ANNOTATION = PREFIX + "pointingRelationEdgeAnnotation";

	public static final String PROP_POINTING_RELATION_SUPPRESS_ID = PREFIX + "pointingRelationSuppressID";

	public static final String PROP_POINTING_RELATION_SUPPRESS_TARGET = PREFIX + "pointingRelationSuppressTarget";

	public static final String PROP_POINTING_RELATION_SUPPRESS_LABEL = PREFIX + "pointingRelationSuppressLabel";

	public static final String PROP_POINTING_RELATION_USE_HASHTAG = PREFIX + "pointingRelationUseHash";

	public static final String PROP_SPAN_ANNO_NAMESPACE = PREFIX + "spanAnnotationNamespace";

	public static final String PROP_ANNOTATE_ALL_SPANS_WITH_NAME = PREFIX + "annotateAllSpansWithSpanName";
	/**
	 * States the meta tag used to mark the TreeTagger document in the input
	 * file(s).
	 *
	 */
	public static final String PROP_META_TAG = PREFIX + "metaTag";
	/**
	 * property key for the encoding of input file
	 */
	public static final String PROP_FILE_ENCODING = "treetagger.input.fileEncoding";

	/**
	 * property to determine the column names. The value is a comma separated
	 * list, starting with the value 'tok'. The default value is 'tok, pos,
	 * lemma'.
	 */
	public static final String PROP_COLUMN_NAMES = "columnNames";
	public static final String COLUMN_NAMES_SEPARATOR = ",";
	public static final String COLUMN_NAMES_TOK = "tok";
	/**
	 * Name of property to determine the separator which should be artificially
	 * added after a token, when mapping treetagger token to STextualDS in Salt.
	 * The default separator is a whitespace given by the character sequence "
	 * ". Note, the separator sequence, must be surrounded by double quotes. To
	 * shut of the adding of a separator, just this property value to "".
	 *
	 */
	public static final String PROP_SEPARATOR_AFTER_TOKEN = PREFIX + "separatorAfterToken";

	private static final Pattern PATTERN_PROP_INPUT_COLUMNS = Pattern.compile("treetagger\\.input\\.column");
	private static final String DEFAULT_POS_NAME = "pos";
	private static final String DEFAULT_LEMMA_NAME = "lemma";

	/**
	 * Set to true to add the element name as a prefix to all span element
	 * attribute annotations.
	 *
	 */
	public static final String PROP_PREFIX_SPAN_ANNOS_WITH_ELEMENT = PREFIX + "prefixElementToAttributes";
	public static final String PROP_PREFIX_ELEMENT_SEPARATOR = PREFIX + "prefixElementSeparator";

	/**
	 * Property of find+replace string pairs to alter specific token values.
	 * Useful for incorporating XML escapes into an imported file's tokens.
	 */
	public static final String PROP_TOKEN_REPLACEMENTS = PREFIX + "replaceTokens";

	/**
	 * Whether to apply token replacement patterns to annotations too. Only
	 * effective if token replacements have been defined, true by default.
	 */
	public static final String PROP_ANNO_REPLACEMENTS = PREFIX + "replacementsInAnnos";

	public TreetaggerImporterProperties() {
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATE_UNANNOTATED_SPANS, Boolean.class,
				"If set true, this switch will cause the module to annotate all spans without attributes with their name as attribute and value.",
				false, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATE_ALL_SPANS_WITH_NAME, Boolean.class,
				"If set true, this switch will cause the module to annotate all spans with their name as attribute and value.",
				false, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_META_TAG, String.class,
				"States the meta tag used to mark the TreeTagger document in the input file(s).", "meta", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_SPAN_ANNO_NAMESPACE, String.class,
				"Namespace to give to span annotations.", null, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_SEPARATOR_AFTER_TOKEN, String.class,
				"Determines the separator which should be artificially added after a token, when mapping treetagger token to STextualDS in Salt. The default separator is a whitespace given by the character sequence \" \". Note, the separator sequence, must be surrunded by double quotes. To shut of the adding of a separator, just this property value to \"\"",
				" ", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_FILE_ENCODING, String.class,
				"Determines the encoding of the input files. ", "UTF-8", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_PREFIX_SPAN_ANNOS_WITH_ELEMENT, Boolean.class,
				"Set to true to add the element name as a prefix to all span element attribute annotations.", false,
				false));
		this.addProperty(new PepperModuleProperty<String>(PROP_PREFIX_ELEMENT_SEPARATOR, String.class,
				"Separator to use when prefixing span attribute annotations with element name.", "_", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_TOKEN_REPLACEMENTS, String.class,
				"Specify values to find and replace in tokens. This value is a comma separated list of mappings: \"REPLACED_STRING\" : \"REPLACEMENT\" (, \"REPLACED_STRING\" : \"REPLACEMENT\")*",
				""));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_ANNO_REPLACEMENTS, Boolean.class,
				"If true, make token replacement patterns apply to annotations as well.", true, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_COLUMN_NAMES, String.class,
				"Property to determine the column names. The value is a comma separated list, starting with the value 'tok'. The default value is 'tok, pos, lemma'.",
				"tok, pos, lemma", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_MAKE_POINTING_RELATIONS, Boolean.class,
				"If set true, the importer attempts to read pointing relations from selected span annotations.",
				false, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POINTING_RELATION_TARGET_ANNOTATION, String.class,
				"The name of a span annotation attribute containing an attribute encoding the id of target spans",
				"head", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POINTING_RELATION_ID_ANNOTATION, String.class,
				"The name of a span annotation attribute containing the id referred to in target annotations",
				"id", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POINTING_RELATION_TYPE, String.class,
				"The edge type to assign to pointing relations.",
				"dep", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POINTING_RELATION_NS, String.class,
				"The namespace to assign to pointing relations.",
				"dep", false));
		this.addProperty(new PepperModuleProperty<String>(PROP_POINTING_RELATION_EDGE_ANNOTATION, String.class,
				"The name of a span annotation attribute containing annotation labels to add as edge annotations to pointing relations",
				"func", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_INVERT_POINTING_RELATIONS, Boolean.class,
				"If set true, pointing relations point towards spans with a pointing relation target annotation, instead of from them.",
				true, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_POINTING_RELATION_USE_HASHTAG, Boolean.class,
				"If set true, pointing relations targets with hashtag are interpreted as href syntax (hashtag is ignored in target).",
				true, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_POINTING_RELATION_SUPPRESS_ID, Boolean.class,
			"If set true, ID annotation of pointing relation markers is not imported as span annotation.",
			true, false));

		this.addProperty(new PepperModuleProperty<Boolean>(PROP_POINTING_RELATION_SUPPRESS_TARGET, Boolean.class,
			"If set true, target annotation of pointing relation markers is not imported as span annotation.",
			true, false));

		this.addProperty(new PepperModuleProperty<Boolean>(PROP_POINTING_RELATION_SUPPRESS_LABEL, Boolean.class,
			"If set true, label annotation of pointing relation markers is not imported as span annotation.",
			true, false));

	}

	public Boolean getAnnotateUnannotatedSpans() {
		return ((Boolean) this.getProperty(PROP_ANNOTATE_UNANNOTATED_SPANS).getValue());
	}

	public Boolean getMakePointingRelations() {
		return ((Boolean) this.getProperty(PROP_MAKE_POINTING_RELATIONS).getValue());
	}
	
	public Boolean getInvertPointingRelations() {
		return ((Boolean) this.getProperty(PROP_INVERT_POINTING_RELATIONS).getValue());
	}

	public Boolean getSuppressPRID() {
		return ((Boolean) this.getProperty(PROP_POINTING_RELATION_SUPPRESS_ID).getValue());
	}

	public Boolean getPRUseHash() {
		return ((Boolean) this.getProperty(PROP_POINTING_RELATION_USE_HASHTAG).getValue());
	}

	public Boolean getSuppressPRTarget() {
		return ((Boolean) this.getProperty(PROP_POINTING_RELATION_SUPPRESS_TARGET).getValue());
	}

	public Boolean getSuppressPRLabel() {
		return ((Boolean) this.getProperty(PROP_POINTING_RELATION_SUPPRESS_LABEL).getValue());
	}

	public String getPointingTargetAnno() {
		return (String) this.getProperty(PROP_POINTING_RELATION_TARGET_ANNOTATION).getValue();
	}

	public String getPointingIDAnno() {
		return (String) this.getProperty(PROP_POINTING_RELATION_ID_ANNOTATION).getValue();
	}

	public String getPointingType() {
		return (String) this.getProperty(PROP_POINTING_RELATION_TYPE).getValue();
	}

	public String getSpanAnnotationNamespace() {
		return (String) this.getProperty(PROP_SPAN_ANNO_NAMESPACE).getValue();
	}

	public String getPointingNS() {
		return (String) this.getProperty(PROP_POINTING_RELATION_NS).getValue();
	}

	public String getPointingEdgeAnno() {
		return (String) this.getProperty(PROP_POINTING_RELATION_EDGE_ANNOTATION).getValue();
	}

	public Boolean getAnnotateAllSpansWithName() {
		return ((Boolean) this.getProperty(PROP_ANNOTATE_ALL_SPANS_WITH_NAME).getValue());
	}

	public Boolean getPrefixSpanAnnotation() {
		return ((Boolean) this.getProperty(PROP_PREFIX_SPAN_ANNOS_WITH_ELEMENT).getValue());
	}

	public String getPrefixSpanSeparator() {
		return (String) this.getProperty(PROP_PREFIX_ELEMENT_SEPARATOR).getValue();
	}

	/**
	 * Returns the separator to be used to separate the text covered by
	 * {@link SToken}.
	 *
	 * @return
	 */
	public String getSeparatorAfterToken() {
		String separator = (String) this.getProperty(PROP_SEPARATOR_AFTER_TOKEN).getValue();
		separator.replace("\"", "");
		return (separator);
	}

	/**
	 * a map of Strings to be replaced and the corresponding replacement String.
	 *
	 */
	private Map<String, String> replacementMapping = null;

	/**
	 * Returns a map of Strings to be escaped and the corresponding replacement
	 * Strings. This map is computed from the property
	 * {@link #PROP_TOKEN_REPLACEMENTS}, which has the form: \"REPLACED_STRING\"
	 * : \"REPLACEMENT\" (, \"REPLACED_STRING\" : \"REPLACEMENT\"). It is
	 * applied to token values, and if {@link #PROP_ANNO_REPLACEMENTS} is true,
	 * then also to annotation values.
	 *
	 * @return
	 */
	public Map<String, String> getReplacementMapping() {
		if (replacementMapping == null) {
			PepperModuleProperty<String> prop = (PepperModuleProperty<String>) getProperty(PROP_TOKEN_REPLACEMENTS);

			String replacements = prop.getValue();
			if ((replacements != null) && (!replacements.isEmpty())) {
				replacementMapping = new Hashtable<String, String>();

				String[] singleMappings = replacements.split(",");
				if (singleMappings.length > 0) {
					for (String singleMapping : singleMappings) {
						String[] parts = singleMapping.split(":");
						{
							if (parts.length == 2) {
								replacementMapping.put(parts[0].trim().replace("\"", ""),
										parts[1].trim().replace("\"", ""));
							}
						}
					}
				}
			}
		}
		return (replacementMapping);
	}

	public Boolean getReplaceInAnnos() {
		return ((Boolean) this.getProperty(PROP_ANNO_REPLACEMENTS).getValue());
	}

	/**
	 * Returns a list of all column names, which is given by
	 * {@link #PROP_COLUMN_NAMES}. The first entry is always 'tok'.
	 * 
	 * @return
	 */
	public List<String> getColumnNames() {
		final String columnNamesAsString = getProperty(PROP_COLUMN_NAMES).getValue().toString();
		List<String> columnNames = splitStringByAndTrim(columnNamesAsString, COLUMN_NAMES_SEPARATOR);
		columnNames = whenColumnNamesDoesNotStartWithTokThenAddTok(columnNames);
		return columnNames;
	}

	private List<String> whenColumnNamesDoesNotStartWithTokThenAddTok(List<String> columnNames) {
		if (!COLUMN_NAMES_TOK.equalsIgnoreCase(columnNames.get(0))) {
			columnNames.add(0, COLUMN_NAMES_TOK);
		}
		return columnNames;
	}

	private List<String> splitStringByAndTrim(String columnNamesAsString, String separator) {
		final List<String> entries = new ArrayList<>();
		final String[] entriesAsArray = columnNamesAsString.split(separator);
		for (String entry : entriesAsArray) {
			entries.add(entry.trim());
		}
		return entries;
	}
}
