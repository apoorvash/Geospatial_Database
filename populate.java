import java.sql.*;
import java.util.*;
import java.io.*;


 public class populate {
 
	 static Connection conn = null;
	 static ResultSet rs = null;
	 static Statement st = null;
	 static String query;
	 
		public populate() throws SQLException{
			
			connectDatabase();
		}
		 
		 public static void connectDatabase() throws SQLException
		{
			 System.out.println("Looking for Oracle's jdbc-odbc driver ... ");
			 DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			 System.out.println("Loaded.");
			 String host = "127.0.0.1";
			 String port = "1521";//"1521"
			 String dbName = "orcl";
			 String JDBC_STRING = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName; 
			 String USER_NAME = "scott";
			 String PASSWD = "Aus12316";
			 try {
				
				 conn = DriverManager.getConnection (JDBC_STRING, USER_NAME, PASSWD);
				 System.out.println("Connected.");
				 st = conn.createStatement ();
				 st.executeUpdate("delete from fire_building");
				 st.executeUpdate("delete from building");
				 st.executeUpdate("delete from fire_hydrant");
				 System.out.println("Data deleted from Tables");
				 
			 } 
			 catch (SQLException sqlEx) {
				 sqlEx.printStackTrace ();
			 } 
		 }
		 
		 public void insert_hydrant(String hydrant_id, int X ,int Y)
		{
			try
			 {
				String hyd_Query=null;
				
					if(!hydrant_id.equals("null")) 
					{
						hyd_Query="insert into fire_hydrant values ('"+hydrant_id+"', mdsys.sdo_geometry(2001,null,mdsys.sdo_point_type("+X+","+Y+",null),null,null))";
					}
					st.executeUpdate(hyd_Query);
			 }
			 catch( Exception e )
			 { 
				e.printStackTrace();
			 }
		}
		  
		 public void insert_building(String bid, String bname, int vertices, int a[][])
		{
				try
				{
					String build_Query=null;
					int i = 0;
					int x1 = a[0][0], y1 = a[0][1];
					if(!bid.equals("null")) 
					{
						
						build_Query="insert into building values ('"+bid+"','"+bname+"','"+vertices+"'";
						build_Query+=",mdsys.sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array(";
						while ( i < vertices )
						{
							build_Query+=a[i][0];
							build_Query+=",";
							build_Query+=a[i][1];
							build_Query+=", ";
							i++;
						}
						build_Query+=x1;
						build_Query+=",";
						build_Query+=y1;
						build_Query+=")))";
					}
					st.executeUpdate(build_Query);

			 }
			 catch( Exception e )
			 { 
				 e.printStackTrace();
			 }
		}
		public void insert_fire(String fname)
		{
			try
			{
				
				String fire_Query = null;
				if(!fname.equals("null"))
				{
				
					fire_Query = "insert into fire_building values ('"+fname+"')";
				}
				st.executeUpdate(fire_Query);
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		public void file_read(String f1, String f2, String f3)
		{
					  File file1 = new File (f1);
					  File file2 = new File (f2);
					  File file3 = new File (f3);
					  FileInputStream fileInput = null;
					  BufferedInputStream bufferInput = null;
					  DataInputStream dataInput = null;
					  try{
							if(file1 != null)
							{
								
								fileInput = new FileInputStream(file1);
								bufferInput = new BufferedInputStream(fileInput);
								dataInput = new DataInputStream(bufferInput);
								while (dataInput.available() != 0)
								{	
									String s1 = dataInput.readLine();
									StringTokenizer stoken = new StringTokenizer(s1, ", ");
									String hydrant_id = null;
									int X = 0,Y = 0;		    	 
									hydrant_id = stoken.nextToken();
									X = Integer.parseInt(stoken.nextToken().trim());
									Y = Integer.parseInt(stoken.nextToken().trim());
									this.insert_hydrant(hydrant_id,X,Y);			
									
								}
								System.out.println( "fire_hydrant table populated \n" );
							}
							
							if(file2 != null)
							{
								fileInput = new FileInputStream(file2);
								bufferInput = new BufferedInputStream(fileInput);
								dataInput = new DataInputStream(bufferInput);
								
								while(dataInput.available() != 0)
								{
									
									int a[][] = new int [100][2];
									int i = 0;
									String s1 = dataInput.readLine();
									StringTokenizer stoken = new StringTokenizer(s1, ",");
									String bid = null, bname = null;
									int vert = 0;
									bid = stoken.nextToken();
									bname = stoken.nextToken().trim();
									vert = Integer.parseInt(stoken.nextToken().trim());
								  
									while(stoken.hasMoreTokens()) {
											a[i][0] = Integer.parseInt(stoken.nextToken().trim());
											a[i][1] = Integer.parseInt(stoken.nextToken().trim());
											i++;   	
									 }
									 this.insert_building(bid,bname,vert,a);   	
								}
								 System.out.println( "Building table populated \n" );
									
							}
							
							if( file3 != null )
							{
								fileInput = new FileInputStream(file3);
								bufferInput = new BufferedInputStream(fileInput);
								dataInput = new DataInputStream(bufferInput);
								while( dataInput.available() != 0 )
								{
								
									String s1 = dataInput.readLine();
									String fname = null;;
									fname = s1;
									this.insert_fire(fname);
								}
								System.out.println( "Fire_Building table populated \n" );
							}
					  }
					  catch (FileNotFoundException e) {
						  e.printStackTrace();
					  } 
					  
					  catch (IOException e) {
						  e.printStackTrace();
					  }
					  catch(NumberFormatException e)
					  {
						e.printStackTrace();
					  }
				}
 
 @SuppressWarnings({ "deprecation", "resource" })
public static void main (String [] args)throws SQLException  {
	 
	 String f1,f2,f3;
	  f1 = args[0];
	  f2 = args[1];
	  f3 = args[2];
	
	  populate h= new populate();
	  
	  h.file_read(f1, f2, f3);
	 
 
 }
 }
