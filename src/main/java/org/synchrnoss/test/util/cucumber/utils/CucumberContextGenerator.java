package org.synchrnoss.test.util.cucumber.utils;

import com.newbay.exactor.Script;
import com.newbay.exactor.ScriptSet;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;

public class CucumberContextGenerator {

    public static void create(String _path) throws Exception {
        File context = new File(_path + "CucumberGlobalContext");
        if (context.exists()) {
            return;
        }
        TypeSpec.Builder globalContext = TypeSpec.classBuilder("CucumberGlobalContext");
        globalContext.addModifiers(Modifier.PUBLIC);
        globalContext.addMethod(MethodSpec.methodBuilder("sanitizeString")
                                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                                .addParameter(String.class, "s")
                                .addStatement("final String replace = s.replace(\"\\\\n\", \"\\n\").replace(\"\\\\\\\\:\", \"\\\\:\")")
                                .beginControlFlow("if (replace.charAt(0) == '\"')")
                                .addStatement("return replace.substring(1, s.length() - 1)")
                                .endControlFlow()
                                .addStatement("return replace")
                                .returns(String.class)
                                .build());
        globalContext.addField(FieldSpec.builder(Script.class, "script")
                                .addModifiers(Modifier.PUBLIC,Modifier.STATIC, Modifier.FINAL)
                                .initializer(" new Script( $S , new $T() )","test",ScriptSet.class)
                                .build());
        TypeSpec current = globalContext.build();

        JavaFile.Builder javaFileBuilder = JavaFile.builder("com.newbay.sal.cucumber", current);
        JavaFile javaFile = javaFileBuilder.build();
        final java.io.File directory = new java.io.File(_path);
        directory.mkdirs();
        javaFile.writeTo(directory);
    }

}