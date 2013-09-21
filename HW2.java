import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.Graphics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.lang.*;
import java.lang.reflect.Array;



public class HW2 extends JFrame {

	private JPanel contentPane;
	private static JLabel labelImg;
	private static JTextArea textAreaQuery;
	private static Graphics g;
	private JTextField mouseCoordinates;
	static Connection conn = null;
	 static ResultSet rs = null;
	 static ResultSet rs1 = null;
	 static Statement stmt = null;
	 static String query;
	 private int leftClickCounter = 0;
	 private int xpoints[] = new int[40];
	 private int ypoints[] = new int[40];
	 private int build_count = 0;
	 private int xpointstore[][] = new int[400][400];
	 private int ypointstore[][] = new int[400][400];
	 private String pointstring = null;
	 private static int querycounter = 0;
	 private static int rightClick_flag = 0;

	  //Launch application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HW2 frame = new HW2();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void draw_range_line( int x ,int y , int r , Graphics g)
	{
		g.setColor(Color.RED);
		g.fillRect(x-r/2, y-r/2, r, r);		
		
	}
	
	public static void draw_hydrant( int x ,int y , int r , Graphics g)
	{
		g.setColor(Color.GREEN);
		g.fillRect(x-r/2, y-r/2, r, r);
		
	}
	 public static void connectDatabase() throws SQLException
	 {
	 	 System.out.println("Looking for Oracle's jdbc-odbc driver ... ");
	 	 DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	 	 System.out.println("Loaded.");
	 	 String host = "127.0.0.1";
	 	 String port = "1521";
	 	 String dbName = "orcl";
	 	 String JDBC_STRING = "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName; 
	 	 String USER_NAME = "scott";
	 	 String PASSWD = "Aus12316";
	 	 try {
	 		
	 		 conn = DriverManager.getConnection (JDBC_STRING, USER_NAME, PASSWD);
	 		 System.out.println("Connected");
	 		 stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	  		 
	 	 } 
	 	 catch (SQLException sqlEx) {
	 		 sqlEx.printStackTrace ();
	 	 } 
	  }
      
	  
	 public static void hydrant_query() throws SQLException
	 {
		 
		 int X,Y;
		 String hydrantQuery="SELECT fh.hydrant_loc.sdo_point.x, fh.hydrant_loc.sdo_point.y FROM fire_hydrant fh";
		 querycounter++;
		 textAreaQuery.append("Query"+querycounter+" :"+hydrantQuery+'\n');
		 try 
		 {
				rs=stmt.executeQuery(hydrantQuery);
				while(rs.next())
				 { 
					X=Integer.parseInt(rs.getString(1));
					Y=Integer.parseInt(rs.getString(2));
				    
					draw_hydrant(X, Y, 10, g);
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	 }
	 
	 public static void building_query() throws SQLException
	 {
		 String buildQuery="SELECT b.building_name,b.vertices,t.X, t.Y FROM  building b,TABLE(SDO_UTIL.GETVERTICES(b.build_loc)) t";
		 String name=null;
		 int verts,k;
		 int xarray[]= new int[400];
		 int yarray[]= new int[400];
		 querycounter++;
		 textAreaQuery.append("Query"+querycounter+" :"+buildQuery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(buildQuery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					yarray[k]=rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						yarray[k]=rs.getInt(4);
						k++;
											
					}
					g.setColor(Color.yellow);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}
				
				
	 
	public static void fire_query() throws SQLException
	{
		String fireQuery="SELECT b.building_name,b.vertices,t.X, t.Y FROM  building b,TABLE(SDO_UTIL.GETVERTICES(b.build_loc)) t, fire_building f WHERE f.fire_build_name = b.building_name";
		int verts, k;
		String name=null;
		int xarray[]= new int[400];
		int yarray[]= new int[400];
		
		 querycounter++;
		 textAreaQuery.append("Query"+querycounter+" :"+fireQuery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(fireQuery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					yarray[k]=rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						yarray[k]=rs.getInt(4);
						k++;
											
					}
					g.setColor(Color.red);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();	
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}

	public static void close_build_query() throws SQLException
	{
		String close_bquery = "SELECT b1.building_name,b1.vertices,t.X, t.Y FROM  building b1, building b2, TABLE(SDO_UTIL.GETVERTICES(b1.build_loc)) t, fire_building f WHERE b2.building_name = f.fire_build_name AND SDO_WITHIN_DISTANCE(b1.build_loc, b2.build_loc, 'distance = 100') = 'TRUE'";
		int verts,k;
		String name=null;
		int xarray[]= new int[400];
		int yarray[]= new int[400];
	
		 querycounter++;
		 textAreaQuery.append("Query"+querycounter+" :"+close_bquery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(close_bquery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					yarray[k]=rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						yarray[k]=rs.getInt(4);
						k++;
					}
					g.setColor(Color.yellow);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();	
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}

	public void get_build(int x1, int x2) throws SQLException
	{
		int b1 = x1;
		int b2 = x2;
		String pointstring = null;
		pointstring = "sdo_geometry(2001,null,mdsys.sdo_point_type("+b1+","+b2+",null),null,null)";
		String get_bquery = "SELECT b.building_name,b.vertices,t.X, t.Y FROM  building b, TABLE(SDO_UTIL.GETVERTICES(b.build_loc)) t WHERE SDO_ANYINTERACT(b.build_loc,"+pointstring+") = 'TRUE'";
		int xarray[]= new int[400];
		int yarray[]= new int[400];
		int verts,k;
		String name=null;
		querycounter++;
		textAreaQuery.append("Query"+querycounter+" :"+get_bquery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(get_bquery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					xpointstore[build_count][k]= rs.getInt(3);
					yarray[k]=rs.getInt(4);
					ypointstore[build_count][k] = rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						xpointstore[build_count][k]= rs.getInt(3);
						yarray[k]=rs.getInt(4);
						ypointstore[build_count][k] = rs.getInt(4);
						k++;
											
					}
					g.setColor(Color.red);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();	
					build_count++;	
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}
	
	public void get_close_hydrant() throws SQLException
	{
		int p,i,j;
		int X, Y;
		for ( p = 0 ; p < build_count ; p++ )
		{
				int q=0;
				String polystring="sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array(";
				polystring+=xpointstore[p][q];
		     	polystring+=",";
		     	polystring+=ypointstore[p][q];
		     	while ( ypointstore[p][q] != 0  )
				{
					polystring+=", ";
					polystring+=xpointstore[p][q];
		     		polystring+=",";
		     		polystring+=ypointstore[p][q];
					q++;
		     	}
				polystring+="))";
				String get_hyd_query = "SELECT fh.hydrant_loc.sdo_point.x, fh.hydrant_loc.sdo_point.y FROM fire_hydrant fh WHERE SDO_NN(fh.hydrant_loc,"+polystring+",'sdo_num_res = 1') = 'TRUE'";
				querycounter++;
				textAreaQuery.append("Query"+querycounter+" :"+get_hyd_query+'\n');
				try 
				{
					connectDatabase();
					rs=stmt.executeQuery(get_hyd_query);
					while(rs.next())
					{ 
					
					X=Integer.parseInt(rs.getString(1));
					Y=Integer.parseInt(rs.getString(2));
				    
					draw_hydrant(X, Y, 10, g);
					 }
				}
				catch (SQLException e1) 
				{
						e1.printStackTrace();
				}
			}
			for ( i = 0 ; i < 400 ; i++ )
			{
				for ( j = 0 ; j < 400 ; j++ )
				{
					xpointstore[i][j] = 0;
					ypointstore[i][j] = 0;
				}
			}
	}
	
	/**
	 * Create frame.
	 */
	public HW2() {
		setTitle("Apoorva Umesh Shetti, USC ID : 6217064961");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1000, 750);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JCheckBox check_Building = new JCheckBox("Buildings");
		check_Building.setBounds(826, 52, 175, 23);
		contentPane.add(check_Building);
		
		
		final JCheckBox check_fire = new JCheckBox("Buildings on Fire");
		check_fire.setBounds(826, 83, 175, 23);
		contentPane.add(check_fire);
		
		final JCheckBox check_hydrant = new JCheckBox("Hydrants");
		check_hydrant.setBounds(826, 118, 175, 23);
		contentPane.add(check_hydrant);
		
		JLabel lblActiveFeatureType = new JLabel("Active Feature Type");
		lblActiveFeatureType.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblActiveFeatureType.setBounds(830, 11, 144, 34);
		contentPane.add(lblActiveFeatureType);
		
		JLabel lblQuery = new JLabel("Query");
		lblQuery.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblQuery.setBounds(830, 169, 95, 23);
		contentPane.add(lblQuery);
		
		final JRadioButton rdbtnWholeRegion = new JRadioButton("Whole Region");
		rdbtnWholeRegion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Whole Region".equals(e.getActionCommand()))
				{
					labelImg.setIcon(new ImageIcon("map.jpg"));
					leftClickCounter = 0;
					
				}
			}
		});
		rdbtnWholeRegion.setBounds(826, 208, 175, 23);
		contentPane.add(rdbtnWholeRegion);
		
		final JRadioButton rdbtnRangeQuery = new JRadioButton("Range Query");
		rdbtnRangeQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Range Query".equals(e.getActionCommand()))
				{
					labelImg.setIcon(new ImageIcon("map.jpg"));
					leftClickCounter = 0;
					
				}
			}
		});
		rdbtnRangeQuery.setBounds(826, 241, 175, 23);
		contentPane.add(rdbtnRangeQuery);
		
