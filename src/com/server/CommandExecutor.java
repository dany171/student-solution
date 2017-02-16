package com.server;

import java.util.HashMap;

import com.exceptions.BadDeleteException;
import com.exceptions.BadStudentException;
import com.exceptions.BadUpdateException;
import com.exceptions.ParseCommandException;
import com.model.Student;
import com.model.Student.Gender;
import com.model.Student.Type;
import com.search.StudentSearchService;
import com.server.data.Consumer;
import com.server.data.DataService;

public class CommandExecutor implements Consumer{

	private DataService dataService;
	private StudentSearchService searchService;
	
	public CommandExecutor(DataService dataService){
		this.dataService = dataService;		
	}
	
	public String execute(String command){
				
		Command cmd;
		
		try{
			cmd = parseCommand(command); 
		
			if(cmd==Command.CREATE){
				return executeCreateStudent(cmd);
			}else
			if(cmd==Command.UPDATE){
				return executeUpdateStudent(cmd);
			}else
			if(cmd==Command.DELETE){
				return executeDeleteStudent(cmd);
			}
		
		}catch(ParseCommandException pe){
			return "Error while parsing the command";
		}catch(BadStudentException bse){
			return bse.getMessage();
		}catch(BadUpdateException bue){
			return bue.getMessage();
		}catch(BadDeleteException bde){
			return bde.getMessage();
		}finally{
			System.out.println("Error processing message");
		}
		return "No command foud";		
				
	}
	
	private String executeCreateStudent(Command cmd) throws BadStudentException {
		
		HashMap<String,String> options = cmd.getOptions();
		
		Student student = new Student();
		
		student.setName(options.get("name"));
		student.setGender(Gender.getGender(options.get("gender")));
		student.setType(Type.getType(options.get("type")));
		
		if(student.getName()==null||
		   student.getName().equalsIgnoreCase("")  ||	
		   student.getGender()==null ||
		   student.getType()==null
		){ 
			throw new BadStudentException("Missing name, gender or type");
		}
		
		try{
			
			Student newStudent = processSave(student);
			return "Student Created:"+newStudent.toString();
			
		}catch(Exception e){
			throw new BadStudentException("Error while saving",e);
		}		
	}
	
	private String executeUpdateStudent(Command cmd) throws BadUpdateException{
		HashMap<String,String> options = cmd.getOptions();
		
		Student student = new Student();
		
		student.setId(new Long(options.get("id")));
		student.setName(options.get("name"));
		student.setGender(Gender.getGender(options.get("gender")));
		student.setType(Type.getType(options.get("type")));
		
		if(student.getId()==null ||
		   student.getName()==null||
		   student.getName().equalsIgnoreCase("")  ||	
		   student.getGender()==null ||
		   student.getType()==null
		){ 
			throw new BadUpdateException("Missing id, name, gender or type");
		}
		
		try{
			
			Student updatedStudent = processUpdate(student);
			return "Student updated:"+updatedStudent.toString();
			
		}catch(Exception e){
			throw new BadUpdateException("Error while updating",e);
		}		
	}
	
	private String executeDeleteStudent(Command cmd) throws BadDeleteException{
		HashMap<String,String> options = cmd.getOptions();
		Long id;
		try{
			String strId = options.get("id");
			id = new Long(strId);
		}catch(Exception e){
			throw new BadDeleteException("Id empty or incompatible",e);
		}
		
		try{
			processDelete(id);
			return "Student with ID: "+id+" was deleted";
		}catch(Exception e ){
			throw new BadDeleteException("Error while deleting",e);
		}
	}
	
	@Override
	public Student processSave(Student student) {
		return dataService.save(student);		
	}
	
	@Override
	public void processDelete(Long id) {
		dataService.delete(id);		
	}

	@Override
	public Student processUpdate(Student student) {
		return dataService.update(student);
	}

	public void setStudentSearchService(StudentSearchService sss){
		this.searchService = sss;
	}

/*	@Override
	public void processSearchByName(String name) {
		searchService.searchByName(name);
		
	}

	@Override
	public void processSearchByType(Type type) {
        searchService.searchByType(type);
		
	}

	@Override
	public void processSearhByTypeAndGender(Type type, Gender gender) {
		searchService.searchByTypeAndGender(type, gender);		
	}*/
	
	private Command parseCommand(String text) throws ParseCommandException{
		
		try {
			String[] tokens = text.split(" ");
		
			String cmd = tokens[0];
			
			text = text.substring(text.indexOf(" ")+1);
			
			text = text.replaceAll(tokens[0], "");
			
			String[] options = text.split(" ");
			
			HashMap<String,String> opts = new HashMap<String, String>();
			
			for(String option : options){
				String[] optAndValue = option.split("=");
				opts.put(optAndValue[0], optAndValue[1]);
			}
			
			return Command.getCommand(cmd, opts);
			
		}catch(Exception e){
			throw new ParseCommandException(e);
		}		
	}
	
	private enum Command{
		CREATE, UPDATE, DELETE;
		
		private HashMap<String,String> options;		
		
		public static Command getCommand(String text,HashMap<String,String> opts){
			
			Command cmd = null;
			
			if(text.equalsIgnoreCase("create")){
				cmd = Command.CREATE;
				cmd.setOptions(opts);
				
			}else
			if(text.equalsIgnoreCase("update")){
				cmd = Command.UPDATE;
				cmd.setOptions(opts);
			}else
			if(text.equalsIgnoreCase("delete")){
				cmd = Command.DELETE;
				cmd.setOptions(opts);
			}
			return cmd;
		}

		public HashMap<String,String> getOptions() {
			return options;
		}

		public void setOptions(HashMap<String,String> options) {
			this.options = options;
		}
		
		
	}
}