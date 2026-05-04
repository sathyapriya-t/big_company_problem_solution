package com.swiss.re.big_company_problem_solution.service;

import com.swiss.re.big_company_problem_solution.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OrganizationAnalyzerTest {

  private static final String CEO_FULL_NAME = "Joe Doe";
  private static final String MARTIN_FULL_NAME = "Martin Chekov";
  private static final String BRETT_FULL_NAME = "Brett Hardleaf";

  private static final double MARTIN_ACTUAL_SALARY = 45000.0;
  private static final double MARTIN_EXPECTED_MIN = 60000.0;
  private static final double MARTIN_DEVIATION = 15000.0;

  private static final String BIG_BOSS_FULL_NAME = "Big Boss";
  private static final double BIG_BOSS_DEVIATION = 40000.0;

  private static final String DEEP_EMPLOYEE_FULL_NAME = "Deep F";
  private static final int DEEP_ACTUAL_DEPTH = 5;
  private static final int DEEP_EXCESS = 1;

  private OrganizationAnalyzerImpl analyzer;

  @BeforeEach
  void setUp() {
    analyzer = new OrganizationAnalyzerImpl();
  }

  @Test
  void analyzeTest_whenManagerSalaryInRange_noSalaryIssues() {
    AnalysisResult result = analyzer.analyze(sampleEmployees());

    assertThat(result.salaryIssues())
        .noneMatch(i -> i.managerFullName().equals(CEO_FULL_NAME));
  }

  @Test
  void analyzeTest_whenManagerEarnsLessThan20PercentAboveAvg_isUnderpaid() {
    AnalysisResult result = analyzer.analyze(sampleEmployees());

    ManagerSalaryIssue issue = result.salaryIssues().stream()
        .filter(i -> i.managerFullName().equals(MARTIN_FULL_NAME))
        .findFirst()
        .orElseThrow();

    assertThat(issue.status()).isEqualTo(SalaryStatus.UNDERPAID);
    assertThat(issue.actualSalary()).isEqualTo(MARTIN_ACTUAL_SALARY);
    assertThat(issue.expectedMinSalary()).isEqualTo(MARTIN_EXPECTED_MIN);
    assertThat(Math.abs(issue.deviation())).isEqualTo(MARTIN_DEVIATION);
  }

  @Test
  void analyzeTest_whenManagerEarnsMoreThan50PercentAboveAvg_isOverpaid() {
    List<Employee> employees = List.of(
        emp(1, "Big", "Boss", 100000, null),
        emp(2, "Low", "Earner", 40000, 1)
    );

    AnalysisResult result = analyzer.analyze(employees);

    assertThat(result.salaryIssues()).hasSize(1)
        .first()
        .satisfies(issue -> {
          assertThat(issue.managerFullName()).isEqualTo(BIG_BOSS_FULL_NAME);
          assertThat(issue.status()).isEqualTo(SalaryStatus.OVERPAID);
          assertThat(issue.deviation()).isEqualTo(BIG_BOSS_DEVIATION);
        });
  }

  @Test
  void analyzeTest_whenLeafEmployee_notInSalaryIssues() {
    AnalysisResult result = analyzer.analyze(sampleEmployees());

    assertThat(result.salaryIssues())
        .noneMatch(i -> i.managerFullName().equals(BRETT_FULL_NAME));
  }

  @Test
  void analyzeTest_whenEmptyEmployeeList_returnsEmptyResult() {
    AnalysisResult result = analyzer.analyze(List.of());

    assertThat(result.salaryIssues()).isEmpty();
    assertThat(result.longReportingLines()).isEmpty();
  }

  @Test
  void analyzeTest_whenEmployeeDepthIsFour_notFlagged() {
    List<Employee> employees = List.of(
        emp(1, "CEO", "A", 100000, null),
        emp(2, "Mgr1", "B", 80000, 1),
        emp(3, "Mgr2", "C", 70000, 2),
        emp(4, "Mgr3", "D", 60000, 3),
        emp(5, "Emp", "E", 50000, 4)
    );

    assertThat(analyzer.analyze(employees).longReportingLines()).isEmpty();
  }

  @Test
  void analyzeTest_whenEmployeeDepthIsFive_isFlagged() {
    List<Employee> employees = List.of(
        emp(1, "CEO", "A", 100000, null),
        emp(2, "Mgr1", "B", 80000, 1),
        emp(3, "Mgr2", "C", 70000, 2),
        emp(4, "Mgr3", "D", 60000, 3),
        emp(5, "Mgr4", "E", 50000, 4),
        emp(6, "Deep", "F", 40000, 5)
    );

    AnalysisResult result = analyzer.analyze(employees);

    assertThat(result.longReportingLines()).hasSize(1);
    ReportingLineIssue issue = result.longReportingLines().getFirst();
    assertThat(issue.employeeFullName()).isEqualTo(DEEP_EMPLOYEE_FULL_NAME);
    assertThat(issue.actualDepth()).isEqualTo(DEEP_ACTUAL_DEPTH);
    assertThat(issue.excess()).isEqualTo(DEEP_EXCESS);
  }

  @Test
  void analyzeTest_whenSampleData_brettDepthThreeNotFlagged() {
    assertThat(analyzer.analyze(sampleEmployees()).longReportingLines()).isEmpty();
  }

  @Test
  void analyzeTest_whenCeoAlone_noIssues() {
    List<Employee> employees = List.of(emp(1, "CEO", "Only", 100000, null));
    AnalysisResult result = analyzer.analyze(employees);

    assertThat(result.salaryIssues()).isEmpty();
    assertThat(result.longReportingLines()).isEmpty();
  }

  private Employee emp(int id, String first, String last, double salary, Integer managerId) {
    return new Employee(id, first, last, salary, managerId);
  }

  private List<Employee> sampleEmployees() {
    return List.of(
        emp(123, "Joe", "Doe", 60000, null),
        emp(124, "Martin", "Chekov", 45000, 123),
        emp(125, "Bob", "Ronstad", 47000, 123),
        emp(300, "Alice", "Hasacat", 50000, 124),
        emp(305, "Brett", "Hardleaf", 34000, 300)
    );
  }

    @Test
    void analyzeTest_whenNoCeo_throwsIllegalArgumentException() {
        List<Employee> employees = List.of(
                emp(1, "No",  "Ceo",     50000, 2),
                emp(2, "Also", "NoCeo",  60000, 1)
        );

        assertThatThrownBy(() -> analyzer.analyze(employees))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected exactly one CEO")
                .hasMessageContaining("0");
    }

    @Test
    void analyzeTest_whenMultipleCeos_throwsIllegalArgumentException() {
        List<Employee> employees = List.of(
                emp(1, "First",  "Ceo", 60000, null),
                emp(2, "Second", "Ceo", 55000, null),
                emp(3, "Employee", "A", 40000, 1)
        );

        assertThatThrownBy(() -> analyzer.analyze(employees))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expected exactly one CEO")
                .hasMessageContaining("2");
    }
}