package com.swiss.re.big_company_problem_solution;

import com.swiss.re.big_company_problem_solution.model.AnalysisResult;
import com.swiss.re.big_company_problem_solution.model.Employee;
import com.swiss.re.big_company_problem_solution.model.SalaryStatus;
import com.swiss.re.big_company_problem_solution.parser.CsvEmployeeParser;
import com.swiss.re.big_company_problem_solution.service.OrganizationAnalyzerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: wires parser + analyzer together using the sample CSV
 * from the problem statement and asserts the expected findings end-to-end.
 */
class BigCompanyApplicationTest {

  private static final String SAMPLE_CSV =
      "Id,firstName,lastName,salary,managerId\n" +
          "123,Joe,Doe,60000,\n" +
          "124,Martin,Chekov,45000,123\n" +
          "125,Bob,Ronstad,47000,123\n" +
          "300,Alice,Hasacat,50000,124\n" +
          "305,Brett,Hardleaf,34000,300\n";

  @Test
  void sampleData_martinChekovIsUnderpaid(@TempDir Path tempDir) throws IOException {
    AnalysisResult result = analyzeFromCsv(tempDir);

    assertThat(result.salaryIssues())
        .hasSize(1)
        .first()
        .satisfies(issue -> {
          assertThat(issue.managerFullName()).isEqualTo("Martin Chekov");
          assertThat(issue.status()).isEqualTo(SalaryStatus.UNDERPAID);
          assertThat(Math.abs(issue.deviation())).isEqualTo(15000.0);
        });
  }

  @Test
  void sampleData_noOverpaidManagers(@TempDir Path tempDir) throws IOException {
    AnalysisResult result = analyzeFromCsv(tempDir);

    assertThat(result.salaryIssues())
        .noneMatch(i -> i.status() == SalaryStatus.OVERPAID);
  }

  @Test
  void sampleData_noLongReportingLines(@TempDir Path tempDir) throws IOException {
    AnalysisResult result = analyzeFromCsv(tempDir);

    assertThat(result.longReportingLines()).isEmpty();
  }


  private AnalysisResult analyzeFromCsv(Path tempDir) throws IOException {
    Path csvFile = tempDir.resolve("data/employees.csv");
    Files.createDirectories(csvFile.getParent());
    Files.writeString(csvFile, SAMPLE_CSV);

    List<Employee> employees = new CsvEmployeeParser().parse(csvFile.toString());
    return new OrganizationAnalyzerImpl().analyze(employees);
  }
}
