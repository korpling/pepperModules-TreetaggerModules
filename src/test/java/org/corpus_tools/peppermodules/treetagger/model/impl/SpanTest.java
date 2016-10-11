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

import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.junit.Test;

public class SpanTest {
	/**
	 * Tests equals method for Span.
	 */
	@Test
	public void testEquals() {
		Span span1 = null;
		Span span2 = null;
		assertEquals(span1, span2);

		span1 = TreetaggerFactory.eINSTANCE.createSpan();
		assertNotSame(span1, span2);
		span2 = TreetaggerFactory.eINSTANCE.createSpan();
		assertEquals(span1, span2);

		assertNull(span1.getName());
		assertNull(span2.getName());
		span1.setName("spanName");
		assertNotSame(span1, span2);
		span2.setName("spanName");
		assertEquals(span1, span2);
	}

} // SpanTest
