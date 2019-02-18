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

import java.util.List;
import java.util.Vector;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;

public class TreetaggerExporterProperties extends PepperModuleProperties {
	public static final String PREFIX = "treetagger.input.";

	/** Keyword for property */
	public static final String PROP_FILE_EXTENSION = "treetagger.fileExtension";
	/** Keyword for property */
	public static final String PROP_EXPORT_ANNOS = "treetagger.exportAnnotations";

	/** Keyword for property */
	public static final String PROP_FLATTEN = "treetagger.output.flatten";

	/** Extension for export file. default= tt. **/
	public static final String FILE_ENDING = "tt";

	public static final String PROP_REPLACE_GENERIC_SPAN_NAMES = "treetagger.output.replaceGenericSpanNames";
	/**
	 * Sets the meta tag used to mark the TreeTagger document in the output
	 * file(s).
	 **/
	public static final String PROP_META_TAG = "treetagger.output.metaTag";
	
	/** Choose a segmentation instead of exporting all tokens */
	public static final String PROP_SEG_NAME = "treetagger.segmentation.name";

	public TreetaggerExporterProperties() {
		this.addProperty(new PepperModuleProperty<String>(PROP_FILE_EXTENSION, String.class, "This property determines the ending of TreeTagger files, which are exported. The default value is '" + FILE_ENDING + "'.", FILE_ENDING, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_EXPORT_ANNOS, String.class, "If set true, each AnyAnnotation of tokens will appear in the output file.", false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_FLATTEN, Boolean.class, "If set true, the output directory structure is flat: all documents are put in the output root 	directory.", false, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_REPLACE_GENERIC_SPAN_NAMES, Boolean.class, "If set true, generic span names like 'sSpan123' will be replaced with the first annotation 	of the span found. If the span has no annotations, the generic name will not be replaced.", false, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_META_TAG, String.class, "Sets the meta tag used to mark the TreeTagger document in the output file(s).", "meta", false));
		this.addProperty(PepperModuleProperty.create().withName(PROP_SEG_NAME).withType(String.class).withDescription("Choose a segmentation instead of exporting all tokens").withDefaultValue(null).build());
	}

	/** Returns file ending for eported files. **/
	public String getFileEnding() {
		return ((String) this.getProperty(PROP_FILE_EXTENSION).getValue());
	}

	private List<String> exportAnnos = null;

	/**
	 * If set true, each AnyAnnotation of tokens will appear in the output file.
	 **/
	public List<String> getExportAnnos() {
		if (exportAnnos == null) {
			synchronized (this) {
				if (exportAnnos == null) {
					exportAnnos = new Vector<String>();
					String[] exportAnnos = ((String) this.getProperty(PROP_EXPORT_ANNOS).getValue()).split(",");
					for (String exportAnno : exportAnnos) {
						this.exportAnnos.add(exportAnno.trim());
					}
				}
			}
		}
		return (exportAnnos);
	}

	/**
	 * If set true, the output directory structure is flat: all documents are
	 * put in the output root directory.
	 **/
	public Boolean isFlatten() {
		return ((Boolean) this.getProperty(PROP_FLATTEN).getValue());
	}

	public Boolean isReplaceGenericSpanNamesProperty() {
		return ((Boolean) this.getProperty(PROP_REPLACE_GENERIC_SPAN_NAMES).getValue());
	}
	
	/**
	 * Get segmentation name for segmentation to choose.
	 * @return null if no name provided, else segmentation name
	 */
	public String getSegmentationName() {
		Object segName = getProperty(PROP_SEG_NAME).getValue();
		return segName == null? null : (String) segName;
	}
}
