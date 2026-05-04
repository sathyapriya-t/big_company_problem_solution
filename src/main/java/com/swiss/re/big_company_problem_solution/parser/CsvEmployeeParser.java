package com.swiss.re.big_company_problem_solution.parser;

import com.swiss.re.big_company_problem_solution.model.Employee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses employee data from a comma-separated values (CSV) file.
 * The file must contain a header row with the columns Id, firstName, lastName, salary, managerId.
 * The managerId column is left blank for the CEO row.
 * Blank lines in the file are silently skipped.
 * Throws IllegalArgumentException if any data row has the wrong number of columns
 * or a non-numeric value in the id or salary fields.
 */
public class CsvEmployeeParser implements EmployeeParser {

  private static final int EXPECTED_COLUMN_COUNT = 5;

  private static final int IDX_ID = 0;
  private static final int IDX_FIRST_NAME = 1;
  private static final int IDX_LAST_NAME = 2;
  private static final int IDX_SALARY = 3;
  private static final int IDX_MANAGER_ID = 4;

  private static final String ERR_IO_READ = "Failed to read employee CSV file: ";
  private static final String ERR_COLUMN_COUNT = "Invalid CSV format at line ";
  private static final String ERR_NUMERIC_VALUE = "Invalid numeric value at line ";

  @Override
  public List<Employee> parse(String source) {
    List<Employee> employees = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
      String headerLine = reader.readLine();
      if (headerLine == null) {
        return employees;
      }

      String line;
      int lineNumber = 1;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }
        employees.add(parseLine(line, lineNumber));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(ERR_IO_READ + source, e);
    }

    return employees;
  }

  private Employee parseLine(String line, int lineNumber) {
    String[] columns = line.split(",", -1);
    if (columns.length != EXPECTED_COLUMN_COUNT) {
      throw new IllegalArgumentException(
          ERR_COLUMN_COUNT + lineNumber + ": expected " +
              EXPECTED_COLUMN_COUNT + " columns but found " + columns.length);
    }

    try {
      int id = Integer.parseInt(columns[IDX_ID].trim());
      String firstName = columns[IDX_FIRST_NAME].trim();
      String lastName = columns[IDX_LAST_NAME].trim();
      double salary = Double.parseDouble(columns[IDX_SALARY].trim());
      String managerRaw = columns[IDX_MANAGER_ID].trim();
      Integer managerId = managerRaw.isEmpty() ? null : Integer.parseInt(managerRaw);

      return new Employee(id, firstName, lastName, salary, managerId);

    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(ERR_NUMERIC_VALUE + lineNumber + ": " + line, e);
    }
  }
}