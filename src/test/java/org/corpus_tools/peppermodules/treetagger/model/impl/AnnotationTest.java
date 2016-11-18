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

import org.corpus_tools.peppermodules.treetagger.model.Annotation;
import org.corpus_tools.peppermodules.treetagger.model.TreetaggerFactory;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class AnnotationTest extends TestCase {

	protected Annotation fixture = null;

	@Override
	@Before
	protected void setUp() throws Exception {
		fixture = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
	}

	/**
	 * Tests equals method for annotations.
	 */
	@Test
	public void testEquals() {
		Annotation anno1 = null;
		Annotation anno2 = null;
		assertEquals(anno1, anno2);

		anno1 = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
		assertNotSame(anno1, anno2);
		anno2 = TreetaggerFactory.eINSTANCE.createAnyAnnotation();
		assertEquals(anno1, anno2);

		assertNull(anno1.getName());
		assertNull(anno2.getName());
		anno1.setName("annotationName");
		assertNotSame(anno1, anno2);
		anno2.setName("annotationName");
		assertEquals(anno1, anno2);

		assertNull(anno1.getValue());
		assertNull(anno2.getValue());
		anno1.setValue("annotationValue");
		assertNotSame(anno1, anno2);
		anno2.setValue("annotationValue");
		assertEquals(anno1, anno2);
	}

} // AnnotationTest
