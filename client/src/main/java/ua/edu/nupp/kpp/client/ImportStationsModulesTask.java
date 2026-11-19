package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportStationsModulesTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportStationsModulesTask.class.getName());
    private static final String EXPECTED_HEADER =
            "station_code;station_name;city;module_code;connector_type;power_kw;load_score;deficit_score";

    private final Path file;
    private final HttpChargingServiceClient api;

    public ImportStationsModulesTask(Path file, HttpChargingServiceClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Початок фонового CSV-імпорту: " + file.getFileName());
        updateMessage("Читання CSV-файлу...");

        if (!Files.exists(file) || !Files.isReadable(file)) {
            throw new IllegalArgumentException("CSV-файл не знайдено або він недоступний: " + file);
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("CSV-файл порожній");
        }

        String header = lines.get(0).replace("\uFEFF", "").strip();
        if (!EXPECTED_HEADER.equals(header)) {
            throw new IllegalArgumentException("Некоректний заголовок CSV-файлу");
        }

        int total = (int) lines.stream().skip(1).filter(s -> !s.isBlank()).count();
        if (total == 0) {
            updateProgress(1, 1);
            updateMessage("Немає рядків для імпорту");
            LOG.info("CSV-файл містить лише заголовок, імпортовано: 0");
            return 0;
        }

        int processed = 0;
        int imported = 0;
        updateProgress(0, total);

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                LOG.warning("Імпорт скасовано користувачем на рядку " + (i + 1));
                break;
            }

            String rawLine = lines.get(i);
            if (rawLine.isBlank()) {
                continue;
            }

            String[] p = rawLine.split(";", -1);
            if (p.length != 8) {
                LOG.warning("Пропущено рядок " + (i + 1) + ": очікується 8 стовпців, отримано " + p.length);
                updateProgress(++processed, total);
                updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано " + imported);
                continue;
            }

            try {
                String stCode = p[0].strip();
                String stName = p[1].strip();
                String city = p[2].strip();
                String modCode = p[3].strip();
                String connType = p[4].strip();
                double powerKw = Double.parseDouble(p[5].strip());
                double loadScore = Double.parseDouble(p[6].strip());
                double deficitScore = Double.parseDouble(p[7].strip());

                if (!Double.isFinite(powerKw) || !Double.isFinite(loadScore) || !Double.isFinite(deficitScore)) {
                    LOG.warning("Некоректне числове значення (NaN/Infinity) у рядку " + (i + 1));
                    throw new IllegalArgumentException("Некоректне числове значення у рядку " + (i + 1));
                }

                Long stationId = findStationIdByCode(stCode);
                if (stationId == null) {
                    api.addStation(new Station(null, stCode, stName, city));
                    stationId = findStationIdByCode(stCode);
                }

                if (stationId == null) {
                    throw new IllegalStateException("Не вдалося визначити ID станції для коду " + stCode);
                }

                ChargingModule module = new ChargingModule(
                        null, stationId, modCode, connType, powerKw, loadScore, deficitScore
                );
                api.addModule(module);
                imported++;
            } catch (NumberFormatException e) {
                LOG.warning("Некоректне число у рядку " + (i + 1) + ": " + e.getMessage());
                throw new IllegalArgumentException("Некоректний числовий формат у рядку " + (i + 1));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Помилка імпорту рядка " + (i + 1), e);
                throw e;
            }

            updateProgress(++processed, total);
            updateMessage("Опрацьовано " + processed + " із " + total + "; імпортовано " + imported);
        }

        LOG.info("Фоновий CSV-імпорт завершено. Імпортовано записів: " + imported);
        return imported;
    }

    private Long findStationIdByCode(String code) throws Exception {
        for (Station s : api.getStations()) {
            if (s.getCode().equalsIgnoreCase(code)) {
                return s.getId();
            }
        }
        return null;
    }
}
