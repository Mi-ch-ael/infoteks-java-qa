package com.example.qa;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.*;

public class Client {
    private final ConnectionManager connectionManager;
    private final static String template = "%s (id=%d)";

    public Client(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public String findStudentById(int id) throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        int index = students.indexOf(new Student(null, id));
        if(index == -1) return String.format("Student with id=%d not found.", id);
        Student student = students.get(index);
        return String.format(template, student.getName(), student.getId());
    }

    public String listStudents() throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        students.sort((thiz, that) -> thiz.getName().compareTo(that.getName()));
        List<String> formattedStrings = new ArrayList<>(students.size());
        for(Student student: students) {
            formattedStrings.add(String.format(template, student.getName(), student.getId()));
        }
        String result = String.join("\n", formattedStrings);
        if(result.isEmpty()) return "<no items>";
        return result;
    }

    public boolean addStudent(String name) throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        int maxId;
        Optional<Student> maxStudent =
                students.stream().max(Comparator.comparingInt(Student::getId));
        maxId = maxStudent.map(Student::getId).orElse(0);
        if(maxId < Integer.MAX_VALUE) {
            students.add(new Student(name, maxId + 1));
            connectionManager.putStudents(JSONParser.getInstance().stringify(students));
            return true;
        }
        if(students.size() == Integer.MAX_VALUE) {
            return false;
        }
        List<Student> sortedStudents = new ArrayList<>(students);
        sortedStudents.sort(Comparator.comparingInt(Student::getId));
        for(Student testStudent = new Student(name, 1); testStudent.getId() < Integer.MAX_VALUE;
            testStudent.setId(testStudent.getId() + 1)) {
            if(Collections.binarySearch(sortedStudents, testStudent, Comparator.comparingInt(Student::getId))
               < 0) {
                students.add(testStudent);
                break;
            }
        }
        connectionManager.putStudents(JSONParser.getInstance().stringify(students));
        return true;
    }

    public boolean deleteStudentById(int id) throws IOException, ScriptException {
        List<Student> students = JSONParser.getInstance().parse(connectionManager.getStudents());
        int index = students.indexOf(new Student(null, id));
        if(index == -1) return false;
        students.remove(index);
        connectionManager.putStudents(JSONParser.getInstance().stringify(students));
        return true;
    }
}
