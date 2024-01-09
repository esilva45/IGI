package br.com.asyg.connector.bmg.fasap.model;

public class PerPersonalVO {
    private int id;
    private String lastName;
    private String firstName;
    private String preferedName;
    private String gender;
    private String startDate;
    private String endDate;
    private String personIdExternal;
    private String validPersonalId;
    private Integer integrou;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPreferedName() {
        return preferedName;
    }

    public void setPreferedName(String preferedName) {
        this.preferedName = preferedName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPersonIdExternal() {
        return personIdExternal;
    }

    public void setPersonIdExternal(String personIdExternal) {
        this.personIdExternal = personIdExternal;
    }

    public Integer getIntegrou() {
        return integrou;
    }

    public void setIntegrou(Integer integrou) {
        this.integrou = integrou;
    }

    public String getValidPersonalId() {
        return validPersonalId;
    }

    public void setValidPersonalId(String validPersonalId) {
        this.validPersonalId = validPersonalId;
    }
    
    @Override
    public String toString() {
        return "PerPersonalVO id [" + id 
        		+ "] lastName [" + lastName 
        		+ "] firstName [" + firstName 
        		+ "] preferedName [" + preferedName 
        		+ "] gender [" + gender 
        		+ "] startDate [" + startDate 
        		+ "] endDate [" + endDate 
        		+ "] personIdExternal [" + personIdExternal 
        		+ "] validPersonalId [" + validPersonalId 
        		+ "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        PerPersonalVO other = (PerPersonalVO) obj;
        
        if (personIdExternal == null) {
            if (other.personIdExternal != null)
                return false;
        } else if (!personIdExternal.equals(other.personIdExternal))
            return false;
        
        return true;
    }
}
