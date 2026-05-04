# Big Company Problem - Organizational Structure Analyzer

## Problem Statement

**Code Exercise 106**

Big Company employs a large number of employees and wants to analyze its organizational
structure to identify potential improvements. The board wants to ensure:

1. **Underpaid managers** - every manager earns at least 20% more than the average salary
   of their direct subordinates, and reports how much short they fall.
2. **Overpaid managers** - no manager earns more than 50% more than that average,
   and reports how much excess they receive.
3. **Deep reporting lines** - employees who have more than 4 managers between themselves
   and the CEO are flagged, along with how many levels too deep they sit.

---

## Tech Stack

| Technology | Version | Role                          |
|------------|---------|-------------------------------|
| Java SE    | 21      | Language & runtime            |
| Maven      | Wrapper | Build & dependency management |
| JUnit 5    | 5.11.0  | Unit & integration tests      |
| AssertJ    | 3.26.3  | Fluent test assertions        |

> No Spring/SpringBoot, no Lombok, no external runtime dependencies - pure Java SE(21) as required.

---

## Project Structure

```
src/
├── main/
│   ├── java/com/swiss/re/big_company_solution_java/
│   │   ├── BigCompanyApplication.java          ← main() entry point
│   │   ├── model/
│   │   │   ├── Employee.java                   ← Immutable record (id, name, salary, managerId)
│   │   │   ├── SalaryStatus.java               ← Enum: UNDERPAID / OVERPAID
│   │   │   ├── AnalysisResult.java             ← Record aggregating all findings
│   │   │   ├── ManagerSalaryIssue.java         ← Record for a salary violation
│   │   │   └── ReportingLineIssue.java         ← Record for a depth violation
│   │   ├── parser/
│   │   │   ├── EmployeeParser.java             ← Interface (extensibility hook)
│   │   │   └── CsvEmployeeParser.java          ← CSV file implementation
│   │   ├── service/
│   │   │   ├── OrganizationAnalyzer.java       ← Interface (business contract)
│   │   │   └── OrganizationAnalyzerImpl.java   ← Core analysis logic
│   │   └── report/
│   │       └── ReportPrinter.java              ← Formats and prints to console
│   └── resources/
│       └── data/
│           ├── employees.csv                   ← Bundled sample data
│           └── employees-covering-all-scenarios.csv ← Additional sample covering all scenarios
└── test/
    └── java/com/swiss/re/big_company_solution_java/
        ├── BigCompanyApplicationTest.java       ← Integration test (3 tests)
        ├── parser/
        │   └── CsvEmployeeParserTest.java       ← Unit tests (7 tests)
        ├── report/
        │   └── ReportPrinterTest.java           ← Unit tests (6 tests)
        └── service/
            └── OrganizationAnalyzerTest.java    ← Unit tests (9 tests)
```

---

## Assumptions


**1. No frameworks or code-generation libraries are used**

The problem statement explicitly requires pure Java SE and JUnit only. As a result:
- **Spring / Spring Boot** - not used; there is no application context or dependency injection container. Classes are wired together directly in `BigCompanyApplication.main()`.
- **Lombok** - not used; records and standard Java constructs replace all boilerplate (getters, constructors, equals/hashCode).
- **Mockito** - not used; unit tests instantiate real classes directly and pass in plain `List` data instead of mocks.
- **Any other runtime dependency** - not present; the final JAR contains only the application classes.

---

**2. Exactly one CEO - the employee with a blank `managerId`**

The CSV has no explicit "role" column, so the CEO is identified purely by having no manager.
The assumption is that exactly one such row exists. If zero or multiple blank `managerId` rows
appeared, the analysis throws an `IllegalArgumentException` immediately with a clear message
rather than silently producing wrong results.

---

**3. The org chart is a valid tree - all `managerId` values reference existing employees**

Every employee (except the CEO) must point to a manager who actually exists in the data,
and no circular relationships are present (e.g. A reports to B and B reports to A).
The input is assumed to represent a clean, real-world reporting hierarchy where every
chain of managers eventually leads up to the CEO and stops there.

---

**4. Managers with zero direct subordinates are skipped for salary checks**

A salary check compares a manager's pay against the average salary of their direct reports.
If a manager has no direct reports there is no average to compute, so there is nothing to
compare against. Flagging such a person as underpaid or overpaid would be meaningless.
These leaf nodes are silently skipped.

---

**5. The CSV always has a header row**

The first line (`Id,firstName,lastName,salary,managerId`) is always present and is skipped
during parsing. If the file is completely empty (not even a header) the parser returns an
empty list. This avoids treating the header as an employee record.

---

**6. The `managerId` column is blank (not absent) for the CEO - all rows always have exactly 5 columns**

The parser splits each line on commas and expects exactly 5 fields. The CEO row ends with a
trailing comma (`123,Joe,Doe,60000,`) so the 5th field is an empty string, which is then
parsed as `null` for `managerId`. Any row with fewer or more than 5 columns throws an
`IllegalArgumentException`.

---

**7. Up to 1000 employees - in-memory processing is appropriate**

The entire employee list is loaded into memory at once and stored in two HashMaps (one indexed
by `id`, one grouped by `managerId`). For up to 1000 employees this is fast and uses
negligible memory. If the dataset were millions of rows, a streaming or database-backed approach
would be needed instead. E.g. OpenCSV

---


## Sample CSV Input

```
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
300,Alice,Hasacat,50000,124
305,Brett,Hardleaf,34000,300
```

---

## Sample Console Output

```
Loaded 5 employees from: src/main/resources/data/employees.csv

============================================================
  MANAGER SALARY ANALYSIS
============================================================

  Underpaid Managers (earn less than 20% above subordinate average):
  - Martin Chekov           | Actual: 45,000.00 | Should be at least: 60,000.00 | Shortfall: 15,000.00

============================================================
  REPORTING LINE ANALYSIS
============================================================
  All employees have reporting lines within the allowed depth.
============================================================
```

**Why Martin Chekov is underpaid:**
- Direct subordinate: Alice Hasacat (salary = 50,000)
- Subordinate average = 50,000
- Required minimum = 50,000 × 1.20 = **60,000**
- Martin earns 45,000 → shortfall = **15,000**

---

Org hierarchy:

```
CEO — Joe Doe (60,000)
 ├── Martin Chekov (45,000)
 │    └── Alice Hasacat (50,000)
 │         └── Brett Hardleaf (34,000)
 └── Bob Ronstad (47,000)
```
