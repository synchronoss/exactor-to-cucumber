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
package org.synchronoss.test.util.cucumber.converters;

import org.synchronoss.test.util.cucumber.utils.StepDevType;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.synchronoss.test.util.cucumber.GenericConverter.DESCRIPTION;
import static java.util.stream.Collectors.toList;

public class ExactorToFeature implements Converter
{

    private final FileWriter _featureFileWriter;
    private String _className;
    private String _featureFileLocation;
    private static final Logger _log = Logger.getLogger(ExactorToFeature.class.getName());
    private static List<String> _comments = new ArrayList<>();

    public ExactorToFeature(String className, String featureFileLocation) throws Exception {
        _className = className;
        _featureFileLocation = featureFileLocation;
        File feature = new File(_featureFileLocation);
        feature.mkdirs();
        _featureFileWriter = new FileWriter(feature.getPath()+ "/" + _className + ".feature");
        _featureFileWriter.write("Feature: " + _className + "\n \n");
    }

    @Override
    public void converter(StepDevType stepDevType, List<String> tokenizedString) throws Exception {
        List<KeyValuePair> keyValuePair = tokenizedString.subList(1, tokenizedString.size())
                .stream().map(KeyValuePair::new).collect(toList());
        _featureFileWriter.write("      " + WordUtils.capitalize(stepDevType.toString().toLowerCase())
                + " " + featureStep(currentObject(tokenizedString.get(0)),keyValuePair) + "\n");
    }

    @Override
    public void close() throws Exception {
        _featureFileWriter.close();

    }

    @Override
    public void scenarioDescription(String line) throws Exception {
        if (line.contains(DESCRIPTION)) {
            _featureFileWriter.write("  Scenario: " + line.substring(DESCRIPTION.length()) + "\n");
        } else {
            _log.warn("Description not found on first line");
        }
    }

    public static void addComment(String comment){
        _comments.add(comment);
    }

    private StringBuilder currentObject(String current){
        StringBuilder stepBuilder = new StringBuilder();
        stepBuilder.append(current.replaceAll("(.)([A-Z])", "$1 $2").toLowerCase());
        return stepBuilder.append(" ");
    }

    private String featureStep(StringBuilder stepBuilder, List<KeyValuePair> parameters) {
        for (KeyValuePair parameter : parameters) {
            stepBuilder.append(parameter.getKey().replaceAll("(.)([A-Z])", "$1 $2").toLowerCase());
            stepBuilder.append("=");
            if (parameter.getValue().contains("\\\\n ")) {
                stepBuilder.append(parameter.getValue().replaceAll("\\\\n ","\\\\n"));
            } else if(parameter.getValue().equals("")) {
                stepBuilder.append("\"\"");
            } else{
                stepBuilder.append(parameter.getValue());
            }
            stepBuilder.append(" ");
        }
        String stepDef = stepBuilder.toString().substring(0, stepBuilder.length() - 1);
        stepDef = stepDef.substring(0,1).toUpperCase() + stepDef.substring(1);
        return  stepDef;
    }
}
