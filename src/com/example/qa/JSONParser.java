package com.example.qa;

import jdk.nashorn.api.scripting.JSObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    private final ScriptEngine scriptEngine;
    private static JSONParser instance = null;

    public static JSONParser getInstance() {
        if(instance == null) {
            instance = new JSONParser();
        }
        return instance;
    }

    private JSONParser() {
        ScriptEngineManager sem = new ScriptEngineManager();
        this.scriptEngine = sem.getEngineByName("javascript");
    }

    public List<Student> parse(String string) throws ScriptException {
        String script = "Java.asJSONCompatible(" + string + ")";
        JSObject obj = (JSObject) this.scriptEngine.eval(script);
        List<Student> result = new ArrayList<>();
        JSObject studentJSONArray = (JSObject) obj.getMember("students");
        for(int i = 0; studentJSONArray.hasSlot(i); ++i) {
            JSObject studentAsJSON = (JSObject) studentJSONArray.getSlot(i);
            result.add(new Student((String) studentAsJSON.getMember("name"),
                    (Integer) studentAsJSON.getMember("id")));
        }
        return result;
    }

    public String stringify(List<Student> students) throws ScriptException {
        return String.format("{\"students\":%s}", students.toString());
    }
}
