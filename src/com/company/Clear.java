package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public enum Clear {

    VOID        ("void"),
    VARIABLES   ("var"),
    METHODS     ("met"),
    IMPORTS     ("imp");

    final String value;

    Clear(String value) {
        this.value = value;
    }

    static Clear[] forValue(String value) {

        if (TextUtils.isEmpty(value)) {
            return values();
        }

        final List<Clear> list = new ArrayList<Clear>();
        for (Clear clear: values()) {
            if (value.contains(clear.value)) {
                list.add(clear);
            }
        }

        final Clear[] clears = new Clear[list.size()];
        list.toArray(clears);
        return clears;
    }
}
