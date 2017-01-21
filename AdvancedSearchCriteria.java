package coop.cuna.cdean;

public class AdvancedSearchCriteria
{
	public AdvancedSearchCriteria(String name, String city, String state, String zipCode, String phone, String url, String ceoLastName, boolean includeInactives)
	{
		System.out.println("City == " + city);
		System.out.println("Include Inactives == " + includeInactives);
		
		
	}
	
	private String theCity = null;
	private String theState = null;
	private String theZipCode = null;
	private String thePhone = null;
	private String theURL = null;
	private String theCeoLastName = null;
}