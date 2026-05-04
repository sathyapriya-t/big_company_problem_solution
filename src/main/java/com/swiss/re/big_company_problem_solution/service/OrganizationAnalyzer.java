package com.swiss.re.big_company_problem_solution.service;

import com.swiss.re.big_company_problem_solution.model.AnalysisResult;
import com.swiss.re.big_company_problem_solution.model.Employee;

import java.util.List;

/**
 * Contract for analyzing the structure of an organization.
 * Accepts the full list of employees and returns all identified salary and
 * reporting-line issues. Keeping this as an interface decouples the analysis
 * logic from the parsing source, making it easy to test in isolation.
 */
public interface OrganizationAnalyzer {

  /**
   * Analyzes the given list of employees, which must include the CEO,
   * and returns all salary issues and reporting-line violations found.
   */
  AnalysisResult analyze(List<Employee> employees);
}

