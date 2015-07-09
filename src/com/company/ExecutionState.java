package com.company;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public enum  ExecutionState {

    TRUE    (Boolean.TRUE),
    FALSE   (Boolean.FALSE),
    RETURN  (null);

    final Boolean value;

    ExecutionState(Boolean value) {
        this.value = value;
    }
}
