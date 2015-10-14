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
package org.corpus_tools.peppermodules.treetagger.tests;

import java.util.List;

import org.corpus_tools.peppermodules.treetagger.mapper.Treetagger2SaltMapper;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;

public class PublicTreetagger2SaltMapper extends Treetagger2SaltMapper {

	public void convert(Document tDocument, SDocument sDocument) {
		super.setTTDocument(tDocument);
		super.setDocument(sDocument);
		super.mapSDocument();
	}
	
	public void addMetaAnnotation(List<Annotation> tAnnotations, SDocument sDocument) {
		super.addMetaAnnotation(tAnnotations, sDocument);
	}
	
	public STextualDS createSTextualDS(List<Token> tTokens, SDocument sDocument) {
		return super.createSTextualDS(tTokens, sDocument);
	}

	public SToken createSToken(Token tToken) {
		return super.createSToken(tToken);
	}
	
	public SAnnotation createAnnotation(Annotation tAnnotation) {
		return super.createAnnotation(tAnnotation);
	}
	
	public STextualRelation createSTextualRelation(SToken sToken, STextualDS sText, int start, int end)	{
		return super.createSTextualRelation(sToken, sText, start, end);
	}
	
}
