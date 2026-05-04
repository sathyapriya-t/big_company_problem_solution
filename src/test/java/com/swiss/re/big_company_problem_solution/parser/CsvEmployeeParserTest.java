package com.swiss.re.big_company_problem_solution.parser;

import com.swiss.re.big_company_problem_solution.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvEmployeeParserTest {

  private static final String EMPLOYEES_CSV = "data/employees.csv";
  private static final String CSV_HEADER = "Id,firstName,lastName,salary,managerId";
  private static final String CEO_ROW = "123,Joe,Doe,60000,";
  private static final String EMPLOYEE_ROW = "124,Martin,Chekov,45000,123";

  private static final int EMP_ID = 124;
  private static final String EMP_FIRST_NAME = "Martin";
  private static final String EMP_LAST_NAME = "Chekov";
  private static final double EMP_SALARY = 45000.0;
  private static final int EMP_MANAGER_ID = 123;

  private CsvEmployeeParser parser;

  @BeforeEach
  void setUp() {
    parser = new CsvEmployeeParser();
  }

  @Test
  void parseTest_whenValidCsv_returnsAllEmployees(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER, CEO_ROW, EMPLOYEE_ROW);

    List<Employee> employees = parser.parse(csv.toString());

    assertThat(employees).hasSize(2);
  }

  @Test
  void parseTest_whenCeoRow_hasNullManagerId(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER, CEO_ROW);

    Employee ceo = parser.parse(csv.toString()).getFirst();

    assertThat(ceo.managerId()).isNull();
  }

  @Test
  void parseTest_whenEmployeeWithManagerId_parsedCorrectly(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER, EMPLOYEE_ROW);

    Employee emp = parser.parse(csv.toString()).getFirst();

    assertThat(emp.id()).isEqualTo(EMP_ID);
    assertThat(emp.firstName()).isEqualTo(EMP_FIRST_NAME);
    assertThat(emp.lastName()).isEqualTo(EMP_LAST_NAME);
    assertThat(emp.salary()).isEqualTo(EMP_SALARY);
    assertThat(emp.managerId()).isEqualTo(EMP_MANAGER_ID);
  }

  @Test
  void parseTest_whenEmptyFile_returnsEmptyList(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER);

    List<Employee> result = parser.parse(csv.toString());

    assertThat(result).isEmpty();
  }

  @Test
  void parseTest_whenBlankLinesPresent_skipsBlankLines(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER, CEO_ROW, "", EMPLOYEE_ROW);

    assertThat(parser.parse(csv.toString())).hasSize(2);
  }

  @Test
  void parseTest_whenInvalidColumnCount_throwsException(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER, "123,Joe,Doe,60000");

    assertThatThrownBy(() -> parser.parse(csv.toString()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid CSV format");
  }

  @Test
  void parseTest_whenInvalidSalary_throwsException(@TempDir Path tempDir) throws IOException {
    Path csv = createCsv(tempDir, CSV_HEADER, "123,Joe,Doe,notANumber,");

    assertThatThrownBy(() -> parser.parse(csv.toString()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid numeric value");
  }

  private Path createCsv(Path dir, String... lines) throws IOException {
    Path file = dir.resolve(EMPLOYEES_CSV);
    Files.createDirectories(file.getParent());
    Files.write(file, List.of(lines));
    return file;
  }
}

