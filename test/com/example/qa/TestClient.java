package com.example.qa;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

public class TestClient {
    @DataProvider(name = "listStudents")
    public Object[][] serveSortedStudents() {
        return new Object[][] {
                {
                        new ArrayList<Student>(),
                        new ArrayList<Student>()
                },
                {
                        Arrays.asList(new Student("Adam", 1), new Student("Jack Johnson", 2)),
                        Arrays.asList(new Student("Adam", 1), new Student("Jack Johnson", 2))
                },
                {
                        Arrays.asList(new Student("Jack Johnson", 1), new Student("Adam", 2)),
                        Arrays.asList(new Student("Adam", 2), new Student("Jack Johnson", 1))
                },
                {
                        Arrays.asList(new Student("Max", 2), new Student("John", 6),
                                new Student("Mike", 1), new Student("Александр", 4),
                                new Student("Molly", 9)),
                        Arrays.asList(new Student("John", 6), new Student("Max", 2),
                                new Student("Mike", 1), new Student("Molly", 9),
                                new Student("Александр", 4))
                }
        };
    }

    @Test(dataProvider = "listStudents")
    public void testListStudents(List<Student> input, List<Student> expected) {
        assertEquals(expected, Client.listStudents(input));
        for(int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i).getName(), input.get(i).getName());
        }
    }

    @Test
    public void testListStudentsSameName() {
        List<Student> result = Client.listStudents(Arrays.asList(
                new Student("Charles", 1),
                new Student("Charles", 2),
                new Student("Adam", 3)
        ));
        assertEquals(3, result.size());
        assertEquals("Adam", result.get(0).getName());
        assertEquals(3, result.get(0).getId());
        assertEquals("Charles", result.get(1).getName());
        assertEquals("Charles", result.get(2).getName());
    }

    @DataProvider(name = "findStudentById")
    public Object[][] serveFindStudentData() {
        return new Object[][] {
                {
                        new ArrayList<Student>(),
                        1,
                        null
                },
                {
                        Arrays.asList(new Student("Kate", 1), new Student("Ann", 3)),
                        1,
                        new Student("Kate", 1)
                },
                {
                        Arrays.asList(new Student("Kate", 1), new Student("Kate", 3)),
                        1,
                        new Student("Kate", 1)
                },
                {
                        Arrays.asList(new Student("Kate", 1), new Student("Ann", 3)),
                        2,
                        null
                }
        };
    }

    @Test(dataProvider = "findStudentById")
    public void testFindStudentById(List<Student> input, int id, Student expected) {
        List<Student> clonedInput = new ArrayList<>(input);
        Student result = Client.findStudentById(id, input);
        assertEquals(input, clonedInput);
        assertEquals(expected, result);
        if(expected != null) {
            assertEquals(expected.getName(), result.getName());
        }
    }

    @DataProvider(name= "deleteStudentById")
    public Object[][] serveDeleteStudentData() {
        return new Object[][] {
                {
                        new ArrayList<Student>(),
                        1,
                        new ArrayList<Student>(),
                        false
                },
                {
                        new ArrayList<>(
                                Arrays.asList(new Student("Kate", 1), new Student("Ann", 3))
                        ),
                        2,
                        Arrays.asList(new Student("Kate", 1), new Student("Ann", 3)),
                        false
                },
                {
                        new ArrayList<>(
                                Arrays.asList(new Student("Kate", 1), new Student("Ann", 3))
                        ),
                        1,
                        Collections.singletonList(new Student("Ann", 3)),
                        true
                },
                {
                        new ArrayList<>(
                                Collections.singletonList(new Student("Ann", 3))
                        ),
                        3,
                        Collections.emptyList(),
                        true
                }
        };
    }

    @Test(dataProvider = "deleteStudentById")
    public void testDeleteStudentById(List<Student> input, int id, List<Student> expectedList,
                                      boolean expectedResult) {
        assertEquals(expectedResult, Client.deleteStudentById(id, input));
        assertEquals(expectedList, input);
    }

    @DataProvider(name = "addStudent")
    public Object[][] serveAddStudentData() {
        return new Object[][] {
                {
                        new ArrayList<Student>(),
                        "Ivan",
                        Collections.singletonList(new Student("Ivan", 1)),
                        true
                },
                {
                        new ArrayList<>(Arrays.asList(new Student("Jack", 1),
                                new Student("John", 2))),
                        "Ivan",
                        Arrays.asList(new Student("Jack", 1),
                                new Student("John", 2), new Student("Ivan", 3)),
                        true
                },
                {
                        new ArrayList<>(Arrays.asList(new Student("Jack", 2),
                                new Student("John", 3))),
                        "Ivan",
                        Arrays.asList(new Student("Jack", 2),
                                new Student("John", 3), new Student("Ivan", 4)),
                        true
                },
                {
                        new ArrayList<>(Collections.singletonList(new Student("Jack", 1))),
                        "Jack",
                        Arrays.asList(new Student("Jack", 1), new Student("Jack", 2)),
                        true
                },
                {
                        new ArrayList<>(Arrays.asList(new Student("Jack", 1),
                                new Student("Tom", Integer.MAX_VALUE))),
                        "Ivan",
                        Arrays.asList(new Student("Jack", 1),
                                new Student("Tom", Integer.MAX_VALUE),
                                new Student("Ivan", 2)),
                        true
                }
        };
    }

    @Test(dataProvider = "addStudent")
    public void testAddStudent(List<Student> input, String name, List<Student> expectedList, boolean expected) {
        assertEquals(expected, Client.addStudent(name, input));
        assertEquals(expectedList, input);
    }

    @Test(groups = {"computational-heavy"})
    public void testAddStudentFullList() {
        List<Student> fullList = new ArrayList<>();
        for(int i = 1; i > 0; ++i) {
            fullList.add(new Student(null, i));
        }
        assertEquals(Integer.MAX_VALUE, fullList.size());
        assertFalse(Client.addStudent("Ivan", fullList));
        assertEquals(Integer.MAX_VALUE, fullList.size());
    }
}
