package coop.cuna.cdean;

import java.sql.*;
import java.text.*;
import java.math.*;

public class TwoYearFinancialSummary 
{
	public TwoYearFinancialSummary(Connection dbConn, String cuid, String period) 
	{
		try
		{
			StringBuffer sqlStmt = new StringBuffer("SELECT f1.cuid, f1.cash_on_hand, f1.cash_on_deposit, f1.cash_equivalents, f1.government_securities, f1.federal_agency_securities, f1.corporate_credit_unions, f1.bank_deposits, f1.mutual_funds, f1.all_other_investments, f1.loans_held_for_sale, " + 
					"f2.cash_on_hand, f2.cash_on_deposit, f2.cash_equivalents, f2.government_securities, f2.federal_agency_securities, f2.corporate_credit_unions, f2.bank_deposits, f2.mutual_funds, f2.all_other_investments, f2.loans_held_for_sale, " +
					"f1.amt_of_credit_cards, f1.other_unsecured_loans, f1.amt_of_pal_lns, f1.edu_loans, f1.new_auto_loans, f1.used_auto_loans, f1.amt_of_1st_mortgages, f1.amt_of_2nd_mortgages, f1.amt_of_leases, f1.all_other_loans, f1.total_loans, f1.loan_loss_allowance, f1.total_member_business_loans, f1.total_unfunded_MBL_commitments," +
					"f2.amt_of_credit_cards, f2.other_unsecured_loans, f2.amt_of_pal_lns, f2.edu_loans, f2.new_auto_loans, f2.used_auto_loans, f2.amt_of_1st_mortgages, f2.amt_of_2nd_mortgages, f2.amt_of_leases, f2.all_other_loans, f2.total_loans, f2.loan_loss_allowance, f2.total_member_business_loans, f2.total_unfunded_MBL_commitments, " +
					"f1.oreos, f1.land_and_building, f1.other_fixed_assets, f1.share_ins_capital_deposit, f1.intangible_assets, f1.total_intangible_and_other_assets-f1.intangible_assets, f1.total_assets, f1.loans_granted, f1.amt_of_pal_lns_granted_ytd, f1.amt_deferred_edu_loans, f1.amt_loans_to_execs, " +
					"f2.oreos, f2.land_and_building, f2.other_fixed_assets, f2.share_ins_capital_deposit, f2.intangible_assets, f2.total_intangible_and_other_assets-f2.intangible_assets, f2.total_assets, f2.loans_granted, f2.amt_of_pal_lns_granted_ytd, f2.amt_deferred_edu_loans, f2.amt_loans_to_execs, " +
					"f1.reverse_repurchase_agreements, f1.other_notes_payable, f1.all_other_liabilities, f1.regular_shares_and_deposits, f1.share_drafts, f1.money_market_accounts, f1.ira_and_keogh, f1.share_certificates, (f1.regular_reserves+f1.other_reserves), f1.undivided_earnings, " +
					"f2.reverse_repurchase_agreements, f2.other_notes_payable, f2.all_other_liabilities, f2.regular_shares_and_deposits, f2.share_drafts, f2.money_market_accounts, f2.ira_and_keogh, f2.share_certificates, (f2.regular_reserves+f2.other_reserves), f2.undivided_earnings,  " +
					"f1.interest_on_loans, f1.rebates, f1.fee_income, f1.investment_income, f1.other_operating_income, f1.total_income, f1.salary_and_benefits_exp, f1.office_occupancy_exp, f1.office_operations_exp, f1.educational_and_promotional_exp, f1.loan_servicing_exp, f1.professional_and_outside_services_exp, f1.member_insurance_exp, f1.all_other_expenses, f1.expense_subtotal, f1.provision_for_loan_loss, " +
					"f2.interest_on_loans, f2.rebates, f2.fee_income, f2.investment_income, f2.other_operating_income, f2.total_income, f2.salary_and_benefits_exp, f2.office_occupancy_exp, f2.office_operations_exp, f2.educational_and_promotional_exp, f2.loan_servicing_exp, f2.professional_and_outside_services_exp, f2.member_insurance_exp, f2.all_other_expenses, f2.expense_subtotal, f2.provision_for_loan_loss, " +
					"f1.expense_subtotal_incl_provisions, f1.non_operating_gain_loss, f1.income_before_dividends_and_interest, f1.interest_on_borrowings, f1.interest_on_dividends_and_savings, f1.cost_of_funds_subtotal, f1.transfers_to_reserves, f1.other_capital_transfers, f1.net_income_after_stabilization_exp, " +
					"f2.expense_subtotal_incl_provisions, f2.non_operating_gain_loss, f2.income_before_dividends_and_interest, f2.interest_on_borrowings, f2.interest_on_dividends_and_savings, f2.cost_of_funds_subtotal, f2.transfers_to_reserves, f2.other_capital_transfers, f2.net_income_after_stabilization_exp, " +
					"f1.delinquent_loans_2_to_6_mo, f1.delinquent_loans_6_to_12_mo, f1.delinquent_loans_over_12_mo, f1.total_delinquent_loans, f1.net_charge_offs, " +
					"f2.delinquent_loans_2_to_6_mo, f2.delinquent_loans_6_to_12_mo, f2.delinquent_loans_over_12_mo, f2.total_delinquent_loans, f2.net_charge_offs, " +
					"f1.pca_net_worth_ratio, f1.pca_classification, (f1.total_capital+f1.loan_loss_allowance), f1.total_capital, f1.oreos,  " +
					"f2.pca_net_worth_ratio, f2.pca_classification, (f2.total_capital+f2.loan_loss_allowance), f2.total_capital, f2.oreos, " +
					"f3.total_assets, f3.total_loans, " + 
					"f4.total_assets, f4.total_loans, " +
					"f1.members, f1.potential_members, f1.part_time_employees, f1.full_time_employees, f1.branches, f1.member_of_fhlb, " +
					"f2.members, f2.potential_members, f2.part_time_employees, f2.full_time_employees, f2.branches, f2.member_of_fhlb " +
					"FROM cuFinancials_" + period  + " f1 " +
					"JOIN cuFinancials_" + (Integer.toString(Integer.valueOf(period.substring(0,4))-1)) + period.substring(4) + " f2 ON f1.cuid=f2.cuid " +
					"JOIN cuFinancials_" + (Integer.toString(Integer.valueOf(period.substring(0,4))-1)) + "12" + " f3 ON f1.cuid=f3.cuid " +
					"JOIN cuFinancials_" + (Integer.toString(Integer.valueOf(period.substring(0,4))-2)) + "12" + " f4 ON f1.cuid=f4.cuid " +
					"WHERE f1.cuid=\'" + cuid + "\' ");

			if(period.substring(4).equals("03"))
			{
				annualizationFactor = (double) (4/1);
			}
			else if(period.substring(4).equals("06"))
			{
				annualizationFactor = (double) (4/2);
			}
			else if(period.substring(4).equals("09"))
			{
				annualizationFactor = (double) (4/3);
			}
			else
			{
				annualizationFactor = (double) (4/4);
			}

			ResultSet rs = dbConn.createStatement().executeQuery(sqlStmt.toString());
			while(rs.next())
			{	
				id = rs.getString(1);
				curr_cash_on_hand = rs.getLong(2);
				curr_cash_on_deposit = rs.getLong(3);
				curr_cash_equivalents = rs.getLong(4);
				curr_government_securities = rs.getLong(5); 
				curr_federal_agency_securities = rs.getLong(6);
				curr_corporate_credit_unions = rs.getLong(7); 
				curr_bank_deposits = rs.getLong(8); 
				curr_mutual_funds = rs.getLong(9);
				curr_all_other_investments = rs.getLong(10); 
				curr_loans_held_for_sale = rs.getLong(11); 

				prev_cash_on_hand = rs.getLong(12);
				prev_cash_on_deposit = rs.getLong(13);
				prev_cash_equivalents = rs.getLong(14);
				prev_government_securities = rs.getLong(15); 
				prev_federal_agency_securities = rs.getLong(16);
				prev_corporate_credit_unions = rs.getLong(17); 
				prev_bank_deposits = rs.getLong(18); 
				prev_mutual_funds = rs.getLong(19);
				prev_all_other_investments = rs.getLong(20);
				prev_loans_held_for_sale = rs.getLong(21);

				curr_cash_and_equivalents = curr_cash_on_hand + curr_cash_on_deposit + curr_cash_equivalents;  
				curr_total_investments = curr_government_securities + curr_federal_agency_securities + curr_corporate_credit_unions + curr_bank_deposits + curr_mutual_funds + curr_all_other_investments;

				prev_cash_and_equivalents = prev_cash_on_hand + prev_cash_on_deposit + prev_cash_equivalents;  
				prev_total_investments = prev_government_securities + prev_federal_agency_securities + prev_corporate_credit_unions + prev_bank_deposits + prev_mutual_funds + prev_all_other_investments;

				curr_credit_cards = rs.getLong(22);
				curr_other_unsecured_loans = rs.getLong(23);
				curr_pal_loans = rs.getLong(24);
				curr_edu_loans = rs.getLong(25);
				curr_new_auto_loans = rs.getLong(26);
				curr_used_auto_loans = rs.getLong(27);
				curr_first_mortgages = rs.getLong(28);
				curr_second_mortgages = rs.getLong(29);
				curr_leases = rs.getLong(30);
				curr_all_other_loans = rs.getLong(31);
				curr_total_loans= rs.getLong(32);
				curr_loan_loss_allowance = rs.getLong(33);
				curr_mbls = rs.getLong(34);
				curr_unfunded_mbl_commitments = rs.getLong(35);

				prev_credit_cards = rs.getLong(36);
				prev_other_unsecured_loans = rs.getLong(37);
				prev_pal_loans = rs.getLong(38);
				prev_edu_loans = rs.getLong(39);
				prev_new_auto_loans = rs.getLong(40);
				prev_used_auto_loans = rs.getLong(41);
				prev_first_mortgages = rs.getLong(42);
				prev_second_mortgages = rs.getLong(43);
				prev_leases = rs.getLong(44);
				prev_all_other_loans = rs.getLong(45);
				prev_total_loans= rs.getLong(46);
				prev_loan_loss_allowance = rs.getLong(46);
				prev_mbls = rs.getLong(48);
				prev_unfunded_mbl_commitments = rs.getLong(49);

				curr_forclosed_and_repo_assets = rs.getLong(50);
				curr_land_and_building = rs.getLong(51);
				curr_other_fixed_assets = rs.getLong(52);
				curr_share_insurance_deposit = rs.getLong(53);
				curr_intangible_assets = rs.getLong(54);
				curr_other_assets = rs.getLong(55);
				curr_total_assets = rs.getLong(56);
				curr_loans_granted_ytd = rs.getLong(57);
				curr_pal_loans_granted_ytd = rs.getLong(58);
				curr_deferred_edu_loans = rs.getLong(59);
				curr_loans_to_execs = rs.getLong(60);

				prev_forclosed_and_repo_assets = rs.getLong(61);
				prev_land_and_building = rs.getLong(62);
				prev_other_fixed_assets = rs.getLong(63);
				prev_share_insurance_deposit = rs.getLong(64);
				prev_intangible_assets = rs.getLong(65);
				prev_other_assets = rs.getLong(66);
				prev_total_assets = rs.getLong(67);
				prev_loans_granted_ytd = rs.getLong(68);
				prev_pal_loans_granted_ytd = rs.getLong(69);
				prev_deferred_edu_loans = rs.getLong(70);
				prev_loans_to_execs = rs.getLong(71);

				curr_reverse_repos = rs.getLong(72);
				curr_other_notes_payable = rs.getLong(73);
				curr_all_other_liabilities = rs.getLong(74);
				curr_regular_shares = rs.getLong(75);
				curr_share_drafts = rs.getLong(76);
				curr_mmas = rs.getLong(77);
				curr_ira_keoghs = rs.getLong(78);
				curr_certificates = rs.getLong(79);
				curr_reserves = rs.getLong(80);
				curr_undivided_earnings = rs.getLong(81);

				curr_total_liabilities = curr_reverse_repos + curr_other_notes_payable + curr_all_other_liabilities;  
				curr_total_shares_and_deposits = curr_regular_shares + curr_share_drafts + curr_mmas + curr_ira_keoghs + curr_certificates;
				curr_total_capital = curr_reserves + curr_undivided_earnings;
				curr_total_liabilities_and_capital = curr_total_liabilities + curr_total_shares_and_deposits + curr_total_capital;

				prev_reverse_repos = rs.getLong(82);
				prev_other_notes_payable = rs.getLong(83);
				prev_all_other_liabilities = rs.getLong(84);
				prev_regular_shares = rs.getLong(85);
				prev_share_drafts = rs.getLong(86);
				prev_mmas = rs.getLong(87);
				prev_ira_keoghs = rs.getLong(88);
				prev_certificates = rs.getLong(89);
				prev_reserves = rs.getLong(90);
				prev_undivided_earnings = rs.getLong(91);

				prev_total_liabilities = prev_reverse_repos + prev_other_notes_payable + prev_all_other_liabilities;  
				prev_total_shares_and_deposits = prev_regular_shares + prev_share_drafts + prev_mmas + prev_ira_keoghs + prev_certificates;
				prev_total_capital = prev_reserves + prev_undivided_earnings;
				prev_total_liabilities_and_capital = prev_total_liabilities + prev_total_shares_and_deposits + prev_total_capital;

				curr_loan_and_lease_interest = rs.getLong(92);
				curr_rebates = rs.getLong(93);
				curr_fee_income = rs.getLong(94);
				curr_investment_income = rs.getLong(95);
				curr_other_operating_income = rs.getLong(96);
				curr_total_income = rs.getLong(97);
				curr_salaries_and_benefits = rs.getLong(98);
				curr_office_occupancy = rs.getLong(99);
				curr_office_operations = rs.getLong(100);
				curr_education_and_promotion = rs.getLong(101);
				curr_loan_servicing = rs.getLong(102);
				curr_professional_and_outside_services = rs.getLong(103);
				curr_member_insurance = rs.getLong(104);
				curr_all_other_expenses = rs.getLong(105);
				curr_expense_subtotal = rs.getLong(106);
				curr_provision_for_loan_loss = rs.getLong(107);

				prev_loan_and_lease_interest = rs.getLong(108);
				prev_rebates = rs.getLong(109);
				prev_fee_income = rs.getLong(110);
				prev_investment_income = rs.getLong(111);
				prev_other_operating_income = rs.getLong(112);
				prev_total_income = rs.getLong(113);
				prev_salaries_and_benefits = rs.getLong(114);
				prev_office_occupancy = rs.getLong(115);
				prev_office_operations = rs.getLong(116);
				prev_education_and_promotion = rs.getLong(117);
				prev_loan_servicing = rs.getLong(118);
				prev_professional_and_outside_services = rs.getLong(119);
				prev_member_insurance = rs.getLong(120);
				prev_all_other_expenses = rs.getLong(121);
				prev_expense_subtotal = rs.getLong(122);
				prev_provision_for_loan_loss = rs.getLong(123);

				curr_exp_subtotal_incl_provisions = rs.getLong(124);
				curr_nonop_gain_loss = rs.getLong(125);
				curr_income_before_divs_and_int = rs.getLong(126);
				curr_interest_on_borrowings = rs.getLong(127);
				curr_dividends = rs.getLong(128);
				curr_subtotal = rs.getLong(129);
				curr_reserve_transfer = rs.getLong(130);
				curr_other_capital_transfers = rs.getLong(131);
				curr_net_income = rs.getLong(132);

				prev_exp_subtotal_incl_provisions = rs.getLong(133);
				prev_nonop_gain_loss = rs.getLong(134);
				prev_income_before_divs_and_int = rs.getLong(135);
				prev_interest_on_borrowings = rs.getLong(136);
				prev_dividends = rs.getLong(137);
				prev_subtotal = rs.getLong(138);
				prev_reserve_transfer = rs.getLong(139);
				prev_other_capital_transfers = rs.getLong(140);
				prev_net_income = rs.getLong(141);

				curr_2_to_6_mo_delq = rs.getLong(142);
				curr_6_to_12_mo_delq  = rs.getLong(143);
				curr_over_12_mo_delq  = rs.getLong(144);
				curr_total_delq  = rs.getLong(145);
				curr_net_charge_offs = rs.getLong(146);

				prev_2_to_6_mo_delq = rs.getLong(147);
				prev_6_to_12_mo_delq  = rs.getLong(148);
				prev_over_12_mo_delq  = rs.getLong(149);
				prev_total_delq  = rs.getLong(150);
				prev_net_charge_offs = rs.getLong(151);

				curr_pca_net_worth_ratio = rs.getInt(152);
				curr_pca_net_worth_classification = rs.getString(153);
				curr_total_capital_incl_allowances = rs.getLong(154);
				curr_total_capital_excl_allowances= rs.getLong(155);
				curr_foreclosed_and_repo_assets = rs.getLong(156);

				prev_pca_net_worth_ratio = rs.getInt(157);
				prev_pca_net_worth_classification = rs.getString(158);
				prev_total_capital_incl_allowances = rs.getLong(159);
				prev_total_capital_excl_allowances= rs.getLong(160);
				prev_foreclosed_and_repo_assets = rs.getLong(161);

				curr_year_end_total_assets = rs.getLong(162);
				curr_year_end_total_loans = rs.getLong(163);

				prev_year_end_total_assets = rs.getLong(164);
				prev_year_end_total_loans = rs.getLong(165);

				curr_members = rs.getLong(166);
				curr_potential_members = rs.getLong(167);
				curr_part_time_employees = rs.getLong(168);
				curr_full_time_employees = rs.getLong(169);
				curr_branches = rs.getLong(170);
				curr_member_of_fhlb = rs.getLong(171);

				prev_members = rs.getLong(172);
				prev_potential_members = rs.getLong(173);
				prev_part_time_employees = rs.getLong(174);
				prev_full_time_employees = rs.getLong(175);
				prev_branches = rs.getLong(176);
				prev_member_of_fhlb = rs.getLong(177);

				if(prev_cash_on_hand != 0)
				{
					pctChgInCashOnHand = ((double) curr_cash_on_hand - (double) prev_cash_on_hand) / (double) prev_cash_on_hand;
				}

				if(prev_cash_on_deposit != 0)
				{
					pctChgInCashOnDeposit = ((double) curr_cash_on_deposit - (double) prev_cash_on_deposit) / (double) prev_cash_on_deposit;	
				}

				if(prev_cash_equivalents != 0)
				{
					pctChgInCashEquivalents = ((double) curr_cash_equivalents - (double) prev_cash_equivalents) / (double) prev_cash_equivalents;
				}

				if(prev_cash_and_equivalents != 0)
				{
					pctChgInCashAndEquivalents = ((double) curr_cash_and_equivalents - (double) prev_cash_and_equivalents) / (double) prev_cash_and_equivalents;
				}

				if(prev_government_securities != 0)
				{
					pctChgInGovSecs = ((double) curr_government_securities - (double) prev_government_securities) / (double) prev_government_securities;
				}

				if(prev_federal_agency_securities != 0)
				{
					pctChgInFedAgencySecs = ((double) curr_federal_agency_securities - (double) prev_federal_agency_securities) / (double) prev_federal_agency_securities;
				}

				if(prev_corporate_credit_unions != 0)
				{
					pctChgInCorpCUs = ((double) curr_corporate_credit_unions - (double) prev_corporate_credit_unions) / (double) prev_corporate_credit_unions;
				}

				if(prev_bank_deposits != 0)
				{
					pctChgInBankDeposits = ((double) prev_bank_deposits - (double) prev_bank_deposits) / (double) prev_bank_deposits;
				}

				if(prev_mutual_funds != 0)
				{
					pctChgInMutualFunds = ((double) curr_mutual_funds - (double) prev_mutual_funds) / (double) prev_mutual_funds;
				}

				if(prev_all_other_investments != 0)
				{
					pctChgInAllOtherInvs = ((double) curr_all_other_investments - (double) prev_all_other_investments) / (double) prev_all_other_investments;
				}

				if(prev_total_investments != 0)
				{
					pctChgInTotalInvestments = ((double) curr_total_investments - (double) prev_total_investments) / (double) prev_total_investments;
				}

				if(prev_loans_held_for_sale != 0)
				{
					pctChgInLoansHeldForSale = ((double) curr_loans_held_for_sale - (double) prev_loans_held_for_sale) / (double) prev_loans_held_for_sale;
				}

				if(prev_credit_cards != 0)
				{
					pctChgInCreditCards = ((double) curr_credit_cards - (double) prev_credit_cards) / (double) prev_credit_cards;
				}

				if(prev_other_unsecured_loans != 0)
				{
					pctChgInOtherUnsecuredLoans = ((double) curr_other_unsecured_loans - (double) prev_other_unsecured_loans) / (double) prev_other_unsecured_loans;
				}

				if(prev_pal_loans != 0)
				{
					pctChgInPALLoans = ((double) curr_pal_loans - (double) prev_pal_loans) / (double) prev_pal_loans;
				}

				if(prev_edu_loans != 0)
				{
					pctChgInEduLoans = ((double) curr_edu_loans - (double) prev_edu_loans) / (double) prev_edu_loans;
				}

				if(prev_new_auto_loans != 0)
				{
					pctChgInNewAutoLoans = ((double) prev_new_auto_loans - (double) prev_new_auto_loans) / (double) prev_new_auto_loans;
				}

				if(prev_used_auto_loans != 0)
				{
					pctChgInUsedAutoLoans = ((double) prev_used_auto_loans - (double) prev_used_auto_loans) / (double) prev_used_auto_loans;
				}

				if(prev_first_mortgages != 0)
				{
					pctChgInFirstMortgages = ((double) curr_first_mortgages - (double) prev_first_mortgages) / (double) prev_first_mortgages;
				}

				if(prev_second_mortgages != 0)
				{
					pctChgInSecondMortgages = ((double) curr_second_mortgages - (double) prev_second_mortgages) / (double) prev_second_mortgages;
				}

				if(prev_leases != 0)
				{
					pctChgInLeases = ((double) curr_leases - (double) prev_leases) / (double) prev_leases;
				}

				if(prev_all_other_loans != 0)
				{
					pctChgInAllOtherLoans = ((double) curr_all_other_loans - (double) prev_all_other_loans) / (double) prev_all_other_loans;
				}

				if(prev_total_loans != 0)
				{
					pctChgInTotalLoans = ((double) curr_total_loans - (double) prev_total_loans) / (double) prev_total_loans;
				}

				if(prev_loan_loss_allowance != 0)
				{
					pctChgInAllowance = ((double) curr_loan_loss_allowance - (double) prev_loan_loss_allowance) / (double) prev_loan_loss_allowance;
				}

				if(prev_mbls != 0)
				{
					pctChgInMBLs = ((double) curr_mbls - (double) prev_mbls) / (double) prev_mbls;
				}

				if(prev_forclosed_and_repo_assets != 0)
				{
					pctChgInRepoAssets = ((double) prev_forclosed_and_repo_assets - (double) prev_forclosed_and_repo_assets) / (double) prev_forclosed_and_repo_assets;
				}

				if(prev_land_and_building != 0)
				{
					pctChgInLandAndBuilding = ((double) curr_land_and_building - (double) prev_land_and_building) / (double) prev_land_and_building;
				}

				if(prev_other_fixed_assets != 0)
				{
					pctChgInOtherFixedAssets = ((double) curr_other_fixed_assets - (double) prev_other_fixed_assets) / (double) prev_other_fixed_assets;
				}

				if(prev_share_insurance_deposit != 0)
				{
					pctChgInInsuranceDeposit = ((double) curr_share_insurance_deposit - (double) prev_share_insurance_deposit) / (double) prev_share_insurance_deposit;
				}

				if(prev_intangible_assets != 0)
				{
					pctChgInIntangibleAssets = ((double) curr_intangible_assets - (double) prev_intangible_assets) / (double) prev_intangible_assets;
				}

				if(prev_other_assets != 0)
				{
					pctChgInOtherAssets = ((double) curr_other_assets - (double) prev_other_assets) / (double) prev_other_assets;
				}

				if(prev_total_assets != 0)
				{
					pctChgInTotalAssets = ((double) curr_total_assets - (double) prev_total_assets) / (double) prev_total_assets;
				}

				if(prev_loans_granted_ytd != 0)
				{
					pctChgInLoansGrantedYTD = ((double) curr_loans_granted_ytd - (double) prev_loans_granted_ytd) / (double) prev_loans_granted_ytd;
				}

				if(prev_pal_loans_granted_ytd != 0)
				{
					pctChgInPALLoansGrantedYTD = ((double) curr_pal_loans_granted_ytd - (double) prev_pal_loans_granted_ytd) / (double) prev_pal_loans_granted_ytd;
				}

				if(prev_deferred_edu_loans != 0)
				{
					pctChgInDeferredEduLoans = ((double) curr_deferred_edu_loans - (double) prev_deferred_edu_loans) / (double) prev_deferred_edu_loans;
				}

				if(prev_loans_to_execs != 0)
				{
					pctChgInLoansToExecs = ((double) curr_loans_to_execs - (double) prev_loans_to_execs) / (double) prev_loans_to_execs;
				}

				if(prev_reverse_repos != 0)
				{
					pctChgInReverseRepos = ((double) curr_reverse_repos - (double) prev_reverse_repos) / (double) prev_reverse_repos;
				}

				if(prev_other_notes_payable != 0)
				{
					pctChgInOtherNotesPayable = ((double) curr_other_notes_payable - (double) prev_other_notes_payable) / (double) prev_other_notes_payable;
				}

				if(prev_all_other_liabilities != 0)
				{
					pctChgInAllOtherLiabilities = ((double) curr_all_other_liabilities - (double) prev_all_other_liabilities) / (double) prev_all_other_liabilities;
				}

				if(prev_total_liabilities != 0)
				{
					pctChgInTotalLiabilities = ((double) curr_total_liabilities - (double) prev_total_liabilities) / (double) prev_total_liabilities;
				}

				if(prev_regular_shares != 0)
				{
					pctChgInRegularShares = ((double) curr_regular_shares - (double) prev_regular_shares) / (double) prev_regular_shares;
				}

				if(prev_share_drafts != 0)
				{
					pctChgInShareDrafts = ((double) curr_share_drafts - (double) prev_share_drafts) / (double) prev_share_drafts;
				}

				if(prev_mmas != 0)
				{
					pctChgInMMAs = ((double) curr_mmas - (double) prev_mmas) / (double) prev_mmas;
				}

				if(prev_ira_keoghs != 0)
				{
					pctChgInIRAKeoghs = ((double) curr_ira_keoghs - (double) prev_ira_keoghs) / (double) prev_ira_keoghs;
				}

				if(prev_certificates != 0)
				{
					pctChgInCertificates = ((double) curr_certificates - (double) prev_certificates) / (double) prev_certificates;
				}

				if(prev_total_shares_and_deposits != 0)
				{
					pctChgInTotalSharesAndDeposits = ((double) curr_total_shares_and_deposits - (double) prev_total_shares_and_deposits) / (double) prev_total_shares_and_deposits;
				}				

				if(prev_reserves != 0)
				{
					pctChgInReserves = ((double) curr_reserves - (double) prev_reserves) / (double) prev_reserves;
				}

				if(prev_undivided_earnings != 0)
				{
					pctChgInUndividedEarnings = ((double) curr_undivided_earnings - (double) prev_undivided_earnings) / (double) prev_undivided_earnings;
				}

				if(prev_total_liabilities != 0)
				{
					pctChgInTotalLiabilities = ((double) curr_total_liabilities - (double) prev_total_liabilities) / (double) prev_total_liabilities;
				}

				if(prev_total_capital != 0)
				{
					pctChgInTotalCapital = ((double) curr_total_capital - (double) prev_total_capital) / (double) prev_total_capital;
				}

				if(prev_total_liabilities_and_capital != 0)
				{
					pctChgInTotalLiabilitiesAndCapital = ((double) curr_total_liabilities_and_capital - (double) prev_total_liabilities_and_capital) / (double) prev_total_liabilities_and_capital;
				}

				if(prev_loan_and_lease_interest != 0)
				{
					pctChgInLoanAndLeaseInterest = ((double) curr_loan_and_lease_interest - (double) prev_loan_and_lease_interest) / (double) prev_loan_and_lease_interest;
				}

				if(prev_rebates != 0)
				{
					pctChgInRebates = ((double) curr_rebates - (double) prev_rebates) / (double) prev_rebates;
				}

				if(prev_fee_income != 0)
				{
					pctChgInFeeIncome = ((double) curr_fee_income - (double) prev_fee_income) / (double) prev_fee_income;
				}

				if(prev_investment_income != 0)
				{
					pctChgInInvestmentIncome = ((double) curr_investment_income - (double) prev_investment_income) / (double) prev_investment_income;
				}

				if(prev_other_operating_income != 0)
				{
					pctChgInOtherOperatingIncome = ((double) curr_other_operating_income - (double) prev_other_operating_income) / (double) prev_other_operating_income;
				}

				if(prev_total_income != 0)
				{
					pctChgInTotalIncome = ((double) curr_total_income - (double) prev_total_income) / (double) prev_total_income;
				}

				if(prev_salaries_and_benefits != 0)
				{
					pctChgInSalariesAndBenefits = ((double) curr_salaries_and_benefits - (double) prev_salaries_and_benefits) / (double) prev_salaries_and_benefits;
				}

				if(prev_office_occupancy != 0)
				{
					pctChgInOfficeOccupancy = ((double) curr_office_occupancy - (double) prev_office_occupancy) / (double) prev_office_occupancy;
				}

				if(prev_office_operations != 0)
				{
					pctChgInOfficeOperations = ((double) prev_office_operations - (double) prev_office_operations) / (double) prev_office_operations;
				}

				if(prev_education_and_promotion != 0)
				{
					pctChgInEducationAndPromotion = ((double) curr_education_and_promotion - (double) prev_education_and_promotion) / (double) prev_education_and_promotion;
				}

				if(prev_loan_servicing != 0)
				{
					pctChgInLoanServicing = ((double) curr_loan_servicing - (double) prev_loan_servicing) / (double) prev_loan_servicing;
				}

				if(prev_professional_and_outside_services != 0)
				{
					pctChgInProfessionalAndOutsideServices = ((double) curr_professional_and_outside_services - (double) prev_professional_and_outside_services) / (double) prev_professional_and_outside_services;
				}

				if(prev_member_insurance != 0)
				{
					pctChgInMemberInsurance = ((double) curr_member_insurance - (double) prev_member_insurance) / (double) prev_member_insurance;
				}

				if(prev_all_other_expenses != 0)
				{
					pctChgInAllOtherExpenses = ((double) curr_all_other_expenses - (double) prev_all_other_expenses) / (double) prev_all_other_expenses;
				}

				if(prev_expense_subtotal != 0)
				{
					pctChgInExpenseSubtotal = ((double) curr_expense_subtotal - (double) prev_expense_subtotal) / (double) prev_expense_subtotal;
				}

				if(prev_provision_for_loan_loss != 0)
				{
					pctChgInProvisionForLoanLoss = ((double) curr_provision_for_loan_loss - (double) prev_provision_for_loan_loss) / (double) prev_provision_for_loan_loss;
				}

				if(prev_exp_subtotal_incl_provisions != 0)
				{
					pctChgInExpSubtotalInclProvisions = ((double) curr_exp_subtotal_incl_provisions - (double) prev_exp_subtotal_incl_provisions) / (double) prev_exp_subtotal_incl_provisions;
				}

				if(prev_nonop_gain_loss != 0)
				{
					pctChgInNonOpGainLoss = ((double) curr_nonop_gain_loss - (double) prev_nonop_gain_loss) / (double) prev_nonop_gain_loss;
				}

				if(prev_income_before_divs_and_int != 0)
				{
					pctChgInIncomeBeforeDivsAndInt = ((double) curr_income_before_divs_and_int - (double) prev_income_before_divs_and_int) / (double) prev_income_before_divs_and_int;
				}

				if(prev_interest_on_borrowings != 0)
				{
					pctChgInInterestOnBorrowings = ((double) curr_interest_on_borrowings - (double) prev_interest_on_borrowings) / (double) prev_interest_on_borrowings;
				}

				if(prev_dividends != 0)
				{
					pctChgInDividends = ((double) curr_dividends - (double) prev_dividends) / (double) prev_dividends;
				}

				if(prev_subtotal != 0)
				{
					pctChgInSubtotal = ((double) curr_subtotal - (double) prev_subtotal) / (double) prev_subtotal;
				}

				if(prev_reserve_transfer != 0)
				{
					pctChgInReserveTransfer = ((double) curr_reserve_transfer - (double) prev_reserve_transfer) / (double) prev_reserve_transfer;
				}

				if(prev_other_capital_transfers != 0)
				{
					pctChgInOtherCapitalTransfers = ((double) curr_other_capital_transfers - (double) prev_other_capital_transfers) / (double) prev_other_capital_transfers;
				}

				if(prev_net_income != 0)
				{
					pctChgInNetIncome = ((double) curr_net_income - (double) prev_net_income) / (double) prev_net_income;
				}

				if(prev_2_to_6_mo_delq != 0)
				{
					pctChgIn2To6MoDelq = ((double) curr_2_to_6_mo_delq - (double) prev_2_to_6_mo_delq) / (double) prev_2_to_6_mo_delq;
				}

				if(prev_6_to_12_mo_delq != 0)
				{
					pctChgIn6To12MoDelq = ((double) curr_6_to_12_mo_delq - (double) prev_6_to_12_mo_delq) / (double) prev_6_to_12_mo_delq;
				}

				if(prev_over_12_mo_delq != 0)
				{
					pctChgInOver12MoDelq = ((double) curr_over_12_mo_delq - (double) prev_over_12_mo_delq) / (double) prev_over_12_mo_delq;
				}

				if(prev_total_delq != 0)
				{
					pctChgInTotalDelq = ((double) curr_total_delq - (double) prev_total_delq) / (double) prev_total_delq;
				}

				if(prev_net_charge_offs != 0)
				{
					pctChgInNetChargeOffs = ((double) curr_net_charge_offs - (double) prev_net_charge_offs) / (double) prev_net_charge_offs;
				}

				if(prev_members != 0)
				{
					pctChgInMembers = ((double) curr_members - (double) prev_members) / (double) prev_members;
				}

				if(prev_potential_members != 0)
				{
					pctChgInPotentialMembers = ((double) curr_potential_members - (double) prev_potential_members) / (double) prev_potential_members;
				}

				if(prev_part_time_employees != 0)
				{
					pctChgInPartTimeEmployees = ((double) curr_part_time_employees - (double) prev_part_time_employees) / (double) prev_part_time_employees;
				}

				if(prev_full_time_employees != 0)
				{
					pctChgInFullTimeEmployees = ((double) curr_full_time_employees - (double) prev_full_time_employees) / (double) prev_full_time_employees;
				}

				if(prev_branches != 0)
				{
					pctChgInBranches = ((double) curr_branches - (double) prev_branches) / (double) prev_branches;
				}
			}
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

			TwoYearFinancialSummary heartland = new TwoYearFinancialSummary(dbConn, "32999", Utils.convertPeriod(Utils.financialPeriods[0]));
			System.out.println(heartland.getCashOnHandPctChg());
			System.out.println( Double.valueOf(((heartland.curr_cash_on_hand - heartland.prev_cash_on_hand) / heartland.prev_cash_on_hand)*100) );
			System.out.println(heartland.curr_cash_on_hand);
			System.out.println(heartland.prev_cash_on_hand);
			System.out.println(heartland.pctChgInCashOnHand);

			System.out.println(heartland.curr_cash_on_deposit);
			System.out.println(heartland.prev_cash_on_deposit);
			System.out.println(heartland.pctChgInCashOnDeposit);

			System.out.println("\\r\\n......Done, and Done-er....");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getID()
	{
		return (id);
	}

	public String getCurr2To6MoDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_2_to_6_mo_delq != -1)
			{
				return(nf.format(curr_2_to_6_mo_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurr6To12MoDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_6_to_12_mo_delq != -1)
			{
				return(nf.format(curr_6_to_12_mo_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOver12MoDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_over_12_mo_delq != -1)
			{
				return(nf.format(curr_over_12_mo_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_delq != -1)
			{
				return(nf.format(curr_total_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrNetChargeOffs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_net_charge_offs != -1)
			{
				return(nf.format(curr_net_charge_offs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrev2To6MoDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_2_to_6_mo_delq != -1)
			{
				return(nf.format(prev_2_to_6_mo_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrev6To12MoDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_6_to_12_mo_delq != -1)
			{
				return(nf.format(prev_6_to_12_mo_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOver12MoDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_over_12_mo_delq != -1)
			{
				return(nf.format(prev_over_12_mo_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalDelq()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_delq != -1)
			{
				return(nf.format(prev_total_delq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevNetChargeOffs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_net_charge_offs != -1)
			{
				return(nf.format(prev_net_charge_offs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrExpSubtotalInclProvisions()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_exp_subtotal_incl_provisions != -1)
			{
				return(nf.format(curr_exp_subtotal_incl_provisions));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrNonOpGainLoss()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_nonop_gain_loss != -1)
			{
				return(nf.format(curr_nonop_gain_loss));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrIncomeBeforeDivsAndInt()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_income_before_divs_and_int != -1)
			{
				return(nf.format(curr_income_before_divs_and_int));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrInterestOnBorrowings()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_interest_on_borrowings != -1)
			{
				return(nf.format(curr_interest_on_borrowings));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrDividends()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_dividends != -1)
			{
				return(nf.format(curr_dividends));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrSubtotal()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_subtotal != -1)
			{
				return(nf.format(curr_subtotal));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrReserveTransfer()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_reserve_transfer != -1)
			{
				return(nf.format(curr_reserve_transfer));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOtherCapitalTransfers()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_other_capital_transfers != -1)
			{
				return(nf.format(curr_other_capital_transfers));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrNetIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_net_income != -1)
			{
				return(nf.format(curr_net_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevExpSubtotalInclProvisions()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_exp_subtotal_incl_provisions != -1)
			{
				return(nf.format(prev_exp_subtotal_incl_provisions));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevNonOpGainLoss()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_nonop_gain_loss != -1)
			{
				return(nf.format(prev_nonop_gain_loss));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevIncomeBeforeDivsAndInt()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_income_before_divs_and_int != -1)
			{
				return(nf.format(prev_income_before_divs_and_int));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevInterestOnBorrowings()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_interest_on_borrowings != -1)
			{
				return(nf.format(prev_interest_on_borrowings));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevDividends()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_dividends != -1)
			{
				return(nf.format(prev_dividends));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevSubtotal()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_subtotal != -1)
			{
				return(nf.format(prev_subtotal));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevReserveTransfer()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_reserve_transfer != -1)
			{
				return(nf.format(prev_reserve_transfer));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOtherCapitalTransfers()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_other_capital_transfers != -1)
			{
				return(nf.format(prev_other_capital_transfers));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevNetIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_net_income != -1)
			{
				return(nf.format(prev_net_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrCashOnHand()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_cash_on_hand != -1)
			{
				return(nf.format(curr_cash_on_hand));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrCashOnDeposit()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_cash_on_hand != -1)
			{
				return(nf.format(curr_cash_on_deposit));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrCashEquivalents()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_cash_on_hand != -1)
			{
				return(nf.format(curr_cash_equivalents));
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

	public String getCurrCashAndEquivalents()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_cash_and_equivalents != -1)
			{
				return(nf.format(curr_cash_and_equivalents));
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

	public String getPrevCashOnHand()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_cash_on_hand != -1)
			{
				return(nf.format(prev_cash_on_hand));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevCashOnDeposit()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_cash_on_hand != -1)
			{
				return(nf.format(prev_cash_on_deposit));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevCashEquivalents()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_cash_on_hand != -1)
			{
				return(nf.format(prev_cash_equivalents));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevCashAndEquivalents()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_cash_and_equivalents != -1)
			{
				return(nf.format(prev_cash_and_equivalents));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCashOnHandPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_cash_on_hand != -1 && prev_cash_on_hand != -1 && prev_cash_on_hand != 0)
			{
				return(nf.format(pctChgInCashOnHand));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCashOnDepositPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_cash_on_deposit != -1 && prev_cash_on_deposit != -1 && prev_cash_on_deposit != 0)
			{
				return(nf.format(pctChgInCashOnDeposit));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCashEquivalentsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_cash_equivalents != -1 && prev_cash_equivalents != -1 && prev_cash_equivalents != 0)
			{
				return(nf.format(pctChgInCashEquivalents));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCashAndEquivalentsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_cash_and_equivalents != -1 && prev_cash_and_equivalents != -1 && prev_cash_and_equivalents != 0)
			{
				return(nf.format(pctChgInCashAndEquivalents));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrGovernmentSecurities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_government_securities != -1)
			{
				return(nf.format(curr_government_securities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrFederalAgencySecurities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_federal_agency_securities != -1)
			{
				return(nf.format(curr_federal_agency_securities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrCorporateCUs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_corporate_credit_unions != -1)
			{
				return(nf.format(curr_corporate_credit_unions));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrBankDeposits()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_bank_deposits != -1)
			{
				return(nf.format(curr_bank_deposits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrMutualFunds()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_mutual_funds != -1)
			{
				return(nf.format(curr_mutual_funds));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrAllOtherInvestments()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_all_other_investments != -1)
			{
				return(nf.format(curr_all_other_investments));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalInvestments()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_investments != -1)
			{
				return(nf.format(curr_total_investments));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLoansHeldForSale()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_loans_held_for_sale != -1)
			{
				return(nf.format(curr_loans_held_for_sale));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevCreditCards()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_credit_cards != -1)
			{
				return(nf.format(prev_credit_cards));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOtherUnsecuredLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_other_unsecured_loans != -1)
			{
				return(nf.format(prev_other_unsecured_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevPALLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_pal_loans != -1)
			{
				return(nf.format(prev_pal_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevEduLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_edu_loans != -1)
			{
				return(nf.format(prev_edu_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevNewAutoLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_new_auto_loans != -1)
			{
				return(nf.format(prev_new_auto_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevUsedAutoLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_used_auto_loans != -1)
			{
				return(nf.format(prev_used_auto_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevFirstMortgages()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_first_mortgages != -1)
			{
				return(nf.format(prev_first_mortgages));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevSecondMortgages()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_second_mortgages != -1)
			{
				return(nf.format(prev_second_mortgages));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLeases()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_leases != -1)
			{
				return(nf.format(prev_leases));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevAllOtherLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_all_other_loans != -1)
			{
				return(nf.format(prev_all_other_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_loans != -1)
			{
				return(nf.format(prev_total_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLoanLossAllowance()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_loan_loss_allowance != -1)
			{
				return(nf.format(prev_loan_loss_allowance));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevMBLs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_mbls != -1)
			{
				return(nf.format(prev_mbls));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrCreditCards()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_credit_cards != -1)
			{
				return(nf.format(curr_credit_cards));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOtherUnsecuredLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_other_unsecured_loans != -1)
			{
				return(nf.format(curr_other_unsecured_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrPALLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_pal_loans != -1)
			{
				return(nf.format(curr_pal_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrEduLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_edu_loans != -1)
			{
				return(nf.format(curr_edu_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrNewAutoLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_new_auto_loans != -1)
			{
				return(nf.format(curr_new_auto_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrUsedAutoLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_used_auto_loans != -1)
			{
				return(nf.format(curr_used_auto_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrFirstMortgages()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_first_mortgages != -1)
			{
				return(nf.format(curr_first_mortgages));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrSecondMortgages()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_second_mortgages != -1)
			{
				return(nf.format(curr_second_mortgages));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLeases()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_leases != -1)
			{
				return(nf.format(curr_leases));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrAllOtherLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_all_other_loans != -1)
			{
				return(nf.format(curr_all_other_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_loans != -1)
			{
				return(nf.format(curr_total_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLoanLossAllowance()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_loan_loss_allowance != -1)
			{
				return(nf.format(curr_loan_loss_allowance));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrMBLs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_mbls != -1)
			{
				return(nf.format(curr_mbls));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrRepoAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_forclosed_and_repo_assets != -1)
			{
				return(nf.format(curr_forclosed_and_repo_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLandAndBuilding()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_land_and_building != -1)
			{
				return(nf.format(curr_land_and_building));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOtherFixedAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_other_fixed_assets != -1)
			{
				return(nf.format(curr_other_fixed_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrInsuranceDeposit()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_share_insurance_deposit != -1)
			{
				return(nf.format(curr_share_insurance_deposit));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrIntangibleAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_intangible_assets != -1)
			{
				return(nf.format(curr_intangible_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOtherAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_other_assets != -1)
			{
				return(nf.format(curr_other_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_assets != -1)
			{
				return(nf.format(curr_total_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLoansGrantedYTD()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_loans_granted_ytd != -1)
			{
				return(nf.format(curr_loans_granted_ytd));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrPALLoansGrantedYTD()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_pal_loans_granted_ytd != -1)
			{
				return(nf.format(curr_pal_loans_granted_ytd));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrDeferredEduLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_deferred_edu_loans != -1)
			{
				return(nf.format(curr_deferred_edu_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLoansToExecs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_loans_to_execs != -1)
			{
				return(nf.format(curr_loans_to_execs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLoansGrantedYTD()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_loans_granted_ytd != -1)
			{
				return(nf.format(prev_loans_granted_ytd));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevPALLoansGrantedYTD()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_pal_loans_granted_ytd != -1)
			{
				return(nf.format(prev_pal_loans_granted_ytd));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevDeferredEduLoans()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_deferred_edu_loans != -1)
			{
				return(nf.format(prev_deferred_edu_loans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLoansToExecs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_loans_to_execs != -1)
			{
				return(nf.format(prev_loans_to_execs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevRepoAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_forclosed_and_repo_assets != -1)
			{
				return(nf.format(prev_forclosed_and_repo_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLandAndBuilding()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_land_and_building != -1)
			{
				return(nf.format(prev_land_and_building));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOtherFixedAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_other_fixed_assets != -1)
			{
				return(nf.format(prev_other_fixed_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevInsuranceDeposit()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_share_insurance_deposit != -1)
			{
				return(nf.format(prev_share_insurance_deposit));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevIntangibleAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_intangible_assets != -1)
			{
				return(nf.format(prev_intangible_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOtherAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_other_assets != -1)
			{
				return(nf.format(prev_other_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalAssets()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_assets != -1)
			{
				return(nf.format(prev_total_assets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getGovernmentSecuritiesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_government_securities > 0 && prev_government_securities > 0)
			{
				return(nf.format(pctChgInGovSecs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getFederalAgencySecuritiesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_federal_agency_securities > 0 && prev_federal_agency_securities > 0)
			{
				return(nf.format(pctChgInFedAgencySecs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCorporateCUsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_corporate_credit_unions >0 && prev_corporate_credit_unions >0)
			{
				return(nf.format(pctChgInCorpCUs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getBankDepositsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_bank_deposits >0 && prev_bank_deposits > 0)
			{
				return(nf.format(pctChgInBankDeposits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getMutualFundsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_mutual_funds > 0 && prev_mutual_funds > 0)
			{
				return(nf.format(pctChgInMutualFunds));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getAllOtherInvestmentsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_all_other_investments > 0 && prev_all_other_investments > 0)
			{
				return(nf.format(pctChgInAllOtherInvs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalInvestmentsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_investments > 0 && prev_total_investments > 0)
			{
				return(nf.format(pctChgInTotalInvestments));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCreditCardsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_credit_cards > 0 && prev_credit_cards > 0)
			{
				return(nf.format(pctChgInCreditCards));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOtherUnsecuredLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_other_unsecured_loans > 0 && prev_other_unsecured_loans > 0)
			{
				return(nf.format(pctChgInOtherUnsecuredLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPALLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_pal_loans > 0 && prev_pal_loans > 0)
			{
				return(nf.format(pctChgInPALLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getEduLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_edu_loans > 0 && prev_edu_loans > 0)
			{
				return(nf.format(pctChgInEduLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getNewAutoLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_new_auto_loans > 0 && prev_new_auto_loans > 0)
			{
				return(nf.format(pctChgInNewAutoLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getUsedAutoLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_used_auto_loans > 0 && prev_used_auto_loans > 0)
			{
				return(nf.format(pctChgInUsedAutoLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getFirstMortgagePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_first_mortgages > 0 && prev_first_mortgages > 0)
			{
				return(nf.format(pctChgInFirstMortgages));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getSecondMortgagePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_second_mortgages > 0 && prev_second_mortgages > 0)
			{
				return(nf.format(pctChgInSecondMortgages));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLeasesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_leases > 0 && prev_leases > 0)
			{
				return(nf.format(pctChgInLeases));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getAllOtherLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_all_other_loans > 0 && prev_all_other_loans > 0)
			{
				return(nf.format(pctChgInAllOtherLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_loans > 0 && prev_total_loans > 0)
			{
				return(nf.format(pctChgInTotalLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLoanLossAllowancePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_loan_loss_allowance > 0 && prev_loan_loss_allowance > 0)
			{
				return(nf.format(pctChgInAllowance));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getMBLsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_mbls > 0 && prev_mbls > 0)
			{
				return(nf.format(pctChgInMBLs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLoansGrantedYTDPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_loans_granted_ytd > 0 && prev_loans_granted_ytd > 0)
			{
				return(nf.format(pctChgInLoansGrantedYTD));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPALLoansGrantedYTDPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_pal_loans_granted_ytd > 0 && prev_pal_loans_granted_ytd > 0)
			{
				return(nf.format(pctChgInPALLoansGrantedYTD));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getDeferredEduLoansPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_deferred_edu_loans > 0 && prev_deferred_edu_loans > 0)
			{
				return(nf.format(pctChgInDeferredEduLoans));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLoansToExecsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_loans_to_execs > 0 && prev_loans_to_execs > 0)
			{
				return(nf.format(pctChgInLoansToExecs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLoansHeldForSalePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_loans_held_for_sale > 0 && prev_loans_held_for_sale > 0)
			{
				return(nf.format(pctChgInLoansHeldForSale));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getRepoAssetsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_forclosed_and_repo_assets > 0 && prev_forclosed_and_repo_assets > 0)
			{
				return(nf.format(pctChgInRepoAssets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLandAndBuildingPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_land_and_building > 0 && prev_land_and_building > 0)
			{
				return(nf.format(pctChgInLandAndBuilding));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOtherFixedAssetsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_other_fixed_assets > 0 && prev_other_fixed_assets > 0)
			{
				return(nf.format(pctChgInOtherFixedAssets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getShareInsuranceDepositPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_share_insurance_deposit > 0 && prev_share_insurance_deposit > 0)
			{
				return(nf.format(pctChgInInsuranceDeposit));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getIntangibleAssetsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_intangible_assets > 0 && prev_intangible_assets > 0)
			{
				return(nf.format(pctChgInIntangibleAssets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOtherAssetsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_other_assets > 0 && prev_other_assets > 0)
			{
				return(nf.format(pctChgInOtherAssets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalAssetsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_assets > 0 && prev_total_assets > 0)
			{
				return(nf.format(pctChgInTotalAssets));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevGovernmentSecurities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_government_securities != -1)
			{
				return(nf.format(prev_government_securities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevFederalAgencySecurities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_federal_agency_securities != -1)
			{
				return(nf.format(prev_federal_agency_securities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevCorporateCUs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_corporate_credit_unions != -1)
			{
				return(nf.format(prev_corporate_credit_unions));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevBankDeposits()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_bank_deposits != -1)
			{
				return(nf.format(prev_bank_deposits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevMutualFunds()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_mutual_funds != -1)
			{
				return(nf.format(prev_mutual_funds));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevAllOtherInvestments()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_all_other_investments != -1)
			{
				return(nf.format(prev_all_other_investments));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalInvestments()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_investments != -1)
			{
				return(nf.format(prev_total_investments));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLoansHeldForSale()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_loans_held_for_sale != -1)
			{
				return(nf.format(prev_loans_held_for_sale));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrReverseRepos()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_reverse_repos != -1)
			{
				return(nf.format(curr_reverse_repos));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevReverseRepos()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_reverse_repos != -1)
			{
				return(nf.format(prev_reverse_repos ));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOtherNotesPayable()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_other_notes_payable != -1)
			{
				return(nf.format(curr_other_notes_payable));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOtherNotesPayable()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_other_notes_payable != -1)
			{
				return(nf.format(prev_other_notes_payable));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrAllOtherLiabilities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_all_other_liabilities != -1)
			{
				return(nf.format(curr_all_other_liabilities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevAllOtherLiabilities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_all_other_liabilities != -1)
			{
				return(nf.format(prev_all_other_liabilities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalLiabilities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_liabilities != -1)
			{
				return(nf.format(curr_total_liabilities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalLiabilities()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_liabilities != -1)
			{
				return(nf.format(prev_total_liabilities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrRegularShares()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_regular_shares != -1)
			{
				return(nf.format(curr_regular_shares));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevRegularShares()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_regular_shares != -1)
			{
				return(nf.format(prev_regular_shares));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrShareDrafts()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_share_drafts != -1)
			{
				return(nf.format(curr_share_drafts));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevShareDrafts()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_share_drafts != -1)
			{
				return(nf.format(prev_share_drafts));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrMMAs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_mmas != -1)
			{
				return(nf.format(curr_mmas));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevMMAs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_mmas != -1)
			{
				return(nf.format(prev_mmas));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrIRAKeoghs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_ira_keoghs != -1)
			{
				return(nf.format(curr_ira_keoghs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevIRAKeoghs()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_ira_keoghs != -1)
			{
				return(nf.format(prev_ira_keoghs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrCertificates()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_certificates != -1)
			{
				return(nf.format(curr_certificates));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevCertificates()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_certificates != -1)
			{
				return(nf.format(prev_certificates));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalSharesAndDeposits()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_shares_and_deposits != -1)
			{
				return(nf.format(curr_total_shares_and_deposits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalSharesAndDeposits()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_shares_and_deposits != -1)
			{
				return(nf.format(prev_total_shares_and_deposits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrReserves()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_shares_and_deposits != -1)
			{
				return(nf.format(curr_reserves));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevReserves()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_shares_and_deposits != -1)
			{
				return(nf.format(prev_reserves));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrUndividedEarnings()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_undivided_earnings != -1)
			{
				return(nf.format(curr_undivided_earnings));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevUndividedEarnings()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_undivided_earnings != -1)
			{
				return(nf.format(prev_undivided_earnings));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalCapital()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_capital != -1)
			{
				return(nf.format(curr_total_capital));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalCapital()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_capital != -1)
			{
				return(nf.format(prev_total_capital));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalLiabilitiesAndCapital()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_liabilities_and_capital != -1)
			{
				return(nf.format(curr_total_liabilities_and_capital));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalLiabilitiesAndCapital()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_liabilities_and_capital != -1)
			{
				return(nf.format(prev_total_liabilities_and_capital));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLoanAndLeaseInterest()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_loan_and_lease_interest != -1)
			{
				return(nf.format(curr_loan_and_lease_interest));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrRebates()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_rebates != -1)
			{
				return(nf.format(curr_rebates));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrFeeIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_fee_income != -1)
			{
				return(nf.format(curr_fee_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrInvestmentIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_investment_income != -1)
			{
				return(nf.format(curr_investment_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOtherOperatingIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_other_operating_income != -1)
			{
				return(nf.format(curr_other_operating_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrTotalIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_total_income != -1)
			{
				return(nf.format(curr_total_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrSalariesAndBenefits()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_salaries_and_benefits != -1)
			{
				return(nf.format(curr_salaries_and_benefits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOfficeOccupancy()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_office_occupancy!= -1)
			{
				return(nf.format(curr_office_occupancy));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrOfficeOperations()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_office_operations != -1)
			{
				return(nf.format(curr_office_operations));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrEducationAndPromotion()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_education_and_promotion != -1)
			{
				return(nf.format(curr_education_and_promotion));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrLoanServicing()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_loan_servicing != -1)
			{
				return(nf.format(curr_loan_servicing));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrProfessionalAndOutsideServices()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_professional_and_outside_services != -1)
			{
				return(nf.format(curr_professional_and_outside_services));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrMemberInsurance()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_member_insurance != -1)
			{
				return(nf.format(curr_member_insurance));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrAllOtherExpenses()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_all_other_expenses != -1)
			{
				return(nf.format(curr_all_other_expenses));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrExpenseSubtotal()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_expense_subtotal != -1)
			{
				return(nf.format(curr_expense_subtotal));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrProvisionForLoanLoss()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_provision_for_loan_loss != -1)
			{
				return(nf.format(curr_provision_for_loan_loss));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLoanAndLeaseInterest()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_loan_and_lease_interest != -1)
			{
				return(nf.format(prev_loan_and_lease_interest));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevRebates()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_rebates != -1)
			{
				return(nf.format(prev_rebates));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevFeeIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_fee_income != -1)
			{
				return(nf.format(prev_fee_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevInvestmentIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_investment_income != -1)
			{
				return(nf.format(prev_investment_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOtherOperatingIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_other_operating_income != -1)
			{
				return(nf.format(prev_other_operating_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevTotalIncome()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_total_income != -1)
			{
				return(nf.format(prev_total_income));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevSalariesAndBenefits()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_salaries_and_benefits != -1)
			{
				return(nf.format(prev_salaries_and_benefits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOfficeOccupancy()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_office_occupancy!= -1)
			{
				return(nf.format(prev_office_occupancy));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevOfficeOperations()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_office_operations != -1)
			{
				return(nf.format(prev_office_operations));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevEducationAndPromotion()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_education_and_promotion != -1)
			{
				return(nf.format(prev_education_and_promotion));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevLoanServicing()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_loan_servicing != -1)
			{
				return(nf.format(prev_loan_servicing));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevProfessionalAndOutsideServices()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_professional_and_outside_services != -1)
			{
				return(nf.format(prev_professional_and_outside_services));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevMemberInsurance()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_member_insurance != -1)
			{
				return(nf.format(prev_member_insurance));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevAllOtherExpenses()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_all_other_expenses != -1)
			{
				return(nf.format(prev_all_other_expenses));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevExpenseSubtotal()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_expense_subtotal != -1)
			{
				return(nf.format(prev_expense_subtotal));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevProvisionForLoanLoss()
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_provision_for_loan_loss != -1)
			{
				return(nf.format(prev_provision_for_loan_loss));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getReverseReposPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_reverse_repos > 0 && prev_reverse_repos > 0)
			{
				return(nf.format(pctChgInReverseRepos));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOtherNotesPayablePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_other_notes_payable > 0 && prev_other_notes_payable > 0)
			{
				return(nf.format(pctChgInOtherNotesPayable));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getAllOtherLiabilitiesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_all_other_liabilities > 0 && prev_all_other_liabilities > 0)
			{
				return(nf.format(pctChgInAllOtherLiabilities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalLiabilitiesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_liabilities > 0 && prev_total_liabilities > 0)
			{
				return(nf.format(pctChgInTotalLiabilities));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getRegularSharesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_regular_shares > 0 && prev_regular_shares > 0)
			{
				return(nf.format(pctChgInRegularShares));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getShareDraftsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_share_drafts > 0 && prev_share_drafts > 0)
			{
				return(nf.format(pctChgInShareDrafts));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getMMAsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_mmas > 0 && prev_mmas> 0)
			{
				return(nf.format(pctChgInMMAs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getIRAKeoghsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_ira_keoghs > 0 && prev_ira_keoghs > 0)
			{
				return(nf.format(pctChgInIRAKeoghs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCertificatesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_certificates > 0 && prev_certificates > 0)
			{
				return(nf.format(pctChgInCertificates));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalSharesAndDepositsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_shares_and_deposits > 0 && prev_total_shares_and_deposits > 0)
			{
				return(nf.format(pctChgInTotalSharesAndDeposits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getReservesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_reserves > 0 && prev_reserves > 0)
			{
				return(nf.format(pctChgInReserves));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getUndividedEarningsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_undivided_earnings > 0 && prev_undivided_earnings > 0)
			{
				return(nf.format(pctChgInUndividedEarnings));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalCapitalPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_capital > 0 && prev_total_capital > 0)
			{
				return(nf.format(pctChgInTotalCapital));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalLiabilitiesAndCapitalPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_liabilities_and_capital > 0 && prev_total_liabilities_and_capital > 0)
			{
				return(nf.format(pctChgInTotalLiabilitiesAndCapital));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLoanAndLeaseInterestPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_loan_and_lease_interest > 0 && prev_loan_and_lease_interest > 0)
			{
				return(nf.format(pctChgInLoanAndLeaseInterest));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getRebatesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_rebates > 0 && prev_rebates > 0)
			{
				return(nf.format(pctChgInRebates));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getFeeIncomePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_fee_income > 0 && prev_fee_income > 0)
			{
				return(nf.format(pctChgInFeeIncome));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getInvestmentIncomePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_investment_income > 0 && prev_investment_income > 0)
			{
				return(nf.format(pctChgInInvestmentIncome));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOtherOperatingIncomePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_other_operating_income > 0 && prev_other_operating_income > 0)
			{
				return(nf.format(pctChgInOtherOperatingIncome));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalIncomePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_income > 0 && prev_total_income > 0)
			{
				return(nf.format(pctChgInTotalIncome));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getSalariesAndBenefitsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_salaries_and_benefits > 0 && prev_salaries_and_benefits > 0)
			{
				return(nf.format(pctChgInSalariesAndBenefits));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOfficeOccupancyPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_office_occupancy > 0 && prev_office_occupancy > 0)
			{
				return(nf.format(pctChgInOfficeOccupancy));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}
	public String getOfficeOperationsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_office_operations > 0 && prev_office_operations > 0)
			{
				return(nf.format(pctChgInOfficeOperations));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getEducationAndPromotionPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_education_and_promotion > 0 && prev_education_and_promotion > 0)
			{
				return(nf.format(pctChgInEducationAndPromotion));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getLoanServicingPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_loan_servicing > 0 && prev_loan_servicing > 0)
			{
				return(nf.format(pctChgInLoanServicing));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getProfessionalAndOutsideServicesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_professional_and_outside_services > 0 && prev_professional_and_outside_services > 0)
			{
				return(nf.format(pctChgInProfessionalAndOutsideServices));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getMemberInsurancePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_member_insurance > 0 && prev_member_insurance > 0)
			{
				return(nf.format(pctChgInMemberInsurance));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getAllOtherExpensesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_all_other_expenses > 0 && prev_all_other_expenses > 0)
			{
				return(nf.format(pctChgInAllOtherExpenses));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getExpenseSubtotalPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_expense_subtotal > 0 && prev_expense_subtotal > 0)
			{
				return(nf.format(pctChgInExpenseSubtotal));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getProvisionForLoanLossPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_provision_for_loan_loss > 0 && prev_provision_for_loan_loss > 0)
			{
				return(nf.format(pctChgInProvisionForLoanLoss));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getExpSubtotalInclProvisionsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_exp_subtotal_incl_provisions > 0 && prev_exp_subtotal_incl_provisions > 0)
			{
				return(nf.format(pctChgInExpSubtotalInclProvisions));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getNonOpGainLossPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_nonop_gain_loss > 0 && prev_nonop_gain_loss > 0)
			{
				return(nf.format(pctChgInNonOpGainLoss));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getIncomeBeforeDivsAndIntPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_income_before_divs_and_int > 0 && prev_income_before_divs_and_int > 0)
			{
				return(nf.format(pctChgInIncomeBeforeDivsAndInt));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getInterestOnBorrowingsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_interest_on_borrowings > 0 && prev_interest_on_borrowings > 0)
			{
				return(nf.format(pctChgInInterestOnBorrowings));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getDividendsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_dividends > 0 && prev_dividends > 0)
			{
				return(nf.format(pctChgInDividends));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getSubtotalPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_subtotal > 0 && prev_subtotal > 0)
			{
				return(nf.format(pctChgInSubtotal));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getReserveTransferPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_reserve_transfer > 0 && prev_reserve_transfer > 0)
			{
				return(nf.format(pctChgInReserveTransfer));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOtherCapitalTransfersPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_other_capital_transfers > 0 && prev_other_capital_transfers > 0)
			{
				return(nf.format(pctChgInOtherCapitalTransfers));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getNetIncomePctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_net_income > 0 && prev_net_income > 0)
			{
				return(nf.format(pctChgInNetIncome));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String get2To6MoDelqPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_2_to_6_mo_delq > 0 && prev_2_to_6_mo_delq > 0)
			{
				return(nf.format(pctChgIn2To6MoDelq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String get6To12MoDelqPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_6_to_12_mo_delq > 0 && prev_6_to_12_mo_delq > 0)
			{
				return(nf.format(pctChgIn6To12MoDelq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getOver12MoDelqPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_over_12_mo_delq > 0 && prev_over_12_mo_delq > 0)
			{
				return(nf.format(pctChgInOver12MoDelq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getTotalDelqPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_total_delq > 0 && prev_total_delq > 0)
			{
				return(nf.format(pctChgInTotalDelq));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getNetChargeOffsPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_net_charge_offs > 0 && prev_net_charge_offs > 0)
			{
				return(nf.format(pctChgInNetChargeOffs));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurr2To6MoDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) curr_2_to_6_mo_delq) / (double) curr_total_loans));

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurr6To12MoDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) curr_6_to_12_mo_delq) / (double) curr_total_loans));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrOver12MoDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) curr_over_12_mo_delq) / (double) curr_total_loans));

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrTotalDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) curr_total_delq) / (double) curr_total_loans));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrNetChargeOffRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) curr_net_charge_offs * annualizationFactor) /  (((double) curr_total_loans + (double) curr_year_end_total_loans) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrPCANetCapitalRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) curr_pca_net_worth_ratio /  ((double) 10000.0))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrPCANetCapitalClassification()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(curr_pca_net_worth_classification);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrTotalCapitalToAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) curr_total_capital_incl_allowances /  ((double) curr_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrNetCapitalToAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) curr_total_capital_excl_allowances /  ((double) curr_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrEstDelqToLoans()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((((double) curr_2_to_6_mo_delq * 0.30) + ((double) curr_6_to_12_mo_delq * 0.50) + (double) curr_over_12_mo_delq) /  ((double) curr_total_loans))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrForeclosedAndRepoAssetsToAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) curr_foreclosed_and_repo_assets /  ((double) curr_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrOperatingExpenseRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) curr_expense_subtotal * annualizationFactor) /  (((double) curr_total_assets + (double) curr_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrReturnOnAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) curr_net_income * annualizationFactor) /  (((double) curr_total_assets + (double) curr_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrGrossSpread()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) (curr_loan_and_lease_interest-curr_rebates+curr_investment_income-curr_dividends-curr_interest_on_borrowings) * annualizationFactor) /  (((double) curr_total_assets + (double) curr_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrFeeIncomeRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) curr_fee_income * annualizationFactor) /  (((double) curr_total_assets + (double) curr_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrLoansToSavingsRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) curr_total_loans /  ((double) curr_total_shares_and_deposits))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrBorrowingsToSavingsAndEquity()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) (curr_reverse_repos+curr_other_notes_payable) /  ((double) (curr_total_shares_and_deposits+curr_total_capital)))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrMBLRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) (curr_mbls-curr_unfunded_mbl_commitments) /  ((double) curr_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrTexasRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) (curr_foreclosed_and_repo_assets+curr_total_delq) /  ((double) curr_total_capital_incl_allowances))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getCurrCAEScore()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);

		try
		{
			double ncua_c1 = (double) curr_total_capital_incl_allowances / (double) curr_total_assets;
			double ncua_c2 = (double) curr_total_capital_excl_allowances / (double) curr_total_assets;
			double ncua_a1 = (double) curr_total_delq / (double) curr_total_loans;
			double ncua_a2 = ((double) curr_net_charge_offs * annualizationFactor)  / ((double) curr_total_loans + (double) curr_year_end_total_loans);
			double ncua_e1 = ((double) curr_net_income * annualizationFactor)  / ((double) curr_total_assets + (double) curr_year_end_total_assets);

			int ncua_c1_score = -1;
			int ncua_c2_score = -1;
			int ncua_a1_score = -1;
			int ncua_a2_score = -1;
			int ncua_e1_score = -1;

			if(curr_total_assets <= 2000000)
			{
				if(ncua_c1 > 0.1100)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.1100 & ncua_c1 > 0.0800)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0800 & ncua_c1 > 0.0400)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0400 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0950)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0950 & ncua_c2 > 0.0550)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0550 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0050)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0150)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0150 & ncua_a1 < 0.0350)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0350 & ncua_a1 < 0.0700)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0700 & ncua_a1 < 0.0950)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0075)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0075 & ncua_a2 < 0.0175)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0175 & ncua_a2 < 0.0250)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0125)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0125 & ncua_e1 > 0.0090)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0090 & ncua_e1 > 0.0040)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0040 & ncua_e1 > 0.0020)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}
			else if(curr_total_assets > 2000000 & curr_total_assets > 10000000)
			{
				if(ncua_c1 > 0.0900)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.0900 & ncua_c1 > 0.0700)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0700 & ncua_c1 > 0.0400)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0400 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0800)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0800 & ncua_c2 > 0.0600)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0600 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0100)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0150)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0150 & ncua_a1 < 0.0350)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0350 & ncua_a1 < 0.0500)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0500 & ncua_a1 < 0.0825)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0075)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0075 & ncua_a2 < 0.0150)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0150 & ncua_a2 < 0.0250)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0100)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0100 & ncua_e1 > 0.0080)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0080 & ncua_e1 > 0.0035)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0035 & ncua_e1 > 0.0015)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}
			else if(curr_total_assets > 10000000 & curr_total_assets > 50000000)
			{
				if(ncua_c1 > 0.0800)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.0800 & ncua_c1 > 0.0600)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0600 & ncua_c1 > 0.0300)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0300 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0700)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0700 & ncua_c2 > 0.0500)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0500 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0050)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0125)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0125 & ncua_a1 < 0.0250)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0250 & ncua_a1 < 0.0350)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0350 & ncua_a1 < 0.0550)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0075)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0075 & ncua_a2 < 0.0150)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0150 & ncua_a2 < 0.0200)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0100)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0100 & ncua_e1 > 0.0080)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0080 & ncua_e1 > 0.0035)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0035 & ncua_e1 > 0.0020)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}
			else     /* > $50 million */
			{
				if(ncua_c1 > 0.0800)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.0800 & ncua_c1 > 0.0600)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0600 & ncua_c1 > 0.0350)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0350 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0700)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0700 & ncua_c2 > 0.0500)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0500 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0050)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0125)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0125 & ncua_a1 < 0.0225)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0225 & ncua_a1 < 0.0325)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0325 & ncua_a1 < 0.0475)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0060)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0060 & ncua_a2 < 0.0120)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0120 & ncua_a2 < 0.0180)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0100)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0100 & ncua_e1 > 0.0080)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0080 & ncua_e1 > 0.0035)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0035 & ncua_e1 > 0.0020)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}

			double ncua_c_score = ((double) ncua_c1_score + (double) ncua_c2_score) / 2;
			double ncua_a_score = ((double) ncua_a1_score + (double) ncua_a2_score) / 2;
			double ncua_e_score = (double) ncua_e1_score;
			double cae_raw = (ncua_c_score + ncua_a_score + ncua_e_score) / 3;

			int over35_1 = 0;
			int over35_2 = 0;
			int over35_3 = 0;

			if(ncua_c_score > 3.5) over35_1 = 1;
			if(ncua_a_score > 3.5) over35_2 = 1;
			if(ncua_e_score > 3.5) over35_2 = 1;

			int over35count = over35_1 + over35_2 + over35_3; 

			int over25_1 = 0;
			int over25_2 = 0;
			int over25_3 = 0;

			if(ncua_c_score > 2.5) over25_1 = 1;
			if(ncua_a_score > 2.5) over25_2 = 1;
			if(ncua_e_score > 2.5) over25_2 = 1;

			int over25count = over25_1 + over25_2 + over25_3;

			long CAE = Math.round(cae_raw);
			if(cae_raw >= 3 & over35count == 0 & over25count <= 1) CAE = 3;


			return(nf.format(CAE));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevPCANetCapitalRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) prev_pca_net_worth_ratio /  ((double) 10000.0))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevPCANetCapitalClassification()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(prev_pca_net_worth_classification);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevTotalCapitalToAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) prev_total_capital_incl_allowances /  ((double) prev_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevNetCapitalToAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) prev_total_capital_excl_allowances /  ((double) prev_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevEstDelqToLoans()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((((double) prev_2_to_6_mo_delq * 0.30) + ((double) prev_6_to_12_mo_delq * 0.50) + (double) prev_over_12_mo_delq) /  ((double) prev_total_loans))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevForeclosedAndRepoAssetsToAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) prev_foreclosed_and_repo_assets /  ((double) prev_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevOperatingExpenseRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) prev_expense_subtotal * annualizationFactor) /  (((double) prev_total_assets + (double) prev_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevReturnOnAssets()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) prev_net_income * annualizationFactor) /  (((double) prev_total_assets + (double) prev_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevGrossSpread()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) (prev_loan_and_lease_interest-prev_rebates+prev_investment_income-prev_dividends-prev_interest_on_borrowings) * annualizationFactor) /  (((double) prev_total_assets + (double) prev_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevFeeIncomeRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) prev_fee_income * annualizationFactor) /  (((double) prev_total_assets + (double) prev_year_end_total_assets) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevLoansToSavingsRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) prev_total_loans /  ((double) prev_total_shares_and_deposits))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevBorrowingsToSavingsAndEquity()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) (prev_reverse_repos+curr_other_notes_payable) /  ((double) (prev_total_shares_and_deposits+curr_total_capital)))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevMBLRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) (prev_mbls-prev_unfunded_mbl_commitments) /  ((double) prev_total_assets))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevTexasRatio()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) ((double) (prev_foreclosed_and_repo_assets+curr_total_delq) /  ((double) prev_total_capital_incl_allowances))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevCAEScore()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);

		try
		{
			double ncua_c1 = (double) prev_total_capital_incl_allowances / (double) prev_total_assets;
			double ncua_c2 = (double) prev_total_capital_excl_allowances / (double) prev_total_assets;
			double ncua_a1 = (double) prev_total_delq / (double) prev_total_loans;
			double ncua_a2 = ((double) prev_net_charge_offs * annualizationFactor)  / ((double) prev_total_loans + (double) prev_year_end_total_loans);
			double ncua_e1 = ((double) prev_net_income * annualizationFactor)  / ((double) prev_total_assets + (double) prev_year_end_total_assets);

			int ncua_c1_score = -1;
			int ncua_c2_score = -1;
			int ncua_a1_score = -1;
			int ncua_a2_score = -1;
			int ncua_e1_score = -1;

			if(prev_total_assets <= 2000000)
			{
				if(ncua_c1 > 0.1100)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.1100 & ncua_c1 > 0.0800)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0800 & ncua_c1 > 0.0400)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0400 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0950)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0950 & ncua_c2 > 0.0550)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0550 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0050)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0150)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0150 & ncua_a1 < 0.0350)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0350 & ncua_a1 < 0.0700)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0700 & ncua_a1 < 0.0950)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0075)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0075 & ncua_a2 < 0.0175)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0175 & ncua_a2 < 0.0250)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0125)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0125 & ncua_e1 > 0.0090)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0090 & ncua_e1 > 0.0040)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0040 & ncua_e1 > 0.0020)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}
			else if(prev_total_assets > 2000000 & prev_total_assets > 10000000)
			{
				if(ncua_c1 > 0.0900)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.0900 & ncua_c1 > 0.0700)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0700 & ncua_c1 > 0.0400)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0400 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0800)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0800 & ncua_c2 > 0.0600)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0600 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0100)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0150)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0150 & ncua_a1 < 0.0350)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0350 & ncua_a1 < 0.0500)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0500 & ncua_a1 < 0.0825)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0075)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0075 & ncua_a2 < 0.0150)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0150 & ncua_a2 < 0.0250)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0100)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0100 & ncua_e1 > 0.0080)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0080 & ncua_e1 > 0.0035)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0035 & ncua_e1 > 0.0015)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}
			else if(prev_total_assets > 10000000 & prev_total_assets > 50000000)
			{
				if(ncua_c1 > 0.0800)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.0800 & ncua_c1 > 0.0600)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0600 & ncua_c1 > 0.0300)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0300 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0700)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0700 & ncua_c2 > 0.0500)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0500 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0050)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0125)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0125 & ncua_a1 < 0.0250)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0250 & ncua_a1 < 0.0350)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0350 & ncua_a1 < 0.0550)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0075)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0075 & ncua_a2 < 0.0150)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0150 & ncua_a2 < 0.0200)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0100)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0100 & ncua_e1 > 0.0080)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0080 & ncua_e1 > 0.0035)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0035 & ncua_e1 > 0.0020)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}
			else     /* > $50 million */
			{
				if(ncua_c1 > 0.0800)
				{
					ncua_c1_score = 1;
				}
				else if(ncua_c1 <= 0.0800 & ncua_c1 > 0.0600)
				{
					ncua_c1_score = 2;
				}
				else if(ncua_c1 <= 0.0600 & ncua_c1 > 0.0350)
				{
					ncua_c1_score = 3;
				}
				else if(ncua_c1 <= 0.0350 & ncua_c1 > 0.0100)
				{
					ncua_c1_score = 4;
				}
				else 
				{
					ncua_c1_score = 5;
				}

				if(ncua_c2 > 0.0700)
				{
					ncua_c2_score = 1;
				}
				else if(ncua_c2 <= 0.0700 & ncua_c2 > 0.0500)
				{
					ncua_c2_score = 2;
				}
				else if(ncua_c2 <= 0.0500 & ncua_c2 > 0.0300)
				{
					ncua_c2_score = 3;
				}
				else if(ncua_c2 <= 0.0300 & ncua_c2 > 0.0050)
				{
					ncua_c2_score = 4;
				}
				else 
				{
					ncua_c2_score = 5;
				}

				if(ncua_a1 < 0.0125)
				{
					ncua_a1_score = 1;
				}
				else if(ncua_a1 >= 0.0125 & ncua_a1 < 0.0225)
				{
					ncua_a1_score = 2;
				}
				else if(ncua_a1 >= 0.0225 & ncua_a1 < 0.0325)
				{
					ncua_a1_score = 3;
				}
				else if(ncua_a1 >= 0.0325 & ncua_a1 < 0.0475)
				{
					ncua_a1_score = 4;
				}
				else 
				{
					ncua_a1_score = 5;
				}

				if(ncua_a2 < 0.0025)
				{
					ncua_a2_score = 1;
				}
				else if(ncua_a2 >= 0.0025 & ncua_a2 < 0.0060)
				{
					ncua_a2_score = 2;
				}
				else if(ncua_a2 >= 0.0060 & ncua_a2 < 0.0120)
				{
					ncua_a2_score = 3;
				}
				else if(ncua_a2 >= 0.0120 & ncua_a2 < 0.0180)
				{
					ncua_a2_score = 4;
				}
				else 
				{
					ncua_a2_score = 5;
				}

				if(ncua_e1 > 0.0100)
				{
					ncua_e1_score = 1;
				}
				else if(ncua_e1 <= 0.0100 & ncua_e1 > 0.0080)
				{
					ncua_e1_score = 2;
				}
				else if(ncua_e1 <= 0.0080 & ncua_e1 > 0.0035)
				{
					ncua_e1_score = 3;
				}
				else if(ncua_e1 <= 0.0035 & ncua_e1 > 0.0020)
				{
					ncua_e1_score = 4;
				}
				else 
				{
					ncua_e1_score = 5;
				}
			}

			double ncua_c_score = ((double) ncua_c1_score + (double) ncua_c2_score) / 2;
			double ncua_a_score = ((double) ncua_a1_score + (double) ncua_a2_score) / 2;
			double ncua_e_score = (double) ncua_e1_score;
			double cae_raw = (ncua_c_score + ncua_a_score + ncua_e_score) / 3;

			int over35_1 = 0;
			int over35_2 = 0;
			int over35_3 = 0;

			if(ncua_c_score > 3.5) over35_1 = 1;
			if(ncua_a_score > 3.5) over35_2 = 1;
			if(ncua_e_score > 3.5) over35_2 = 1;

			int over35count = over35_1 + over35_2 + over35_3; 

			int over25_1 = 0;
			int over25_2 = 0;
			int over25_3 = 0;

			if(ncua_c_score > 2.5) over25_1 = 1;
			if(ncua_a_score > 2.5) over25_2 = 1;
			if(ncua_e_score > 2.5) over25_2 = 1;

			int over25count = over25_1 + over25_2 + over25_3;

			long CAE = Math.round(cae_raw);
			if(cae_raw >= 3 & over35count == 0 & over25count <= 1) CAE = 3;


			return(nf.format(CAE));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrev2To6MoDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) prev_2_to_6_mo_delq /  ((double) prev_total_loans))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrev6To12MoDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) prev_6_to_12_mo_delq /  ((double) prev_total_loans))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevOver12MoDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) prev_over_12_mo_delq /  ((double) prev_total_loans))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevTotalDelinquencyRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{
			return(nf.format((double) ((double) prev_total_delq) / (double) prev_total_loans));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevNetChargeOffRate()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		try
		{			
			return(nf.format((double) (((double) prev_net_charge_offs * annualizationFactor) /  (((double) prev_total_loans + (double) prev_year_end_total_loans) / 2))));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPrevMembers()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_members != -1)
			{
				return(nf.format(prev_members));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevPotentialMembers()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_potential_members != -1)
			{
				return(nf.format(prev_potential_members));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevPartTimeEmployees()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_part_time_employees != -1)
			{
				return(nf.format(prev_part_time_employees));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevFullTimeEmployees()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_full_time_employees != -1)
			{
				return(nf.format(prev_full_time_employees));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getPrevBranches()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (prev_branches != -1)
			{
				return(nf.format(prev_branches));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrMembers()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_members != -1)
			{
				return(nf.format(curr_members));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrPotentialMembers()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_potential_members != -1)
			{
				return(nf.format(curr_potential_members));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrPartTimeEmployees()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_part_time_employees != -1)
			{
				return(nf.format(curr_part_time_employees));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrFullTimeEmployees()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_full_time_employees != -1)
			{
				return(nf.format(curr_full_time_employees));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrBranches()
	{
		NumberFormat nf = NumberFormat.getNumberInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);

		try
		{
			if (curr_branches != -1)
			{
				return(nf.format(curr_branches));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("N/A");
	}

	public String getCurrFHLBMember()
	{
		if (curr_member_of_fhlb != -1)
		{
			return ("Yes");
		}
		else
		{
			return ("No");
		}
	}
	
	public String getPrevFHLBMember()
	{
		if (prev_member_of_fhlb != -1)
		{
			return ("Yes");
		}
		else
		{
			return ("No");
		}
	}

	public String getMembersPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_members > 0 && prev_members > 0)
			{
				return(nf.format(pctChgInMembers));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPotentialMembersPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_potential_members > 0 && prev_potential_members > 0)
			{
				return(nf.format(pctChgInPotentialMembers));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getPartTimeEmployeesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_part_time_employees > 0 && prev_part_time_employees > 0)
			{
				return(nf.format(pctChgInPartTimeEmployees));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getFullTimeEmployeesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_full_time_employees > 0 && prev_full_time_employees > 0)
			{
				return(nf.format(pctChgInFullTimeEmployees));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}

	public String getBranchesPctChg()
	{
		NumberFormat nf = NumberFormat.getPercentInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);

		try
		{
			if (curr_branches > 0 && prev_branches > 0)
			{
				return(nf.format(pctChgInBranches));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return ("");
	}


	private String id = null;

	private double annualizationFactor = -1;

	private long curr_cash_on_hand = -1;
	private long curr_cash_on_deposit = -1;
	private long curr_cash_equivalents = -1;
	private long curr_cash_and_equivalents = -1;
	private long curr_government_securities = -1;
	private long curr_federal_agency_securities = -1;
	private long curr_corporate_credit_unions = -1;
	private long curr_bank_deposits = -1;
	private long curr_mutual_funds = -1;
	private long curr_all_other_investments = -1;
	private long curr_total_investments = -1;
	private long curr_loans_held_for_sale = -1;

	private long curr_credit_cards = -1;
	private long curr_other_unsecured_loans = -1;
	private long curr_pal_loans = -1;
	private long curr_edu_loans = -1;
	private long curr_new_auto_loans = -1;
	private long curr_used_auto_loans = -1;
	private long curr_first_mortgages = -1;
	private long curr_second_mortgages = -1;
	private long curr_leases = -1;
	private long curr_all_other_loans= -1;
	private long curr_total_loans = -1;
	private long curr_loan_loss_allowance = -1;
	private long curr_mbls = -1;
	private long curr_unfunded_mbl_commitments = -1;

	private long curr_forclosed_and_repo_assets = -1;
	private long curr_land_and_building = -1;
	private long curr_other_fixed_assets = -1;
	private long curr_share_insurance_deposit = -1;
	private long curr_intangible_assets = -1;
	private long curr_other_assets = -1;
	private long curr_total_assets = -1;
	private long curr_loans_granted_ytd = -1;
	private long curr_pal_loans_granted_ytd = -1;
	private long curr_deferred_edu_loans = -1;
	private long curr_loans_to_execs = -1;

	private long curr_reverse_repos = -1;
	private long curr_other_notes_payable = -1;
	private long curr_all_other_liabilities = -1;
	private long curr_total_liabilities = -1;
	private long curr_regular_shares = -1;
	private long curr_share_drafts = -1;
	private long curr_mmas = -1;
	private long curr_ira_keoghs = -1;
	private long curr_certificates = -1;
	private long curr_total_shares_and_deposits = -1;
	private long curr_reserves = -1;
	private long curr_undivided_earnings = -1;
	private long curr_total_capital = -1;
	private long curr_total_liabilities_and_capital = -1;

	private long curr_loan_and_lease_interest = -1;
	private long curr_rebates = -1;
	private long curr_fee_income = -1;
	private long curr_investment_income = -1;
	private long curr_other_operating_income = -1;
	private long curr_total_income = -1;
	private long curr_salaries_and_benefits = -1;
	private long curr_office_occupancy = -1;
	private long curr_office_operations = -1;
	private long curr_education_and_promotion = -1;
	private long curr_loan_servicing = -1;
	private long curr_professional_and_outside_services = -1;
	private long curr_member_insurance = -1;
	private long curr_all_other_expenses = -1;
	private long curr_expense_subtotal = -1;
	private long curr_provision_for_loan_loss = -1;

	private long curr_exp_subtotal_incl_provisions = -1;
	private long curr_nonop_gain_loss = -1;
	private long curr_income_before_divs_and_int = -1;
	private long curr_interest_on_borrowings = -1;
	private long curr_dividends = -1;
	private long curr_subtotal = -1;
	private long curr_reserve_transfer = -1;
	private long curr_other_capital_transfers = -1;
	private long curr_net_income = -1;

	private long curr_2_to_6_mo_delq = -1;
	private long curr_6_to_12_mo_delq = -1;
	private long curr_over_12_mo_delq = -1;
	private long curr_total_delq = -1;
	private long curr_net_charge_offs = -1;

	private int curr_pca_net_worth_ratio = -1;
	private String curr_pca_net_worth_classification = null;
	private long curr_total_capital_incl_allowances = -1;
	private long curr_total_capital_excl_allowances = -1;
	private long curr_foreclosed_and_repo_assets = -1;

	private long curr_year_end_total_assets = -1;
	private long curr_year_end_total_loans = -1;

	private long curr_members = -1;
	private long curr_potential_members = -1;
	private long curr_part_time_employees = -1;
	private long curr_full_time_employees = -1;
	private long curr_branches = -1;
	private long curr_member_of_fhlb = -1;

	private long prev_cash_on_hand = -1;
	private long prev_cash_on_deposit = -1;
	private long prev_cash_equivalents = -1;
	private long prev_cash_and_equivalents = -1;
	private long prev_government_securities = -1;
	private long prev_federal_agency_securities = -1;
	private long prev_corporate_credit_unions = -1;
	private long prev_bank_deposits = -1;
	private long prev_mutual_funds = -1;
	private long prev_all_other_investments= -1;
	private long prev_total_investments= -1;
	private long prev_loans_held_for_sale = -1;

	private long prev_credit_cards = -1;
	private long prev_other_unsecured_loans = -1;
	private long prev_pal_loans = -1;
	private long prev_edu_loans = -1;
	private long prev_new_auto_loans = -1;
	private long prev_used_auto_loans = -1;
	private long prev_first_mortgages = -1;
	private long prev_second_mortgages = -1;
	private long prev_leases = -1;
	private long prev_all_other_loans= -1;
	private long prev_total_loans = -1;
	private long prev_loan_loss_allowance = -1;
	private long prev_mbls = -1;
	private long prev_unfunded_mbl_commitments = -1;

	private long prev_forclosed_and_repo_assets = -1;
	private long prev_land_and_building = -1;
	private long prev_other_fixed_assets = -1;
	private long prev_share_insurance_deposit = -1;
	private long prev_intangible_assets = -1;
	private long prev_other_assets = -1;
	private long prev_total_assets = -1;
	private long prev_loans_granted_ytd = -1;
	private long prev_pal_loans_granted_ytd = -1;
	private long prev_deferred_edu_loans = -1;
	private long prev_loans_to_execs = -1;

	private long prev_reverse_repos = -1;
	private long prev_other_notes_payable = -1;
	private long prev_all_other_liabilities = -1;
	private long prev_total_liabilities = -1;
	private long prev_regular_shares = -1;
	private long prev_share_drafts = -1;
	private long prev_mmas = -1;
	private long prev_ira_keoghs = -1;
	private long prev_certificates = -1;
	private long prev_total_shares_and_deposits = -1;
	private long prev_reserves = -1;
	private long prev_undivided_earnings = -1;
	private long prev_total_capital = -1;
	private long prev_total_liabilities_and_capital = -1;

	private long prev_loan_and_lease_interest = -1;
	private long prev_rebates = -1;
	private long prev_fee_income = -1;
	private long prev_investment_income = -1;
	private long prev_other_operating_income = -1;
	private long prev_total_income = -1;
	private long prev_salaries_and_benefits = -1;
	private long prev_office_occupancy = -1;
	private long prev_office_operations = -1;
	private long prev_education_and_promotion = -1;
	private long prev_loan_servicing = -1;
	private long prev_professional_and_outside_services = -1;
	private long prev_member_insurance = -1;
	private long prev_all_other_expenses = -1;
	private long prev_expense_subtotal = -1;
	private long prev_provision_for_loan_loss = -1;

	private long prev_exp_subtotal_incl_provisions = -1;
	private long prev_nonop_gain_loss = -1;
	private long prev_income_before_divs_and_int = -1;
	private long prev_interest_on_borrowings = -1;
	private long prev_dividends = -1;
	private long prev_subtotal = -1;
	private long prev_reserve_transfer = -1;
	private long prev_other_capital_transfers = -1;
	private long prev_net_income = -1;

	private long prev_2_to_6_mo_delq = -1;
	private long prev_6_to_12_mo_delq = -1;
	private long prev_over_12_mo_delq = -1;
	private long prev_total_delq = -1;
	private long prev_net_charge_offs = -1;

	private int prev_pca_net_worth_ratio = -1;
	private String prev_pca_net_worth_classification = null;
	private long prev_total_capital_incl_allowances = -1;
	private long prev_total_capital_excl_allowances= -1;
	private long prev_foreclosed_and_repo_assets = -1;

	private long prev_year_end_total_assets = -1;
	private long prev_year_end_total_loans = -1;

	private long prev_members = -1;
	private long prev_potential_members = -1;
	private long prev_part_time_employees = -1;
	private long prev_full_time_employees = -1;
	private long prev_branches = -1;
	private long prev_member_of_fhlb = -1;

	private double pctChgInCashOnHand = -1;
	private double pctChgInCashOnDeposit = -1;
	private double pctChgInCashEquivalents = -1;
	private double pctChgInCashAndEquivalents = -1;

	private double pctChgInGovSecs = -1;
	private double pctChgInFedAgencySecs = -1;
	private double pctChgInCorpCUs = -1;
	private double pctChgInBankDeposits = -1;
	private double pctChgInMutualFunds = -1;
	private double pctChgInAllOtherInvs = -1;
	private double pctChgInTotalInvestments = -1;
	private double pctChgInLoansHeldForSale = -1;

	private double pctChgInCreditCards = -1;
	private double pctChgInOtherUnsecuredLoans = -1;
	private double pctChgInPALLoans = -1;
	private double pctChgInEduLoans = -1;
	private double pctChgInNewAutoLoans = -1;
	private double pctChgInUsedAutoLoans = -1;
	private double pctChgInFirstMortgages = -1;
	private double pctChgInSecondMortgages = -1;
	private double pctChgInLeases = -1;
	private double pctChgInAllOtherLoans = -1;
	private double pctChgInTotalLoans = -1;
	private double pctChgInAllowance = -1;
	private double pctChgInMBLs = -1;

	private double pctChgInRepoAssets = -1;
	private double pctChgInLandAndBuilding = -1;
	private double pctChgInOtherFixedAssets = -1;
	private double pctChgInInsuranceDeposit = -1;
	private double pctChgInIntangibleAssets = -1;
	private double pctChgInOtherAssets = -1;
	private double pctChgInTotalAssets = -1;
	private double pctChgInLoansGrantedYTD = -1;
	private double pctChgInPALLoansGrantedYTD = -1;
	private double pctChgInDeferredEduLoans = -1;
	private double pctChgInLoansToExecs = -1;

	private double pctChgInReverseRepos = -1;
	private double pctChgInOtherNotesPayable = -1;
	private double pctChgInAllOtherLiabilities = -1;
	private double pctChgInTotalLiabilities = -1;
	private double pctChgInRegularShares = -1;
	private double pctChgInShareDrafts = -1;
	private double pctChgInMMAs = -1;
	private double pctChgInIRAKeoghs = -1;
	private double pctChgInCertificates = -1;
	private double pctChgInTotalSharesAndDeposits = -1;
	private double pctChgInReserves = -1;
	private double pctChgInUndividedEarnings = -1;
	private double pctChgInTotalCapital = -1;
	private double pctChgInTotalLiabilitiesAndCapital = -1;

	private double pctChgInLoanAndLeaseInterest = -1;
	private double pctChgInRebates = -1;
	private double pctChgInFeeIncome = -1;
	private double pctChgInInvestmentIncome = -1;
	private double pctChgInOtherOperatingIncome = -1;
	private double pctChgInTotalIncome = -1;
	private double pctChgInSalariesAndBenefits = -1;
	private double pctChgInOfficeOccupancy = -1;
	private double pctChgInOfficeOperations = -1;
	private double pctChgInEducationAndPromotion = -1;
	private double pctChgInLoanServicing = -1;
	private double pctChgInProfessionalAndOutsideServices = -1;
	private double pctChgInMemberInsurance = -1;
	private double pctChgInAllOtherExpenses = -1;
	private double pctChgInExpenseSubtotal = -1;
	private double pctChgInProvisionForLoanLoss = -1;

	private double pctChgInExpSubtotalInclProvisions = -1;
	private double pctChgInNonOpGainLoss = -1;
	private double pctChgInIncomeBeforeDivsAndInt = -1;
	private double pctChgInInterestOnBorrowings = -1;
	private double pctChgInDividends = -1;
	private double pctChgInSubtotal = -1;
	private double pctChgInReserveTransfer = -1;
	private double pctChgInOtherCapitalTransfers = -1;
	private double pctChgInNetIncome = -1;

	private double pctChgIn2To6MoDelq = -1;
	private double pctChgIn6To12MoDelq = -1;
	private double pctChgInOver12MoDelq = -1;
	private double pctChgInTotalDelq = -1;
	private double pctChgInNetChargeOffs = -1;

	private double pctChgInMembers = -1;
	private double pctChgInPotentialMembers = -1;
	private double pctChgInPartTimeEmployees = -1;
	private double pctChgInFullTimeEmployees = -1;
	private double pctChgInBranches = -1;
}
