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
package org.corpus_tools.peppermodules.treetagger.mapper;

import java.util.Set;

import org.corpus_tools.peppermodules.treetagger.model.Document;
import org.corpus_tools.peppermodules.treetagger.model.Span;
import org.corpus_tools.peppermodules.treetagger.model.Token;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SMetaAnnotation;

public class PublicSalt2TreetaggerMapper extends Salt2TreetaggerMapper {

	@Override
	public void addDocumentAnnotations(Set<SMetaAnnotation> sMetaAnnotations, Document tDocument) {
		super.addDocumentAnnotations(sMetaAnnotations, tDocument);
	}

	@Override
	public void addTokens(SDocumentGraph sDocumentGraph, Document tDocument) {
		super.addTokens(sDocumentGraph, tDocument);
	}

	@Override
	public void addTokenAnnotations(SToken sToken, Token tToken) {
		super.addTokenAnnotations(sToken, tToken);
	}

	@Override
	public Span createSpan(SSpan sSpan) {
		return super.createSpan(sSpan);
	}

}