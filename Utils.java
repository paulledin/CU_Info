package coop.cuna.cdean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils 
{
	public static Connection getConnection() throws SQLException, IOException
	{
		return (DriverManager.getConnection("jdbc:mysql://stat1pro:3306/EandSInfo", "pwl", "ledin"));
	}

	public static void closeConnection(Connection dbConn)
	{
		try
		{
			dbConn.close();
		}
		catch(Exception e)
		{
			System.out.println("Unable to return connection to the database.");
			e.printStackTrace();
		}
	}

	public static String[] financialPeriods = { "September 2016", "June 2016", "March 2016",
			"December 2015", "September 2015", "June 2015", "March 2015",  
			"December 2014", "September 2014", "June 2014", "March 2014",
			"December 2013", "September 2013", "June 2013", "March 2013",
			"December 2012", "September 2012", "June 2012", "March 2012",
			"December 2011", "September 2011", "June 2011", "March 2011",
			"December 2010", "September 2010", "June 2010", "March 2010",
			"December 2009", "September 2009", "June 2009", "March 2009",
			"December 2008", "September 2008", "June 2008", "March 2008",
			"December 2007", "September 2007", "June 2007", "March 2007",
			"December 2006", "September 2006", "June 2006"
	};

	public static String[] states = { "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "District of Columbia", "Florida",
			"Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massassachusetts",
			"Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", 
			"North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee",
			"Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming", "Guam", "Puerto Rico", "Virgin Islands", "American Samoa"
	};

	public static String[] stateAbrs = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "DC", "FL",
			"GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA",
			"MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", 
			"NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN",
			"TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "GU", "PR", "VI", "AS"
	};
	
	public static String[] duesYears = { "2017", "2016", "2015" };

	public static String getStateAbbreviation(String fullName)
	{		
		for(int i = 0; i < Utils.states.length; i++)
		{
			if(states[i].equals(fullName))
			{
				return (stateAbrs[i]);
			}
		}

		return null;
	}

	public static String convertPeriod(String longPeriodName)
	{		
		if (longPeriodName.substring(0, 3).equals("Mar"))
		{
			return (longPeriodName.substring(longPeriodName.length()-4) + "03");
		}
		else if (longPeriodName.substring(0, 3).equals("Jun"))
		{
			return (longPeriodName.substring(longPeriodName.length()-4) + "06");
		}
		else if (longPeriodName.substring(0, 3).equals("Sep"))
		{
			return (longPeriodName.substring(longPeriodName.length()-4) + "09");
		}
		else 
		{
			return (longPeriodName.substring(longPeriodName.length()-4) + "12");
		}
	}

	public static String convertToLongPeriod(String shortPeriodName)
	{	
		if (shortPeriodName.substring(4).equals("03"))
		{
			return ("March " + shortPeriodName.substring(0, 4));
		}
		else if (shortPeriodName.substring(4).equals("06"))
		{
			return ("June " + shortPeriodName.substring(0, 4));
		}
		else if (shortPeriodName.substring(4).equals("09"))
		{
			return ("September " + shortPeriodName.substring(0, 4));
		}
		else 
		{
			return ("December " + shortPeriodName.substring(0, 4));
		}
	}

	public static String getYear(String shortPeriodName)
	{	
		return (shortPeriodName.substring(0, 4));
	}

	public static String convertToLongPeriodNoYear(String shortPeriodName)
	{	
		if (shortPeriodName.substring(4).equals("03"))
		{
			return ("March 31");
		}
		else if (shortPeriodName.substring(4).equals("06"))
		{
			return ("June 30");
		}
		else if (shortPeriodName.substring(4).equals("09"))
		{
			return ("September 30");
		}
		else 
		{
			return ("December 31");
		}
	}

	public static String[] search(Connection dbConn, String fullSearchText, boolean includeInactives) 
	{	
		try
		{
			if (fullSearchText.matches("#\\d{1,5}"))
			{
				return (Utils.findByCharter(dbConn, fullSearchText, includeInactives));
			}
			else if (fullSearchText.matches("\\d{1,6}"))
			{
				return (Utils.findByCUID(dbConn, fullSearchText, includeInactives));
			}
			else if (fullSearchText.matches("cust\\d{1,8}"))
			{
				return (Utils.findByCustomerID(dbConn, fullSearchText, includeInactives));
			}
			else
			{
				return (Utils.findByName(dbConn, fullSearchText, includeInactives));
			}
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in the search function of Utils.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByCEOLastName(Connection dbConn, String ceoLastName, boolean includeInactives)
	{
		try
		{		     
			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE mgrlast=\'" + ceoLastName + "\'");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE mgrlast=\'" + ceoLastName + "\' AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE mgrlast=\'" + ceoLastName + "\' ORDER BY id");
			}

			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByCEOLastName function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByURL(Connection dbConn, String url, boolean includeInactives)
	{
		try
		{		     
			String formattedURL = Pattern.compile("https://").matcher(url).replaceAll("");
			formattedURL = Pattern.compile("http://").matcher(url).replaceAll("");

			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE web=\'" + formattedURL + "\'");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE web=\'" + formattedURL + "\' AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE web=\'" + formattedURL + "\' ORDER BY id");
			}

			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByURL function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByPhone(Connection dbConn, String phone, boolean includeInactives)
	{
		try
		{		     
			String formattedPhone = Pattern.compile("-").matcher(phone).replaceAll(""); 

			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE phone=\'" + formattedPhone + "\'");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE phone=\'" + formattedPhone + "\' AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE phone=\'" + formattedPhone + "\' ORDER BY id");
			}

			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByPhone function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByState(Connection dbConn, String state, boolean includeInactives)
	{
		try
		{
			String stateAbr = Utils.getStateAbbreviation(state);
			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE (stst=\'" + stateAbr + "\' OR post=\'" + stateAbr + "\') ");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE (stst=\'" + stateAbr + "\' OR post=\'" + stateAbr + "\') AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE (stst=\'" + stateAbr + "\' OR post=\'" + stateAbr + "\') ORDER BY id");
			}
			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByState function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByZipCode(Connection dbConn, String zipCode, boolean includeInactives)
	{
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE (stzip LIKE (\'%" + zipCode + "%\') OR pozip LIKE (\'%" + zipCode + "%\')) ");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE (stzip LIKE (\'%" + zipCode + "%\') OR pozip LIKE (\'%" + zipCode + "%\')) AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE (stzip LIKE (\'%" + zipCode + "%\') OR pozip LIKE (\'%" + zipCode + "%\')) ORDER BY id");
			}

			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByZipCode function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByCity(Connection dbConn, String city, boolean includeInactives)
	{
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE (stcity LIKE (\'%" + city + "%\') OR pocity LIKE (\'%" + city + "%\')) ");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE (stcity LIKE (\'%" + city + "%\') OR pocity LIKE (\'%" + city + "%\')) AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE (stcity LIKE (\'%" + city + "%\') OR pocity LIKE (\'%" + city + "%\')) ORDER BY id");
			}

			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByCity function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByCharter(Connection dbConn, String charter, boolean includeInactives)
	{
		try
		{
			charter = charter.substring(1);

			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE fcht=" + charter);
			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			rs.next();
			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE fcht=" + charter + " AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE fcht=" + charter + " ORDER BY id");
			}
			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByCharter function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByCUID(Connection dbConn, String id, boolean includeInactives) 
	{
		try
		{
			/* This can never return more than one CUID and really acts as CUID validator. */
			if (id.length()==6 && id.substring(0, 1).equals("0"))
			{
				id = id.substring(1);
			}

			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE id='" + String.format("%5s", id).replace(' ', '0') + "' ");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			rs.next();
			
			String[] theCUID = null;
			if (rs.getInt(1)==1)
			{	
				theCUID = new String[1];
				theCUID[0] = String.format("%5s", id).replace(' ', '0');
				return (theCUID);
			}
			else
			{
				return (new String[0]);
			}
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByCUID function in Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByCustomerID(Connection dbConn, String customerID, boolean includeInactives) 
	{
		try
		{
			/* This can never return more than one CustomerID and really acts as ID validator. */
			
			customerID = customerID.substring(4);
		
			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE customer_id='" + String.format("%8s", customerID).replace(' ', '0') + "' ");
			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			rs.next();

			String[] ids = new String[rs.getInt(1)];

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE customer_id='" + String.format("%8s", customerID).replace(' ', '0') + "' AND Status=\'A\' " + " ORDER BY id");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE customer_id='" + String.format("%8s", customerID).replace(' ', '0') + "' ORDER BY id");
			}
			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			int index = 0;
			while(rs.next())
			{
				ids[index] = rs.getString(1);
				index++;
			}

			return ids;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByCustomerID function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] findByName(Connection dbConn, String fullSearchTerm, boolean includeInactives) 
	{			
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM MasterFile WHERE cu_name LIKE(\'%" + fullSearchTerm + "%\')");

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			rs.next();

			ArrayList ids = new ArrayList();

			if (!includeInactives)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE cu_name LIKE(\'%" + fullSearchTerm + "%\') AND Status=\'A\' ORDER BY cu_name ");
			}
			else
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE cu_name LIKE(\'%" + fullSearchTerm + "%\') ORDER BY cu_name ");
			}

			rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			while(rs.next())
			{
				ids.add(rs.getString(1));
			}

			Object[] aliases = Utils.searchAliases(dbConn, fullSearchTerm, includeInactives);

			for(int i = 0; i < aliases.length; i++)
			{
				if (!ids.contains(aliases[i]))
				{
					ids.add(aliases[i]);
				}
			}

			if(ids.size() > 0)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE id IN (");
				for(int i = 0; i <ids.size(); i++)
				{
					sqlStmt.append(ids.get(i)); 

					if(i < ids.size()-1)
					{
						sqlStmt.append(", "); 
					}
					else
					{
						sqlStmt.append(") "); 
					}
				}

				sqlStmt.append(" ORDER BY cu_name ");
				rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			}

			String[] idArray = new String[ids.size()];
			for(int i = 0; i < ids.size(); i++)
			{
				rs.next();
				idArray[i] = rs.getString(1);				
			}

			return idArray;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in findByName function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] searchAliases(Connection dbConn, String fullSearchTerm, boolean includeInactives) 
	{		
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT DISTINCT(master_customer_id) FROM pyalias WHERE search_name LIKE(\'%" + fullSearchTerm + "%\') AND alias_code='AKA' ");
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			ArrayList ids = new ArrayList();

			while (rs.next())
			{
				ids.add(rs.getString(1));
			}

			if (ids.size() > 0)
			{
				sqlStmt = new StringBuffer("SELECT id, cu_name FROM MasterFile WHERE customer_id IN (");
				for (int i = 0; i < ids.size(); i++)
				{
					sqlStmt.append("'" + ids.get(i) + "'");
					if(i <= ids.size()-2)
					{
						sqlStmt.append(", "); 
					}
					else
					{
						sqlStmt.append(") "); 
					}
				}

				if (!includeInactives)
				{
					sqlStmt.append("AND status='A' "); 
				}

				sqlStmt.append("ORDER BY cu_name "); 

				rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
				ids.clear();;
				while (rs.next())
				{
					ids.add(rs.getString(1));
				}
			}

			String[] idArray = new String[ids.size()];
			for(int i = 0; i < ids.size(); i++)
			{
				idArray[i] = (String) ids.get(i);
			}

			return (idArray);
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in searchAliases function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] advancedSearch(Connection dbConn, String name, String city, String state, String zip, String phone, String url, String ceoLastName, boolean includeInactives, String sortOrder) 
	{		
		try
		{	
			StringBuffer sqlStmt = new StringBuffer("SELECT id FROM MasterFile ");  
			boolean gotWhereKeyword = false;

			if (name.length() > 0)
			{
				sqlStmt.append("WHERE cu_name LIKE (\'%" + name + "%\') ");
				gotWhereKeyword = true;
			}

			if (city.length() > 0)
			{
				if(gotWhereKeyword)
				{
					sqlStmt.append("AND stcity LIKE (\'%" + city + "%\') ");
				}
				else
				{
					sqlStmt.append("WHERE stcity LIKE (\'%" + city + "%\') ");
					gotWhereKeyword = true;
				}
			}

			if (state.length() > 0)
			{
				if(gotWhereKeyword)
				{
					sqlStmt.append("AND stst LIKE (\'%" + Utils.getStateAbbreviation(state) + "%\') ");
				}
				else
				{
					sqlStmt.append("WHERE stst LIKE (\'%" + Utils.getStateAbbreviation(state) + "%\') ");
					gotWhereKeyword = true;
				}
			}

			if (zip.length() > 0)
			{
				if(gotWhereKeyword)
				{
					sqlStmt.append("AND stzip LIKE (\'%" + zip + "%\') ");
				}
				else
				{
					sqlStmt.append("WHERE stzip LIKE (\'%" + zip + "%\') ");
					gotWhereKeyword = true;
				}
			}

			if (phone.length() > 0)
			{
				phone = Pattern.compile("-").matcher(phone).replaceAll("");

				if(gotWhereKeyword)
				{
					sqlStmt.append("AND phone LIKE (\'%" + phone + "%\') ");
				}
				else
				{
					sqlStmt.append("WHERE phone LIKE (\'%" + phone + "%\') ");
					gotWhereKeyword = true;
				}
			}

			if (url.length() > 0)
			{
				url = Pattern.compile("http://").matcher(url).replaceAll("");
				url = Pattern.compile("https://").matcher(url).replaceAll("");
				url = Pattern.compile("www.").matcher(url).replaceAll("");

				if(gotWhereKeyword)
				{
					sqlStmt.append("AND web LIKE (\'%" + url + "%\') ");
				}
				else
				{
					sqlStmt.append("WHERE web LIKE (\'%" + url + "%\') ");
					gotWhereKeyword = true;
				}
			}

			if (ceoLastName.length() > 0)
			{
				if(gotWhereKeyword)
				{
					sqlStmt.append("AND mgrlast LIKE (\'%" + ceoLastName + "%\') ");
				}
				else
				{
					sqlStmt.append("WHERE mgrlast LIKE (\'%" + ceoLastName + "%\') ");
					gotWhereKeyword = true;
				}
			}

			if (!includeInactives)
			{
				sqlStmt.append(" AND Status=\'A\' "); 
			}

			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			ArrayList ids = new ArrayList();
			while(rs.next())
			{
				ids.add(rs.getString(1));
			}

			if (name.length() > 0)
			{
				StringBuffer aliasIDSQL = new StringBuffer(" WHERE id IN (");
				Object[] aliases = Utils.searchAliases(dbConn, name, includeInactives);

				if(aliases.length > 0)
				{

					for(int i = 0; i < aliases.length; i++)
					{
						aliasIDSQL.append("'" + aliases[i] + "'," );
					}

					aliasIDSQL = aliasIDSQL.replace(aliasIDSQL.length()-1, aliasIDSQL.length(), ")");

					String replacementSQL = Pattern.compile("WHERE cu_name LIKE \\('%" + name + "%'\\)").matcher(sqlStmt.toString()).replaceAll(aliasIDSQL.toString());

					rs = dbConn.createStatement().executeQuery(replacementSQL);
					while(rs.next())
					{
						String id = rs.getString(1);
						if (!ids.contains(id))
						{
							ids.add(id);
						}
					}
				}
			}

			if(ids.size() > 0)
			{
				sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE id IN (");
				for(int i = 0; i < ids.size(); i++)
				{
					sqlStmt.append(ids.get(i)); 

					if(i < ids.size()-1)
					{
						sqlStmt.append(", "); 
					}
					else
					{
						sqlStmt.append(") "); 
					}
				}

				if (sortOrder == "CUID")
				{
					sqlStmt.append(" ORDER BY id "); 
				}
				else if (sortOrder == "CU Name")
				{
					sqlStmt.append(" ORDER BY cu_name "); 
				}
				else if (sortOrder == "City")
				{
					sqlStmt.append(" ORDER BY stcity "); 
				}
				else if (sortOrder == "State")
				{
					sqlStmt.append(" ORDER BY stst "); 
				}
				else if (sortOrder == "Zip Code")
				{
					sqlStmt.append(" ORDER BY stzip "); 
				}
				else if (sortOrder == "Phone #")
				{
					sqlStmt.append(" ORDER BY phone "); 
				}
				else if (sortOrder == "Website")
				{
					sqlStmt.append(" ORDER BY web "); 
				}
				else if (sortOrder == "CEO Last Name")
				{
					sqlStmt.append(" ORDER BY mgrlast "); 
				}

				rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			}

			String[] idArray = new String[ids.size()];
			for(int i = 0; i < ids.size(); i++)
			{
				rs.next();
				idArray[i] = rs.getString(1);				
			}

			return idArray;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in advancedSearch function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static String[] getMergers(Connection dbConn, String cuid) 
	{			
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT id FROM MasterFile WHERE surv_id='" + cuid + "' ORDER BY status_chg_date DESC ");
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			ArrayList ids = new ArrayList();

			while(rs.next())
			{
				ids.add(rs.getString(1));
			}

			String[] idArray = new String[ids.size()];
			for(int i = 0; i < ids.size(); i++)
			{
				idArray[i] = (String) ids.get(i);
			}

			return idArray;
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in getMergers function of Utils.");
			e.printStackTrace();
		}

		return null;
	}

	public static long computeDues(Connection dbConn, String cuid, String year) 
	{			
		long assets = -1;
		long members = -1;
		String cunaAffiliated = null;
		String lgAffiliated = null;
		
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT f1.cuid, f1.total_assets, f1.members, f2.afl, f2.league_affiliated " + 
					                                "FROM cuFinancials_" + Integer.toString(Integer.valueOf(year)-1) + "06" + " f1 " +
					                                "LEFT JOIN MasterFile f2 on f1.cuid=f2.id " +
					                                "WHERE f1.cuid=" + cuid);
			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			while(rs.next())
			{
				assets = rs.getLong(2);
				members = rs.getLong(3);
				cunaAffiliated = rs.getString(4);
				lgAffiliated = rs.getString(5);
			}
		}
		catch(Exception e)
		{
			System.out.println("Had a problem in computeDues function of Utils.");
			e.printStackTrace();
		}
						
		double dues = 0;

		if (year.equals("2015"))
		{
			//Assets < 5,000,000 dues=10 cents per member + (.00001*assets) (10/1)
			//Assets >=5,000,000 dues =12 cent per member + (.00002*assets) (12/2)

			if(assets < 5000000)
			{
				dues = (0.10*members)+(0.00001*assets);
			}
			else
			{
				dues = (0.12*members)+(0.00002*assets);
			}
		}
		else if (year.equals("2016"))
		{
			dues = (0.12*members)+(0.000018*assets);

			if(assets < 5000000)
			{
				dues = dues / 2;
			}

			if(dues > 260000)
			{
				dues = 260000 * 0.9;
			}
		}
		else if (year.equals("2017"))
		{
			dues = (0.12*members)+(0.000018*assets);

			if(assets < 5000000)
			{
				dues = dues / 2;
			}

			if(dues > 261000)
			{
				dues = 261000 * 0.009;
			}

			if(cunaAffiliated.equals("A") && lgAffiliated.equals("A"))
			{
				dues = dues * 0.975; 
			}
		}

		return (Math.round(dues));
	}

	public static void main(String[] args) 
	{
		String charterTest = "#66710";
		String cuidTest = "032999";
		String custIDTest = "cust00037030";
		String nameTest = "Heartland CU";

		try
		{
			//String[] results = search(charterTest, false);
			//System.out.println("found == " + results[0]);

			//System.out.println(Utils.convertPeriod(Utils.financialPeriods[1]));
			//System.out.println(Utils.convertToLongPeriod("201603"));

			//String[] results2 = search(cuidTest);
			//System.out.println("found == " + results2[0]);

			Connection dbConn = Utils.getConnection();

			String[] results3 = Utils.search(dbConn, custIDTest, false);

			System.out.println("found == " + results3[0]);

			//String[] results4 = search(nameTest, false);
			//System.out.println("found == " + results4[3]);


			//Object[] results5 = searchAliases(dbConn, "Telco", false) ;
			//System.out.println("found == " + results5.length);

			//String[] mergees = Utils.getMergers(dbConn, "32999");
			//System.out.println("found == " + mergees.length);

			long dues = Utils.computeDues(dbConn, "32999", "2016");
			System.out.println(dues);

			String[] ids = Utils.findByCEOLastName(dbConn, "dischler", false);

			System.out.println("Found " + ids.length + " CUs in Superior");
			System.out.println("First found was id# " + ids[0]);

			System.out.println(Utils.getStateAbbreviation("Colorado"));


			ids = Utils.advancedSearch(dbConn, "Telco","", "", "", "", "", "", false, "CUID");
			System.out.println("Found " + ids.length + " CUs in Superior");
			System.out.println("First found was id# " + ids[0]);

			for(int i = 0; i < ids.length; i++)
			{
				System.out.println(ids[i]);
			}

			Utils.closeConnection(dbConn);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			e.getMessage();
		}

		System.out.println("\\r\\n......Done, and Done-er....");

	}
}