		final JRadioButton rdbtnCloseFireQuery = new JRadioButton("Find Neighbor Buildings");
		rdbtnCloseFireQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Find Closest Building".equals(e.getActionCommand()))
				{
					labelImg.setIcon(new ImageIcon("map.jpg"));
					leftClickCounter = 0;
					
				}
			}
		});
		rdbtnCloseFireQuery.setBounds(826, 274, 175, 23);
		contentPane.add(rdbtnCloseFireQuery);
		
		final JRadioButton rdbtnCloseHydrant = new JRadioButton("Find Closest Fire Hydrants");
		rdbtnCloseHydrant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Find Closest Hydrant".equals(e.getActionCommand()))
				{
					labelImg.setIcon(new ImageIcon("map.jpg"));
					
				}
			}
		});
		rdbtnCloseHydrant.setBounds(826, 308, 175, 23);
		contentPane.add(rdbtnCloseHydrant);	
		
		ButtonGroup bg= new ButtonGroup();
		bg.add(rdbtnCloseHydrant);
		bg.add(rdbtnCloseFireQuery);
		bg.add(rdbtnRangeQuery);
		bg.add(rdbtnWholeRegion);
		
		labelImg = new JLabel("");
		labelImg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(rdbtnRangeQuery.isSelected())
				{
					
					int a1 = e.getX();
					int a2 = e.getY();
					
					if(SwingUtilities.isLeftMouseButton(e))
					{	
					g = labelImg.getGraphics();
					draw_range_line(a1, a2, 5, g);
					xpoints[leftClickCounter]=a1;
					ypoints[leftClickCounter]=a2;
					if(leftClickCounter>0)
					{
						g.drawLine(xpoints[leftClickCounter], ypoints[leftClickCounter], xpoints[leftClickCounter-1], ypoints[leftClickCounter-1]);
					}
					leftClickCounter++;
					}
					if(SwingUtilities.isRightMouseButton(e))
					{
						xpoints[leftClickCounter]=xpoints[0];
						ypoints[leftClickCounter]=ypoints[0];
						g.drawPolyline(xpoints, ypoints, leftClickCounter+1);
						rightClick_flag=1;
						
						
					}
					
				}
			
				if(rdbtnCloseHydrant.isSelected())
				{
					
						
						int a1 = e.getX();
						int a2 = e.getY();
						try {
							connectDatabase();
							g = labelImg.getGraphics();
							get_build(a1,a2);
			           		
						} catch (SQLException e1) {
							e1.printStackTrace();
						}  
						} 
				}
				
		});
		labelImg.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				int a1 = arg0.getX();
				int a2 = arg0.getY();
				String s = Integer.toString(a1) +','+ Integer.toString(a2);
				mouseCoordinates.setText(s);
				
			}
		});
		labelImg.setIcon(new ImageIcon("map.jpg"));
		labelImg.setBounds(0, 0, 820, 580);
		contentPane.add(labelImg);
		
		
		mouseCoordinates = new JTextField();
		mouseCoordinates.setText("Mouse Coordinates");
		mouseCoordinates.setBounds(150, 595, 148, 20);
		contentPane.add(mouseCoordinates);
		mouseCoordinates.setColumns(10);
		
		JTextArea txtrMouseCoordinates = new JTextArea();
		txtrMouseCoordinates.setText("Mouse Coordinates");
		txtrMouseCoordinates.setBounds(10, 595, 146, 22);
		contentPane.add(txtrMouseCoordinates);
		

		JButton btnSubmit = new JButton("Submit Query");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(rdbtnWholeRegion.isSelected())
				{	
					try {
					connectDatabase();
					g = labelImg.getGraphics();
					
					if(check_Building.isSelected())
						building_query();
					if(check_hydrant.isSelected())
						hydrant_query();
					if(check_fire.isSelected())
						fire_query();
					if(!check_Building.isSelected() && !check_fire.isSelected() && !check_hydrant.isSelected())
						labelImg.setIcon(new ImageIcon("map.jpg"));
					
					
					}
					catch (SQLException e1) {
					e1.printStackTrace();
					}
				}
				if(rdbtnRangeQuery.isSelected() && rightClick_flag==1)
				{
					try {
						connectDatabase();
						g = labelImg.getGraphics();
						int k,X,Y,rad,ver;
						String name;
						int xarray1[]= new int[400];
						int yarray1[]= new int[400];
						String polystring="sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array(";
		     			for(int i=0;i<leftClickCounter;i++)
						{
		     				polystring+=xpoints[i];
		     				polystring+=",";
		     				polystring+=ypoints[i];
		     				polystring+=", ";
		     			}
		     			polystring+=xpoints[0];polystring+=",";polystring+=ypoints[0];polystring+="))";
		     			
						if (check_hydrant.isSelected())
						{
							String query="Select h.hydrant_loc.sdo_point.x, h.hydrant_loc.sdo_point.y FROM fire_hydrant h WHERE sdo_relate(h.hydrant_loc,";
							query+=polystring;
							query+=",'mask=anyinteract') = 'TRUE'";
							querycounter++;
							textAreaQuery.append("Query"+querycounter+" :"+query+'\n');
							try 
							{
								connectDatabase();
								rs=stmt.executeQuery(query);
								while(rs.next())
								{ 
	           					
									X=Integer.parseInt(rs.getString(1));
									Y=Integer.parseInt(rs.getString(2));
	           				    
									draw_hydrant(X, Y, 10, g);
	           					
								}
							}
							catch (SQLException e1) 
							{
	           						e1.printStackTrace();
							}
						}	
						
						if (check_Building.isSelected())
							 {	
								String query2="SELECT b.building_name,b.vertices,t.X, t.Y FROM  building b,TABLE(SDO_UTIL.GETVERTICES(b.build_loc)) t WHERE sdo_relate(b.build_loc,";
								query2+=polystring;
								query2+=",'mask=anyinteract') = 'TRUE'";
								querycounter++;
								textAreaQuery.append("Query"+querycounter+" :"+query2+'\n');
								try 
								{
									connectDatabase();
								  rs=stmt.executeQuery(query2);
								  while(rs.next())
								  { 
									k=0;
									name=rs.getString(1);
									ver=rs.getInt(2);
									xarray1[k]=rs.getInt(3);
									yarray1[k]=rs.getInt(4);
									k++;
									while(rs.next() && rs.getString(1).compareTo(name)==0)
									{
										xarray1[k]=rs.getInt(3);
										yarray1[k]=rs.getInt(4);
										k++;
															
									}
									g.setColor(Color.yellow);
									g.drawPolyline(xarray1, yarray1, ver+1);
									rs.previous();
									
								 }	
								}
								catch (SQLException e1) 
								{
											e1.printStackTrace();
								}
						}
						if(check_fire.isSelected())
						{
							String query="SELECT b.building_name,b.vertices,t.X, t.Y FROM  building b, fire_building f, TABLE(SDO_UTIL.GETVERTICES(b.build_loc)) t WHERE b.building_name = f.fire_build_name AND sdo_relate(b.build_loc,";
							query+=polystring;
							query+=",'mask=anyinteract') = 'TRUE'";
							querycounter++;
							textAreaQuery.append("Query"+querycounter+" :"+query+'\n');
							 try 
								 {
										connectDatabase();
										rs=stmt.executeQuery(query);
										while(rs.next())
										 { 
											k=0;
											name=rs.getString(1);
											ver=rs.getInt(2);
											xarray1[k]=rs.getInt(3);
											yarray1[k]=rs.getInt(4);
											k++;
											while(rs.next() && rs.getString(1).compareTo(name)==0)
											{
												xarray1[k]=rs.getInt(3);
												yarray1[k]=rs.getInt(4);
												k++;
																	
											}
											g.setColor(Color.red);
											g.drawPolyline(xarray1, yarray1, ver+1);
											rs.previous();
												
										 }
								 }
								 catch (SQLException e1) 
								 {
												e1.printStackTrace();
								 }
								}
				}
				catch (SQLException e1) {
						e1.printStackTrace();
				}
				}
				if(rdbtnCloseFireQuery.isSelected())
				{
					try {
						
						connectDatabase();
						g = labelImg.getGraphics();
						close_build_query();
						fire_query();
						
						}
				catch (SQLException e1) {
						e1.printStackTrace();
				}
				}
				
				if(rdbtnCloseHydrant.isSelected())
				{
					try{
					
						
						g = labelImg.getGraphics();
						get_close_hydrant();
						build_count=0;
						
					}
					catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
				
			
		});
		btnSubmit.setBounds(822, 557, 152, 23);
		contentPane.add(btnSubmit);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 620, 964, 81);
		contentPane.add(scrollPane);
		
		textAreaQuery = new JTextArea();
		scrollPane.setViewportView(textAreaQuery);
		textAreaQuery.setText("Query Text \n");
		
	}
}
