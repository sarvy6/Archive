package it.eforum;

import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import it.eforum.archive.Archive;
import it.eforum.archive.Employee;
import it.eforum.archive.PayLip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Manager {
    private Connection connection;
    public Manager() throws SQLException{
        String url = "jdbc:mysql://localhost:3306/nomedb";
        String user = "tuo_utente";
        String password = "tua_password";
        connection = DriverManager.getConnection(url, user, password);
    }
    public String addEmployee(Employee employee) {
        String result = "";
        String sql = "INSERT INTO Employees (taxCode, firstName, lastName, grossSalary, netSalary, hireDate) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getTaxCode());
            pstmt.setString(2, employee.getFirstName());
            pstmt.setString(3, employee.getLastName());
            pstmt.setDouble(4, employee.getGrossSalary());
            pstmt.setDouble(5, employee.getNetSalary());
            pstmt.setDate(6, java.sql.Date.valueOf(employee.getHireDate()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                result = "azione completata";
            } else {
                result = "inserimento fallito";
            }
        } catch (SQLException ex) {
            result = ex.getMessage();
        }

        return result;
    }
    public String addPayLip(Employee employee, PayLip payLip) {
        String result = "";
        String sql = "INSERT INTO PayLips (taxCode, creationDate, updateDate, referenceMonth, netSalary, grossSalary) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee.getTaxCode());
            pstmt.setDate(2, java.sql.Date.valueOf(payLip.getCreationDate()));
            
            if (payLip.getUpdateDate() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(payLip.getUpdateDate()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            pstmt.setDate(4, java.sql.Date.valueOf(payLip.getReferenceMonth()));
            pstmt.setDouble(5, payLip.getNetSalary());
            pstmt.setDouble(6, payLip.getGrossSalary());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                result = "azione completata";
            } else {
                result = "inserimento fallito";
            }
        } catch (SQLException ex) {
            result = ex.getMessage();
        }

        return result;
    }
    
    public HashMap<Employee, ArrayList<PayLip>> read() throws SQLException {
        var dictionary = new HashMap<Employee, ArrayList<PayLip>>();

        String sql = """
            SELECT e.taxCode, e.firstName, e.lastName, e.grossSalary, e.netSalary, e.hireDate,
                p.id, p.creationDate, p.updateDate, p.referenceMonth, p.netSalary AS pNetSalary, p.grossSalary AS pGrossSalary
            FROM employees e
            JOIN paylips p ON e.taxCode = p.taxCode
        """;

        try (Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {
                Employee employee = Employee.getCheckedInstance(
                    result.getString("taxCode"),
                    result.getString("firstName"),
                    result.getString("lastName"),
                    result.getDouble("grossSalary"),
                    result.getDouble("netSalary"),
                    result.getDate("hireDate").toLocalDate()
                );
                PayLip payLip = new PayLip(
                    result.getInt("id"),
                    result.getString("taxCode"),
                    result.getDate("creationDate").toLocalDate(),
                    result.getDate("updateDate") != null ? result.getDate("updateDate").toLocalDate() : LocalDate.now(),
                    result.getDate("referenceMonth").toLocalDate(),
                    result.getDouble("pNetSalary"),
                    result.getDouble("pGrossSalary")
                );

                dictionary.computeIfAbsent(employee, k -> new ArrayList<>()).add(payLip);
            }
        }

        return dictionary;
    }


}
