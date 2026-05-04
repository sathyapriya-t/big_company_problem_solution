package com.swiss.re.big_company_problem_solution.report;

import com.swiss.re.big_company_problem_solution.model.AnalysisResult;
import com.swiss.re.big_company_problem_solution.model.ManagerSalaryIssue;
import com.swiss.re.big_company_problem_solution.model.ReportingLineIssue;
import com.swiss.re.big_company_problem_solution.model.SalaryStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReportPrinterTest {

  private static final String UNDERPAID_MANAGER_NAME = "Martin Chekov";
  private static final double UNDERPAID_ACTUAL_SALARY = 45_000;
  private static final double UNDERPAID_EXPECTED_MIN = 60_000;
  private static final double UNDERPAID_EXPECTED_MAX = 75_000;
  private static final double UNDERPAID_DEVIATION = -15_000;

  private static final String OVERPAID_MANAGER_NAME = "Big Boss";
  private static final double OVERPAID_ACTUAL_SALARY = 100_000;
  private static final double OVERPAID_EXPECTED_MIN = 48_000;
  private static final double OVERPAID_EXPECTED_MAX = 60_000;
  private static final double OVERPAID_DEVIATION = 40_000;

  private static final String DEEP_EMPLOYEE_NAME = "Deep Employee";
  private static final int DEEP_ACTUAL_DEPTH = 6;
  private static final int MAX_ALLOWED_DEPTH = 4;
  private static final int DEEP_EXCESS_LEVELS = 2;
  private final ByteArrayOutputStream outCapture = new ByteArrayOutputStream();
  private ReportPrinter printer;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
    printer = new ReportPrinter();
    originalOut = System.out;
    System.setOut(new PrintStream(outCapture));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void printTest_whenUnderpaidManager_appearsInOutput() {
    ManagerSalaryIssue underpaid = new ManagerSalaryIssue(
        UNDERPAID_MANAGER_NAME, SalaryStatus.UNDERPAID,
        UNDERPAID_ACTUAL_SALARY, UNDERPAID_EXPECTED_MIN, UNDERPAID_EXPECTED_MAX, UNDERPAID_DEVIATION);

    AnalysisResult result = new AnalysisResult(List.of(underpaid), List.of());
    printer.print(result);

    String output = outCapture.toString();
    assertThat(output).contains(UNDERPAID_MANAGER_NAME);
    assertThat(output).contains("Underpaid");
    assertThat(output).contains("15,000.00");
  }

  @Test
  void printTest_whenOverpaidManager_appearsInOutput() {
    ManagerSalaryIssue overpaid = new ManagerSalaryIssue(
        OVERPAID_MANAGER_NAME, SalaryStatus.OVERPAID,
        OVERPAID_ACTUAL_SALARY, OVERPAID_EXPECTED_MIN, OVERPAID_EXPECTED_MAX, OVERPAID_DEVIATION);

    AnalysisResult result = new AnalysisResult(List.of(overpaid), List.of());
    printer.print(result);

    String output = outCapture.toString();
    assertThat(output).contains(OVERPAID_MANAGER_NAME);
    assertThat(output).contains("Overpaid");
    assertThat(output).contains("40,000.00");
  }

  @Test
  void printTest_whenNoSalaryIssues_printsAllClearMessage() {
    AnalysisResult result = new AnalysisResult(List.of(), List.of());
    printer.print(result);

    assertThat(outCapture.toString())
        .contains("All managers have salaries within the expected range.");
  }

  @Test
  void printTest_whenLongReportingLine_appearsInOutput() {
    ReportingLineIssue issue = new ReportingLineIssue(
        DEEP_EMPLOYEE_NAME, DEEP_ACTUAL_DEPTH, MAX_ALLOWED_DEPTH, DEEP_EXCESS_LEVELS);

    AnalysisResult result = new AnalysisResult(List.of(), List.of(issue));
    printer.print(result);

    String output = outCapture.toString();
    assertThat(output).contains(DEEP_EMPLOYEE_NAME);
    assertThat(output).contains(String.valueOf(DEEP_ACTUAL_DEPTH));
    assertThat(output).contains(String.valueOf(DEEP_EXCESS_LEVELS));
  }

  @Test
  void printTest_whenNoReportingLineIssues_printsAllClearMessage() {
    AnalysisResult result = new AnalysisResult(List.of(), List.of());
    printer.print(result);

    assertThat(outCapture.toString())
        .contains("All employees have reporting lines within the allowed depth.");
  }

  @Test
  void printTest_whenCalled_alwaysIncludesBothSectionHeaders() {
    AnalysisResult result = new AnalysisResult(List.of(), List.of());
    printer.print(result);

    String output = outCapture.toString();
    assertThat(output).contains("MANAGER SALARY ANALYSIS");
    assertThat(output).contains("REPORTING LINE ANALYSIS");
  }
}