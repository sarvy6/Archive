package it.eforum.archive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Archive {
    private HashMap<Employee, ArrayList<PayLip>> dictionary;
    public Archive(HashMap<Employee, ArrayList<PayLip>> dictionary){
        this.dictionary = dictionary;
    }
    public void addEmployee(Employee item){
        dictionary.put(item, new ArrayList<>());
    }
    public String addPayLip(Employee employee, PayLip payLip){
        var list = dictionary.get(employee);
        if(list == null) return "dipemdente non trovato";
        list.add(payLip);
        dictionary.put(employee, list);
        return null;
    }
    public String getNetOfYearByEmployee(Employee employee, LocalDate date){
        double salary =  employee.getNetSalaryInTheMonth(date);
        return String.format("%s %s ha lo stipendio nel mese %d dell'anno %d di %f",employee.firstName,
         employee.lastName, date.getMonthValue(),date.getYear(), salary);
    }
    public String getWithheldByEmployee(Employee employee, LocalDate date){
        double salary = employee.getGrossSalaryInTheMonth(date) - employee.getNetSalaryInTheMonth(date);
        return String.format("%s %s ha la trattenuta nel mese %d dell'anno %d di %f",employee.firstName,
             employee.lastName, date.getMonthValue(),date.getYear(), salary);
    }
    public String[] getMaxSalaryData(Employee employee, LocalDate date){
        return new String[]{getMaxSalary(), getMaxSalaryOfEmployeebyMonth(employee, date)};
    }
    public String getMaxSalary(){
        Employee employee = dictionary.keySet()
            .stream()
            .max(Comparator.comparingDouble(Employee::getNetSalary))
            .orElse(null);
        return employee == null ? "nessun dipendente registrato" : String.format("Il dipendente %s $s è quello con lo stipendio più alto, di: %f",
            employee.firstName, employee.lastName, employee.netSalary);
    }
    public String getMaxSalaryOfEmployeebyMonth(Employee employee, LocalDate date){
        var list = dictionary.get(employee);
        PayLip result = list.stream().max(Comparator.comparingDouble(PayLip::getNetSalary)).orElse(null);
        return result == null ? "Nessun Busta disponibile per l'utente" : String.format("Il dipendente %s $s ha come busta paga la più alta di: %f",
            employee.firstName, employee.lastName, result.netSalary);
    }
    public Employee getEmployeeByIndex(int index){
        var list = dictionary.keySet();
        if(list.size() < index) return null;
        return list.toArray(new Employee[0])[index];
    }
    public String getPaginatedPayLips(int index) {
    var list = new ArrayList<>(dictionary.values());
    
    if (index * 10 >= list.size()) return "";

    PayLip[] result = list.stream()
        .skip(index * 10L)
        .limit(10)
        .toArray(PayLip[]::new);

    StringBuilder sb = new StringBuilder();
    
    for (PayLip i : result) {
        Employee employee = getEmployeeByCF(i.taxCode);
        if (employee != null) {
            sb.append(employee.firstName)
              .append(" ")
              .append(employee.lastName)
              .append(" ")
              .append(String.format("%.2f", employee.getNetSalary()))
              .append("\n");
        }
    }

    return sb.toString();
}

    public Employee getEmployeeByCF(String cf) {
        return dictionary.keySet().stream()
            .filter(e -> e.getTaxCode().equalsIgnoreCase(cf))
            .findFirst()
            .orElse(null);
    }

}
