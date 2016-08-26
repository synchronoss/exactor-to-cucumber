package org.synchrnoss.test.util.cucumber;

import com.google.common.base.Preconditions;
import org.synchrnoss.test.util.cucumber.converters.Converter;
import org.synchrnoss.test.util.cucumber.converters.GlueFileProvider;
import org.synchrnoss.test.util.cucumber.converters.factories.ExacterToFeatureFactory;
import org.synchrnoss.test.util.cucumber.converters.factories.ExacterToGlueFactory;
import org.synchrnoss.test.util.cucumber.utils.CucumberContextGenerator;
import org.synchrnoss.test.util.cucumber.utils.FolderIterator;
import  org.synchrnoss.test.util.cucumber.utils.GradleBuildModifier;
import org.synchrnoss.test.util.cucumber.utils.TestRunnerGenerator;

import java.util.logging.Logger;

public class CucumberConverter
{
    private static final Logger _logger = Logger.getLogger(CucumberConverter.class.getName());

    private static final String ARTIFACT = "ARTIFACT";

    public static void main(String[] args) throws Exception {
        String artifactName = Preconditions.checkNotNull(System.getProperty(ARTIFACT),"the property " + ARTIFACT + " must be set.");
        GradleBuildModifier.modify(artifactName + "/build.gradle");
        TestRunnerGenerator.create(artifactName + "/src/test/java");
        CucumberContextGenerator.create(artifactName + "/src/test/java");
        FolderIterator iterator = new FolderIterator(artifactName);
        try (final GlueFileProvider glueFileProvider = new GlueFileProvider(artifactName + "/src/test/java", "com.newbay.sal.cucumber")) {
            iterator.getFiles().forEach(path -> {
                _logger.info("processing " + path);
                try {
                    try (Converter converter = new ExacterToFeatureFactory().createConverter(path)) {
                        new GenericConverter().convert(converter, path);
                    }
                    try (Converter converter = new ExacterToGlueFactory().createConverter(glueFileProvider)) {
                        new GenericConverter().convert(converter, path);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("unable to process file " + path, e);
                }
            });
        }
    }
}
