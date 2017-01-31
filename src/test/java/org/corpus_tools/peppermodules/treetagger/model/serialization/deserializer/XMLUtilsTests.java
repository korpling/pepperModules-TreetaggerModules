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
package org.corpus_tools.peppermodules.treetagger.model.serialization.deserializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;

import org.junit.Test;

public class XMLUtilsTests {

	/**
	 * tests whether expressions are correctly recognised as
	 * <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#sec-starttags">start
	 * tag expressions</a>
	 */
	@Test
	public final void testIsStartTag() {
		String[] valids = { "<Test>", "<TAG test=''>", "<TAG test='testVal'>", "<TAG test='test:test'>",
				"<TAG test=\"test\">", };

		String[] invalids = { "<>", "<TAG test='test\">", "<TAG test=\"test'>", "</TAG test='testVal'>",
				"TAG test='testVal'"

		};

		for (String valid : valids) {
			if (!XMLUtils.isStartTag(valid)) {
				fail("");
			}
		}
		;

		for (String invalid : invalids) {
			if (XMLUtils.isStartTag(invalid)) {
				fail("");
			}
		}
		;

	}

	/**
	 * tests whether expressions are correctly recognised as
	 * <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-S">white space
	 * expression</a>
	 */
	@Test
	public final void testIsWhiteSpace() {
		Character x09 = new Character((char) 0x09);
		Character x0A = new Character((char) 0x0A);
		Character x0D = new Character((char) 0x0D);
		Character x20 = new Character((char) 0x20);

		String[] valids = { x09.toString(), x0A.toString(), x0D.toString(), x20.toString(),
				String.format("%c%c", x09, x0A), String.format("%c%c", x20, x09), String.format("%c%c", x0D, x20),
				String.format("%c%c%c%c", x09, x20, x09, x0A), String.format("%c%c%c%c", x0A, x0D, x20, x09),
				String.format("%c%c%c%c", x09, x09, x0D, x20) };

		String[] invalids = { "", "a", "_", "ten", ".", "?" };

		for (String valid : valids) {
			if (!XMLUtils.isWhiteSpace(valid)) {
				fail("");
			}
		}
		;

		for (String invalid : invalids) {
			if (XMLUtils.isWhiteSpace(invalid)) {
				fail("");
			}
		}
		;
	}

	/**
	 * tests whether expressions are correctly recognised as
	 * <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-Eq">eq
	 * expressions</a>
	 */
	@Test
	public final void testIsEq() {
		Character x09 = new Character((char) 0x09);
		Character x0A = new Character((char) 0x0A);
		Character x0D = new Character((char) 0x0D);
		Character x20 = new Character((char) 0x20);

		String[] valids = { "=", String.format("%c%c=", x09, x0A), String.format("%c=%c", x20, x09),
				String.format("=%c%c", x0D, x20), String.format("%c=%c%c%c", x09, x20, x09, x0A),
				String.format("%c%c%c=%c", x0A, x0D, x20, x09), String.format("=%c%c%c%c", x09, x09, x0D, x20) };

		String[] invalids = { " ", " ", "_", "t", ".", "?" };

		for (String valid : valids) {
			if (!XMLUtils.isEq(valid)) {
				fail("");
			}
		}
		;

		for (String invalid : invalids) {
			if (XMLUtils.isEq(invalid)) {
				fail("");
			}
		}
		;
	}

	/**
	 * tests whether names are correctly extracted and returned from start tags
	 */
	@Test
	public final void testGetName() {
		assertThat(XMLUtils.extractTagName("<TAG>")).isEqualTo("TAG");
		assertThat(XMLUtils.extractTagName("<TAG >")).isEqualTo("TAG");
		assertThat(XMLUtils.extractTagName("<TAG att='test'>")).isEqualTo("TAG");
	}

	/**
	 * tests whether lists of attribute-value-pairs (implemented as <a href=
	 * "http://download.oracle.com/javase/6/docs/api/java/util/1AbstractMap.SimpleEntry.html">SimpleEntry</a>
	 * (String,String) of start tags are returned correctly.
	 */
	@Test
	public final void testGetAttributeValueList() {
		final Map<String, String> attributes = XMLUtils.extractAttributeValuePairs(
				"<TAG test='testVal' test2='test2Val' test3=\"test3Val\"    test4='test4Val'");
		assertThat(attributes.get("test")).isEqualTo("testVal");
		assertThat(attributes.get("test2")).isEqualTo("test2Val");
		assertThat(attributes.get("test3")).isEqualTo("test3Val");
		assertThat(attributes.get("test4")).isEqualTo("test4Val");
	}
}
