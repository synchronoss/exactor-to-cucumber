package org.synchrnoss.test.util.cucumber.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradleBuildModifier
{

    private static final List<String> compileGroup = Arrays.asList("compile group: 'info.cukes', name: 'cucumber-java', version: '1.2.4'", "testCompile 'info.cukes:cucumber-junit:1.2.4'");


    public static void modify(String _path) throws Exception {
        Boolean present = false;
        List<String> gradleContents = new ArrayList<>();
        Boolean modified = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(_path))) {
            for (String fileLine : new FileLineIterator(reader)) {
                if (fileLine.trim().equals(compileGroup.get(0))) {
                    present = true;
                } else if (fileLine.contains("dependencies") && !modified) {
                    modified = true;
                    gradleContents.add(fileLine);
                    for(String compile: compileGroup){
                        gradleContents.add(compile);
                    }
                } else {
                    gradleContents.add(fileLine.trim());
                }
            }
        }
        if (!present) {
            try (BufferedWriter modifyGradle = new BufferedWriter(new FileWriter(_path))) {
                for (String contents : gradleContents) {
                    modifyGradle.write(contents + "\n");
                }
            }
        }
    }
}

