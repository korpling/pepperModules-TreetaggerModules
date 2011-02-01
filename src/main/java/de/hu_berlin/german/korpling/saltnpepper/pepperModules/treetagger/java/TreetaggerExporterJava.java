package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.java;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.LemmaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.POSAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.TreetaggerFactory;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.resources.TabResourceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperConvertException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperExceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperInterfaceFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.modules.SAccessorModule;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltSemantics.modules.STypeOfChecker;

@Component(name="TreetaggerExporterJavaComponent", factory="PepperExporterComponentFactory")
@Service(value=PepperExporter.class)
public class TreetaggerExporterJava extends PepperExporterImpl implements PepperExporter
{
	public TreetaggerExporterJava()
	{
		super();
		this.name= "TreetaggerExporterJava";
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
		this.supportedFormats= new BasicEList<FormatDefinition>();
		FormatDefinition formatDef= PepperInterfaceFactory.eINSTANCE.createFormatDefinition();
		formatDef.setFormatName("treetagger");
		formatDef.setFormatVersion("1.0");
		this.supportedFormats.add(formatDef);
	}
	
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
	protected void deactivate(ComponentContext componentContext) 
	{
		if (this.getLogService()!= null)
			this.getLogService().log(LogService.LOG_DEBUG,this.getName()+" is deactivated...");

	}
	
	/**
	 * current properties
	 */
	private Properties props= null;
	public void setProps(Properties props) {
		this.props = props;
	}

	public Properties getProps() {
		return props;
	}
	
	/**
	 * Keyword for property
	 */
	private String PROP_FILE_EXTENSION= "treetagger.fileExtension";
	/**
	 * Keyword for property
	 */
	private String PROP_EXPORT_ANNOS= "treetagger.exportAnnotations";
	
	/**
	 * Keyword for SALTSEMANTICS.SPOSAnnotation.
	 */
	private String KW_SALTSEMANTICS_SPOS= "SALTSEMANTICS.SPOSAnnotation";
	
	/**
	 * Keyword for SALTSEMANTICS.SLemmaAnnotation.
	 */
	private String KW_SALTSEMANTICS_SLEMMA= "SALTSEMANTICS.SLemmaAnnotation";
	
	/**
	 * Extension for export file. default= tab.
	 */
	private String fileExtension= "tab";
	
	/**
	 * List of annotation names to export. 
	 */
	private EList<String> exportAnnoNames= null;
	
	@Override
	public void start(SElementId sElementId) throws PepperModuleException 
	{
		if (	(sElementId!= null) &&
				(sElementId.getSIdentifiableElement()!= null) &&
				(sElementId.getSIdentifiableElement() instanceof SDocument))
		{	
			{//load props, if exists
				if (this.getSpecialParams()!= null)
				{
					this.setProps(new Properties());
					try {
						this.getProps().load(new InputStreamReader(new FileInputStream(this.getSpecialParams().toFileString())));
					} catch (FileNotFoundException e) {
						
					} catch (IOException e) {
						throw new PepperModuleException("Cannot start converting, because can not read the given file for special parameters: "+ this.getSpecialParams());
					}
				}
			}
			{//create file extension, if it is given by prop
				if (	(this.getProps()!= null) &&
						(this.getProps().getProperty(PROP_FILE_EXTENSION)!= null))
					this.fileExtension= this.getProps().getProperty(PROP_FILE_EXTENSION);
			}
			{//create list of annotations to export
				if (	(this.getProps()!= null) &&
						(this.getProps().getProperty(PROP_EXPORT_ANNOS)!= null))
				{
					this.exportAnnoNames= new BasicEList<String>();
					String[]exportAnnos= this.getProps().getProperty(PROP_EXPORT_ANNOS).split(",");
					for (String exportAnno: exportAnnos)
					{
						this.exportAnnoNames.add(exportAnno.trim());
					}
				}
			}
			this.createFolderStructure(sElementId);
			if (((SDocument)sElementId.getSIdentifiableElement()).getSDocumentGraph()!= null)
			{
				Document tDocument= createDocument(((SDocument)sElementId.getSIdentifiableElement()));
				
				//create uri to save
				URI uri= URI.createFileURI(this.getCorpusDefinition().getCorpusPath().toFileString()+ "/" + sElementId.getSId()+ "/" + tDocument.getName()+ "."+fileExtension);
				try {
					this.saveToFile(uri, tDocument);
				} catch (IOException e) {
					throw new PepperConvertException("Cannot write document with id: '"+sElementId.getSElementPath().lastSegment()+"' into uri: '"+uri+"'.", e);
				}
			}	
		}
	}
	
