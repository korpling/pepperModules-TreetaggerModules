package de.hu_berlin.german.korpling.saltnpepper.pepperModules.treetagger.mapper;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Document;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Span;
import de.hu_berlin.german.korpling.saltnpepper.misc.treetagger.Token;
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