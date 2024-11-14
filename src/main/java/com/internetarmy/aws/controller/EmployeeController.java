package com.internetarmy.aws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.internetarmy.aws.model.Employee;
import com.internetarmy.aws.service.impl.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private EmployeeService service;
	
	@PostMapping
	public Employee saveEmployee(@RequestBody Employee emp) {
		return service.saveEmployee(emp);
	}
	
	@GetMapping
	public List<Employee> findAllEmpoyees(@RequestParam(required = false) Integer id, @RequestParam(required = false) String name){
		return service.findEmployee(id, name);
	}
	
//	@GetMapping("/startConsumer")
//	public String startConsumer() {
//		service.startConsumer();
//		return "Started";
//	}

}
