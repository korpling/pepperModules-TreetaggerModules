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
package org.corpus_tools.peppermodules.treetagger.model.impl;

import static org.junit.Assert.assertEquals;

import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.junit.Test;

public class DocumentTest {

	/**
	 * Tests equals method for Documents. The name field is not subject to
	 * equality.
	 */
	@Test
	public void testEquals() {
		Document document1 = TreetaggerFactory.eINSTANCE.createDocument();
		Document document2 = TreetaggerFactory.eINSTANCE.createDocument();
		assertEquals(document1, document2);

		Document[] docArray = { document1, document2 };

		for (int docIndex = 0; docIndex < 2; docIndex++) {
			// add some tokens to document and some annotations to tokens
			for (int i = 0; i < 10; i++) {
				Token token = TreetaggerFactory.eINSTANCE.createToken();
				token.setText(String.format("token#%d.Text", i));
				token.setDocument(docArray[docIndex]);
				for (int j = 0; j < 2; j++) {
					Annotation annotation = TreetaggerFactory.eINSTANCE.createAnnotation();
					annotation.setValue(String.format("token#%d.Annotation#%d", i, j));
					annotation.setAnnotatableElement(token);
				}
			}
		}

		assertEquals(document1, document2);
	}

} // DocumentTest
