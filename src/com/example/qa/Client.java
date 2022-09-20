package com.example.qa;

import java.util.*;

public class Client {

    public static Student findStudentById(int id, List<Student> students) {
        int index = students.indexOf(new Student(null, id));
        if(index == -1) return null;
        return students.get(index);
    }

    public static List<Student> listStudents(List<Student> students) {
        students.sort(Comparator.comparing(Student::getName));
        return students;
    }

    public static boolean addStudent(String name, List<Student> students) {
        int maxId;
        Optional<Student> maxStudent =
                students.stream().max(Comparator.comparingInt(Student::getId));
        maxId = maxStudent.map(Student::getId).orElse(0);
        if(maxId < Integer.MAX_VALUE) {
            students.add(new Student(name, maxId + 1));
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
        return true;
    }

    public static boolean deleteStudentById(int id, List<Student> students) {
        int index = students.indexOf(new Student(null, id));
        if(index == -1) return false;
        students.remove(index);
        return true;
    }
}
