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

import org.corpus_tools.peppermodules.treetagger.model.POSAnnotation;

public class POSAnnotationImpl extends AnnotationImpl implements POSAnnotation {
	protected POSAnnotationImpl() {
		super();
		super.name = NAME;
	}

	/**
	 * Does not set the name - name will always be set to "pos"
	 */
	@Override
	public void setName(String newValue) {
		super.setName(NAME);
	}

}
