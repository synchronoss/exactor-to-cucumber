package org.synchrnoss.test.util.cucumber.converters.factories;

import org.synchrnoss.test.util.cucumber.converters.Converter;
import org.synchrnoss.test.util.cucumber.converters.ExactorToGlue;
import org.synchrnoss.test.util.cucumber.converters.GlueFileProvider;

public class ExacterToGlueFactory
{
    public Converter createConverter(GlueFileProvider glueFileProvider) {
        return new ExactorToGlue(glueFileProvider);
    }
}
