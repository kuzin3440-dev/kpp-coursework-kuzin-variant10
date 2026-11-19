# Курсова робота: Інформаційна система обслуговування зарядних станцій електромобілів (Варіант №10)

Клієнт–серверна інформаційна система для обліку зарядних станцій електромобілів, зарядних модулів, реєстрації технічних інцидентів та планування ремонтних завдань. Система підтримує CRUD-операції через HTTP/JSON, автоматичний розрахунок інтегрального показника пріоритетності реагування, чотиристанний життєвий цикл інциденту, фоновий CSV-імпорт через JavaFX Task та модульне тестування JUnit 5.

## Технології
* **Java 21** та **Apache Maven**
* **Servlet API 4.0.1** (`javax.servlet`) та **Apache Tomcat 9.x**
* **MySQL** та **JDBC** (`mysql-connector-j 9.7.0`)
* **Jackson Databind 2.18.2** (обмін даними у форматі JSON)
* **JavaFX 21.0.2** (`javafx-controls`, `javafx.concurrent.Task`)
* **JUnit 5** (`junit-jupiter 5.11.4`)
* **Git / GitHub**

## Структура репозиторію
```text
kpp-coursework-kuzin-variant10/
├── .gitignore
├── README.md
├── database.sql
├── server/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/ua/edu/nupp/kpp/server/
│       │   └── webapp/WEB-INF/web.xml
│       └── test/
│           └── java/ua/edu/nupp/kpp/server/CourseworkTest.java
└── client/
    ├── pom.xml
    ├── sample-data/
    │   └── stations_modules.csv
    └── src/
        └── main/java/ua/edu/nupp/kpp/client/
            ├── MainApp.java
            ├── HttpChargingServiceClient.java
            └── ImportStationsModulesTask.java
```

## 1. Підготовка бази даних MySQL
1. Запустіть сервер MySQL (наприклад, через XAMPP Control Panel).
2. Відкрийте `http://localhost/phpmyadmin` та імпортуйте кореневий файл `database.sql`.
3. Переконайтеся, що створено базу даних `ev_charging_service` із таблицями `station`, `charging_module`, `incident` та `repair_task`. За потреби скоригуйте локальні параметри підключення у класі `DBUtil.java`.

## 2. Тестування та запуск серверної частини
1. Перейдіть до серверного модуля та запустіть модульні тести JUnit 5:
   ```bash
   cd server
   mvn clean test
   ```
   Очікуваний результат: `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 (BUILD SUCCESS)`.
2. Зберіть WAR-архів:
   ```bash
   mvn clean package
   ```
3. Скопіюйте файл `server/target/ev-charging-server.war` у каталог `webapps` сервера Apache Tomcat 9.x та запустіть Tomcat (`catalina.bat run`).
4. Перевірте доступність HTTP/JSON API у браузері за адресою:
   `http://localhost:8080/ev-charging-server/stations`

## 3. Запуск JavaFX-клієнта
1. Переконайтеся, що MySQL та Apache Tomcat 9.x працюють.
2. Відкрийте термінал у каталозі `client` і виконайте:
   ```bash
   cd client
   mvn javafx:run
   ```

## 4. Фоновий CSV-імпорт
* Вхідний файл розташований за відносним шляхом `client/sample-data/stations_modules.csv` (кодування UTF-8, роздільник `;`).
* Формат заголовка: `station_code;station_name;city;module_code;connector_type;power_kw;load_score;deficit_score`.
* Для запуску відкрийте вкладку «Зарядні модулі» у JavaFX-клієнті та натисніть кнопку «Імпортувати CSV». Імпорт виконується у фоновому потоці `ImportStationsModulesTask` із відображенням `ProgressBar` і текстового статусу.
* Увага: повторний імпорт того самого CSV-файлу без відновлення початкового стану бази даних створить нові записи зарядних модулів.
