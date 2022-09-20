package com.example.qa;

import javax.script.ScriptException;
import java.io.IOException;

public interface CommandRunner {
    String list() throws IOException, ScriptException;
    String find(int id) throws IOException, ScriptException;
    String delete(int id) throws IOException, ScriptException;
    String add(String name) throws IOException, ScriptException;
}
