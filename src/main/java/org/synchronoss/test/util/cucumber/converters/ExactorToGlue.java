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


import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.synchronoss.test.util.cucumber.utils.StepDevType;
import com.newbay.exactor.Parameter;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.text.WordUtils;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ExactorToGlue implements Converter
{

    private GlueFileProvider _glueFileProvider;

    public ExactorToGlue(GlueFileProvider glueFileProvider) {
        _glueFileProvider = glueFileProvider;
    }

    @Override
    public void scenarioDescription(String description) throws Exception {
    }

    @Override
    public void converter(StepDevType stepDevType, List<String> fileLine) throws Exception {
        List<KeyValuePair> keyValuePair = fileLine.subList(1, fileLine.size())
                .stream().map(KeyValuePair::new).collect(toList());

        String stepDefName = getStepName(currentObject(fileLine.get(0)),keyValuePair);
        MethodSpec current = createMethod(stepDevType.getCucumberClass(), stepDefName, fileLine,
                fileLine.get(0));

        if (!StepDefinitions.checkExists(current)) {
            TypeSpec.Builder glueClassbuilderFor = getTypeSpecForThisLine(fileLine);
            glueClassbuilderFor.addMethod(current);
            StepDefinitions.addMethod(current);
        }
    }

    private TypeSpec.Builder getTypeSpecForThisLine(List<String> fileLine) throws Exception {
        Class salClass = getSALClass(fileLine.get(0));
        final Package aPackage = salClass.getPackage();
        final String[] split = aPackage.getName().split("\\.");
        final String s = split[split.length - 1];
        return _glueFileProvider.getGlueClassbuilderFor(s);
    }

    @Override
    public void close() throws Exception {
    }

    /**
     * Returns the glue code that will exist in the cucumber annotation.
     * */
    private static String getStepName(StringBuilder stepBuilder, List<KeyValuePair> parameters) {
        for (KeyValuePair parameter : parameters) {
            stepBuilder.append(parameter.getKey().replaceAll("(.)([A-Z])", "$1 $2").toLowerCase());
            stepBuilder.append("=([^\\\\s]*|\\\".*\\\") ");
        }
        String stepDef = stepBuilder.toString().substring(0, stepBuilder.length() - 1);
        stepDef = stepDef.substring(0,1).toUpperCase() + stepDef.substring(1);
        stepDef = '^' + stepDef;
        return  stepDef;
    }

    private StringBuilder currentObject(String current){
        StringBuilder builder = new StringBuilder();
        builder.append(current.replaceAll("(.)([A-Z])", "$1 $2").toLowerCase());
        return builder.append(" ");
    }

    private MethodSpec createMethod(Class cucumberAnnotationType, String name,
                                    List<String> sections, String methodName) throws Exception {
        Class salClass = getSALClass(sections.get(0));
        CodeBlock.Builder codeBlock = CodeBlock.builder().addStatement("$T object = new $T()", salClass,salClass);
        codeBlock.addStatement("object.setScript(CucumberGlobalContext.script)");
        Method[] declaredMethods = salClass.getDeclaredMethods();
        ArrayList<ParameterSpec> parameters = getMethodParameters(sections, codeBlock, declaredMethods);
        codeBlock.addStatement("object.execute()");
        return MethodSpec.methodBuilder(methodName.substring(0,1).toLowerCase() + methodName.substring(1) + getParameterDescriptor(parameters))
                .addParameters(parameters)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(cucumberAnnotationType)
                        .addMember("value", "\"" + name.replace("${key}", "_key") + "$L\"", "$")
                        .build())
                .returns(void.class)
                .addException(Exception.class)
                .addCode(codeBlock.build())
                .build();
    }

    private String getParameterDescriptor(ArrayList<ParameterSpec> parameters) {
        StringBuilder builder = new StringBuilder();
        parameters.forEach(string -> builder.append(WordUtils.capitalize(string.name)));
        return builder.toString();
    }


    private static Class getSALClass(String className) throws Exception{
        ClassPath currentClassLoader = ClassPath.from(ClassLoader.getSystemClassLoader());
        ImmutableSet<ClassPath.ClassInfo> topLevelClasses = currentClassLoader.getTopLevelClasses();
        List<ClassPath.ClassInfo> classPathClasses = new ArrayList<>();
        topLevelClasses.stream().filter(n -> n.getPackageName().contains("com.newbay")).forEach(classPathClasses::add);
        ClassPath.ClassInfo newClass = searchClasses(className, classPathClasses);
        return newClass.load();

    }


    private static ArrayList<ParameterSpec> getMethodParameters(List<String> tokenizedString, CodeBlock.Builder codeBlock, Method[] salMethods) throws Exception {
        ArrayList<ParameterSpec> parameters = new ArrayList<>();
        List<KeyValuePair> keyValuePairStream = tokenizedString.subList(1, tokenizedString.size()).stream().map(KeyValuePair::new).collect(toList());
        for (KeyValuePair section : keyValuePairStream) {
            String key = section.getKey();
            String capitalizedParam = WordUtils.capitalize(key);
            Type paramType = getType("set" + capitalizedParam, salMethods, tokenizedString.get(0));
            ParameterSpec newParameter = ParameterSpec.builder(paramType, "_" + key).build();
            parameters.add(newParameter);
            if (String.class.equals(paramType)){
                codeBlock.addStatement("object.add$T(new $T(\"" + key + "=\" + " + "CucumberGlobalContext.sanitizeString(_" + key + ")))", Parameter.class, Parameter.class);
            } else {
                codeBlock.addStatement("object.add$T(new $T(\"" + key + "=\" + " + "_" + key + "))", Parameter.class, Parameter.class);
            }
        }
        return parameters;
    }

    private static ClassPath.ClassInfo searchClasses(String className, List<ClassPath.ClassInfo> classes) {
        for(ClassPath.ClassInfo current: classes ){
            if(current.getSimpleName().equals(className)){
                return current;
            }
        }
        return null;
    }


    private static Type getType(String methodName, Method[] methods, String className) throws Exception {
        try {
            for (Method current : methods) {
                if (current.getName().equals(methodName)) {
                    List<java.lang.reflect.Parameter> salMethods = Arrays.asList(current.getParameters());
                    return salMethods.get(0).getParameterizedType();
                }
            }
            Class current = getSALClass(className).getSuperclass();
            Method[] declaredMethods = current.getDeclaredMethods();
            return getType(methodName, declaredMethods, current.getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException("Unable to find the method " + methodName + " in the class hierarchy of " + className, e);
        }
    }
}