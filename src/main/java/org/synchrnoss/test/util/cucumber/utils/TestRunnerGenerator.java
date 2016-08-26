package org.synchrnoss.test.util.cucumber.utils;

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