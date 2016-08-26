package org.synchrnoss.test.util.cucumber.converters;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import cucumber.api.java.After;
import org.apache.commons.lang3.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GlueFileProvider implements AutoCloseable
{

    private static final Map<String, TypeSpec.Builder> classNameToTypeSpecBuilder = new HashMap<>();
    private String _sourcePath;
    private String _packageLocation;

    public GlueFileProvider(String sourcePath, String packageLocation) {
        _sourcePath = sourcePath;
        _packageLocation = packageLocation;
    }

    public TypeSpec.Builder getGlueClassbuilderFor(String className) {
        return classNameToTypeSpecBuilder.computeIfAbsent(className, key -> {
            TypeSpec.Builder currentStepDev = TypeSpec.classBuilder(WordUtils.capitalize(className));
            currentStepDev.addModifiers(Modifier.PUBLIC);
            MethodSpec end = MethodSpec.methodBuilder("end")
                    .addAnnotation(After.class)
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("CucumberGlobalContext.script.getScriptSet()\n" +
                            "            .fireScriptEnded(CucumberGlobalContext.script);")
                    .build();
            currentStepDev.addMethod(end);
            return currentStepDev;
        });
    }

    public void close() throws IOException {
        for (TypeSpec.Builder builder : classNameToTypeSpecBuilder.values()) {
            JavaFile.Builder javaFileBuilder = JavaFile.builder(_packageLocation, builder.build());
            JavaFile javaFile = javaFileBuilder.build();
            final java.io.File directory = new java.io.File(_sourcePath);
            directory.mkdirs();
            javaFile.writeTo(directory);
        }

    }
}
