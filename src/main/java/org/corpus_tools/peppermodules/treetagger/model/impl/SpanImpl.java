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

import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;

public class SpanImpl extends AnnotatableElementImpl implements Span {
	protected String name = null;
	protected List<Token> tokens = new ArrayList<>();

	protected SpanImpl() {
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
	 * Object must be instance of Span, have the same name as this,
	 * getTokens().size() must be equal and annotations must be equal.
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

		if (!(obj instanceof Span)) {
			return false;
		}

		Span span = (Span) obj;

		// ##### compare names #####
		if (((this.getName() != null) && (!(this.getName().equals(span.getName()))))
				|| ((span.getName() != null) && (!(span.getName().equals(this.getName()))))) {
			return false;
		}

		// ##### compare tokens (mind order) #####
		if (this.getTokens().size() != span.getTokens().size()) {
			return false;
		}

		// this would cause stack overflow, because spans test tokens and tokens
		// test spans
		// iteration via counter (not iterator) -> threadsave!
		// for (int i=0;i<this.getTokens().size();i++) {
		// if (!this.getTokens().get(i).equals(span.getTokens().get(i))) {
		// return false;
		// }
		// }

		// okay fine, check super to compare Annotations
		return super.equals(obj);
	}

} // SpanImpl
