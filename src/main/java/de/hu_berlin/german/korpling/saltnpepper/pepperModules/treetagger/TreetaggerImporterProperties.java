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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperty;

public class TreetaggerImporterProperties extends PepperModuleProperties {
	public static final String PREFIX="treetagger.input.";
	
	private static final String PROP_ANNOTATE_UNANNOTATED_SPANS= PREFIX+ "annotateUnannotatedSpans";
	
	private static final String PROP_ANNOTATE_ALL_SPANS_WITH_NAME= PREFIX+ "annotateAllSpansWithSpanName";
	
	public TreetaggerImporterProperties()
	{
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATE_UNANNOTATED_SPANS, Boolean.class, "If set true, this switch will cause the module to annotate all spans without attributes with their name as attribute and value.", false, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_ANNOTATE_ALL_SPANS_WITH_NAME, Boolean.class, "If set true, this switch will cause the module to annotate all spans with their name as attribute and value.", false, false));		
	}
	
	public Boolean getAnnotateUnannotatedSpans()
	{
		return((Boolean)this.getProperty(PROP_ANNOTATE_UNANNOTATED_SPANS).getValue());
	}
	
	public Boolean getAnnotateAllSpansWithName()
	{
		return((Boolean)this.getProperty(PROP_ANNOTATE_ALL_SPANS_WITH_NAME).getValue());
	}
	
}
