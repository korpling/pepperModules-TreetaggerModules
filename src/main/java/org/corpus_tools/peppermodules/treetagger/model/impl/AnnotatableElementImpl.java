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

import org.corpus_tools.peppermodules.treetagger.model.AnnotatableElement;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;

public abstract class AnnotatableElementImpl implements AnnotatableElement {
	protected List<Annotation> annotations = new ArrayList<>();

	protected AnnotatableElementImpl() {
		super();
	}

	@Override
	public List<Annotation> getAnnotations() {
		return annotations;
	}

	/**
	 * Checks this and given object for equality. Conditions for equality:
	 * Object must be instance of AnnotatableElement, getAnnotations().size()
	 * must be equal and all Annotations must correspond.
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
		if (!(obj instanceof AnnotatableElement)) {
			return false;
		}

		AnnotatableElement annotatableElement = (AnnotatableElement) obj;

		// ##### compare annotations (order not relevant) #####
		if (this.getAnnotations().size() != annotatableElement.getAnnotations().size()) {
			return false;
		}
		// iteration via counter (not iterator) -> threadsave!
		for (int i = 0; i < this.getAnnotations().size(); i++) {

			boolean equalExists = false;
			for (int j = 0; (j < annotatableElement.getAnnotations().size()) && !equalExists; j++) {
				equalExists = (annotatableElement.getAnnotations().get(j).equals(this.getAnnotations().get(i)));
			}
			if (!equalExists) {
				return false;
			}
		}

		// okay fine
		return true;
	}

} // AnnotatableElementImpl
