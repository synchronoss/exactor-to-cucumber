/**
 Copyright (c) 2016, Synchronoss
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the Synchronoss.
 4. Neither the name of the Synchronoss nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY Synchronoss ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL Synchronoss BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * */
package org.synchronoss.test.util.cucumber;

import com.google.common.base.Preconditions;
import org.synchronoss.test.util.cucumber.converters.Converter;
import org.synchronoss.test.util.cucumber.converters.GlueFileProvider;
import org.synchronoss.test.util.cucumber.converters.factories.ExacterToFeatureFactory;
import org.synchronoss.test.util.cucumber.converters.factories.ExacterToGlueFactory;
import org.synchronoss.test.util.cucumber.utils.CucumberContextGenerator;
import org.synchronoss.test.util.cucumber.utils.FolderIterator;
import org.synchronoss.test.util.cucumber.utils.GradleBuildModifier;
import org.synchronoss.test.util.cucumber.utils.TestRunnerGenerator;

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
