package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.exceptions;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;

public class TreetaggerImporterException extends PepperModuleException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3401782824448250143L;
	private static String prefixStr= "This Exception was throwed by TreetaggerImporter, an import module for pepper. The reason is: ";
	
	public TreetaggerImporterException()
	{ super(); }
	
    public TreetaggerImporterException(String s)
    { super(prefixStr + s); }
    
	public TreetaggerImporterException(String s, Throwable ex)
	{super(prefixStr + s, ex); }
}

