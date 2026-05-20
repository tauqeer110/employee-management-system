package com.ems.employee.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
  MongoDB document that maps to the "employees" collection.
  @Document tells Spring Data MongoDB which collection to use.
 */
@Document(collection = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    private String id;

    private String name;

    private int age;

    private String address;
}
