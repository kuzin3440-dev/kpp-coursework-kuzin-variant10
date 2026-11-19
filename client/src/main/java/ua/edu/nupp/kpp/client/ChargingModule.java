package ua.edu.nupp.kpp.client;

public class ChargingModule {
    private Long id;
    private long stationId;
    private String moduleCode;
    private String connectorType;
    private double powerKw;
    private double loadScore;
    private double deficitScore;

    public ChargingModule() {
    }

    public ChargingModule(Long id,
                          long stationId,
                          String moduleCode,
                          String connectorType,
                          double powerKw,
                          double loadScore,
                          double deficitScore) {
        this.id = id;
        this.stationId = stationId;
        this.moduleCode = moduleCode;
        this.connectorType = connectorType;
        this.powerKw = powerKw;
        this.loadScore = loadScore;
        this.deficitScore = deficitScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public double getPowerKw() {
        return powerKw;
    }

    public void setPowerKw(double powerKw) {
        this.powerKw = powerKw;
    }

    public double getLoadScore() {
        return loadScore;
    }

    public void setLoadScore(double loadScore) {
        this.loadScore = loadScore;
    }

    public double getDeficitScore() {
        return deficitScore;
    }

    public void setDeficitScore(double deficitScore) {
        this.deficitScore = deficitScore;
    }
}
