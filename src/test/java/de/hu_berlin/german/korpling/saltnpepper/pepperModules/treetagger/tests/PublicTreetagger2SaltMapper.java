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

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper.Treetagger2SaltMapper;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

public class PublicTreetagger2SaltMapper extends Treetagger2SaltMapper {

	public void convert(Document tDocument, SDocument sDocument) {
		super.setTTDocument(tDocument);
		super.setSDocument(sDocument);
		super.mapSDocument();
	}
	
	public void addSMetaAnnotation(EList<Annotation> tAnnotations, SDocument sDocument) {
		super.addSMetaAnnotation(tAnnotations, sDocument);
	}
	
	public STextualDS createSTextualDS(EList<Token> tTokens, SDocument sDocument) {
		return super.createSTextualDS(tTokens, sDocument);
	}

	public SToken createSToken(Token tToken) {
		return super.createSToken(tToken);
	}
	
	public SAnnotation createSAnnotation(Annotation tAnnotation) {
		return super.createSAnnotation(tAnnotation);
	}
	
	public STextualRelation createSTextualRelation(SToken sToken, STextualDS sText, int start, int end)	{
		return super.createSTextualRelation(sToken, sText, start, end);
	}
	
}
