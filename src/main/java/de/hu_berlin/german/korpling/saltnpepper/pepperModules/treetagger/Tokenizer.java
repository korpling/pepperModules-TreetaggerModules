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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.tokenizer.TTTokenizer;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.tokenizer.TTTokenizer.TTLanguages;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.tokenizer.Token;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperManipulator;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperManipulatorImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This manipulator tokenizes a given text like the tool Treetagger does. Therefore it uses abbreviation files, which can be
 * adopted and extended.
 * @author Florian Zipser
 * @version 1.0
 *
 */
@Component(name="TokenizerComponent", factory="PepperManipulatorComponentFactory")
@Service(value=PepperManipulator.class)
public class Tokenizer extends PepperManipulatorImpl 
{
	public static final String PROP_TOKENIZER_LANGUAGE= "treetagger.tokenizer.language";  
	public Tokenizer()
	{
		super();
		
		{//setting name of module
			this.name= "Tokenizer";
		}//setting name of module
		
		{//for testing the symbolic name has to be set without osgi
			if (	(this.getSymbolicName()==  null) ||
					(this.getSymbolicName().equals("")))
				this.setSymbolicName("de.hu_berlin.german.korpling.saltnpepper.pepperModules.TreetaggerModules");
		}//for testing the symbolic name has to be set without osgi
		
		{//just for logging: to say, that the current module has been loaded
			if (this.getLogService()!= null)
				this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is created...");
		}//just for logging: to say, that the current module has been loaded
	}
	
	private File abbreviationFolder= null;
	/**
	 * Returns the Abbriviation folder, where to find abbreviation files for the Treetagger tokenizer.
	 * @return
	 */
	private synchronized File getAbbriviationFolder()
	{
		if (abbreviationFolder== null)
			abbreviationFolder= new File(this.getResources().toFileString()+"/tokenizer/abbreviations");
		
		return(abbreviationFolder);
	}
	
	private TTLanguages language= null;
	/**
	 * Returns the language of the STextualDS given by a property file.
	 * @return
	 */
	private synchronized TTTokenizer.TTLanguages getLanguage()
	{
		if (language== null)
		{
			if (this.getSpecialParams()!= null)
			{
				//default case
				language= TTLanguages.en;
				Properties props= new Properties();
				{//load properties
					InputStream in= null;
					try {
						in = new FileInputStream(this.getSpecialParams().toFileString());
						props.load(in);
					} catch (FileNotFoundException e) {
						if (this.getLogService()!= null)
							this.getLogService().log(LogService.LOG_WARNING, "Cannot load property file '"+this.getSpecialParams()+"' for module '"+this.getName()+"', because of nested exception. ",e);
					} catch (IOException e) {
						if (this.getLogService()!= null)
							this.getLogService().log(LogService.LOG_WARNING, "Cannot load property file '"+this.getSpecialParams()+"' for module '"+this.getName()+"', because of nested exception. ",e);
					}
					finally
					{
						if (in!= null)
						try {
							in.close();
						} catch (IOException e) {
							if (this.getLogService()!= null)
								this.getLogService().log(LogService.LOG_WARNING, "Cannot close property file '"+this.getSpecialParams()+"' for module '"+this.getName()+"', because of nested exception. ",e);
						}
					}
				}//load properties
				String prop= props.getProperty(PROP_TOKENIZER_LANGUAGE).trim();
				if (prop!= null)
				{
					this.language= TTLanguages.valueOf(prop);
				}
			}
		}
		return(language);
	}
	
	/**
	 * This method is called by method start() of superclass PepperManipulator, if the method was not overriden
	 * by the current class. If this is not the case, this method will be called for every document which has
	 * to be processed.
	 * @param sElementId the id value for the current document or corpus to process  
	 */
	@Override
	public void start(SElementId sElementId) throws PepperModuleException 
	{
		if (	(sElementId!= null) &&
				(sElementId.getSIdentifiableElement()!= null) &&
				((sElementId.getSIdentifiableElement() instanceof SDocument)))
		{//only if given sElementId belongs to an object of type SDocument or SCorpus	
			SDocumentGraph sDocGraph= ((SDocument)sElementId.getSIdentifiableElement()).getSDocumentGraph();
			if(sDocGraph!= null)
			{//if document contains a document graph
				if (sDocGraph.getSTextualDSs()!= null)
				{
					for (STextualDS sText: sDocGraph.getSTextualDSs())
					{
						if (sText!= null)
						{
							TTTokenizer tokenizer= new TTTokenizer();
							tokenizer.setAbbriviationFolder(this.getAbbriviationFolder());
							tokenizer.setLngLang(this.getLanguage());
							for (Token token: tokenizer.tokenizeToToken(sText.getSText()))
							{
								SToken sTok= SaltFactory.eINSTANCE.createSToken();
								sDocGraph.addSNode(sTok);
								STextualRelation sTextRelation= SaltFactory.eINSTANCE.createSTextualRelation();
								sTextRelation.setSStart(token.start);
								sTextRelation.setSEnd(token.end);
								sTextRelation.setSToken(sTok);
								sTextRelation.setSTextualDS(sText);
								sDocGraph.addSRelation(sTextRelation);
							}
						}
					}
				}
			}//if document contains a document graph
		}//only if given sElementId belongs to an object of type SDocument or SCorpus
	}
	
//================================ start: methods used by OSGi
	/**
	 * This method is called by the OSGi framework, when a component with this class as class-entry
	 * gets activated.
	 * @param componentContext OSGi-context of the current component
	 */
	protected void activate(ComponentContext componentContext) 
	{
		this.setSymbolicName(componentContext.getBundleContext().getBundle().getSymbolicName());
		{//just for logging: to say, that the current module has been activated
			if (this.getLogService()!= null)
				this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is activated...");
		}//just for logging: to say, that the current module has been activated
	}

	/**
	 * This method is called by the OSGi framework, when a component with this class as class-entry
	 * gets deactivated.
	 * @param componentContext OSGi-context of the current component
	 */
	protected void deactivate(ComponentContext componentContext) 
	{
		{//just for logging: to say, that the current module has been deactivated
			if (this.getLogService()!= null)
				this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is deactivated...");
		}	
	}
//================================ start: methods used by OSGi
}

