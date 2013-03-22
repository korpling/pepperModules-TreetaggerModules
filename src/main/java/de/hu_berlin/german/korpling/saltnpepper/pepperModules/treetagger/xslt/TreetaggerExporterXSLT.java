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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.xslt;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.ExtensionFactoryPair;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModulesFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.RETURNING_MODE;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.xsltModules.PepperXSLTExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.xsltModules.impl.PepperXSLTExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

@Component(name="TreetaggerExporterXSLTComponent", factory="PepperExporterComponentFactory")
public class TreetaggerExporterXSLT extends PepperXSLTExporterImpl implements PepperXSLTExporter
{
	public TreetaggerExporterXSLT()
	{
		super();
		//setting name of module
		this.name= "TreetaggerExporterXSLT";
		//set list of formats supported by this module
		this.addSupportedFormat("treetagger", "1.0", null);
	}

	/**
	 * file extionsion in which format document shall be saved.
	 * 
	 */
	//TODO Can be set by prop file
	private String fileExtension= "tab";
	
	@Override
	public void start(SElementId sElementId) throws PepperModuleException 
	{
		if (	(sElementId!= null) &&
				(sElementId.getSIdentifiableElement()!= null) &&
				(sElementId.getSIdentifiableElement() instanceof SDocument))
		{	
			this.createFolderStructure(sElementId);
			
			SDocumentGraph sDocGraph= ((SDocument)sElementId.getSIdentifiableElement()).getSDocumentGraph();
			if (sDocGraph!= null)
			{	
				if (this.getTemproraries()== null)
					throw new PepperModuleException("Cannot start module '"+this.getName()+"', because the temproraries aren�t set.");
				if (this.getResources()== null)
					throw new PepperModuleException("Cannot start module '"+this.getName()+"', because the resource path aren�t set.");
				this.returningMode= RETURNING_MODE.PUT;
				
				URI tmpFolderURI= URI.createFileURI(this.getTemproraries().toFileString()+"/" +sElementId.getSElementPath());
				File tmpFolder= new File(tmpFolderURI.toFileString());
				if (!tmpFolder.exists())
					tmpFolder.mkdirs();
				File tmpFile= new File(tmpFolder.getAbsolutePath()+"/"+sElementId.getSElementPath()+".xmi");
				URI tmpModelURI= URI.createFileURI(tmpFile.getAbsolutePath());
				File targetFolder= new File(this.getCorpusDefinition().getCorpusPath().toFileString()+"/"+sElementId.getSElementPath()); 
				URI targetURI= URI.createFileURI(targetFolder.getAbsolutePath()+"/"+sElementId.getSElementPath().lastSegment()+"."+fileExtension);
				
				URI xsltURI= URI.createFileURI(this.getResources().toFileString() + "/salt2treetagger_new.xslt");		
				
				{//adding resource factories
					ExtensionFactoryPair pair= PepperModulesFactory.eINSTANCE.createExtensionFactoryPair();
					pair.setFileExtension(fileExtension);
					pair.setResourceFactory(new TabResourceFactory());
					this.getXsltTransformer().getExtensionFactoryPairs().add(pair);
					pair= PepperModulesFactory.eINSTANCE.createExtensionFactoryPair();
					pair.setFileExtension(Resource.Factory.Registry.DEFAULT_EXTENSION);
					pair.setResourceFactory(new XMIResourceFactoryImpl());
					this.getXsltTransformer().getExtensionFactoryPairs().add(pair);
				}
				
//				this.getXsltTransformer().save(this.getSaltProject(), tmpCorpusModelURI);
				//workaraund, to store just a single document, without other resources
				SDocument sDocument= sDocGraph.getSDocument();
				sDocGraph.setSDocument(null);
				
				this.getXsltTransformer().save(sDocGraph, tmpModelURI);
				this.getXsltTransformer().transform(tmpModelURI, targetURI, xsltURI);
				sDocGraph.setSDocument(sDocument);
			}
		}
	}
}
