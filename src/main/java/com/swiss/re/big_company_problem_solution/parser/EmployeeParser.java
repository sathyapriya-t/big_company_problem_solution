package com.swiss.re.big_company_problem_solution.parser;

import com.swiss.re.big_company_problem_solution.model.Employee;

import java.util.List;

/**
 * Contract for loading employee data from any source.
 * Implementations can target CSV files, databases, REST APIs, or any other format
 * without requiring changes to the analysis or reporting layers.
 */
public interface EmployeeParser {

  /**
   * Parses employees from the given source path or resource identifier.
   * Returns an empty list if the source contains no data rows.
   */
  List<Employee> parse(String source);
}