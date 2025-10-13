package it.eforum;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

import it.eforum.archive.Archive;
import it.eforum.archive.Employee;
import it.eforum.archive.PayLip;

public class TUI {
    private Manager manger;
    private Scanner in;
    private Archive archive;
    public TUI(){
        try{
            manger = new Manager();
            in = new Scanner(System.in);
            archive = new Archive(manger.read());
        }catch(SQLException ex){
            System.exit(0);
        }
    }
    public void menu(){
        System.out.println("benvenuto");
        while (true) {
            int choice = -1;
            System.out.println("[0] --> aggiungi un dipendente");
            System.out.println("[1] --> aggiungi una busta paga");
            System.out.println("[2] --> stipendioi di un dipendete in un mese");
            System.out.println("[3] --> totale trattenute di un dipendente");
            System.out.println("[4] --> dipendente con lo stipendio più alto");
            System.out.println("[5] --> info");
            System.out.println("[6] --> esci");
            do{
                while (!in.hasNextInt()) in.nextLine();
                choice = in.nextInt();
            }while(choice < 0 || choice > 6);
            if(choice == 6) break;
            doChoice(choice);
        }
    }
    private void doChoice(int index){
        if(index == 0) addEmployee();
        if(index == 1) addPayLip();
        if(index == 2) getPayLipInMonth();
        if(index == 3) getWithheldInMonth();
        if(index == 4) getBigPayLip();
        if(index == 5) getInfo();
    }
    private void addEmployee() {

        try {
            System.out.print("Codice fiscale: ");
            String taxCode = in.nextLine();

            System.out.print("Nome: ");
            String firstName = in.nextLine();

            System.out.print("Cognome: ");
            String lastName = in.nextLine();

            System.out.print("RAL (stipendio lordo): ");
            double grossSalary = Double.parseDouble(in.nextLine());

            System.out.print("RAN (stipendio netto): ");
            double netSalary = Double.parseDouble(in.nextLine());

            System.out.print("Data assunzione (YYYY-MM-DD): ");
            LocalDate hireDate = LocalDate.parse(in.nextLine());

            Employee employee = Employee.getCheckedInstance(taxCode, firstName, lastName, grossSalary, netSalary, hireDate);
            String result = manger.addEmployee(employee);
            if(result.equals("azione completata")) archive.addEmployee(employee);
            System.out.println(result);
        } catch (Exception ex) {
            System.out.println("Errore: " + ex.getMessage());
        }
    }

    private void addPayLip() {

        try {
            System.out.print("Codice fiscale dipendente: ");
            int index = searchEmployee();
            if(index == -1) return;

            Employee employee = archive.getEmployeeByIndex(index);
            if (employee == null) {
                System.out.println("Dipendente non trovato.");
                return;
            }

            System.out.print("Data creazione (YYYY-MM-DD): ");
            LocalDate creationDate = LocalDate.parse(in.nextLine());

            System.out.print("Data aggiornamento (vuoto se non presente): ");
            String updateInput = in.nextLine();
            LocalDate updateDate = updateInput.isBlank() ? null : LocalDate.parse(updateInput);

            System.out.print("Mese di riferimento (YYYY-MM-DD): ");
            LocalDate referenceMonth = LocalDate.parse(in.nextLine());


            PayLip payLip = PayLip.getCheckedInstance(employee.getTaxCode(), creationDate, updateDate, referenceMonth);
            payLip.setGrossSalary(employee.getGrossSalaryInTheMonth(referenceMonth));
            payLip.setNetSalary(employee.getNetSalaryInTheMonth(referenceMonth));

            String result = manger.addPayLip(employee, payLip);
            if(result.equals("azione completata")) archive.addPayLip(employee, payLip);
            System.out.println(result);
        } catch (Exception ex) {
            System.out.println("Errore: " + ex.getMessage());
        }
    }

    private int searchEmployee(){
        System.out.println("elenco");
        int k = 0, choice = -1;
        for(Employee i: archive.getEmployees()){
            StringBuilder sb = new StringBuilder();
            sb.append("[")
                .append(Integer.toString(k++))
                .append("] -->")
                .append(i.getFirstName())
                .append(" ")
                .append(i.getLastName())
                .append(" ")
                .append("\n");
            System.out.println(sb.toString());
        }
        System.out.println(String.format("[%d] --> indietro", k++));
        do{
            while (!in.hasNextInt()) in.nextLine();
            choice = in.nextInt();
        }while(choice < 0 || choice > k);
        return choice == k ? -1 : choice;
    }
    private void getPayLipInMonth(){
        int index = searchEmployee();
        if(index == -1) return;
        Employee employee = archive.getEmployeeByIndex(index);
        try{
            System.out.print("Mese di riferimento (YYYY-MM-DD): ");
            LocalDate referenceMonth = LocalDate.parse(in.nextLine());
            System.out.println(archive.getNetOfMonthByEmployee(employee,referenceMonth));
        }catch(Exception ex){
            System.out.println("Errore" + ex.getMessage());
        }
    }
    private void getWithheldInMonth(){
        int index = searchEmployee();
        if(index == -1) return;
        Employee employee = archive.getEmployeeByIndex(index);
        try{
            System.out.print("Mese di riferimento (YYYY-MM-DD): ");
            LocalDate referenceMonth = LocalDate.parse(in.nextLine());
            System.out.println(archive.getWithheldOfMonthByEmployee(employee,referenceMonth));
        }catch(Exception ex){
            System.out.println("Errore" + ex.getMessage());
        }
    }
    private void getBigPayLip(){
        try{
            System.out.println(archive.getMaxSalary());
        }catch(Exception ex){
            System.out.println("Errore" + ex.getMessage());
        }
    }
    private void getInfo(){
        int choice = -1;
        while(true){
            while (!in.hasNextInt()) in.nextLine();
            choice = in.nextInt();
            if(choice == -1) break;
            System.out.println(archive.getPaginatedPayLips(choice));
        }
    }
}
