package com.internetarmy.aws.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.internetarmy.aws.model.Employee;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
	
@Repository
public class EmployeeRepo {
	
	@PersistenceContext
	private EntityManager em;
	
	public <T> T saveEmployee(T t) {
		return em.merge(t);
	}
	
	@SuppressWarnings("unchecked")
	public List<Employee> findAllEmployees(Integer id, String name) {
		StringBuilder builder = new StringBuilder();
		builder.append("select * from employee where 1=1 ");
		if(id != null) {
			builder.append("and id = :id ");
		}
		if(name != null && !name.isBlank()) {
			builder.append("and employee_name = :name ");
		}
		Query query = em.createNativeQuery(builder.toString(), Employee.class);
		if(id != null) query.setParameter("id", id);
		if(name != null && !name.isBlank()) query.setParameter("name", name);
		return query.getResultList();
	}
	
}
