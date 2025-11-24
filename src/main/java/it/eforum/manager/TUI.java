package it.eforum.manager;

import it.eforum.entity.Employee;
import it.eforum.entity.PaySlip;
import it.eforum.entity.Response;

import java.math.BigDecimal;
import java.util.List;

public class TUI {
    private final Manager manager;

    public TUI() {
        manager = new Manager();
    }

    public void menu() {
        int choice = 0;
        while (true) {
            if (choice == 0) break;
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
            List<PaySlip> list = manager.executeSelect(
                    "from PaySlip p where p.employee.id = " + employee.getId(), PaySlip.class);
            BigDecimal total = BigDecimal.ZERO;
            for (PaySlip p : list) total = total.add(p.getNetSalary());
            return Response.ok("Net salary calculated", total);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<BigDecimal> withheld(Employee employee) {
        try {
            List<PaySlip> list = manager.executeSelect(
                    "from PaySlip p where p.employee.id = " + employee.getId(), PaySlip.class);
            BigDecimal totalWithheld = BigDecimal.ZERO;
            for (PaySlip p : list) totalWithheld = totalWithheld.add(p.getGrossSalary().subtract(p.getNetSalary()));
            return Response.ok("Total withheld calculated", totalWithheld);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<PaySlip> maxPaySlipSalary() {
        try {
            List<PaySlip> list = manager.executeSelect(
                    "from PaySlip p order by p.netSalary desc", PaySlip.class);
            if (!list.isEmpty()) {
                return Response.ok("Max payslip found", list.get(0));
            }
            return Response.ok("No payslips available", null);
        } catch (Exception ex) {
            return Response.error(ex.getMessage());
        }
    }

    public Response<List<Employee>> getPage(int page) { // 10 elementi per pagina
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
}
