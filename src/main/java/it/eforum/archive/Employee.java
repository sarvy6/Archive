package it.eforum.archive;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Employee {
    protected String taxCode, firstName, lastName;
    protected double grossSalary, netSalary;
    protected LocalDate hireDate;
    protected static int daysInAYear;
    public static Employee getCheckedInstance(String taxCode, String firstName, String lastName, double grossSalary, double netSalary, LocalDate hiredate) throws IllegalArgumentException{
        updatedayInAYear(hiredate.getYear());
        return new Employee(taxCode, firstName, lastName, grossSalary, netSalary, hiredate);
    }
    private Employee(String taxCode, String firstName, String lastName, double grossSalary, double netSalary, LocalDate hireDate) throws IllegalArgumentException {
        setTaxCode(taxCode);
        setFirstName(firstName);
        setLastName(lastName);
        setGrossSalary(grossSalary);
        setNetSalary(netSalary);
        setHireDate(hireDate);
    }
    protected static void updatedayInAYear(int year) {
        int workingDays = 0;

        LocalDate date = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            date = date.plusDays(1);
        }

        daysInAYear = workingDays;
    }
    public double getNetSalaryInTheMonth(LocalDate date){
        return calculateSalary(date, netSalary);
    }
    public double getGrossSalaryInTheMonth(LocalDate date){
        return calculateSalary(date, grossSalary);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public static int getDaysInAYear() {
        return daysInAYear;
    }

    private double calculateSalary(LocalDate date, double startSalary){
        updatedayInAYear(date.getYear());
        LocalDate start = LocalDate.of(date.getYear(), date.getMonthValue(), 1);
        LocalDate end = LocalDate.of(date.getYear(), date.getMonthValue(), 31);
        double salary = 0, daySalary = startSalary / daysInAYear;
        while (!start.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                salary += daySalary;
            }
            date = date.plusDays(1);
        }
        return salary;
    }
    public void setTaxCode(String taxCode) {
        if (taxCode == null || taxCode.length() != 16) {
            throw new IllegalArgumentException("Tax code must be exactly 16 characters");
        }
        this.taxCode = taxCode.toUpperCase();
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName;
    }

    public void setGrossSalary(double grossSalary) {
        if (grossSalary < 0) {
            throw new IllegalArgumentException("Gross salary cannot be negative");
        }
        this.grossSalary = grossSalary;
    }

    public void setNetSalary(double netSalary) {
        if (netSalary < 0 || netSalary > this.grossSalary) {
            throw new IllegalArgumentException("Net salary cannot be negative or greater than gross salary");
        }
        this.netSalary = netSalary;
    }
    public String getTaxCode(){return taxCode;}
    public void setHireDate(LocalDate hireDate) {
        if (hireDate == null || hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future or null");
        }
        this.hireDate = hireDate;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false;

        Employee temp = (Employee) o;
        return this.taxCode == temp.taxCode;
    }
    

}
