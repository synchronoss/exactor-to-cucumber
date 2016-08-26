package org.synchrnoss.test.util.cucumber.utils;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public enum StepDevType
{
    GIVEN(Given.class), WHEN(When.class), THEN(Then.class), AND(And.class);

    private Class<?> _cucumberClass;

    StepDevType(Class<?> cucumberClass) {
        _cucumberClass = cucumberClass;
    }

    public Class<?> getCucumberClass() {
        return _cucumberClass;
    }

    public static boolean isValidEnumLabel(String label) {
        return label.equals("Given") || label.equals("When") || label.equals("Then")|| label.equals("And");
    }

}