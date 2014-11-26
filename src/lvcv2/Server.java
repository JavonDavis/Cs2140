package lvcv2;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Write a description of interface Server here.
 * 
 * @author Kerine Wint and Javon Davis 
 * @version Nov 9. 2014
 */
public class Server
{
    /*
   private static HashMap<String,String> managers = new HashMap<>();
   private static HashMap<String,String> developers = new HashMap<>();
   private static HashMap<String,String> names = new HashMap<>();
   
  static
  {
      managers.put("M1231", "cat");
      managers.put("M1232", "cat");
      managers.put("M1233", "cat");
      managers.put("M1234", "cat");
      managers.put("M1235", "cat");
      
      developers.put("D1231", "dog");
      developers.put("D1232", "dog");
      developers.put("D1233", "dog");
      developers.put("D1234", "dog");
      developers.put("D1235", "dog");   
      
      names.put("M1231", "Mark");
      names.put("M1232", "Kerine");
      names.put("M1233", "Javon");
      names.put("M1234", "David");
      names.put("M1235", "Tiffany");
      names.put("D1231", "Kartel");
      names.put("D1232", "Demarco");
      names.put("D1233", "Carlton");
      names.put("D1234", "Will");
      names.put("D1235", "Uncle Phil");
  }*/
    
    public static void addProject(Project project)
    {
        Connection c;
        Statement stmt;
        try {
          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection("jdbc:sqlite:lvc.db");
          c.setAutoCommit(false);
          

          stmt = c.createStatement();
          
            System.out.println(""+project.getManager().getId());
          
          String sql = "INSERT INTO projects (title,deadline,manager_id,description) " +
                "VALUES ('"+project.getTitle()+"', '"+project.getDeadline()+"', '"+project.getManager().getId()+"','"+project.getDescription()+"');"; 
          stmt.executeUpdate(sql);


          stmt.close();
          c.commit();
          c.close();
          
        } catch ( ClassNotFoundException | SQLException e ) {
          Logger.getLogger(LVCv2.class.getName()).log(Level.SEVERE, null, e);
          
        }
        
    }
  
    public static List<Project> getProjects(Employee employee)
    {
        Connection c;
        Statement stmt;
          
        ArrayList<Project> projects = new ArrayList<>();
          
          if(employee.isManager())
          {
            try {
              Class.forName("org.sqlite.JDBC");
              c = DriverManager.getConnection("jdbc:sqlite:lvc.db");// open test database
              c.setAutoCommit(false);

              stmt = c.createStatement();

              ResultSet rs = stmt.executeQuery( "SELECT * FROM projects where manager_id like '"+employee.getId()+"';" );
              while ( rs.next() ) {
                  Project project;

                  String title = rs.getString("title");
                  String deadline = rs.getString("deadline");
                  String description = rs.getString("description");

                  project = new Project(title, (Manager) employee, description, deadline);

                  projects.add(project);

              }
              rs.close();
              stmt.close();
              c.close();
              return projects;
            } catch ( ClassNotFoundException | SQLException e ) {
              Logger.getLogger(LVCv2.class.getName()).log(Level.SEVERE, null, e);
              return null;
            }
        }else if(employee.isDeveloper())
        {
            
            try {
              Class.forName("org.sqlite.JDBC");
              c = DriverManager.getConnection("jdbc:sqlite:lvc.db");// open test database
              c.setAutoCommit(false);

              stmt = c.createStatement();

              ResultSet rs = stmt.executeQuery( "SELECT * FROM projects_developers where developer_id like '"+employee.getId()+"';" );
             
              while ( rs.next() ) {
                  Project project;

                  int id = rs.getInt("project_id");
                  
                  Statement stmt2 = c.createStatement();
                  ResultSet rs2 = stmt2.executeQuery( "SELECT * FROM projects where id = "+id+";" );
                while ( rs2.next() ) {

                    String title = rs2.getString("title");
                    String deadline = rs2.getString("deadline");
                    String description = rs2.getString("description");
                    String manager_id = rs2.getString("manager_id");
                    
                    
                    Manager manager;
                    
                    Statement stmt3 = c.createStatement();
                    
                    ResultSet rs3 = stmt3.executeQuery( "SELECT * FROM employees where id like '"+manager_id+"';" );
                    while ( rs3.next() ) {
 
                        String name = rs3.getString("name");
                        
                        manager = new Manager(manager_id,name);
                        
                        project = new Project(title,manager , description, deadline);

                        projects.add(project);

                    }
                    rs3.close();
                    stmt3.close();
                }
                rs2.close();
                stmt2.close();
              }
              rs.close();
              stmt.close();
              c.close();
              return projects;
            } catch ( ClassNotFoundException | SQLException e ) {
              Logger.getLogger(LVCv2.class.getName()).log(Level.SEVERE, null, e);
              return null;
            }
        }
          return null;
    }
    
    public void addCode(Project project, String location, Developer developer)
    {
        
    }
    
    public static Employee validEmployee(String _id,String pw)
    {
          Connection c;
          Statement stmt;
          Employee employee;
          try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:lvc.db");// open test database
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM employees where id like '"+_id+"' and password like "
                    + "'"+pw+"';" );
            while ( rs.next() ) {
                String name = rs.getString("name");
                String id = rs.getString("id");

                if(rs.getString("identifier").equals("M"))
                {
                    employee = new Manager(id,name);
                    rs.close();
                    stmt.close();
                    c.close();
                    return employee;
                }
                else if(rs.getString("identifier").equals("D"))
                {
                    employee = new Developer(id,name);
                    rs.close();
                    stmt.close();
                    c.close();
                    return employee;
                }
            }
            rs.close();
            stmt.close();
            c.close();
          } catch ( ClassNotFoundException | SQLException e ) {
            Logger.getLogger(LVCv2.class.getName()).log(Level.SEVERE, null, e);
          }
          
          return null;
    }
  
}
