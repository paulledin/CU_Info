package coop.cuna.cdean;

import java.sql.*;
import java.text.*;

public class CreditUnion 
{
	public CreditUnion(Connection dbConn, String cuid, String period) 
	{
		try
		{
			setFinancialsPeriod(period);

			StringBuffer sqlStmt = new StringBuffer("SELECT f1.id, f1.customer_id, f1.cu_name, f1.status, f1.abanum, f1.fcht, f1.charter_type, f3.League, f1.org_date, f1.status_chg_date, " + 
					"f1.common_bond, f1.tom_code, f1.afl, f1.league_affiliated, f1.lg, concat(substring(f1.phone,1,3),\'-\',substring(f1.phone,4,3),\'-\',substring(f1.phone,7,4)), " +
					"concat(substring(f1.fax,1,3),\'-\',substring(f1.fax,4,3),\'-\',substring(f1.fax,7,4)), f1.web, f1.staddr, f1.stcity, f1.stst, " +
					"f1.stzip, f1.stcountry, f1.stcounty, f1.stcong, f1.st_fips_county, f1.stlong, f1.stlat, f1.poaddr, f1.pocity, f1.post, f1.pozip, " + 
					"f1.pocountry, f1.pocounty, f1.pocong, f1.po_fips_county, f1.polong, f1.polat, f1.mgr_formal_salutation, f1.surv_id,  " +
					"f2.limited_inc, f2.tom_code, f2.total_assets, f2.members, f2.branches, f2.pca_net_worth_ratio, f2.pca_classification, f2.total_loans, f2.total_shares_and_deposits, " +
					"f2.share_drafts, f2.offer_business_loans, f2.offer_credit_cards, (f2.first_mortgage_loans_fixed+f2.first_mortgage_loans_adj), f2.offer_atm_debit_card_program, f2.offer_bilingual_services, " +				
					"f2.offer_risk_based_loans, f2.offer_payday_loans, f2.offer_payday_alternative_loans, f1.total_assets, f1.members, f1.tom_desc, f4.cuid " + 
					"FROM MasterFile f1 " +
					"LEFT JOIN cuFinancials_" + financialsPeriod + " f2 ON f1.id=f2.cuid " +
					"LEFT JOIN League_Codes f3 ON f1.lg=f3.League_Code " + 
					"LEFT JOIN mblGrandfatheredCUs f4 on f1.id=f4.cuid " + 
					"WHERE f1.id=\'" + cuid + "\'");

			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());

			while(rs.next())
			{
				id = rs.getString(1);
				customer_id = rs.getString(2);
				name = rs.getString(3);
				status = rs.getString(4);
				abanum = rs.getString(5);
				charter = rs.getInt(6);
				charter_type = rs.getString(7);
				lgCode = rs.getString(8);
				org_date = rs.getString(9);
				status_chg_date = rs.getString(10);
				common_bond = rs.getString(11);
				tom = rs.getString(12);
				cuna_afl = rs.getString(13); 
				league_afl = rs.getString(14);
				afl_league_code = rs.getString(15); 
				phone = rs.getString(16);
				fax = rs.getString(17);
				web = rs.getString(18);
				street_address = rs.getString(19);
				street_city = rs.getString(20);
				street_state = rs.getString(21);
				street_zip = rs.getString(22);
				street_country = rs.getString(23);
				street_county = rs.getString(24);
				street_cong_dist = rs.getString(25);
				street_fips_county = rs.getString(26);
				street_longitude = rs.getString(27);
				street_latitude = rs.getString(28);
				postal_address = rs.getString(29);
				postal_city = rs.getString(30);
				postal_state = rs.getString(31);
				postal_zip = rs.getString(32);
				postal_country = rs.getString(33);
				postal_county = rs.getString(34);
				postal_cong_dist = rs.getString(35);
				postal_fips_county = rs.getString(36);
				postal_longitude = rs.getString(37);
				postal_latitude = rs.getString(38);
				ceo = rs.getString(39);
				survivor_id = rs.getString(40); 
				limitedIncInd = rs.getInt(41);
				ncua_tom = rs.getInt(42);
				assets = rs.getLong(43);
				members = rs.getLong(44);
				branches = rs.getInt(45);
				pca_net_worth_ratio = rs.getInt(46);
				pca_classificaiton = rs.getString(47);
				loans = rs.getLong(48);
				savings = rs.getLong(49);
				share_drafts = rs.getLong(50);
				offer_mbls = rs.getInt(51);
				offer_credit_cards = rs.getInt(52);
				first_mortgages_outstanding = rs.getLong(53);
				offer_atm_or_debit = rs.getInt(54);
				offer_bilingual_services = rs.getInt(55);
				offer_risk_based_loans = rs.getInt(56);
				offer_payday_loans = rs.getInt(57);
				offer_payday_alternative_loans = rs.getInt(58);
				lastReportedAssets = rs.getLong(59);
				lastReportedMembers = rs.getLong(60);
				tomDescription = rs.getString(61);
				mblGrandfathered = rs.getString(62);
			}
			
			mergees = Utils.getMergers(dbConn, id);
		}
		catch(SQLException sqle)
		{
			System.out.println("Had a SQL Exception loading the credit union data in the constructor.");
			sqle.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try
		{
			Connection dbConn = Utils.getConnection();

			CreditUnion heartland = new CreditUnion(dbConn, "13583", Utils.convertPeriod(Utils.financialPeriods[0]));
			System.out.println(heartland.getName());
			System.out.println(heartland.getCEO());
			System.out.println(heartland.getTomDescription());
			System.out.println(heartland.isMBLGrandfathered());
			System.out.println(heartland.isMBLExempt());
			System.out.println(heartland.getAssets());
			System.out.println(heartland.getMembers());
			System.out.println(heartland.getPCANWRatio());
			System.out.println(heartland.getCunaAffiliation());
			System.out.println(heartland.getLeagueAffiliation());


			System.out.println("\\r\\n......Done, and Done-er....");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getID()
	{
		return id;
	}

	public String getCustomerID()
	{
		return customer_id;
	}

	public String getName()
	{
		return name;
	}

	public String getStatus()
	{
		if (status.equals("A"))
		{
			return ("Active");
		}
		else if (status.equals("L"))
		{
			return ("Liquidated");
		}
		else if (status.equals("M"))
		{
			return ("Merged");
		}
		else if (status.equals("P"))
		{
			return ("Pending Merger");
		}
		else if (status.equals("Q"))
		{
			return ("Pending Liq/P&A");
		}
		else if (status.equals("U"))
		{
			return ("Purchased & Assumed");
		}
		else if (status.equals("Z"))
		{
			return ("Pending Unknown");
		}
		return status;
	}

	public String getABAnumber()
	{
		return abanum;
	}

	public int getCharter()
	{
		return charter;
	}

	public String getCharterType()
	{
		return charter_type;
	}

	public String getLeagueCode()
	{
		return lgCode;
	}

	public String getOrganizationDate()
	{
		if (org_date != null)
		{
			String year = org_date.substring(0, 4);
			String month = org_date.substring(5, 7);
			String day = org_date.substring(8, 10);

			if (month.equals("01"))
			{
				month = "January";
			}
			else if (month.equals("02"))
			{
				month = "February";
			}
			else if (month.equals("03"))
			{
				month = "March";
			}
			else if (month.equals("04"))
			{
				month = "April";
			}
			else if (month.equals("05"))
			{
				month = "May";
			}
			else if (month.equals("06"))
			{
				month = "June";
			}
			else if (month.equals("07"))
			{
				month = "July";
			}
			else if (month.equals("08"))
			{
				month = "August";
			}
			else if (month.equals("09"))
			{
				month = "September";
			}
			else if (month.equals("10"))
			{
				month = "October";
			}
			else if (month.equals("11"))
			{
				month = "Novemeber";
			}
			else
			{
				month = "December";
			}

			return (day + "-" + month + "-" + year);
		}
		else
		{
			return ("N/A");
		}
	}

	public String getStatusChangeDate()
	{
		return status_chg_date;
	}

	public String getCommonBond()
	{
		return common_bond;
	}

	public String getTomCode()
	{
		return tom;
	}

	public String getTomDescription()
	{
		return tomDescription;
	}

	public String getCunaAffiliation()
	{
		if (cuna_afl.equals("A"))
		{
			return ("Yes");
		}
		else
		{
			return ("No");
		}
	}

	public String getLeagueAffiliation()
	{
		if (league_afl.equals("A"))
		{
			return ("Yes");
		}
		else
		{
			return ("No");
		}
	}

	public String getLeagueAffiliatedLeague()
	{
		if (league_afl.equals("A"))
		{
			return (afl_league_code);
		}
		else
		{
			return ("NA");
		}
	}

	public String getPhoneNumber()
	{
		return phone;
	}

	public String getFaxNumber()
	{
		return fax;
	}

	public String getWebsite()
	{
		return web;
	}

	public String getStreetAddress()
	{
		return street_address;
	}

	public String getStreetCity()
	{
		return street_city;
	}

	public String getStreetState()
	{
		return street_state;
	}

	public String getStreetZip()
	{
		return street_zip;
	}

	public String getStreetCountry()
	{
		return street_country;
	}

	public String getStreetCounty()
	{
		return street_county;
	}

	public String getStreetCongDistrict()
	{
		return street_cong_dist;
	}

	public String getStreetFIPSCounty()
	{
		return street_fips_county;
	}

	public String getStreetLongitude()
	{
		return street_longitude;
	}

	public String getStreetLatitude()
	{
		return street_latitude;
	}

	public String getPostalAddress()
	{
		return postal_address;
	}

	public String getPostalCity()
	{
		return postal_city;
	}

	public String getPostalState()
	{
		return postal_state;
	}

	public String getPostalZip()
	{
		return postal_zip;
	}

	public String getPostalCountry()
	{
		return postal_country;
	}

	public String getPostalCounty()
	{
		return postal_county;
	}

	public String getPostalCongDistrict()
	{
		return postal_cong_dist;
	}

	public String getPostalFIPSCounty()
	{
		return postal_fips_county;
	}

	public String getPostalLongitude()
	{
		return postal_longitude;
	}

	public String getPostalLatitude()
	{
		return postal_latitude;
	}

	public String getCEO()
	{
		return ceo;
	}

	public String getSurvivorID()
	{	
		return (survivor_id);
	}

	public String getLowIncomeDesignated()
	{
		if(assets != 0)
		{
			if (limitedIncInd==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String isMBLGrandfathered()
	{
		if(mblGrandfathered == null)
		{
			return ("No");
		}
		else
		{
			return ("Yes");
		}
	}

	public String isMBLExempt()
	{
		if(assets != 0)
		{
			if(isMBLGrandfathered().equals("Yes") || charter>80000 || limitedIncInd==1)
			{
				return "Yes";
			}
			else
			{
				return "No";
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String getPeriod()
	{
		String monthAndDay = null;

		if (financialsPeriod.substring(4).equals("03"))
		{
			monthAndDay = "March 31, " + financialsPeriod.substring(0, 4);

		}
		else if (financialsPeriod.substring(4).equals("06"))
		{
			monthAndDay = "March 30, " + financialsPeriod.substring(0, 4);
		}
		else if (financialsPeriod.substring(4).equals("09"))
		{
			monthAndDay = "September 30, " + financialsPeriod.substring(0, 4);
		}
		else
		{
			monthAndDay = "December 31, " + financialsPeriod.substring(0, 4);
		}

		return (monthAndDay);
	}

	public String getAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

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
	
	public long getUnformattedAssets()
	{
		return (assets);
	}
	
	public long getUnformattedMembers()
	{
		return (members);
	}

	public String getMembers()
	{
		System.out.println("Hello From getMembers()");
		
		
		NumberFormat nf = NumberFormat.getIntegerInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

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

	public String getBranches()
	{
		NumberFormat nf = NumberFormat.getIntegerInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			return(nf.format(branches));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("No Branches Reported");
	}

	public String getPCANWRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMinimumFractionDigits(2);		
		double nwAsDouble = new Double(pca_net_worth_ratio).doubleValue() / 100 / 100;

		try
		{	
			if (assets != 0)
			{
				return(nf.format(nwAsDouble));
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

		return ("No PCA NW Ratio Reported");
	}

	public String getPCAClassification()
	{
		if (assets != 0)
		{
			return(pca_classificaiton);
		}
		else
		{
			return ("N/A");
		}
	}

	public String getLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (assets != 0)
			{
				return(nf.format(loans));
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

	public String getSavings()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (assets != 0)
			{
				return(nf.format(savings));
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

		return ("No Savings Reported");
	}

	public String offerShareDrafts()
	{
		if(assets != 0)
		{
			if (share_drafts>0)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerMBLs()
	{
		if (assets != 0)
		{
			if (offer_mbls==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerCreditCards()
	{
		if (assets != 0)
		{
			if (offer_credit_cards==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerFirstMortgages()
	{
		if(assets !=0)
		{
			if (first_mortgages_outstanding>0)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}

	}

	public String offerAtmOrDebit()
	{
		if(assets != 0)
		{
			if (offer_atm_or_debit==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerBilingualServices()
	{
		if(assets != 0)
		{
			if (offer_bilingual_services==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerRiskBasedLoans()
	{
		if(assets != 0)
		{
			if (offer_risk_based_loans==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerPaydayLoans()
	{
		if(assets != 0)
		{
			if (offer_payday_loans==1)
			{
				return ("Yes");
			}
			else
			{
				return ("No");
			}
		}
		else
		{
			return ("N/A");
		}
	}

	public String offerPALLoans()
	{
		if(assets != 0)
		{
			if (charter_type == null || !charter_type.equals("INSURED-FED"))
			{
				return ("N/A");
			}
			else
			{
				if (offer_payday_alternative_loans==1)
				{
					return ("Yes");
				}
				else
				{
					return ("No");
				}
			}
		}
		{
			return ("N/A");
		}
	}

	public String getFinancialsPeriod()
	{
		return (financialsPeriod);
	}

	public void setFinancialsPeriod(String period)
	{
		financialsPeriod = period;
	}

	public String[] getMergees()
	{
		return mergees;
	}
	
	public long getLastReportedAssets()
	{
		return (lastReportedAssets);
	}
	
	public long getLastReportedMembers()
	{
		return (lastReportedMembers);
	}

	private String financialsPeriod = "201512";

	private String id = "NA";
	private String customer_id = "NA";
	private String name = "No Credit Union Found";
	private String status = "NA";
	private String abanum = "NA";
	private int charter = 99999;
	private String charter_type = "NA"; 
	private String lgCode = "NA";
	private String org_date = "NA";
	private String status_chg_date = "NA";
	private String common_bond = "NA";
	private String tom = "NA";
	private String cuna_afl = "NA";
	private String league_afl = "NA";
	private String afl_league_code = "NA";
	private String phone = "NA";
	private String fax = "NA";
	private String web = "";
	private String street_address = "";
	private String street_city = "";
	private String street_state = "";
	private String street_zip = "";
	private String street_country = "";
	private String street_county = "";
	private String street_cong_dist = "";
	private String street_fips_county = "";
	private String street_longitude = "";
	private String street_latitude = "";
	private String postal_address = "";
	private String postal_city = "";
	private String postal_state = "";
	private String postal_zip = "";
	private String postal_country = "";
	private String postal_county = "";
	private String postal_cong_dist = "";
	private String postal_fips_county = "";
	private String postal_longitude = "";
	private String postal_latitude = "";
	private String ceo = "";
	private String survivor_id = "99999";
	private int limitedIncInd = -1;
	private int ncua_tom = -1;
	private long assets = -1;
	private long members = -1;
	private int branches = -1;
	private int pca_net_worth_ratio = -1;
	private String pca_classificaiton = "";
	private long loans = -1;
	private long savings = -1;
	private long share_drafts = -1;
	private int offer_mbls = -1;
	private int offer_credit_cards = -1;
	private long first_mortgages_outstanding = -1;
	private int offer_atm_or_debit = -1;
	private int offer_bilingual_services = -1; 
	private int offer_risk_based_loans = -1;
	private int offer_payday_loans = -1;
	private int offer_payday_alternative_loans = -1;

	private String[] mergees = null;
	private String tomDescription = null;
	private String mblGrandfathered = null;
	
	private long lastReportedAssets = -1;
	private long lastReportedMembers = -1;
}
