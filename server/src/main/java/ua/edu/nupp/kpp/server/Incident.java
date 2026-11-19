package ua.edu.nupp.kpp.server;

public class Incident {
    private Long id;
    private long moduleId;
    private String description;
    private IncidentState state;
    private double f1;
    private double f2;
    private double f3;
    private double priorityIndex;

    public Incident() {
    }

    public Incident(Long id,
                    long moduleId,
                    String description,
                    IncidentState state,
                    double f1,
                    double f2,
                    double f3,
                    double priorityIndex) {
        this.id = id;
        this.moduleId = moduleId;
        this.description = description;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.priorityIndex = priorityIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentState getState() {
        return state;
    }

    public void setState(IncidentState state) {
        this.state = state;
    }

    public double getF1() {
        return f1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF2() {
        return f2;
    }

    public void setF2(double f2) {
        this.f2 = f2;
    }

    public double getF3() {
        return f3;
    }

    public void setF3(double f3) {
        this.f3 = f3;
    }

    public double getPriorityIndex() {
        return priorityIndex;
    }

    public void setPriorityIndex(double priorityIndex) {
        this.priorityIndex = priorityIndex;
    }
}
