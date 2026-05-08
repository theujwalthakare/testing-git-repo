/**
 * ============================================================================
 * JAVA PRACTICAL SLIPS 2026 - DEBUGGED VERSION
 * JAVA 24 COMPATIBLE
 * ============================================================================
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

// ============================================================================
// CUSTOM EXCEPTIONS
// ============================================================================

class MarksOutOfBoundException extends Exception {
    public MarksOutOfBoundException(String message) {
        super(message);
    }
}

class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// ============================================================================
// EMPLOYEE HIERARCHY
// ============================================================================

class Employee {
    protected String name;
    protected String employeeId;
    protected String dateOfBirth;
    protected double salary;

    public Employee(String name, String employeeId, String dateOfBirth, double salary) {
        this.name = name;
        this.employeeId = employeeId;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
    }

    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Date Of Birth: " + dateOfBirth);
    }

    public double calculateSalary() {
        return salary;
    }
}

class Manager extends Employee {
    private double bonus;

    public Manager(String name, String employeeId, String dateOfBirth,
                   double salary, double bonus) {
        super(name, employeeId, dateOfBirth, salary);
        this.bonus = bonus;
    }

    @Override
    public double calculateSalary() {
        return salary + bonus;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Salary: " + salary);
        System.out.println("Bonus: " + bonus);
    }
}

class SalesManager extends Manager {
    private double commission;

    public SalesManager(String name, String employeeId, String dateOfBirth,
                        double salary, double bonus, double commission) {
        super(name, employeeId, dateOfBirth, salary, bonus);
        this.commission = commission;
    }

    @Override
    public double calculateSalary() {
        return super.calculateSalary() + commission;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Commission: " + commission);
    }
}

// ============================================================================
// LIBRARY MANAGEMENT
// ============================================================================

class Book {
    private String bookId;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public Book(String bookId, String title, String author, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public void issueBook() throws BookNotAvailableException {
        if (availableCopies <= 0) {
            throw new BookNotAvailableException("Book not available");
        }
        availableCopies--;
    }

    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public void displayInfo() {
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Available Copies: " + availableCopies);
    }
}

class Library {
    private final List<Book> books;
    private final Map<String, List<Book>> issuedBooks;

    public Library() {
        books = new ArrayList<>();
        issuedBooks = new HashMap<>();
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book Added Successfully");
    }

    public void issueBook(String memberId, String bookId)
            throws BookNotAvailableException {

        Book book = findBook(bookId);

        if (book == null) {
            throw new BookNotAvailableException("Book Not Found");
        }

        book.issueBook();

        issuedBooks.computeIfAbsent(memberId, k -> new ArrayList<>()).add(book);

        System.out.println("Book Issued Successfully");
    }

    public void returnBook(String memberId, String bookId)
            throws BookNotAvailableException {

        Book book = findBook(bookId);

        if (book == null) {
            throw new BookNotAvailableException("Book Not Found");
        }

        List<Book> memberBooks = issuedBooks.get(memberId);

        if (memberBooks == null || memberBooks.isEmpty()) {
            throw new BookNotAvailableException("No Books Issued");
        }

        if (memberBooks.remove(book)) {
            book.returnBook();
            System.out.println("Book Returned Successfully");
        }
    }

    public void displayBooks() {
        for (Book book : books) {
            book.displayInfo();
            System.out.println();
        }
    }

    private Book findBook(String bookId) {
        for (Book book : books) {
            if (book.getBookId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }
}

// ============================================================================
// BANK ACCOUNT
// ============================================================================

class BankAccount {
    private final String accountNumber;
    private final String accountHolder;
    private double balance;

    public BankAccount(String accountNumber,
                       String accountHolder,
                       double balance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid Amount");
            return;
        }

        balance += amount;

        System.out.println(accountHolder + " Deposited: " + amount);
        System.out.println("Current Balance: " + balance);
    }

    public synchronized void withdraw(double amount)
            throws InsufficientFundsException {

        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient Balance");
        }

        balance -= amount;

        System.out.println(accountHolder + " Withdraw: " + amount);
        System.out.println("Current Balance: " + balance);
    }

    public void displayInfo() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Holder: " + accountHolder);
        System.out.println("Balance: " + balance);
    }
}

class DepositThread extends Thread {
    private final BankAccount account;

    public DepositThread(BankAccount account) {
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            account.deposit(500);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread Interrupted");
            }
        }
    }
}

class WithdrawThread extends Thread {
    private final BankAccount account;

    public WithdrawThread(BankAccount account) {
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {

            try {
                account.withdraw(300);
                Thread.sleep(500);

            } catch (InsufficientFundsException e) {
                System.out.println(e.getMessage());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread Interrupted");
            }
        }
    }
}

// ============================================================================
// FILE OPERATIONS
// ============================================================================

class EmployeeFileManager {

    private static final String FILE_NAME = "employees.txt";

    public static void saveEmployee(String employeeData) {

        try (
                FileWriter fw = new FileWriter(FILE_NAME,
                        StandardCharsets.UTF_8,
                        true);

                BufferedWriter bw = new BufferedWriter(fw)
        ) {

            bw.write(employeeData);
            bw.newLine();

            System.out.println("Employee Saved Successfully");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void displayEmployees() {

        try (
                BufferedReader br =
                        new BufferedReader(new FileReader(FILE_NAME,
                                StandardCharsets.UTF_8))
        ) {

            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

// ============================================================================
// STUDENT VALIDATION
// ============================================================================

class Student {
    private String rollNo;
    private String name;
    private int marks;

    public Student(String rollNo, String name, int marks)
            throws MarksOutOfBoundException {

        this.rollNo = rollNo;
        this.name = name;

        setMarks(marks);
    }

    public void setMarks(int marks)
            throws MarksOutOfBoundException {

        if (marks < 0 || marks > 100) {
            throw new MarksOutOfBoundException(
                    "Marks Must Be Between 0 To 100"
            );
        }

        this.marks = marks;
    }

    public void displayInfo() {
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Marks: " + marks);
    }
}

// ============================================================================
// MAIN CLASS
// ============================================================================

public class JavaPracticalSlipsSolution {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            displayMenu();

            int choice = getChoice();

            switch (choice) {

                case 1 -> employeeDemo();

                case 2 -> libraryDemo();

                case 3 -> bankDemo();

                case 4 -> fileDemo();

                case 5 -> exceptionDemo();

                case 6 -> {
                    System.out.println("Program Exited");
                    running = false;
                }

                default -> System.out.println("Invalid Choice");
            }
        }

        scanner.close();
    }

    private static void displayMenu() {

        System.out.println("\n===== JAVA PRACTICAL MENU =====");

        System.out.println("1. Employee Hierarchy");
        System.out.println("2. Library Management");
        System.out.println("3. Bank Multithreading");
        System.out.println("4. File Operations");
        System.out.println("5. Exception Handling");
        System.out.println("6. Exit");
    }

    private static int getChoice() {

        try {
            System.out.print("Enter Choice: ");
            return scanner.nextInt();

        } catch (InputMismatchException e) {

            scanner.nextLine();
            return -1;
        }
    }

    private static void employeeDemo() {

        Employee emp = new Employee(
                "John",
                "E101",
                "10-10-2000",
                50000
        );

        Manager manager = new Manager(
                "Alice",
                "M101",
                "11-11-1995",
                70000,
                10000
        );

        SalesManager salesManager = new SalesManager(
                "Bob",
                "SM101",
                "05-05-1992",
                80000,
                15000,
                12000
        );

        emp.displayInfo();

        System.out.println();

        manager.displayInfo();

        System.out.println("Total Salary: " +
                manager.calculateSalary());

        System.out.println();

        salesManager.displayInfo();

        System.out.println("Total Salary: " +
                salesManager.calculateSalary());
    }

    private static void libraryDemo() {

        Library library = new Library();

        library.addBook(
                new Book("B101",
                        "Java Programming",
                        "James Gosling",
                        3)
        );

        library.addBook(
                new Book("B102",
                        "Data Structures",
                        "Mark Allen",
                        2)
        );

        library.displayBooks();

        try {

            library.issueBook("M001", "B101");

            library.returnBook("M001", "B101");

        } catch (BookNotAvailableException e) {

            System.out.println(e.getMessage());
        }
    }

    private static void bankDemo() {

        BankAccount account =
                new BankAccount(
                        "ACC101",
                        "John Doe",
                        5000
                );

        DepositThread depositThread =
                new DepositThread(account);

        WithdrawThread withdrawThread =
                new WithdrawThread(account);

        depositThread.start();
        withdrawThread.start();

        try {

            depositThread.join();
            withdrawThread.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        account.displayInfo();
    }

    private static void fileDemo() {

        EmployeeFileManager.saveEmployee(
                "E101,John,50000"
        );

        EmployeeFileManager.saveEmployee(
                "E102,Alice,70000"
        );

        EmployeeFileManager.displayEmployees();
    }

    private static void exceptionDemo() {

        try {

            Student student =
                    new Student(
                            "101",
                            "Rahul",
                            110
                    );

            student.displayInfo();

        } catch (MarksOutOfBoundException e) {

            System.out.println(e.getMessage());
        }
    }
}