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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

import javax.lang.model.element.Modifier;
import java.io.File;

public class TestRunnerGenerator
{

    public static void create(String path) throws Exception {
        File runner = new File(path + "TestRunner.java");
        if (runner.exists()) {
            return;
        }

        TypeSpec.Builder testRunner = TypeSpec.classBuilder("TestRunner");
        testRunner.addModifiers(Modifier.PUBLIC);
        testRunner.addAnnotation(AnnotationSpec.builder(RunWith.class)
                .addMember("value", "$T.class", Cucumber.class)
                .build());
        testRunner.addAnnotation(AnnotationSpec.builder(CucumberOptions.class)
                .build());
        TypeSpec current = testRunner.build();

        JavaFile.Builder javaFileBuilder = JavaFile.builder("com.newbay.sal.cucumber", current);
        JavaFile javaFile = javaFileBuilder.build();
        final java.io.File directory = new java.io.File(path);
        directory.mkdirs();
        javaFile.writeTo(directory);
    }
}