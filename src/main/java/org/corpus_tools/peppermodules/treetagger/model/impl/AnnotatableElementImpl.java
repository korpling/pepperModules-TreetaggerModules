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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotatableElementImpl other = (AnnotatableElementImpl) obj;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AnnotatableElementImpl [annotations=" + annotations + "]";
	}

} // AnnotatableElementImpl
