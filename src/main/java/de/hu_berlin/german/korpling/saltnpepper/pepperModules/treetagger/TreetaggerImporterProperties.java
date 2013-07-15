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
