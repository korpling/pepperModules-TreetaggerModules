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

import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import de.hu_berlin.german.korpling.saltnpepper.pepper.exceptions.PepperFWException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperMapper;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.impl.PepperExporterImpl;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper.Salt2TreetaggerMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SElementId;

/**
 * This class exports data from Salt to Treetagger format
 * @author hildebax
 * @author Florian Zipser
 *
 */
@Component(name="TreetaggerExporterComponent", factory="PepperExporterComponentFactory")
public class TreetaggerExporter extends PepperExporterImpl implements PepperExporter
{
	public TreetaggerExporter()
	{
		super();
		//setting name of module
		this.setName("TreetaggerExporter");
		//set list of formats supported by this module
		this.addSupportedFormat("treetagger", "1.0", null);
		this.setProperties(new TreetaggerExporterProperties());
	}
	
	@Override
	public void exportCorpusStructure(SCorpusGraph sCorpusGraph)
	{
		if (sCorpusGraph== null)
			throw new PepperFWException("No SCorpusGraph was passed for exportCorpusStructure(SCorpusGraph corpusGraph). This might be a bug of the pepper framework.");
		else 
		{
			for (SDocument sDocument: sCorpusGraph.getSDocuments())
			{
				String corpusPath = this.getCorpusDesc().getCorpusPath().toFileString();
				String docPath    = sDocument.getSElementPath().toString();
				String docName    = sDocument.getSName()+ "." + ((TreetaggerExporterProperties)getProperties()).getFileEnding();

				File path = null;
				if (((TreetaggerExporterProperties)getProperties()).isFlatten()) { path = new File(corpusPath);           }
				else               { path = new File(corpusPath + docPath); }
				
				path.mkdirs();
				URI uri = URI.createFileURI(path.toString() + '/' + docName);	
				this.getSElementId2ResourceTable().put(sDocument.getSElementId(), uri);
			}
		}
	}
	
	
	/**
	 * Creates a mapper of type {@link PAULA2SaltMapper}.
	 * {@inheritDoc PepperModule#createPepperMapper(SElementId)}
	 */
	@Override
	public PepperMapper createPepperMapper(SElementId sElementId)
	{
		Salt2TreetaggerMapper mapper= new Salt2TreetaggerMapper();
		if (sElementId.getSIdentifiableElement() instanceof SDocument)
			mapper.setResourceURI(getSElementId2ResourceTable().get(sElementId));
		return(mapper);
	}
}
