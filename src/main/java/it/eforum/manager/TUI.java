package it.eforum.manager;

import it.eforum.entity.Employee;
import it.eforum.entity.PaySlip;
import it.eforum.entity.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class TUI {
    private final Manager manager;
    private final Scanner scanner;

    public TUI() {
        manager = new Manager();
        scanner = new Scanner(System.in);
    }

    public void menu() {
        int choice;
        StringBuilder menuBuilder = new StringBuilder();
        menuBuilder.append("\n");
        menuBuilder.append("1. Create Employee\n");
        menuBuilder.append("2. Edit Employee\n");
        menuBuilder.append("3. Delete Employee\n");
        menuBuilder.append("4. Show Employees\n");
        menuBuilder.append("5. Create PaySlip\n");
        menuBuilder.append("6. Show PaySlips of Employee\n");
        menuBuilder.append("7. Calculate Net Salary\n");
        menuBuilder.append("8. Withheld\n");
        menuBuilder.append("9. Max PaySlip Salary\n");
        menuBuilder.append("10. Get Page\n");
        menuBuilder.append("0. Exit\n");

        while (true) {
            System.out.println(menuBuilder.toString());
            System.out.print("Choice: ");
            choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) break;

            switch (choice) {
                case 1 -> createEmployee(getEmployee());
                case 2 -> editEmployee(getEmployee());
                case 3 -> deleteEmployee(getEmployee());
                case 4 -> showEmployees();
                case 5 -> createPaySlip(getPaySlip(getEmployee()));
                case 6 -> showPaySlipsOf(getEmployee());
                case 7 -> calculateNetSalary(getEmployee());
                case 8 -> withheld(getEmployee());
                case 9 -> maxPaySlipSalary();
                case 10 -> {
                    System.out.print("Page number: ");
                    int page = Integer.parseInt(scanner.nextLine());
                    getPage(page);
                }
            }
        }
        Manager.shutdown();
    }

    public Response<Employee> createEmployee(Employee employee) {
        try {
            manager.create(employee);
            return Response.ok("Employee created", employee);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<Employee> deleteEmployee(Employee employee) {
        try {
            manager.delete(employee);
            return Response.ok("Employee deleted", employee);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<Employee> editEmployee(Employee employee) {
        try {
            manager.edit(employee);
            return Response.ok("Employee updated", employee);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<List<Employee>> showEmployees() {
        try {
            List<Employee> list = manager.selectAll(Employee.class);
            return Response.ok("Employees retrieved", list);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<PaySlip> createPaySlip(PaySlip paySlip) {
        try {
            manager.create(paySlip);
            return Response.ok("PaySlip created", paySlip);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<List<PaySlip>> showPaySlipsOf(Employee employee) {
        try {
            String hql = "from PaySlip p where p.employee.id = " + employee.getId();
            List<PaySlip> list = manager.executeSelect(hql, PaySlip.class);
            return Response.ok("PaySlips retrieved", list);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<BigDecimal> calculateNetSalary(Employee employee) {
        try {
            List<PaySlip> list = manager.executeSelect("from PaySlip p where p.employee.id = " + employee.getId(), PaySlip.class);
            BigDecimal total = BigDecimal.ZERO;
            for (PaySlip p : list) total = total.add(p.getNetSalary());
            return Response.ok("Net salary calculated", total);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<BigDecimal> withheld(Employee employee) {
        try {
            List<PaySlip> list = manager.executeSelect("from PaySlip p where p.employee.id = " + employee.getId(), PaySlip.class);
            BigDecimal totalWithheld = BigDecimal.ZERO;
            for (PaySlip p : list) totalWithheld = totalWithheld.add(p.getGrossSalary().subtract(p.getNetSalary()));
            return Response.ok("Total withheld calculated", totalWithheld);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<PaySlip> maxPaySlipSalary() {
        try {
            List<PaySlip> list = manager.executeSelect("from PaySlip p order by p.netSalary desc", PaySlip.class);
            if (!list.isEmpty()) {
                return Response.ok("Max payslip found", list.get(0));
            }
            return Response.ok("No payslips available", null);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<List<Employee>> getPage(int page) {
        int offset = (page - 1) * 10;
        try {
            List<Employee> list = manager.selectAll(Employee.class);
            int toIndex = Math.min(offset + 10, list.size());
            if (offset >= list.size()) return Response.ok("No employees on this page", List.of());
            return Response.ok("Page retrieved", list.subList(offset, toIndex));
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    private Employee getEmployee() {
        Employee emp = new Employee();
        System.out.print("First name: ");
        emp.setFirstName(scanner.nextLine());
        System.out.print("Last name: ");
        emp.setLastName(scanner.nextLine());
        return emp;
    }

    private PaySlip getPaySlip(Employee employee) {
        PaySlip p = new PaySlip();
        p.setEmployee(employee);
        System.out.print("Gross Salary: ");
        p.setGrossSalary(new BigDecimal(scanner.nextLine()));
        System.out.print("Net Salary: ");
        p.setNetSalary(new BigDecimal(scanner.nextLine()));
        p.setCreationDate(LocalDateTime.now());
        p.setModificationDate(LocalDateTime.now());
        return p;
    }
}
