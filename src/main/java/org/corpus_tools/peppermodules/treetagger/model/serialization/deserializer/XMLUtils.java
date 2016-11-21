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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for XML processing
 * 
 * @author hildebax
 * @version 1.0.0
 */
public class XMLUtils {

	private static final Character x09 = new Character((char) 0x09); // horizontal
																		// tab
	private static final Character x0A = new Character((char) 0x0A); // line
																		// feed
	private static final Character x0D = new Character((char) 0x0D); // carriage
																		// return
	private static final Character x20 = new Character((char) 0x20); // space
	private static final Character x22 = new Character((char) 0x22); // double
																		// quotation
	private static final Character x27 = new Character((char) 0x27); // single
																		// quotation
	private static final Character xB7 = new Character((char) 0xB7); // middle
																		// dot
	private static final Character xC0 = new Character((char) 0xC0); // À
	private static final Character xD6 = new Character((char) 0xD6); // Ö
	private static final Character xD8 = new Character((char) 0xD8); //
	private static final Character xF6 = new Character((char) 0xF6);
	private static final Character xF8 = new Character((char) 0xF8);
	private static final Character x02FF = new Character((char) 0x02FF);
	private static final Character x0300 = new Character((char) 0x0300);
	private static final Character x036F = new Character((char) 0x036F);
	private static final Character x0370 = new Character((char) 0x0370);
	private static final Character x037D = new Character((char) 0x037D);
	private static final Character x037F = new Character((char) 0x037F);
	private static final Character x1FFF = new Character((char) 0x1FFF);
	private static final Character x200C = new Character((char) 0x200C);
	private static final Character x200D = new Character((char) 0x200D);
	private static final Character x203F = new Character((char) 0x203F);
	private static final Character x2040 = new Character((char) 0x2040);
	private static final Character x2070 = new Character((char) 0x2070);
	private static final Character x218F = new Character((char) 0x218F);
	private static final Character x2C00 = new Character((char) 0x2C00);
	private static final Character x2FEF = new Character((char) 0x2FEF);
	private static final Character x3001 = new Character((char) 0x3001);
	private static final Character xD7FF = new Character((char) 0xD7FF);
	private static final Character xE000 = new Character((char) 0xE000);
	private static final Character xF900 = new Character((char) 0xF900);
	private static final Character xFDCF = new Character((char) 0xFDCF);
	private static final Character xFDF0 = new Character((char) 0xFDF0);
	private static final Character xFFFD = new Character((char) 0xFFFD);

	private static final String s = String.format("([%c%c%c%c]+)", x09, x0A, x0D, x20);
	private static final String eq = "(" + s + "?=" + s + "?" + ")";
	private static final String namestartchars = String.format(":A-Z_a-z%c-%c%c-%c%c-%c%c-%c%c-%c%c-%c%c-%c%c-%c%c-%c%c-%c%c-%c", xC0, xD6, xD8, xF6, xF8, x02FF, x0370, x037D, x037F, x1FFF, x200C, x200D, x2070, x218F, x2C00, x2FEF, x3001, xD7FF, xF900, xFDCF, xFDF0, xFFFD);
	private static final String namechars = namestartchars + String.format("-\\.0-9%c%c-%c%c-%c", xB7, x0300, x036F, x203F, x2040);
	private static final String namestartchar = "([" + namestartchars + "])";
	private static final String namechar = "([" + namechars + "])";
	private static final String name = "(" + namestartchar + namechar + "*)";
	private static final String normalChar = String.format("[%c%c%c%c-%c%c-%c]", x09, x0A, x0D, x20, xD7FF, xE000, xFFFD);
	private static final String entityref = "(&" + name + ";)";
	private static final String charref = "((&#[0-9]+;)|(&#x[0-9a-fA-F]+;))";
	private static final String reference = "(" + entityref + "|" + charref + ")";
	private static final String attvalueSingleQuote = String.format("(%c([^<&%c]|%s)*%c)", x27, x27, reference, x27);
	private static final String attvalueDoubleQuote = String.format("(%c([^<&%c]|%s)*%c)", x22, x22, reference, x22);
	private static final String attvalue = String.format("(%s|%s)", attvalueSingleQuote, attvalueDoubleQuote);
	private static final String attribute = "(" + name + eq + attvalue + ")";

	public static final String startTag = "<" + name + "(" + s + attribute + ")*" + s + "?>\t*";
	public static final String endTag = "</" + name + s + "?>\t*";

