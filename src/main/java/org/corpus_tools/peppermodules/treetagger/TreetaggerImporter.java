/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
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
package org.corpus_tools.peppermodules.treetagger;

import java.util.List;

import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.impl.PepperImporterImpl;
import org.corpus_tools.pepper.modules.PepperImporter;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.peppermodules.treetagger.mapper.Treetagger2SaltMapper;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.resources.TabReader;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

/**
 * This class imports data from Treetagger format to Salt
 * 
 * @author hildebax
 * @author Florian Zipser
 * 
 */
@Component(name = "TreetaggerImporterComponent", factory = "PepperImporterComponentFactory")
public class TreetaggerImporter extends PepperImporterImpl implements PepperImporter {
	// ---------------------------------------------------------------------------------------
	public static final String[] TREETAGGER_FILE_ENDINGS = { "treetagger", "tab", "tt", "txt" };

	public TreetaggerImporter() {
		super();
		// setting name of module
		setName("TreetaggerImporter");
		setSupplierContact(URI.createURI(PepperConfiguration.EMAIL));
		setSupplierHomepage(URI.createURI("https://github.com/korpling/pepperModules-TreetaggerModules"));
		setDesc("This importer transforms data in TreeTagger format produced by the TreeTagger tool (see http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/) to a Salt model. ");
		// set list of formats supported by this module
		addSupportedFormat("treetagger", "1.0", null);

		setProperties(new TreetaggerImporterProperties());
		// adding all file endings to list of endings for documents (necessary
		// for importCorpusStructure)
		for (String ending : TREETAGGER_FILE_ENDINGS) {
			this.getDocumentEndings().add(ending);
		}
	}

	/**
	 * Creates a mapper of type {@link PAULA2SaltMapper}.
	 * {@inheritDoc PepperModule#createPepperMapper(Identifier)}
	 */
	@Override
	public PepperMapper createPepperMapper(Identifier sElementId) {
		Treetagger2SaltMapper mapper = new Treetagger2SaltMapper();

		if (sElementId.getIdentifiableElement() instanceof SDocument) {
			URI uri = getIdentifier2ResourceTable().get(sElementId);
			Document tDocument = this.loadFromFile(uri);
			if (tDocument == null) {
				mapper = null;
			} else {
				mapper.setTTDocument(tDocument);
			}

		}
		return (mapper);
	}

	private Document loadFromFile(URI uri) {
		Document retVal = null;
		if (uri != null) {

			TabReader reader = new TabReader();
			List<Document> documents = reader.load(uri, getProperties().getProperties());

			if (!documents.isEmpty()) {
				retVal = documents.get(0);
			}

			// // create resource set and resource
			// ResourceSet resourceSet = new ResourceSetImpl();
			//
			// // Register XML resource factory
			// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("treetagger",
			// new XMIResourceFactoryImpl());
			// TabResourceFactory tabResourceFactory = new TabResourceFactory();
			// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tab",
			// tabResourceFactory);
			// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("tt",
			// tabResourceFactory);
			// resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("txt",
			// tabResourceFactory);
			// Resource resource = null;
			// try {
			// // load resource
			// resource = resourceSet.createResource(uri);
			//
			// if (resource == null) {
			// throw new PepperModuleException(this, "Cannot load The resource
			// is null.");
			// }
			// resource.load(getProperties().getProperties());
			// } catch (IOException e) {
			// throw new PepperModuleException(this, "Cannot load resource '" +
			// uri + "'.", e);
			// } catch (NullPointerException e) {
			// throw new PepperModuleException(this, "Cannot load resource '" +
			// uri + "'.", e);
			// }
			// if (resource.getContents().size() > 0) {
			// retVal = (Document) resource.getContents().get(0);
			// }
		}
		return (retVal);
	}
}
