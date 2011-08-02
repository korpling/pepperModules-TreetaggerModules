/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.tests;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper.Salt2TreetaggerMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;

public class PublicSalt2TreetaggerMapper extends Salt2TreetaggerMapper {

	public void addDocumentAnnotations(EList<SMetaAnnotation> sMetaAnnotations, Document tDocument) {
		super.addDocumentAnnotations(sMetaAnnotations, tDocument);
	}
	
	public void addTokens(SDocumentGraph sDocumentGraph, Document tDocument) {
		super.addTokens(sDocumentGraph, tDocument);
	}
	
	public void addTokenAnnotations(SToken sToken, Token tToken) {
		super.addTokenAnnotations(sToken, tToken);
	}
	
	public Span createSpan(SSpan sSpan) {
		return super.createSpan(sSpan);
	}

}