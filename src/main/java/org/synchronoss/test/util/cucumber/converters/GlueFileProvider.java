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
