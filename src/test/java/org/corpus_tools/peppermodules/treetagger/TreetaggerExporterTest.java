package org.corpus_tools.peppermodules.treetagger;

import static org.assertj.core.api.Assertions.assertThat;

import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.common.ModuleFitness;
import org.corpus_tools.pepper.common.ModuleFitness.FitnessFeature;
import org.corpus_tools.pepper.core.ModuleFitnessChecker;
import org.corpus_tools.pepper.testFramework.PepperExporterTest;
import org.corpus_tools.pepper.testFramework.PepperTestUtil;
import org.corpus_tools.salt.SaltFactory;
import org.junit.Before;
import org.junit.Test;

public class TreetaggerExporterTest extends PepperExporterTest {

	@Before
	public void setUp() throws Exception {
		super.setFixture(new TreetaggerExporter());
		super.getFixture().setSaltProject(SaltFactory.createSaltProject());

		// set formats to support
		FormatDesc formatDef = new FormatDesc();
		formatDef.setFormatName("treetagger");
		formatDef.setFormatVersion("1.0");
		addSupportedFormat(formatDef);
	}

	@Test
	public void whenSelfTestingModule_thenResultShouldBeTrue() {
		final ModuleFitness fitness = new ModuleFitnessChecker(PepperTestUtil.createDefaultPepper()).selfTest(fixture);
		assertThat(fitness.getFitness(FitnessFeature.HAS_SELFTEST)).isTrue();
		assertThat(fitness.getFitness(FitnessFeature.HAS_PASSED_SELFTEST)).isTrue();
	}
}
