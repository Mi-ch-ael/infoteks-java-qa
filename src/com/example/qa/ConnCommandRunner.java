package com.example.qa;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnCommandRunner implements CommandRunner{
    private final ConnectionManager connectionManager;
    private final static String template = "%s (id=%d)";

    public ConnCommandRunner(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public String list() throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        Client.listStudents(students);
        List<String> formattedStrings = new ArrayList<>(students.size());
        for(Student student: students) {
            formattedStrings.add(String.format(template, student.getName(), student.getId()));
        }
        String result = String.join("\n", formattedStrings);
        if(result.isEmpty()) return "<no items>";
        return result;
    }

    public String find(int id) throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        Student student = Client.findStudentById(id, students);
        if(student == null) return String.format("Student with id=%d not found.", id);
        return String.format(template, student.getName(), student.getId());
    }

    public String delete(int id) throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        if(!Client.deleteStudentById(id, students)) {
            return String.format("Student with id=%d not found.", id);
        }
        connectionManager.putStudents(JSONParser.getInstance().stringify(students));
        return "Ok";
    }

    public String add(String name) throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        if(!Client.addStudent(name, students)) {
            return "Failed to add student: all valid ids are already in use.";
        }
        connectionManager.putStudents(JSONParser.getInstance().stringify(students));
        return "Ok";
    }
}
