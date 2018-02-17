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
import java.util.regex.Pattern;

import org.corpus_tools.pepper.common.PepperConfiguration;
import org.corpus_tools.pepper.core.SelfTestDesc;
import org.corpus_tools.pepper.impl.PepperImporterImpl;
import org.corpus_tools.pepper.modules.PepperImporter;
import org.corpus_tools.pepper.modules.PepperMapper;
import org.corpus_tools.peppermodules.treetagger.mapper.Treetagger2SaltMapper;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.impl.Treetagger;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.graph.Identifier;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import com.google.common.base.Strings;

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
	public static final String[] TREETAGGER_FILE_ENDINGS = { "treetagger", "tab", "tt", "txt", "xml" };
	private static final Pattern TREETAGGER_MATCH_PATTERN = Pattern.compile("[a-zA-Z0-9]+(\t[a-zA-Z0-9]+)*");

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

	@Override
	public Double isImportable(URI corpusPath) {
		Double retValue = 0.0;
		for (String content : sampleFileContent(corpusPath, TREETAGGER_FILE_ENDINGS)) {
			if (Strings.isNullOrEmpty(content)) {
				continue;
			}
			if (TREETAGGER_MATCH_PATTERN.matcher(content).find()) {
				retValue = 1.0;
				break;
			}
		}
		return retValue;
	}

	@Override
	public SelfTestDesc getSelfTestDesc() {
		return new SelfTestDesc(
				getResources().appendSegment("selfTests").appendSegment("treetaggerImporter").appendSegment("in"),
				getResources().appendSegment("selfTests").appendSegment("treetaggerImporter")
						.appendSegment("expected"));
	}

	/**
	 * Creates a mapper of type {@link PAULA2SaltMapper}.
	 * {@inheritDoc PepperModule#createPepperMapper(Identifier)}
	 */
	@Override
	public PepperMapper createPepperMapper(Identifier identifier) {
		Treetagger2SaltMapper mapper = new Treetagger2SaltMapper();
		if (identifier.getIdentifiableElement() instanceof SDocument) {
			URI uri = getIdentifier2ResourceTable().get(identifier);
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
		if (uri == null) {
			return null;
		}
		final List<String> columnNames = ((TreetaggerImporterProperties) getProperties()).getColumnNames();
		final String metaTag = getProperties().getProperty(TreetaggerImporterProperties.PROP_META_TAG).getValue()
				.toString();
		final String fileEncoding = getProperties().getProperty(TreetaggerImporterProperties.PROP_FILE_ENCODING)
				.getValue().toString();
		final List<Document> documents = Treetagger.deserialize().withFileEncoding(fileEncoding)
				.withMetaTagName(metaTag).withColumnNames(columnNames).from(uri);

		if (documents.isEmpty()) {
			return null;
		}
		return (documents.get(0));
	}
}
