package it.eforum;

import it.eforum.entity.Employee;
import it.eforum.entity.PaySlip;
import it.eforum.entity.Response;
import it.eforum.manager.Manager;
import it.eforum.manager.TUI;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TUITest {

    static TUI tui;
    static Employee employee;
    static PaySlip paySlip;

    @BeforeAll
    static void setup() {
        tui = new TUI();

        employee = new Employee();
        employee.setFirstName("Mario");
        employee.setLastName("Rossi");

        paySlip = new PaySlip();
        paySlip.setEmployee(employee);
        paySlip.setGrossSalary(BigDecimal.valueOf(3000));
        paySlip.setNetSalary(BigDecimal.valueOf(2400));
        paySlip.setCreationDate(LocalDateTime.now());
        paySlip.setModificationDate(LocalDateTime.now());
    }

    @Test
    @Order(1)
    void testCreateEmployee() {
        Response<Employee> response = tui.createEmployee(employee);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData().getId());
        assertEquals("Mario", response.getData().getFirstName());
    }

    @Test
    @Order(2)
    void testEditEmployee() {
        employee.setLastName("Bianchi");
        Response<Employee> response = tui.editEmployee(employee);
        assertTrue(response.isSuccess());
        assertEquals("Bianchi", response.getData().getLastName());
    }

    @Test
    @Order(3)
    void testCreatePaySlip() {
        Response<PaySlip> response = tui.createPaySlip(paySlip);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());

        BigDecimal expectedNet = BigDecimal.valueOf(2400);
        BigDecimal actualNet = response.getData().getNetSalary();
        assertEquals(0, expectedNet.compareTo(actualNet), "NetSalary should be 2400");
    }

    @Test
    @Order(4)
    void testShowEmployees() {
        Response<List<Employee>> response = tui.showEmployees();
        assertTrue(response.isSuccess());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    @Order(5)
    void testCalculateNetSalary() {
        Response<BigDecimal> response = tui.calculateNetSalary(employee);
        assertTrue(response.isSuccess());

        BigDecimal expected = BigDecimal.valueOf(2400);
        BigDecimal actual = response.getData();
        assertEquals(0, expected.compareTo(actual), "Calculated net salary mismatch");
    }

    @Test
    @Order(6)
    void testWithheld() {
        Response<BigDecimal> response = tui.withheld(employee);
        assertTrue(response.isSuccess());

        BigDecimal expected = BigDecimal.valueOf(600); // 3000 - 2400
        BigDecimal actual = response.getData();
        assertEquals(0, expected.compareTo(actual), "Withheld amount mismatch");
    }

    @Test
    @Order(7)
    void testMaxPaySlipSalary() {
        Response<PaySlip> response = tui.maxPaySlipSalary();
        assertTrue(response.isSuccess());

        BigDecimal expectedNet = BigDecimal.valueOf(2400);
        BigDecimal actualNet = response.getData().getNetSalary();
        assertEquals(0, expectedNet.compareTo(actualNet), "Max net salary mismatch");
    }

    @Test
    @Order(8)
    void testGetPage() {
        Response<List<Employee>> response = tui.getPage(1);
        assertTrue(response.isSuccess());
        assertFalse(response.getData().isEmpty());
    }

    @AfterAll
    static void cleanup() {
        Manager.shutdown();
    }
}