	// TODO: this is just an approximation to the processing instruction syntax,
	// could be made more precise
	private static final String piTarget = name;
	private static final String processingInstructionTag = "<\\?" + piTarget + "(" + s + normalChar + "*" + ")?" + "\\?>";

	private static final Pattern sPattern = Pattern.compile(s);
	private static final Pattern eqPattern = Pattern.compile(eq);
	private static final Pattern namePattern = Pattern.compile(name);
	private static final Pattern attributePattern = Pattern.compile(attribute);
	private static final Pattern attvaluePattern = Pattern.compile(attvalue);
	private static final Pattern startTagPattern = Pattern.compile(startTag);
	private static final Pattern endTagPattern = Pattern.compile(endTag);
	private static final Pattern processingInstructionTagPattern = Pattern.compile(processingInstructionTag);

	/**
	 * Returns true if input is a <a
	 * href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-S">white space
	 * expression</a> according to XML specification.
	 * 
	 * @param input
	 *            the expression
	 * @return true or false
	 */
	public static final boolean isWhiteSpace(String input) {
		return sPattern.matcher(input).matches();
	}

	/**
	 * Returns true if input is an <a
	 * href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-Eq">eq
	 * expression</a> according to XML specification.
	 * 
	 * @param input
	 *            the expression
	 * @return true or false
	 */
	public static final boolean isEq(String input) {
		return eqPattern.matcher(input).matches();
	}

	/**
	 * Returns true if input is a <a
	 * href="http://www.w3.org/TR/2008/REC-xml-20081126/#sec-starttags">start
	 * tag expression</a> according to XML specification.
	 * 
	 * @param input
	 *            the expression
	 * @return true or false
	 */
	public static final boolean isStartTag(String input) {
		return startTagPattern.matcher(input).matches();
	}

	/**
	 * Returns true if input is an <a
	 * href="http://www.w3.org/TR/2008/REC-xml-20081126/#sec-starttags">end tag
	 * expression</a> according to XML specification.
	 * 
	 * @param input
	 *            the expression
	 * @return true or false
	 */
	public static final boolean isEndTag(String input) {
		return endTagPattern.matcher(input).matches();
	}

	/**
	 * Returns true if input is a processing instruction expression.
	 * 
	 * @param input
	 *            the expression
	 * @return true or false
	 */
	public static final boolean isProcessingInstructionTag(String input) {
		return processingInstructionTagPattern.matcher(input).matches();
	}

	/**
	 * Returns the first match of a <a
	 * href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-Name">name</a> in
	 * the input according to the XML specifications. If there is no match,
	 * <code>null</code> is returned.
	 * 
	 * @param input
	 *            the expression containing the name
	 * @return the name or <code>null</code>
	 */
	public static final String getName(String input) {
		Matcher matcher = namePattern.matcher(input);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * Returns a list of attribute-value-pairs from the input string
	 * 
	 * @param input
	 *            the expression
	 * @return the list
	 */
	public static final ArrayList<SimpleEntry<String, String>> getAttributeValueList(String input) {
		ArrayList<SimpleEntry<String, String>> list = new ArrayList<SimpleEntry<String, String>>();
		Matcher attrMatcher = attributePattern.matcher(input);
		while (attrMatcher.find()) {
			Matcher attrNameMatcher = namePattern.matcher(attrMatcher.group());
			attrNameMatcher.find();
			String arg0 = attrNameMatcher.group();
			Matcher attrValueMatcher = attvaluePattern.matcher(attrMatcher.group());
			attrValueMatcher.find();
			String arg1 = attrValueMatcher.group();
			arg1 = arg1.substring(1, arg1.length() - 1);
			list.add(new SimpleEntry<String, String>(arg0, arg1));
		}
		return list;
	}

	/**
	 * Returns a Hashtable of attribute-value-pairs from the input string
	 * 
	 * @param input
	 *            the expression
	 * @return the Hashtable
	 */
	public static final Hashtable<String, String> getAttributeValueTable(String input) {
		Hashtable<String, String> table = new Hashtable<String, String>();
		ArrayList<SimpleEntry<String, String>> list = XMLUtils.getAttributeValueList(input);
		for (int i = 0; i < list.size(); i++) {
			SimpleEntry<String, String> entry = list.get(i);
			table.put(entry.getKey(), entry.getValue());
		}
		return table;
	}
}
