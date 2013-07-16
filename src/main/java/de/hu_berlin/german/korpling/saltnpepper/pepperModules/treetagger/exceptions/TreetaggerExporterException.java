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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.exceptions;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;

public class TreetaggerExporterException extends PepperModuleException
{

	private static final long serialVersionUID = -3401782824448250143L;
	private static String prefixStr= "This Exception was thrown by TreetaggerImporter, an import module for pepper. The reason is: ";
	
	public TreetaggerExporterException()
	{ super(); }
	
    public TreetaggerExporterException(String s)
    { super(prefixStr + s); }
    
	public TreetaggerExporterException(String s, Throwable ex)
	{super(prefixStr + s, ex); }
}

