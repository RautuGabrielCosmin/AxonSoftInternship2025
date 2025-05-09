# AxonSoftInternship2025

## ⭐ Functional Requirements

Axon Soft needs your help in extracting some information regarding the applicants to their internship program.

The source data regarding the applicants will include, for each applicant:

1. **the name** of the applicant (first name, middle names and last name);  
2. **their email** address;  
3. **the date and time** they delivered the solution to the challenge they were given;  
4. **the score** they obtained for the submitted solution (values ranging from 0 to 10).

This data will be provided in a **CSV** (comma separated values) file similar to the example below:
name,email,delivery_datetime,score
Speranța Cruce,speranta_cruce@gmail.com,2023-01-24T20:14:53,2.33
Ionică Sergiu Ramos,chiarel@ionicaromass.ro,2023-01-24T16:32:19,9.00
Carla Ștefănescu,carlita_ste@yahoo.com,2023-01-23T23:59:01,5.20
Lucrețiu Hambare,hambare_lucretiu@outlook.com,2023-01-24T22:30:15,10
Robin Hoffman-Rus,robman@dasmail.de,2023-01-23T12:00:46,8.99


While processing the source data, some **adjustments** should be applied to the **scores**:

- If the applicant delivered the solution in the **first day**, they get **+1 whole bonus point**.  
- If the applicant delivered the solution in the **second half** of the **last day**, they get **–1 whole malus point**.

The information to be extracted is:

1. the **number of unique applicants**  
2. the **last names of the top 3 applicants**  
3. the **average score** of the **top half** before scores adjustment  

The output must be delivered in **JSON** format with the following properties:

- **uniqueApplicants**  
  – the number of unique applicants  
  – value data type: *integer*  
- **topApplicants**  
  – the last names of top 3 applicants  
  – value data type: *array of strings*  
- **averageScore**  
  – the average score of the top half *before* scores adjustment  
  – value data type: *decimal number* with at most 2 decimals; half-up rounding to be applied if needed  

---

## Clarifications

1. **Name** pattern:  FirstName [MiddleName1 MiddleName2 …] LastName
– middle names are optional, but **FirstName** and **LastName** are mandatory.  
2. **Email address**  
– constitutes a unique identifier for an applicant;  
– if the same email appears on multiple lines, those lines refer to the same applicant;  
– if an applicant appears multiple times, take the **last valid** appearance;  
– to be valid, an email must:  
  - be composed only of ASCII characters;  
  - contain only ASCII letters, digits and `@ . _ -`;  
  - start with a letter;  
  - contain `@` exactly once;  
  - contain a `.` somewhere after `@` (not immediately);  
  - end with a letter.  
3. **Delivery date and time** in local ISO format:  yyyy-MM-dd'T'HH:mm:ss
– **date** part: 4-digit year, 2-digit month, 2-digit day, separated by `-`;  
– **time** part: 2-digit hour (24h), 2-digit minutes, 2-digit seconds, separated by `:`;  
– date and time separated by capital `T`.  
4. **Score**: decimal number with up to **2** decimals.  
– integer and decimal parts separated by `.`;  
– value ≥ 0, ≤ 10.  
5. Lines that contain errors or cannot be parsed should be **ignored**.  
6. If **name**, **email**, **delivery_datetime** or **score** violate constraints, the line is invalid and must be ignored.  
7. The CSV file **might** or **might not** start with a header.  
8. **Top applicants** are those with the highest **final** score after adjustments:  
– deliver last names in **descending** order of final score;  
– ties broken by:  
  1. higher **initial** score;  
  2. earlier delivery datetime;  
  3. alphabetical order of the email.  
9. **Adjusting scores**:  
– **first day** = smallest date in the data set;  
– **last day** = largest date in the data set;  
– **second half** of a day = times ≥ `12:00:00`;  
– if all deliveries on the same day, **no** adjustments.  
10. **Average score** of the top half (before adjustment):  
 – if odd number of applicants, **top half** has the extra one.  

---

## Examples

### Example 1

**Input**  
```csv
name,email,delivery_datetime,score
Speranța Cruce,speranta_cruce@gmail.com,2023-01-24T20:14:53,2.33
Ionică Sergiu Ramos,chiarel@ionicaromass.ro,2023-01-24T16:32:19,9.00
Carla Ștefănescu,carlita_ste@yahoo.com,2023-01-23T23:59:01,5.20
Lucrețiu Hambare,hambare_lucretiu@outlook.com,2023-01-24T22:30:15,10
Robin Hoffman-Rus,robman@dasmail.de,2023-01-23T12:00:46,8.99
```
**Output**
{
  "uniqueApplicants": 5,
  "topApplicants": [
    "Hoffman-Rus",
    "Hambare",
    "Ramos"
  ],
  "averageScore": 9.33
}

