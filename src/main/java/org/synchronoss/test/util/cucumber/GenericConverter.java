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

import org.synchronoss.test.util.cucumber.converters.Converter;
import org.synchronoss.test.util.cucumber.utils.FileLineIterator;
import org.synchronoss.test.util.cucumber.utils.StepDevType;
import org.synchronoss.test.util.cucumber.converters.ExactorToFeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GenericConverter
{

    public static final String DESCRIPTION = "Description";
    public static final String COMMENT_LINE = "#";


    public void convert(Converter converter, String fileLocation) throws Exception {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileLocation))) {
            converter.scenarioDescription(reader.readLine());
            StepDevType stepDevType = StepDevType.GIVEN;
            StepDevType prevStepDev = null;
            boolean first = true;
            for (String fileLine : new FileLineIterator(reader)) {
                try{
                    if (!fileLine.trim().startsWith(COMMENT_LINE)) {
                        if (StepDevType.isValidEnumLabel(fileLine)) {
                            stepDevType = StepDevType.valueOf(fileLine.toUpperCase());
                            first = true;
                        } else {
                            if(!first){
                                stepDevType = StepDevType.valueOf("AND");
                            }
                            converter.converter(stepDevType, sortLine(parseLine(fileLine)));
                            if(first){
                               first = false;
                            }
                        }
                    }
                    else {
                        ExactorToFeature.addComment(fileLine);
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
        return Arrays.asList(line.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)"))
                .stream().collect(Collectors.toList());
    }

    public static List<String> sortLine(List<String> fileLine){
        Collections.sort(fileLine);
        return fileLine;
    }
}
