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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperInterfaceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.RETURNING_MODE;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperImporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.exceptions.TreetaggerImporterException;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper.Treetagger2SaltMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

@Component(name="TreetaggerImporterJavaComponent", factory="PepperImporterComponentFactory")
@Service(value=PepperImporter.class)
public class TreetaggerImporterJava extends PepperImporterImpl implements PepperImporter
{

	//---------------------------------------------------------------------------------------
	private void log(int logLevel, String logText) {
		if (this.getLogService()!=null) {
			this.getLogService().log(logLevel, logText);
		}
	}

	@SuppressWarnings("unused")
	private void logError  (String logText) { this.log(LogService.LOG_ERROR,   logText); }
	private void logWarning(String logText) { this.log(LogService.LOG_WARNING, logText); }
	@SuppressWarnings("unused")
	private void logInfo   (String logText) { this.log(LogService.LOG_INFO,    logText); }
	private void logDebug  (String logText) { this.log(LogService.LOG_DEBUG,   logText); }

	//---------------------------------------------------------------------------------------
	private Properties properties = null;
	
	public Properties getProperties() {
		return this.properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	//---------------------------------------------------------------------------------------
	
	public TreetaggerImporterJava()
	{
		super();
		this.name= "TreetaggerImporterJava";
		//for testing the symbolic name has to be set without osgi
		if (	(this.getSymbolicName()==  null) ||
				(this.getSymbolicName().equalsIgnoreCase("")))
			this.setSymbolicName("de.hu_berlin.german.korpling.saltnpepper.pepperModules.TreetaggerModules");
		this.init();
		logDebug(this.getName()+" is created...");
	}

	protected void init()
	{
		this.supportedFormats= new BasicEList<FormatDefinition>();
		FormatDefinition formatDef= PepperInterfaceFactory.eINSTANCE.createFormatDefinition();
		formatDef.setFormatName("treetagger");
		formatDef.setFormatVersion("1.0");
		this.supportedFormats.add(formatDef);
	}
	
	private EList<FormatDefinition> supportedFormats= null;
	
	@Override
	public EList<FormatDefinition> getSupportedFormats() 
	{
		return(this.supportedFormats);
	}

	/**
	 * Stores relation between documents and their resource 
	 */
	private Map<SElementId, URI> documentResourceTable= null;
	
	@Override
	public void importCorpusStructure(SCorpusGraph corpusGraph)
			throws PepperModuleException
	{
		this.setSCorpusGraph(corpusGraph);
		if (this.getSCorpusGraph()== null)
			throw new PepperModuleException(this.name+": Cannot start with importing corpus, because salt project isn�t set.");
		
		if (this.getCorpusDefinition()== null)
			throw new PepperModuleException(this.name+": Cannot start with importing corpus, because no corpus definition to import is given.");
		if (this.getCorpusDefinition().getCorpusPath()== null)
			throw new PepperModuleException(this.name+": Cannot start with importing corpus, because the path of given corpus definition is null.");
		if (this.getCorpusDefinition().getCorpusPath().isFile())
		{
			if (	(this.getCorpusDefinition().getCorpusPath().toFileString().endsWith("/")) || 
					(this.getCorpusDefinition().getCorpusPath().toFileString().endsWith("\\")))
			{//clean uri in corpus path (if it is a folder and ends with/, / has to be removed)
				this.getCorpusDefinition().setCorpusPath(this.getCorpusDefinition().getCorpusPath().trimSegments(1));
			}//clean uri in corpus path (if it is a folder and ends with/, / has to be removed)
			
			try {
				this.documentResourceTable= this.createCorpusStructure(this.getCorpusDefinition().getCorpusPath(), null, null);
			} catch (IOException e) {
				throw new PepperModuleException(this.name+": Cannot start with importing corpus, because some exception occurs: ",e);
			}
		}	
	}
	
	@Override
	public void start(SElementId sElementId) throws PepperModuleException 
	{
		if (sElementId.getSIdentifiableElement()!= null)
		{	
			if (sElementId.getSIdentifiableElement() instanceof SDocument)
			{	
				this.returningMode= RETURNING_MODE.PUT;
				URI uri= this.documentResourceTable.get(sElementId);
				if (uri== null)
					throw new TreetaggerImporterException("Cannot import document '"+sElementId+"', because no corresponding uri was found.");
				
				
				if (this.getSpecialParams()!=null) {
					String propertyFileName = this.getSpecialParams().toFileString();
					try {
						this.setProperties(new Properties());
						this.getProperties().load(new FileInputStream(propertyFileName));
					} catch (IOException e) {
						logWarning(String.format("couldn´t load properties file '%s'. using default values.",propertyFileName));
					}
				}

				Document tDocument = this.loadFromFile(uri);
				//TODO: Factory!
				if (tDocument==null) {
					//TODO: take document out of the process
				}
				else {
					Treetagger2SaltMapper mapper = new Treetagger2SaltMapper();
					mapper.setProperties(this.getProperties());
					mapper.setLogService(this.getLogService());
					mapper.convert(tDocument,this.getSCorpusGraph().getSDocument(sElementId));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Document loadFromFile(URI uri)
	{
		Document retVal= null;
		if (uri!= null)
		{
			// create resource set and resource 
			ResourceSet resourceSet = new ResourceSetImpl();
	
			// Register XML resource factory
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("treetagger",new XMIResourceFactoryImpl());
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tab",new TabResourceFactory());
			Resource resource= null;
			try {
				//load resource 
				resource = resourceSet.createResource(uri);

				if (resource== null)
					throw new TreetaggerImporterException("Cannot load The resource is null.");
				
				@SuppressWarnings("rawtypes")
				//options map for resource.load
				Map options = new HashMap();
				//put logService for TabResource loading into options
				options.put("LOGSERVICE", this.getLogService());
				//put properties for TabResource loading into options
				options.put("PROPERTIES", this.getProperties());

				resource.load(options);
			} 
			catch (IOException e) 
			{	throw new TreetaggerImporterException("Cannot load resource '"+uri+"'.");	}
			catch (NullPointerException e) 
			{	throw new TreetaggerImporterException("Cannot load resource '"+uri+"'.");	}
			if (resource.getContents().size()>0) {
				retVal= (Document) resource.getContents().get(0);
			}
		}
		return(retVal);
	}
	
	protected void activate(ComponentContext componentContext) 
	{
		this.setSymbolicName(componentContext.getBundleContext().getBundle().getSymbolicName());
	}

	protected void deactivate(ComponentContext componentContext) {
		logDebug(this.getName()+" is deactivated...");
	}
}
