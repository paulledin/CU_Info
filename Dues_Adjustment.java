package coop.cuna.cdean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.NumberFormat;

public class Dues_Adjustment 
{
	public Dues_Adjustment(Connection dbConn, String cuid, String year)
	{
		id = cuid;
		nf.setMaximumFractionDigits(0);

		if (year.equals("2016"))
		{

			try
			{
				StringBuffer sqlStmt = new StringBuffer("SELECT cuid, status_current, afl_current, lg_afl, lg, name_jeanne_file, stst, members_june2015, " + 
						"assets_june2015, dues_2016, collected, reafl_100pct, dues_lost_from_midyr_afl, dues_lost_from_reafl_init, " + 
						"disafl_dues, dues_paid_by_disafls, hardship_waivers, dues_col_from_mergers, dues_lost_from_inactives, other " +
						"FROM Dues_Adjustments_" + year + " " +
						"WHERE cuid='" + cuid + "'");
				ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

				while(rs.next())
				{
					status = rs.getString(2);
					cuna_affiliation = rs.getString(3);
					lg_affiliation = rs.getString(4);
					lg_code = rs.getString(5);
					name = rs.getString(6);
					state = rs.getString(7);
					members = rs.getLong(8);
					assets = rs.getLong(9);
					dues = rs.getLong(10);
					collected = rs.getLong(11);
					refl_100_pct = rs.getLong(12);
					mid_year_afl_dues_lost = rs.getLong(13);
					realf_init_lost_dues = rs.getLong(14);
					dues_lost_from_disafls = rs.getLong(15);
					dues_paid_by_disafls = rs.getLong(16);
					hardship_waivers = rs.getLong(17);
					dues_collected_from_mergers = rs.getLong(18);
					dues_lost_from_inactives = rs.getLong(19);
					other = rs.getLong(20);
				}
			}
			catch(Exception e)
			{
				System.out.println("Had a problem in getting dues adjustments data.");
				e.printStackTrace();
			}
		}
	}

	public String getCUID()
	{
		return (id);
	}

	public String getCUNAAffiliation()
	{
		return (cuna_affiliation);
	}

	public String getLeagueAffiliation()
	{
		return (lg_affiliation);
	}

	public String getLeagueCode()
	{
		return (lg_code);
	}

	public String getName()
	{
		return (name);
	}

	public String getState()
	{
		return (state);
	}

	public String getMembers()
	{
		try
		{
			if (members != -1)
			{
				return(nf.format(members));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getAssets()
	{
		try
		{
			if (assets != -1)
			{
				return(nf.format(assets));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getDues()
	{
		try
		{
			if (dues != -1)
			{
				return(nf.format(dues));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getReAfl100Pct()
	{
		try
		{
			if (refl_100_pct != -1)
			{
				return(nf.format(refl_100_pct));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getMidYrAflDuesLost()
	{
		try
		{
			if (mid_year_afl_dues_lost != -1)
			{
				return(nf.format(mid_year_afl_dues_lost));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getReAflInitLostDues()
	{
		try
		{
			if (realf_init_lost_dues != -1)
			{
				return(nf.format(realf_init_lost_dues));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getDuesLostFromDisAfls()
	{
		try
		{
			if (dues_lost_from_disafls != -1)
			{
				return(nf.format(dues_lost_from_disafls));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}
	
	public String getDuesPaidByDisAfls()
	{
		try
		{
			if (dues_paid_by_disafls != -1)
			{
				return(nf.format(dues_paid_by_disafls));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getHardshipWaivers()
	{
		try
		{
			if (hardship_waivers != -1)
			{
				return(nf.format(hardship_waivers));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getDuesCollectedFromMergers()
	{
		try
		{
			if (dues_collected_from_mergers != -1)
			{
				return(nf.format(dues_collected_from_mergers));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getDuesLostFromInactives()
	{
		try
		{
			if (dues_lost_from_inactives != -1)
			{
				return(nf.format(dues_lost_from_inactives));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getOtherDues()
	{
		try
		{
			if (other != -1)
			{
				return(nf.format(other));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}
	
	public String getCollected()
	{
		try
		{
			if (collected != -1)
			{
				return(nf.format(collected));
			}
			else
			{
				return ("N/A");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public static void main(String[] args) 
	{
		try
		{
			Connection dbConn = Utils.getConnection();

			Dues_Adjustment heartland = new Dues_Adjustment(dbConn, "32999", "2016");
			System.out.println(heartland.getAssets());



			System.out.println("\\r\\n......Done, and Done-er....");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private String id = null;
	private String status = null;
	private String cuna_affiliation = null;
	private String lg_affiliation = null;
	private String lg_code = null;
	private String name = null;
	private String state = null;
	private long members = -1;
	private long assets = -1;
	private long dues = -1;
	private long collected = -1;
	private long refl_100_pct = -1;
	private long mid_year_afl_dues_lost = -1;
	private long realf_init_lost_dues = -1;
	private long dues_lost_from_disafls = -1;
	private long dues_paid_by_disafls = -1;
	private long hardship_waivers = -1;
	private long dues_collected_from_mergers = -1;
	private long dues_lost_from_inactives = -1;
	private long other = -1;
	
	private NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
}
