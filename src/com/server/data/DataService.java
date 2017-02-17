package com.server.data;

import java.util.Collection;
import java.util.Map;

import com.model.Student;
import com.model.Student.Gender;
import com.model.Student.Type;

public interface DataService {

	public Student save(Student student);
	public void delete(Long id);
	public Student update(Student student);
	public boolean persist(String filename);
	public boolean load(String filename);
	public Map<String,Student> getStudentsByName();
	public PropertyTypeMap<Gender> getStudentsByGender();
	public PropertyTypeMap<Type> getStudentsByType(); 
}
