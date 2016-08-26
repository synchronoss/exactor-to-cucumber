package org.synchrnoss.test.util.cucumber.converters;

import org.synchrnoss.test.util.cucumber.utils.StepDevType;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.synchrnoss.test.util.cucumber.GenericConverter.DESCRIPTION;
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
            }else if(parameter.getValue().contains(" ")) {
                stepBuilder.append(parameter.getValue().replaceAll(" ", ""));
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
