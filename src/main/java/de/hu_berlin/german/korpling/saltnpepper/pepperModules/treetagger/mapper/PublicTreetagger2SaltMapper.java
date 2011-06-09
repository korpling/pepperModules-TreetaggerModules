package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Annotation;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

public class PublicTreetagger2SaltMapper extends Treetagger2SaltMapper {

	public void convert(Document tDocument, SDocument sDocument) {
		super.map(tDocument, sDocument);
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
