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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TreetaggerImporterPropertiesTest {

	TreetaggerImporterProperties fixture = null;

	@Before
	public void setUp() throws Exception {
		fixture = new TreetaggerImporterProperties();
	}

	@Test
	public void whenGetDefaultColumnNames_thenReturnTokPosLemma() {
		assertThat(fixture.getColumnNames()).containsExactly("tok", "pos", "lemma");
	}

	@Test
	public void whenGetCustomColumnNames_thenReturnThemAsList() {
		fixture.setPropertyValue(TreetaggerImporterProperties.PROP_COLUMN_NAMES, "tok, col1");
		assertThat(fixture.getColumnNames()).containsExactly("tok", "col1");
	}

	@Test
	public void whenGetCustomColumnNamesWithWhitespaceCharacter_thenReturnThemAsList() {
		fixture.setPropertyValue(TreetaggerImporterProperties.PROP_COLUMN_NAMES, "		 tok  , col1	");
		assertThat(fixture.getColumnNames()).containsExactly("tok", "col1");
	}

	@Test
	public void whenGetCustomColumnNamesWithoutTok_thenReturnListWithTok() {
		fixture.setPropertyValue(TreetaggerImporterProperties.PROP_COLUMN_NAMES, "col1, col2, col3");
		assertThat(fixture.getColumnNames()).containsExactly("tok", "col1", "col2", "col3");
	}
}
