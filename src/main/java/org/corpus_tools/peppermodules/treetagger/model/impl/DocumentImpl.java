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

import java.util.ArrayList;
import java.util.List;

import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Token;

public class DocumentImpl extends AnnotatableElementImpl implements Document {

	protected String name = null;

	protected List<Token> tokens = new ArrayList<>();

	protected DocumentImpl() {
		super();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String newName) {
		name = newName;
	}

	@Override
	public List<Token> getTokens() {
		return tokens;
	}

	/**
	 * Checks this and given object for equality. Conditions for equality:
	 * Object must be instance of Document, getTokens().size() must be equal,
	 * all Tokens must be equal and all annotations must correspond.
	 * 
	 * @param obj
	 *            An object
	 * @return true or false
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Document)) {
			return false;
		}

		Document doc = (Document) obj;

		// this can only be used if documents are really identical (including
		// names), but this method only has to check for equality of contents
		// //##### compare names #####
		// if
		// (((this.getName()!=null)&&(!(this.getName().equals(doc.getName()))))
		// ||((doc.getName()!=null)&&(!(doc.getName().equals(this.getName())))))
		// {
		// return false;
		// }

		// ##### compare tokens (mind order) #####
		if (this.getTokens().size() != doc.getTokens().size()) {
			return false;
		}

		// iteration via counter (not iterator) -> threadsave!
		for (int i = 0; i < this.getTokens().size(); i++) {
			if (!this.getTokens().get(i).equals(doc.getTokens().get(i))) {
				return false;
			}
		}

		// okay fine, check super to compare Annotations
		return super.equals(obj);
	}

} // DocumentImpl