### Example 2 (invalid lines filtered)


**Input**
```csv
Speranța,speranta_cruce@gmail.com,2023-01-24T20:14:53,2.33
Ionică Sergiu Ramos,chiarel@ionicaromass.ro,2023-01-24 16:32:19,9.00
Carla Ștefănescu,carlita_ste_yahoo.com,2023-01-23T23:59:01,5.20
,,,,,
Lucrețiu Hambare,hambare_lucretiu@outlook.com,2023-01-24T22:30:15,10
_)(*&^%$#@!#
Robin Hoffman-Rus,hambare_lucretiu@outlook.com,2023-01-23T12:00:46,8.99
```

**Output**
{
  "uniqueApplicants": 1,
  "topApplicants": [
    "Hoffman-Rus"
  ],
  "averageScore": 8.99
}

Technical Requirements
1.  Language: Java
2.  Mandatory class:

public class ApplicantsProcessor {
    /**
     * @param csvStream  input stream allowing to read the CSV input file
     * @return           the processing output, in JSON format
     */
    public String processApplicants(InputStream csvStream) {
        // Your implementation goes here
    }
}

3.  The class ApplicantsProcessor described above is mandatory, but it need not be the only class.
4.  The method processApplicants is mandatory, but it need not be the only method in ApplicantsProcessor.
5.  You may use any third-party library, provided it:
       -suits the purpose;
       -is open-sourced;
       -is available on Maven Central.

## 🚀 Solution description:
<!-- badges -->
![Java 23](https://img.shields.io/badge/Java-23-blue) ![License MIT](https://img.shields.io/badge/License-MIT-green) 

# 🚀 AxonSoftIntern

A clean, modular Java application that reads a CSV of internship applicants, applies business rules (score bonuses/penalties), and outputs a JSON summary: unique applicants, top 3 by score, and average score.

---

## 📋 Table of Contents
1. [🌟 Features](#-features)  
2. [🛠️ Tech Stack](#️-tech-stack)  
3. [📂 Project Structure](#-project-structure)  
4. [📐 Architecture](#-architecture)  
5. [📥 Getting Started](#-getting-started)  
6. [▶️ Usage](#️-usage)  

---

## 🌟 Features
- ✅ **CSV Parsing** with OpenCSV  
- ✅ **Validation**: name, email, date-time, score  
- 🚀 **Score Adjustments**: +1 on first day, -1 on second half of last day  
- 🔢 **Statistics**:  
  - `uniqueApplicants` count  
  - `topApplicants` (top 3 last names)  
  - `averageScore` of top half (before adjustments)  
- 📝 **Logging** via SLF4J + Logback  
- 🧪 **Example runner** with two sample CSVs  

---

## 🛠️ Tech Stack

| Layer         | Technology             |
| ------------- | ---------------------- |
| Language      | Java 23                |
| Build Tool    | Maven                  |
| CSV Parsing   | [OpenCSV 5.10](https://mvnrepository.com/artifact/com.opencsv/opencsv) |
| Logging       | SLF4J + Logback 1.5.16 |
| Testing       | (Add JUnit/TestNG)     |

---

## 📂 Project Structure

```text
AxonSoftIntern/
├─ pom.xml                   # Maven build & dependencies
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ domain/
│  │  │  │  ├─ Applicant.java
│  │  │  │  └─ Application.java
│  │  │  └─ org/example/
│  │  │     ├─ ApplicantsProcessor.java
│  │  │     └─ Main.java
│  │  └─ resources/
│  │     ├─ example1.csv
│  │     └─ example2.csv
│  └─ test/                  # (optional tests)
└─ logback.xml               # Logging configuration
```

## 📐 Architecture
flowchart LR
  A[Main] --> B[ApplicantsProcessor]
  B --> C[CSVReader (OpenCSV)]
  C -->|rows| D[buildApplication()]
  D --> E[Application Objects]
  E --> F[Validation + Dedup by email]
  F --> G[Score Adjustment]
  G --> H[Sorting & Aggregation]
  H --> I[buildJsonResult()]
  I --> Main

## 📥 Getting Started
1. Clone the repo:
    git clone https://github.com/yourusername/AxonSoftIntern.git
    cd AxonSoftIntern

2. Build with Maven:
   mvn clean package

3. Run:
   java -jar target/AxonSoftIntern-1.0-SNAPSHOT.jar
   Or within your IDE: run org.example.Main.

## ▶️ Usage
By default, Main loads example1.csv and example2.csv from src/main/resources and prints two JSON summaries:
{
  "uniqueApplicants": 5,
  "topApplicants": ["Hoffman-Rus","Hambare","Ramos"],
  "averageScore": 9.33
}

Customize by passing your own CSV path:
java -cp target/AxonSoftIntern-1.0-SNAPSHOT.jar org.example.Main path/to/your.csv
