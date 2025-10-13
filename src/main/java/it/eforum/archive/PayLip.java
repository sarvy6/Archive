package it.eforum.archive;

import java.security.PublicKey;
import java.time.LocalDate;

public class PayLip {
    protected int id;
    protected String taxCode;
    protected LocalDate creationDate, updateDate, referenceMonth;
    protected double netSalary, grossSalary;

    public static PayLip getCheckedInstance(String taxCode, LocalDate creationDate, LocalDate updateDate, LocalDate referenceMonth) {
        return new PayLip(taxCode, creationDate, updateDate, referenceMonth);
    }
    private PayLip(String taxCode, LocalDate creationDate, LocalDate updateDate, LocalDate referenceMonth) {
        setTaxCode(taxCode);
        setCreationDate(creationDate);
        setUpdateDate(updateDate);
        setReferenceMonth(referenceMonth);
    }

    public void setGrossSalary(double grossSalary){
        if(grossSalary <= 0) throw new IllegalArgumentException("salary must be poitive");
        this.grossSalary = grossSalary;
    }
    public void setNetSalary(double netSalary){
        if(netSalary <= 0) throw new IllegalArgumentException("salary must be poitive");
        this.netSalary = netSalary;
    }
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive integer");
        }
        this.id = id;
    }

    public void setTaxCode(String taxCode) {
        if (taxCode == null || taxCode.length() != 16) {
            throw new IllegalArgumentException("Tax code must be exactly 16 characters");
        }
        this.taxCode = taxCode;
    }

    public void setCreationDate(LocalDate creationDate) {
        if (creationDate == null || creationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Creation date cannot be null or in the future");
        }
        this.creationDate = creationDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        if (updateDate != null && updateDate.isBefore(this.creationDate)) {
            throw new IllegalArgumentException("Update date cannot be before creation date");
        }
        this.updateDate = updateDate;
    }

    public void setReferenceMonth(LocalDate referenceMonth) {
        if (referenceMonth == null || referenceMonth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Reference month cannot be null or in the future");
        }
        this.referenceMonth = referenceMonth;
    }
    public double getNetSalary(){return netSalary;}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; 
        if (o == null || getClass() != o.getClass()) return false;

        PayLip temp = (PayLip) o;
        return this.id == temp.id;
    }
}
