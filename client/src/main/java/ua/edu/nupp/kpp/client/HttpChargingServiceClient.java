package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpChargingServiceClient {
    private static final String BASE = "http://localhost:8080/ev-charging-server";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // --- Station CRUD ---
    public List<Station> getStations() throws IOException, InterruptedException {
        return mapper.readValue(get("/stations"), new TypeReference<List<Station>>() {});
    }

    public void addStation(Station s) throws IOException, InterruptedException {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", "add");
        p.put("code", s.getCode());
        p.put("name", s.getName());
        p.put("city", s.getCity());
        post("/stations", p);
    }

    public void updateStation(Station s) throws IOException, InterruptedException {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", "update");
        p.put("id", String.valueOf(s.getId()));
        p.put("code", s.getCode());
        p.put("name", s.getName());
        p.put("city", s.getCity());
        post("/stations", p);
    }

    public void deleteStation(long id) throws IOException, InterruptedException {
        post("/stations", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    // --- ChargingModule CRUD ---
    public List<ChargingModule> getModules() throws IOException, InterruptedException {
        return mapper.readValue(get("/modules"), new TypeReference<List<ChargingModule>>() {});
    }

    public void addModule(ChargingModule m) throws IOException, InterruptedException {
        post("/modules", moduleParams("add", m));
    }

    public void updateModule(ChargingModule m) throws IOException, InterruptedException {
        Map<String, String> p = moduleParams("update", m);
        p.put("id", String.valueOf(m.getId()));
        post("/modules", p);
    }

    public void deleteModule(long id) throws IOException, InterruptedException {
        post("/modules", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    private Map<String, String> moduleParams(String action, ChargingModule m) {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", action);
        p.put("stationId", String.valueOf(m.getStationId()));
        p.put("moduleCode", m.getModuleCode());
        p.put("connectorType", m.getConnectorType());
        p.put("powerKw", String.valueOf(m.getPowerKw()));
        p.put("loadScore", String.valueOf(m.getLoadScore()));
        p.put("deficitScore", String.valueOf(m.getDeficitScore()));
        return p;
    }

    // --- Incident CRUD & Lifecycle ---
    public List<Incident> getIncidents() throws IOException, InterruptedException {
        return mapper.readValue(get("/incidents"), new TypeReference<List<Incident>>() {});
    }

    public void addIncident(long moduleId, String description, double f2) throws IOException, InterruptedException {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", "add");
        p.put("moduleId", String.valueOf(moduleId));
        p.put("description", description);
        p.put("f2", String.valueOf(f2));
        post("/incidents", p);
    }

    public void updateIncident(long id, long moduleId, String description, double f2) throws IOException, InterruptedException {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", "update");
        p.put("id", String.valueOf(id));
        p.put("moduleId", String.valueOf(moduleId));
        p.put("description", description);
        p.put("f2", String.valueOf(f2));
        post("/incidents", p);
    }

    public void changeIncidentState(long id, IncidentState state) throws IOException, InterruptedException {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", "transition");
        p.put("id", String.valueOf(id));
        p.put("state", state.name());
        post("/incidents", p);
    }

    public void deleteIncident(long id) throws IOException, InterruptedException {
        post("/incidents", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    // --- RepairTask CRUD ---
    public List<RepairTask> getTasks() throws IOException, InterruptedException {
        return mapper.readValue(get("/tasks"), new TypeReference<List<RepairTask>>() {});
    }

    public void addTask(RepairTask t) throws IOException, InterruptedException {
        post("/tasks", taskParams("add", t));
    }

    public void updateTask(RepairTask t) throws IOException, InterruptedException {
        Map<String, String> p = taskParams("update", t);
        p.put("id", String.valueOf(t.getId()));
        post("/tasks", p);
    }

    public void deleteTask(long id) throws IOException, InterruptedException {
        post("/tasks", Map.of("action", "delete", "id", String.valueOf(id)));
    }

    private Map<String, String> taskParams(String action, RepairTask t) {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("action", action);
        p.put("incidentId", String.valueOf(t.getIncidentId()));
        p.put("description", t.getDescription());
        p.put("plannedDate", t.getPlannedDate());
        return p;
    }

    // --- HTTP Helpers ---
    private String get(String path) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .GET()
                .build();
        return send(req);
    }

    private String post(String endpoint, Map<String, String> params) throws IOException, InterruptedException {
        StringBuilder body = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                body.append("&");
            }
            body.append(enc(entry.getKey())).append("=").append(enc(entry.getValue()));
            first = false;
        }
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + endpoint))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
        return send(req);
    }

    private String send(HttpRequest req) throws IOException, InterruptedException {
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            String errMsg = "HTTP " + resp.statusCode();
            try {
                JsonNode node = mapper.readTree(resp.body());
                if (node.has("error")) {
                    errMsg = node.get("error").asText();
                }
            } catch (Exception ignored) {
                errMsg = resp.body();
            }
            throw new IOException(errMsg);
        }
        return resp.body();
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
