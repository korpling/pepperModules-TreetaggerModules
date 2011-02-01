package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.xslt;

import java.io.File;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.ExtensionFactoryPair;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperInterfaceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.RETURNING_MODE;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.xsltModules.PepperXSLTExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.xsltModules.impl.PepperXSLTExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

@Component(name="TreetaggerExporterXSLTComponent", factory="PepperExporterComponentFactory")
@Service(value=PepperExporter.class)
public class TreetaggerExporterXSLT extends PepperXSLTExporterImpl implements PepperXSLTExporter
{
	public TreetaggerExporterXSLT()
	{
		super();
		this.name= "TreetaggerExporterXSLT";
		//for testing the symbolic name has to be set without osgi
		if (	(this.getSymbolicName()==  null) ||
				(this.getSymbolicName().equalsIgnoreCase("")))
			this.setSymbolicName("de.hu_berlin.german.korpling.saltnpepper.pepperModules.TreetaggerModules");
		if (this.getLogService()!= null)
			this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is created...");
		this.init();
	}

	protected void init()
	{
		super.init();
		this.supportedFormats= new BasicEList<FormatDefinition>();
		FormatDefinition formatDef= PepperInterfaceFactory.eINSTANCE.createFormatDefinition();
		formatDef.setFormatName("treetagger");
		formatDef.setFormatVersion("1.0");
		this.supportedFormats.add(formatDef);
	}
// ===================== start: bundle-stuff ===================== 
	protected void activate(ComponentContext componentContext) 
	{
		this.setSymbolicName(componentContext.getBundleContext().getBundle().getSymbolicName());
		if (this.getLogService()!= null)
			this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is activated...");
	}

	/**
	 * Wird von der Service Component Runtime vor der Deaktivierung der Komponente
	 * aufgerufen und gibt noch eine Abschiedsbotschaft aus
	 * 
	 * @param componentContext
	 *          Der Kontext der Komponente
	 */
	protected void deactivate(ComponentContext componentContext) {
		if (this.getLogService()!= null)
			this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is deactivated...");

	}
	// ===================== end: bundle-stuff =====================
	
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
					ExtensionFactoryPair pair= PepperInterfaceFactory.eINSTANCE.createExtensionFactoryPair();
					pair.setFileExtension(fileExtension);
					pair.setResourceFactory(new TabResourceFactory());
					this.getXsltTransformer().getExtensionFactoryPairs().add(pair);
					pair= PepperInterfaceFactory.eINSTANCE.createExtensionFactoryPair();
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
