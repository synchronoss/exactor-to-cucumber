package org.synchrnoss.test.util.cucumber.converters;


import com.squareup.javapoet.MethodSpec;

import java.util.HashSet;
import java.util.Set;

public class StepDefinitions {

    private static Set<MethodSpec> allMethods = new HashSet<>();

    public static void addMethod(MethodSpec newMethod){
        allMethods.add(newMethod);
    }

    public static Boolean checkExists(MethodSpec newMethod){
        return allMethods.stream().anyMatch(
                current -> {
                    if (!current.name.equals(newMethod.name)) {
                        return false;
                    }
                    String annotation = current.annotations.get(0).members.get("value").toString();
                    return annotation.equals(newMethod.annotations.get(0).members.get("value").toString());
                });
    }

}

