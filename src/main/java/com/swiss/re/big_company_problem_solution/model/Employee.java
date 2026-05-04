package com.swiss.re.big_company_problem_solution.model;

/**
 * Immutable representation of a single employee loaded from the CSV input.
 * Holds the employee's unique id, full name, annual salary, and the id of their
 * direct manager.
 * The managerId is null for the CEO, who has no manager.
 */
public record Employee(int id, String firstName, String lastName, double salary, Integer managerId) {
}
