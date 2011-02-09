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

import java.io.IOException;
import java.util.Map;

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

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperImporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperInterfaceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.RETURNING_MODE;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperImporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.exceptions.TreetaggerImporterException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltCommonFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.SaltSemanticsFactory;

@Component(name="TreetaggerImporterJavaComponent", factory="PepperImporterComponentFactory")
@Service(value=PepperImporter.class)
public class TreetaggerImporterJava extends PepperImporterImpl implements PepperImporter
{
	//TODO there shall be a possibility to read from Property-file
	/**
	 * Specifies the seperator, which has to be set between to the texts of two token.
	 */
	private String sepreator= " ";
	
	public TreetaggerImporterJava()
	{
		super();
		this.name= "TreetaggerImporterJava";
		//for testing the symbolic name has to be set without osgi
		if (	(this.getSymbolicName()==  null) ||
				(this.getSymbolicName().equalsIgnoreCase("")))
			this.setSymbolicName("de.hu_berlin.german.korpling.saltnpepper.pepperModules.TreetaggerModules");
		this.init();
		if (this.getLogService()!= null)
			this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is created...");
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
				throw new PepperModuleException(this.name+": Cannot start with importing corpus, because saome exception occurs: ",e);
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
				
				Document tDocument= null;
				tDocument= this.loadFromFile(uri);
				this.createSDocument(tDocument, this.getSCorpusGraph().getSDocument(sElementId));
			}
		}
	}

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
			
				resource.load(null);
			} 
			catch (IOException e) 
			{	throw new TreetaggerImporterException("Cannot load resource '"+uri+"'.");	}
			catch (NullPointerException e) 
			{	throw new TreetaggerImporterException("Cannot load resource '"+uri+"'.");	}
			
			retVal= (Document) resource.getContents().get(0);
		}
		return(retVal);
	}
	
// ============================ start Mapping ============================
	//TODO put all mappings in other class
	private void createSDocument(Document tDocument, SDocument sDocument)
	{
		sDocument.setSDocumentGraph(SaltCommonFactory.eINSTANCE.createSDocumentGraph());
		sDocument.getSDocumentGraph().setSName(tDocument.getName()+"_graph");
//		STextualDS sText= 
		this.createSTextualDS(tDocument.getTokens(), sDocument);
//		sDocument.getSDocumentGraph().addSNode(sText);	
	}
	
	private STextualDS createSTextualDS(EList<Token> tTokens, SDocument sDocument)
	{
		//creating and adding STextualDS
		STextualDS sText= SaltCommonFactory.eINSTANCE.createSTextualDS();
		sDocument.getSDocumentGraph().addSNode(sText);
		
		String text= null;
		int start= 0;
		int end= 0; 
		for (Token tToken: tTokens)
		{
			if (text== null)
			{
				start= 0;
				end= tToken.getText().length();
				text= tToken.getText();
			}
			else 
			{
				start= text.length() + sepreator.length();
				end= start + tToken.getText().length();				
				text= text+ this.sepreator + tToken.getText();
			}
			//creating and adding token
			SToken sToken= this.createSToken(tToken);
			sDocument.getSDocumentGraph().addSNode(sToken);
			
			STextualRelation sTextRel= this.createSTextualRelation(sToken, sText, start, end);
			sDocument.getSDocumentGraph().addSRelation(sTextRel);
		}	
		sText.setSText(text);
		return(sText);
	}
	
	
	private SToken createSToken(Token tToken)
	{
		SToken retVal= null;
		retVal= SaltCommonFactory.eINSTANCE.createSToken();
		for (Annotation tAnnotation: tToken.getAnnotations())
		{
			retVal.addSAnnotation(this.createSAnnotation(tAnnotation));
		}	
		return(retVal);
	}
	
	private SAnnotation createSAnnotation(Annotation tAnnotation)
	{
		SAnnotation retVal= null;
		if (tAnnotation instanceof POSAnnotation)
			retVal= SaltSemanticsFactory.eINSTANCE.createSPOSAnnotation();
		else if (tAnnotation instanceof LemmaAnnotation)
			retVal= SaltSemanticsFactory.eINSTANCE.createSLemmaAnnotation();
		else 
		{
			retVal= SaltCommonFactory.eINSTANCE.createSAnnotation();
			retVal.setSName(tAnnotation.getName());
		}
		retVal.setSValue(tAnnotation.getValue());
		return(retVal);
	}
	
	private STextualRelation createSTextualRelation(SToken sToken, STextualDS sText, int start, int end)
	{
		STextualRelation retVal= null;
		retVal= SaltCommonFactory.eINSTANCE.createSTextualRelation();
		retVal.setSTextualDS(sText);
		retVal.setSToken(sToken);
		retVal.setSStart(start);
		retVal.setSEnd(end);
		return(retVal);
	}
// ============================ end Mapping ============================
	
	protected void activate(ComponentContext componentContext) 
	{
		this.setSymbolicName(componentContext.getBundleContext().getBundle().getSymbolicName());
	}

	protected void deactivate(ComponentContext componentContext) {
		if (this.getLogService()!= null)
			this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is deactivated...");
	}
}
