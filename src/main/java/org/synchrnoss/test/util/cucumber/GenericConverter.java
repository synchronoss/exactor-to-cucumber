package org.synchrnoss.test.util.cucumber;

import org.synchrnoss.test.util.cucumber.converters.Converter;
import org.synchrnoss.test.util.cucumber.utils.FileLineIterator;
import org.synchrnoss.test.util.cucumber.utils.StepDevType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.synchrnoss.test.util.cucumber.converters.ExactorToFeature.addComment;
import static  org.synchrnoss.test.util.cucumber.utils.StepDevType.*;

public class GenericConverter
{

    public static final String DESCRIPTION = "Description";
    public static final String COMMENT_LINE = "#";


    public void convert(Converter converter, String fileLocation) throws Exception {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation))) {
            converter.scenarioDescription(reader.readLine());
            StepDevType stepDevType = GIVEN;
            StepDevType prevStepDev = null;
            boolean first = true;
            for (String fileLine : new FileLineIterator(reader)) {
                try{
                    if (!fileLine.trim().startsWith(COMMENT_LINE)) {
                        if (isValidEnumLabel(fileLine)) {
                            stepDevType = valueOf(fileLine.toUpperCase());
                            first = true;
                        } else {
                            if(!first){
                                stepDevType = valueOf("AND");
                            }
                            converter.converter(stepDevType, sortLine(parseLine(fileLine)));
                            if(first){
                               first = false;
                            }
                        }
                    }
                    else {
                        addComment(fileLine);
                    }
                }catch(Exception e){
                    throw new RuntimeException("unable to process " + fileLine, e);
                }
            }
        }
    }

    /**
     * Parses the current line being read in from the file and splits it into "sections" that are stored in a
     * List<String>
     * e.g. RequiresLocalServer because ="it creates a besoke server" becomes an ArrayList composed of elements
     * "RequiresLocalServer" and "because = it creates a besoke server"
     * The method splits the initial string using a regex, which works by splitting the string by its whitespace,
     * unless the whitespace exits within quotes. Once the string is split, the quotes are then removed.
     * @param line
     * @return
     */
    public static List<String> parseLine(String line) {
        boolean oddQuotes = line.matches("[^\"]*\"(?:[^\"]*\"[^\"]*\")*[^\"]*");
        if (oddQuotes) {
            throw new RuntimeException("Line contains uneven number of quotation marks");
        }
       return Arrays.asList(line.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)")).stream()
               .map(string -> string.replaceAll("\"", "")).collect(Collectors.toList());
    }

    public static List<String> sortLine(List<String> fileLine){
        Collections.sort(fileLine);
        return fileLine;
    }
}