	private Document createDocument(SDocument sDocument)
	{
		Document tDocument= TreetaggerFactory.eINSTANCE.createDocument();
		tDocument.setName(sDocument.getSName());
		
		//create tokens
		for (SToken sToken: sDocument.getSDocumentGraph().getSTokens())
		{
			Token tToken= this.createToken(sToken, sDocument);
			tDocument.getTokens().add(tToken);
		}
		return(tDocument);
	}
	
	private Token createToken(SToken sToken, SDocument sDocument)
	{
		Token tToken= TreetaggerFactory.eINSTANCE.createToken();
		
		//Accessor-module to have better access to salt. 
		SAccessorModule sAccessor= new SAccessorModule();
		tToken.setText(sAccessor.getOverlappedText(sDocument, sToken));
		
		//adding annotations
		for (SAnnotation sAnno: sToken.getSAnnotations())
		{
			Annotation tAnno= this.createAnnotation(sAnno, sDocument);
			if (tAnno== null);
			else if (tAnno instanceof POSAnnotation)
				tToken.setPosAnnotation((POSAnnotation)tAnno);
			else if (tAnno instanceof LemmaAnnotation)
				tToken.setLemmaAnnotation((LemmaAnnotation)tAnno);
			else tToken.getAnnotations().add(tAnno);
		}	
		return(tToken);
	}
	
	private Annotation createAnnotation(SAnnotation sAnno, SDocument sDocument)
	{
		Annotation tAnnotation= null;
		
		if (this.exportAnnoNames!= null)
		{//check if only special annotations shall be exported
			if (exportAnnoNames.contains(KW_SALTSEMANTICS_SPOS))
			{
				if (STypeOfChecker.isOfTypeSPOSAnnotation(sAnno))
				{
					tAnnotation= TreetaggerFactory.eINSTANCE.createPOSAnnotation();
					if (sAnno.getSValue()!= null)
						tAnnotation.setValue(sAnno.getSValue().toString());
				}
			}
			if (exportAnnoNames.contains(KW_SALTSEMANTICS_SLEMMA))
			{
				if (STypeOfChecker.isOfTypeSLemmaAnnotation(sAnno))
				{
					tAnnotation= TreetaggerFactory.eINSTANCE.createLemmaAnnotation();
					if (sAnno.getSValue()!= null)
						tAnnotation.setValue(sAnno.getSValue().toString());
				}
			}
			if (	(sAnno.getSName() != null) &&
					(exportAnnoNames.contains(sAnno.getSName())))
			{	
				tAnnotation= TreetaggerFactory.eINSTANCE.createLemmaAnnotation();
				if (sAnno.getSValue()!= null)
					tAnnotation.setValue(sAnno.getSValue().toString());
			}
				
		}
		else
		{//export everything
			tAnnotation= TreetaggerFactory.eINSTANCE.createAnnotation();
			if (sAnno.getSValue()!= null)
				tAnnotation.setValue(sAnno.getSValue().toString());
		}
		
//		//TODO changing for ISOCat
//		// if annotation is pos annotation
//		if (sAnno.getSName().equalsIgnoreCase("pos"))
//		{
//			tAnnotation= TreetaggerFactory.eINSTANCE.createPOSAnnotation();
//			if (sAnno.getSValue()!= null)
//				tAnnotation.setValue(sAnno.getSValue().toString());
//		}	
//		// if annotation is lemma annotation
//		else if (sAnno.getSName().equalsIgnoreCase("lemma"))
//		{
//			tAnnotation= TreetaggerFactory.eINSTANCE.createLemmaAnnotation();
//			if (sAnno.getSValue()!= null)	
//				tAnnotation.setValue(sAnno.getSValue().toString());
//		}	
//		// if annotation is any annotation
//		else 
//		{
//			tAnnotation= TreetaggerFactory.eINSTANCE.createAnnotation();
//			if (sAnno.getSValue()!= null)
//				tAnnotation.setValue(sAnno.getSValue().toString());
//		}	
//		if (sAnno.getSValue()!= null)
//			System.out.println(sAnno.getSName()+ " has a null value");
		
		return(tAnnotation);
	}
	
	private void saveToFile(URI uri, Document tDocument) throws IOException
	{
		// create resource set and resource 
		ResourceSet resourceSet = new ResourceSetImpl();

		// Register XML resource factory
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("treetagger",new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tab",new TabResourceFactory());
		//load resource 
		Resource resource = resourceSet.createResource(uri);
		
		if (resource== null)
			throw new PepperConvertException("Cannot load treetagger file, the resource '"+uri+"'is null.");
		
		resource.getContents().add(tDocument);
		
		resource.save(null);
	}
}
