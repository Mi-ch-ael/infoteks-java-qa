package com.example.qa;

import java.io.InputStream;
import java.util.Scanner;
import java.util.stream.Stream;

public class IOProcessor {
    private final String username;
    private final String prompt;
    private final Scanner scanner;
    private final Client client;

    public IOProcessor(InputStream stream, Client client, String username, String address) {
        this.scanner = new Scanner(stream);
        this.client = client;
        this.prompt = String.format("%s@%s> ", username, address.split("/")[0]);
        this.username = username;
    }

    public void process() {
        System.out.println("Hello, " + username + "!\nAvailable commands:\n" +
                "list - list all students in alphabetical order\n" +
                "find <id> - print student with numerical id <id>\n" +
                "add <name> - add student with name <name>\n" +
                "delete <id> - delete student with numerical id <id>\n" +
                "quit - end the program. You can also quit by pressing Ctrl-D");
        System.out.print(prompt);
        while (scanner.hasNextLine()) {
            String[] args = scanner.nextLine().trim().split("\\s+", 2);
            args = Stream.of(args).filter(string -> !string.isEmpty()).toArray(String[]::new);
            if(args.length == 0) {
                System.out.print(prompt);
                continue;
            }
            String command = args[0];
            try {
                switch (command) {
                    case "list":
                        if (args.length > 1) {
                            System.out.printf("Command '%s' does not require arguments.\nUsage: %s\n",
                                    args[0], args[0]);
                            System.out.print(prompt);
                            continue;
                        }
                        System.out.println(client.listStudents());
                        break;
                    case "find":
                    case "delete":
                        if (args.length != 2) {
                            System.out.printf("Command '%s' requires one argument 'id'.\nUsage: %s <id>\n",
                                    args[0], args[0]);
                            System.out.print(prompt);
                            continue;
                        }
                        int id;
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            System.out.printf("Argument 'id' for '%s' command must be an integer.\n" +
                                    "Usage: %s <id>\n", args[0], args[0]);
                            System.out.print(prompt);
                            continue;
                        }
                        if (args[0].equals("find")) {
                            System.out.println(client.findStudentById(id));
                        }
                        if (args[0].equals("delete")) {
                            if(!client.deleteStudentById(id)) {
                                System.out.printf("Could not delete a student: student with id=%d not found\n",
                                        id);
                            }
                        }
                        break;
                    case "add":
                        if (args.length != 2) {
                            System.out.printf("Command '%s' requires one argument 'name'.\nUsage: %s <name>\n",
                                    args[0], args[0]);
                            System.out.print(prompt);
                            continue;
                        }
                        if(!client.addStudent(args[1])) {
                            System.out.println("Failed to add student: all valid ids are already in use.");
                        }
                        break;
                    case "quit":
                        System.out.println("Bye");
                        scanner.close();
                        return;
                    default:
                        System.out.printf("Unknown command '%s'.\nAvailable commands: list, find <id>, " +
                                "add <name>, delete <id>, quit\n", args[0]);
                }
            }
            catch (Exception e) {
                System.err.println("Failed to execute the command due to the following error:");
                e.printStackTrace();
            }
            System.out.print(prompt);
        }
        System.out.println("Bye");
        scanner.close();
    }
}
