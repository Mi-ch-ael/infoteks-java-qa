package com.example.qa;

public class TestCommandRunner implements CommandRunner{
    public String list() {
        return "<no items>";
    }

    public String find(int id) {
        return String.format("Student with id=%d not found.", id);
    }
    public String delete(int id) {
        return String.format("Student with id=%d not found.", id);
    }

    public String add(String name) {
        return "Ok";
    }
}
