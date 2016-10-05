package org.corpus_tools.peppermodules.treetagger;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.PepperUtil;
import org.corpus_tools.pepper.testFramework.PepperTestUtil;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;

public class RoundtripTest {

	@Test
	public void whenProcessingRoundtripForSimpleCorpus_SourceAndTargetCorpusMustBeEqual() throws IOException {
		final URI in = URI.createFileURI(PepperTestUtil.getTestResources() + "/roundTrip/simple/");
		final URI out = URI.createFileURI(PepperUtil.getTempTestFile("/roundTrip/").getAbsolutePath());
		final TreetaggerImporter importer = new TreetaggerImporter();
		importer.setCorpusDesc(new CorpusDesc.Builder().withCorpusPath(in).build());
		final TreetaggerExporter exporter = new TreetaggerExporter();
		exporter.setCorpusDesc(new CorpusDesc.Builder().withCorpusPath(out).build());
		PepperTestUtil.runPepperForTest(importer, exporter);

		File actual = new File(out.toFileString() + "/simple/doc1.tt");
		File expected = new File(in.toFileString() + "doc1.tt");
		assertTrue("comparing '" + expected + "' with '" + actual + "'", FileUtils.contentEquals(expected, actual));

		actual = new File(out.toFileString() + "/simple/doc2.tt");
		expected = new File(in.toFileString() + "doc2.tt");
		assertTrue("comparing '" + expected + "' with '" + actual + "'", FileUtils.contentEquals(expected, actual));
	}

	@Test
	public void whenProcessingRoundtripForCorpusWithFiveColumns_SourceAndTargetCorpusMustBeEqual() throws IOException {
		final URI in = URI.createFileURI(PepperTestUtil.getTestResources() + "/roundTrip/fiveColumns/");
		final URI out = URI.createFileURI(PepperUtil.getTempTestFile("/roundTrip/").getAbsolutePath());
		final TreetaggerImporter importer = new TreetaggerImporter();
		importer.setCorpusDesc(new CorpusDesc.Builder().withCorpusPath(in).build());
		importer.getProperties().setPropertyValue("treetagger.input.column1", "pos");
		importer.getProperties().setPropertyValue("treetagger.input.column2", "lemma");
		importer.getProperties().setPropertyValue("treetagger.input.column3", "claws");
		importer.getProperties().setPropertyValue("treetagger.input.column4", "tok_func");
		final TreetaggerExporter exporter = new TreetaggerExporter();
		exporter.setCorpusDesc(new CorpusDesc.Builder().withCorpusPath(out).build());
		PepperTestUtil.runPepperForTest(importer, exporter);

		File actual = new File(out.toFileString() + "/fiveColumns/GUM_interview_ants.tt");
		File expected = new File(in.toFileString() + "GUM_interview_ants.tt");
		assertTrue("comparing '" + expected + "' with '" + actual + "'", FileUtils.contentEquals(expected, actual));
	}
}
