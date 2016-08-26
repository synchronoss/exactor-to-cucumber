package org.synchrnoss.test.util.cucumber.converters;

import  org.synchrnoss.test.util.cucumber.utils.StepDevType;

import java.util.List;

public interface Converter extends AutoCloseable
{
    void scenarioDescription(String line) throws Exception;
    void converter(StepDevType stepDevType, List<String> fileLine) throws Exception;
}
