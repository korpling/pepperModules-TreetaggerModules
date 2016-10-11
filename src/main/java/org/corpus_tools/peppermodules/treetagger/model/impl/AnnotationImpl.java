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

import org.corpus_tools.peppermodules.treetagger.model.AnnotatableElement;
import org.corpus_tools.peppermodules.treetagger.model.Annotation;

public class AnnotationImpl implements Annotation {
	protected String name = null;
	protected String value = null;
	protected AnnotatableElement annotatableElement;

	protected AnnotationImpl() {
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
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String newValue) {
		value = newValue;
	}

	@Override
	public AnnotatableElement getAnnotatableElement() {
		return annotatableElement;
	}

	@Override
	public void setAnnotatableElement(AnnotatableElement newAnnotatableElement) {
		annotatableElement = newAnnotatableElement;
	}

	/**
	 * TODO: describe
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Annotation)) {
			return false;
		}
		Annotation anno = (Annotation) obj;

		// ##### compare name #####
		if (((this.getName() != null) && (!(this.getName().equals(anno.getName()))))
				|| ((anno.getName() != null) && (!(anno.getName().equals(this.getName()))))) {
			return false;
		}

		// ##### compare value #####
		if (((this.getValue() != null) && (!(this.getValue().equals(anno.getValue()))))
				|| ((anno.getValue() != null) && (!(anno.getValue().equals(this.getValue()))))) {
			return false;
		}

		// okay fine
		return true;
	}

} // AnnotationImpl
