package org.example;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import domain.Application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicantsProcessor {
    private final static Logger logger = LoggerFactory.getLogger(ApplicantsProcessor.class);

    //intregul proces
    public String processAppliants(InputStream csvStream){
        logger.info("ApplicantsProcessor has been started");
        //creez o mapa cu aplicatiile din fisier.
        Map<String, Application> validApplication = readAndParseCsv(csvStream);

        //verific daca aplicatia este goala
        if(validApplication.isEmpty()){
            logger.info("There are no applicants in your csv");
            return "{\"uniqueApplicants\":0,\"topApplicants\":[],\"averageScore\":0}";
        }
        //prima aplicatie
        LocalDateTime earliestApplication = findEarliestDateApplication(validApplication.values());
        //ultima aplicatie
        LocalDateTime latestDateApplication = findLatestDateApplication(validApplication.values());

        //o variabila booleana care ne spune true daca sunt mai multe aplicatii intr-o zi sau false daca nu sunt mai
        //multe aplicatii intr-o zi
        boolean multipleDaysOfApplicationsReceived = daysOfApplicationsReceived(validApplication.values());
        if(multipleDaysOfApplicationsReceived){
            logger.info("Multiple days of applications received");
            applyBonus(validApplication.values(), earliestApplication, latestDateApplication);
        }

        List<Application> applicantsList = new ArrayList<>(validApplication.values());
        //sorteaza aplicanti
        sortApplicants(applicantsList);
        logger.info("Applicants have been sorted.");
        //media dintre prima jumatate de aplicanti
        double averageTopHalfBeforeBonusPoints = computeTopHalfBeforeBonusPoints(applicantsList);
        //cei 3 aplicanti
        List<String> top3LastName = getTop3LastNameApplicants(applicantsList);
        //JSON format file
        return buildJsonResult(
                applicantsList.size(),
                top3LastName,
                averageTopHalfBeforeBonusPoints
        );
    }

    /*
    * functia readAndParseCsv citeste fiecare linie din fisierul .csv
    * creaza o mapa care are ca si chei stringuri si ca si valori aplicatiile
    * din fisier citeste fiecare linie si verifica daca contine mai putin de 4 coloane (incepand de la 0 iterarea),
    * mai putin de 4 coloane deoarece este name(firstname,middlename,lastname),email,date,score.
     */
    public Map<String, Application> readAndParseCsv(InputStream csvStream) {
        //creaza o mapa cu aplicatiile
        Map<String, Application> validApplication = new HashMap<>();

        //citeste din fisier
        try (CSVReader reader = new CSVReader(new InputStreamReader(csvStream))) {
            logger.info("Reading CSV file");
            //face o lista din randurile fisierului.csv
            List<String[]> rows = reader.readAll();

            //itereaza prin fiecare rand
            for (int i = 0; i < rows.size(); i++) {
                String[] columns = rows.get(i);
                //verifica daca fiecare coloana contine mai putin de 4 itemi
                if (columns.length < 4) {
                    continue;
                }
                //verifica daca coloana este header daca este continua
                if (i == 0 && itsHeader(columns)) {
                    continue;
                }
                // creaza un array de string cu cele 4 coloane {name,email,date,score}
                Application app = buildApplication(columns[0], columns[1], columns[2], columns[3]);
                if (app != null) {
                    // rescrie in mapa daca emailul se repeta astfel incat sa fie un singur aplicant
                    validApplication.put(app.getEmailOfApplicant(), app);
                }
            }
        } catch (IOException | CsvException e) {
            logger.error(e.getMessage());
        }
        logger.info("Applicants have been parsed");
        return validApplication;
    }

    /*
    * aceasta functie itsHeader ia ca parametru o coloana din linia pe care o citeste din fisierul.csv, care este un array de strings
    * si returneaza true sau false daca este header row adica daca contine name,email,date,score; acestea nu sunt datele
    * unui aplicant deci va returna true daca sunt datele unui aplicant va returna false
     */
    public boolean itsHeader(String[] columns) {
        String joined = String.join(",", columns).toLowerCase();
        return joined.contains("name") && joined.contains("email")
                && joined.contains("delivery") && joined.contains("score");
    }

    /*
     * functia isValidEmail verifica daca emailul introdus este unul valid
     * returneaza true daca este valid, false daca este invalid
     */
    public boolean isValidEmail(String email) {
        //verifica daca emailul este gol sau daca primul caracter din email nu este o litera
        if (email.isEmpty() || !Character.isLetter(email.charAt(0))) {
            logger.error("Email field is invalid");
            return false;
        }
        // verifica daca exista mai multe caractere speciale @ daca da returneaza false si emailul este invalid
        if (email.chars().filter(ch -> ch == '@').count() != 1) {
            logger.error("Email field is invalid");
            return false;
        }
        int atIndex = email.indexOf('@');//gaseste indexul @
        int dotIndex = email.indexOf('.', atIndex + 1);//gaseste indexul punctului (.)
        if (dotIndex <= atIndex + 1) {//verifica ca punctul sa nu fie inainte sau imediat dupa @
            logger.error("Email field is invalid");
            return false;
        }
        //
        char lastChar = email.charAt(email.length() - 1);//ultimul caracter din email
        if (!Character.isLetter(lastChar)) {//verifica daca ultimul caracter este o litera
            logger.error("Email field is invalid");
            return false;
        }
       //
        for (char c : email.toCharArray()) {//verifica daca emailul nu contine unul dintre aceste caractere speciale returneaza false
            if (!(Character.isLetterOrDigit(c) || c == '@' || c == '.' || c == '_' || c == '-')) {
                logger.error("Email field is invalid");
                return false;
            }
        }
        logger.info("Email field is invalid");
        return true;
    }

    //functia aceasta returneaza daca aplicatia este valida si intocmeste toate conditile, cum ar fii un email valid
    //formatul datei si timpului sa fie conform, numele sa fie corect introduse.
    public Application buildApplication(String nameField, String emailField,
                                         String dateTimeField, String scoreField) {
        // verifica daca emailul este valid
        if (!isValidEmail(emailField)) {
            logger.error("Email field is invalid");
            return null;
        }

        // LocalDateTime este o clasa din java care reprezinta data si timpul impreuna
        // dateTime converteste parametrul dateTimeField intr un obiect LocalDateTime
        LocalDateTime dateTime = parseDateTime(dateTimeField);
        if (dateTime == null) {
            logger.error("Date time field is invalid");
            return null;
        }
        // foloseste T ca si separator intre data si timp din "2023-01-24", "20:14:53" va fii "2023-01-24T20:14:53"
        String datePart = dateTimeField.split("T").length > 0
                ? dateTimeField.split("T")[0] : "";
        String timePart = dateTimeField.split("T").length > 1
                ? dateTimeField.split("T")[1] : "00:00:00";

        // converteste stringul intr o variabila de tip double apleand functia parseScore
        Double score = parseScore(scoreField);
        if (score == null) {
            logger.error("Score field is invalid");
            return null;
        }

        // initiaza nameList ca o lista de stringuri
        List<String> nameList = splitName(nameField);
        if (nameList.size() < 2) {//daca lista are mai putin de 2 nume este invalida
            logger.error("Name field is invalid");
            return null;
        }
        String firstName = nameList.get(0);//primul string este prenumele
        String lastName = nameList.get(nameList.size() - 1);//ultimul stirng este numele de famile
        List<String> middle = new ArrayList<>();//restul sunt numele mijlocii
        if (nameList.size() > 2) {//daca sunt mai mult de 2 nume asta inseamna ca are un prenume
            middle = nameList.subList(1, nameList.size() - 1);//creaza o sublista si adauga prenumele dupa primul nume
        }
        logger.info("Application built successfully");
        return new Application(
                firstName, middle, lastName, emailField,
                datePart, timePart, score, 0
        );
    }

    public LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            dateTimeStr += ":00";
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Double parseScore(String scoreStr) {
        try {//converteste un string intr o variabila double
            double val = Double.parseDouble(scoreStr);
            if (val < 0.0 || val > 10.0) {//verifica daca scorul este de la 0.0 pana la 10.0
                logger.error("Score field is invalid");
                return null;
            }
            return val;
        } catch (NumberFormatException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    // functia splitName ia ca parametru tot numele si returneaza o lista de stringuri
    public List<String> splitName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        List<String> names = new ArrayList<>();
        for (String p : parts) {
            if (!p.isBlank()) {
                names.add(p);
            }
        }
        return names;
    }
    // formatul pentru afisarea datei si timpului ex."2023-01-24T20:14:53"
    public LocalDateTime combineDateTime(Application application) {
        String format = application.getDateOfDelivery() + "T" + application.getTimeOfDelivery();
        //
        return parseDateTime(format);
    }

    // aceasta functie itereaza prin toate aplicatiile si returneaza prima aplicatie care a fost trimisa
    public LocalDateTime findEarliestDateApplication(Collection<Application> applications) {
        LocalDateTime earliest = null;
        for (Application apps : applications) {
            LocalDateTime ldt = combineDateTime(apps);
            if (earliest == null || ldt.isBefore(earliest)) {
                earliest = ldt;
            }
        }
        return earliest;
    }

    // aceasta functie itereaza prin toate aplicatiile si returneaza ultima aplicatie care a fost trimisa
    public LocalDateTime findLatestDateApplication(Collection<Application> applications) {
        LocalDateTime latest = null;
        for (Application apps : applications) {
            LocalDateTime ldt = combineDateTime(apps);
            if (latest == null || ldt.isAfter(latest)) {
                latest = ldt;
            }
        }
        return latest;
    }

    // aceasta functie verifica daca aplicatiile in mai multe date diferite, adaugand aplicatiile intr-un set
    // care are proprietatea unica ca fiecare obiect sa fie unic returneaza true daca sunt aplicatiile au fost trimise
    //in mai multe date si false daca au fost trimise intr-o singura zi
    public boolean daysOfApplicationsReceived(Collection<Application> applications) {
        Set<String> distinctDates = new HashSet<>();
        for (Application apps : applications) {
            distinctDates.add(apps.getDateOfDelivery());
            if (distinctDates.size() > 1) {
                return true;
            }
        }
        return false;
    }

    // functia aceasta itereaza prin toate aplicatiile si ajusteaza bonusul pe baza date si timpului in care a fost
    // trimisa aplicatia
    public void applyBonus(Collection<Application> application,
                                    LocalDateTime earliest, LocalDateTime latest) {
        String earliestDay = earliest.toLocalDate().toString();
        String latestDay   = latest.toLocalDate().toString();

        for (Application app : application) {
            String day = app.getDateOfDelivery();
            String time = app.getTimeOfDelivery();

            if (day.equals(earliestDay)) {
                app.setBonusPoints(app.getBonusPoints() + 1);
            } else if (day.equals(latestDay) && time.compareTo("12:00:00") >= 0) {
                app.setBonusPoints(app.getBonusPoints() - 1);
            }
        }
    }

    // aceasta functie ordoneaza aplicatiile astfel incat cele mai bune sunt la inceputul listei folosindu-se de scorul
    // acordat ca si criteriu de ordonare
    public void sortApplicants(List<Application> list) {
        list.sort((a, b) -> {
            double finalA = a.getScoreOfApplication() + a.getBonusPoints();
            double finalB = b.getScoreOfApplication() + b.getBonusPoints();

            // final score descending
            int cmp = Double.compare(finalB, finalA);
            if (cmp != 0) return cmp;

            // bigger initial score
            cmp = Double.compare(b.getScoreOfApplication(), a.getScoreOfApplication());
            if (cmp != 0) return cmp;

            // earlier date/time
            LocalDateTime dtA = combineDateTime(a);
            LocalDateTime dtB = combineDateTime(b);
            cmp = dtA.compareTo(dtB);
            if (cmp != 0) return cmp;

            // alphabetical email
            return a.getEmailOfApplicant().compareToIgnoreCase(b.getEmailOfApplicant());
        });
    }

    // functia aceasta calculeaza media dintre scorurile fara puncte bonus dintre primi candidati cu cele mai mari scoruri
    public double computeTopHalfBeforeBonusPoints(List<Application> apps) {
        if (apps.isEmpty()){
            logger.warn("No applications found");
            return 0.0;
        }
        int size = apps.size();
        int halfSize = (size % 2 == 1) ? (size + 1) / 2 : size / 2;

        double sum = 0;
        for (int i = 0; i < halfSize; i++) {
            sum += apps.get(i).getScoreOfApplication();
        }
        double avg = sum / halfSize;
        return Math.round(avg * 100.0) / 100.0;
    }

    // aceasta functie returneaza ultimul nume al celor top 3 aplicanti
    public List<String> getTop3LastNameApplicants(List<Application> sortedApps) {
        List<String> top3 = new ArrayList<>();
        for (int i = 0; i < 3 && i < sortedApps.size(); i++) {
            top3.add(sortedApps.get(i).getLastNameOfApplicant());
        }
        return top3;
    }

    // functia aceasta creaza formatul pentru JSON file.
    public String buildJsonResult(int uniqueApplicants,
                                   List<String> topLastNames,
                                   double averageScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"uniqueApplicants\": ").append(uniqueApplicants).append(",\n");
        sb.append("  \"topApplicants\": [");
        for (int i = 0; i < topLastNames.size(); i++) {
            sb.append("\"").append(topLastNames.get(i)).append("\"");
            if (i < topLastNames.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("],\n");
        sb.append("  \"averageScore\": ").append(String.format("%.2f", averageScore)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}