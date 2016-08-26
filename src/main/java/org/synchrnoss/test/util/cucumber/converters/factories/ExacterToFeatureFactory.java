package org.synchrnoss.test.util.cucumber.converters.factories;

import org.synchrnoss.test.util.cucumber.converters.Converter;
import org.synchrnoss.test.util.cucumber.converters.ExactorToFeature;
import org.apache.log4j.Logger;

public class ExacterToFeatureFactory
{
    public Converter createConverter(String path) throws Exception {

        final String[] split = path.split("/");
        final String className = split[split.length - 1].split("\\.")[0];
        final String[] splitPath = path.split("test/exactor");
        final String featureFileLocation = splitPath[0] + "test/resources/com/newbay/sal/cucumber/" + splitPath[1].split(className)[0];

        return new ExactorToFeature(className, featureFileLocation);
    }
}
