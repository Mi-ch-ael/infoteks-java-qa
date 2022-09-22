package com.example.qa;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertSame;

public class TestJSONParser {
    @Test
    public void testCreation() {
        JSONParser parser1 = JSONParser.getInstance();
        JSONParser parser2 = JSONParser.getInstance();
        assertSame(parser1, parser2);
    }

    @DataProvider(name = "JSONStrings")
    public Object[][] serveCorrectJSONStrings() {
        return new Object[][] {
                {"{\"students\": []}", new ArrayList<Student>()},
                {"{\"students\": [{\"id\": 1, \"name\": \"Max\"}, {\"id\": 2, \"name\": \"Jack\"}]}",
                        Arrays.asList(new Student("Max", 1), new Student("Jack", 2))},
                {"{\"students\": [{\"id\": 1, \"name\": \"Jack\"}, {\"id\": 2, \"name\": \"Jack\"}]}",
                        Arrays.asList(new Student("Jack", 1), new Student("Jack", 2))}
        };
    }

    @Test(dataProvider = "JSONStrings")
    public void testParseJSON(String string, List<Student> expected) throws ScriptException {
        assertEquals(expected, JSONParser.getInstance().parse(string));
    }

    @DataProvider(name = "studentLists")
    public Object[][] serveStudentLists() {
        return new Object[][] {
                {new ArrayList<Student>(), "{\"students\":[]}"},
                {Arrays.asList(new Student("Max", 1), new Student("Jack", 2)),
                        "{\"students\":[{\"id\":1,\"name\":\"Max\"}, {\"id\":2,\"name\":\"Jack\"}]}"},
                {Arrays.asList(new Student("Jack", 1), new Student("Jack", 2)),
                        "{\"students\":[{\"id\":1,\"name\":\"Jack\"}, {\"id\":2,\"name\":\"Jack\"}]}"}
        };
    }

    @Test(dataProvider = "studentLists")
    public void testStringifyStudentLists(List<Student> students, String expected) {
        assertEquals(expected, JSONParser.getInstance().stringify(students));
    }
}
