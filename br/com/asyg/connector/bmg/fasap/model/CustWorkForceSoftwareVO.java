package br.com.asyg.connector.bmg.fasap.model;

public class CustWorkForceSoftwareVO {
    private int id;
    private String externalName;
    private String dateEnd;
    private String dateStart;
    private String absenceType;
    private String absencesDays;
    private String lastModDate;
    private String absenceTypeDescription;
    private Integer integrou;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExternalName() {
        return externalName;
    }

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
    }

    public String getAbsencesDays() {
        return absencesDays;
    }

    public void setAbsencesDays(String absencesDays) {
        this.absencesDays = absencesDays;
    }

    public String getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(String lastModDate) {
        this.lastModDate = lastModDate;
    }

    public String getAbsenceTypeDescription() {
        return absenceTypeDescription;
    }

    public void setAbsenceTypeDescription(String absenceTypeDescription) {
        this.absenceTypeDescription = absenceTypeDescription;
    }

    public Integer getIntegrou() {
        return integrou;
    }

    public void setIntegrou(Integer integrou) {
        this.integrou = integrou;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (this == obj)
                return true;
            
            if (obj == null)
                return false;
            
            if (getClass() != obj.getClass())
                return false;
            
            CustWorkForceSoftwareVO other = (CustWorkForceSoftwareVO) obj;
            
            if (externalName == null) {
                if (other.externalName != null)
                    return false;
            } else if (!externalName.equals(other.externalName))
                return false;

            if (dateStart == null) {
                if (externalName.equals(other.externalName) && other.dateStart != null)
                    return false;
            } else if (externalName.equals(other.externalName) && !dateStart.equals(other.dateStart))
                return false;
            if (dateEnd == null) {
                if (externalName.equals(other.externalName) && other.dateEnd != null)
                    return false;
            } else if (externalName.equals(other.externalName) && !dateEnd.equals(other.dateEnd))
                return false;
            if (absencesDays == null) {
                if (externalName.equals(other.externalName) && other.absencesDays != null)
                    return false;
            } else if (externalName.equals(other.externalName) && !absencesDays.equals(other.absencesDays))
                return false;
            if (absenceType == null) {
                if (externalName.equals(other.externalName) && other.absenceType != null)
                    return false;
            } else if (externalName.equals(other.externalName) && !absenceType.equals(other.absenceType))
                return false;
            if (absenceType == null) {
                if (externalName.equals(other.externalName) && other.absenceTypeDescription != null)
                    return false;
            } else if (externalName.equals(other.externalName) && !absenceTypeDescription.equals(other.absenceTypeDescription))
                return false;
            if (status == null) {
                if (externalName.equals(other.externalName) && other.status != null)
                    return false;
            } else if (externalName.equals(other.externalName) && !status.equals(other.status))
                return false;
            
            return true;
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro");
        }
        return true;
    }
}
