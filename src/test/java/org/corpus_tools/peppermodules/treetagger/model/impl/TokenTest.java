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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.corpus_tools.peppermodules.treetagger.model.LemmaAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.POSAnnotation;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.junit.Before;
import org.junit.Test;

public class TokenTest {
	private Token fixture;

	@Before
	public void beforeEach() {
		fixture = TreetaggerFactory.eINSTANCE.createToken();
	}

	@Test
	public void testGetPosAnnotation() {
		assertNull(fixture.getPosAnnotation());
		POSAnnotation anno = TreetaggerFactory.eINSTANCE.createPOSAnnotation();
		anno.setName("pos");
		anno.setValue("VVFIN");
		fixture.setPosAnnotation(anno);
		assertEquals(anno, fixture.getPosAnnotation());
	}

	@Test
	public void testGetLemmaAnnotation() {
		assertNull(fixture.getLemmaAnnotation());
		LemmaAnnotation anno = TreetaggerFactory.eINSTANCE.createLemmaAnnotation();
		anno.setName("lemma");
		anno.setValue("someLemma");
		fixture.setLemmaAnnotation(anno);
		assertEquals(anno, fixture.getLemmaAnnotation());
	}

	@Test
	public void testSetLemmaAnnotation() {
		this.testGetLemmaAnnotation();
	}

	@Test
	public void testEquals() {
		Token token1 = null;
		Token token2 = null;
		assertEquals(token1, token2);

		token1 = TreetaggerFactory.eINSTANCE.createToken();
		assertNotSame(token1, token2);
		token2 = TreetaggerFactory.eINSTANCE.createToken();
		assertEquals(token1, token2);

		assertNull(token1.getText());
		assertNull(token2.getText());
		token1.setText("tokenText");
		assertNotSame(token1, token2);
		token2.setText("tokenText");
		assertEquals(token1, token2);
	}
}
