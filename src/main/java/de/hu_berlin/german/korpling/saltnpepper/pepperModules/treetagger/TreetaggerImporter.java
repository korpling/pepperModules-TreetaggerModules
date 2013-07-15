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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResource;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperImporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.exceptions.TreetaggerImporterException;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper.Treetagger2SaltMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This class imports data from Treetagger format to Salt
 * @author hildebax
 *
 */
@Component(name="TreetaggerImporterComponent", factory="PepperImporterComponentFactory")
public class TreetaggerImporter extends PepperImporterImpl implements PepperImporter
{
	private Properties conversionProperties = null;
	
	/**
	 * Getter for Properties
	 * @return the Properties
	 */
	public Properties getConversionProperties() {
		return this.conversionProperties;
	}

	/**
	 * Setter for Properties
	 * @param properties the Properties
	 */
	public void setConversionProperties(Properties properties) {
		this.conversionProperties = properties;
	}
	//---------------------------------------------------------------------------------------
	public static final String[] TREETAGGER_FILE_ENDINGS={"treetagger", "tab", "tt"};
	
	public TreetaggerImporter()
	{
		super();
		//setting name of module
		this.name= "TreetaggerImporter";
		//set list of formats supported by this module
		this.addSupportedFormat("treetagger", "1.0", null);
			
		this.setProperties(new TreetaggerImporterProperties());
		//adding all file endings to list of endings for documents (necessary for importCorpusStructure)
		for (String ending: TREETAGGER_FILE_ENDINGS)
			this.getSDocumentEndings().add(ending);
	}
	
	/**
	 * Creates a mapper of type {@link PAULA2SaltMapper}.
	 * {@inheritDoc PepperModule#createPepperMapper(SElementId)}
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId)
	{
		Treetagger2SaltMapper mapper= new Treetagger2SaltMapper();
	
		if (sElementId.getSIdentifiableElement() instanceof SDocument)
		{
			URI uri= getSElementId2ResourceTable().get(sElementId);
			Document tDocument = this.loadFromFile(uri);
			if (tDocument==null) {
				mapper= null;
			}
			else {
				mapper.setTTDocument(tDocument);
			}
		
		}
		return(mapper);
	}
	
//		/**
//	 * Stores relation between documents and their resource 
//	 */
//	private Map<SElementId, URI> documentResourceTable= null;
//	
//	@Override
//	public void importCorpusStructure(SCorpusGraph corpusGraph)
//			throws PepperModuleException
//	{
//		this.setSCorpusGraph(corpusGraph);
//		if (this.getSCorpusGraph()== null)
//			throw new PepperModuleException(this.name+": Cannot start with importing corpus, because salt project isn?t set.");
//		
//		if (this.getCorpusDefinition()== null)
//			throw new PepperModuleException(this.name+": Cannot start with importing corpus, because no corpus definition to import is given.");
//		if (this.getCorpusDefinition().getCorpusPath()== null)
//			throw new PepperModuleException(this.name+": Cannot start with importing corpus, because the path of given corpus definition is null.");
//		if (this.getCorpusDefinition().getCorpusPath().isFile())
//		{
//			if (	(this.getCorpusDefinition().getCorpusPath().toFileString().endsWith("/")) || 
//					(this.getCorpusDefinition().getCorpusPath().toFileString().endsWith("\\")))
//			{//clean uri in corpus path (if it is a folder and ends with/, / has to be removed)
//				this.getCorpusDefinition().setCorpusPath(this.getCorpusDefinition().getCorpusPath().trimSegments(1));
//			}//clean uri in corpus path (if it is a folder and ends with/, / has to be removed)
//			
//			try {
//				this.documentResourceTable= this.createCorpusStructure(this.getCorpusDefinition().getCorpusPath(), null, null);
//			} catch (IOException e) {
//				throw new PepperModuleException(this.name+": Cannot start with importing corpus, because some exception occurs: ",e);
//			}
//		}	
//	}
	
	
//	/**
//	 * Starts the conversion of the element corresponding to the ID 
//	 */
//	@Override
//	public void start(SElementId sElementId) throws PepperModuleException 
//	{
//		if (sElementId.getSIdentifiableElement()!= null)
//		{	
//			if (sElementId.getSIdentifiableElement() instanceof SDocument)
//			{	
//				this.returningMode= RETURNING_MODE.PUT;
//				URI uri= this.documentResourceTable.get(sElementId);
//				if (uri== null)
//					throw new TreetaggerImporterException("Cannot import document '"+sElementId+"', because no corresponding uri was found.");
//				
//				
//				if (this.getSpecialParams()!=null) {
//					String propertyFileName = this.getSpecialParams().toFileString();
//					try {
//						this.setConversionProperties(new Properties());
//						this.getConversionProperties().load(new FileInputStream(propertyFileName));
//					} catch (IOException e) {
//						this.getLogService().log(LogService.LOG_WARNING, String.format("couldn´t load properties file '%s'. using default values.",propertyFileName));
//					}
//				}
//
//				Document tDocument = this.loadFromFile(uri);
//				if (tDocument==null) {
//					//TODO: take document out of the process
//				}
//				else {
//					Treetagger2SaltMapper mapper = new Treetagger2SaltMapper();
//					mapper.setProperties(this.getConversionProperties());
//					mapper.setLogService(this.getLogService());
//					mapper.map(tDocument,this.getSCorpusGraph().getSDocument(sElementId));
//				}
//			}
//		}
//	}

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
			TabResourceFactory tabResourceFactory = new TabResourceFactory();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tab",tabResourceFactory);
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tt",tabResourceFactory);
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
				options.put(TabResource.logServiceKey, this.getLogService());
				//put properties for TabResource loading into options
				options.put(TabResource.propertiesKey, this.getConversionProperties());

				resource.load(options);
			} 
			catch (IOException e) 
			{	throw new TreetaggerImporterException("Cannot load resource '"+uri+"'.",e);	}
			catch (NullPointerException e) 
			{	throw new TreetaggerImporterException("Cannot load resource '"+uri+"'.",e);	}
			if (resource.getContents().size()>0) {
				retVal= (Document) resource.getContents().get(0);
			}
		}
		return(retVal);
	}
}
