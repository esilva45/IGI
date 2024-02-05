package br.com.asyg.connector.bmg.fasap.model;

public class EmploymentNavVO {
    private String perPersonal;
    private String validPerPersonal;
    private String startDate;
    private String endDate;

    public String getPerPersonal() {
        return perPersonal;
    }

    public void setPerPersonal(String perPersonal) {
        this.perPersonal = perPersonal;
    }

    public String getValidPerPersonal() {
        return validPerPersonal;
    }

    public void setValidPerPersonal(String validPerPersonal) {
        this.validPerPersonal = validPerPersonal;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        EmploymentNavVO other = (EmploymentNavVO) obj;
        
        if (perPersonal == null) {
            if (other.perPersonal != null)
                return false;
        } else if (!perPersonal.equals(other.perPersonal))
            return false;
        
        return true;
    }
}
