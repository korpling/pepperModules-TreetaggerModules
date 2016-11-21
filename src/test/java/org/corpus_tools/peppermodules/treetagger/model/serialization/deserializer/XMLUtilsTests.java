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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.corpus_tools.peppermodules.treetagger.model.serialization.deserializer.XMLUtils;

import junit.framework.TestCase;

/**
 * Test case for the XMLUtils
 * @author hildebax
 *
 */
public class XMLUtilsTests extends TestCase {

	public XMLUtilsTests(String name) {
		super(name);
	}

	/**
	 * tests whether expressions are correctly recognised as <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#sec-starttags">start tag expressions</a>
	 */
	public final void testIsStartTag() {
		String[] valids = {
				"<Test>",
				"<TAG test=''>",
				"<TAG test='testVal'>",
				"<TAG test='test:test'>",
				"<TAG test=\"test\">",
		};
		
		String[] invalids = {
				"<>",
				"<TAG test='test\">",
				"<TAG test=\"test'>",
				"</TAG test='testVal'>",
				"TAG test='testVal'"
				
		};
		
		for (String valid:valids) {
			if (!XMLUtils.isStartTag(valid)) {
				fail();
			}
		};
		
		for (String invalid:invalids) {
			if(XMLUtils.isStartTag(invalid)) {
				fail();
			}
		};

	}

	/**
	 * tests whether expressions are correctly recognised as <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-S">white space expression</a>
	 */
	public final void testIsWhiteSpace() {
		Character x09 = new Character((char)0x09);
		Character x0A = new Character((char)0x0A);
		Character x0D = new Character((char)0x0D);
		Character x20 = new Character((char)0x20);
		
		String[] valids = {
				x09.toString(),
				x0A.toString(),
				x0D.toString(),
				x20.toString(),
				String.format("%c%c", x09, x0A),
				String.format("%c%c", x20, x09),
				String.format("%c%c", x0D, x20),
				String.format("%c%c%c%c", x09, x20, x09, x0A),
				String.format("%c%c%c%c", x0A, x0D, x20, x09),
				String.format("%c%c%c%c", x09, x09, x0D, x20)
		};
		
		String[] invalids = {
				"",
				"a",
				"_",
				"ten",
				".",
				"?"
		};
		
		for (String valid:valids) {
			if (!XMLUtils.isWhiteSpace(valid)) {
				fail();
			}
		};
		
		for (String invalid:invalids) {
			if(XMLUtils.isWhiteSpace(invalid)) {
				fail();
			}
		};
	}

	
	
	/**
	 * tests whether expressions are correctly recognised as <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-Eq">eq expressions</a> 
	 */
	public final void testIsEq() {
		Character x09 = new Character((char)0x09);
		Character x0A = new Character((char)0x0A);
		Character x0D = new Character((char)0x0D);
		Character x20 = new Character((char)0x20);

		String[] valids = {
				"=",
				String.format("%c%c=", x09, x0A),
				String.format("%c=%c", x20, x09),
				String.format("=%c%c", x0D, x20),
				String.format("%c=%c%c%c", x09, x20, x09, x0A),
				String.format("%c%c%c=%c", x0A, x0D, x20, x09),
				String.format("=%c%c%c%c", x09, x09, x0D, x20)
		};
		
		String[] invalids = {
				" ",
				" ",
				"_",
				"t",
				".",
				"?"
		};
		
		for (String valid:valids) {
			if (!XMLUtils.isEq(valid)) {
				fail();
			}
		};
		
		for (String invalid:invalids) {
			if(XMLUtils.isEq(invalid)) {
				fail();
			}
		};
	}
	
	
	/**
	 * tests whether names are correctly extracted and returned from start tags 
	 */
	public final void testGetName() {
		assertEquals("TAG", XMLUtils.getName("<TAG>"));
		assertEquals("TAG", XMLUtils.getName("<TAG >"));
		assertEquals("TAG", XMLUtils.getName("<TAG att='test'>"));
	
		assertNotSame("TAG", XMLUtils.getName("TAG att='test'>"));
	}

	/**
	 * tests whether lists of attribute-value-pairs (implemented as <a href="http://download.oracle.com/javase/6/docs/api/java/util/1AbstractMap.SimpleEntry.html">SimpleEntry</a> (String,String) of start tags are returned correctly.
	 */
	public final void testGetAttributeValueList() {
		ArrayList<SimpleEntry<String, String>> list = XMLUtils.getAttributeValueList("<TAG test='testVal' test2='test2Val' test3=\"test3Val\"    test4='test4Val'");
		for (int i=0;i<list.size();i++) {
			SimpleEntry<String,String> entry = list.get(i);
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}

	/**
	 * tests whether hashtables (String,String) of attribute-value-pairs of start tags are returned correctly.
	 */
	public final void testGetAttributeValueTable() {
		Hashtable<String, String> table = XMLUtils.getAttributeValueTable("<TAG test='testVal' test2='test2Val' test3=\"test3Val\"    test4='test4Val'");
		for (Entry<String, String> entry : table.entrySet()) {
  		  System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
	}
	
	
	
}
