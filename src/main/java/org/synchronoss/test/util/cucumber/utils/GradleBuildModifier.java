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
package org.synchronoss.test.util.cucumber.utils;

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

