package coop.cuna.cdean;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.beans.value.ChangeListener;
import java.sql.*;
import java.text.NumberFormat;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;


public class CU_Info extends Application 
{
	public static void main(String[] args) 
	{	
		CU_Info.financialsPeriod = Utils.convertPeriod(Utils.financialPeriods[0]);
		Application.launch(args);
	}

	public void start(Stage primaryStage) 
	{
		try
		{
			if(dbConn == null)
			{
				dbConn = Utils.getConnection();
			}
		}
		catch(Exception e)
		{
			System.out.println("Unable to establish connection to the database.");
			e.printStackTrace();
		}

		primaryStage.setTitle("1CUNA - Economics and Statistics");

		/* Defaults to the finest credit union in the land.. */
		if (aCreditUnion==null)
		{
			setCreditUnion("32999");
			cuidBatch = new String[1];
			cuidBatch[0] = "32999";
		}
		else
		{
			setCreditUnion(aCreditUnion.getID());
		}

		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 675, 750, Color.WHITE);

		MenuBar menuBar = buildMenuBar(primaryStage);

		BorderPane cuInfo = new BorderPane();
		Text title = buildTitleBar();
		BorderPane.setAlignment(title, Pos.TOP_CENTER);

		cuInfo.setTop(title);

		if(whichScreen == mainScreen)
		{
			cuInfo.setLeft(buildMainScreenLeftColumn(primaryStage));
			cuInfo.setRight(buildMainScreenRightColumn());
		}
		else if(whichScreen == mergersScreen)
		{
			cuInfo.setCenter(buildAllMergersBox(primaryStage));
		}
		else if(whichScreen == twoYrFinancialsCompScreen)
		{	
			if(financialsScreenNumber == 1)
			{
				if(balanceSheetScreenNumber == 1)
				{
					cuInfo.setCenter(buildCashAndInvestmentsBox(primaryStage));
				}
				else if(balanceSheetScreenNumber == 2)
				{
					cuInfo.setCenter(buildLoansBox(primaryStage));
				}
				else if(balanceSheetScreenNumber == 3)
				{
					cuInfo.setCenter(buildOtherAssetsBox(primaryStage));
				}
				else if(balanceSheetScreenNumber == 4)
				{
					cuInfo.setCenter(buildLiabilitiesAndCapitalBox(primaryStage));
				}
			}
			else if(financialsScreenNumber == 2)
			{
				if(incomeStatementScreenNumber == 1)
				{
					cuInfo.setCenter(buildIncomeStatementPage1Box(primaryStage));
				}
				else if(incomeStatementScreenNumber == 2)
				{
					cuInfo.setCenter(buildIncomeStatementPage2Box(primaryStage));
				}
			}
			else if(financialsScreenNumber == 3)
			{
				cuInfo.setCenter(buildAssetQualityBox(primaryStage));
			}
			else if(financialsScreenNumber == 4)
			{
				cuInfo.setCenter(buildKeyRatiosBox(primaryStage));
			}
			else if(financialsScreenNumber == 5)
			{
				cuInfo.setCenter(buildDemographicsBox(primaryStage));
			}
		}
		else if(whichScreen == advancedSearchScreen)
		{
			cuInfo.setCenter(buildAdvancedSearchBox(primaryStage));
		}
		else if(whichScreen == duesScreen)
		{
			cuInfo.setCenter(buildDuesBox(primaryStage));
		}
		
		Label failedSearchScene = new Label("Search Failed to Find Any CUs Matching that Criteria.");
		failedSearchScene.setFont(Font.font("Serif", 15));
		Button failedSearchButton  = new Button("OK");
		
		BorderPane failedToFindPane = new BorderPane();
		BorderPane.setAlignment(failedSearchScene, Pos.BOTTOM_CENTER);
		BorderPane.setAlignment(failedSearchButton, Pos.CENTER);
		failedToFindPane.setTop(failedSearchScene);
		failedToFindPane.setCenter(failedSearchButton);
		
		Scene failedToFindScene = new Scene(failedToFindPane, 350, 75);
		Stage failedToFindStage = new Stage();
		failedToFindStage.setScene(failedToFindScene);
		failedToFindStage.initModality(Modality.APPLICATION_MODAL);
		failedToFindStage.setTitle("No Credit Unions Found!!");

		failedSearchButton.setOnAction((event) -> 
		{
			noneFound = false;
			failedToFindStage.close();
		});

		
		VBox findPanel = buildSearchBar(primaryStage);
		
		if(whichScreen != advancedSearchScreen)
		{
			cuInfo.setBottom(findPanel);
		}
		
		root.setTop(menuBar);
		root.setCenter(cuInfo);
		primaryStage.setScene(scene);
		primaryStage.show();

		if (noneFound == true)
		{
			failedToFindStage.showAndWait();
		}
	}
	
	public void stop() 
	{
		Utils.closeConnection(dbConn);
	}

	public void setCreditUnion(String cuid)
	{
		try
		{
			aCreditUnion = new CreditUnion(dbConn, cuid, CU_Info.financialsPeriod);
		}
		catch(Exception e)
		{
			System.out.println("Had a problem setting the credit union in CU_Info.");
			e.printStackTrace();
		}
	}

	public CreditUnion getCreditUnion()
	{
		return (aCreditUnion);
	}

	public void setScreen(int whichOne)
	{
		whichScreen = whichOne;
	}

	public void resetMergersScreen()
	{
		mergerScreenNumber = 1;
	}

	private MenuBar buildMenuBar(Stage primaryStage)
	{
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		Menu fileMenu = new Menu("File");

		MenuItem mainScreenMenuItem  = new MenuItem("Credit Union Overview");
		mainScreenMenuItem.setOnAction((event) -> 
		{
			setScreen(CU_Info.mainScreen);
			start(primaryStage);
		});
		
		MenuItem advancedSearchMenuItem  = new MenuItem("Advanced Search");
		advancedSearchMenuItem.setOnAction((event) -> 
		{
			setScreen(CU_Info.advancedSearchScreen);
			start(primaryStage);
		});

		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction(actionEvent -> Platform.exit() );

		Menu mergersMenu = new Menu("Mergers");
		MenuItem mergersMenuItem = new MenuItem("Merger List");
		mergersMenuItem.setOnAction((event) -> 
		{
			setScreen(CU_Info.mergersScreen);
			start(primaryStage);
		});

		Menu financialsMenu = new Menu("Financials");
		MenuItem twoYrFinancialsMenuItem = new MenuItem("Two Year Financial Comparison");
		twoYrFinancialsMenuItem.setOnAction((event) -> 
		{
			setScreen(CU_Info.twoYrFinancialsCompScreen);
			start(primaryStage);
		});
		
		Menu oneCUNAMenu = new Menu("1CUNA");
		MenuItem duesMenuItem = new MenuItem("Dues Estimates");
		duesMenuItem.setOnAction((event) -> 
		{
			setScreen(CU_Info.duesScreen);
			start(primaryStage);
		});

		fileMenu.getItems().addAll(mainScreenMenuItem, advancedSearchMenuItem, new SeparatorMenuItem(), exitMenuItem);
		mergersMenu.getItems().addAll(new SeparatorMenuItem(), mergersMenuItem);
		financialsMenu.getItems().addAll(new SeparatorMenuItem(), twoYrFinancialsMenuItem);
		oneCUNAMenu.getItems().addAll(new SeparatorMenuItem(), duesMenuItem);


		//menuBar.getMenus().addAll(fileMenu, mergersMenu, financialsMenu);
		menuBar.getMenus().addAll(fileMenu, mergersMenu, financialsMenu, oneCUNAMenu);

		return (menuBar);
	}

	private Text buildTitleBar()
	{
		Text title = new Text("Credit Union Info");
		title.setFont(Font.font("Serif", 30));
		title.setFill(Color.BLACK);
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(2.0f);
		dropShadow.setOffsetY(2.0f);
		dropShadow.setColor(Color.rgb(50, 50, 50, .588));
		title.setEffect(dropShadow);
		title.setTextAlignment(TextAlignment.CENTER);

		return (title);
	}

	private VBox buildNameAndAddressBox()
	{
		Text cuName = new Text(aCreditUnion.getName());
		cuName.setFont(Font.font("Serif", 18));
		cuName.setFill(Color.BLUE);

		Text cuStAddr = new Text(aCreditUnion.getStreetAddress());
		cuStAddr.setFont(Font.font("Serif", 14));
		cuStAddr.setFill(Color.BLUE);

		Text cuStCity = new Text(aCreditUnion.getStreetCity() + ", " + aCreditUnion.getStreetState() + " " + aCreditUnion.getStreetZip());
		cuStCity.setFont(Font.font("Serif", 14));
		cuStCity.setFill(Color.BLUE);

		VBox nameAndAddrBox = new VBox();
		nameAndAddrBox.getChildren().add(cuName);
		nameAndAddrBox.getChildren().add(cuStAddr);
		nameAndAddrBox.getChildren().add(cuStCity);

		return (nameAndAddrBox);
	}

	private HBox buildPhoneAndFaxBox()
	{
		Text phoneLabel = new Text("Phone: ");
		phoneLabel.setFont(Font.font("Serif", 14));
		phoneLabel.setFill(Color.BLACK);

		Text cuPhone = new Text(aCreditUnion.getPhoneNumber());
		cuPhone.setFont(Font.font("Serif", 14));
		cuPhone.setFill(Color.BLUE);

		Text faxLabel = new Text("Fax: ");
		faxLabel.setFont(Font.font("Serif", 14));
		faxLabel.setFill(Color.BLACK);
		faxLabel.setTextAlignment(TextAlignment.CENTER);

		Text cuFax = new Text(aCreditUnion.getFaxNumber());
		cuFax.setFont(Font.font("Serif", 14));
		cuFax.setFill(Color.BLUE);

		HBox phoneAndFaxBox = new HBox();
		phoneAndFaxBox.getChildren().add(phoneLabel);
		phoneAndFaxBox.getChildren().add(cuPhone);
		phoneAndFaxBox.getChildren().add(new Text("  "));
		phoneAndFaxBox.getChildren().add(faxLabel);
		phoneAndFaxBox.getChildren().add(cuFax);

		return (phoneAndFaxBox);
	}

	private VBox buildMailingAddressBox()
	{
		Text poaddrLabel = new Text("Mailing Address:");
		poaddrLabel.setFont(Font.font("Serif", 14));
		poaddrLabel.setFill(Color.BLACK);

		Text cuPOAddr = new Text("     " + aCreditUnion.getPostalAddress());
		cuPOAddr.setFont(Font.font("Serif", 14));
		cuPOAddr.setFill(Color.BLUE);

		Text cuPOCity = new Text("     " + aCreditUnion.getPostalCity() + ", " + aCreditUnion.getPostalState() + " " + aCreditUnion.getPostalZip());
		cuPOCity.setFont(Font.font("Serif", 14));
		cuPOCity.setFill(Color.BLUE);

		VBox mailingAddrBox = new VBox();
		mailingAddrBox.getChildren().add(poaddrLabel);
		mailingAddrBox.getChildren().add(cuPOAddr);
		mailingAddrBox.getChildren().add(cuPOCity);

		return (mailingAddrBox);
	}

	private HBox buildWebsiteBox()
	{
		Text webLabel = new Text("Web Address: ");
		webLabel.setFont(Font.font("Serif", 14));
		webLabel.setFill(Color.BLACK);

		Hyperlink hpl = new Hyperlink(aCreditUnion.getWebsite());
		hpl.setFont(Font.font("Serif", 14));
		hpl.setTextFill(Color.BLUE);
		hpl.setBorder(null);

		hpl.setOnAction(new EventHandler<ActionEvent>() 
		{   
			public void handle(ActionEvent e) 
			{        
				getHostServices().showDocument(hpl.getText());
			}
		});

		HBox websiteBox = new HBox();
		websiteBox.getChildren().add(webLabel);
		websiteBox.getChildren().add(hpl);

		return (websiteBox);
	}

	private VBox buildContactBox()
	{
		VBox nameAndAddrBox = buildNameAndAddressBox();
		HBox phoneAndFaxBox = buildPhoneAndFaxBox();
		VBox mailingAddrBox = buildMailingAddressBox();
		HBox websiteBox = buildWebsiteBox();

		VBox contactBox = new VBox();
		contactBox.getChildren().add(nameAndAddrBox);
		contactBox.getChildren().add(phoneAndFaxBox);
		contactBox.getChildren().add(mailingAddrBox);
		contactBox.getChildren().add(websiteBox);
		contactBox.setPadding(new Insets(10));
		contactBox.setSpacing(5);

		return (contactBox);
	}

	private HBox buildCEOBox()
	{
		Text ceoLabel = new Text("CEO: ");
		ceoLabel.setFont(Font.font("Serif", 14));
		ceoLabel.setFill(Color.BLACK);

		Text cuCeo = new Text(aCreditUnion.getCEO());
		cuCeo.setFont(Font.font("Serif", 14));
		cuCeo.setFill(Color.BLUE);

		HBox ceoBox = new HBox();
		ceoBox.getChildren().add(ceoLabel);
		ceoBox.getChildren().add(cuCeo);

		return (ceoBox);
	}

	private VBox buildTomBox()
	{
		Text tomLabel = new Text("Type Of Membership: ");
		tomLabel.setFont(Font.font("Serif", 14));
		tomLabel.setFill(Color.BLACK);

		Text cuTom = new Text(aCreditUnion.getTomDescription());
		cuTom.setFont(Font.font("Serif", 14));
		cuTom.setFill(Color.BLUE);

		VBox tomBox = new VBox();
		tomBox.getChildren().add(tomLabel);
		tomBox.getChildren().add(cuTom);

		return (tomBox);
	}

	private HBox buildOrgDateBox()
	{
		Text orgDateLabel = new Text("Organization Date: ");
		orgDateLabel.setFont(Font.font("Serif", 14));
		orgDateLabel.setFill(Color.BLACK);

		Text cuOrgDate = new Text(aCreditUnion.getOrganizationDate());
		cuOrgDate.setFont(Font.font("Serif", 14));
		cuOrgDate.setFill(Color.BLUE);

		HBox orgDateBox = new HBox();
		orgDateBox.getChildren().add(orgDateLabel);
		orgDateBox.getChildren().add(cuOrgDate);

		return (orgDateBox);
	}

	private HBox buildMBLGrandfatheredBox()
	{
		Text mblGrandfatheredLabel = new Text("MBL Grandfathered: ");
		mblGrandfatheredLabel.setFont(Font.font("Serif", 14));
		mblGrandfatheredLabel.setFill(Color.BLACK);

		Text cuMBLGrandfathered = new Text(aCreditUnion.isMBLGrandfathered());
		cuMBLGrandfathered.setFont(Font.font("Serif", 14));
		cuMBLGrandfathered.setFill(Color.BLUE);

		HBox grandfatheredBox = new HBox();
		grandfatheredBox.getChildren().add(mblGrandfatheredLabel);
		grandfatheredBox.getChildren().add(cuMBLGrandfathered);

		return (grandfatheredBox);
	}

	private HBox buildMBLExemptBox()
	{
		Text mblExemptLabel = new Text("MBL Exempt: ");
		mblExemptLabel.setFont(Font.font("Serif", 14));
		mblExemptLabel.setFill(Color.BLACK);

		Text cuMBLExempt = new Text(aCreditUnion.isMBLExempt());
		cuMBLExempt.setFont(Font.font("Serif", 14));
		cuMBLExempt.setFill(Color.BLUE);

		HBox mblExemptBox = new HBox();
		mblExemptBox.getChildren().add(mblExemptLabel);
		mblExemptBox.getChildren().add(cuMBLExempt);

		return (mblExemptBox);
	}

	private HBox buildLowIncomeDesignatedBox()
	{
		Text lowIncLabel = new Text("Low Income Designated: ");
		lowIncLabel.setFont(Font.font("Serif", 14));
		lowIncLabel.setFill(Color.BLACK);

		Text cuLowInc = new Text(aCreditUnion.getLowIncomeDesignated());
		cuLowInc.setFont(Font.font("Serif", 14));
		cuLowInc.setFill(Color.BLUE);

		HBox lowIncBox = new HBox();
		lowIncBox.getChildren().add(lowIncLabel);
		lowIncBox.getChildren().add(cuLowInc);

		return (lowIncBox);
	}

	private VBox buildDemographicsBox()
	{
		HBox ceoBox = buildCEOBox();
		VBox tomBox = buildTomBox();
		HBox orgDateBox = buildOrgDateBox();
		HBox grandfatheredBox = buildMBLGrandfatheredBox();
		HBox mblExemptBox = buildMBLExemptBox();
		HBox lowIncBox = buildLowIncomeDesignatedBox();

		VBox demographicBox = new VBox();
		demographicBox.getChildren().add(ceoBox);
		demographicBox.getChildren().add(tomBox);
		demographicBox.getChildren().add(orgDateBox);
		demographicBox.getChildren().add(grandfatheredBox);
		demographicBox.getChildren().add(mblExemptBox);
		demographicBox.getChildren().add(lowIncBox);
		demographicBox.setPadding(new Insets(10));
		demographicBox.setSpacing(5);

		return (demographicBox);
	}

	private HBox buildFinancialsDateBox(Stage primaryStage)
	{
		ComboBox financialsComboBox = new ComboBox();
		financialsComboBox.getItems().addAll(Utils.financialPeriods);
		financialsComboBox.setValue(Utils.convertToLongPeriod(financialsPeriod));

		financialsComboBox.setOnAction((event) -> 
		{
			CU_Info.financialsPeriod = Utils.convertPeriod(financialsComboBox.getValue().toString());
			start(primaryStage);
		});

		Text finDateLabel = new Text("Financial Data As Of: ");
		finDateLabel.setFont(Font.font("Serif", 14));
		finDateLabel.setFill(Color.BLACK);

		Text cuFinDate = new Text(aCreditUnion.getPeriod());
		cuFinDate.setFont(Font.font("Serif", 14));
		cuFinDate.setFill(Color.BLUE);

		HBox finDateBox = new HBox();
		finDateBox.getChildren().add(finDateLabel);
		finDateBox.getChildren().add(financialsComboBox);

		return (finDateBox);
	}

	private HBox buildAssetsBox()
	{
		Text assetsLabel = new Text("Assets: ");
		assetsLabel.setFont(Font.font("Serif", 14));
		assetsLabel.setFill(Color.BLACK);

		Text cuAssets = new Text(aCreditUnion.getAssets());
		cuAssets.setFont(Font.font("Serif", 14));
		cuAssets.setFill(Color.BLUE);

		HBox assetsBox = new HBox();
		assetsBox.getChildren().add(assetsLabel);
		assetsBox.getChildren().add(cuAssets);

		return (assetsBox);
	}

	private HBox buildLoansBox()
	{
		Text loansLabel = new Text("Loans: ");
		loansLabel.setFont(Font.font("Serif", 14));
		loansLabel.setFill(Color.BLACK);

		Text cuLoans = new Text(aCreditUnion.getLoans());
		cuLoans.setFont(Font.font("Serif", 14));
		cuLoans.setFill(Color.BLUE);

		HBox loansBox = new HBox();
		loansBox.getChildren().add(loansLabel);
		loansBox.getChildren().add(cuLoans);

		return (loansBox);
	}

	private HBox buildSavingsBox()
	{
		Text savingsLabel = new Text("Savings: ");
		savingsLabel.setFont(Font.font("Serif", 14));
		savingsLabel.setFill(Color.BLACK);

		Text cuSavings = new Text(aCreditUnion.getSavings());
		cuSavings.setFont(Font.font("Serif", 14));
		cuSavings.setFill(Color.BLUE);

		HBox savingsBox = new HBox();
		savingsBox.getChildren().add(savingsLabel);
		savingsBox.getChildren().add(cuSavings);

		return (savingsBox);
	}

	private HBox buildMembersBox()
	{
		Text membersLabel = new Text("Members: ");
		membersLabel.setFont(Font.font("Serif", 14));
		membersLabel.setFill(Color.BLACK);

		Text cuMembers = new Text(aCreditUnion.getMembers());
		cuMembers.setFont(Font.font("Serif", 14));
		cuMembers.setFill(Color.BLUE);

		HBox membersBox = new HBox();
		membersBox.getChildren().add(membersLabel);
		membersBox.getChildren().add(cuMembers);

		return (membersBox);
	}

	private HBox buildNetWorthBox()
	{
		Text netWorthLabel = new Text("Net Worth Ratio: ");
		netWorthLabel.setFont(Font.font("Serif", 14));
		netWorthLabel.setFill(Color.BLACK);

		Text cuNetWorth = new Text(aCreditUnion.getPCANWRatio());
		cuNetWorth.setFont(Font.font("Serif", 14));
		cuNetWorth.setFill(Color.BLUE);

		HBox networthBox = new HBox();
		networthBox.getChildren().add(netWorthLabel);
		networthBox.getChildren().add(cuNetWorth);

		return (networthBox);
	}

	private HBox buildPCAClassificationBox()
	{
		Text pcaClassificationLabel = new Text("PCA NW Classification: ");
		pcaClassificationLabel.setFont(Font.font("Serif", 14));
		pcaClassificationLabel.setFill(Color.BLACK);

		Text cuPCAClassification= new Text(aCreditUnion.getPCAClassification());
		cuPCAClassification.setFont(Font.font("Serif", 14));
		cuPCAClassification.setFill(Color.BLUE);

		HBox pcaclassificationBox = new HBox();
		pcaclassificationBox.getChildren().add(pcaClassificationLabel);
		pcaclassificationBox.getChildren().add(cuPCAClassification);

		return (pcaclassificationBox);
	}

	private VBox buildFinancialsBox(Stage primaryStage)
	{
		HBox finDateBox = buildFinancialsDateBox(primaryStage);
		HBox assetsBox = buildAssetsBox();
		HBox loansBox = buildLoansBox();
		HBox savingsBox = buildSavingsBox();
		HBox membersBox = buildMembersBox();
		HBox networthBox = buildNetWorthBox();
		HBox pcaclassificationBox = buildPCAClassificationBox();

		VBox financialsBox = new VBox();
		financialsBox.getChildren().add(finDateBox);
		financialsBox.getChildren().add(assetsBox);
		financialsBox.getChildren().add(loansBox);
		financialsBox.getChildren().add(savingsBox);
		financialsBox.getChildren().add(membersBox);
		financialsBox.getChildren().add(networthBox);
		financialsBox.getChildren().add(pcaclassificationBox);
		financialsBox.setPadding(new Insets(10));
		financialsBox.setSpacing(5);

		return (financialsBox);
	}

	private VBox buildMainScreenLeftColumn(Stage primaryStage)
	{
		VBox contactBox = buildContactBox();
		VBox demographicBox = buildDemographicsBox();
		VBox financialsBox = buildFinancialsBox(primaryStage);

		VBox mainScreenLeftCol = new VBox();
		mainScreenLeftCol.getChildren().add(contactBox);
		mainScreenLeftCol.getChildren().add(demographicBox);
		mainScreenLeftCol.getChildren().add(financialsBox);

		return (mainScreenLeftCol);
	}

	private HBox buildStatusBox()
	{
		Text statusLabel = new Text("Status: ");
		statusLabel.setFont(Font.font("Serif", 14));
		statusLabel.setFill(Color.BLACK);

		Text cuStatus = new Text(aCreditUnion.getStatus());
		cuStatus.setFont(Font.font("Serif", 14));
		cuStatus.setFill(Color.BLUE);

		HBox statusBox = new HBox();
		statusBox.getChildren().add(statusLabel);
		statusBox.getChildren().add(cuStatus);
		statusBox.setAlignment(Pos.CENTER_RIGHT);

		return (statusBox);
	}

	private HBox buildCUNAAflBox()
	{
		Text cunaAFLLabel = new Text("CUNA Affiliated: ");
		cunaAFLLabel.setFont(Font.font("Serif", 14));
		cunaAFLLabel.setFill(Color.BLACK);

		Text cuCunaAFL = new Text(aCreditUnion.getCunaAffiliation());
		cuCunaAFL.setFont(Font.font("Serif", 14));
		cuCunaAFL.setFill(Color.BLUE);

		HBox cunaAFLBox = new HBox();
		cunaAFLBox.getChildren().add(cunaAFLLabel);
		cunaAFLBox.getChildren().add(cuCunaAFL);
		cunaAFLBox.setAlignment(Pos.CENTER_RIGHT);

		return (cunaAFLBox);
	}

	private HBox buildLgAflBox()
	{
		Text lgAFLLabel = new Text("League Affiliated: ");
		lgAFLLabel.setFont(Font.font("Serif", 14));
		lgAFLLabel.setFill(Color.BLACK);

		Text cuLgAFL = new Text(aCreditUnion.getLeagueAffiliation());
		cuLgAFL.setFont(Font.font("Serif", 14));
		cuLgAFL.setFill(Color.BLUE);

		HBox lgAFLBox = new HBox();
		lgAFLBox.getChildren().add(lgAFLLabel);
		lgAFLBox.getChildren().add(cuLgAFL);
		lgAFLBox.setAlignment(Pos.CENTER_RIGHT);

		return (lgAFLBox);
	}

	private HBox buildAflLgBox()
	{
		Text aflLgLabel = new Text("League: ");
		aflLgLabel.setFont(Font.font("Serif", 14));
		aflLgLabel.setFill(Color.BLACK);

		Text cuAflLg = new Text(aCreditUnion.getLeagueCode());
		cuAflLg.setFont(Font.font("Serif", 14));
		cuAflLg.setFill(Color.BLUE);

		HBox aflLgBox = new HBox();
		aflLgBox.getChildren().add(aflLgLabel);
		aflLgBox.getChildren().add(cuAflLg);
		aflLgBox.setAlignment(Pos.CENTER_RIGHT);

		return (aflLgBox);
	}

	private HBox buildAbaNumberBox()
	{
		Text abaLabel = new Text("Routing & Transit: ");
		abaLabel.setFont(Font.font("Serif", 14));
		abaLabel.setFill(Color.BLACK);

		Text cuAbaNum = new Text(aCreditUnion.getABAnumber());
		cuAbaNum.setFont(Font.font("Serif", 14));
		cuAbaNum.setFill(Color.BLUE);

		HBox abaNumBox = new HBox();
		abaNumBox.getChildren().add(abaLabel);
		abaNumBox.getChildren().add(cuAbaNum);
		abaNumBox.setAlignment(Pos.CENTER_RIGHT);

		return (abaNumBox);
	}

	private HBox buildCongressionalDistrictBox()
	{
		Text congLabel = new Text("Congressional District (HQ): ");
		congLabel.setFont(Font.font("Serif", 14));
		congLabel.setFill(Color.BLACK);

		Text cuCong = new Text(aCreditUnion.getStreetState() + "-" + aCreditUnion.getStreetCongDistrict());
		cuCong.setFont(Font.font("Serif", 14));
		cuCong.setFill(Color.BLUE);

		HBox congBox = new HBox();
		congBox.getChildren().add(congLabel);
		congBox.getChildren().add(cuCong);
		congBox.setAlignment(Pos.CENTER_RIGHT);

		return (congBox);
	}

	private HBox buildCustomerIDBox()
	{
		Text custIDLabel = new Text("CUNA Customer ID: ");
		custIDLabel.setFont(Font.font("Serif", 14));
		custIDLabel.setFill(Color.BLACK);

		Text cuCustID = new Text(aCreditUnion.getCustomerID());
		cuCustID.setFont(Font.font("Serif", 14));
		cuCustID.setFill(Color.BLUE);

		HBox custIDBox = new HBox();
		custIDBox.getChildren().add(custIDLabel);
		custIDBox.getChildren().add(cuCustID);
		custIDBox.setAlignment(Pos.CENTER_RIGHT);

		return (custIDBox);
	}


	private HBox buildCharterTypeBox()
	{
		Text charterTypeLabel = new Text("Charter Type: ");
		charterTypeLabel.setFont(Font.font("Serif", 14));
		charterTypeLabel.setFill(Color.BLACK);

		Text cuCharterType = new Text(aCreditUnion.getCharterType());
		cuCharterType.setFont(Font.font("Serif", 14));
		cuCharterType.setFill(Color.BLUE);

		HBox charterTypeBox = new HBox();
		charterTypeBox.getChildren().add(charterTypeLabel);
		charterTypeBox.getChildren().add(cuCharterType);
		charterTypeBox.setAlignment(Pos.CENTER_RIGHT);

		return (charterTypeBox);
	}

	private VBox buildStatusesBox()
	{
		HBox statusBox = this.buildStatusBox();
		HBox cunaAFLBox = this.buildCUNAAflBox();
		HBox lgAFLBox = this.buildLgAflBox();
		HBox aflLgBox = this.buildAflLgBox();
		HBox abaNumBox = this.buildAbaNumberBox();
		HBox congBox = this.buildCongressionalDistrictBox();
		HBox custIDBox = this.buildCustomerIDBox();
		HBox charterTypeBox = this.buildCharterTypeBox();

		VBox statusesBox = new VBox();
		statusesBox.getChildren().add(statusBox);
		statusesBox.getChildren().add(cunaAFLBox);
		statusesBox.getChildren().add(lgAFLBox);
		statusesBox.getChildren().add(aflLgBox);
		statusesBox.getChildren().add(abaNumBox);
		statusesBox.getChildren().add(congBox);
		statusesBox.getChildren().add(custIDBox);
		statusesBox.getChildren().add(charterTypeBox);
		statusesBox.setPadding(new Insets(10));
		statusesBox.setSpacing(5);

		return (statusesBox);
	}

	private VBox buildServiceOfferingsBox()
	{
		Text offerShareDraftsLabel = new Text("Offer Share Drafts: ");
		offerShareDraftsLabel.setFont(Font.font("Serif", 14));
		offerShareDraftsLabel.setFill(Color.BLACK);

		Text cuOfferShareDrafts = new Text(aCreditUnion.offerShareDrafts());
		cuOfferShareDrafts.setFont(Font.font("Serif", 14));
		cuOfferShareDrafts.setFill(Color.BLUE);

		HBox offerShareDraftsBox = new HBox();
		offerShareDraftsBox.getChildren().add(offerShareDraftsLabel);
		offerShareDraftsBox.getChildren().add(cuOfferShareDrafts);
		offerShareDraftsBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerMBLsLabel = new Text("Offer Business Loans: ");
		offerMBLsLabel.setFont(Font.font("Serif", 14));
		offerMBLsLabel.setFill(Color.BLACK);

		Text cuOfferMBLs = new Text(aCreditUnion.offerMBLs());
		cuOfferMBLs.setFont(Font.font("Serif", 14));
		cuOfferMBLs.setFill(Color.BLUE);

		HBox offerMBLsBox = new HBox();
		offerMBLsBox.getChildren().add(offerMBLsLabel);
		offerMBLsBox.getChildren().add(cuOfferMBLs);
		offerMBLsBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerCCsLabel = new Text("Offer Credit Cards: ");
		offerCCsLabel.setFont(Font.font("Serif", 14));
		offerCCsLabel.setFill(Color.BLACK);

		Text cuOfferCCs = new Text(aCreditUnion.offerCreditCards());
		cuOfferCCs.setFont(Font.font("Serif", 14));
		cuOfferCCs.setFill(Color.BLUE);

		HBox offerCCsBox = new HBox();
		offerCCsBox.getChildren().add(offerCCsLabel);
		offerCCsBox.getChildren().add(cuOfferCCs);
		offerCCsBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerMort1Label = new Text("Offer 1st Mortgages: ");
		offerMort1Label.setFont(Font.font("Serif", 14));
		offerMort1Label.setFill(Color.BLACK);

		Text cuOfferMort1 = new Text(aCreditUnion.offerFirstMortgages());
		cuOfferMort1.setFont(Font.font("Serif", 14));
		cuOfferMort1.setFill(Color.BLUE);

		HBox offerMort1Box = new HBox();
		offerMort1Box.getChildren().add(offerMort1Label);
		offerMort1Box.getChildren().add(cuOfferMort1);
		offerMort1Box.setAlignment(Pos.CENTER_RIGHT);

		Text offerDebitLabel = new Text("Offer ATM/Debit Card Program: ");
		offerDebitLabel.setFont(Font.font("Serif", 14));
		offerDebitLabel.setFill(Color.BLACK);

		Text cuOfferDebit = new Text(aCreditUnion.offerAtmOrDebit());
		cuOfferDebit.setFont(Font.font("Serif", 14));
		cuOfferDebit.setFill(Color.BLUE);

		HBox offerDebitBox = new HBox();
		offerDebitBox.getChildren().add(offerDebitLabel);
		offerDebitBox.getChildren().add(cuOfferDebit);
		offerDebitBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerBiligualLabel = new Text("Offer Biligual Services: ");
		offerBiligualLabel.setFont(Font.font("Serif", 14));
		offerBiligualLabel.setFill(Color.BLACK);

		Text cuOfferBiligual = new Text(aCreditUnion.offerBilingualServices());
		cuOfferBiligual.setFont(Font.font("Serif", 14));
		cuOfferBiligual.setFill(Color.BLUE);

		HBox offerBiligualBox = new HBox();
		offerBiligualBox.getChildren().add(offerBiligualLabel);
		offerBiligualBox.getChildren().add(cuOfferBiligual);
		offerBiligualBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerRiskBasedLoansLabel = new Text("Offer Risk Based Loans: ");
		offerRiskBasedLoansLabel.setFont(Font.font("Serif", 14));
		offerRiskBasedLoansLabel.setFill(Color.BLACK);

		Text cuOfferRiskBasedLoans = new Text(aCreditUnion.offerRiskBasedLoans());
		cuOfferRiskBasedLoans.setFont(Font.font("Serif", 14));
		cuOfferRiskBasedLoans.setFill(Color.BLUE);

		HBox offerRiskBasedLoansBox = new HBox();
		offerRiskBasedLoansBox.getChildren().add(offerRiskBasedLoansLabel);
		offerRiskBasedLoansBox.getChildren().add(cuOfferRiskBasedLoans);
		offerRiskBasedLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerPaydayLoansLabel = new Text("Offer Payday Loans: ");
		offerPaydayLoansLabel.setFont(Font.font("Serif", 14));
		offerPaydayLoansLabel.setFill(Color.BLACK);

		Text cuOfferPaydayLoans = new Text(aCreditUnion.offerPaydayLoans());
		cuOfferPaydayLoans.setFont(Font.font("Serif", 14));
		cuOfferPaydayLoans.setFill(Color.BLUE);

		HBox offerPaydayLoansBox = new HBox();
		offerPaydayLoansBox.getChildren().add(offerPaydayLoansLabel);
		offerPaydayLoansBox.getChildren().add(cuOfferPaydayLoans);
		offerPaydayLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text offerPaydayAltLoansLabel = new Text("Offer PAL Loans (FCU Only): ");
		offerPaydayAltLoansLabel.setFont(Font.font("Serif", 14));
		offerPaydayAltLoansLabel.setFill(Color.BLACK);

		Text cuOfferPaydayAltLoans = new Text(aCreditUnion.offerPALLoans());
		cuOfferPaydayAltLoans.setFont(Font.font("Serif", 14));
		cuOfferPaydayAltLoans.setFill(Color.BLUE);

		HBox offerPaydayAltLoansBox = new HBox();
		offerPaydayAltLoansBox.getChildren().add(offerPaydayAltLoansLabel);
		offerPaydayAltLoansBox.getChildren().add(cuOfferPaydayAltLoans);
		offerPaydayAltLoansBox.setAlignment(Pos.CENTER_RIGHT);

		VBox servicesBox = new VBox();
		servicesBox.getChildren().add(offerDebitBox);
		servicesBox.getChildren().add(offerBiligualBox);
		servicesBox.getChildren().add(offerShareDraftsBox);
		servicesBox.getChildren().add(offerCCsBox);
		servicesBox.getChildren().add(offerMort1Box);
		servicesBox.getChildren().add(offerMBLsBox);
		servicesBox.getChildren().add(offerRiskBasedLoansBox);
		servicesBox.getChildren().add(offerPaydayLoansBox);
		servicesBox.getChildren().add(offerPaydayAltLoansBox);
		servicesBox.setPadding(new Insets(10));
		servicesBox.setSpacing(5);

		return (servicesBox);
	}

	private VBox buildMainScreenRightColumn()
	{
		VBox statusesBox = buildStatusesBox();
		VBox servicesBox = this.buildServiceOfferingsBox();

		VBox mainScreenRightCol = new VBox();
		mainScreenRightCol.getChildren().add(statusesBox);
		mainScreenRightCol.getChildren().add(servicesBox);

		return (mainScreenRightCol);
	}

	private VBox buildMergeeBox(String cuid, Stage primaryStage)
	{
		CreditUnion theCU = new CreditUnion(dbConn, cuid, financialsPeriod);

		Text mergeDateLabel = new Text("Merged: ");
		mergeDateLabel.setFont(Font.font("Serif", 14));
		mergeDateLabel.setFill(Color.BLACK);

		Text mergerDate = new Text(theCU.getStatusChangeDate());
		mergerDate.setFont(Font.font("Serif", 14));
		mergerDate.setFill(Color.BLUE);

		HBox mergerDateBox = new HBox();
		mergerDateBox.getChildren().add(mergeDateLabel);
		mergerDateBox.getChildren().add(mergerDate);
		mergerDateBox.setPadding(new Insets(5));
		mergerDateBox.setAlignment(Pos.CENTER);

		Text cuidLabel = new Text("CUID: ");
		cuidLabel.setFont(Font.font("Serif", 14));
		cuidLabel.setFill(Color.BLACK);

		Hyperlink cuidLnk = new Hyperlink(cuid);
		cuidLnk.setFont(Font.font("Serif", 14));
		cuidLnk.setTextFill(Color.BLUE);
		cuidLnk.setBorder(null);

		cuidLnk.setOnAction(new EventHandler<ActionEvent>() 
		{   
			public void handle(ActionEvent e) 
			{        
				cuidBatch = Utils.search(dbConn, cuid, Boolean.TRUE);

				index = 0;
				setCreditUnion(cuidBatch[index]);
				setScreen(CU_Info.mainScreen);	
				start(primaryStage);
			}
		});

		HBox cuidBox = new HBox();
		cuidBox.getChildren().add(cuidLabel);
		cuidBox.getChildren().add(cuidLnk);
		cuidBox.setPadding(new Insets(5));
		cuidBox.setAlignment(Pos.CENTER);

		Text cuName = new Text(theCU.getName());
		cuName.setFont(Font.font("Serif", 14));
		cuName.setFill(Color.BLUE);

		Text cuStCity = new Text(theCU.getStreetCity() + ", " + theCU.getStreetState());
		cuStCity.setFont(Font.font("Serif", 14));
		cuStCity.setFill(Color.BLUE);

		VBox nameBox = new VBox();
		nameBox.getChildren().add(cuName);
		nameBox.getChildren().add(cuStCity);
		nameBox.setPadding(new Insets(5));
		nameBox.setAlignment(Pos.CENTER);

		Text assetsLabel = new Text("Assets: ");
		assetsLabel.setFont(Font.font("Serif", 14));
		assetsLabel.setFill(Color.BLACK);

		Text assets = new Text(Long.toString(theCU.getLastReportedAssets()));
		assets.setFont(Font.font("Serif", 14));
		assets.setFill(Color.BLUE);

		HBox assetsBox = new HBox();
		assetsBox.getChildren().add(assetsLabel);
		assetsBox.getChildren().add(assets);
		assetsBox.setPadding(new Insets(5));
		assetsBox.setAlignment(Pos.CENTER);

		Text membersLabel = new Text("Members: ");
		membersLabel.setFont(Font.font("Serif", 14));
		membersLabel.setFill(Color.BLACK);

		Text members = new Text(Long.toString(theCU.getLastReportedMembers()));
		members.setFont(Font.font("Serif", 14));
		members.setFill(Color.BLUE);

		HBox membersBox = new HBox();
		membersBox.getChildren().add(membersLabel);
		membersBox.getChildren().add(members);
		membersBox.setPadding(new Insets(5));
		membersBox.setAlignment(Pos.CENTER);

		Text spacer = new Text(" -------------------- ");
		spacer.setFont(Font.font("Serif", 14));
		spacer.setFill(Color.BLUE);

		VBox spacerBox = new VBox();
		spacerBox.getChildren().add(spacer);
		spacerBox.setPadding(new Insets(5));
		spacerBox.setAlignment(Pos.CENTER);

		VBox mergeeBox = new VBox();
		mergeeBox.getChildren().add(mergerDateBox); 
		mergeeBox.getChildren().add(cuidBox);
		mergeeBox.getChildren().add(nameBox);
		//mergeeBox.getChildren().add(assetsBox);
		//mergeeBox.getChildren().add(membersBox);
		mergeeBox.getChildren().add(spacerBox);

		return (mergeeBox);
	}

	private VBox buildAllMergersBox(Stage primaryStage)
	{
		String[] mergees = aCreditUnion.getMergees();

		HBox mergeesBox = new HBox();

		Text header = new Text(" All Recorded Mergers for " + aCreditUnion.getName());
		if(mergees.length == 0)
		{
			header = new Text(" No Mergers On Record for " + aCreditUnion.getName());
		}

		header.setFont(Font.font("Serif", 16));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" -------------------------------------------------- ");
		spacer.setFont(Font.font("Serif", 14));
		spacer.setFill(Color.BLUE);

		Text spacer2 = new Text(" -------------------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 14));
		spacer2.setFill(Color.BLUE);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		mergeesBox.getChildren().add(headerBox);

		if(mergees.length%9 == 0)
		{
			numberOfMergerScreens = mergees.length / 9;
		}
		else
		{
			numberOfMergerScreens = (mergees.length / 9) + 1;
		}

		int offset = 9 * (mergerScreenNumber-1);

		VBox col = new VBox();
		for(int i = offset; i < mergees.length & i < 9+offset; i++)
		{	
			if(i%3 == 0)
			{	
				if(i != 0)
				{
					mergeesBox.getChildren().add(col);
				}
				col = new VBox();
				col.setAlignment(Pos.TOP_CENTER);
			}
			col.getChildren().add(buildMergeeBox(mergees[i], primaryStage));
		}
		mergeesBox.getChildren().add(col);
		mergeesBox.setAlignment(Pos.CENTER);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(headerBox);
		wHeader.getChildren().add(mergeesBox);

		if(numberOfMergerScreens > 1)
		{
			HBox mergerNavButtons = new HBox();

			if(mergerScreenNumber != 1)
			{
				Button previous = new Button("Prev");
				previous.setOnAction((event) -> 
				{
					mergerScreenNumber--;
					start(primaryStage);
				});

				mergerNavButtons.getChildren().addAll(previous);
			}

			if(mergerScreenNumber < numberOfMergerScreens)
			{
				Button next = new Button("Next");
				next.setOnAction((event) -> 
				{
					mergerScreenNumber++;
					start(primaryStage);
				});

				mergerNavButtons.getChildren().addAll(next);
			}

			mergerNavButtons.setSpacing(5);
			mergerNavButtons.setPadding(new Insets(10)); 
			mergerNavButtons.setAlignment(Pos.CENTER);

			wHeader.getChildren().add(mergerNavButtons);
		}

		return (wHeader);
	}

	private VBox buildCashAndInvestmentsBox(Stage primaryStage)
	{
		VBox cashAndInvsBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		HBox cashAndInvsDataBox = new HBox();
		cashAndInvsDataBox.getChildren().add(buildCashAndInvestmentsLabelBox(primaryStage));
		cashAndInvsDataBox.getChildren().add(buildCashAndInvestmentsCurrentPeriodBox(primaryStage));
		cashAndInvsDataBox.getChildren().add(buildCashAndInvestmentsPriorPeriodBox(primaryStage));
		cashAndInvsDataBox.getChildren().add(buildCashAndInvestmentsPCTChangeBox(primaryStage));

		Button next = new Button("-->");
		next.setOnAction((event) -> 
		{
			balanceSheetScreenNumber++;
			start(primaryStage);
		});

		VBox nextBox = new VBox();
		nextBox.setSpacing(5);
		nextBox.setPadding(new Insets(15)); 
		nextBox.setAlignment(Pos.CENTER_RIGHT);
		nextBox.getChildren().add(next);

		cashAndInvsDataBox.getChildren().add(nextBox);

		cashAndInvsBox.getChildren().add(headerBox);
		cashAndInvsBox.getChildren().add(cashAndInvsDataBox);
		cashAndInvsBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (cashAndInvsBox);
	}

	private VBox buildLoansBox(Stage primaryStage)
	{
		VBox loansBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		Button prev = new Button("<--");
		prev.setOnAction((event) -> 
		{
			balanceSheetScreenNumber--;
			start(primaryStage);
		});

		VBox prevBox = new VBox();
		prevBox.setSpacing(5);
		prevBox.setPadding(new Insets(15)); 
		prevBox.setAlignment(Pos.CENTER_RIGHT);
		prevBox.getChildren().add(prev);

		HBox LoansDataBox = new HBox();
		LoansDataBox.getChildren().add(prevBox);
		LoansDataBox.getChildren().add(buildLoansLabelBox(primaryStage));
		LoansDataBox.getChildren().add(buildLoansCurrentPeriodBox(primaryStage));
		LoansDataBox.getChildren().add(buildLoansPriorPeriodBox(primaryStage));
		LoansDataBox.getChildren().add(buildLoansPCTChangeBox(primaryStage));

		Button next = new Button("-->");
		next.setOnAction((event) -> 
		{
			balanceSheetScreenNumber++;
			start(primaryStage);
		});

		VBox nextBox = new VBox();
		nextBox.setSpacing(5);
		nextBox.setPadding(new Insets(15)); 
		nextBox.setAlignment(Pos.CENTER_RIGHT);
		nextBox.getChildren().add(next);

		LoansDataBox.getChildren().add(nextBox);

		loansBox.getChildren().add(headerBox);
		loansBox.getChildren().add(LoansDataBox);
		loansBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (loansBox);
	}

	private VBox buildDuesBox(Stage primaryStage)
	{
		VBox advancedSearchBox = new VBox();

		Text header = new Text("1CUNA - Dues");

		header.setFont(Font.font("Serif", 15));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		HBox advancedSearchDataBox = new HBox();
		advancedSearchDataBox.getChildren().add(buildDuesLabelBox(primaryStage));
		advancedSearchDataBox.getChildren().add(buildDuesEstCurrBox(primaryStage));
		advancedSearchDataBox.getChildren().add(buildDuesEstTMinus1Box(primaryStage));
		advancedSearchDataBox.getChildren().add(buildDuesEstTMinus2Box(primaryStage));

		
		
		
		advancedSearchDataBox.setAlignment(Pos.CENTER);

		advancedSearchBox.getChildren().add(headerBox);
		advancedSearchBox.getChildren().add(advancedSearchDataBox);
		
		advancedSearchBox.setAlignment(Pos.TOP_CENTER);
		
		return (advancedSearchBox);
	}
	
	private VBox buildAdvancedSearchBox(Stage primaryStage)
	{
		VBox advancedSearchBox = new VBox();

		Text header = new Text("Advanced Credit Union Search");

		header.setFont(Font.font("Serif", 15));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		HBox advancedSearchDataBox = new HBox();
		advancedSearchDataBox.getChildren().add(buildAdvancedSearchLabelBox(primaryStage));
		advancedSearchDataBox.getChildren().add(buildAdvancedSearchEntryBox(primaryStage));

		advancedSearchDataBox.setAlignment(Pos.CENTER);

		advancedSearchBox.getChildren().add(headerBox);
		advancedSearchBox.getChildren().add(advancedSearchDataBox);
		
		advancedSearchBox.setAlignment(Pos.TOP_CENTER);
		
		return (advancedSearchBox);
	}

	private VBox buildDemographicsBox(Stage primaryStage)
	{
		VBox demographicsBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		HBox demographicsDataBox = new HBox();
		demographicsDataBox.getChildren().add(buildDemographicsLabelBox(primaryStage));
		demographicsDataBox.getChildren().add(buildDemographicsCurrentPeriodBox(primaryStage));
		demographicsDataBox.getChildren().add(buildDemographicsPriorPeriodBox(primaryStage));
		demographicsDataBox.getChildren().add(buildDemographicsPCTChangeBox(primaryStage));

		demographicsBox.getChildren().add(headerBox);
		demographicsBox.getChildren().add(demographicsDataBox);
		demographicsBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (demographicsBox);
	}

	private VBox buildKeyRatiosBox(Stage primaryStage)
	{
		VBox keyRatiosBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		HBox keyRatiosDataBox = new HBox();
		keyRatiosDataBox.getChildren().add(buildKeyRatiosLabelBox(primaryStage));
		keyRatiosDataBox.getChildren().add(buildKeyRatiosCurrentPeriodBox(primaryStage));
		keyRatiosDataBox.getChildren().add(buildKeyRatiosPriorPeriodBox(primaryStage));

		keyRatiosBox.getChildren().add(headerBox);
		keyRatiosBox.getChildren().add(keyRatiosDataBox);
		keyRatiosBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (keyRatiosBox);
	}

	private VBox buildAssetQualityBox(Stage primaryStage)
	{
		VBox assetQualityBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		HBox assetQualityDataBox = new HBox();
		assetQualityDataBox.getChildren().add(buildAssetQualityLabelBox(primaryStage));
		assetQualityDataBox.getChildren().add(buildAssetQualityCurrentPeriodBox(primaryStage));
		assetQualityDataBox.getChildren().add(buildAssetQualityPriorPeriodBox(primaryStage));
		assetQualityDataBox.getChildren().add(buildAssetQualityPCTChangeBox(primaryStage));

		assetQualityBox.getChildren().add(headerBox);
		assetQualityBox.getChildren().add(assetQualityDataBox);
		assetQualityBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (assetQualityBox);
	}

	private VBox buildIncomeStatementPage1Box(Stage primaryStage)
	{
		VBox incomeStatementPage1Box = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		Button next = new Button("-->");
		next.setOnAction((event) -> 
		{
			incomeStatementScreenNumber++;
			start(primaryStage);
		});

		VBox nextBox = new VBox();
		nextBox.setSpacing(5);
		nextBox.setPadding(new Insets(15)); 
		nextBox.setAlignment(Pos.CENTER_RIGHT);
		nextBox.getChildren().add(next);

		HBox incomeStatementPage1DataBox = new HBox();
		incomeStatementPage1DataBox.getChildren().add(buildIncomeStatementPage1LabelBox(primaryStage));
		incomeStatementPage1DataBox.getChildren().add(buildIncomeStatementPage1CurrentPeriodBox(primaryStage));
		incomeStatementPage1DataBox.getChildren().add(buildIncomeStatementPage1PriorPeriodBox(primaryStage));
		incomeStatementPage1DataBox.getChildren().add(buildIncomeStatementPage1PCTChangeBox(primaryStage));
		incomeStatementPage1DataBox.getChildren().add(nextBox);

		incomeStatementPage1Box.getChildren().add(headerBox);
		incomeStatementPage1Box.getChildren().add(incomeStatementPage1DataBox);
		incomeStatementPage1Box.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (incomeStatementPage1Box);
	}

	private VBox buildIncomeStatementPage2Box(Stage primaryStage)
	{
		VBox incomeStatementPage2Box = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		Button prev = new Button("<--");
		prev.setOnAction((event) -> 
		{
			incomeStatementScreenNumber--;
			start(primaryStage);
		});

		VBox prevBox = new VBox();
		prevBox.setSpacing(5);
		prevBox.setPadding(new Insets(15)); 
		prevBox.setAlignment(Pos.CENTER_RIGHT);
		prevBox.getChildren().add(prev);

		HBox incomeStatementPage2DataBox = new HBox();
		incomeStatementPage2DataBox.getChildren().add(prevBox);
		incomeStatementPage2DataBox.getChildren().add(buildIncomeStatementPage2LabelBox(primaryStage));
		incomeStatementPage2DataBox.getChildren().add(buildIncomeStatementPage2CurrentPeriodBox(primaryStage));
		incomeStatementPage2DataBox.getChildren().add(buildIncomeStatementPage2PriorPeriodBox(primaryStage));
		incomeStatementPage2DataBox.getChildren().add(buildIncomeStatementPage2PCTChangeBox(primaryStage));

		incomeStatementPage2Box.getChildren().add(headerBox);
		incomeStatementPage2Box.getChildren().add(incomeStatementPage2DataBox);
		incomeStatementPage2Box.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (incomeStatementPage2Box);
	}

	private VBox buildLiabilitiesAndCapitalBox(Stage primaryStage)
	{
		VBox liabilitiesAndCapitalBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		Button prev = new Button("<--");
		prev.setOnAction((event) -> 
		{
			balanceSheetScreenNumber--;
			start(primaryStage);
		});

		VBox prevBox = new VBox();
		prevBox.setSpacing(5);
		prevBox.setPadding(new Insets(15)); 
		prevBox.setAlignment(Pos.CENTER_RIGHT);
		prevBox.getChildren().add(prev);

		HBox liabilitiesAndCapitalDataBox = new HBox();
		liabilitiesAndCapitalDataBox.getChildren().add(prevBox);
		liabilitiesAndCapitalDataBox.getChildren().add(buildLiabilitiesAndCapitalLabelBox(primaryStage));
		liabilitiesAndCapitalDataBox.getChildren().add(buildLiabilitiesAndCapitalCurrentPeriodBox(primaryStage));
		liabilitiesAndCapitalDataBox.getChildren().add(buildLiabilitiesAndCapitalPriorPeriodBox(primaryStage));
		liabilitiesAndCapitalDataBox.getChildren().add(buildLiabilitiesAndCapitalPCTChangeBox(primaryStage));


		liabilitiesAndCapitalBox.getChildren().add(headerBox);
		liabilitiesAndCapitalBox.getChildren().add(liabilitiesAndCapitalDataBox);
		liabilitiesAndCapitalBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (liabilitiesAndCapitalBox);
	}

	private VBox buildOtherAssetsBox(Stage primaryStage)
	{
		VBox otherAssetsBox = new VBox();

		twoYrFinSum = new TwoYearFinancialSummary(dbConn, aCreditUnion.getID(), CU_Info.financialsPeriod);

		Text header = new Text("Two Year Financial Comparison for: " + aCreditUnion.getName());
		Text asOf = new Text("All Data As Of " + Utils.convertToLongPeriodNoYear(aCreditUnion.getFinancialsPeriod()));

		header.setFont(Font.font("Serif", 14));
		header.setFill(Color.BLACK);

		Text spacer = new Text(" ------------------------------------------------------------ ");
		spacer.setFont(Font.font("Serif", 12));
		spacer.setFill(Color.BLACK);

		Text spacer2 = new Text(" ------------------------------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox headerBox = new VBox();
		headerBox.getChildren().add(spacer);
		headerBox.getChildren().add(header);
		headerBox.getChildren().add(asOf);
		headerBox.getChildren().add(spacer2);
		headerBox.setPadding(new Insets(10));
		headerBox.setAlignment(Pos.CENTER);

		Button prev = new Button("<--");
		prev.setOnAction((event) -> 
		{
			balanceSheetScreenNumber--;
			start(primaryStage);
		});

		VBox prevBox = new VBox();
		prevBox.setSpacing(5);
		prevBox.setPadding(new Insets(15)); 
		prevBox.setAlignment(Pos.CENTER_RIGHT);
		prevBox.getChildren().add(prev);

		HBox otherAssetsDataBox = new HBox();
		otherAssetsDataBox.getChildren().add(prevBox);
		otherAssetsDataBox.getChildren().add(buildOtherAssetsLabelBox(primaryStage));
		otherAssetsDataBox.getChildren().add(buildOtherAssetsCurrentPeriodBox(primaryStage));
		otherAssetsDataBox.getChildren().add(buildOtherAssetsPriorPeriodBox(primaryStage));
		otherAssetsDataBox.getChildren().add(buildOtherAssetsPCTChangeBox(primaryStage));

		Button next = new Button("-->");
		next.setOnAction((event) -> 
		{
			balanceSheetScreenNumber++;
			start(primaryStage);
		});

		VBox nextBox = new VBox();
		nextBox.setSpacing(5);
		nextBox.setPadding(new Insets(15)); 
		nextBox.setAlignment(Pos.CENTER_RIGHT);
		nextBox.getChildren().add(next);

		otherAssetsDataBox.getChildren().add(nextBox);

		otherAssetsBox.getChildren().add(headerBox);
		otherAssetsBox.getChildren().add(otherAssetsDataBox);
		otherAssetsBox.getChildren().add(buildFinancialsButtonBox(primaryStage));

		return (otherAssetsBox);
	}
	
	private VBox buildAdvancedSearchEntryBox(Stage primaryStage)
	{
		TextField nameField = new TextField();
		nameField.setPromptText("");
		nameField.setPrefColumnCount(10);
		nameField.setPadding(new Insets(5));
		nameField.getText();
		
		TextField cityField = new TextField();
		cityField.setPromptText("");
		cityField.setPrefColumnCount(10);
		cityField.setPadding(new Insets(5));
		cityField.getText();

		TextField stateField = new TextField();
		stateField.setPromptText("");
		stateField.setPrefColumnCount(10);
		stateField.setPadding(new Insets(5));
		stateField.getText();

		ComboBox stateComboBox = new ComboBox();
		stateComboBox.getItems().addAll(Utils.states);
		
		TextField zipField = new TextField();
		zipField.setPromptText("");
		zipField.setPrefColumnCount(10);
		zipField.setPadding(new Insets(5));
		zipField.getText();
		
		TextField phoneField = new TextField();
		phoneField.setPromptText("");
		phoneField.setPrefColumnCount(10);
		phoneField.setPadding(new Insets(5));
		phoneField.getText();
		
		TextField webField = new TextField();
		webField.setPromptText("");
		webField.setPrefColumnCount(15);
		webField.setPadding(new Insets(5));
		webField.getText();

		TextField ceoLastField = new TextField();
		ceoLastField.setPromptText("");
		ceoLastField.setPrefColumnCount(10);
		ceoLastField.setPadding(new Insets(5));
		ceoLastField.getText();
		
		Button findButton  = new Button("Search");
		findButton.setAlignment(Pos.CENTER);
		findButton.setPadding(new Insets(5));
		
		CheckBox includeInactives = new CheckBox("Include Inactives");
		//includeInactives.setSelected(inactivesIncluded);
		includeInactives.setPadding(new Insets(5));
		
		ComboBox sortOrderComboBox = new ComboBox();
		sortOrderComboBox.getItems().add("CUID");
		sortOrderComboBox.getItems().add("CU Name");
		sortOrderComboBox.getItems().add("City");
		sortOrderComboBox.getItems().add("State");
		sortOrderComboBox.getItems().add("Zip Code");
		sortOrderComboBox.getItems().add("Phone #");
		sortOrderComboBox.getItems().add("Website");
		sortOrderComboBox.getItems().add("CEO Last Name");
		sortOrderComboBox.setValue("CUID");
		
		findButton.setOnAction((event) -> 
		{
			inactivesIncluded = includeInactives.isSelected();
			
			if(stateComboBox.getValue() != null)
			{
				cuidBatch = Utils.advancedSearch(dbConn, nameField.getText(), cityField.getText(), stateComboBox.getValue().toString(), zipField.getText(), phoneField.getText(), webField.getText(), ceoLastField.getText(), inactivesIncluded, sortOrderComboBox.getValue().toString());
			}
			else
			{
				cuidBatch = Utils.advancedSearch(dbConn, nameField.getText(), cityField.getText(), "", zipField.getText(), phoneField.getText(), webField.getText(), ceoLastField.getText(), inactivesIncluded, sortOrderComboBox.getValue().toString());
			}
			
			index = 0;
			if (cuidBatch.length == 0)
			{
				noneFound = true;
			}
			else
			{	
				noneFound = false;
				setCreditUnion(cuidBatch[index]);
				setScreen(mainScreen);
				resetMergersScreen();
			}
			
			start(primaryStage);
		});
		
		VBox wHeader = new VBox();
		wHeader.getChildren().add(nameField);
		wHeader.getChildren().add(cityField);
		wHeader.getChildren().add(stateComboBox);
		wHeader.getChildren().add(zipField);
		wHeader.getChildren().add(phoneField);
		wHeader.getChildren().add(webField);
		wHeader.getChildren().add(ceoLastField);
		wHeader.getChildren().add(sortOrderComboBox);
		wHeader.getChildren().add(includeInactives);
		wHeader.getChildren().add(findButton);
		
		wHeader.setAlignment(Pos.CENTER);
	

		return (wHeader);
	}

	private VBox buildDuesEstCurrBox(Stage primaryStage)
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);
		
		Dues_Adjustment duesAdjs = new Dues_Adjustment(dbConn, aCreditUnion.getID(), Utils.duesYears[0]);
		
		Text duesLabel = new Text(Utils.duesYears[0]);
		duesLabel.setFont(Font.font("Serif", 14));
		duesLabel.setFill(Color.BLACK);

		HBox duesBox = new HBox();
		duesBox.getChildren().add(duesLabel);
		duesBox.setPadding(new Insets(5));
		duesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);
		
		Text duesEstLabel = new Text(nf.format(Utils.computeDues(dbConn, aCreditUnion.getID(), Utils.duesYears[0])));
		duesEstLabel.setFont(Font.font("Serif", 12));
		duesEstLabel.setFill(Color.BLACK);

		HBox duesEstBox = new HBox();
		duesEstBox.getChildren().add(duesEstLabel);
		duesEstBox.setPadding(new Insets(3));
		duesEstBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer2 = new Text(" ------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);
		
		Text reafl100Label = new Text(duesAdjs.getReAfl100Pct());
		reafl100Label.setFont(Font.font("Serif", 12));
		reafl100Label.setFill(Color.BLACK);

		HBox reafl100Box = new HBox();
		reafl100Box.getChildren().add(reafl100Label);
		reafl100Box.setPadding(new Insets(3));
		reafl100Box.setAlignment(Pos.CENTER_RIGHT);
		
		Text midYrDuesLostLabel = new Text(duesAdjs.getMidYrAflDuesLost());
		midYrDuesLostLabel.setFont(Font.font("Serif", 12));
		midYrDuesLostLabel.setFill(Color.BLACK);

		HBox midYrDuesLostBox = new HBox();
		midYrDuesLostBox.getChildren().add(midYrDuesLostLabel);
		midYrDuesLostBox.setPadding(new Insets(3));
		midYrDuesLostBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text aflInitDuesLostLabel = new Text(duesAdjs.getReAflInitLostDues());
		aflInitDuesLostLabel.setFont(Font.font("Serif", 12));
		aflInitDuesLostLabel.setFill(Color.BLACK);

		HBox aflInitDuesLostBox = new HBox();
		aflInitDuesLostBox.getChildren().add(aflInitDuesLostLabel);
		aflInitDuesLostBox.setPadding(new Insets(3));
		aflInitDuesLostBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text disAflDuesLostLabel = new Text(duesAdjs.getDuesLostFromDisAfls());
		disAflDuesLostLabel.setFont(Font.font("Serif", 12));
		disAflDuesLostLabel.setFill(Color.BLACK);

		HBox disAflDuesLostBox = new HBox();
		disAflDuesLostBox.getChildren().add(disAflDuesLostLabel);
		disAflDuesLostBox.setPadding(new Insets(3));
		disAflDuesLostBox.setAlignment(Pos.CENTER_RIGHT);

		Text duesPaidByDisAflsLabel = new Text(duesAdjs.getDuesPaidByDisAfls());
		duesPaidByDisAflsLabel.setFont(Font.font("Serif", 12));
		duesPaidByDisAflsLabel.setFill(Color.BLACK);

		HBox duesPaidByDisAflsBox = new HBox();
		duesPaidByDisAflsBox.getChildren().add(duesPaidByDisAflsLabel);
		duesPaidByDisAflsBox.setPadding(new Insets(3));
		duesPaidByDisAflsBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text hardshipWaiverLabel = new Text(duesAdjs.getHardshipWaivers());
		hardshipWaiverLabel.setFont(Font.font("Serif", 12));
		hardshipWaiverLabel.setFill(Color.BLACK);

		HBox hardshipWaiverBox = new HBox();
		hardshipWaiverBox.getChildren().add(hardshipWaiverLabel);
		hardshipWaiverBox.setPadding(new Insets(3));
		hardshipWaiverBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text mergerDuesCollectedLabel = new Text(duesAdjs.getDuesCollectedFromMergers());
		mergerDuesCollectedLabel.setFont(Font.font("Serif", 12));
		mergerDuesCollectedLabel.setFill(Color.BLACK);

		HBox mergerDuesCollectedBox = new HBox();
		mergerDuesCollectedBox.getChildren().add(mergerDuesCollectedLabel);
		mergerDuesCollectedBox.setPadding(new Insets(3));
		mergerDuesCollectedBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text duesLostFromInactivesLabel = new Text(duesAdjs.getDuesLostFromInactives());
		duesLostFromInactivesLabel.setFont(Font.font("Serif", 12));
		duesLostFromInactivesLabel.setFill(Color.BLACK);

		HBox duesLostFromInactivesBox = new HBox();
		duesLostFromInactivesBox.getChildren().add(duesLostFromInactivesLabel);
		duesLostFromInactivesBox.setPadding(new Insets(3));
		duesLostFromInactivesBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text otherLabel = new Text(duesAdjs.getDuesLostFromInactives());
		otherLabel.setFont(Font.font("Serif", 12));
		otherLabel.setFill(Color.BLACK);

		HBox otherBox = new HBox();
		otherBox.getChildren().add(otherLabel);
		otherBox.setPadding(new Insets(3));
		otherBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer3 = new Text(" ------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);
		
		Text collectedLabel = new Text(duesAdjs.getCollected());
		collectedLabel.setFont(Font.font("Serif", 12));
		collectedLabel.setFill(Color.BLACK);

		HBox collectedBox = new HBox();
		collectedBox.getChildren().add(collectedLabel);
		collectedBox.setPadding(new Insets(3));
		collectedBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer4 = new Text(" ------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);
		
		VBox wHeader = new VBox();
		wHeader.getChildren().add(duesBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(duesEstBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(reafl100Box);
		wHeader.getChildren().add(midYrDuesLostBox);
		wHeader.getChildren().add(aflInitDuesLostBox);
		wHeader.getChildren().add(disAflDuesLostBox);
		wHeader.getChildren().add(duesPaidByDisAflsBox);
		wHeader.getChildren().add(hardshipWaiverBox);
		wHeader.getChildren().add(mergerDuesCollectedBox);
		wHeader.getChildren().add(duesLostFromInactivesBox);
		wHeader.getChildren().add(otherBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(collectedBox);
		wHeader.getChildren().add(spacer4);
		
		
		wHeader.setAlignment(Pos.CENTER);

		return (wHeader);
	}
	
	private VBox buildDuesEstTMinus1Box(Stage primaryStage)
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);
		
		Dues_Adjustment duesAdjs = new Dues_Adjustment(dbConn, aCreditUnion.getID(), Utils.duesYears[1]);
		
		Text duesLabel = new Text(Utils.duesYears[1]);
		duesLabel.setFont(Font.font("Serif", 14));
		duesLabel.setFill(Color.BLACK);

		HBox duesBox = new HBox();
		duesBox.getChildren().add(duesLabel);
		duesBox.setPadding(new Insets(5));
		duesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);
		
		Text duesEstLabel = new Text(nf.format(Utils.computeDues(dbConn, aCreditUnion.getID(), Utils.duesYears[1])));
		duesEstLabel.setFont(Font.font("Serif", 12));
		duesEstLabel.setFill(Color.BLACK);

		HBox duesEstBox = new HBox();
		duesEstBox.getChildren().add(duesEstLabel);
		duesEstBox.setPadding(new Insets(3));
		duesEstBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer2 = new Text(" ------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);
		
		Text reafl100Label = new Text(duesAdjs.getReAfl100Pct());
		reafl100Label.setFont(Font.font("Serif", 12));
		reafl100Label.setFill(Color.BLACK);

		HBox reafl100Box = new HBox();
		reafl100Box.getChildren().add(reafl100Label);
		reafl100Box.setPadding(new Insets(3));
		reafl100Box.setAlignment(Pos.CENTER_RIGHT);
		
		Text midYrDuesLostLabel = new Text(duesAdjs.getMidYrAflDuesLost());
		midYrDuesLostLabel.setFont(Font.font("Serif", 12));
		midYrDuesLostLabel.setFill(Color.BLACK);

		HBox midYrDuesLostBox = new HBox();
		midYrDuesLostBox.getChildren().add(midYrDuesLostLabel);
		midYrDuesLostBox.setPadding(new Insets(3));
		midYrDuesLostBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text aflInitDuesLostLabel = new Text(duesAdjs.getReAflInitLostDues());
		aflInitDuesLostLabel.setFont(Font.font("Serif", 12));
		aflInitDuesLostLabel.setFill(Color.BLACK);

		HBox aflInitDuesLostBox = new HBox();
		aflInitDuesLostBox.getChildren().add(aflInitDuesLostLabel);
		aflInitDuesLostBox.setPadding(new Insets(3));
		aflInitDuesLostBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text disAflDuesLostLabel = new Text(duesAdjs.getDuesLostFromDisAfls());
		disAflDuesLostLabel.setFont(Font.font("Serif", 12));
		disAflDuesLostLabel.setFill(Color.BLACK);

		HBox disAflDuesLostBox = new HBox();
		disAflDuesLostBox.getChildren().add(disAflDuesLostLabel);
		disAflDuesLostBox.setPadding(new Insets(3));
		disAflDuesLostBox.setAlignment(Pos.CENTER_RIGHT);

		Text duesPaidByDisAflsLabel = new Text(duesAdjs.getDuesPaidByDisAfls());
		duesPaidByDisAflsLabel.setFont(Font.font("Serif", 12));
		duesPaidByDisAflsLabel.setFill(Color.BLACK);

		HBox duesPaidByDisAflsBox = new HBox();
		duesPaidByDisAflsBox.getChildren().add(duesPaidByDisAflsLabel);
		duesPaidByDisAflsBox.setPadding(new Insets(3));
		duesPaidByDisAflsBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text hardshipWaiverLabel = new Text(duesAdjs.getHardshipWaivers());
		hardshipWaiverLabel.setFont(Font.font("Serif", 12));
		hardshipWaiverLabel.setFill(Color.BLACK);

		HBox hardshipWaiverBox = new HBox();
		hardshipWaiverBox.getChildren().add(hardshipWaiverLabel);
		hardshipWaiverBox.setPadding(new Insets(3));
		hardshipWaiverBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text mergerDuesCollectedLabel = new Text(duesAdjs.getDuesCollectedFromMergers());
		mergerDuesCollectedLabel.setFont(Font.font("Serif", 12));
		mergerDuesCollectedLabel.setFill(Color.BLACK);

		HBox mergerDuesCollectedBox = new HBox();
		mergerDuesCollectedBox.getChildren().add(mergerDuesCollectedLabel);
		mergerDuesCollectedBox.setPadding(new Insets(3));
		mergerDuesCollectedBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text duesLostFromInactivesLabel = new Text(duesAdjs.getDuesLostFromInactives());
		duesLostFromInactivesLabel.setFont(Font.font("Serif", 12));
		duesLostFromInactivesLabel.setFill(Color.BLACK);

		HBox duesLostFromInactivesBox = new HBox();
		duesLostFromInactivesBox.getChildren().add(duesLostFromInactivesLabel);
		duesLostFromInactivesBox.setPadding(new Insets(3));
		duesLostFromInactivesBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text otherLabel = new Text(duesAdjs.getDuesLostFromInactives());
		otherLabel.setFont(Font.font("Serif", 12));
		otherLabel.setFill(Color.BLACK);

		HBox otherBox = new HBox();
		otherBox.getChildren().add(otherLabel);
		otherBox.setPadding(new Insets(3));
		otherBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer3 = new Text(" ------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);
		
		Text collectedLabel = new Text(duesAdjs.getCollected());
		collectedLabel.setFont(Font.font("Serif", 12));
		collectedLabel.setFill(Color.BLACK);

		HBox collectedBox = new HBox();
		collectedBox.getChildren().add(collectedLabel);
		collectedBox.setPadding(new Insets(3));
		collectedBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer4 = new Text(" ------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);
		
		VBox wHeader = new VBox();
		wHeader.getChildren().add(duesBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(duesEstBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(reafl100Box);
		wHeader.getChildren().add(midYrDuesLostBox);
		wHeader.getChildren().add(aflInitDuesLostBox);
		wHeader.getChildren().add(disAflDuesLostBox);
		wHeader.getChildren().add(duesPaidByDisAflsBox);
		wHeader.getChildren().add(hardshipWaiverBox);
		wHeader.getChildren().add(mergerDuesCollectedBox);
		wHeader.getChildren().add(duesLostFromInactivesBox);
		wHeader.getChildren().add(otherBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(collectedBox);
		wHeader.getChildren().add(spacer4);

		wHeader.setAlignment(Pos.CENTER);

		return (wHeader);
	}
	
	private VBox buildDuesEstTMinus2Box(Stage primaryStage)
	{
		NumberFormat nf = NumberFormat.getCurrencyInstance(java.util.Locale.US);
		nf.setMaximumFractionDigits(0);
		
		Dues_Adjustment duesAdjs = new Dues_Adjustment(dbConn, aCreditUnion.getID(), Utils.duesYears[2]);
		
		Text duesLabel = new Text(Utils.duesYears[2]);
		duesLabel.setFont(Font.font("Serif", 14));
		duesLabel.setFill(Color.BLACK);

		HBox duesBox = new HBox();
		duesBox.getChildren().add(duesLabel);
		duesBox.setPadding(new Insets(5));
		duesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);
		
		Text duesEstLabel = new Text(nf.format(Utils.computeDues(dbConn, aCreditUnion.getID(), Utils.duesYears[2])));
		duesEstLabel.setFont(Font.font("Serif", 12));
		duesEstLabel.setFill(Color.BLACK);

		HBox duesEstBox = new HBox();
		duesEstBox.getChildren().add(duesEstLabel);
		duesEstBox.setPadding(new Insets(3));
		duesEstBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer2 = new Text(" ------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);
		
		Text reafl100Label = new Text(duesAdjs.getReAfl100Pct());
		reafl100Label.setFont(Font.font("Serif", 12));
		reafl100Label.setFill(Color.BLACK);

		HBox reafl100Box = new HBox();
		reafl100Box.getChildren().add(reafl100Label);
		reafl100Box.setPadding(new Insets(3));
		reafl100Box.setAlignment(Pos.CENTER_RIGHT);
		
		Text midYrDuesLostLabel = new Text(duesAdjs.getMidYrAflDuesLost());
		midYrDuesLostLabel.setFont(Font.font("Serif", 12));
		midYrDuesLostLabel.setFill(Color.BLACK);

		HBox midYrDuesLostBox = new HBox();
		midYrDuesLostBox.getChildren().add(midYrDuesLostLabel);
		midYrDuesLostBox.setPadding(new Insets(3));
		midYrDuesLostBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text aflInitDuesLostLabel = new Text(duesAdjs.getReAflInitLostDues());
		aflInitDuesLostLabel.setFont(Font.font("Serif", 12));
		aflInitDuesLostLabel.setFill(Color.BLACK);

		HBox aflInitDuesLostBox = new HBox();
		aflInitDuesLostBox.getChildren().add(aflInitDuesLostLabel);
		aflInitDuesLostBox.setPadding(new Insets(3));
		aflInitDuesLostBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text disAflDuesLostLabel = new Text(duesAdjs.getDuesLostFromDisAfls());
		disAflDuesLostLabel.setFont(Font.font("Serif", 12));
		disAflDuesLostLabel.setFill(Color.BLACK);

		HBox disAflDuesLostBox = new HBox();
		disAflDuesLostBox.getChildren().add(disAflDuesLostLabel);
		disAflDuesLostBox.setPadding(new Insets(3));
		disAflDuesLostBox.setAlignment(Pos.CENTER_RIGHT);

		Text duesPaidByDisAflsLabel = new Text(duesAdjs.getDuesPaidByDisAfls());
		duesPaidByDisAflsLabel.setFont(Font.font("Serif", 12));
		duesPaidByDisAflsLabel.setFill(Color.BLACK);

		HBox duesPaidByDisAflsBox = new HBox();
		duesPaidByDisAflsBox.getChildren().add(duesPaidByDisAflsLabel);
		duesPaidByDisAflsBox.setPadding(new Insets(3));
		duesPaidByDisAflsBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text hardshipWaiverLabel = new Text(duesAdjs.getHardshipWaivers());
		hardshipWaiverLabel.setFont(Font.font("Serif", 12));
		hardshipWaiverLabel.setFill(Color.BLACK);

		HBox hardshipWaiverBox = new HBox();
		hardshipWaiverBox.getChildren().add(hardshipWaiverLabel);
		hardshipWaiverBox.setPadding(new Insets(3));
		hardshipWaiverBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text mergerDuesCollectedLabel = new Text(duesAdjs.getDuesCollectedFromMergers());
		mergerDuesCollectedLabel.setFont(Font.font("Serif", 12));
		mergerDuesCollectedLabel.setFill(Color.BLACK);

		HBox mergerDuesCollectedBox = new HBox();
		mergerDuesCollectedBox.getChildren().add(mergerDuesCollectedLabel);
		mergerDuesCollectedBox.setPadding(new Insets(3));
		mergerDuesCollectedBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text duesLostFromInactivesLabel = new Text(duesAdjs.getDuesLostFromInactives());
		duesLostFromInactivesLabel.setFont(Font.font("Serif", 12));
		duesLostFromInactivesLabel.setFill(Color.BLACK);

		HBox duesLostFromInactivesBox = new HBox();
		duesLostFromInactivesBox.getChildren().add(duesLostFromInactivesLabel);
		duesLostFromInactivesBox.setPadding(new Insets(3));
		duesLostFromInactivesBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text otherLabel = new Text(duesAdjs.getDuesLostFromInactives());
		otherLabel.setFont(Font.font("Serif", 12));
		otherLabel.setFill(Color.BLACK);

		HBox otherBox = new HBox();
		otherBox.getChildren().add(otherLabel);
		otherBox.setPadding(new Insets(3));
		otherBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer3 = new Text(" ------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);
		
		Text collectedLabel = new Text(duesAdjs.getCollected());
		collectedLabel.setFont(Font.font("Serif", 12));
		collectedLabel.setFill(Color.BLACK);

		HBox collectedBox = new HBox();
		collectedBox.getChildren().add(collectedLabel);
		collectedBox.setPadding(new Insets(3));
		collectedBox.setAlignment(Pos.CENTER_RIGHT);
		
		Text spacer4 = new Text(" ------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);
		
		VBox wHeader = new VBox();
		wHeader.getChildren().add(duesBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(duesEstBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(reafl100Box);
		wHeader.getChildren().add(midYrDuesLostBox);
		wHeader.getChildren().add(aflInitDuesLostBox);
		wHeader.getChildren().add(disAflDuesLostBox);
		wHeader.getChildren().add(duesPaidByDisAflsBox);
		wHeader.getChildren().add(hardshipWaiverBox);
		wHeader.getChildren().add(mergerDuesCollectedBox);
		wHeader.getChildren().add(duesLostFromInactivesBox);
		wHeader.getChildren().add(otherBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(collectedBox);
		wHeader.getChildren().add(spacer4);
		wHeader.setAlignment(Pos.CENTER);

		return (wHeader);
	}
	
	private VBox buildDuesLabelBox(Stage primaryStage)
	{
		Text duesLabel = new Text("Dues Estimates");
		duesLabel.setFont(Font.font("Serif", 14));
		duesLabel.setFill(Color.BLACK);

		HBox duesBox = new HBox();
		duesBox.getChildren().add(duesLabel);
		duesBox.setPadding(new Insets(5));
		duesBox.setAlignment(Pos.CENTER_LEFT);
		
		Text spacer1 = new Text(" ----------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);
		
		Text duesEstLabel = new Text("Full Dues");
		duesEstLabel.setFont(Font.font("Serif", 12));
		duesEstLabel.setFill(Color.BLACK);
		
		Text spacer2 = new Text(" ----------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		HBox duesEstBox = new HBox();
		duesEstBox.getChildren().add(duesEstLabel);
		duesEstBox.setPadding(new Insets(3));
		duesEstBox.setAlignment(Pos.CENTER_LEFT);

		Text reafl100Label = new Text("    Reaffiliated at 100%");
		reafl100Label.setFont(Font.font("Serif", 12));
		reafl100Label.setFill(Color.BLACK);

		HBox reafl100Box = new HBox();
		reafl100Box.getChildren().add(reafl100Label);
		reafl100Box.setPadding(new Insets(3));
		reafl100Box.setAlignment(Pos.CENTER_LEFT);

		Text midYrReaflLabel = new Text("    Dues Lost From Mid-Year Affiliation");
		midYrReaflLabel.setFont(Font.font("Serif", 12));
		midYrReaflLabel.setFill(Color.BLACK);

		HBox midYrReaflBox = new HBox();
		midYrReaflBox.getChildren().add(midYrReaflLabel);
		midYrReaflBox.setPadding(new Insets(3));
		midYrReaflBox.setAlignment(Pos.CENTER_LEFT);
		
		Text reaflInitLabel = new Text("    Dues Lost From Affiliation Initiative");
		reaflInitLabel.setFont(Font.font("Serif", 12));
		reaflInitLabel.setFill(Color.BLACK);

		HBox reaflInitBox = new HBox();
		reaflInitBox.getChildren().add(reaflInitLabel);
		reaflInitBox.setPadding(new Insets(3));
		reaflInitBox.setAlignment(Pos.CENTER_LEFT);
		
		Text disaffiliationsLabel = new Text("    Dues Lost From Disaffiliation");
		disaffiliationsLabel.setFont(Font.font("Serif", 12));
		disaffiliationsLabel.setFill(Color.BLACK);

		HBox disaffiliationsBox = new HBox();
		disaffiliationsBox.getChildren().add(disaffiliationsLabel);
		disaffiliationsBox.setPadding(new Insets(3));
		disaffiliationsBox.setAlignment(Pos.CENTER_LEFT);
		
		Text duesPaidByDisaflsLabel = new Text("    Dues Paid By Disaffiliates");
		duesPaidByDisaflsLabel.setFont(Font.font("Serif", 12));
		duesPaidByDisaflsLabel.setFill(Color.BLACK);

		HBox duesPaidByDisaflsBox = new HBox();
		duesPaidByDisaflsBox.getChildren().add(duesPaidByDisaflsLabel);
		duesPaidByDisaflsBox.setPadding(new Insets(3));
		duesPaidByDisaflsBox.setAlignment(Pos.CENTER_LEFT);
		
		Text hardshipWaiversLabel = new Text("    Hardship Waivers");
		hardshipWaiversLabel.setFont(Font.font("Serif", 12));
		hardshipWaiversLabel.setFill(Color.BLACK);

		HBox hardshipWaiversBox = new HBox();
		hardshipWaiversBox.getChildren().add(hardshipWaiversLabel);
		hardshipWaiversBox.setPadding(new Insets(3));
		hardshipWaiversBox.setAlignment(Pos.CENTER_LEFT);
		
		Text mergerDuesLabel = new Text("    Dues Collected From Mergers");
		mergerDuesLabel.setFont(Font.font("Serif", 12));
		mergerDuesLabel.setFill(Color.BLACK);

		HBox mergerDuesBox = new HBox();
		mergerDuesBox.getChildren().add(mergerDuesLabel);
		mergerDuesBox.setPadding(new Insets(3));
		mergerDuesBox.setAlignment(Pos.CENTER_LEFT);
		
		Text deadCUsLabel = new Text("    Dues Lost From Inactive CUs");
		deadCUsLabel.setFont(Font.font("Serif", 12));
		deadCUsLabel.setFill(Color.BLACK);

		HBox deadCUsBox = new HBox();
		deadCUsBox.getChildren().add(deadCUsLabel);
		deadCUsBox.setPadding(new Insets(3));
		deadCUsBox.setAlignment(Pos.CENTER_LEFT);
		
		Text otherLabel = new Text("    Other");
		otherLabel.setFont(Font.font("Serif", 12));
		otherLabel.setFill(Color.BLACK);

		HBox otherBox = new HBox();
		otherBox.getChildren().add(otherLabel);
		otherBox.setPadding(new Insets(3));
		otherBox.setAlignment(Pos.CENTER_LEFT);
		
		Text spacer3 = new Text(" ----------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);
		
		Text collectedLabel = new Text("Collected");
		collectedLabel.setFont(Font.font("Serif", 12));
		collectedLabel.setFill(Color.BLACK);

		HBox collectedBox = new HBox();
		collectedBox.getChildren().add(collectedLabel);
		collectedBox.setPadding(new Insets(3));
		collectedBox.setAlignment(Pos.CENTER_LEFT);
		
		Text spacer4 = new Text(" ----------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);
		
		
		VBox wHeader = new VBox();
		wHeader.getChildren().add(duesBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(duesEstBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(reafl100Box);
		wHeader.getChildren().add(midYrReaflBox);
		wHeader.getChildren().add(reaflInitBox);
		wHeader.getChildren().add(disaffiliationsBox);
		wHeader.getChildren().add(duesPaidByDisaflsBox);
		wHeader.getChildren().add(hardshipWaiversBox);
		wHeader.getChildren().add(mergerDuesBox);
		wHeader.getChildren().add(deadCUsBox);
		wHeader.getChildren().add(otherBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(collectedBox);
		wHeader.getChildren().add(spacer4);

		
		wHeader.setAlignment(Pos.CENTER);

		return (wHeader);
	}
	
	private VBox buildAdvancedSearchLabelBox(Stage primaryStage)
	{
		Text nameLabel = new Text("Name");
		nameLabel.setFont(Font.font("Serif", 15));
		nameLabel.setFill(Color.BLACK);

		HBox nameBox = new HBox();
		nameBox.getChildren().add(nameLabel);
		nameBox.setPadding(new Insets(5));
		
		Text cityLabel = new Text("City");
		cityLabel.setFont(Font.font("Serif", 15));
		cityLabel.setFill(Color.BLACK);

		HBox cityBox = new HBox();
		cityBox.getChildren().add(cityLabel);
		cityBox.setPadding(new Insets(5));

		Text stateLabel = new Text("State");
		stateLabel.setFont(Font.font("Serif", 15));

		HBox stateBox = new HBox();
		stateBox.getChildren().add(stateLabel);
		stateBox.setPadding(new Insets(5));
		
		Text zipLabel = new Text("Zip Code");
		zipLabel.setFont(Font.font("Serif", 15));
		zipLabel.setFill(Color.BLACK);

		HBox zipBox = new HBox();
		zipBox.getChildren().add(zipLabel);
		zipBox.setPadding(new Insets(5));
		
		Text phoneLabel = new Text("Phone #");
		phoneLabel.setFont(Font.font("Serif", 15));
		phoneLabel.setFill(Color.BLACK);

		HBox phoneBox = new HBox();
		phoneBox.getChildren().add(phoneLabel);
		phoneBox.setPadding(new Insets(5));
		
		Text webLabel = new Text("Website");
		webLabel.setFont(Font.font("Serif", 15));
		webLabel.setFill(Color.BLACK);

		HBox webBox = new HBox();
		webBox.getChildren().add(webLabel);
		webBox.setPadding(new Insets(5));
		
		Text ceoLastLabel = new Text("CEO Last Name");
		ceoLastLabel.setFont(Font.font("Serif", 15));
		ceoLastLabel.setFill(Color.BLACK);

		HBox ceoLastBox = new HBox();
		ceoLastBox.getChildren().add(ceoLastLabel);
		ceoLastBox.setPadding(new Insets(5));
		
		Text searchBoxLabel = new Text("");
		searchBoxLabel.setFont(Font.font("Serif", 15));
		searchBoxLabel.setFill(Color.BLACK);

		HBox searchBox = new HBox();
		searchBox.getChildren().add(searchBoxLabel);
		searchBox.setPadding(new Insets(5));
		
		Text includeInactivesLabel = new Text("");
		includeInactivesLabel.setFont(Font.font("Serif", 15));
		includeInactivesLabel.setFill(Color.BLACK);

		HBox includeInactivesBox = new HBox();
		includeInactivesBox.getChildren().add(includeInactivesLabel);
		includeInactivesBox.setPadding(new Insets(5));
		
		Text sortOrderLabel = new Text("Sort Order");
		sortOrderLabel.setFont(Font.font("Serif", 15));
		sortOrderLabel.setFill(Color.BLACK);

		HBox sortOrderBox = new HBox();
		sortOrderBox.getChildren().add(sortOrderLabel);
		sortOrderBox.setPadding(new Insets(5));
		
		VBox wHeader = new VBox();
		wHeader.getChildren().add(nameBox);
		wHeader.getChildren().add(cityBox);
		wHeader.getChildren().add(stateBox);
		wHeader.getChildren().add(zipBox);
		wHeader.getChildren().add(phoneBox);
		wHeader.getChildren().add(webBox);
		wHeader.getChildren().add(ceoLastBox);
		wHeader.getChildren().add(sortOrderBox);
		wHeader.getChildren().add(includeInactivesBox);
		wHeader.getChildren().add(searchBox);
		
		wHeader.setAlignment(Pos.CENTER);

		return (wHeader);
	}

	private VBox buildDemographicsLabelBox(Stage primaryStage)
	{	
		Text incomeStatementLabel = new Text("Demographics");
		incomeStatementLabel.setFont(Font.font("Serif", 14));
		incomeStatementLabel.setFill(Color.BLACK);

		HBox incomeStatementBox = new HBox();
		incomeStatementBox.getChildren().add(incomeStatementLabel);
		incomeStatementBox.setPadding(new Insets(5));
		incomeStatementBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" ---------------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text membersLabel = new Text("Members");
		membersLabel.setFont(Font.font("Serif", 12));
		membersLabel.setFill(Color.BLACK);

		HBox membersBox = new HBox();
		membersBox.getChildren().add(membersLabel);
		membersBox.setPadding(new Insets(3));
		membersBox.setAlignment(Pos.CENTER_LEFT);

		Text potentialMembersLabel = new Text("Potential Members");
		potentialMembersLabel.setFont(Font.font("Serif", 12));
		potentialMembersLabel.setFill(Color.BLACK);

		HBox potentialMembersBox = new HBox();
		potentialMembersBox.getChildren().add(potentialMembersLabel);
		potentialMembersBox.setPadding(new Insets(3));
		potentialMembersBox.setAlignment(Pos.CENTER_LEFT);

		Text partTimeEmployeesLabel = new Text("Part-Time Employees");
		partTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		partTimeEmployeesLabel.setFill(Color.BLACK);

		HBox partTimeEmployeesBox = new HBox();
		partTimeEmployeesBox.getChildren().add(partTimeEmployeesLabel);
		partTimeEmployeesBox.setPadding(new Insets(3));
		partTimeEmployeesBox.setAlignment(Pos.CENTER_LEFT);

		Text fullTimeEmployeesLabel = new Text("Full-Time Employees");
		fullTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		fullTimeEmployeesLabel.setFill(Color.BLACK);

		HBox fullTimeEmployeesBox = new HBox();
		fullTimeEmployeesBox.getChildren().add(fullTimeEmployeesLabel);
		fullTimeEmployeesBox.setPadding(new Insets(3));
		fullTimeEmployeesBox.setAlignment(Pos.CENTER_LEFT);

		Text branchesLabel = new Text("Branches");
		branchesLabel.setFont(Font.font("Serif", 12));
		branchesLabel.setFill(Color.BLACK);

		HBox branchesBox = new HBox();
		branchesBox.getChildren().add(branchesLabel);
		branchesBox.setPadding(new Insets(3));
		branchesBox.setAlignment(Pos.CENTER_LEFT);

		Text fhlbLabel = new Text("Member of Federal Home Loan Bank");
		fhlbLabel.setFont(Font.font("Serif", 12));
		fhlbLabel.setFill(Color.BLACK);

		HBox fhlbBox = new HBox();
		fhlbBox.getChildren().add(fhlbLabel);
		fhlbBox.setPadding(new Insets(3));
		fhlbBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" ---------------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(incomeStatementBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(membersBox);
		wHeader.getChildren().add(potentialMembersBox);
		wHeader.getChildren().add(partTimeEmployeesBox);
		wHeader.getChildren().add(fullTimeEmployeesBox);
		wHeader.getChildren().add(branchesBox);
		wHeader.getChildren().add(fhlbBox);
		wHeader.getChildren().add(spacer2);

		return (wHeader);
	}

	private VBox buildKeyRatiosLabelBox(Stage primaryStage)
	{	
		Text assetQualityLabel = new Text("Key Ratios");
		assetQualityLabel.setFont(Font.font("Serif", 14));
		assetQualityLabel.setFill(Color.BLACK);

		HBox assetQualityBox = new HBox();
		assetQualityBox.getChildren().add(assetQualityLabel);
		assetQualityBox.setPadding(new Insets(5));
		assetQualityBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" --------------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text pcaNetWorthToAssetsLabel = new Text("PCA Net Worth Ratio");
		pcaNetWorthToAssetsLabel.setFont(Font.font("Serif", 12));
		pcaNetWorthToAssetsLabel.setFill(Color.BLACK);

		HBox pcaNetWorthToAssetsBox = new HBox();
		pcaNetWorthToAssetsBox.getChildren().add(pcaNetWorthToAssetsLabel);
		pcaNetWorthToAssetsBox.setPadding(new Insets(3));
		pcaNetWorthToAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text pcaNetWorthClassificationLabel = new Text("PCA Net Worth Classification");
		pcaNetWorthClassificationLabel.setFont(Font.font("Serif", 12));
		pcaNetWorthClassificationLabel.setFill(Color.BLACK);

		HBox pcaNetWorthClassificationBox = new HBox();
		pcaNetWorthClassificationBox.getChildren().add(pcaNetWorthClassificationLabel);
		pcaNetWorthClassificationBox.setPadding(new Insets(3));
		pcaNetWorthClassificationBox.setAlignment(Pos.CENTER_LEFT);

		Text totalCapitalToAssetsLabel = new Text("Total Capital / Assets");
		totalCapitalToAssetsLabel.setFont(Font.font("Serif", 12));
		totalCapitalToAssetsLabel.setFill(Color.BLACK);

		HBox totalCapitalToAssetsBox = new HBox();
		totalCapitalToAssetsBox.getChildren().add(totalCapitalToAssetsLabel);
		totalCapitalToAssetsBox.setPadding(new Insets(3));
		totalCapitalToAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text netCapitalToAssetsLabel = new Text("Net Captial / Assets");
		netCapitalToAssetsLabel.setFont(Font.font("Serif", 12));
		netCapitalToAssetsLabel.setFill(Color.BLACK);

		HBox netCapitalToAssetsBox = new HBox();
		netCapitalToAssetsBox.getChildren().add(netCapitalToAssetsLabel);
		netCapitalToAssetsBox.setPadding(new Insets(3));
		netCapitalToAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" ----- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text delinquencyRateLabel = new Text("Delinquent Loans / Loans");
		delinquencyRateLabel.setFont(Font.font("Serif", 12));
		delinquencyRateLabel.setFill(Color.BLACK);

		HBox delinquencyRateBox = new HBox();
		delinquencyRateBox.getChildren().add(delinquencyRateLabel);
		delinquencyRateBox.setPadding(new Insets(3));
		delinquencyRateBox.setAlignment(Pos.CENTER_LEFT);

		Text estDelqLoansToLoansLabel = new Text("Est Delinquent Loans / Loans");
		estDelqLoansToLoansLabel.setFont(Font.font("Serif", 12));
		estDelqLoansToLoansLabel.setFill(Color.BLACK);

		HBox estDelqLoansToLoansBox = new HBox();
		estDelqLoansToLoansBox.getChildren().add(estDelqLoansToLoansLabel);
		estDelqLoansToLoansBox.setPadding(new Insets(3));
		estDelqLoansToLoansBox.setAlignment(Pos.CENTER_LEFT);

		Text netChargeOffLabel = new Text("Net Charge-Offs / Avg Loans");
		netChargeOffLabel.setFont(Font.font("Serif", 12));
		netChargeOffLabel.setFill(Color.BLACK);

		HBox netChargeOffBox = new HBox();
		netChargeOffBox.getChildren().add(netChargeOffLabel);
		netChargeOffBox.setPadding(new Insets(3));
		netChargeOffBox.setAlignment(Pos.CENTER_LEFT);

		Text oreosToAssetsLabel = new Text("Foreclosed & Repo Assets / Assets");
		oreosToAssetsLabel.setFont(Font.font("Serif", 12));
		oreosToAssetsLabel.setFill(Color.BLACK);

		HBox oreosToAssetsBox = new HBox();
		oreosToAssetsBox.getChildren().add(oreosToAssetsLabel);
		oreosToAssetsBox.setPadding(new Insets(3));
		oreosToAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" ----- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text operatingExpenseRatioLabel = new Text("Operating Expenses / Avg Assets");
		operatingExpenseRatioLabel.setFont(Font.font("Serif", 12));
		operatingExpenseRatioLabel.setFill(Color.BLACK);

		HBox operatingExpenseRatioBox = new HBox();
		operatingExpenseRatioBox.getChildren().add(operatingExpenseRatioLabel);
		operatingExpenseRatioBox.setPadding(new Insets(3));
		operatingExpenseRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text roaLabel = new Text("Net Income / Avg Assets");
		roaLabel.setFont(Font.font("Serif", 12));
		roaLabel.setFill(Color.BLACK);

		HBox roaBox = new HBox();
		roaBox.getChildren().add(roaLabel);
		roaBox.setPadding(new Insets(3));
		roaBox.setAlignment(Pos.CENTER_LEFT);

		Text feeIncomeRatioLabel = new Text("Fee Income / Avg Assets");
		feeIncomeRatioLabel.setFont(Font.font("Serif", 12));
		feeIncomeRatioLabel.setFill(Color.BLACK);

		HBox feeIncomeRatioBox = new HBox();
		feeIncomeRatioBox.getChildren().add(feeIncomeRatioLabel);
		feeIncomeRatioBox.setPadding(new Insets(3));
		feeIncomeRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text grossSpreadLabel = new Text("Gross Spread");
		grossSpreadLabel.setFont(Font.font("Serif", 12));
		grossSpreadLabel.setFill(Color.BLACK);

		HBox grossSpreadBox = new HBox();
		grossSpreadBox.getChildren().add(grossSpreadLabel);
		grossSpreadBox.setPadding(new Insets(3));
		grossSpreadBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" ----- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansToSavingsLabel = new Text("Loans / Savings");
		loansToSavingsLabel.setFont(Font.font("Serif", 12));
		loansToSavingsLabel.setFill(Color.BLACK);

		HBox loansToSavingsBox = new HBox();
		loansToSavingsBox.getChildren().add(loansToSavingsLabel);
		loansToSavingsBox.setPadding(new Insets(3));
		loansToSavingsBox.setAlignment(Pos.CENTER_LEFT);

		Text borrowingsToSavingsAndEquityLabel = new Text("Borrowings / Savings and Equity");
		borrowingsToSavingsAndEquityLabel.setFont(Font.font("Serif", 12));
		borrowingsToSavingsAndEquityLabel.setFill(Color.BLACK);

		HBox borrowingsToSavingsAndEquityBox = new HBox();
		borrowingsToSavingsAndEquityBox.getChildren().add(borrowingsToSavingsAndEquityLabel);
		borrowingsToSavingsAndEquityBox.setPadding(new Insets(3));
		borrowingsToSavingsAndEquityBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" ----- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text mblRatioLabel = new Text("MBLs / Assets");
		mblRatioLabel.setFont(Font.font("Serif", 12));
		mblRatioLabel.setFill(Color.BLACK);

		HBox mblRatioBox = new HBox();
		mblRatioBox.getChildren().add(mblRatioLabel);
		mblRatioBox.setPadding(new Insets(3));
		mblRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text texasRatioLabel = new Text("Texas Ratio");
		texasRatioLabel.setFont(Font.font("Serif", 12));
		texasRatioLabel.setFill(Color.BLACK);

		HBox texasRatioBox = new HBox();
		texasRatioBox.getChildren().add(texasRatioLabel);
		texasRatioBox.setPadding(new Insets(3));
		texasRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text caeScoreLabel = new Text("CAE Score (1-5, 1=Best)");
		caeScoreLabel.setFont(Font.font("Serif", 12));
		caeScoreLabel.setFill(Color.BLACK);

		HBox caeScoreBox = new HBox();
		caeScoreBox.getChildren().add(caeScoreLabel);
		caeScoreBox.setPadding(new Insets(3));
		caeScoreBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer6 = new Text(" --------------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(assetQualityBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(pcaNetWorthToAssetsBox);
		wHeader.getChildren().add(pcaNetWorthClassificationBox);
		wHeader.getChildren().add(totalCapitalToAssetsBox);
		wHeader.getChildren().add(netCapitalToAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(delinquencyRateBox);
		wHeader.getChildren().add(estDelqLoansToLoansBox);
		wHeader.getChildren().add(netChargeOffBox);
		wHeader.getChildren().add(oreosToAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(operatingExpenseRatioBox);
		wHeader.getChildren().add(roaBox);
		wHeader.getChildren().add(grossSpreadBox);
		wHeader.getChildren().add(feeIncomeRatioBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansToSavingsBox);
		wHeader.getChildren().add(borrowingsToSavingsAndEquityBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(mblRatioBox);
		wHeader.getChildren().add(texasRatioBox);
		wHeader.getChildren().add(caeScoreBox);
		wHeader.getChildren().add(spacer6);

		return (wHeader);
	}

	private VBox buildAssetQualityLabelBox(Stage primaryStage)
	{	
		Text assetQualityLabel = new Text("Asset Quality");
		assetQualityLabel.setFont(Font.font("Serif", 14));
		assetQualityLabel.setFill(Color.BLACK);

		HBox assetQualityBox = new HBox();
		assetQualityBox.getChildren().add(assetQualityLabel);
		assetQualityBox.setPadding(new Insets(5));
		assetQualityBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" ---------------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text delinquentLoansAndLeasesLabel = new Text("$ Delinquent Loans and Leases");
		delinquentLoansAndLeasesLabel.setFont(Font.font("Serif", 14));
		delinquentLoansAndLeasesLabel.setFill(Color.BLACK);

		HBox delinquentLoansAndLeasesBox = new HBox();
		delinquentLoansAndLeasesBox.getChildren().add(delinquentLoansAndLeasesLabel);
		delinquentLoansAndLeasesBox.setPadding(new Insets(5));
		delinquentLoansAndLeasesBox.setAlignment(Pos.CENTER_LEFT);

		Text twoToSixMoDelqLabel = new Text(" - 2 to 6 Months Delq");
		twoToSixMoDelqLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqBox = new HBox();
		twoToSixMoDelqBox.getChildren().add(twoToSixMoDelqLabel);
		twoToSixMoDelqBox.setPadding(new Insets(3));
		twoToSixMoDelqBox.setAlignment(Pos.CENTER_LEFT);

		Text sixToTwelveMoDelqLabel = new Text(" - 6 to 12 Months Delq");
		sixToTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqBox = new HBox();
		sixToTwelveMoDelqBox.getChildren().add(sixToTwelveMoDelqLabel);
		sixToTwelveMoDelqBox.setPadding(new Insets(3));
		sixToTwelveMoDelqBox.setAlignment(Pos.CENTER_LEFT);

		Text overTwelveMoDelqLabel = new Text(" - Over 12 Months Delq");
		overTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqBox = new HBox();
		overTwelveMoDelqBox.getChildren().add(overTwelveMoDelqLabel);
		overTwelveMoDelqBox.setPadding(new Insets(3));
		overTwelveMoDelqBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" ---------------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalDelqLabel = new Text(" -- Total Delinquencies $");
		totalDelqLabel.setFont(Font.font("Serif", 12));
		totalDelqLabel.setFill(Color.BLACK);

		HBox totalDelqBox = new HBox();
		totalDelqBox.getChildren().add(totalDelqLabel);
		totalDelqBox.setPadding(new Insets(3));
		totalDelqBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" ---------------------------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text delinquentLoansAndLeasesRatioLabel = new Text("% Delinquent Loans and Leases");
		delinquentLoansAndLeasesRatioLabel.setFont(Font.font("Serif", 14));
		delinquentLoansAndLeasesRatioLabel.setFill(Color.BLACK);

		HBox delinquentLoansAndLeasesRatioBox = new HBox();
		delinquentLoansAndLeasesRatioBox.getChildren().add(delinquentLoansAndLeasesRatioLabel);
		delinquentLoansAndLeasesRatioBox.setPadding(new Insets(5));
		delinquentLoansAndLeasesRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text twoToSixMoDelqRatioLabel = new Text(" - 2 to 6 Months Delq");
		twoToSixMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqRatioLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqRatioBox = new HBox();
		twoToSixMoDelqRatioBox.getChildren().add(twoToSixMoDelqRatioLabel);
		twoToSixMoDelqRatioBox.setPadding(new Insets(3));
		twoToSixMoDelqRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text sixToTwelveMoDelqRatioLabel = new Text(" - 6 to 12 Months Delq");
		sixToTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqRatioBox = new HBox();
		sixToTwelveMoDelqRatioBox.getChildren().add(sixToTwelveMoDelqRatioLabel);
		sixToTwelveMoDelqRatioBox.setPadding(new Insets(3));
		sixToTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text overTwelveMoDelqRatioLabel = new Text(" - Over 12 Months Delq");
		overTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqRatioBox = new HBox();
		overTwelveMoDelqRatioBox.getChildren().add(overTwelveMoDelqRatioLabel);
		overTwelveMoDelqRatioBox.setPadding(new Insets(3));
		overTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" ---------------------------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalDelqRatioLabel = new Text(" -- Total Delinquencies %");
		totalDelqRatioLabel.setFont(Font.font("Serif", 12));
		totalDelqRatioLabel.setFill(Color.BLACK);

		HBox totalDelqRatioBox = new HBox();
		totalDelqRatioBox.getChildren().add(totalDelqRatioLabel);
		totalDelqRatioBox.setPadding(new Insets(3));
		totalDelqRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" ---------------------------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text netChargeOffsLabel = new Text(" $ Net Charge Offs");
		netChargeOffsLabel.setFont(Font.font("Serif", 12));
		netChargeOffsLabel.setFill(Color.BLACK);

		HBox netChargeOffsBox = new HBox();
		netChargeOffsBox.getChildren().add(netChargeOffsLabel);
		netChargeOffsBox.setPadding(new Insets(3));
		netChargeOffsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer6 = new Text(" ---------------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text netChargeOffsRatioLabel = new Text(" % Net Charge Offs");
		netChargeOffsRatioLabel.setFont(Font.font("Serif", 12));
		netChargeOffsRatioLabel.setFill(Color.BLACK);

		HBox netChargeOffsRatioBox = new HBox();
		netChargeOffsRatioBox.getChildren().add(netChargeOffsRatioLabel);
		netChargeOffsRatioBox.setPadding(new Insets(3));
		netChargeOffsRatioBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer7 = new Text(" ---------------------------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(assetQualityBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(delinquentLoansAndLeasesBox);
		wHeader.getChildren().add(twoToSixMoDelqBox);
		wHeader.getChildren().add(sixToTwelveMoDelqBox);
		wHeader.getChildren().add(overTwelveMoDelqBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalDelqBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(netChargeOffsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(delinquentLoansAndLeasesRatioBox);
		wHeader.getChildren().add(twoToSixMoDelqRatioBox);
		wHeader.getChildren().add(sixToTwelveMoDelqRatioBox);
		wHeader.getChildren().add(overTwelveMoDelqRatioBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(totalDelqRatioBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netChargeOffsRatioBox);
		wHeader.getChildren().add(spacer7);

		return (wHeader);
	}

	private VBox buildIncomeStatementPage1LabelBox(Stage primaryStage)
	{	
		Text incomeStatementLabel = new Text("Income Statement");
		incomeStatementLabel.setFont(Font.font("Serif", 14));
		incomeStatementLabel.setFill(Color.BLACK);

		HBox incomeStatementBox = new HBox();
		incomeStatementBox.getChildren().add(incomeStatementLabel);
		incomeStatementBox.setPadding(new Insets(5));
		incomeStatementBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" ---------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text loanAndLeaseInterestLabel = new Text("Loan and Lease Interest");
		loanAndLeaseInterestLabel.setFont(Font.font("Serif", 12));
		loanAndLeaseInterestLabel.setFill(Color.BLACK);

		HBox loanAndLeaseInterestBox = new HBox();
		loanAndLeaseInterestBox.getChildren().add(loanAndLeaseInterestLabel);
		loanAndLeaseInterestBox.setPadding(new Insets(3));
		loanAndLeaseInterestBox.setAlignment(Pos.CENTER_LEFT);

		Text rebatesLabel = new Text("Less Rebates");
		rebatesLabel.setFont(Font.font("Serif", 12));
		rebatesLabel.setFill(Color.BLACK);

		HBox rebatesBox = new HBox();
		rebatesBox.getChildren().add(rebatesLabel);
		rebatesBox.setPadding(new Insets(3));
		rebatesBox.setAlignment(Pos.CENTER_LEFT);

		Text feeIncomeLabel = new Text("Fee Income");
		feeIncomeLabel.setFont(Font.font("Serif", 12));
		feeIncomeLabel.setFill(Color.BLACK);

		HBox feeIncomeBox = new HBox();
		feeIncomeBox.getChildren().add(feeIncomeLabel);
		feeIncomeBox.setPadding(new Insets(3));
		feeIncomeBox.setAlignment(Pos.CENTER_LEFT);

		Text investmentIncomeLabel = new Text("Investment Income");
		investmentIncomeLabel.setFont(Font.font("Serif", 12));
		investmentIncomeLabel.setFill(Color.BLACK);

		HBox investmentIncomeBox = new HBox();
		investmentIncomeBox.getChildren().add(investmentIncomeLabel);
		investmentIncomeBox.setPadding(new Insets(3));
		investmentIncomeBox.setAlignment(Pos.CENTER_LEFT);

		Text otherOperatingIncomeLabel = new Text("Other Operating Income");
		otherOperatingIncomeLabel.setFont(Font.font("Serif", 12));
		otherOperatingIncomeLabel.setFill(Color.BLACK);

		HBox otherOperatingIncomeBox = new HBox();
		otherOperatingIncomeBox.getChildren().add(otherOperatingIncomeLabel);
		otherOperatingIncomeBox.setPadding(new Insets(3));
		otherOperatingIncomeBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" ---------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalIncomeLabel = new Text(" - Total Income");
		totalIncomeLabel.setFont(Font.font("Serif", 12));
		totalIncomeLabel.setFill(Color.BLACK);

		HBox totalIncomeBox = new HBox();
		totalIncomeBox.getChildren().add(totalIncomeLabel);
		totalIncomeBox.setPadding(new Insets(3));
		totalIncomeBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" ---------------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text salariesAndBenefitsLabel = new Text("Salaries and Benefits");
		salariesAndBenefitsLabel.setFont(Font.font("Serif", 12));
		salariesAndBenefitsLabel.setFill(Color.BLACK);

		HBox salariesAndBenefitsBox = new HBox();
		salariesAndBenefitsBox.getChildren().add(salariesAndBenefitsLabel);
		salariesAndBenefitsBox.setPadding(new Insets(3));
		salariesAndBenefitsBox.setAlignment(Pos.CENTER_LEFT);

		Text officeOccupancyLabel = new Text("Office Occupancy");
		officeOccupancyLabel.setFont(Font.font("Serif", 12));
		officeOccupancyLabel.setFill(Color.BLACK);

		HBox officeOccupancyBox = new HBox();
		officeOccupancyBox.getChildren().add(officeOccupancyLabel);
		officeOccupancyBox.setPadding(new Insets(3));
		officeOccupancyBox.setAlignment(Pos.CENTER_LEFT);

		Text officeOperationsLabel = new Text("Office Operations");
		officeOperationsLabel.setFont(Font.font("Serif", 12));
		officeOperationsLabel.setFill(Color.BLACK);

		HBox officeOperationsBox = new HBox();
		officeOperationsBox.getChildren().add(officeOperationsLabel);
		officeOperationsBox.setPadding(new Insets(3));
		officeOperationsBox.setAlignment(Pos.CENTER_LEFT);

		Text educationAndPromotionLabel = new Text("Education and Promotion");
		educationAndPromotionLabel.setFont(Font.font("Serif", 12));
		educationAndPromotionLabel.setFill(Color.BLACK);

		HBox educationAndPromotionBox = new HBox();
		educationAndPromotionBox.getChildren().add(educationAndPromotionLabel);
		educationAndPromotionBox.setPadding(new Insets(3));
		educationAndPromotionBox.setAlignment(Pos.CENTER_LEFT);

		Text loanServicingLabel = new Text("Loan Servicing");
		loanServicingLabel.setFont(Font.font("Serif", 12));
		loanServicingLabel.setFill(Color.BLACK);

		HBox loanServicingBox = new HBox();
		loanServicingBox.getChildren().add(loanServicingLabel);
		loanServicingBox.setPadding(new Insets(3));
		loanServicingBox.setAlignment(Pos.CENTER_LEFT);

		Text professionalAndOutsideServicesLabel = new Text("Prof and Outside Services");
		professionalAndOutsideServicesLabel.setFont(Font.font("Serif", 12));
		professionalAndOutsideServicesLabel.setFill(Color.BLACK);

		HBox professionalAndOutsideServicesBox = new HBox();
		professionalAndOutsideServicesBox.getChildren().add(professionalAndOutsideServicesLabel);
		professionalAndOutsideServicesBox.setPadding(new Insets(3));
		professionalAndOutsideServicesBox.setAlignment(Pos.CENTER_LEFT);

		Text memberInsuranceLabel = new Text("Member Insurance");
		memberInsuranceLabel.setFont(Font.font("Serif", 12));
		memberInsuranceLabel.setFill(Color.BLACK);

		HBox memberInsuranceBox = new HBox();
		memberInsuranceBox.getChildren().add(memberInsuranceLabel);
		memberInsuranceBox.setPadding(new Insets(3));
		memberInsuranceBox.setAlignment(Pos.CENTER_LEFT);

		Text allOtherExpensesLabel = new Text("All Other Expenses");
		allOtherExpensesLabel.setFont(Font.font("Serif", 12));
		allOtherExpensesLabel.setFill(Color.BLACK);

		HBox allOtherExpensesBox = new HBox();
		allOtherExpensesBox.getChildren().add(allOtherExpensesLabel);
		allOtherExpensesBox.setPadding(new Insets(3));
		allOtherExpensesBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" ---------------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text expenseSubtotalLabel = new Text(" - Expense Subtotal");
		expenseSubtotalLabel.setFont(Font.font("Serif", 12));
		expenseSubtotalLabel.setFill(Color.BLACK);

		HBox expenseSubtotalBox = new HBox();
		expenseSubtotalBox.getChildren().add(expenseSubtotalLabel);
		expenseSubtotalBox.setPadding(new Insets(3));
		expenseSubtotalBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" ---------------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text provisionForLoanLossLabel = new Text(" - Provision For Loan Loss");
		provisionForLoanLossLabel.setFont(Font.font("Serif", 12));
		provisionForLoanLossLabel.setFill(Color.BLACK);

		HBox provisionForLoanLossBox = new HBox();
		provisionForLoanLossBox.getChildren().add(provisionForLoanLossLabel);
		provisionForLoanLossBox.setPadding(new Insets(3));
		provisionForLoanLossBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer6 = new Text(" ---------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);


		VBox wHeader = new VBox();
		wHeader.getChildren().add(incomeStatementBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(loanAndLeaseInterestBox);
		wHeader.getChildren().add(rebatesBox);
		wHeader.getChildren().add(feeIncomeBox);
		wHeader.getChildren().add(investmentIncomeBox);
		wHeader.getChildren().add(otherOperatingIncomeBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalIncomeBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(salariesAndBenefitsBox);
		wHeader.getChildren().add(officeOccupancyBox);
		wHeader.getChildren().add(officeOperationsBox);
		wHeader.getChildren().add(educationAndPromotionBox);
		wHeader.getChildren().add(loanServicingBox);
		wHeader.getChildren().add(professionalAndOutsideServicesBox);
		wHeader.getChildren().add(memberInsuranceBox);
		wHeader.getChildren().add(allOtherExpensesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(expenseSubtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(provisionForLoanLossBox);
		wHeader.getChildren().add(spacer6);


		return (wHeader);
	}

	private VBox buildIncomeStatementPage2LabelBox(Stage primaryStage)
	{	
		Text incomeStatementLabel = new Text("Income Statement");
		incomeStatementLabel.setFont(Font.font("Serif", 14));
		incomeStatementLabel.setFill(Color.BLACK);

		HBox incomeStatementBox = new HBox();
		incomeStatementBox.getChildren().add(incomeStatementLabel);
		incomeStatementBox.setPadding(new Insets(5));
		incomeStatementBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" ------------------------------------ ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text expenseSubtotalInclProvisionsLabel = new Text("Exp Subtotal Incl Provisions");
		expenseSubtotalInclProvisionsLabel.setFont(Font.font("Serif", 12));
		expenseSubtotalInclProvisionsLabel.setFill(Color.BLACK);

		HBox expenseSubtotalInclProvisionsBox = new HBox();
		expenseSubtotalInclProvisionsBox.getChildren().add(expenseSubtotalInclProvisionsLabel);
		expenseSubtotalInclProvisionsBox.setPadding(new Insets(3));
		expenseSubtotalInclProvisionsBox.setAlignment(Pos.CENTER_LEFT);

		Text nonOperatingGainLossLabel = new Text("Non Operating Gain (Loss)");
		nonOperatingGainLossLabel.setFont(Font.font("Serif", 12));
		nonOperatingGainLossLabel.setFill(Color.BLACK);

		HBox nonOperatingGainLossBox = new HBox();
		nonOperatingGainLossBox.getChildren().add(nonOperatingGainLossLabel);
		nonOperatingGainLossBox.setPadding(new Insets(3));
		nonOperatingGainLossBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" ------------------------------------ ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text incomeBeforeDivsAndIntLabel = new Text("Income Before Divs And Int");
		incomeBeforeDivsAndIntLabel.setFont(Font.font("Serif", 12));
		incomeBeforeDivsAndIntLabel.setFill(Color.BLACK);

		HBox incomeBeforeDivsAndIntBox = new HBox();
		incomeBeforeDivsAndIntBox.getChildren().add(incomeBeforeDivsAndIntLabel);
		incomeBeforeDivsAndIntBox.setPadding(new Insets(3));
		incomeBeforeDivsAndIntBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" ------------------------------------ ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text costOfFundsLabel = new Text("Cost of Funds:");
		costOfFundsLabel.setFont(Font.font("Serif", 14));
		costOfFundsLabel.setFill(Color.BLACK);

		HBox costOfFundsBox = new HBox();
		costOfFundsBox.getChildren().add(costOfFundsLabel);
		costOfFundsBox.setPadding(new Insets(5));
		costOfFundsBox.setAlignment(Pos.CENTER_LEFT);

		Text interestOnBorrowingsLabel = new Text(" - Interest On Borrowings");
		interestOnBorrowingsLabel.setFont(Font.font("Serif", 12));
		interestOnBorrowingsLabel.setFill(Color.BLACK);

		HBox interestOnBorrowingsBox = new HBox();
		interestOnBorrowingsBox.getChildren().add(interestOnBorrowingsLabel);
		interestOnBorrowingsBox.setPadding(new Insets(3));
		interestOnBorrowingsBox.setAlignment(Pos.CENTER_LEFT);

		Text dividendsLabel = new Text(" - Dividends");
		dividendsLabel.setFont(Font.font("Serif", 12));
		dividendsLabel.setFill(Color.BLACK);

		HBox dividendsBox = new HBox();
		dividendsBox.getChildren().add(dividendsLabel);
		dividendsBox.setPadding(new Insets(3));
		dividendsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" ------------------------------------ ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text subtotalLabel = new Text(" - Subtotal");
		subtotalLabel.setFont(Font.font("Serif", 12));
		subtotalLabel.setFill(Color.BLACK);

		HBox subtotalBox = new HBox();
		subtotalBox.getChildren().add(subtotalLabel);
		subtotalBox.setPadding(new Insets(3));
		subtotalBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" ------------------------------------ ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text estDistributionOfNetLabel = new Text("Est Distribution of Net:");
		estDistributionOfNetLabel.setFont(Font.font("Serif", 14));
		estDistributionOfNetLabel.setFill(Color.BLACK);

		HBox estDistributionOfNetBox = new HBox();
		estDistributionOfNetBox.getChildren().add(estDistributionOfNetLabel);
		estDistributionOfNetBox.setPadding(new Insets(5));
		estDistributionOfNetBox.setAlignment(Pos.CENTER_LEFT);

		Text reserveTransferLabel = new Text(" - Reserve Transfer");
		reserveTransferLabel.setFont(Font.font("Serif", 12));
		reserveTransferLabel.setFill(Color.BLACK);

		HBox reserveTransferBox = new HBox();
		reserveTransferBox.getChildren().add(reserveTransferLabel);
		reserveTransferBox.setPadding(new Insets(3));
		reserveTransferBox.setAlignment(Pos.CENTER_LEFT);

		Text otherCapitalTransfersLabel = new Text(" - Other Capital Transfers");
		otherCapitalTransfersLabel.setFont(Font.font("Serif", 12));
		otherCapitalTransfersLabel.setFill(Color.BLACK);

		HBox otherCapitalTransfersBox = new HBox();
		otherCapitalTransfersBox.getChildren().add(otherCapitalTransfersLabel);
		otherCapitalTransfersBox.setPadding(new Insets(3));
		otherCapitalTransfersBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer6 = new Text(" ------------------------------------ ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text netIncomeLabel = new Text(" - Net Income");
		netIncomeLabel.setFont(Font.font("Serif", 12));
		netIncomeLabel.setFill(Color.BLACK);

		HBox netIncomeBox = new HBox();
		netIncomeBox.getChildren().add(netIncomeLabel);
		netIncomeBox.setPadding(new Insets(3));
		netIncomeBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer7 = new Text(" ------------------------------------ ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text spacer8 = new Text(" ------------------------------------ ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(incomeStatementBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(expenseSubtotalInclProvisionsBox);
		wHeader.getChildren().add(nonOperatingGainLossBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(incomeBeforeDivsAndIntBox);
		wHeader.getChildren().add(spacer3);		
		wHeader.getChildren().add(costOfFundsBox);
		wHeader.getChildren().add(interestOnBorrowingsBox);
		wHeader.getChildren().add(dividendsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(subtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(estDistributionOfNetBox);
		wHeader.getChildren().add(reserveTransferBox);
		wHeader.getChildren().add(otherCapitalTransfersBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netIncomeBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(spacer8);


		return (wHeader);
	}

	private VBox buildLoansLabelBox(Stage primaryStage)
	{	
		Text loansAndLeasesLabel = new Text("Loans And Leases");
		loansAndLeasesLabel.setFont(Font.font("Serif", 14));
		loansAndLeasesLabel.setFill(Color.BLACK);

		HBox loansAndLeasesBox = new HBox();
		loansAndLeasesBox.getChildren().add(loansAndLeasesLabel);
		loansAndLeasesBox.setPadding(new Insets(5));
		loansAndLeasesBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" --------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text creditCardsLabel = new Text("Credit Cards");
		creditCardsLabel.setFont(Font.font("Serif", 12));
		creditCardsLabel.setFill(Color.BLACK);

		HBox creditCardsBox = new HBox();
		creditCardsBox.getChildren().add(creditCardsLabel);
		creditCardsBox.setPadding(new Insets(3));
		creditCardsBox.setAlignment(Pos.CENTER_LEFT);

		Text otherUnsecuredLabel = new Text("Other Unsecured");
		otherUnsecuredLabel.setFont(Font.font("Serif", 12));
		otherUnsecuredLabel.setFill(Color.BLACK);

		HBox otherUnsecuredBox = new HBox();
		otherUnsecuredBox.getChildren().add(otherUnsecuredLabel);
		otherUnsecuredBox.setPadding(new Insets(3));
		otherUnsecuredBox.setAlignment(Pos.CENTER_LEFT);

		Text palLabel = new Text("Payday Alternative (FCU Only)");
		palLabel.setFont(Font.font("Serif", 12));
		palLabel.setFill(Color.BLACK);

		HBox palBox = new HBox();
		palBox.getChildren().add(palLabel);
		palBox.setPadding(new Insets(3));
		palBox.setAlignment(Pos.CENTER_LEFT);

		Text eduLabel = new Text("Non-Fed Guaranteed Student");
		eduLabel.setFont(Font.font("Serif", 12));
		eduLabel.setFill(Color.BLACK);

		HBox eduBox = new HBox();
		eduBox.getChildren().add(eduLabel);
		eduBox.setPadding(new Insets(3));
		eduBox.setAlignment(Pos.CENTER_LEFT);

		Text newAutoLabel = new Text("New Vehicle");
		newAutoLabel.setFont(Font.font("Serif", 12));
		newAutoLabel.setFill(Color.BLACK);

		HBox newAutoBox = new HBox();
		newAutoBox.getChildren().add(newAutoLabel);
		newAutoBox.setPadding(new Insets(3));
		newAutoBox.setAlignment(Pos.CENTER_LEFT);

		Text usedAutoLabel = new Text("Used Vehicle");
		usedAutoLabel.setFont(Font.font("Serif", 12));
		usedAutoLabel.setFill(Color.BLACK);

		HBox usedAutoBox = new HBox();
		usedAutoBox.getChildren().add(usedAutoLabel);
		usedAutoBox.setPadding(new Insets(3));
		usedAutoBox.setAlignment(Pos.CENTER_LEFT);

		Text firstMortgageLabel = new Text("1st Mortgages");
		firstMortgageLabel.setFont(Font.font("Serif", 12));
		firstMortgageLabel.setFill(Color.BLACK);

		HBox firstMortgageBox = new HBox();
		firstMortgageBox.getChildren().add(firstMortgageLabel);
		firstMortgageBox.setPadding(new Insets(3));
		firstMortgageBox.setAlignment(Pos.CENTER_LEFT);

		Text secondMortgageLabel = new Text("2nd Mortgages");
		secondMortgageLabel.setFont(Font.font("Serif", 12));
		secondMortgageLabel.setFill(Color.BLACK);

		HBox secondMortgageBox = new HBox();
		secondMortgageBox.getChildren().add(secondMortgageLabel);
		secondMortgageBox.setPadding(new Insets(3));
		secondMortgageBox.setAlignment(Pos.CENTER_LEFT);

		Text leasesLabel = new Text("Leases Receivable");
		leasesLabel.setFont(Font.font("Serif", 12));
		leasesLabel.setFill(Color.BLACK);

		HBox leasesBox = new HBox();
		leasesBox.getChildren().add(leasesLabel);
		leasesBox.setPadding(new Insets(3));
		leasesBox.setAlignment(Pos.CENTER_LEFT);

		Text allOtherLoansLabel = new Text("All Other Loans");
		allOtherLoansLabel.setFont(Font.font("Serif", 12));
		allOtherLoansLabel.setFill(Color.BLACK);

		HBox allOtherLoansBox = new HBox();
		allOtherLoansBox.getChildren().add(allOtherLoansLabel);
		allOtherLoansBox.setPadding(new Insets(3));
		allOtherLoansBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" --------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLoansLabel = new Text("Total Loans & Leases");
		totalLoansLabel.setFont(Font.font("Serif", 13));
		totalLoansLabel.setFill(Color.BLACK);

		HBox totalLoansBox = new HBox();
		totalLoansBox.getChildren().add(totalLoansLabel);
		totalLoansBox.setPadding(new Insets(5));
		totalLoansBox.setAlignment(Pos.BASELINE_LEFT);

		Text spacer3 = new Text(" --------------------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text allowanceLabel = new Text("(Loan Loss Allowance)");
		allowanceLabel.setFont(Font.font("Serif", 12));
		allowanceLabel.setFill(Color.BLACK);

		HBox allowanceBox = new HBox();
		allowanceBox.getChildren().add(allowanceLabel);
		allowanceBox.setPadding(new Insets(3));
		allowanceBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" --------------------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text mblLabel = new Text("Business Loans");
		mblLabel.setFont(Font.font("Serif", 12));
		mblLabel.setFill(Color.BLACK);

		HBox mblBox = new HBox();
		mblBox.getChildren().add(mblLabel);
		mblBox.setPadding(new Insets(3));
		mblBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" --------------------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(loansAndLeasesBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(creditCardsBox);
		wHeader.getChildren().add(otherUnsecuredBox);
		wHeader.getChildren().add(palBox);
		wHeader.getChildren().add(eduBox);
		wHeader.getChildren().add(newAutoBox);
		wHeader.getChildren().add(usedAutoBox);
		wHeader.getChildren().add(firstMortgageBox);
		wHeader.getChildren().add(secondMortgageBox);
		wHeader.getChildren().add(leasesBox);
		wHeader.getChildren().add(allOtherLoansBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLoansBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(allowanceBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(mblBox);
		wHeader.getChildren().add(spacer5);

		return (wHeader);
	}

	private VBox buildLiabilitiesAndCapitalLabelBox(Stage primaryStage)
	{	
		Text liabilitiesAndCapitalLabel = new Text("Liabilities And Capital");
		liabilitiesAndCapitalLabel.setFont(Font.font("Serif", 14));
		liabilitiesAndCapitalLabel.setFill(Color.BLACK);

		HBox liabilitiesAndCapitalBox = new HBox();
		liabilitiesAndCapitalBox.getChildren().add(liabilitiesAndCapitalLabel);
		liabilitiesAndCapitalBox.setPadding(new Insets(5));
		liabilitiesAndCapitalBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" -------------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text reverseReposLabel = new Text("Reverse Repos");
		reverseReposLabel.setFont(Font.font("Serif", 12));
		reverseReposLabel.setFill(Color.BLACK);

		HBox reverseReposBox = new HBox();
		reverseReposBox.getChildren().add(reverseReposLabel);
		reverseReposBox.setPadding(new Insets(3));
		reverseReposBox.setAlignment(Pos.CENTER_LEFT);

		Text otherNotesLabel = new Text("Other Notes Payable");
		otherNotesLabel.setFont(Font.font("Serif", 12));
		otherNotesLabel.setFill(Color.BLACK);

		HBox otherNotesBox = new HBox();
		otherNotesBox.getChildren().add(otherNotesLabel);
		otherNotesBox.setPadding(new Insets(3));
		otherNotesBox.setAlignment(Pos.CENTER_LEFT);

		Text otherLiabilitiesLabel = new Text("All Other Liabilities");
		otherLiabilitiesLabel.setFont(Font.font("Serif", 12));
		otherLiabilitiesLabel.setFill(Color.BLACK);

		HBox otherLiabilitiesBox = new HBox();
		otherLiabilitiesBox.getChildren().add(otherLiabilitiesLabel);
		otherLiabilitiesBox.setPadding(new Insets(3));
		otherLiabilitiesBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" -------------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLiabilitiesLabel = new Text("Total Liabilities");
		totalLiabilitiesLabel.setFont(Font.font("Serif", 12));
		totalLiabilitiesLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesBox = new HBox();
		totalLiabilitiesBox.getChildren().add(totalLiabilitiesLabel);
		totalLiabilitiesBox.setPadding(new Insets(3));
		totalLiabilitiesBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" -------------------------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text regularSharesLabel = new Text("Regular Shares and Deposits");
		regularSharesLabel.setFont(Font.font("Serif", 12));
		regularSharesLabel.setFill(Color.BLACK);

		HBox regularSharesBox = new HBox();
		regularSharesBox.getChildren().add(regularSharesLabel);
		regularSharesBox.setPadding(new Insets(3));
		regularSharesBox.setAlignment(Pos.CENTER_LEFT);

		Text shareDraftsLabel = new Text("Share Drafts");
		shareDraftsLabel.setFont(Font.font("Serif", 12));
		shareDraftsLabel.setFill(Color.BLACK);

		HBox shareDraftsBox = new HBox();
		shareDraftsBox.getChildren().add(shareDraftsLabel);
		shareDraftsBox.setPadding(new Insets(3));
		shareDraftsBox.setAlignment(Pos.CENTER_LEFT);

		Text moneyMarketLabel = new Text("Money Market Shares");
		moneyMarketLabel.setFont(Font.font("Serif", 12));
		moneyMarketLabel.setFill(Color.BLACK);

		HBox moneyMarketBox = new HBox();
		moneyMarketBox.getChildren().add(moneyMarketLabel);
		moneyMarketBox.setPadding(new Insets(3));
		moneyMarketBox.setAlignment(Pos.CENTER_LEFT);

		Text iraKeoghLabel = new Text("IRA/Keoghs");
		iraKeoghLabel.setFont(Font.font("Serif", 12));
		iraKeoghLabel.setFill(Color.BLACK);

		HBox iraKeoghBox = new HBox();
		iraKeoghBox.getChildren().add(iraKeoghLabel);
		iraKeoghBox.setPadding(new Insets(3));
		iraKeoghBox.setAlignment(Pos.CENTER_LEFT);

		Text certificatesLabel = new Text("Certificates");
		certificatesLabel.setFont(Font.font("Serif", 12));
		certificatesLabel.setFill(Color.BLACK);

		HBox certificatesBox = new HBox();
		certificatesBox.getChildren().add(certificatesLabel);
		certificatesBox.setPadding(new Insets(3));
		certificatesBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" -------------------------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalSharesAndDepositsLabel = new Text("Total Shares and Deposits");
		totalSharesAndDepositsLabel.setFont(Font.font("Serif", 12));
		totalSharesAndDepositsLabel.setFill(Color.BLACK);

		HBox totalSharesAndDepositsBox = new HBox();
		totalSharesAndDepositsBox.getChildren().add(totalSharesAndDepositsLabel);
		totalSharesAndDepositsBox.setPadding(new Insets(3));
		totalSharesAndDepositsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" -------------------------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text reservesLabel = new Text("Reserves");
		reservesLabel.setFont(Font.font("Serif", 12));
		reservesLabel.setFill(Color.BLACK);

		HBox reservesBox = new HBox();
		reservesBox.getChildren().add(reservesLabel);
		reservesBox.setPadding(new Insets(3));
		reservesBox.setAlignment(Pos.CENTER_LEFT);

		Text undividedEarningsLabel = new Text("Undivided Earnings");
		undividedEarningsLabel.setFont(Font.font("Serif", 12));
		undividedEarningsLabel.setFill(Color.BLACK);

		HBox undividedEarningsBox = new HBox();
		undividedEarningsBox.getChildren().add(undividedEarningsLabel);
		undividedEarningsBox.setPadding(new Insets(3));
		undividedEarningsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer6 = new Text(" -------------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text totalCapitalLabel = new Text("Total Capital");
		totalCapitalLabel.setFont(Font.font("Serif", 12));
		totalCapitalLabel.setFill(Color.BLACK);

		HBox totalCapitalBox = new HBox();
		totalCapitalBox.getChildren().add(totalCapitalLabel);
		totalCapitalBox.setPadding(new Insets(3));
		totalCapitalBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer7 = new Text(" -------------------------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text totalLiabilitiesAndCapitalLabel = new Text("Total Liabilities and Capital");
		totalLiabilitiesAndCapitalLabel.setFont(Font.font("Serif", 14));
		totalLiabilitiesAndCapitalLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesAndCapitalBox = new HBox();
		totalLiabilitiesAndCapitalBox.getChildren().add(totalLiabilitiesAndCapitalLabel);
		totalLiabilitiesAndCapitalBox.setPadding(new Insets(5));
		totalLiabilitiesAndCapitalBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer8 = new Text(" -------------------------------------------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		Text spacer9 = new Text(" -------------------------------------------- ");
		spacer9.setFont(Font.font("Serif", 12));
		spacer9.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(liabilitiesAndCapitalBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(reverseReposBox);
		wHeader.getChildren().add(otherNotesBox);
		wHeader.getChildren().add(otherLiabilitiesBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLiabilitiesBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(regularSharesBox);
		wHeader.getChildren().add(shareDraftsBox);
		wHeader.getChildren().add(moneyMarketBox);
		wHeader.getChildren().add(iraKeoghBox);
		wHeader.getChildren().add(certificatesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalSharesAndDepositsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(reservesBox);
		wHeader.getChildren().add(undividedEarningsBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(totalCapitalBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(totalLiabilitiesAndCapitalBox);
		wHeader.getChildren().add(spacer8);
		wHeader.getChildren().add(spacer9);

		return (wHeader);
	}

	private VBox buildOtherAssetsLabelBox(Stage primaryStage)
	{	
		Text otherAssetsLabel = new Text("Other Assets");
		otherAssetsLabel.setFont(Font.font("Serif", 14));
		otherAssetsLabel.setFill(Color.BLACK);

		HBox otherAssetsBox = new HBox();
		otherAssetsBox.getChildren().add(otherAssetsLabel);
		otherAssetsBox.setPadding(new Insets(5));
		otherAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" -------------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text repoAssetsLabel = new Text("Foreclosed and Repossessed Assets");
		repoAssetsLabel.setFont(Font.font("Serif", 12));
		repoAssetsLabel.setFill(Color.BLACK);

		HBox repoAssetsBox = new HBox();
		repoAssetsBox.getChildren().add(repoAssetsLabel);
		repoAssetsBox.setPadding(new Insets(3));
		repoAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text landAndBuildingLabel = new Text("Land And Building");
		landAndBuildingLabel.setFont(Font.font("Serif", 12));
		landAndBuildingLabel.setFill(Color.BLACK);

		HBox landAndBuildingBox = new HBox();
		landAndBuildingBox.getChildren().add(landAndBuildingLabel);
		landAndBuildingBox.setPadding(new Insets(3));
		landAndBuildingBox.setAlignment(Pos.CENTER_LEFT);

		Text otherFixedLabel = new Text("Other Fixed Assets");
		otherFixedLabel.setFont(Font.font("Serif", 12));
		otherFixedLabel.setFill(Color.BLACK);

		HBox otherFixedBox = new HBox();
		otherFixedBox.getChildren().add(otherFixedLabel);
		otherFixedBox.setPadding(new Insets(3));
		otherFixedBox.setAlignment(Pos.CENTER_LEFT);

		Text insuranceDepositLabel = new Text("NCUA Insurance Deposit");
		insuranceDepositLabel.setFont(Font.font("Serif", 12));
		insuranceDepositLabel.setFill(Color.BLACK);

		HBox insuranceDepositBox = new HBox();
		insuranceDepositBox.getChildren().add(insuranceDepositLabel);
		insuranceDepositBox.setPadding(new Insets(3));
		insuranceDepositBox.setAlignment(Pos.CENTER_LEFT);

		Text intangibleAssetsLabel = new Text("Intangible Assets");
		intangibleAssetsLabel.setFont(Font.font("Serif", 12));
		intangibleAssetsLabel.setFill(Color.BLACK);

		HBox intangibleAssetsBox = new HBox();
		intangibleAssetsBox.getChildren().add(intangibleAssetsLabel);
		intangibleAssetsBox.setPadding(new Insets(3));
		intangibleAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text totalOtherAssetsLabel = new Text("Other Assets");
		totalOtherAssetsLabel.setFont(Font.font("Serif", 12));
		totalOtherAssetsLabel.setFill(Color.BLACK);

		HBox totalOtherAssetsBox = new HBox();
		totalOtherAssetsBox.getChildren().add(totalOtherAssetsLabel);
		totalOtherAssetsBox.setPadding(new Insets(3));
		totalOtherAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" -------------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalAssetsLabel = new Text("Total Assets");
		totalAssetsLabel.setFont(Font.font("Serif", 14));
		totalAssetsLabel.setFill(Color.BLACK);

		HBox totalAssetsBox = new HBox();
		totalAssetsBox.getChildren().add(totalAssetsLabel);
		totalAssetsBox.setPadding(new Insets(5));
		totalAssetsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" -------------------------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text spacer4 = new Text(" -------------------------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansGrantedYTDLabel = new Text("Loans Granted YTD");
		loansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		loansGrantedYTDLabel.setFill(Color.BLACK);

		HBox loansGrantedYTDBox = new HBox();
		loansGrantedYTDBox.getChildren().add(loansGrantedYTDLabel);
		loansGrantedYTDBox.setPadding(new Insets(3));
		loansGrantedYTDBox.setAlignment(Pos.CENTER_LEFT);

		Text palLoansGrantedYTDLabel = new Text("PAL Loans Granted YTD");
		palLoansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		palLoansGrantedYTDLabel.setFill(Color.BLACK);

		HBox palLoansGrantedYTDBox = new HBox();
		palLoansGrantedYTDBox.getChildren().add(palLoansGrantedYTDLabel);
		palLoansGrantedYTDBox.setPadding(new Insets(3));
		palLoansGrantedYTDBox.setAlignment(Pos.CENTER_LEFT);

		Text deferredEduLoansLabel = new Text("Student Loans in Deferred Status");
		deferredEduLoansLabel.setFont(Font.font("Serif", 12));
		deferredEduLoansLabel.setFill(Color.BLACK);

		HBox deferredEduLoansBox = new HBox();
		deferredEduLoansBox.getChildren().add(deferredEduLoansLabel);
		deferredEduLoansBox.setPadding(new Insets(3));
		deferredEduLoansBox.setAlignment(Pos.CENTER_LEFT);

		Text execLoansLabel = new Text("Loans to CU Officals/Executives");
		execLoansLabel.setFont(Font.font("Serif", 12));
		execLoansLabel.setFill(Color.BLACK);

		HBox execLoansBox = new HBox();
		execLoansBox.getChildren().add(execLoansLabel);
		execLoansBox.setPadding(new Insets(3));
		execLoansBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer5 = new Text(" -------------------------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text spacer6 = new Text(" -------------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text(" -------------------------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(otherAssetsBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(repoAssetsBox);
		wHeader.getChildren().add(landAndBuildingBox);
		wHeader.getChildren().add(otherFixedBox);
		wHeader.getChildren().add(insuranceDepositBox);
		wHeader.getChildren().add(intangibleAssetsBox);
		wHeader.getChildren().add(totalOtherAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansGrantedYTDBox);
		wHeader.getChildren().add(palLoansGrantedYTDBox);
		wHeader.getChildren().add(deferredEduLoansBox);
		wHeader.getChildren().add(execLoansBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(spacer7);

		return (wHeader);
	}


	private HBox buildFinancialsButtonBox(Stage primaryStage)
	{
		HBox finButtonBox = new HBox();

		Button balanceSheetButton = new Button("Balance Sheet");
		balanceSheetButton.setOnAction((event) -> 
		{
			financialsScreenNumber = 1; 
			balanceSheetScreenNumber = 1;
			start(primaryStage);
		});

		Button incomeStatementButton = new Button("Income Statement");
		incomeStatementButton.setOnAction((event) -> 
		{
			financialsScreenNumber = 2; 
			start(primaryStage);
		});

		Button assetQualityButton = new Button("Asset Quality");
		assetQualityButton.setOnAction((event) -> 
		{
			financialsScreenNumber = 3;
			start(primaryStage);
		});

		Button keyRatiosButton = new Button("Key Ratios");
		keyRatiosButton.setOnAction((event) -> 
		{
			financialsScreenNumber = 4;
			start(primaryStage);
		});

		Button demosButton = new Button("Demographics");
		demosButton.setOnAction((event) -> 
		{
			financialsScreenNumber = 5;
			start(primaryStage);
		});

		finButtonBox.getChildren().add(balanceSheetButton);
		finButtonBox.getChildren().add(incomeStatementButton);
		finButtonBox.getChildren().add(assetQualityButton);
		finButtonBox.getChildren().add(keyRatiosButton);
		finButtonBox.getChildren().add(demosButton);

		finButtonBox.setAlignment(Pos.BOTTOM_CENTER);
		finButtonBox.setSpacing(5);
		finButtonBox.setPadding(new Insets(10));

		return (finButtonBox);
	}

	private VBox buildCashAndInvestmentsLabelBox(Stage primaryStage)
	{	
		Text assetsLabel = new Text("Cash and Investments");
		assetsLabel.setFont(Font.font("Serif", 14));
		assetsLabel.setFill(Color.BLACK);

		HBox assetsBox = new HBox();
		assetsBox.getChildren().add(assetsLabel);
		assetsBox.setPadding(new Insets(5));
		assetsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer1 = new Text(" -------------------------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text cashOnHandLabel = new Text("Cash On Hand");
		cashOnHandLabel.setFont(Font.font("Serif", 12));
		cashOnHandLabel.setFill(Color.BLACK);

		HBox cashOnHandBox = new HBox();
		cashOnHandBox.getChildren().add(cashOnHandLabel);
		cashOnHandBox.setPadding(new Insets(3));
		cashOnHandBox.setAlignment(Pos.CENTER_LEFT);

		Text cashOnDepositLabel = new Text("Cash On Deposit");
		cashOnDepositLabel.setFont(Font.font("Serif", 12));
		cashOnDepositLabel.setFill(Color.BLACK);

		HBox cashOnDepositBox = new HBox();
		cashOnDepositBox.getChildren().add(cashOnDepositLabel);
		cashOnDepositBox.setPadding(new Insets(3));
		cashOnDepositBox.setAlignment(Pos.CENTER_LEFT);

		Text cashEquivalentsLabel = new Text("Cash Equivalents");
		cashEquivalentsLabel.setFont(Font.font("Serif", 12));
		cashEquivalentsLabel.setFill(Color.BLACK);

		HBox cashEquivalentsBox = new HBox();
		cashEquivalentsBox.getChildren().add(cashEquivalentsLabel);
		cashEquivalentsBox.setPadding(new Insets(3));
		cashEquivalentsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer2 = new Text(" -------------------------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text cashAndEquivsLabel = new Text("Cash And Equivalents");
		cashAndEquivsLabel.setFont(Font.font("Serif", 13));
		cashAndEquivsLabel.setFill(Color.BLACK);

		HBox cashAndEquivsBox = new HBox();
		cashAndEquivsBox.getChildren().add(cashAndEquivsLabel);
		cashAndEquivsBox.setPadding(new Insets(5));
		cashAndEquivsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer3 = new Text(" -------------------------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text govSecsLabel = new Text("Government Securities");
		govSecsLabel.setFont(Font.font("Serif", 12));
		govSecsLabel.setFill(Color.BLACK);

		HBox govSecsBox = new HBox();
		govSecsBox.getChildren().add(govSecsLabel);
		govSecsBox.setPadding(new Insets(3));
		govSecsBox.setAlignment(Pos.CENTER_LEFT);

		Text fedAgencySecsLabel = new Text("Federal Agency Securities");
		fedAgencySecsLabel.setFont(Font.font("Serif", 12));
		fedAgencySecsLabel.setFill(Color.BLACK);

		HBox fedAgencySecsBox = new HBox();
		fedAgencySecsBox.getChildren().add(fedAgencySecsLabel);
		fedAgencySecsBox.setPadding(new Insets(3));
		fedAgencySecsBox.setAlignment(Pos.CENTER_LEFT);

		Text corpCUsLabel = new Text("Corporate Credit Unions");
		corpCUsLabel.setFont(Font.font("Serif", 12));
		corpCUsLabel.setFill(Color.BLACK);

		HBox corpCUSBox = new HBox();
		corpCUSBox.getChildren().add(corpCUsLabel);
		corpCUSBox.setPadding(new Insets(3));
		corpCUSBox.setAlignment(Pos.CENTER_LEFT);

		Text bankDepositsLabel = new Text("Bank Deposits");
		bankDepositsLabel.setFont(Font.font("Serif", 12));
		bankDepositsLabel.setFill(Color.BLACK);

		HBox bankDepositsBox = new HBox();
		bankDepositsBox.getChildren().add(bankDepositsLabel);
		bankDepositsBox.setPadding(new Insets(3));
		bankDepositsBox.setAlignment(Pos.CENTER_LEFT);

		Text mutualFundsLabel = new Text("Mutual Funds");
		mutualFundsLabel.setFont(Font.font("Serif", 12));
		mutualFundsLabel.setFill(Color.BLACK);

		HBox mutualFundsBox = new HBox();
		mutualFundsBox.getChildren().add(mutualFundsLabel);
		mutualFundsBox.setPadding(new Insets(3));
		mutualFundsBox.setAlignment(Pos.CENTER_LEFT);

		Text allOtherInvestmentsLabel = new Text("All Other Investments");
		allOtherInvestmentsLabel.setFont(Font.font("Serif", 12));
		allOtherInvestmentsLabel.setFill(Color.BLACK);

		HBox allOtherInvestmentsBox = new HBox();
		allOtherInvestmentsBox.getChildren().add(allOtherInvestmentsLabel);
		allOtherInvestmentsBox.setPadding(new Insets(3));
		allOtherInvestmentsBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer4 = new Text(" -------------------------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalInvestmentsLabel = new Text("Total Investments");
		totalInvestmentsLabel.setFont(Font.font("Serif", 13));
		totalInvestmentsLabel.setFill(Color.BLACK);

		HBox totalInvestmentsBox = new HBox();
		totalInvestmentsBox.getChildren().add(totalInvestmentsLabel);
		totalInvestmentsBox.setPadding(new Insets(5));
		totalInvestmentsBox.setAlignment(Pos.BASELINE_LEFT);

		Text spacer5 = new Text(" -------------------------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text loansHeldForSaleLabel = new Text("Loans Held For Sale");
		loansHeldForSaleLabel.setFont(Font.font("Serif", 12));
		loansHeldForSaleLabel.setFill(Color.BLACK);

		HBox loansHeldForSaleBox = new HBox();
		loansHeldForSaleBox.getChildren().add(loansHeldForSaleLabel);
		loansHeldForSaleBox.setPadding(new Insets(3));
		loansHeldForSaleBox.setAlignment(Pos.CENTER_LEFT);

		Text spacer6 = new Text(" -------------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(assetsBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(cashOnHandBox);
		wHeader.getChildren().add(cashOnDepositBox);
		wHeader.getChildren().add(cashEquivalentsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(cashAndEquivsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(govSecsBox);
		wHeader.getChildren().add(fedAgencySecsBox);
		wHeader.getChildren().add(corpCUSBox);
		wHeader.getChildren().add(bankDepositsBox);
		wHeader.getChildren().add(mutualFundsBox);
		wHeader.getChildren().add(allOtherInvestmentsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalInvestmentsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(loansHeldForSaleBox);
		wHeader.getChildren().add(spacer6);

		return (wHeader);
	}

	private VBox buildDemographicsCurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" -------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text membersLabel = new Text(twoYrFinSum.getCurrMembers());
		membersLabel.setFont(Font.font("Serif", 12));
		membersLabel.setFill(Color.BLACK);

		HBox membersBox = new HBox();
		membersBox.getChildren().add(membersLabel);
		membersBox.setPadding(new Insets(3));
		membersBox.setAlignment(Pos.CENTER_RIGHT);

		Text potentialMembersLabel = new Text(twoYrFinSum.getCurrPotentialMembers());
		potentialMembersLabel.setFont(Font.font("Serif", 12));
		potentialMembersLabel.setFill(Color.BLACK);

		HBox potentialMembersBox = new HBox();
		potentialMembersBox.getChildren().add(potentialMembersLabel);
		potentialMembersBox.setPadding(new Insets(3));
		potentialMembersBox.setAlignment(Pos.CENTER_RIGHT);

		Text partTimeEmployeesLabel = new Text(twoYrFinSum.getCurrPartTimeEmployees());
		partTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		partTimeEmployeesLabel.setFill(Color.BLACK);

		HBox partTimeEmployeesBox = new HBox();
		partTimeEmployeesBox.getChildren().add(partTimeEmployeesLabel);
		partTimeEmployeesBox.setPadding(new Insets(3));
		partTimeEmployeesBox.setAlignment(Pos.CENTER_RIGHT);

		Text fullTimeEmployeesLabel = new Text(twoYrFinSum.getCurrFullTimeEmployees());
		fullTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		fullTimeEmployeesLabel.setFill(Color.BLACK);

		HBox fullTimeEmployeesBox = new HBox();
		fullTimeEmployeesBox.getChildren().add(fullTimeEmployeesLabel);
		fullTimeEmployeesBox.setPadding(new Insets(3));
		fullTimeEmployeesBox.setAlignment(Pos.CENTER_RIGHT);

		Text branchesLabel = new Text(twoYrFinSum.getCurrBranches());
		branchesLabel.setFont(Font.font("Serif", 12));
		branchesLabel.setFill(Color.BLACK);

		HBox branchesBox = new HBox();
		branchesBox.getChildren().add(branchesLabel);
		branchesBox.setPadding(new Insets(3));
		branchesBox.setAlignment(Pos.CENTER_RIGHT);

		Text fhlbLabel = new Text(twoYrFinSum.getCurrFHLBMember());
		fhlbLabel.setFont(Font.font("Serif", 12));
		fhlbLabel.setFill(Color.BLACK);

		HBox fhlbBox = new HBox();
		fhlbBox.getChildren().add(fhlbLabel);
		fhlbBox.setPadding(new Insets(3));
		fhlbBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" -------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(membersBox);
		wHeader.getChildren().add(potentialMembersBox);
		wHeader.getChildren().add(partTimeEmployeesBox);
		wHeader.getChildren().add(fullTimeEmployeesBox);
		wHeader.getChildren().add(branchesBox);
		wHeader.getChildren().add(fhlbBox);
		wHeader.getChildren().add(spacer2);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}


	private VBox buildKeyRatiosCurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text pcaNetWorthToAssetsLabel = new Text(twoYrFinSum.getCurrPCANetCapitalRatio());
		pcaNetWorthToAssetsLabel.setFont(Font.font("Serif", 12));
		pcaNetWorthToAssetsLabel.setFill(Color.BLACK);

		HBox pcaNetWorthToAssetsBox = new HBox();
		pcaNetWorthToAssetsBox.getChildren().add(pcaNetWorthToAssetsLabel);
		pcaNetWorthToAssetsBox.setPadding(new Insets(3));
		pcaNetWorthToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text pcaNetWorthClassificationLabel = new Text(twoYrFinSum.getCurrPCANetCapitalClassification());
		pcaNetWorthClassificationLabel.setFont(Font.font("Serif", 12));
		pcaNetWorthClassificationLabel.setFill(Color.BLACK);

		HBox pcaNetWorthClassificationBox = new HBox();
		pcaNetWorthClassificationBox.getChildren().add(pcaNetWorthClassificationLabel);
		pcaNetWorthClassificationBox.setPadding(new Insets(3));
		pcaNetWorthClassificationBox.setAlignment(Pos.CENTER_RIGHT);

		Text totalCapitalToAssetsLabel = new Text(twoYrFinSum.getCurrTotalCapitalToAssets());
		totalCapitalToAssetsLabel.setFont(Font.font("Serif", 12));
		totalCapitalToAssetsLabel.setFill(Color.BLACK);

		HBox totalCapitalToAssetsBox = new HBox();
		totalCapitalToAssetsBox.getChildren().add(totalCapitalToAssetsLabel);
		totalCapitalToAssetsBox.setPadding(new Insets(3));
		totalCapitalToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text netCapitalToAssetsLabel = new Text(twoYrFinSum.getCurrNetCapitalToAssets());
		netCapitalToAssetsLabel.setFont(Font.font("Serif", 12));
		netCapitalToAssetsLabel.setFill(Color.BLACK);

		HBox netCapitalToAssetsBox = new HBox();
		netCapitalToAssetsBox.getChildren().add(netCapitalToAssetsLabel);
		netCapitalToAssetsBox.setPadding(new Insets(3));
		netCapitalToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text delinquencyRateLabel = new Text(twoYrFinSum.getCurrTotalDelinquencyRate());
		delinquencyRateLabel.setFont(Font.font("Serif", 12));
		delinquencyRateLabel.setFill(Color.BLACK);

		HBox delinquencyRateBox = new HBox();
		delinquencyRateBox.getChildren().add(delinquencyRateLabel);
		delinquencyRateBox.setPadding(new Insets(3));
		delinquencyRateBox.setAlignment(Pos.CENTER_RIGHT);

		Text estDelqLoansToLoansLabel = new Text(twoYrFinSum.getCurrEstDelqToLoans());
		estDelqLoansToLoansLabel.setFont(Font.font("Serif", 12));
		estDelqLoansToLoansLabel.setFill(Color.BLACK);

		HBox estDelqLoansToLoansBox = new HBox();
		estDelqLoansToLoansBox.getChildren().add(estDelqLoansToLoansLabel);
		estDelqLoansToLoansBox.setPadding(new Insets(3));
		estDelqLoansToLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text netChargeOffLabel = new Text(twoYrFinSum.getCurrNetChargeOffRate());
		netChargeOffLabel.setFont(Font.font("Serif", 12));
		netChargeOffLabel.setFill(Color.BLACK);

		HBox netChargeOffBox = new HBox();
		netChargeOffBox.getChildren().add(netChargeOffLabel);
		netChargeOffBox.setPadding(new Insets(3));
		netChargeOffBox.setAlignment(Pos.CENTER_RIGHT);

		Text oreosToAssetsLabel = new Text(twoYrFinSum.getCurrForeclosedAndRepoAssetsToAssets());
		oreosToAssetsLabel.setFont(Font.font("Serif", 12));
		oreosToAssetsLabel.setFill(Color.BLACK);

		HBox oreosToAssetsBox = new HBox();
		oreosToAssetsBox.getChildren().add(oreosToAssetsLabel);
		oreosToAssetsBox.setPadding(new Insets(3));
		oreosToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text operatingExpenseRatioLabel = new Text(twoYrFinSum.getCurrOperatingExpenseRatio());
		operatingExpenseRatioLabel.setFont(Font.font("Serif", 12));
		operatingExpenseRatioLabel.setFill(Color.BLACK);

		HBox operatingExpenseRatioBox = new HBox();
		operatingExpenseRatioBox.getChildren().add(operatingExpenseRatioLabel);
		operatingExpenseRatioBox.setPadding(new Insets(3));
		operatingExpenseRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text roaLabel = new Text(twoYrFinSum.getCurrReturnOnAssets());
		roaLabel.setFont(Font.font("Serif", 12));
		roaLabel.setFill(Color.BLACK);

		HBox roaBox = new HBox();
		roaBox.getChildren().add(roaLabel);
		roaBox.setPadding(new Insets(3));
		roaBox.setAlignment(Pos.CENTER_RIGHT);

		Text feeIncomeRatioLabel = new Text(twoYrFinSum.getCurrFeeIncomeRatio());
		feeIncomeRatioLabel.setFont(Font.font("Serif", 12));
		feeIncomeRatioLabel.setFill(Color.BLACK);

		HBox feeIncomeRatioBox = new HBox();
		feeIncomeRatioBox.getChildren().add(feeIncomeRatioLabel);
		feeIncomeRatioBox.setPadding(new Insets(3));
		feeIncomeRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text grossSpreadLabel = new Text(twoYrFinSum.getCurrGrossSpread());
		grossSpreadLabel.setFont(Font.font("Serif", 12));
		grossSpreadLabel.setFill(Color.BLACK);

		HBox grossSpreadBox = new HBox();
		grossSpreadBox.getChildren().add(grossSpreadLabel);
		grossSpreadBox.setPadding(new Insets(3));
		grossSpreadBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansToSavingsLabel = new Text(twoYrFinSum.getCurrLoansToSavingsRatio());
		loansToSavingsLabel.setFont(Font.font("Serif", 12));
		loansToSavingsLabel.setFill(Color.BLACK);

		HBox loansToSavingsBox = new HBox();
		loansToSavingsBox.getChildren().add(loansToSavingsLabel);
		loansToSavingsBox.setPadding(new Insets(3));
		loansToSavingsBox.setAlignment(Pos.CENTER_RIGHT);

		Text borrowingsToSavingsAndEquityLabel = new Text(twoYrFinSum.getCurrBorrowingsToSavingsAndEquity());
		borrowingsToSavingsAndEquityLabel.setFont(Font.font("Serif", 12));
		borrowingsToSavingsAndEquityLabel.setFill(Color.BLACK);

		HBox borrowingsToSavingsAndEquityBox = new HBox();
		borrowingsToSavingsAndEquityBox.getChildren().add(borrowingsToSavingsAndEquityLabel);
		borrowingsToSavingsAndEquityBox.setPadding(new Insets(3));
		borrowingsToSavingsAndEquityBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text mblRatioLabel = new Text(twoYrFinSum.getCurrMBLRatio());
		mblRatioLabel.setFont(Font.font("Serif", 12));
		mblRatioLabel.setFill(Color.BLACK);

		HBox mblRatioBox = new HBox();
		mblRatioBox.getChildren().add(mblRatioLabel);
		mblRatioBox.setPadding(new Insets(3));
		mblRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text texasRatioLabel = new Text(twoYrFinSum.getCurrTexasRatio());
		texasRatioLabel.setFont(Font.font("Serif", 12));
		texasRatioLabel.setFill(Color.BLACK);

		HBox texasRatioBox = new HBox();
		texasRatioBox.getChildren().add(texasRatioLabel);
		texasRatioBox.setPadding(new Insets(3));
		texasRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text caeScoreLabel = new Text(twoYrFinSum.getCurrCAEScore());
		caeScoreLabel.setFont(Font.font("Serif", 12));
		caeScoreLabel.setFill(Color.BLACK);

		HBox caeScoreBox = new HBox();
		caeScoreBox.getChildren().add(caeScoreLabel);
		caeScoreBox.setPadding(new Insets(3));
		caeScoreBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ----------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(pcaNetWorthToAssetsBox);
		wHeader.getChildren().add(pcaNetWorthClassificationBox);
		wHeader.getChildren().add(totalCapitalToAssetsBox);
		wHeader.getChildren().add(netCapitalToAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(delinquencyRateBox);
		wHeader.getChildren().add(estDelqLoansToLoansBox);
		wHeader.getChildren().add(netChargeOffBox);
		wHeader.getChildren().add(oreosToAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(operatingExpenseRatioBox);
		wHeader.getChildren().add(roaBox);
		wHeader.getChildren().add(grossSpreadBox);
		wHeader.getChildren().add(feeIncomeRatioBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansToSavingsBox);
		wHeader.getChildren().add(borrowingsToSavingsAndEquityBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(mblRatioBox);
		wHeader.getChildren().add(texasRatioBox);
		wHeader.getChildren().add(caeScoreBox);
		wHeader.getChildren().add(spacer6);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildAssetQualityCurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text delqLoansAndLeasesLabel = new Text("");
		delqLoansAndLeasesLabel.setFont(Font.font("Serif", 14));
		delqLoansAndLeasesLabel.setFill(Color.BLACK);

		HBox delqLoansAndLeasesBox = new HBox();
		delqLoansAndLeasesBox.getChildren().add(delqLoansAndLeasesLabel);
		delqLoansAndLeasesBox.setPadding(new Insets(5));
		delqLoansAndLeasesBox.setAlignment(Pos.CENTER_RIGHT);

		Text twoToSixMoDelqLabel = new Text(twoYrFinSum.getCurr2To6MoDelq());
		twoToSixMoDelqLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqBox = new HBox();
		twoToSixMoDelqBox.getChildren().add(twoToSixMoDelqLabel);
		twoToSixMoDelqBox.setPadding(new Insets(3));
		twoToSixMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text sixToTwelveMoDelqLabel = new Text(twoYrFinSum.getCurr6To12MoDelq());
		sixToTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqBox = new HBox();
		sixToTwelveMoDelqBox.getChildren().add(sixToTwelveMoDelqLabel);
		sixToTwelveMoDelqBox.setPadding(new Insets(3));
		sixToTwelveMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text overTwelveMoDelqLabel = new Text(twoYrFinSum.getCurrOver12MoDelq());
		overTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqBox = new HBox();
		overTwelveMoDelqBox.getChildren().add(overTwelveMoDelqLabel);
		overTwelveMoDelqBox.setPadding(new Insets(3));
		overTwelveMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalDelqLabel = new Text(twoYrFinSum.getCurrTotalDelq());
		totalDelqLabel.setFont(Font.font("Serif", 12));
		totalDelqLabel.setFill(Color.BLACK);

		HBox totalDelqBox = new HBox();
		totalDelqBox.getChildren().add(totalDelqLabel);
		totalDelqBox.setPadding(new Insets(3));
		totalDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text delqLoansAndLeasesRatioLabel = new Text("");
		delqLoansAndLeasesRatioLabel.setFont(Font.font("Serif", 14));
		delqLoansAndLeasesRatioLabel.setFill(Color.BLACK);

		HBox delqLoansAndLeasesRatioBox = new HBox();
		delqLoansAndLeasesRatioBox.getChildren().add(delqLoansAndLeasesRatioLabel);
		delqLoansAndLeasesRatioBox.setPadding(new Insets(5));
		delqLoansAndLeasesRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text twoToSixMoDelqRatioLabel = new Text(twoYrFinSum.getCurr2To6MoDelinquencyRate());
		twoToSixMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqRatioLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqRatioBox = new HBox();
		twoToSixMoDelqRatioBox.getChildren().add(twoToSixMoDelqRatioLabel);
		twoToSixMoDelqRatioBox.setPadding(new Insets(3));
		twoToSixMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text sixToTwelveMoDelqRatioLabel = new Text(twoYrFinSum.getCurr6To12MoDelinquencyRate());
		sixToTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqRatioBox = new HBox();
		sixToTwelveMoDelqRatioBox.getChildren().add(sixToTwelveMoDelqRatioLabel);
		sixToTwelveMoDelqRatioBox.setPadding(new Insets(3));
		sixToTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text overTwelveMoDelqRatioLabel = new Text(twoYrFinSum.getCurrOver12MoDelinquencyRate());
		overTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqRatioBox = new HBox();
		overTwelveMoDelqRatioBox.getChildren().add(overTwelveMoDelqRatioLabel);
		overTwelveMoDelqRatioBox.setPadding(new Insets(3));
		overTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalDelqRatioLabel = new Text(twoYrFinSum.getCurrTotalDelinquencyRate());
		totalDelqRatioLabel.setFont(Font.font("Serif", 12));
		totalDelqRatioLabel.setFill(Color.BLACK);

		HBox totalDelqRatioBox = new HBox();
		totalDelqRatioBox.getChildren().add(totalDelqRatioLabel);
		totalDelqRatioBox.setPadding(new Insets(3));
		totalDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text netChargeOffsLabel = new Text(twoYrFinSum.getCurrNetChargeOffs());
		netChargeOffsLabel.setFont(Font.font("Serif", 12));
		netChargeOffsLabel.setFill(Color.BLACK);

		HBox netChargeOffsBox = new HBox();
		netChargeOffsBox.getChildren().add(netChargeOffsLabel);
		netChargeOffsBox.setPadding(new Insets(3));
		netChargeOffsBox.setAlignment(Pos.CENTER_RIGHT);

		Text netChargeOffsRatioLabel = new Text(twoYrFinSum.getCurrNetChargeOffRate());
		netChargeOffsRatioLabel.setFont(Font.font("Serif", 12));
		netChargeOffsRatioLabel.setFill(Color.BLACK);

		HBox netChargeOffsRatioBox = new HBox();
		netChargeOffsRatioBox.getChildren().add(netChargeOffsRatioLabel);
		netChargeOffsRatioBox.setPadding(new Insets(3));
		netChargeOffsRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(delqLoansAndLeasesBox);
		wHeader.getChildren().add(twoToSixMoDelqBox);
		wHeader.getChildren().add(sixToTwelveMoDelqBox);
		wHeader.getChildren().add(overTwelveMoDelqBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalDelqBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(netChargeOffsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(delqLoansAndLeasesRatioBox);
		wHeader.getChildren().add(twoToSixMoDelqRatioBox);
		wHeader.getChildren().add(sixToTwelveMoDelqRatioBox);
		wHeader.getChildren().add(overTwelveMoDelqRatioBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(totalDelqRatioBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netChargeOffsRatioBox);
		wHeader.getChildren().add(spacer7);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildIncomeStatementPage1CurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text loanAndLeaseInterestLabel = new Text(twoYrFinSum.getCurrLoanAndLeaseInterest());
		loanAndLeaseInterestLabel.setFont(Font.font("Serif", 12));
		loanAndLeaseInterestLabel.setFill(Color.BLACK);

		HBox loanAndLeaseInterestBox = new HBox();
		loanAndLeaseInterestBox.getChildren().add(loanAndLeaseInterestLabel);
		loanAndLeaseInterestBox.setPadding(new Insets(3));
		loanAndLeaseInterestBox.setAlignment(Pos.CENTER_RIGHT);

		Text rebatesLabel = new Text(twoYrFinSum.getCurrRebates());
		rebatesLabel.setFont(Font.font("Serif", 12));
		rebatesLabel.setFill(Color.BLACK);

		HBox rebatesBox = new HBox();
		rebatesBox.getChildren().add(rebatesLabel);
		rebatesBox.setPadding(new Insets(3));
		rebatesBox.setAlignment(Pos.CENTER_RIGHT);

		Text feeIncomeLabel = new Text(twoYrFinSum.getCurrFeeIncome());
		feeIncomeLabel.setFont(Font.font("Serif", 12));
		feeIncomeLabel.setFill(Color.BLACK);

		HBox feeIncomeBox = new HBox();
		feeIncomeBox.getChildren().add(feeIncomeLabel);
		feeIncomeBox.setPadding(new Insets(3));
		feeIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text investmentIncomeLabel = new Text(twoYrFinSum.getCurrInvestmentIncome());
		investmentIncomeLabel.setFont(Font.font("Serif", 12));
		investmentIncomeLabel.setFill(Color.BLACK);

		HBox investmentIncomeBox = new HBox();
		investmentIncomeBox.getChildren().add(investmentIncomeLabel);
		investmentIncomeBox.setPadding(new Insets(3));
		investmentIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherOperatingIncomeLabel = new Text(twoYrFinSum.getCurrOtherOperatingIncome());
		otherOperatingIncomeLabel.setFont(Font.font("Serif", 12));
		otherOperatingIncomeLabel.setFill(Color.BLACK);

		HBox otherOperatingIncomeBox = new HBox();
		otherOperatingIncomeBox.getChildren().add(otherOperatingIncomeLabel);
		otherOperatingIncomeBox.setPadding(new Insets(3));
		otherOperatingIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalIncomeLabel = new Text(twoYrFinSum.getCurrTotalIncome());
		totalIncomeLabel.setFont(Font.font("Serif", 12));
		totalIncomeLabel.setFill(Color.BLACK);

		HBox totalIncomeBox = new HBox();
		totalIncomeBox.getChildren().add(totalIncomeLabel);
		totalIncomeBox.setPadding(new Insets(3));
		totalIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text salariesAndBenefitsLabel = new Text(twoYrFinSum.getCurrSalariesAndBenefits());
		salariesAndBenefitsLabel.setFont(Font.font("Serif", 12));
		salariesAndBenefitsLabel.setFill(Color.BLACK);

		HBox salariesAndBenefitsBox = new HBox();
		salariesAndBenefitsBox.getChildren().add(salariesAndBenefitsLabel);
		salariesAndBenefitsBox.setPadding(new Insets(3));
		salariesAndBenefitsBox.setAlignment(Pos.CENTER_RIGHT);

		Text officeOccupancyLabel = new Text(twoYrFinSum.getCurrOfficeOccupancy());
		officeOccupancyLabel.setFont(Font.font("Serif", 12));
		officeOccupancyLabel.setFill(Color.BLACK);

		HBox officeOccupancyBox = new HBox();
		officeOccupancyBox.getChildren().add(officeOccupancyLabel);
		officeOccupancyBox.setPadding(new Insets(3));
		officeOccupancyBox.setAlignment(Pos.CENTER_RIGHT);

		Text officeOperationsLabel = new Text(twoYrFinSum.getCurrOfficeOperations());
		officeOperationsLabel.setFont(Font.font("Serif", 12));
		officeOperationsLabel.setFill(Color.BLACK);

		HBox officeOperationsBox = new HBox();
		officeOperationsBox.getChildren().add(officeOperationsLabel);
		officeOperationsBox.setPadding(new Insets(3));
		officeOperationsBox.setAlignment(Pos.CENTER_RIGHT);

		Text educationAndPromotionLabel = new Text(twoYrFinSum.getCurrEducationAndPromotion());
		educationAndPromotionLabel.setFont(Font.font("Serif", 12));
		educationAndPromotionLabel.setFill(Color.BLACK);

		HBox educationAndPromotionBox = new HBox();
		educationAndPromotionBox.getChildren().add(educationAndPromotionLabel);
		educationAndPromotionBox.setPadding(new Insets(3));
		educationAndPromotionBox.setAlignment(Pos.CENTER_RIGHT);

		Text loanServicingLabel = new Text(twoYrFinSum.getCurrLoanServicing());
		loanServicingLabel.setFont(Font.font("Serif", 12));
		loanServicingLabel.setFill(Color.BLACK);

		HBox loanServicingBox = new HBox();
		loanServicingBox.getChildren().add(loanServicingLabel);
		loanServicingBox.setPadding(new Insets(3));
		loanServicingBox.setAlignment(Pos.CENTER_RIGHT);

		Text professionalAndOutsideServicesLabel = new Text(twoYrFinSum.getCurrProfessionalAndOutsideServices());
		professionalAndOutsideServicesLabel.setFont(Font.font("Serif", 12));
		professionalAndOutsideServicesLabel.setFill(Color.BLACK);

		HBox professionalAndOutsideServicesBox = new HBox();
		professionalAndOutsideServicesBox.getChildren().add(professionalAndOutsideServicesLabel);
		professionalAndOutsideServicesBox.setPadding(new Insets(3));
		professionalAndOutsideServicesBox.setAlignment(Pos.CENTER_RIGHT);

		Text memberInsuranceLabel = new Text(twoYrFinSum.getCurrMemberInsurance());
		memberInsuranceLabel.setFont(Font.font("Serif", 12));
		memberInsuranceLabel.setFill(Color.BLACK);

		HBox memberInsuranceBox = new HBox();
		memberInsuranceBox.getChildren().add(memberInsuranceLabel);
		memberInsuranceBox.setPadding(new Insets(3));
		memberInsuranceBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherExpensesLabel = new Text(twoYrFinSum.getCurrAllOtherExpenses());
		allOtherExpensesLabel.setFont(Font.font("Serif", 12));
		allOtherExpensesLabel.setFill(Color.BLACK);

		HBox allOtherExpensesBox = new HBox();
		allOtherExpensesBox.getChildren().add(allOtherExpensesLabel);
		allOtherExpensesBox.setPadding(new Insets(3));
		allOtherExpensesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text expenseSubtotalLabel = new Text(twoYrFinSum.getCurrExpenseSubtotal());
		expenseSubtotalLabel.setFont(Font.font("Serif", 12));
		expenseSubtotalLabel.setFill(Color.BLACK);

		HBox expenseSubtotalBox = new HBox();
		expenseSubtotalBox.getChildren().add(expenseSubtotalLabel);
		expenseSubtotalBox.setPadding(new Insets(3));
		expenseSubtotalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text provisionForLoanLossLabel = new Text(twoYrFinSum.getCurrProvisionForLoanLoss());
		provisionForLoanLossLabel.setFont(Font.font("Serif", 12));
		provisionForLoanLossLabel.setFill(Color.BLACK);

		HBox provisionForLoanLossBox = new HBox();
		provisionForLoanLossBox.getChildren().add(provisionForLoanLossLabel);
		provisionForLoanLossBox.setPadding(new Insets(3));
		provisionForLoanLossBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(loanAndLeaseInterestBox);
		wHeader.getChildren().add(rebatesBox);
		wHeader.getChildren().add(feeIncomeBox);
		wHeader.getChildren().add(investmentIncomeBox);
		wHeader.getChildren().add(otherOperatingIncomeBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalIncomeBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(salariesAndBenefitsBox);
		wHeader.getChildren().add(officeOccupancyBox);
		wHeader.getChildren().add(officeOperationsBox);
		wHeader.getChildren().add(educationAndPromotionBox);
		wHeader.getChildren().add(loanServicingBox);
		wHeader.getChildren().add(professionalAndOutsideServicesBox);
		wHeader.getChildren().add(memberInsuranceBox);
		wHeader.getChildren().add(allOtherExpensesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(expenseSubtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(provisionForLoanLossBox);
		wHeader.getChildren().add(spacer6);		

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildIncomeStatementPage2CurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text expSubtotalInclProvisionsLabel = new Text(twoYrFinSum.getCurrExpSubtotalInclProvisions());
		expSubtotalInclProvisionsLabel.setFont(Font.font("Serif", 12));
		expSubtotalInclProvisionsLabel.setFill(Color.BLACK);

		HBox expSubtotalInclProvisionsBox = new HBox();
		expSubtotalInclProvisionsBox.getChildren().add(expSubtotalInclProvisionsLabel);
		expSubtotalInclProvisionsBox.setPadding(new Insets(3));
		expSubtotalInclProvisionsBox.setAlignment(Pos.CENTER_RIGHT);

		Text nonOpGainLossLabel = new Text(twoYrFinSum.getCurrNonOpGainLoss());
		nonOpGainLossLabel.setFont(Font.font("Serif", 12));
		nonOpGainLossLabel.setFill(Color.BLACK);

		HBox nonOpGainLossBox = new HBox();
		nonOpGainLossBox.getChildren().add(nonOpGainLossLabel);
		nonOpGainLossBox.setPadding(new Insets(3));
		nonOpGainLossBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text incomeBeforeDivsAndIntLabel = new Text(twoYrFinSum.getCurrIncomeBeforeDivsAndInt());
		incomeBeforeDivsAndIntLabel.setFont(Font.font("Serif", 12));
		incomeBeforeDivsAndIntLabel.setFill(Color.BLACK);

		HBox incomeBeforeDivsAndIntBox = new HBox();
		incomeBeforeDivsAndIntBox.getChildren().add(incomeBeforeDivsAndIntLabel);
		incomeBeforeDivsAndIntBox.setPadding(new Insets(3));
		incomeBeforeDivsAndIntBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text costOfFundsLabel = new Text("");
		costOfFundsLabel.setFont(Font.font("Serif", 14));
		costOfFundsLabel.setFill(Color.BLACK);

		HBox costOfFundsBox = new HBox();
		costOfFundsBox.getChildren().add(costOfFundsLabel);
		costOfFundsBox.setPadding(new Insets(5));
		costOfFundsBox.setAlignment(Pos.CENTER_RIGHT);

		Text interestOnBorrowingsLabel = new Text(twoYrFinSum.getCurrInterestOnBorrowings());
		interestOnBorrowingsLabel.setFont(Font.font("Serif", 12));
		interestOnBorrowingsLabel.setFill(Color.BLACK);

		HBox interestOnBorrowingsBox = new HBox();
		interestOnBorrowingsBox.getChildren().add(interestOnBorrowingsLabel);
		interestOnBorrowingsBox.setPadding(new Insets(3));
		interestOnBorrowingsBox.setAlignment(Pos.CENTER_RIGHT);

		Text dividendsLabel = new Text(twoYrFinSum.getCurrDividends());
		dividendsLabel.setFont(Font.font("Serif", 12));
		dividendsLabel.setFill(Color.BLACK);

		HBox dividendsBox = new HBox();
		dividendsBox.getChildren().add(dividendsLabel);
		dividendsBox.setPadding(new Insets(3));
		dividendsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text subtotalLabel = new Text(twoYrFinSum.getCurrSubtotal());
		subtotalLabel.setFont(Font.font("Serif", 12));
		subtotalLabel.setFill(Color.BLACK);

		HBox subtotalBox = new HBox();
		subtotalBox.getChildren().add(subtotalLabel);
		subtotalBox.setPadding(new Insets(3));
		subtotalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text estDistributionOfNetLabel = new Text("");
		estDistributionOfNetLabel.setFont(Font.font("Serif", 14));
		estDistributionOfNetLabel.setFill(Color.BLACK);

		HBox estDistributionOfNetBox = new HBox();
		estDistributionOfNetBox.getChildren().add(estDistributionOfNetLabel);
		estDistributionOfNetBox.setPadding(new Insets(5));
		estDistributionOfNetBox.setAlignment(Pos.CENTER_RIGHT);

		Text reserveTransferLabel = new Text(twoYrFinSum.getCurrReserveTransfer());
		reserveTransferLabel.setFont(Font.font("Serif", 12));
		reserveTransferLabel.setFill(Color.BLACK);

		HBox reserveTransferBox = new HBox();
		reserveTransferBox.getChildren().add(reserveTransferLabel);
		reserveTransferBox.setPadding(new Insets(3));
		reserveTransferBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherCapitalTransfersLabel = new Text(twoYrFinSum.getCurrOtherCapitalTransfers());
		otherCapitalTransfersLabel.setFont(Font.font("Serif", 12));
		otherCapitalTransfersLabel.setFill(Color.BLACK);

		HBox otherCapitalTransfersBox = new HBox();
		otherCapitalTransfersBox.getChildren().add(otherCapitalTransfersLabel);
		otherCapitalTransfersBox.setPadding(new Insets(3));
		otherCapitalTransfersBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text netIncomeLabel = new Text(twoYrFinSum.getCurrNetIncome());
		netIncomeLabel.setFont(Font.font("Serif", 12));
		netIncomeLabel.setFill(Color.BLACK);

		HBox netIncomeBox = new HBox();
		netIncomeBox.getChildren().add(netIncomeLabel);
		netIncomeBox.setPadding(new Insets(3));
		netIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text spacer8 = new Text(" ---------------------------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(expSubtotalInclProvisionsBox);
		wHeader.getChildren().add(nonOpGainLossBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(incomeBeforeDivsAndIntBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(costOfFundsBox);
		wHeader.getChildren().add(interestOnBorrowingsBox);
		wHeader.getChildren().add(dividendsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(subtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(estDistributionOfNetBox);
		wHeader.getChildren().add(reserveTransferBox);
		wHeader.getChildren().add(otherCapitalTransfersBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netIncomeBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(spacer8);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildIncomeStatementPage2PriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text expSubtotalInclProvisionsLabel = new Text(twoYrFinSum.getPrevExpSubtotalInclProvisions());
		expSubtotalInclProvisionsLabel.setFont(Font.font("Serif", 12));
		expSubtotalInclProvisionsLabel.setFill(Color.BLACK);

		HBox expSubtotalInclProvisionsBox = new HBox();
		expSubtotalInclProvisionsBox.getChildren().add(expSubtotalInclProvisionsLabel);
		expSubtotalInclProvisionsBox.setPadding(new Insets(3));
		expSubtotalInclProvisionsBox.setAlignment(Pos.CENTER_RIGHT);

		Text nonOpGainLossLabel = new Text(twoYrFinSum.getPrevNonOpGainLoss());
		nonOpGainLossLabel.setFont(Font.font("Serif", 12));
		nonOpGainLossLabel.setFill(Color.BLACK);

		HBox nonOpGainLossBox = new HBox();
		nonOpGainLossBox.getChildren().add(nonOpGainLossLabel);
		nonOpGainLossBox.setPadding(new Insets(3));
		nonOpGainLossBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text incomeBeforeDivsAndIntLabel = new Text(twoYrFinSum.getPrevIncomeBeforeDivsAndInt());
		incomeBeforeDivsAndIntLabel.setFont(Font.font("Serif", 12));
		incomeBeforeDivsAndIntLabel.setFill(Color.BLACK);

		HBox incomeBeforeDivsAndIntBox = new HBox();
		incomeBeforeDivsAndIntBox.getChildren().add(incomeBeforeDivsAndIntLabel);
		incomeBeforeDivsAndIntBox.setPadding(new Insets(3));
		incomeBeforeDivsAndIntBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text costOfFundsLabel = new Text("");
		costOfFundsLabel.setFont(Font.font("Serif", 14));
		costOfFundsLabel.setFill(Color.BLACK);

		HBox costOfFundsBox = new HBox();
		costOfFundsBox.getChildren().add(costOfFundsLabel);
		costOfFundsBox.setPadding(new Insets(5));
		costOfFundsBox.setAlignment(Pos.CENTER_RIGHT);

		Text interestOnBorrowingsLabel = new Text(twoYrFinSum.getPrevInterestOnBorrowings());
		interestOnBorrowingsLabel.setFont(Font.font("Serif", 12));
		interestOnBorrowingsLabel.setFill(Color.BLACK);

		HBox interestOnBorrowingsBox = new HBox();
		interestOnBorrowingsBox.getChildren().add(interestOnBorrowingsLabel);
		interestOnBorrowingsBox.setPadding(new Insets(3));
		interestOnBorrowingsBox.setAlignment(Pos.CENTER_RIGHT);

		Text dividendsLabel = new Text(twoYrFinSum.getPrevDividends());
		dividendsLabel.setFont(Font.font("Serif", 12));
		dividendsLabel.setFill(Color.BLACK);

		HBox dividendsBox = new HBox();
		dividendsBox.getChildren().add(dividendsLabel);
		dividendsBox.setPadding(new Insets(3));
		dividendsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text subtotalLabel = new Text(twoYrFinSum.getPrevSubtotal());
		subtotalLabel.setFont(Font.font("Serif", 12));
		subtotalLabel.setFill(Color.BLACK);

		HBox subtotalBox = new HBox();
		subtotalBox.getChildren().add(subtotalLabel);
		subtotalBox.setPadding(new Insets(3));
		subtotalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text estDistributionOfNetLabel = new Text("");
		estDistributionOfNetLabel.setFont(Font.font("Serif", 14));
		estDistributionOfNetLabel.setFill(Color.BLACK);

		HBox estDistributionOfNetBox = new HBox();
		estDistributionOfNetBox.getChildren().add(estDistributionOfNetLabel);
		estDistributionOfNetBox.setPadding(new Insets(5));
		estDistributionOfNetBox.setAlignment(Pos.CENTER_RIGHT);

		Text reserveTransferLabel = new Text(twoYrFinSum.getPrevReserveTransfer());
		reserveTransferLabel.setFont(Font.font("Serif", 12));
		reserveTransferLabel.setFill(Color.BLACK);

		HBox reserveTransferBox = new HBox();
		reserveTransferBox.getChildren().add(reserveTransferLabel);
		reserveTransferBox.setPadding(new Insets(3));
		reserveTransferBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherCapitalTransfersLabel = new Text(twoYrFinSum.getPrevOtherCapitalTransfers());
		otherCapitalTransfersLabel.setFont(Font.font("Serif", 12));
		otherCapitalTransfersLabel.setFill(Color.BLACK);

		HBox otherCapitalTransfersBox = new HBox();
		otherCapitalTransfersBox.getChildren().add(otherCapitalTransfersLabel);
		otherCapitalTransfersBox.setPadding(new Insets(3));
		otherCapitalTransfersBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text netIncomeLabel = new Text(twoYrFinSum.getPrevNetIncome());
		netIncomeLabel.setFont(Font.font("Serif", 12));
		netIncomeLabel.setFill(Color.BLACK);

		HBox netIncomeBox = new HBox();
		netIncomeBox.getChildren().add(netIncomeLabel);
		netIncomeBox.setPadding(new Insets(3));
		netIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text spacer8 = new Text(" ---------------------------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(expSubtotalInclProvisionsBox);
		wHeader.getChildren().add(nonOpGainLossBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(incomeBeforeDivsAndIntBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(costOfFundsBox);
		wHeader.getChildren().add(interestOnBorrowingsBox);
		wHeader.getChildren().add(dividendsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(subtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(estDistributionOfNetBox);
		wHeader.getChildren().add(reserveTransferBox);
		wHeader.getChildren().add(otherCapitalTransfersBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netIncomeBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(spacer8);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildLiabilitiesAndCapitalCurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text reverseReposLabel = new Text(twoYrFinSum.getCurrReverseRepos());
		reverseReposLabel.setFont(Font.font("Serif", 12));
		reverseReposLabel.setFill(Color.BLACK);

		HBox reverseReposBox = new HBox();
		reverseReposBox.getChildren().add(reverseReposLabel);
		reverseReposBox.setPadding(new Insets(3));
		reverseReposBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherNotesPayableLabel = new Text(twoYrFinSum.getCurrOtherNotesPayable());
		otherNotesPayableLabel.setFont(Font.font("Serif", 12));
		otherNotesPayableLabel.setFill(Color.BLACK);

		HBox otherNotesPayableBox = new HBox();
		otherNotesPayableBox.getChildren().add(otherNotesPayableLabel);
		otherNotesPayableBox.setPadding(new Insets(3));
		otherNotesPayableBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherLiabilitiesLabel = new Text(twoYrFinSum.getCurrAllOtherLiabilities());
		allOtherLiabilitiesLabel.setFont(Font.font("Serif", 12));
		allOtherLiabilitiesLabel.setFill(Color.BLACK);

		HBox allOtherLiabilitiesBox = new HBox();
		allOtherLiabilitiesBox.getChildren().add(allOtherLiabilitiesLabel);
		allOtherLiabilitiesBox.setPadding(new Insets(3));
		allOtherLiabilitiesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLiabilitiesLabel = new Text(twoYrFinSum.getCurrTotalLiabilities());
		totalLiabilitiesLabel.setFont(Font.font("Serif", 12));
		totalLiabilitiesLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesBox = new HBox();
		totalLiabilitiesBox.getChildren().add(totalLiabilitiesLabel);
		totalLiabilitiesBox.setPadding(new Insets(3));
		totalLiabilitiesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text regularSharesLabel = new Text(twoYrFinSum.getCurrRegularShares());
		regularSharesLabel.setFont(Font.font("Serif", 12));
		regularSharesLabel.setFill(Color.BLACK);

		HBox regularSharesBox = new HBox();
		regularSharesBox.getChildren().add(regularSharesLabel);
		regularSharesBox.setPadding(new Insets(3));
		regularSharesBox.setAlignment(Pos.CENTER_RIGHT);

		Text shareDraftsLabel = new Text(twoYrFinSum.getCurrShareDrafts());
		shareDraftsLabel.setFont(Font.font("Serif", 12));
		shareDraftsLabel.setFill(Color.BLACK);

		HBox shareDraftsBox = new HBox();
		shareDraftsBox.getChildren().add(shareDraftsLabel);
		shareDraftsBox.setPadding(new Insets(3));
		shareDraftsBox.setAlignment(Pos.CENTER_RIGHT);

		Text mmaLabel = new Text(twoYrFinSum.getCurrMMAs());
		mmaLabel.setFont(Font.font("Serif", 12));
		mmaLabel.setFill(Color.BLACK);

		HBox mmaBox = new HBox();
		mmaBox.getChildren().add(mmaLabel);
		mmaBox.setPadding(new Insets(3));
		mmaBox.setAlignment(Pos.CENTER_RIGHT);

		Text iraKeoghLabel = new Text(twoYrFinSum.getCurrIRAKeoghs());
		iraKeoghLabel.setFont(Font.font("Serif", 12));
		iraKeoghLabel.setFill(Color.BLACK);

		HBox iraKeoghBox = new HBox();
		iraKeoghBox.getChildren().add(iraKeoghLabel);
		iraKeoghBox.setPadding(new Insets(3));
		iraKeoghBox.setAlignment(Pos.CENTER_RIGHT);

		Text certificatesLabel = new Text(twoYrFinSum.getCurrCertificates());
		certificatesLabel.setFont(Font.font("Serif", 12));
		certificatesLabel.setFill(Color.BLACK);

		HBox certificatesBox = new HBox();
		certificatesBox.getChildren().add(certificatesLabel);
		certificatesBox.setPadding(new Insets(3));
		certificatesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalSharesAndDepositsLabel = new Text(twoYrFinSum.getCurrTotalSharesAndDeposits());
		totalSharesAndDepositsLabel.setFont(Font.font("Serif", 12));
		totalSharesAndDepositsLabel.setFill(Color.BLACK);

		HBox totalSharesAndDepositsBox = new HBox();
		totalSharesAndDepositsBox.getChildren().add(totalSharesAndDepositsLabel);
		totalSharesAndDepositsBox.setPadding(new Insets(3));
		totalSharesAndDepositsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text reservesLabel = new Text(twoYrFinSum.getCurrReserves());
		reservesLabel.setFont(Font.font("Serif", 12));
		reservesLabel.setFill(Color.BLACK);

		HBox reservesBox = new HBox();
		reservesBox.getChildren().add(reservesLabel);
		reservesBox.setPadding(new Insets(3));
		reservesBox.setAlignment(Pos.CENTER_RIGHT);

		Text undividedEarningsLabel = new Text(twoYrFinSum.getCurrUndividedEarnings());
		undividedEarningsLabel.setFont(Font.font("Serif", 12));
		undividedEarningsLabel.setFill(Color.BLACK);

		HBox undividedEarningsBox = new HBox();
		undividedEarningsBox.getChildren().add(undividedEarningsLabel);
		undividedEarningsBox.setPadding(new Insets(3));
		undividedEarningsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text totalCapitalLabel = new Text(twoYrFinSum.getCurrTotalCapital());
		totalCapitalLabel.setFont(Font.font("Serif", 12));
		totalCapitalLabel.setFill(Color.BLACK);

		HBox totalCapitalBox = new HBox();
		totalCapitalBox.getChildren().add(totalCapitalLabel);
		totalCapitalBox.setPadding(new Insets(3));
		totalCapitalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text totalLiabilitiesAndCapitalLabel = new Text(twoYrFinSum.getCurrTotalLiabilitiesAndCapital());
		totalLiabilitiesAndCapitalLabel.setFont(Font.font("Serif", 14));
		totalLiabilitiesAndCapitalLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesAndCapitalBox = new HBox();
		totalLiabilitiesAndCapitalBox.getChildren().add(totalLiabilitiesAndCapitalLabel);
		totalLiabilitiesAndCapitalBox.setPadding(new Insets(5));
		totalLiabilitiesAndCapitalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer8 = new Text(" ---------------------------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		Text spacer9 = new Text(" ---------------------------- ");
		spacer9.setFont(Font.font("Serif", 12));
		spacer9.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(reverseReposBox);
		wHeader.getChildren().add(otherNotesPayableBox);
		wHeader.getChildren().add(allOtherLiabilitiesBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLiabilitiesBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(regularSharesBox);
		wHeader.getChildren().add(shareDraftsBox);
		wHeader.getChildren().add(mmaBox);
		wHeader.getChildren().add(iraKeoghBox);
		wHeader.getChildren().add(certificatesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalSharesAndDepositsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(reservesBox);
		wHeader.getChildren().add(undividedEarningsBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(totalCapitalBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(totalLiabilitiesAndCapitalBox);
		wHeader.getChildren().add(spacer8);
		wHeader.getChildren().add(spacer9);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildDemographicsPriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" -------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text membersLabel = new Text(twoYrFinSum.getPrevMembers());
		membersLabel.setFont(Font.font("Serif", 12));
		membersLabel.setFill(Color.BLACK);

		HBox membersBox = new HBox();
		membersBox.getChildren().add(membersLabel);
		membersBox.setPadding(new Insets(3));
		membersBox.setAlignment(Pos.CENTER_RIGHT);

		Text potentialMembersLabel = new Text(twoYrFinSum.getPrevPotentialMembers());
		potentialMembersLabel.setFont(Font.font("Serif", 12));
		potentialMembersLabel.setFill(Color.BLACK);

		HBox potentialMembersBox = new HBox();
		potentialMembersBox.getChildren().add(potentialMembersLabel);
		potentialMembersBox.setPadding(new Insets(3));
		potentialMembersBox.setAlignment(Pos.CENTER_RIGHT);

		Text partTimeEmployeesLabel = new Text(twoYrFinSum.getPrevPartTimeEmployees());
		partTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		partTimeEmployeesLabel.setFill(Color.BLACK);

		HBox partTimeEmployeesBox = new HBox();
		partTimeEmployeesBox.getChildren().add(partTimeEmployeesLabel);
		partTimeEmployeesBox.setPadding(new Insets(3));
		partTimeEmployeesBox.setAlignment(Pos.CENTER_RIGHT);

		Text fullTimeEmployeesLabel = new Text(twoYrFinSum.getPrevFullTimeEmployees());
		fullTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		fullTimeEmployeesLabel.setFill(Color.BLACK);

		HBox fullTimeEmployeesBox = new HBox();
		fullTimeEmployeesBox.getChildren().add(fullTimeEmployeesLabel);
		fullTimeEmployeesBox.setPadding(new Insets(3));
		fullTimeEmployeesBox.setAlignment(Pos.CENTER_RIGHT);

		Text branchesLabel = new Text(twoYrFinSum.getPrevBranches());
		branchesLabel.setFont(Font.font("Serif", 12));
		branchesLabel.setFill(Color.BLACK);

		HBox branchesBox = new HBox();
		branchesBox.getChildren().add(branchesLabel);
		branchesBox.setPadding(new Insets(3));
		branchesBox.setAlignment(Pos.CENTER_RIGHT);

		Text fhlbLabel = new Text(twoYrFinSum.getPrevFHLBMember());
		fhlbLabel.setFont(Font.font("Serif", 12));
		fhlbLabel.setFill(Color.BLACK);

		HBox fhlbBox = new HBox();
		fhlbBox.getChildren().add(fhlbLabel);
		fhlbBox.setPadding(new Insets(3));
		fhlbBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" -------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(membersBox);
		wHeader.getChildren().add(potentialMembersBox);
		wHeader.getChildren().add(partTimeEmployeesBox);
		wHeader.getChildren().add(fullTimeEmployeesBox);
		wHeader.getChildren().add(branchesBox);
		wHeader.getChildren().add(fhlbBox);
		wHeader.getChildren().add(spacer2);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}


	private VBox buildKeyRatiosPriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text pcaNetWorthToAssetsLabel = new Text(twoYrFinSum.getPrevPCANetCapitalRatio());
		pcaNetWorthToAssetsLabel.setFont(Font.font("Serif", 12));
		pcaNetWorthToAssetsLabel.setFill(Color.BLACK);

		HBox pcaNetWorthToAssetsBox = new HBox();
		pcaNetWorthToAssetsBox.getChildren().add(pcaNetWorthToAssetsLabel);
		pcaNetWorthToAssetsBox.setPadding(new Insets(3));
		pcaNetWorthToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text pcaNetWorthClassificationLabel = new Text(twoYrFinSum.getPrevPCANetCapitalClassification());
		pcaNetWorthClassificationLabel.setFont(Font.font("Serif", 12));
		pcaNetWorthClassificationLabel.setFill(Color.BLACK);

		HBox pcaNetWorthClassificationBox = new HBox();
		pcaNetWorthClassificationBox.getChildren().add(pcaNetWorthClassificationLabel);
		pcaNetWorthClassificationBox.setPadding(new Insets(3));
		pcaNetWorthClassificationBox.setAlignment(Pos.CENTER_RIGHT);

		Text totalCapitalToAssetsLabel = new Text(twoYrFinSum.getPrevTotalCapitalToAssets());
		totalCapitalToAssetsLabel.setFont(Font.font("Serif", 12));
		totalCapitalToAssetsLabel.setFill(Color.BLACK);

		HBox totalCapitalToAssetsBox = new HBox();
		totalCapitalToAssetsBox.getChildren().add(totalCapitalToAssetsLabel);
		totalCapitalToAssetsBox.setPadding(new Insets(3));
		totalCapitalToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text netCapitalToAssetsLabel = new Text(twoYrFinSum.getPrevNetCapitalToAssets());
		netCapitalToAssetsLabel.setFont(Font.font("Serif", 12));
		netCapitalToAssetsLabel.setFill(Color.BLACK);

		HBox netCapitalToAssetsBox = new HBox();
		netCapitalToAssetsBox.getChildren().add(netCapitalToAssetsLabel);
		netCapitalToAssetsBox.setPadding(new Insets(3));
		netCapitalToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text delinquencyRateLabel = new Text(twoYrFinSum.getPrevTotalDelinquencyRate());
		delinquencyRateLabel.setFont(Font.font("Serif", 12));
		delinquencyRateLabel.setFill(Color.BLACK);

		HBox delinquencyRateBox = new HBox();
		delinquencyRateBox.getChildren().add(delinquencyRateLabel);
		delinquencyRateBox.setPadding(new Insets(3));
		delinquencyRateBox.setAlignment(Pos.CENTER_RIGHT);

		Text estDelqLoansToLoansLabel = new Text(twoYrFinSum.getPrevEstDelqToLoans());
		estDelqLoansToLoansLabel.setFont(Font.font("Serif", 12));
		estDelqLoansToLoansLabel.setFill(Color.BLACK);

		HBox estDelqLoansToLoansBox = new HBox();
		estDelqLoansToLoansBox.getChildren().add(estDelqLoansToLoansLabel);
		estDelqLoansToLoansBox.setPadding(new Insets(3));
		estDelqLoansToLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text netChargeOffLabel = new Text(twoYrFinSum.getPrevNetChargeOffRate());
		netChargeOffLabel.setFont(Font.font("Serif", 12));
		netChargeOffLabel.setFill(Color.BLACK);

		HBox netChargeOffBox = new HBox();
		netChargeOffBox.getChildren().add(netChargeOffLabel);
		netChargeOffBox.setPadding(new Insets(3));
		netChargeOffBox.setAlignment(Pos.CENTER_RIGHT);

		Text oreosToAssetsLabel = new Text(twoYrFinSum.getPrevForeclosedAndRepoAssetsToAssets());
		oreosToAssetsLabel.setFont(Font.font("Serif", 12));
		oreosToAssetsLabel.setFill(Color.BLACK);

		HBox oreosToAssetsBox = new HBox();
		oreosToAssetsBox.getChildren().add(oreosToAssetsLabel);
		oreosToAssetsBox.setPadding(new Insets(3));
		oreosToAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text operatingExpenseRatioLabel = new Text(twoYrFinSum.getPrevOperatingExpenseRatio());
		operatingExpenseRatioLabel.setFont(Font.font("Serif", 12));
		operatingExpenseRatioLabel.setFill(Color.BLACK);

		HBox operatingExpenseRatioBox = new HBox();
		operatingExpenseRatioBox.getChildren().add(operatingExpenseRatioLabel);
		operatingExpenseRatioBox.setPadding(new Insets(3));
		operatingExpenseRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text roaLabel = new Text(twoYrFinSum.getPrevReturnOnAssets());
		roaLabel.setFont(Font.font("Serif", 12));
		roaLabel.setFill(Color.BLACK);

		HBox roaBox = new HBox();
		roaBox.getChildren().add(roaLabel);
		roaBox.setPadding(new Insets(3));
		roaBox.setAlignment(Pos.CENTER_RIGHT);

		Text feeIncomeRatioLabel = new Text(twoYrFinSum.getPrevFeeIncomeRatio());
		feeIncomeRatioLabel.setFont(Font.font("Serif", 12));
		feeIncomeRatioLabel.setFill(Color.BLACK);

		HBox feeIncomeRatioBox = new HBox();
		feeIncomeRatioBox.getChildren().add(feeIncomeRatioLabel);
		feeIncomeRatioBox.setPadding(new Insets(3));
		feeIncomeRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text grossSpreadLabel = new Text(twoYrFinSum.getPrevGrossSpread());
		grossSpreadLabel.setFont(Font.font("Serif", 12));
		grossSpreadLabel.setFill(Color.BLACK);

		HBox grossSpreadBox = new HBox();
		grossSpreadBox.getChildren().add(grossSpreadLabel);
		grossSpreadBox.setPadding(new Insets(3));
		grossSpreadBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansToSavingsLabel = new Text(twoYrFinSum.getPrevLoansToSavingsRatio());
		loansToSavingsLabel.setFont(Font.font("Serif", 12));
		loansToSavingsLabel.setFill(Color.BLACK);

		HBox loansToSavingsBox = new HBox();
		loansToSavingsBox.getChildren().add(loansToSavingsLabel);
		loansToSavingsBox.setPadding(new Insets(3));
		loansToSavingsBox.setAlignment(Pos.CENTER_RIGHT);

		Text borrowingsToSavingsAndEquityLabel = new Text(twoYrFinSum.getPrevBorrowingsToSavingsAndEquity());
		borrowingsToSavingsAndEquityLabel.setFont(Font.font("Serif", 12));
		borrowingsToSavingsAndEquityLabel.setFill(Color.BLACK);

		HBox borrowingsToSavingsAndEquityBox = new HBox();
		borrowingsToSavingsAndEquityBox.getChildren().add(borrowingsToSavingsAndEquityLabel);
		borrowingsToSavingsAndEquityBox.setPadding(new Insets(3));
		borrowingsToSavingsAndEquityBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text mblRatioLabel = new Text(twoYrFinSum.getPrevMBLRatio());
		mblRatioLabel.setFont(Font.font("Serif", 12));
		mblRatioLabel.setFill(Color.BLACK);

		HBox mblRatioBox = new HBox();
		mblRatioBox.getChildren().add(mblRatioLabel);
		mblRatioBox.setPadding(new Insets(3));
		mblRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text texasRatioLabel = new Text(twoYrFinSum.getPrevTexasRatio());
		texasRatioLabel.setFont(Font.font("Serif", 12));
		texasRatioLabel.setFill(Color.BLACK);

		HBox texasRatioBox = new HBox();
		texasRatioBox.getChildren().add(texasRatioLabel);
		texasRatioBox.setPadding(new Insets(3));
		texasRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text caeScoreLabel = new Text(twoYrFinSum.getPrevCAEScore());
		caeScoreLabel.setFont(Font.font("Serif", 12));
		caeScoreLabel.setFill(Color.BLACK);

		HBox caeScoreBox = new HBox();
		caeScoreBox.getChildren().add(caeScoreLabel);
		caeScoreBox.setPadding(new Insets(3));
		caeScoreBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ----------------------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(pcaNetWorthToAssetsBox);
		wHeader.getChildren().add(pcaNetWorthClassificationBox);
		wHeader.getChildren().add(totalCapitalToAssetsBox);
		wHeader.getChildren().add(netCapitalToAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(delinquencyRateBox);
		wHeader.getChildren().add(estDelqLoansToLoansBox);
		wHeader.getChildren().add(netChargeOffBox);
		wHeader.getChildren().add(oreosToAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(operatingExpenseRatioBox);
		wHeader.getChildren().add(roaBox);
		wHeader.getChildren().add(grossSpreadBox);
		wHeader.getChildren().add(feeIncomeRatioBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansToSavingsBox);
		wHeader.getChildren().add(borrowingsToSavingsAndEquityBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(mblRatioBox);
		wHeader.getChildren().add(texasRatioBox);
		wHeader.getChildren().add(caeScoreBox);
		wHeader.getChildren().add(spacer6);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildAssetQualityPriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text delqLoansAndLeasesLabel = new Text("");
		delqLoansAndLeasesLabel.setFont(Font.font("Serif", 14));
		delqLoansAndLeasesLabel.setFill(Color.BLACK);

		HBox delqLoansAndLeasesBox = new HBox();
		delqLoansAndLeasesBox.getChildren().add(delqLoansAndLeasesLabel);
		delqLoansAndLeasesBox.setPadding(new Insets(5));
		delqLoansAndLeasesBox.setAlignment(Pos.CENTER_RIGHT);

		Text twoToSixMoDelqLabel = new Text(twoYrFinSum.getPrev2To6MoDelq());
		twoToSixMoDelqLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqBox = new HBox();
		twoToSixMoDelqBox.getChildren().add(twoToSixMoDelqLabel);
		twoToSixMoDelqBox.setPadding(new Insets(3));
		twoToSixMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text sixToTwelveMoDelqLabel = new Text(twoYrFinSum.getPrev6To12MoDelq());
		sixToTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqBox = new HBox();
		sixToTwelveMoDelqBox.getChildren().add(sixToTwelveMoDelqLabel);
		sixToTwelveMoDelqBox.setPadding(new Insets(3));
		sixToTwelveMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text overTwelveMoDelqLabel = new Text(twoYrFinSum.getPrevOver12MoDelq());
		overTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqBox = new HBox();
		overTwelveMoDelqBox.getChildren().add(overTwelveMoDelqLabel);
		overTwelveMoDelqBox.setPadding(new Insets(3));
		overTwelveMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalDelqLabel = new Text(twoYrFinSum.getPrevTotalDelq());
		totalDelqLabel.setFont(Font.font("Serif", 12));
		totalDelqLabel.setFill(Color.BLACK);

		HBox totalDelqBox = new HBox();
		totalDelqBox.getChildren().add(totalDelqLabel);
		totalDelqBox.setPadding(new Insets(3));
		totalDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text delqLoansAndLeasesRatioLabel = new Text("");
		delqLoansAndLeasesRatioLabel.setFont(Font.font("Serif", 14));
		delqLoansAndLeasesRatioLabel.setFill(Color.BLACK);

		HBox delqLoansAndLeasesRatioBox = new HBox();
		delqLoansAndLeasesRatioBox.getChildren().add(delqLoansAndLeasesRatioLabel);
		delqLoansAndLeasesRatioBox.setPadding(new Insets(5));
		delqLoansAndLeasesRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text twoToSixMoDelqRatioLabel = new Text(twoYrFinSum.getPrev2To6MoDelinquencyRate());
		twoToSixMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqRatioLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqRatioBox = new HBox();
		twoToSixMoDelqRatioBox.getChildren().add(twoToSixMoDelqRatioLabel);
		twoToSixMoDelqRatioBox.setPadding(new Insets(3));
		twoToSixMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text sixToTwelveMoDelqRatioLabel = new Text(twoYrFinSum.getPrev6To12MoDelinquencyRate());
		sixToTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqRatioBox = new HBox();
		sixToTwelveMoDelqRatioBox.getChildren().add(sixToTwelveMoDelqRatioLabel);
		sixToTwelveMoDelqRatioBox.setPadding(new Insets(3));
		sixToTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text overTwelveMoDelqRatioLabel = new Text(twoYrFinSum.getPrevOver12MoDelinquencyRate());
		overTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqRatioBox = new HBox();
		overTwelveMoDelqRatioBox.getChildren().add(overTwelveMoDelqRatioLabel);
		overTwelveMoDelqRatioBox.setPadding(new Insets(3));
		overTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalDelqRatioLabel = new Text(twoYrFinSum.getPrevTotalDelinquencyRate());
		totalDelqRatioLabel.setFont(Font.font("Serif", 12));
		totalDelqRatioLabel.setFill(Color.BLACK);

		HBox totalDelqRatioBox = new HBox();
		totalDelqRatioBox.getChildren().add(totalDelqRatioLabel);
		totalDelqRatioBox.setPadding(new Insets(3));
		totalDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text netChargeOffsLabel = new Text(twoYrFinSum.getPrevNetChargeOffs());
		netChargeOffsLabel.setFont(Font.font("Serif", 12));
		netChargeOffsLabel.setFill(Color.BLACK);

		HBox netChargeOffsBox = new HBox();
		netChargeOffsBox.getChildren().add(netChargeOffsLabel);
		netChargeOffsBox.setPadding(new Insets(3));
		netChargeOffsBox.setAlignment(Pos.CENTER_RIGHT);

		Text netChargeOffsRatioLabel = new Text(twoYrFinSum.getPrevNetChargeOffRate());
		netChargeOffsRatioLabel.setFont(Font.font("Serif", 12));
		netChargeOffsRatioLabel.setFill(Color.BLACK);

		HBox netChargeOffsRatioBox = new HBox();
		netChargeOffsRatioBox.getChildren().add(netChargeOffsRatioLabel);
		netChargeOffsRatioBox.setPadding(new Insets(3));
		netChargeOffsRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(delqLoansAndLeasesBox);
		wHeader.getChildren().add(twoToSixMoDelqBox);
		wHeader.getChildren().add(sixToTwelveMoDelqBox);
		wHeader.getChildren().add(overTwelveMoDelqBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalDelqBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(netChargeOffsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(delqLoansAndLeasesRatioBox);
		wHeader.getChildren().add(twoToSixMoDelqRatioBox);
		wHeader.getChildren().add(sixToTwelveMoDelqRatioBox);
		wHeader.getChildren().add(overTwelveMoDelqRatioBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(totalDelqRatioBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netChargeOffsRatioBox);
		wHeader.getChildren().add(spacer7);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildIncomeStatementPage1PriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text loanAndLeaseInterestLabel = new Text(twoYrFinSum.getPrevLoanAndLeaseInterest());
		loanAndLeaseInterestLabel.setFont(Font.font("Serif", 12));
		loanAndLeaseInterestLabel.setFill(Color.BLACK);

		HBox loanAndLeaseInterestBox = new HBox();
		loanAndLeaseInterestBox.getChildren().add(loanAndLeaseInterestLabel);
		loanAndLeaseInterestBox.setPadding(new Insets(3));
		loanAndLeaseInterestBox.setAlignment(Pos.CENTER_RIGHT);

		Text rebatesLabel = new Text(twoYrFinSum.getCurrRebates());
		rebatesLabel.setFont(Font.font("Serif", 12));
		rebatesLabel.setFill(Color.BLACK);

		HBox rebatesBox = new HBox();
		rebatesBox.getChildren().add(rebatesLabel);
		rebatesBox.setPadding(new Insets(3));
		rebatesBox.setAlignment(Pos.CENTER_RIGHT);

		Text feeIncomeLabel = new Text(twoYrFinSum.getPrevFeeIncome());
		feeIncomeLabel.setFont(Font.font("Serif", 12));
		feeIncomeLabel.setFill(Color.BLACK);

		HBox feeIncomeBox = new HBox();
		feeIncomeBox.getChildren().add(feeIncomeLabel);
		feeIncomeBox.setPadding(new Insets(3));
		feeIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text investmentIncomeLabel = new Text(twoYrFinSum.getPrevInvestmentIncome());
		investmentIncomeLabel.setFont(Font.font("Serif", 12));
		investmentIncomeLabel.setFill(Color.BLACK);

		HBox investmentIncomeBox = new HBox();
		investmentIncomeBox.getChildren().add(investmentIncomeLabel);
		investmentIncomeBox.setPadding(new Insets(3));
		investmentIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherOperatingIncomeLabel = new Text(twoYrFinSum.getPrevOtherOperatingIncome());
		otherOperatingIncomeLabel.setFont(Font.font("Serif", 12));
		otherOperatingIncomeLabel.setFill(Color.BLACK);

		HBox otherOperatingIncomeBox = new HBox();
		otherOperatingIncomeBox.getChildren().add(otherOperatingIncomeLabel);
		otherOperatingIncomeBox.setPadding(new Insets(3));
		otherOperatingIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalIncomeLabel = new Text(twoYrFinSum.getPrevTotalIncome());
		totalIncomeLabel.setFont(Font.font("Serif", 12));
		totalIncomeLabel.setFill(Color.BLACK);

		HBox totalIncomeBox = new HBox();
		totalIncomeBox.getChildren().add(totalIncomeLabel);
		totalIncomeBox.setPadding(new Insets(3));
		totalIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text salariesAndBenefitsLabel = new Text(twoYrFinSum.getPrevSalariesAndBenefits());
		salariesAndBenefitsLabel.setFont(Font.font("Serif", 12));
		salariesAndBenefitsLabel.setFill(Color.BLACK);

		HBox salariesAndBenefitsBox = new HBox();
		salariesAndBenefitsBox.getChildren().add(salariesAndBenefitsLabel);
		salariesAndBenefitsBox.setPadding(new Insets(3));
		salariesAndBenefitsBox.setAlignment(Pos.CENTER_RIGHT);

		Text officeOccupancyLabel = new Text(twoYrFinSum.getPrevOfficeOccupancy());
		officeOccupancyLabel.setFont(Font.font("Serif", 12));
		officeOccupancyLabel.setFill(Color.BLACK);

		HBox officeOccupancyBox = new HBox();
		officeOccupancyBox.getChildren().add(officeOccupancyLabel);
		officeOccupancyBox.setPadding(new Insets(3));
		officeOccupancyBox.setAlignment(Pos.CENTER_RIGHT);

		Text officeOperationsLabel = new Text(twoYrFinSum.getPrevOfficeOperations());
		officeOperationsLabel.setFont(Font.font("Serif", 12));
		officeOperationsLabel.setFill(Color.BLACK);

		HBox officeOperationsBox = new HBox();
		officeOperationsBox.getChildren().add(officeOperationsLabel);
		officeOperationsBox.setPadding(new Insets(3));
		officeOperationsBox.setAlignment(Pos.CENTER_RIGHT);

		Text educationAndPromotionLabel = new Text(twoYrFinSum.getPrevEducationAndPromotion());
		educationAndPromotionLabel.setFont(Font.font("Serif", 12));
		educationAndPromotionLabel.setFill(Color.BLACK);

		HBox educationAndPromotionBox = new HBox();
		educationAndPromotionBox.getChildren().add(educationAndPromotionLabel);
		educationAndPromotionBox.setPadding(new Insets(3));
		educationAndPromotionBox.setAlignment(Pos.CENTER_RIGHT);

		Text loanServicingLabel = new Text(twoYrFinSum.getPrevLoanServicing());
		loanServicingLabel.setFont(Font.font("Serif", 12));
		loanServicingLabel.setFill(Color.BLACK);

		HBox loanServicingBox = new HBox();
		loanServicingBox.getChildren().add(loanServicingLabel);
		loanServicingBox.setPadding(new Insets(3));
		loanServicingBox.setAlignment(Pos.CENTER_RIGHT);

		Text professionalAndOutsideServicesLabel = new Text(twoYrFinSum.getPrevProfessionalAndOutsideServices());
		professionalAndOutsideServicesLabel.setFont(Font.font("Serif", 12));
		professionalAndOutsideServicesLabel.setFill(Color.BLACK);

		HBox professionalAndOutsideServicesBox = new HBox();
		professionalAndOutsideServicesBox.getChildren().add(professionalAndOutsideServicesLabel);
		professionalAndOutsideServicesBox.setPadding(new Insets(3));
		professionalAndOutsideServicesBox.setAlignment(Pos.CENTER_RIGHT);

		Text memberInsuranceLabel = new Text(twoYrFinSum.getPrevMemberInsurance());
		memberInsuranceLabel.setFont(Font.font("Serif", 12));
		memberInsuranceLabel.setFill(Color.BLACK);

		HBox memberInsuranceBox = new HBox();
		memberInsuranceBox.getChildren().add(memberInsuranceLabel);
		memberInsuranceBox.setPadding(new Insets(3));
		memberInsuranceBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherExpensesLabel = new Text(twoYrFinSum.getPrevAllOtherExpenses());
		allOtherExpensesLabel.setFont(Font.font("Serif", 12));
		allOtherExpensesLabel.setFill(Color.BLACK);

		HBox allOtherExpensesBox = new HBox();
		allOtherExpensesBox.getChildren().add(allOtherExpensesLabel);
		allOtherExpensesBox.setPadding(new Insets(3));
		allOtherExpensesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text expenseSubtotalLabel = new Text(twoYrFinSum.getPrevExpenseSubtotal());
		expenseSubtotalLabel.setFont(Font.font("Serif", 12));
		expenseSubtotalLabel.setFill(Color.BLACK);

		HBox expenseSubtotalBox = new HBox();
		expenseSubtotalBox.getChildren().add(expenseSubtotalLabel);
		expenseSubtotalBox.setPadding(new Insets(3));
		expenseSubtotalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text provisionForLoanLossLabel = new Text(twoYrFinSum.getPrevProvisionForLoanLoss());
		provisionForLoanLossLabel.setFont(Font.font("Serif", 12));
		provisionForLoanLossLabel.setFill(Color.BLACK);

		HBox provisionForLoanLossBox = new HBox();
		provisionForLoanLossBox.getChildren().add(provisionForLoanLossLabel);
		provisionForLoanLossBox.setPadding(new Insets(3));
		provisionForLoanLossBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(loanAndLeaseInterestBox);
		wHeader.getChildren().add(rebatesBox);
		wHeader.getChildren().add(feeIncomeBox);
		wHeader.getChildren().add(investmentIncomeBox);
		wHeader.getChildren().add(otherOperatingIncomeBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalIncomeBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(salariesAndBenefitsBox);
		wHeader.getChildren().add(officeOccupancyBox);
		wHeader.getChildren().add(officeOperationsBox);
		wHeader.getChildren().add(educationAndPromotionBox);
		wHeader.getChildren().add(loanServicingBox);
		wHeader.getChildren().add(professionalAndOutsideServicesBox);
		wHeader.getChildren().add(memberInsuranceBox);
		wHeader.getChildren().add(allOtherExpensesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(expenseSubtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(provisionForLoanLossBox);
		wHeader.getChildren().add(spacer6);		

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildLiabilitiesAndCapitalPriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text reverseReposLabel = new Text(twoYrFinSum.getPrevReverseRepos());
		reverseReposLabel.setFont(Font.font("Serif", 12));
		reverseReposLabel.setFill(Color.BLACK);

		HBox reverseReposBox = new HBox();
		reverseReposBox.getChildren().add(reverseReposLabel);
		reverseReposBox.setPadding(new Insets(3));
		reverseReposBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherNotesPayableLabel = new Text(twoYrFinSum.getPrevOtherNotesPayable());
		otherNotesPayableLabel.setFont(Font.font("Serif", 12));
		otherNotesPayableLabel.setFill(Color.BLACK);

		HBox otherNotesPayableBox = new HBox();
		otherNotesPayableBox.getChildren().add(otherNotesPayableLabel);
		otherNotesPayableBox.setPadding(new Insets(3));
		otherNotesPayableBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherLiabilitiesLabel = new Text(twoYrFinSum.getPrevAllOtherLiabilities());
		allOtherLiabilitiesLabel.setFont(Font.font("Serif", 12));
		allOtherLiabilitiesLabel.setFill(Color.BLACK);

		HBox allOtherLiabilitiesBox = new HBox();
		allOtherLiabilitiesBox.getChildren().add(allOtherLiabilitiesLabel);
		allOtherLiabilitiesBox.setPadding(new Insets(3));
		allOtherLiabilitiesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLiabilitiesLabel = new Text(twoYrFinSum.getPrevTotalLiabilities());
		totalLiabilitiesLabel.setFont(Font.font("Serif", 12));
		totalLiabilitiesLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesBox = new HBox();
		totalLiabilitiesBox.getChildren().add(totalLiabilitiesLabel);
		totalLiabilitiesBox.setPadding(new Insets(3));
		totalLiabilitiesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text regularSharesLabel = new Text(twoYrFinSum.getPrevRegularShares());
		regularSharesLabel.setFont(Font.font("Serif", 12));
		regularSharesLabel.setFill(Color.BLACK);

		HBox regularSharesBox = new HBox();
		regularSharesBox.getChildren().add(regularSharesLabel);
		regularSharesBox.setPadding(new Insets(3));
		regularSharesBox.setAlignment(Pos.CENTER_RIGHT);

		Text shareDraftsLabel = new Text(twoYrFinSum.getPrevShareDrafts());
		shareDraftsLabel.setFont(Font.font("Serif", 12));
		shareDraftsLabel.setFill(Color.BLACK);

		HBox shareDraftsBox = new HBox();
		shareDraftsBox.getChildren().add(shareDraftsLabel);
		shareDraftsBox.setPadding(new Insets(3));
		shareDraftsBox.setAlignment(Pos.CENTER_RIGHT);

		Text mmaLabel = new Text(twoYrFinSum.getPrevMMAs());
		mmaLabel.setFont(Font.font("Serif", 12));
		mmaLabel.setFill(Color.BLACK);

		HBox mmaBox = new HBox();
		mmaBox.getChildren().add(mmaLabel);
		mmaBox.setPadding(new Insets(3));
		mmaBox.setAlignment(Pos.CENTER_RIGHT);

		Text iraKeoghLabel = new Text(twoYrFinSum.getPrevIRAKeoghs());
		iraKeoghLabel.setFont(Font.font("Serif", 12));
		iraKeoghLabel.setFill(Color.BLACK);

		HBox iraKeoghBox = new HBox();
		iraKeoghBox.getChildren().add(iraKeoghLabel);
		iraKeoghBox.setPadding(new Insets(3));
		iraKeoghBox.setAlignment(Pos.CENTER_RIGHT);

		Text certificatesLabel = new Text(twoYrFinSum.getPrevCertificates());
		certificatesLabel.setFont(Font.font("Serif", 12));
		certificatesLabel.setFill(Color.BLACK);

		HBox certificatesBox = new HBox();
		certificatesBox.getChildren().add(certificatesLabel);
		certificatesBox.setPadding(new Insets(3));
		certificatesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalSharesAndDepositsLabel = new Text(twoYrFinSum.getPrevTotalSharesAndDeposits());
		totalSharesAndDepositsLabel.setFont(Font.font("Serif", 12));
		totalSharesAndDepositsLabel.setFill(Color.BLACK);

		HBox totalSharesAndDepositsBox = new HBox();
		totalSharesAndDepositsBox.getChildren().add(totalSharesAndDepositsLabel);
		totalSharesAndDepositsBox.setPadding(new Insets(3));
		totalSharesAndDepositsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text reservesLabel = new Text(twoYrFinSum.getPrevReserves());
		reservesLabel.setFont(Font.font("Serif", 12));
		reservesLabel.setFill(Color.BLACK);

		HBox reservesBox = new HBox();
		reservesBox.getChildren().add(reservesLabel);
		reservesBox.setPadding(new Insets(3));
		reservesBox.setAlignment(Pos.CENTER_RIGHT);

		Text undividedEarningsLabel = new Text(twoYrFinSum.getPrevUndividedEarnings());
		undividedEarningsLabel.setFont(Font.font("Serif", 12));
		undividedEarningsLabel.setFill(Color.BLACK);

		HBox undividedEarningsBox = new HBox();
		undividedEarningsBox.getChildren().add(undividedEarningsLabel);
		undividedEarningsBox.setPadding(new Insets(3));
		undividedEarningsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text totalCapitalLabel = new Text(twoYrFinSum.getPrevTotalCapital());
		totalCapitalLabel.setFont(Font.font("Serif", 12));
		totalCapitalLabel.setFill(Color.BLACK);

		HBox totalCapitalBox = new HBox();
		totalCapitalBox.getChildren().add(totalCapitalLabel);
		totalCapitalBox.setPadding(new Insets(3));
		totalCapitalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text totalLiabilitiesAndCapitalLabel = new Text(twoYrFinSum.getPrevTotalLiabilitiesAndCapital());
		totalLiabilitiesAndCapitalLabel.setFont(Font.font("Serif", 14));
		totalLiabilitiesAndCapitalLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesAndCapitalBox = new HBox();
		totalLiabilitiesAndCapitalBox.getChildren().add(totalLiabilitiesAndCapitalLabel);
		totalLiabilitiesAndCapitalBox.setPadding(new Insets(5));
		totalLiabilitiesAndCapitalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer8 = new Text(" ---------------------------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		Text spacer9 = new Text(" ---------------------------- ");
		spacer9.setFont(Font.font("Serif", 12));
		spacer9.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(reverseReposBox);
		wHeader.getChildren().add(otherNotesPayableBox);
		wHeader.getChildren().add(allOtherLiabilitiesBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLiabilitiesBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(regularSharesBox);
		wHeader.getChildren().add(shareDraftsBox);
		wHeader.getChildren().add(mmaBox);
		wHeader.getChildren().add(iraKeoghBox);
		wHeader.getChildren().add(certificatesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalSharesAndDepositsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(reservesBox);
		wHeader.getChildren().add(undividedEarningsBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(totalCapitalBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(totalLiabilitiesAndCapitalBox);
		wHeader.getChildren().add(spacer8);
		wHeader.getChildren().add(spacer9);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildOtherAssetsCurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text repoAssetsLabel = new Text(twoYrFinSum.getCurrRepoAssets());
		repoAssetsLabel.setFont(Font.font("Serif", 12));
		repoAssetsLabel.setFill(Color.BLACK);

		HBox repoAssetsBox = new HBox();
		repoAssetsBox.getChildren().add(repoAssetsLabel);
		repoAssetsBox.setPadding(new Insets(3));
		repoAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text landAndBuildingLabel = new Text(twoYrFinSum.getCurrLandAndBuilding());
		landAndBuildingLabel.setFont(Font.font("Serif", 12));
		landAndBuildingLabel.setFill(Color.BLACK);

		HBox landAndBuildingBox = new HBox();
		landAndBuildingBox.getChildren().add(landAndBuildingLabel);
		landAndBuildingBox.setPadding(new Insets(3));
		landAndBuildingBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherFixedAssetsLabel = new Text(twoYrFinSum.getCurrOtherFixedAssets());
		otherFixedAssetsLabel.setFont(Font.font("Serif", 12));
		otherFixedAssetsLabel.setFill(Color.BLACK);

		HBox otherFixedAssetsBox = new HBox();
		otherFixedAssetsBox.getChildren().add(otherFixedAssetsLabel);
		otherFixedAssetsBox.setPadding(new Insets(3));
		otherFixedAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text insuranceDepositLabel = new Text(twoYrFinSum.getCurrInsuranceDeposit());
		insuranceDepositLabel.setFont(Font.font("Serif", 12));
		insuranceDepositLabel.setFill(Color.BLACK);

		HBox insuranceDepositBox = new HBox();
		insuranceDepositBox.getChildren().add(insuranceDepositLabel);
		insuranceDepositBox.setPadding(new Insets(3));
		insuranceDepositBox.setAlignment(Pos.CENTER_RIGHT);

		Text intangibleAssetsLabel = new Text(twoYrFinSum.getCurrIntangibleAssets());
		intangibleAssetsLabel.setFont(Font.font("Serif", 12));
		intangibleAssetsLabel.setFill(Color.BLACK);

		HBox intangibleAssetsBox = new HBox();
		intangibleAssetsBox.getChildren().add(intangibleAssetsLabel);
		intangibleAssetsBox.setPadding(new Insets(3));
		intangibleAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherAssetsLabel = new Text(twoYrFinSum.getCurrOtherAssets());
		allOtherAssetsLabel.setFont(Font.font("Serif", 12));
		allOtherAssetsLabel.setFill(Color.BLACK);

		HBox allOtherAssetsBox = new HBox();
		allOtherAssetsBox.getChildren().add(allOtherAssetsLabel);
		allOtherAssetsBox.setPadding(new Insets(3));
		allOtherAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalAssetsLabel = new Text(twoYrFinSum.getCurrTotalAssets());
		totalAssetsLabel.setFont(Font.font("Serif", 14));
		totalAssetsLabel.setFill(Color.BLACK);

		HBox totalAssetsBox = new HBox();
		totalAssetsBox.getChildren().add(totalAssetsLabel);
		totalAssetsBox.setPadding(new Insets(5));
		totalAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansGrantedYTDLabel = new Text(twoYrFinSum.getCurrLoansGrantedYTD());
		loansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		loansGrantedYTDLabel.setFill(Color.BLACK);

		HBox loansGrantedYTDBox = new HBox();
		loansGrantedYTDBox.getChildren().add(loansGrantedYTDLabel);
		loansGrantedYTDBox.setPadding(new Insets(3));
		loansGrantedYTDBox.setAlignment(Pos.CENTER_RIGHT);

		Text palLoansGrantedYTDLabel = new Text(twoYrFinSum.getCurrPALLoansGrantedYTD());
		palLoansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		palLoansGrantedYTDLabel.setFill(Color.BLACK);

		HBox palLoansGrantedYTDBox = new HBox();
		palLoansGrantedYTDBox.getChildren().add(palLoansGrantedYTDLabel);
		palLoansGrantedYTDBox.setPadding(new Insets(3));
		palLoansGrantedYTDBox.setAlignment(Pos.CENTER_RIGHT);

		Text deferredEduLoansLabel = new Text(twoYrFinSum.getCurrDeferredEduLoans());
		deferredEduLoansLabel.setFont(Font.font("Serif", 12));
		deferredEduLoansLabel.setFill(Color.BLACK);

		HBox deferredEduLoansBox = new HBox();
		deferredEduLoansBox.getChildren().add(deferredEduLoansLabel);
		deferredEduLoansBox.setPadding(new Insets(3));
		deferredEduLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text loansToExecsLabel = new Text(twoYrFinSum.getCurrLoansToExecs());
		loansToExecsLabel.setFont(Font.font("Serif", 12));
		loansToExecsLabel.setFill(Color.BLACK);

		HBox loansToExecsBox = new HBox();
		loansToExecsBox.getChildren().add(loansToExecsLabel);
		loansToExecsBox.setPadding(new Insets(3));
		loansToExecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(repoAssetsBox);
		wHeader.getChildren().add(landAndBuildingBox);
		wHeader.getChildren().add(otherFixedAssetsBox);
		wHeader.getChildren().add(insuranceDepositBox);
		wHeader.getChildren().add(intangibleAssetsBox);
		wHeader.getChildren().add(allOtherAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansGrantedYTDBox);
		wHeader.getChildren().add(palLoansGrantedYTDBox);
		wHeader.getChildren().add(deferredEduLoansBox);
		wHeader.getChildren().add(loansToExecsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(spacer7);


		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildOtherAssetsPriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text repoAssetsLabel = new Text(twoYrFinSum.getPrevRepoAssets());
		repoAssetsLabel.setFont(Font.font("Serif", 12));
		repoAssetsLabel.setFill(Color.BLACK);

		HBox repoAssetsBox = new HBox();
		repoAssetsBox.getChildren().add(repoAssetsLabel);
		repoAssetsBox.setPadding(new Insets(3));
		repoAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text landAndBuildingLabel = new Text(twoYrFinSum.getPrevLandAndBuilding());
		landAndBuildingLabel.setFont(Font.font("Serif", 12));
		landAndBuildingLabel.setFill(Color.BLACK);

		HBox landAndBuildingBox = new HBox();
		landAndBuildingBox.getChildren().add(landAndBuildingLabel);
		landAndBuildingBox.setPadding(new Insets(3));
		landAndBuildingBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherFixedAssetsLabel = new Text(twoYrFinSum.getPrevOtherFixedAssets());
		otherFixedAssetsLabel.setFont(Font.font("Serif", 12));
		otherFixedAssetsLabel.setFill(Color.BLACK);

		HBox otherFixedAssetsBox = new HBox();
		otherFixedAssetsBox.getChildren().add(otherFixedAssetsLabel);
		otherFixedAssetsBox.setPadding(new Insets(3));
		otherFixedAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text insuranceDepositLabel = new Text(twoYrFinSum.getPrevInsuranceDeposit());
		insuranceDepositLabel.setFont(Font.font("Serif", 12));
		insuranceDepositLabel.setFill(Color.BLACK);

		HBox insuranceDepositBox = new HBox();
		insuranceDepositBox.getChildren().add(insuranceDepositLabel);
		insuranceDepositBox.setPadding(new Insets(3));
		insuranceDepositBox.setAlignment(Pos.CENTER_RIGHT);

		Text intangibleAssetsLabel = new Text(twoYrFinSum.getPrevIntangibleAssets());
		intangibleAssetsLabel.setFont(Font.font("Serif", 12));
		intangibleAssetsLabel.setFill(Color.BLACK);

		HBox intangibleAssetsBox = new HBox();
		intangibleAssetsBox.getChildren().add(intangibleAssetsLabel);
		intangibleAssetsBox.setPadding(new Insets(3));
		intangibleAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherAssetsLabel = new Text(twoYrFinSum.getPrevOtherAssets());
		allOtherAssetsLabel.setFont(Font.font("Serif", 12));
		allOtherAssetsLabel.setFill(Color.BLACK);

		HBox allOtherAssetsBox = new HBox();
		allOtherAssetsBox.getChildren().add(allOtherAssetsLabel);
		allOtherAssetsBox.setPadding(new Insets(3));
		allOtherAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalAssetsLabel = new Text(twoYrFinSum.getPrevTotalAssets());
		totalAssetsLabel.setFont(Font.font("Serif", 14));
		totalAssetsLabel.setFill(Color.BLACK);

		HBox totalAssetsBox = new HBox();
		totalAssetsBox.getChildren().add(totalAssetsLabel);
		totalAssetsBox.setPadding(new Insets(5));
		totalAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansGrantedYTDLabel = new Text(twoYrFinSum.getPrevLoansGrantedYTD());
		loansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		loansGrantedYTDLabel.setFill(Color.BLACK);

		HBox loansGrantedYTDBox = new HBox();
		loansGrantedYTDBox.getChildren().add(loansGrantedYTDLabel);
		loansGrantedYTDBox.setPadding(new Insets(3));
		loansGrantedYTDBox.setAlignment(Pos.CENTER_RIGHT);

		Text palLoansGrantedYTDLabel = new Text(twoYrFinSum.getPrevPALLoansGrantedYTD());
		palLoansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		palLoansGrantedYTDLabel.setFill(Color.BLACK);

		HBox palLoansGrantedYTDBox = new HBox();
		palLoansGrantedYTDBox.getChildren().add(palLoansGrantedYTDLabel);
		palLoansGrantedYTDBox.setPadding(new Insets(3));
		palLoansGrantedYTDBox.setAlignment(Pos.CENTER_RIGHT);

		Text deferredEduLoansLabel = new Text(twoYrFinSum.getPrevDeferredEduLoans());
		deferredEduLoansLabel.setFont(Font.font("Serif", 12));
		deferredEduLoansLabel.setFill(Color.BLACK);

		HBox deferredEduLoansBox = new HBox();
		deferredEduLoansBox.getChildren().add(deferredEduLoansLabel);
		deferredEduLoansBox.setPadding(new Insets(3));
		deferredEduLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text loansToExecsLabel = new Text(twoYrFinSum.getPrevLoansToExecs());
		loansToExecsLabel.setFont(Font.font("Serif", 12));
		loansToExecsLabel.setFill(Color.BLACK);

		HBox loansToExecsBox = new HBox();
		loansToExecsBox.getChildren().add(loansToExecsLabel);
		loansToExecsBox.setPadding(new Insets(3));
		loansToExecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text(" ---------------------------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(repoAssetsBox);
		wHeader.getChildren().add(landAndBuildingBox);
		wHeader.getChildren().add(otherFixedAssetsBox);
		wHeader.getChildren().add(insuranceDepositBox);
		wHeader.getChildren().add(intangibleAssetsBox);
		wHeader.getChildren().add(allOtherAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansGrantedYTDBox);
		wHeader.getChildren().add(palLoansGrantedYTDBox);
		wHeader.getChildren().add(deferredEduLoansBox);
		wHeader.getChildren().add(loansToExecsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(spacer7);


		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildLoansCurrentPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text creditCardsLabel = new Text(twoYrFinSum.getCurrCreditCards());
		creditCardsLabel.setFont(Font.font("Serif", 12));
		creditCardsLabel.setFill(Color.BLACK);

		HBox creditCardsBox = new HBox();
		creditCardsBox.getChildren().add(creditCardsLabel);
		creditCardsBox.setPadding(new Insets(3));
		creditCardsBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherUnsecuredLabel = new Text(twoYrFinSum.getCurrOtherUnsecuredLoans());
		otherUnsecuredLabel.setFont(Font.font("Serif", 12));
		otherUnsecuredLabel.setFill(Color.BLACK);

		HBox otherUnsecuredBox = new HBox();
		otherUnsecuredBox.getChildren().add(otherUnsecuredLabel);
		otherUnsecuredBox.setPadding(new Insets(3));
		otherUnsecuredBox.setAlignment(Pos.CENTER_RIGHT);

		Text palLabel = new Text(twoYrFinSum.getCurrPALLoans());
		palLabel.setFont(Font.font("Serif", 12));
		palLabel.setFill(Color.BLACK);

		HBox palBox = new HBox();
		palBox.getChildren().add(palLabel);
		palBox.setPadding(new Insets(3));
		palBox.setAlignment(Pos.CENTER_RIGHT);

		Text eduLabel = new Text(twoYrFinSum.getCurrEduLoans());
		eduLabel.setFont(Font.font("Serif", 12));
		eduLabel.setFill(Color.BLACK);

		HBox eduBox = new HBox();
		eduBox.getChildren().add(eduLabel);
		eduBox.setPadding(new Insets(3));
		eduBox.setAlignment(Pos.CENTER_RIGHT);

		Text newAutoLabel = new Text(twoYrFinSum.getCurrNewAutoLoans());
		newAutoLabel.setFont(Font.font("Serif", 12));
		newAutoLabel.setFill(Color.BLACK);

		HBox newAutoBox = new HBox();
		newAutoBox.getChildren().add(newAutoLabel);
		newAutoBox.setPadding(new Insets(3));
		newAutoBox.setAlignment(Pos.CENTER_RIGHT);

		Text usedAutoLabel = new Text(twoYrFinSum.getCurrUsedAutoLoans());
		usedAutoLabel.setFont(Font.font("Serif", 12));
		usedAutoLabel.setFill(Color.BLACK);

		HBox usedAutoBox = new HBox();
		usedAutoBox.getChildren().add(usedAutoLabel);
		usedAutoBox.setPadding(new Insets(3));
		usedAutoBox.setAlignment(Pos.CENTER_RIGHT);

		Text firstMortgageLabel = new Text(twoYrFinSum.getCurrFirstMortgages());
		firstMortgageLabel.setFont(Font.font("Serif", 12));
		firstMortgageLabel.setFill(Color.BLACK);

		HBox firstMortgageBox = new HBox();
		firstMortgageBox.getChildren().add(firstMortgageLabel);
		firstMortgageBox.setPadding(new Insets(3));
		firstMortgageBox.setAlignment(Pos.CENTER_RIGHT);

		Text secondMortgageLabel = new Text(twoYrFinSum.getCurrSecondMortgages());
		secondMortgageLabel.setFont(Font.font("Serif", 12));
		secondMortgageLabel.setFill(Color.BLACK);

		HBox secondMortgageBox = new HBox();
		secondMortgageBox.getChildren().add(secondMortgageLabel);
		secondMortgageBox.setPadding(new Insets(3));
		secondMortgageBox.setAlignment(Pos.CENTER_RIGHT);

		Text leasesLabel = new Text(twoYrFinSum.getCurrLeases());
		leasesLabel.setFont(Font.font("Serif", 12));
		leasesLabel.setFill(Color.BLACK);

		HBox leasesBox = new HBox();
		leasesBox.getChildren().add(leasesLabel);
		leasesBox.setPadding(new Insets(3));
		leasesBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherLabel = new Text(twoYrFinSum.getCurrAllOtherLoans());
		allOtherLabel.setFont(Font.font("Serif", 12));
		allOtherLabel.setFill(Color.BLACK);

		HBox allOtherBox = new HBox();
		allOtherBox.getChildren().add(allOtherLabel);
		allOtherBox.setPadding(new Insets(3));
		allOtherBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLoansLabel = new Text(twoYrFinSum.getCurrTotalLoans());
		totalLoansLabel.setFont(Font.font("Serif", 13));
		totalLoansLabel.setFill(Color.BLACK);

		HBox totalLoansBox = new HBox();
		totalLoansBox.getChildren().add(totalLoansLabel);
		totalLoansBox.setPadding(new Insets(5));
		totalLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text loanLossAllowanceLabel = new Text(twoYrFinSum.getCurrLoanLossAllowance());
		loanLossAllowanceLabel.setFont(Font.font("Serif", 12));
		loanLossAllowanceLabel.setFill(Color.BLACK);

		HBox loanLossAllowanceBox = new HBox();
		loanLossAllowanceBox.getChildren().add(loanLossAllowanceLabel);
		loanLossAllowanceBox.setPadding(new Insets(3));
		loanLossAllowanceBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text mblLabel = new Text(twoYrFinSum.getCurrMBLs());
		mblLabel.setFont(Font.font("Serif", 12));
		mblLabel.setFill(Color.BLACK);

		HBox mblBox = new HBox();
		mblBox.getChildren().add(mblLabel);
		mblBox.setPadding(new Insets(3));
		mblBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(creditCardsBox);
		wHeader.getChildren().add(otherUnsecuredBox);
		wHeader.getChildren().add(palBox);
		wHeader.getChildren().add(eduBox);
		wHeader.getChildren().add(newAutoBox);
		wHeader.getChildren().add(usedAutoBox);
		wHeader.getChildren().add(firstMortgageBox);
		wHeader.getChildren().add(secondMortgageBox);
		wHeader.getChildren().add(leasesBox);
		wHeader.getChildren().add(allOtherBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLoansBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(loanLossAllowanceBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(mblBox);
		wHeader.getChildren().add(spacer5);


		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildLoansPriorPeriodBox(Stage primaryStage)
	{	
		Text periodLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text creditCardsLabel = new Text(twoYrFinSum.getPrevCreditCards());
		creditCardsLabel.setFont(Font.font("Serif", 12));
		creditCardsLabel.setFill(Color.BLACK);

		HBox creditCardsBox = new HBox();
		creditCardsBox.getChildren().add(creditCardsLabel);
		creditCardsBox.setPadding(new Insets(3));
		creditCardsBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherUnsecuredLabel = new Text(twoYrFinSum.getPrevOtherUnsecuredLoans());
		otherUnsecuredLabel.setFont(Font.font("Serif", 12));
		otherUnsecuredLabel.setFill(Color.BLACK);

		HBox otherUnsecuredBox = new HBox();
		otherUnsecuredBox.getChildren().add(otherUnsecuredLabel);
		otherUnsecuredBox.setPadding(new Insets(3));
		otherUnsecuredBox.setAlignment(Pos.CENTER_RIGHT);

		Text palLabel = new Text(twoYrFinSum.getPrevPALLoans());
		palLabel.setFont(Font.font("Serif", 12));
		palLabel.setFill(Color.BLACK);

		HBox palBox = new HBox();
		palBox.getChildren().add(palLabel);
		palBox.setPadding(new Insets(3));
		palBox.setAlignment(Pos.CENTER_RIGHT);

		Text eduLabel = new Text(twoYrFinSum.getPrevEduLoans());
		eduLabel.setFont(Font.font("Serif", 12));
		eduLabel.setFill(Color.BLACK);

		HBox eduBox = new HBox();
		eduBox.getChildren().add(eduLabel);
		eduBox.setPadding(new Insets(3));
		eduBox.setAlignment(Pos.CENTER_RIGHT);

		Text newAutoLabel = new Text(twoYrFinSum.getPrevNewAutoLoans());
		newAutoLabel.setFont(Font.font("Serif", 12));
		newAutoLabel.setFill(Color.BLACK);

		HBox newAutoBox = new HBox();
		newAutoBox.getChildren().add(newAutoLabel);
		newAutoBox.setPadding(new Insets(3));
		newAutoBox.setAlignment(Pos.CENTER_RIGHT);

		Text usedAutoLabel = new Text(twoYrFinSum.getPrevUsedAutoLoans());
		usedAutoLabel.setFont(Font.font("Serif", 12));
		usedAutoLabel.setFill(Color.BLACK);

		HBox usedAutoBox = new HBox();
		usedAutoBox.getChildren().add(usedAutoLabel);
		usedAutoBox.setPadding(new Insets(3));
		usedAutoBox.setAlignment(Pos.CENTER_RIGHT);

		Text firstMortgageLabel = new Text(twoYrFinSum.getPrevFirstMortgages());
		firstMortgageLabel.setFont(Font.font("Serif", 12));
		firstMortgageLabel.setFill(Color.BLACK);

		HBox firstMortgageBox = new HBox();
		firstMortgageBox.getChildren().add(firstMortgageLabel);
		firstMortgageBox.setPadding(new Insets(3));
		firstMortgageBox.setAlignment(Pos.CENTER_RIGHT);

		Text secondMortgageLabel = new Text(twoYrFinSum.getPrevSecondMortgages());
		secondMortgageLabel.setFont(Font.font("Serif", 12));
		secondMortgageLabel.setFill(Color.BLACK);

		HBox secondMortgageBox = new HBox();
		secondMortgageBox.getChildren().add(secondMortgageLabel);
		secondMortgageBox.setPadding(new Insets(3));
		secondMortgageBox.setAlignment(Pos.CENTER_RIGHT);

		Text leasesLabel = new Text(twoYrFinSum.getPrevLeases());
		leasesLabel.setFont(Font.font("Serif", 12));
		leasesLabel.setFill(Color.BLACK);

		HBox leasesBox = new HBox();
		leasesBox.getChildren().add(leasesLabel);
		leasesBox.setPadding(new Insets(3));
		leasesBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherLabel = new Text(twoYrFinSum.getPrevAllOtherLoans());
		allOtherLabel.setFont(Font.font("Serif", 12));
		allOtherLabel.setFill(Color.BLACK);

		HBox allOtherBox = new HBox();
		allOtherBox.getChildren().add(allOtherLabel);
		allOtherBox.setPadding(new Insets(3));
		allOtherBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLoansLabel = new Text(twoYrFinSum.getPrevTotalLoans());
		totalLoansLabel.setFont(Font.font("Serif", 13));
		totalLoansLabel.setFill(Color.BLACK);

		HBox totalLoansBox = new HBox();
		totalLoansBox.getChildren().add(totalLoansLabel);
		totalLoansBox.setPadding(new Insets(5));
		totalLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text loanLossAllowanceLabel = new Text(twoYrFinSum.getPrevLoanLossAllowance());
		loanLossAllowanceLabel.setFont(Font.font("Serif", 12));
		loanLossAllowanceLabel.setFill(Color.BLACK);

		HBox loanLossAllowanceBox = new HBox();
		loanLossAllowanceBox.getChildren().add(loanLossAllowanceLabel);
		loanLossAllowanceBox.setPadding(new Insets(3));
		loanLossAllowanceBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text mblLabel = new Text(twoYrFinSum.getPrevMBLs());
		mblLabel.setFont(Font.font("Serif", 12));
		mblLabel.setFill(Color.BLACK);

		HBox mblBox = new HBox();
		mblBox.getChildren().add(mblLabel);
		mblBox.setPadding(new Insets(3));
		mblBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(creditCardsBox);
		wHeader.getChildren().add(otherUnsecuredBox);
		wHeader.getChildren().add(palBox);
		wHeader.getChildren().add(eduBox);
		wHeader.getChildren().add(newAutoBox);
		wHeader.getChildren().add(usedAutoBox);
		wHeader.getChildren().add(firstMortgageBox);
		wHeader.getChildren().add(secondMortgageBox);
		wHeader.getChildren().add(leasesBox);
		wHeader.getChildren().add(allOtherBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLoansBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(loanLossAllowanceBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(mblBox);
		wHeader.getChildren().add(spacer5);


		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildDemographicsPCTChangeBox(Stage primaryStage)
	{	
		Text periodLabel = new Text("% Chg");
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text membersLabel = new Text(twoYrFinSum.getMembersPctChg());
		membersLabel.setFont(Font.font("Serif", 12));
		membersLabel.setFill(Color.BLACK);

		HBox membersBox = new HBox();
		membersBox.getChildren().add(membersLabel);
		membersBox.setPadding(new Insets(3));
		membersBox.setAlignment(Pos.CENTER_RIGHT);

		Text potentialMembersLabel = new Text(twoYrFinSum.getPotentialMembersPctChg());
		potentialMembersLabel.setFont(Font.font("Serif", 12));
		potentialMembersLabel.setFill(Color.BLACK);

		HBox potentialMembersBox = new HBox();
		potentialMembersBox.getChildren().add(potentialMembersLabel);
		potentialMembersBox.setPadding(new Insets(3));
		potentialMembersBox.setAlignment(Pos.CENTER_RIGHT);

		Text partTimeEmployeesLabel = new Text(twoYrFinSum.getPartTimeEmployeesPctChg());
		partTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		partTimeEmployeesLabel.setFill(Color.BLACK);

		HBox partTimeEmployeesBox = new HBox();
		partTimeEmployeesBox.getChildren().add(partTimeEmployeesLabel);
		partTimeEmployeesBox.setPadding(new Insets(3));
		partTimeEmployeesBox.setAlignment(Pos.CENTER_RIGHT);

		Text fullTimeEmployeesLabel = new Text(twoYrFinSum.getFullTimeEmployeesPctChg());
		fullTimeEmployeesLabel.setFont(Font.font("Serif", 12));
		fullTimeEmployeesLabel.setFill(Color.BLACK);

		HBox fullTimeEmployeesBox = new HBox();
		fullTimeEmployeesBox.getChildren().add(fullTimeEmployeesLabel);
		fullTimeEmployeesBox.setPadding(new Insets(3));
		fullTimeEmployeesBox.setAlignment(Pos.CENTER_RIGHT);

		Text branchesLabel = new Text(twoYrFinSum.getBranchesPctChg());
		branchesLabel.setFont(Font.font("Serif", 12));
		branchesLabel.setFill(Color.BLACK);

		HBox branchesBox = new HBox();
		branchesBox.getChildren().add(branchesLabel);
		branchesBox.setPadding(new Insets(3));
		branchesBox.setAlignment(Pos.CENTER_RIGHT);

		Text memberOfFHLBLabel = new Text("");
		memberOfFHLBLabel.setFont(Font.font("Serif", 12));
		memberOfFHLBLabel.setFill(Color.BLACK);

		HBox memberOfFHLBBox = new HBox();
		memberOfFHLBBox.getChildren().add(memberOfFHLBLabel);
		memberOfFHLBBox.setPadding(new Insets(3));
		memberOfFHLBBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(membersBox);
		wHeader.getChildren().add(potentialMembersBox);
		wHeader.getChildren().add(partTimeEmployeesBox);
		wHeader.getChildren().add(fullTimeEmployeesBox);
		wHeader.getChildren().add(branchesBox);
		wHeader.getChildren().add(memberOfFHLBBox);
		wHeader.getChildren().add(spacer2);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildAssetQualityPCTChangeBox(Stage primaryStage)
	{	
		Text periodLabel = new Text("% Chg");
		periodLabel.setFont(Font.font("Serif", 14));
		periodLabel.setFill(Color.BLACK);

		HBox periodBox = new HBox();
		periodBox.getChildren().add(periodLabel);
		periodBox.setPadding(new Insets(5));
		periodBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text delqLoansAndLeasesLabel = new Text("");
		delqLoansAndLeasesLabel.setFont(Font.font("Serif", 14));
		delqLoansAndLeasesLabel.setFill(Color.BLACK);

		HBox delqLoansAndLeasesBox = new HBox();
		delqLoansAndLeasesBox.getChildren().add(delqLoansAndLeasesLabel);
		delqLoansAndLeasesBox.setPadding(new Insets(5));
		delqLoansAndLeasesBox.setAlignment(Pos.CENTER_RIGHT);

		Text twoToSixMoDelqLabel = new Text(twoYrFinSum.get2To6MoDelqPctChg());
		twoToSixMoDelqLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqBox = new HBox();
		twoToSixMoDelqBox.getChildren().add(twoToSixMoDelqLabel);
		twoToSixMoDelqBox.setPadding(new Insets(3));
		twoToSixMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text sixToTwelveMoDelqLabel = new Text(twoYrFinSum.get6To12MoDelqPctChg());
		sixToTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqBox = new HBox();
		sixToTwelveMoDelqBox.getChildren().add(sixToTwelveMoDelqLabel);
		sixToTwelveMoDelqBox.setPadding(new Insets(3));
		sixToTwelveMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text overTwelveMoDelqLabel = new Text(twoYrFinSum.getOver12MoDelqPctChg());
		overTwelveMoDelqLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqBox = new HBox();
		overTwelveMoDelqBox.getChildren().add(overTwelveMoDelqLabel);
		overTwelveMoDelqBox.setPadding(new Insets(3));
		overTwelveMoDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalDelqLabel = new Text(twoYrFinSum.getTotalDelqPctChg());
		totalDelqLabel.setFont(Font.font("Serif", 12));
		totalDelqLabel.setFill(Color.BLACK);

		HBox totalDelqBox = new HBox();
		totalDelqBox.getChildren().add(totalDelqLabel);
		totalDelqBox.setPadding(new Insets(3));
		totalDelqBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text delqLoansAndLeasesRatioLabel = new Text("");
		delqLoansAndLeasesRatioLabel.setFont(Font.font("Serif", 14));
		delqLoansAndLeasesRatioLabel.setFill(Color.BLACK);

		HBox delqLoansAndLeasesRatioBox = new HBox();
		delqLoansAndLeasesRatioBox.getChildren().add(delqLoansAndLeasesRatioLabel);
		delqLoansAndLeasesRatioBox.setPadding(new Insets(5));
		delqLoansAndLeasesRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text twoToSixMoDelqRatioLabel = new Text("");
		twoToSixMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		twoToSixMoDelqRatioLabel.setFill(Color.BLACK);

		HBox twoToSixMoDelqRatioBox = new HBox();
		twoToSixMoDelqRatioBox.getChildren().add(twoToSixMoDelqRatioLabel);
		twoToSixMoDelqRatioBox.setPadding(new Insets(3));
		twoToSixMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text sixToTwelveMoDelqRatioLabel = new Text("");
		sixToTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		sixToTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox sixToTwelveMoDelqRatioBox = new HBox();
		sixToTwelveMoDelqRatioBox.getChildren().add(sixToTwelveMoDelqRatioLabel);
		sixToTwelveMoDelqRatioBox.setPadding(new Insets(3));
		sixToTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text overTwelveMoDelqRatioLabel = new Text("");
		overTwelveMoDelqRatioLabel.setFont(Font.font("Serif", 12));
		overTwelveMoDelqRatioLabel.setFill(Color.BLACK);

		HBox overTwelveMoDelqRatioBox = new HBox();
		overTwelveMoDelqRatioBox.getChildren().add(overTwelveMoDelqRatioLabel);
		overTwelveMoDelqRatioBox.setPadding(new Insets(3));
		overTwelveMoDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalDelqRatioLabel = new Text("");
		totalDelqRatioLabel.setFont(Font.font("Serif", 12));
		totalDelqRatioLabel.setFill(Color.BLACK);

		HBox totalDelqRatioBox = new HBox();
		totalDelqRatioBox.getChildren().add(totalDelqRatioLabel);
		totalDelqRatioBox.setPadding(new Insets(3));
		totalDelqRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text("");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text netChargeOffsLabel = new Text(twoYrFinSum.getNetChargeOffsPctChg());
		netChargeOffsLabel.setFont(Font.font("Serif", 12));
		netChargeOffsLabel.setFill(Color.BLACK);

		HBox netChargeOffsBox = new HBox();
		netChargeOffsBox.getChildren().add(netChargeOffsLabel);
		netChargeOffsBox.setPadding(new Insets(3));
		netChargeOffsBox.setAlignment(Pos.CENTER_RIGHT);

		Text netChargeOffsRatioLabel = new Text("");
		netChargeOffsRatioLabel.setFont(Font.font("Serif", 12));
		netChargeOffsRatioLabel.setFill(Color.BLACK);

		HBox netChargeOffsRatioBox = new HBox();
		netChargeOffsRatioBox.getChildren().add(netChargeOffsRatioLabel);
		netChargeOffsRatioBox.setPadding(new Insets(3));
		netChargeOffsRatioBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text("");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text("");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(periodBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(delqLoansAndLeasesBox);
		wHeader.getChildren().add(twoToSixMoDelqBox);
		wHeader.getChildren().add(sixToTwelveMoDelqBox);
		wHeader.getChildren().add(overTwelveMoDelqBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalDelqBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(netChargeOffsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(delqLoansAndLeasesRatioBox);
		wHeader.getChildren().add(twoToSixMoDelqRatioBox);
		wHeader.getChildren().add(sixToTwelveMoDelqRatioBox);
		wHeader.getChildren().add(overTwelveMoDelqRatioBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(totalDelqRatioBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netChargeOffsRatioBox);
		wHeader.getChildren().add(spacer7);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}


	private VBox buildIncomeStatementPage1PCTChangeBox(Stage primaryStage)
	{		
		Text headerLabel = new Text("% Chg");
		headerLabel.setFont(Font.font("Serif", 14));
		headerLabel.setFill(Color.BLACK);

		HBox headerBox = new HBox();
		headerBox.getChildren().add(headerLabel);
		headerBox.setPadding(new Insets(5));
		headerBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ----------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text loanAndLeaseInterestLabel = new Text(twoYrFinSum.getLoanAndLeaseInterestPctChg());
		loanAndLeaseInterestLabel.setFont(Font.font("Serif", 12));
		loanAndLeaseInterestLabel.setFill(Color.BLACK);

		HBox loanAndLeaseInterestBox = new HBox();
		loanAndLeaseInterestBox.getChildren().add(loanAndLeaseInterestLabel);
		loanAndLeaseInterestBox.setPadding(new Insets(3));
		loanAndLeaseInterestBox.setAlignment(Pos.CENTER_RIGHT);

		Text rebatesLabel = new Text(twoYrFinSum.getRebatesPctChg());
		rebatesLabel.setFont(Font.font("Serif", 12));
		rebatesLabel.setFill(Color.BLACK);

		HBox rebatesBox = new HBox();
		rebatesBox.getChildren().add(rebatesLabel);
		rebatesBox.setPadding(new Insets(3));
		rebatesBox.setAlignment(Pos.CENTER_RIGHT);

		Text feeIncomeLabel = new Text(twoYrFinSum.getFeeIncomePctChg());
		feeIncomeLabel.setFont(Font.font("Serif", 12));
		feeIncomeLabel.setFill(Color.BLACK);

		HBox feeIncomeBox = new HBox();
		feeIncomeBox.getChildren().add(feeIncomeLabel);
		feeIncomeBox.setPadding(new Insets(3));
		feeIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text investmentIncomeLabel = new Text(twoYrFinSum.getInvestmentIncomePctChg());
		investmentIncomeLabel.setFont(Font.font("Serif", 12));
		investmentIncomeLabel.setFill(Color.BLACK);

		HBox investmentIncomeBox = new HBox();
		investmentIncomeBox.getChildren().add(investmentIncomeLabel);
		investmentIncomeBox.setPadding(new Insets(3));
		investmentIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherOperatingIncomeLabel = new Text(twoYrFinSum.getOtherOperatingIncomePctChg());
		otherOperatingIncomeLabel.setFont(Font.font("Serif", 12));
		otherOperatingIncomeLabel.setFill(Color.BLACK);

		HBox otherOperatingIncomeBox = new HBox();
		otherOperatingIncomeBox.getChildren().add(otherOperatingIncomeLabel);
		otherOperatingIncomeBox.setPadding(new Insets(3));
		otherOperatingIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalIncomeLabel = new Text(twoYrFinSum.getTotalIncomePctChg());
		totalIncomeLabel.setFont(Font.font("Serif", 12));
		totalIncomeLabel.setFill(Color.BLACK);

		HBox totalIncomeBox = new HBox();
		totalIncomeBox.getChildren().add(totalIncomeLabel);
		totalIncomeBox.setPadding(new Insets(3));
		totalIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text salariesAndBenefitsLabel = new Text(twoYrFinSum.getSalariesAndBenefitsPctChg());
		salariesAndBenefitsLabel.setFont(Font.font("Serif", 12));
		salariesAndBenefitsLabel.setFill(Color.BLACK);

		HBox salariesAndBenefitsBox = new HBox();
		salariesAndBenefitsBox.getChildren().add(salariesAndBenefitsLabel);
		salariesAndBenefitsBox.setPadding(new Insets(3));
		salariesAndBenefitsBox.setAlignment(Pos.CENTER_RIGHT);

		Text officeOccupancyLabel = new Text(twoYrFinSum.getOfficeOccupancyPctChg());
		officeOccupancyLabel.setFont(Font.font("Serif", 12));
		officeOccupancyLabel.setFill(Color.BLACK);

		HBox officeOccupancyBox = new HBox();
		officeOccupancyBox.getChildren().add(officeOccupancyLabel);
		officeOccupancyBox.setPadding(new Insets(3));
		officeOccupancyBox.setAlignment(Pos.CENTER_RIGHT);

		Text officeOperationsLabel = new Text(twoYrFinSum.getOfficeOperationsPctChg());
		officeOperationsLabel.setFont(Font.font("Serif", 12));
		officeOperationsLabel.setFill(Color.BLACK);

		HBox officeOperationsBox = new HBox();
		officeOperationsBox.getChildren().add(officeOperationsLabel);
		officeOperationsBox.setPadding(new Insets(3));
		officeOperationsBox.setAlignment(Pos.CENTER_RIGHT);

		Text educationAndPromotionLabel = new Text(twoYrFinSum.getEducationAndPromotionPctChg());
		educationAndPromotionLabel.setFont(Font.font("Serif", 12));
		educationAndPromotionLabel.setFill(Color.BLACK);

		HBox educationAndPromotionBox = new HBox();
		educationAndPromotionBox.getChildren().add(educationAndPromotionLabel);
		educationAndPromotionBox.setPadding(new Insets(3));
		educationAndPromotionBox.setAlignment(Pos.CENTER_RIGHT);

		Text loanServicingLabel = new Text(twoYrFinSum.getLoanServicingPctChg());
		loanServicingLabel.setFont(Font.font("Serif", 12));
		loanServicingLabel.setFill(Color.BLACK);

		HBox loanServicingBox = new HBox();
		loanServicingBox.getChildren().add(loanServicingLabel);
		loanServicingBox.setPadding(new Insets(3));
		loanServicingBox.setAlignment(Pos.CENTER_RIGHT);

		Text professionalAndOutsideServicesLabel = new Text(twoYrFinSum.getProfessionalAndOutsideServicesPctChg());
		professionalAndOutsideServicesLabel.setFont(Font.font("Serif", 12));
		professionalAndOutsideServicesLabel.setFill(Color.BLACK);

		HBox professionalAndOutsideServicesBox = new HBox();
		professionalAndOutsideServicesBox.getChildren().add(professionalAndOutsideServicesLabel);
		professionalAndOutsideServicesBox.setPadding(new Insets(3));
		professionalAndOutsideServicesBox.setAlignment(Pos.CENTER_RIGHT);

		Text memberInsuranceLabel = new Text(twoYrFinSum.getMemberInsurancePctChg());
		memberInsuranceLabel.setFont(Font.font("Serif", 12));
		memberInsuranceLabel.setFill(Color.BLACK);

		HBox memberInsuranceBox = new HBox();
		memberInsuranceBox.getChildren().add(memberInsuranceLabel);
		memberInsuranceBox.setPadding(new Insets(3));
		memberInsuranceBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherExpensesLabel = new Text(twoYrFinSum.getAllOtherExpensesPctChg());
		allOtherExpensesLabel.setFont(Font.font("Serif", 12));
		allOtherExpensesLabel.setFill(Color.BLACK);

		HBox allOtherExpensesBox = new HBox();
		allOtherExpensesBox.getChildren().add(allOtherExpensesLabel);
		allOtherExpensesBox.setPadding(new Insets(3));
		allOtherExpensesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text expenseSubtotalLabel = new Text(twoYrFinSum.getExpenseSubtotalPctChg());
		expenseSubtotalLabel.setFont(Font.font("Serif", 12));
		expenseSubtotalLabel.setFill(Color.BLACK);

		HBox expenseSubtotalBox = new HBox();
		expenseSubtotalBox.getChildren().add(expenseSubtotalLabel);
		expenseSubtotalBox.setPadding(new Insets(3));
		expenseSubtotalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text provisionForLoanLossLabel = new Text(twoYrFinSum.getProvisionForLoanLossPctChg());
		provisionForLoanLossLabel.setFont(Font.font("Serif", 12));
		provisionForLoanLossLabel.setFill(Color.BLACK);

		HBox provisionForLoanLossBox = new HBox();
		provisionForLoanLossBox.getChildren().add(provisionForLoanLossLabel);
		provisionForLoanLossBox.setPadding(new Insets(3));
		provisionForLoanLossBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ----------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(headerBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(loanAndLeaseInterestBox);
		wHeader.getChildren().add(rebatesBox);
		wHeader.getChildren().add(feeIncomeBox);
		wHeader.getChildren().add(investmentIncomeBox);
		wHeader.getChildren().add(otherOperatingIncomeBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalIncomeBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(salariesAndBenefitsBox);
		wHeader.getChildren().add(officeOccupancyBox);
		wHeader.getChildren().add(officeOperationsBox);
		wHeader.getChildren().add(educationAndPromotionBox);
		wHeader.getChildren().add(loanServicingBox);
		wHeader.getChildren().add(professionalAndOutsideServicesBox);
		wHeader.getChildren().add(memberInsuranceBox);
		wHeader.getChildren().add(allOtherExpensesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(expenseSubtotalBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(provisionForLoanLossBox);
		wHeader.getChildren().add(spacer6);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildIncomeStatementPage2PCTChangeBox(Stage primaryStage)
	{		
		Text headerLabel = new Text("% Chg");
		headerLabel.setFont(Font.font("Serif", 14));
		headerLabel.setFill(Color.BLACK);

		HBox headerBox = new HBox();
		headerBox.getChildren().add(headerLabel);
		headerBox.setPadding(new Insets(5));
		headerBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ----------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text expSubtotalInclProvisionsLabel = new Text(twoYrFinSum.getExpSubtotalInclProvisionsPctChg());
		expSubtotalInclProvisionsLabel.setFont(Font.font("Serif", 12));
		expSubtotalInclProvisionsLabel.setFill(Color.BLACK);

		HBox expSubtotalInclProvisionsBox = new HBox();
		expSubtotalInclProvisionsBox.getChildren().add(expSubtotalInclProvisionsLabel);
		expSubtotalInclProvisionsBox.setPadding(new Insets(3));
		expSubtotalInclProvisionsBox.setAlignment(Pos.CENTER_RIGHT);

		Text nonOpGainLossLabel = new Text(twoYrFinSum.getNonOpGainLossPctChg());
		nonOpGainLossLabel.setFont(Font.font("Serif", 12));
		nonOpGainLossLabel.setFill(Color.BLACK);

		HBox nonOpGainLossBox = new HBox();
		nonOpGainLossBox.getChildren().add(nonOpGainLossLabel);
		nonOpGainLossBox.setPadding(new Insets(3));
		nonOpGainLossBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text incomeBeforeDivsAndIntLabel = new Text(twoYrFinSum.getIncomeBeforeDivsAndIntPctChg());
		incomeBeforeDivsAndIntLabel.setFont(Font.font("Serif", 12));
		incomeBeforeDivsAndIntLabel.setFill(Color.BLACK);

		HBox incomeBeforeDivsAndIntBox = new HBox();
		incomeBeforeDivsAndIntBox.getChildren().add(incomeBeforeDivsAndIntLabel);
		incomeBeforeDivsAndIntBox.setPadding(new Insets(3));
		incomeBeforeDivsAndIntBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text costOfFundsLabel = new Text("");
		costOfFundsLabel.setFont(Font.font("Serif", 14));
		costOfFundsLabel.setFill(Color.BLACK);

		HBox costOfFundsBox = new HBox();
		costOfFundsBox.getChildren().add(costOfFundsLabel);
		costOfFundsBox.setPadding(new Insets(5));
		costOfFundsBox.setAlignment(Pos.CENTER_RIGHT);

		Text interestOnBorrowingsLabel = new Text(twoYrFinSum.getInterestOnBorrowingsPctChg());
		interestOnBorrowingsLabel.setFont(Font.font("Serif", 12));
		interestOnBorrowingsLabel.setFill(Color.BLACK);

		HBox interestOnBorrowingsBox = new HBox();
		interestOnBorrowingsBox.getChildren().add(interestOnBorrowingsLabel);
		interestOnBorrowingsBox.setPadding(new Insets(3));
		interestOnBorrowingsBox.setAlignment(Pos.CENTER_RIGHT);

		Text dividendsLabel = new Text(twoYrFinSum.getDividendsPctChg());
		dividendsLabel.setFont(Font.font("Serif", 12));
		dividendsLabel.setFill(Color.BLACK);

		HBox dividendsBox = new HBox();
		dividendsBox.getChildren().add(dividendsLabel);
		dividendsBox.setPadding(new Insets(3));
		dividendsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text subtotalLabel = new Text(twoYrFinSum.getSubtotalPctChg());
		subtotalLabel.setFont(Font.font("Serif", 12));
		subtotalLabel.setFill(Color.BLACK);

		HBox subtotalBox = new HBox();
		subtotalBox.getChildren().add(subtotalLabel);
		subtotalBox.setPadding(new Insets(3));
		subtotalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text estDistOfNetLabel = new Text("");
		estDistOfNetLabel.setFont(Font.font("Serif", 14));
		estDistOfNetLabel.setFill(Color.BLACK);

		HBox estDistOfNetBox = new HBox();
		estDistOfNetBox.getChildren().add(estDistOfNetLabel);
		estDistOfNetBox.setPadding(new Insets(5));
		estDistOfNetBox.setAlignment(Pos.CENTER_RIGHT);

		Text reserveTransferLabel = new Text(twoYrFinSum.getReserveTransferPctChg());
		reserveTransferLabel.setFont(Font.font("Serif", 12));
		reserveTransferLabel.setFill(Color.BLACK);

		HBox reserveTransferBox = new HBox();
		reserveTransferBox.getChildren().add(reserveTransferLabel);
		reserveTransferBox.setPadding(new Insets(3));
		reserveTransferBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherCapitalTransfersLabel = new Text(twoYrFinSum.getOtherCapitalTransfersPctChg());
		otherCapitalTransfersLabel.setFont(Font.font("Serif", 12));
		otherCapitalTransfersLabel.setFill(Color.BLACK);

		HBox otherCapitalTransfersBox = new HBox();
		otherCapitalTransfersBox.getChildren().add(otherCapitalTransfersLabel);
		otherCapitalTransfersBox.setPadding(new Insets(3));
		otherCapitalTransfersBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ----------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text netIncomeLabel = new Text(twoYrFinSum.getNetIncomePctChg());
		netIncomeLabel.setFont(Font.font("Serif", 12));
		netIncomeLabel.setFill(Color.BLACK);

		HBox netIncomeBox = new HBox();
		netIncomeBox.getChildren().add(netIncomeLabel);
		netIncomeBox.setPadding(new Insets(3));
		netIncomeBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer7 = new Text(" ----------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text spacer8 = new Text(" ----------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(headerBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(expSubtotalInclProvisionsBox);
		wHeader.getChildren().add(nonOpGainLossBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(incomeBeforeDivsAndIntBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(costOfFundsBox);
		wHeader.getChildren().add(interestOnBorrowingsBox);
		wHeader.getChildren().add(dividendsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(subtotalBox);
		wHeader.getChildren().add(spacer5);		
		wHeader.getChildren().add(estDistOfNetBox);
		wHeader.getChildren().add(reserveTransferBox);
		wHeader.getChildren().add(otherCapitalTransfersBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(netIncomeBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(spacer8);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildLiabilitiesAndCapitalPCTChangeBox(Stage primaryStage)
	{		
		Text headerLabel = new Text("% Chg");
		headerLabel.setFont(Font.font("Serif", 14));
		headerLabel.setFill(Color.BLACK);

		HBox headerBox = new HBox();
		headerBox.getChildren().add(headerLabel);
		headerBox.setPadding(new Insets(5));
		headerBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ----------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text reverseReposLabel = new Text(twoYrFinSum.getReverseReposPctChg());
		reverseReposLabel.setFont(Font.font("Serif", 12));
		reverseReposLabel.setFill(Color.BLACK);

		HBox reverseReposBox = new HBox();
		reverseReposBox.getChildren().add(reverseReposLabel);
		reverseReposBox.setPadding(new Insets(3));
		reverseReposBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherNotesPayableLabel = new Text(twoYrFinSum.getOtherNotesPayablePctChg());
		otherNotesPayableLabel.setFont(Font.font("Serif", 12));
		otherNotesPayableLabel.setFill(Color.BLACK);

		HBox otherNotesPayableBox = new HBox();
		otherNotesPayableBox.getChildren().add(otherNotesPayableLabel);
		otherNotesPayableBox.setPadding(new Insets(3));
		otherNotesPayableBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherLiabilitiesLabel = new Text(twoYrFinSum.getAllOtherLiabilitiesPctChg());
		allOtherLiabilitiesLabel.setFont(Font.font("Serif", 12));
		allOtherLiabilitiesLabel.setFill(Color.BLACK);

		HBox allOtherLiabilitiesBox = new HBox();
		allOtherLiabilitiesBox.getChildren().add(allOtherLiabilitiesLabel);
		allOtherLiabilitiesBox.setPadding(new Insets(3));
		allOtherLiabilitiesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLiabilitiesLabel = new Text(twoYrFinSum.getTotalLiabilitiesPctChg());
		totalLiabilitiesLabel.setFont(Font.font("Serif", 12));
		totalLiabilitiesLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesBox = new HBox();
		totalLiabilitiesBox.getChildren().add(totalLiabilitiesLabel);
		totalLiabilitiesBox.setPadding(new Insets(3));
		totalLiabilitiesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text regularSharesLabel = new Text(twoYrFinSum.getRegularSharesPctChg());
		regularSharesLabel.setFont(Font.font("Serif", 12));
		regularSharesLabel.setFill(Color.BLACK);

		HBox regularSharesBox = new HBox();
		regularSharesBox.getChildren().add(regularSharesLabel);
		regularSharesBox.setPadding(new Insets(3));
		regularSharesBox.setAlignment(Pos.CENTER_RIGHT);

		Text shareDraftsLabel = new Text(twoYrFinSum.getShareDraftsPctChg());
		shareDraftsLabel.setFont(Font.font("Serif", 12));
		shareDraftsLabel.setFill(Color.BLACK);

		HBox shareDraftsBox = new HBox();
		shareDraftsBox.getChildren().add(shareDraftsLabel);
		shareDraftsBox.setPadding(new Insets(3));
		shareDraftsBox.setAlignment(Pos.CENTER_RIGHT);

		Text mmaLabel = new Text(twoYrFinSum.getMMAsPctChg());
		mmaLabel.setFont(Font.font("Serif", 12));
		mmaLabel.setFill(Color.BLACK);

		HBox mmaBox = new HBox();
		mmaBox.getChildren().add(mmaLabel);
		mmaBox.setPadding(new Insets(3));
		mmaBox.setAlignment(Pos.CENTER_RIGHT);

		Text iraKeoghLabel = new Text(twoYrFinSum.getIRAKeoghsPctChg());
		iraKeoghLabel.setFont(Font.font("Serif", 12));
		iraKeoghLabel.setFill(Color.BLACK);

		HBox iraKeoghBox = new HBox();
		iraKeoghBox.getChildren().add(iraKeoghLabel);
		iraKeoghBox.setPadding(new Insets(3));
		iraKeoghBox.setAlignment(Pos.CENTER_RIGHT);

		Text certificatesLabel = new Text(twoYrFinSum.getCertificatesPctChg());
		certificatesLabel.setFont(Font.font("Serif", 12));
		certificatesLabel.setFill(Color.BLACK);

		HBox certificatesBox = new HBox();
		certificatesBox.getChildren().add(certificatesLabel);
		certificatesBox.setPadding(new Insets(3));
		certificatesBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalSharesAndDepositsLabel = new Text(twoYrFinSum.getTotalSharesAndDepositsPctChg());
		totalSharesAndDepositsLabel.setFont(Font.font("Serif", 12));
		totalSharesAndDepositsLabel.setFill(Color.BLACK);

		HBox totalSharesAndDepositsBox = new HBox();
		totalSharesAndDepositsBox.getChildren().add(totalSharesAndDepositsLabel);
		totalSharesAndDepositsBox.setPadding(new Insets(3));
		totalSharesAndDepositsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text reservesLabel = new Text(twoYrFinSum.getReservesPctChg());
		reservesLabel.setFont(Font.font("Serif", 12));
		reservesLabel.setFill(Color.BLACK);

		HBox reservesBox = new HBox();
		reservesBox.getChildren().add(reservesLabel);
		reservesBox.setPadding(new Insets(3));
		reservesBox.setAlignment(Pos.CENTER_RIGHT);

		Text undividedEarningsLabel = new Text(twoYrFinSum.getUndividedEarningsPctChg());
		undividedEarningsLabel.setFont(Font.font("Serif", 12));
		undividedEarningsLabel.setFill(Color.BLACK);

		HBox undividedEarningsBox = new HBox();
		undividedEarningsBox.getChildren().add(undividedEarningsLabel);
		undividedEarningsBox.setPadding(new Insets(3));
		undividedEarningsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ----------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text totalCapitalLabel = new Text(twoYrFinSum.getTotalCapitalPctChg());
		totalCapitalLabel.setFont(Font.font("Serif", 12));
		totalCapitalLabel.setFill(Color.BLACK);

		HBox totalCapitalBox = new HBox();
		totalCapitalBox.getChildren().add(totalCapitalLabel);
		totalCapitalBox.setPadding(new Insets(3));
		totalCapitalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer7 = new Text(" ----------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		Text totalLiabilitiesAndCapitalLabel = new Text(twoYrFinSum.getTotalLiabilitiesAndCapitalPctChg());
		totalLiabilitiesAndCapitalLabel.setFont(Font.font("Serif", 14));
		totalLiabilitiesAndCapitalLabel.setFill(Color.BLACK);

		HBox totalLiabilitiesAndCapitalBox = new HBox();
		totalLiabilitiesAndCapitalBox.getChildren().add(totalLiabilitiesAndCapitalLabel);
		totalLiabilitiesAndCapitalBox.setPadding(new Insets(5));
		totalLiabilitiesAndCapitalBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer8 = new Text(" ----------- ");
		spacer8.setFont(Font.font("Serif", 12));
		spacer8.setFill(Color.BLACK);

		Text spacer9 = new Text(" ----------- ");
		spacer9.setFont(Font.font("Serif", 12));
		spacer9.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(headerBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(reverseReposBox);
		wHeader.getChildren().add(otherNotesPayableBox);
		wHeader.getChildren().add(allOtherLiabilitiesBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLiabilitiesBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(regularSharesBox);
		wHeader.getChildren().add(shareDraftsBox);
		wHeader.getChildren().add(mmaBox);
		wHeader.getChildren().add(iraKeoghBox);
		wHeader.getChildren().add(certificatesBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalSharesAndDepositsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(reservesBox);
		wHeader.getChildren().add(undividedEarningsBox);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(totalCapitalBox);
		wHeader.getChildren().add(spacer7);
		wHeader.getChildren().add(totalLiabilitiesAndCapitalBox);
		wHeader.getChildren().add(spacer8);
		wHeader.getChildren().add(spacer9);


		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildOtherAssetsPCTChangeBox(Stage primaryStage)
	{		
		Text headerLabel = new Text("% Chg");
		headerLabel.setFont(Font.font("Serif", 14));
		headerLabel.setFill(Color.BLACK);

		HBox headerBox = new HBox();
		headerBox.getChildren().add(headerLabel);
		headerBox.setPadding(new Insets(5));
		headerBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ----------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text repoAssetsLabel = new Text(twoYrFinSum.getRepoAssetsPctChg());
		repoAssetsLabel.setFont(Font.font("Serif", 12));
		repoAssetsLabel.setFill(Color.BLACK);

		HBox repoAssetsBox = new HBox();
		repoAssetsBox.getChildren().add(repoAssetsLabel);
		repoAssetsBox.setPadding(new Insets(3));
		repoAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text landAndBuildingLabel = new Text(twoYrFinSum.getLandAndBuildingPctChg());
		landAndBuildingLabel.setFont(Font.font("Serif", 12));
		landAndBuildingLabel.setFill(Color.BLACK);

		HBox landAndBuildingBox = new HBox();
		landAndBuildingBox.getChildren().add(landAndBuildingLabel);
		landAndBuildingBox.setPadding(new Insets(3));
		landAndBuildingBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherFixedAssetsLabel = new Text(twoYrFinSum.getOtherFixedAssetsPctChg());
		otherFixedAssetsLabel.setFont(Font.font("Serif", 12));
		otherFixedAssetsLabel.setFill(Color.BLACK);

		HBox otherFixedAssetsBox = new HBox();
		otherFixedAssetsBox.getChildren().add(otherFixedAssetsLabel);
		otherFixedAssetsBox.setPadding(new Insets(3));
		otherFixedAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text shareInsuranceDepositLabel = new Text(twoYrFinSum.getShareInsuranceDepositPctChg());
		shareInsuranceDepositLabel.setFont(Font.font("Serif", 12));
		shareInsuranceDepositLabel.setFill(Color.BLACK);

		HBox shareInsuranceDepositBox = new HBox();
		shareInsuranceDepositBox.getChildren().add(shareInsuranceDepositLabel);
		shareInsuranceDepositBox.setPadding(new Insets(3));
		shareInsuranceDepositBox.setAlignment(Pos.CENTER_RIGHT);

		Text intangibleAssetsLabel = new Text(twoYrFinSum.getIntangibleAssetsPctChg());
		intangibleAssetsLabel.setFont(Font.font("Serif", 12));
		intangibleAssetsLabel.setFill(Color.BLACK);

		HBox intangibleAssetsBox = new HBox();
		intangibleAssetsBox.getChildren().add(intangibleAssetsLabel);
		intangibleAssetsBox.setPadding(new Insets(3));
		intangibleAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherAssetsLabel = new Text(twoYrFinSum.getOtherAssetsPctChg());
		otherAssetsLabel.setFont(Font.font("Serif", 12));
		otherAssetsLabel.setFill(Color.BLACK);

		HBox otherAssetsBox = new HBox();
		otherAssetsBox.getChildren().add(otherAssetsLabel);
		otherAssetsBox.setPadding(new Insets(3));
		otherAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalAssetsLabel = new Text(twoYrFinSum.getTotalAssetsPctChg());
		totalAssetsLabel.setFont(Font.font("Serif", 14));
		totalAssetsLabel.setFill(Color.BLACK);

		HBox totalAssetsBox = new HBox();
		totalAssetsBox.getChildren().add(totalAssetsLabel);
		totalAssetsBox.setPadding(new Insets(5));
		totalAssetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text spacer4 = new Text(" ----------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text loansGrantedYTDLabel = new Text(twoYrFinSum.getLoansGrantedYTDPctChg());
		loansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		loansGrantedYTDLabel.setFill(Color.BLACK);

		HBox loansGrantedYTDBox = new HBox();
		loansGrantedYTDBox.getChildren().add(loansGrantedYTDLabel);
		loansGrantedYTDBox.setPadding(new Insets(3));
		loansGrantedYTDBox.setAlignment(Pos.CENTER_RIGHT);

		Text palLoansGrantedYTDLabel = new Text(twoYrFinSum.getPALLoansGrantedYTDPctChg());
		palLoansGrantedYTDLabel.setFont(Font.font("Serif", 12));
		palLoansGrantedYTDLabel.setFill(Color.BLACK);

		HBox palLoansGrantedYTDBox = new HBox();
		palLoansGrantedYTDBox.getChildren().add(palLoansGrantedYTDLabel);
		palLoansGrantedYTDBox.setPadding(new Insets(3));
		palLoansGrantedYTDBox.setAlignment(Pos.CENTER_RIGHT);

		Text deferredEduLoansLabel = new Text(twoYrFinSum.getDeferredEduLoansPctChg());
		deferredEduLoansLabel.setFont(Font.font("Serif", 12));
		deferredEduLoansLabel.setFill(Color.BLACK);

		HBox deferredEduLoansBox = new HBox();
		deferredEduLoansBox.getChildren().add(deferredEduLoansLabel);
		deferredEduLoansBox.setPadding(new Insets(3));
		deferredEduLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text loansToExecsLabel = new Text(twoYrFinSum.getLoansToExecsPctChg());
		loansToExecsLabel.setFont(Font.font("Serif", 12));
		loansToExecsLabel.setFill(Color.BLACK);

		HBox loansToExecsBox = new HBox();
		loansToExecsBox.getChildren().add(loansToExecsLabel);
		loansToExecsBox.setPadding(new Insets(3));
		loansToExecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text spacer6 = new Text(" ----------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		Text spacer7 = new Text(" ----------- ");
		spacer7.setFont(Font.font("Serif", 12));
		spacer7.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(headerBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(repoAssetsBox);
		wHeader.getChildren().add(landAndBuildingBox);
		wHeader.getChildren().add(otherFixedAssetsBox);
		wHeader.getChildren().add(shareInsuranceDepositBox);
		wHeader.getChildren().add(intangibleAssetsBox);
		wHeader.getChildren().add(otherAssetsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalAssetsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(loansGrantedYTDBox);
		wHeader.getChildren().add(palLoansGrantedYTDBox);
		wHeader.getChildren().add(deferredEduLoansBox);
		wHeader.getChildren().add(loansToExecsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(spacer6);
		wHeader.getChildren().add(spacer7);


		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildLoansPCTChangeBox(Stage primaryStage)
	{		
		Text headerLabel = new Text("% Chg");
		headerLabel.setFont(Font.font("Serif", 14));
		headerLabel.setFill(Color.BLACK);

		HBox headerBox = new HBox();
		headerBox.getChildren().add(headerLabel);
		headerBox.setPadding(new Insets(5));
		headerBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ----------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text creditCardsLabel = new Text(twoYrFinSum.getCreditCardsPctChg());
		creditCardsLabel.setFont(Font.font("Serif", 12));
		creditCardsLabel.setFill(Color.BLACK);

		HBox creditCardsBox = new HBox();
		creditCardsBox.getChildren().add(creditCardsLabel);
		creditCardsBox.setPadding(new Insets(3));
		creditCardsBox.setAlignment(Pos.CENTER_RIGHT);

		Text otherUnsecuredLabel = new Text(twoYrFinSum.getOtherUnsecuredLoansPctChg());
		otherUnsecuredLabel.setFont(Font.font("Serif", 12));
		otherUnsecuredLabel.setFill(Color.BLACK);

		HBox otherUnsecuredBox = new HBox();
		otherUnsecuredBox.getChildren().add(otherUnsecuredLabel);
		otherUnsecuredBox.setPadding(new Insets(3));
		otherUnsecuredBox.setAlignment(Pos.CENTER_RIGHT);

		Text palLabel = new Text(twoYrFinSum.getPALLoansPctChg());
		palLabel.setFont(Font.font("Serif", 12));
		palLabel.setFill(Color.BLACK);

		HBox palBox = new HBox();
		palBox.getChildren().add(palLabel);
		palBox.setPadding(new Insets(3));
		palBox.setAlignment(Pos.CENTER_RIGHT);

		Text eduLabel = new Text(twoYrFinSum.getEduLoansPctChg());
		eduLabel.setFont(Font.font("Serif", 12));
		eduLabel.setFill(Color.BLACK);

		HBox eduBox = new HBox();
		eduBox.getChildren().add(eduLabel);
		eduBox.setPadding(new Insets(3));
		eduBox.setAlignment(Pos.CENTER_RIGHT);

		Text newAutoLabel = new Text(twoYrFinSum.getNewAutoLoansPctChg());
		newAutoLabel.setFont(Font.font("Serif", 12));
		newAutoLabel.setFill(Color.BLACK);

		HBox newAutoBox = new HBox();
		newAutoBox.getChildren().add(newAutoLabel);
		newAutoBox.setPadding(new Insets(3));
		newAutoBox.setAlignment(Pos.CENTER_RIGHT);

		Text usedAutoLabel = new Text(twoYrFinSum.getUsedAutoLoansPctChg());
		usedAutoLabel.setFont(Font.font("Serif", 12));
		usedAutoLabel.setFill(Color.BLACK);

		HBox usedAutoBox = new HBox();
		usedAutoBox.getChildren().add(usedAutoLabel);
		usedAutoBox.setPadding(new Insets(3));
		usedAutoBox.setAlignment(Pos.CENTER_RIGHT);

		Text firstMortgageLabel = new Text(twoYrFinSum.getFirstMortgagePctChg());
		firstMortgageLabel.setFont(Font.font("Serif", 12));
		firstMortgageLabel.setFill(Color.BLACK);

		HBox firstMortgageBox = new HBox();
		firstMortgageBox.getChildren().add(firstMortgageLabel);
		firstMortgageBox.setPadding(new Insets(3));
		firstMortgageBox.setAlignment(Pos.CENTER_RIGHT);

		Text secondMortgageLabel = new Text(twoYrFinSum.getSecondMortgagePctChg());
		secondMortgageLabel.setFont(Font.font("Serif", 12));
		secondMortgageLabel.setFill(Color.BLACK);

		HBox secondMortgageBox = new HBox();
		secondMortgageBox.getChildren().add(secondMortgageLabel);
		secondMortgageBox.setPadding(new Insets(3));
		secondMortgageBox.setAlignment(Pos.CENTER_RIGHT);

		Text leasesLabel = new Text(twoYrFinSum.getLeasesPctChg());
		leasesLabel.setFont(Font.font("Serif", 12));
		leasesLabel.setFill(Color.BLACK);

		HBox leasesBox = new HBox();
		leasesBox.getChildren().add(leasesLabel);
		leasesBox.setPadding(new Insets(3));
		leasesBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherLoansLabel = new Text(twoYrFinSum.getAllOtherLoansPctChg());
		allOtherLoansLabel.setFont(Font.font("Serif", 12));
		allOtherLoansLabel.setFill(Color.BLACK);

		HBox allOtherLoansBox = new HBox();
		allOtherLoansBox.getChildren().add(allOtherLoansLabel);
		allOtherLoansBox.setPadding(new Insets(3));
		allOtherLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text totalLoansLabel = new Text(twoYrFinSum.getTotalLoansPctChg());
		totalLoansLabel.setFont(Font.font("Serif", 13));
		totalLoansLabel.setFill(Color.BLACK);

		HBox totalLoansBox = new HBox();
		totalLoansBox.getChildren().add(totalLoansLabel);
		totalLoansBox.setPadding(new Insets(5));
		totalLoansBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text allowanceLabel = new Text(twoYrFinSum.getLoanLossAllowancePctChg());
		allowanceLabel.setFont(Font.font("Serif", 12));
		allowanceLabel.setFill(Color.BLACK);

		HBox allowanceBox = new HBox();
		allowanceBox.getChildren().add(allowanceLabel);
		allowanceBox.setPadding(new Insets(3));
		allowanceBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text mblLabel = new Text(twoYrFinSum.getMBLsPctChg());
		mblLabel.setFont(Font.font("Serif", 12));
		mblLabel.setFill(Color.BLACK);

		HBox mblBox = new HBox();
		mblBox.getChildren().add(mblLabel);
		mblBox.setPadding(new Insets(3));
		mblBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(headerBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(creditCardsBox);
		wHeader.getChildren().add(otherUnsecuredBox);
		wHeader.getChildren().add(palBox);
		wHeader.getChildren().add(eduBox);
		wHeader.getChildren().add(newAutoBox);
		wHeader.getChildren().add(usedAutoBox);
		wHeader.getChildren().add(firstMortgageBox);
		wHeader.getChildren().add(secondMortgageBox);
		wHeader.getChildren().add(leasesBox);
		wHeader.getChildren().add(allOtherLoansBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(totalLoansBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(allowanceBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(mblBox);
		wHeader.getChildren().add(spacer5);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}


	private VBox buildCashAndInvestmentsPCTChangeBox(Stage primaryStage)
	{		
		Text assetsLabel = new Text("% Chg");
		assetsLabel.setFont(Font.font("Serif", 14));
		assetsLabel.setFill(Color.BLACK);

		HBox assetsBox = new HBox();
		assetsBox.getChildren().add(assetsLabel);
		assetsBox.setPadding(new Insets(5));
		assetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ----------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text cashOnHandLabel = new Text(twoYrFinSum.getCashOnHandPctChg());
		cashOnHandLabel.setFont(Font.font("Serif", 12));
		cashOnHandLabel.setFill(Color.BLACK);

		HBox cashOnHandBox = new HBox();
		cashOnHandBox.getChildren().add(cashOnHandLabel);
		cashOnHandBox.setPadding(new Insets(3));
		cashOnHandBox.setAlignment(Pos.CENTER_RIGHT);

		Text cashOnDepositLabel = new Text(twoYrFinSum.getCashOnDepositPctChg());
		cashOnDepositLabel.setFont(Font.font("Serif", 12));
		cashOnDepositLabel.setFill(Color.BLACK);

		HBox cashOnDepositBox = new HBox();
		cashOnDepositBox.getChildren().add(cashOnDepositLabel);
		cashOnDepositBox.setPadding(new Insets(3));
		cashOnDepositBox.setAlignment(Pos.CENTER_RIGHT);

		Text cashEquivalentsLabel = new Text(twoYrFinSum.getCashEquivalentsPctChg());
		cashEquivalentsLabel.setFont(Font.font("Serif", 12));
		cashEquivalentsLabel.setFill(Color.BLACK);

		HBox cashEquivalentsBox = new HBox();
		cashEquivalentsBox.getChildren().add(cashEquivalentsLabel);
		cashEquivalentsBox.setPadding(new Insets(3));
		cashEquivalentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ----------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text cashAndEquivsLabel = new Text(twoYrFinSum.getCashAndEquivalentsPctChg());
		cashAndEquivsLabel.setFont(Font.font("Serif", 13));
		cashAndEquivsLabel.setFill(Color.BLACK);

		HBox cashAndEquivsBox = new HBox();
		cashAndEquivsBox.getChildren().add(cashAndEquivsLabel);
		cashAndEquivsBox.setPadding(new Insets(5));
		cashAndEquivsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ----------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text govSecsLabel = new Text(twoYrFinSum.getGovernmentSecuritiesPctChg());
		govSecsLabel.setFont(Font.font("Serif", 12));
		govSecsLabel.setFill(Color.BLACK);

		HBox govSecsBox = new HBox();
		govSecsBox.getChildren().add(govSecsLabel);
		govSecsBox.setPadding(new Insets(3));
		govSecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text fedAgencySecsLabel = new Text(twoYrFinSum.getFederalAgencySecuritiesPctChg());
		fedAgencySecsLabel.setFont(Font.font("Serif", 12));
		fedAgencySecsLabel.setFill(Color.BLACK);

		HBox fedAgencySecsBox = new HBox();
		fedAgencySecsBox.getChildren().add(fedAgencySecsLabel);
		fedAgencySecsBox.setPadding(new Insets(3));
		fedAgencySecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text corpCUsLabel = new Text(twoYrFinSum.getCorporateCUsPctChg());
		corpCUsLabel.setFont(Font.font("Serif", 12));
		corpCUsLabel.setFill(Color.BLACK);

		HBox corpCUSBox = new HBox();
		corpCUSBox.getChildren().add(corpCUsLabel);
		corpCUSBox.setPadding(new Insets(3));
		corpCUSBox.setAlignment(Pos.CENTER_RIGHT);

		Text bankDepositsLabel = new Text(twoYrFinSum.getBankDepositsPctChg());
		bankDepositsLabel.setFont(Font.font("Serif", 12));
		bankDepositsLabel.setFill(Color.BLACK);

		HBox bankDepositsBox = new HBox();
		bankDepositsBox.getChildren().add(bankDepositsLabel);
		bankDepositsBox.setPadding(new Insets(3));
		bankDepositsBox.setAlignment(Pos.CENTER_RIGHT);

		Text mutualFundsLabel = new Text(twoYrFinSum.getMutualFundsPctChg());
		mutualFundsLabel.setFont(Font.font("Serif", 12));
		mutualFundsLabel.setFill(Color.BLACK);

		HBox mutualFundsBox = new HBox();
		mutualFundsBox.getChildren().add(mutualFundsLabel);
		mutualFundsBox.setPadding(new Insets(3));
		mutualFundsBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherInvestmentsLabel = new Text(twoYrFinSum.getAllOtherInvestmentsPctChg());
		allOtherInvestmentsLabel.setFont(Font.font("Serif", 12));
		allOtherInvestmentsLabel.setFill(Color.BLACK);

		HBox allOtherInvestmentsBox = new HBox();
		allOtherInvestmentsBox.getChildren().add(allOtherInvestmentsLabel);
		allOtherInvestmentsBox.setPadding(new Insets(3));
		allOtherInvestmentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ----------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalInvestmentsLabel = new Text(twoYrFinSum.getTotalInvestmentsPctChg());
		totalInvestmentsLabel.setFont(Font.font("Serif", 13));
		totalInvestmentsLabel.setFill(Color.BLACK);

		HBox totalInvestmentsBox = new HBox();
		totalInvestmentsBox.getChildren().add(totalInvestmentsLabel);
		totalInvestmentsBox.setPadding(new Insets(5));
		totalInvestmentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ----------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text loansHeldForSaleLabel = new Text(twoYrFinSum.getLoansHeldForSalePctChg());
		loansHeldForSaleLabel.setFont(Font.font("Serif", 12));
		loansHeldForSaleLabel.setFill(Color.BLACK);

		HBox loansHeldForSaleBox = new HBox();
		loansHeldForSaleBox.getChildren().add(loansHeldForSaleLabel);
		loansHeldForSaleBox.setPadding(new Insets(3));
		loansHeldForSaleBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ----------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(assetsBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(cashOnHandBox);
		wHeader.getChildren().add(cashOnDepositBox);
		wHeader.getChildren().add(cashEquivalentsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(cashAndEquivsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(govSecsBox);
		wHeader.getChildren().add(fedAgencySecsBox);
		wHeader.getChildren().add(corpCUSBox);
		wHeader.getChildren().add(bankDepositsBox);
		wHeader.getChildren().add(mutualFundsBox);
		wHeader.getChildren().add(allOtherInvestmentsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalInvestmentsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(loansHeldForSaleBox);
		wHeader.getChildren().add(spacer6);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildCashAndInvestmentsPriorPeriodBox(Stage primaryStage)
	{		
		/* something to be fixed i.e. change the variable names */
		Text assetsLabel = new Text(Integer.toString(Integer.valueOf(aCreditUnion.getFinancialsPeriod().substring(0,4))-1));
		assetsLabel.setFont(Font.font("Serif", 14));
		assetsLabel.setFill(Color.BLACK);

		HBox assetsBox = new HBox();
		assetsBox.getChildren().add(assetsLabel);
		assetsBox.setPadding(new Insets(5));
		assetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);

		Text cashOnHandLabel = new Text(twoYrFinSum.getPrevCashOnHand());
		cashOnHandLabel.setFont(Font.font("Serif", 12));
		cashOnHandLabel.setFill(Color.BLACK);

		HBox cashOnHandBox = new HBox();
		cashOnHandBox.getChildren().add(cashOnHandLabel);
		cashOnHandBox.setPadding(new Insets(3));
		cashOnHandBox.setAlignment(Pos.CENTER_RIGHT);

		Text cashOnDepositLabel = new Text(twoYrFinSum.getPrevCashOnDeposit());
		cashOnDepositLabel.setFont(Font.font("Serif", 12));
		cashOnDepositLabel.setFill(Color.BLACK);

		HBox cashOnDepositBox = new HBox();
		cashOnDepositBox.getChildren().add(cashOnDepositLabel);
		cashOnDepositBox.setPadding(new Insets(3));
		cashOnDepositBox.setAlignment(Pos.CENTER_RIGHT);

		Text cashEquivalentsLabel = new Text(twoYrFinSum.getPrevCashEquivalents());
		cashEquivalentsLabel.setFont(Font.font("Serif", 12));
		cashEquivalentsLabel.setFill(Color.BLACK);

		HBox cashEquivalentsBox = new HBox();
		cashEquivalentsBox.getChildren().add(cashEquivalentsLabel);
		cashEquivalentsBox.setPadding(new Insets(3));
		cashEquivalentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text cashAndEquivsLabel = new Text(twoYrFinSum.getPrevCashAndEquivalents());
		cashAndEquivsLabel.setFont(Font.font("Serif", 13));
		cashAndEquivsLabel.setFill(Color.BLACK);

		HBox cashAndEquivsBox = new HBox();
		cashAndEquivsBox.getChildren().add(cashAndEquivsLabel);
		cashAndEquivsBox.setPadding(new Insets(5));
		cashAndEquivsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text govSecsLabel = new Text(twoYrFinSum.getPrevGovernmentSecurities());
		govSecsLabel.setFont(Font.font("Serif", 12));
		govSecsLabel.setFill(Color.BLACK);

		HBox govSecsBox = new HBox();
		govSecsBox.getChildren().add(govSecsLabel);
		govSecsBox.setPadding(new Insets(3));
		govSecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text fedAgencySecsLabel = new Text(twoYrFinSum.getPrevFederalAgencySecurities());
		fedAgencySecsLabel.setFont(Font.font("Serif", 12));
		fedAgencySecsLabel.setFill(Color.BLACK);

		HBox fedAgencySecsBox = new HBox();
		fedAgencySecsBox.getChildren().add(fedAgencySecsLabel);
		fedAgencySecsBox.setPadding(new Insets(3));
		fedAgencySecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text corpCUsLabel = new Text(twoYrFinSum.getPrevCorporateCUs());
		corpCUsLabel.setFont(Font.font("Serif", 12));
		corpCUsLabel.setFill(Color.BLACK);

		HBox corpCUSBox = new HBox();
		corpCUSBox.getChildren().add(corpCUsLabel);
		corpCUSBox.setPadding(new Insets(3));
		corpCUSBox.setAlignment(Pos.CENTER_RIGHT);

		Text bankDepositsLabel = new Text(twoYrFinSum.getPrevBankDeposits());
		bankDepositsLabel.setFont(Font.font("Serif", 12));
		bankDepositsLabel.setFill(Color.BLACK);

		HBox bankDepositsBox = new HBox();
		bankDepositsBox.getChildren().add(bankDepositsLabel);
		bankDepositsBox.setPadding(new Insets(3));
		bankDepositsBox.setAlignment(Pos.CENTER_RIGHT);

		Text mutualFundsLabel = new Text(twoYrFinSum.getPrevMutualFunds());
		mutualFundsLabel.setFont(Font.font("Serif", 12));
		mutualFundsLabel.setFill(Color.BLACK);

		HBox mutualFundsBox = new HBox();
		mutualFundsBox.getChildren().add(mutualFundsLabel);
		mutualFundsBox.setPadding(new Insets(3));
		mutualFundsBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherInvestmentsLabel = new Text(twoYrFinSum.getPrevAllOtherInvestments());
		allOtherInvestmentsLabel.setFont(Font.font("Serif", 12));
		allOtherInvestmentsLabel.setFill(Color.BLACK);

		HBox allOtherInvestmentsBox = new HBox();
		allOtherInvestmentsBox.getChildren().add(allOtherInvestmentsLabel);
		allOtherInvestmentsBox.setPadding(new Insets(3));
		allOtherInvestmentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalInvestmentsLabel = new Text(twoYrFinSum.getPrevTotalInvestments());
		totalInvestmentsLabel.setFont(Font.font("Serif", 13));
		totalInvestmentsLabel.setFill(Color.BLACK);

		HBox totalInvestmentsBox = new HBox();
		totalInvestmentsBox.getChildren().add(totalInvestmentsLabel);
		totalInvestmentsBox.setPadding(new Insets(5));
		totalInvestmentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text loansHeldForSaleLabel = new Text(twoYrFinSum.getPrevLoansHeldForSale());
		loansHeldForSaleLabel.setFont(Font.font("Serif", 12));
		loansHeldForSaleLabel.setFill(Color.BLACK);

		HBox loansHeldForSaleBox = new HBox();
		loansHeldForSaleBox.getChildren().add(loansHeldForSaleLabel);
		loansHeldForSaleBox.setPadding(new Insets(3));
		loansHeldForSaleBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(assetsBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(cashOnHandBox);
		wHeader.getChildren().add(cashOnDepositBox);
		wHeader.getChildren().add(cashEquivalentsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(cashAndEquivsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(govSecsBox);
		wHeader.getChildren().add(fedAgencySecsBox);
		wHeader.getChildren().add(corpCUSBox);
		wHeader.getChildren().add(bankDepositsBox);
		wHeader.getChildren().add(mutualFundsBox);
		wHeader.getChildren().add(allOtherInvestmentsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalInvestmentsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(loansHeldForSaleBox);
		wHeader.getChildren().add(spacer6);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildCashAndInvestmentsCurrentPeriodBox(Stage primaryStage)
	{	
		Text assetsLabel = new Text(Utils.getYear(aCreditUnion.getFinancialsPeriod()));
		assetsLabel.setFont(Font.font("Serif", 14));
		assetsLabel.setFill(Color.BLACK);

		HBox assetsBox = new HBox();
		assetsBox.getChildren().add(assetsLabel);
		assetsBox.setPadding(new Insets(5));
		assetsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer1 = new Text(" ---------------------------- ");
		spacer1.setFont(Font.font("Serif", 12));
		spacer1.setFill(Color.BLACK);


		Text cashOnHandLabel = new Text(twoYrFinSum.getCurrCashOnHand());
		cashOnHandLabel.setFont(Font.font("Serif", 12));
		cashOnHandLabel.setFill(Color.BLACK);

		HBox cashOnHandBox = new HBox();
		cashOnHandBox.getChildren().add(cashOnHandLabel);
		cashOnHandBox.setPadding(new Insets(3));
		cashOnHandBox.setAlignment(Pos.CENTER_RIGHT);

		Text cashOnDepositLabel = new Text(twoYrFinSum.getCurrCashOnDeposit());
		cashOnDepositLabel.setFont(Font.font("Serif", 12));
		cashOnDepositLabel.setFill(Color.BLACK);

		HBox cashOnDepositBox = new HBox();
		cashOnDepositBox.getChildren().add(cashOnDepositLabel);
		cashOnDepositBox.setPadding(new Insets(3));
		cashOnDepositBox.setAlignment(Pos.CENTER_RIGHT);

		Text cashEquivalentsLabel = new Text(twoYrFinSum.getCurrCashEquivalents());
		cashEquivalentsLabel.setFont(Font.font("Serif", 12));
		cashEquivalentsLabel.setFill(Color.BLACK);

		HBox cashEquivalentsBox = new HBox();
		cashEquivalentsBox.getChildren().add(cashEquivalentsLabel);
		cashEquivalentsBox.setPadding(new Insets(3));
		cashEquivalentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer2 = new Text(" ---------------------------- ");
		spacer2.setFont(Font.font("Serif", 12));
		spacer2.setFill(Color.BLACK);

		Text cashAndEquivsLabel = new Text(twoYrFinSum.getCurrCashAndEquivalents());
		cashAndEquivsLabel.setFont(Font.font("Serif", 13));
		cashAndEquivsLabel.setFill(Color.BLACK);

		HBox cashAndEquivsBox = new HBox();
		cashAndEquivsBox.getChildren().add(cashAndEquivsLabel);
		cashAndEquivsBox.setPadding(new Insets(5));
		cashAndEquivsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer3 = new Text(" ---------------------------- ");
		spacer3.setFont(Font.font("Serif", 12));
		spacer3.setFill(Color.BLACK);

		Text govSecsLabel = new Text(twoYrFinSum.getCurrGovernmentSecurities());
		govSecsLabel.setFont(Font.font("Serif", 12));
		govSecsLabel.setFill(Color.BLACK);

		HBox govSecsBox = new HBox();
		govSecsBox.getChildren().add(govSecsLabel);
		govSecsBox.setPadding(new Insets(3));
		govSecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text fedAgencySecsLabel = new Text(twoYrFinSum.getCurrFederalAgencySecurities());
		fedAgencySecsLabel.setFont(Font.font("Serif", 12));
		fedAgencySecsLabel.setFill(Color.BLACK);

		HBox fedAgencySecsBox = new HBox();
		fedAgencySecsBox.getChildren().add(fedAgencySecsLabel);
		fedAgencySecsBox.setPadding(new Insets(3));
		fedAgencySecsBox.setAlignment(Pos.CENTER_RIGHT);

		Text corpCUsLabel = new Text(twoYrFinSum.getCurrCorporateCUs());
		corpCUsLabel.setFont(Font.font("Serif", 12));
		corpCUsLabel.setFill(Color.BLACK);

		HBox corpCUSBox = new HBox();
		corpCUSBox.getChildren().add(corpCUsLabel);
		corpCUSBox.setPadding(new Insets(3));
		corpCUSBox.setAlignment(Pos.CENTER_RIGHT);

		Text bankDepositsLabel = new Text(twoYrFinSum.getCurrBankDeposits());
		bankDepositsLabel.setFont(Font.font("Serif", 12));
		bankDepositsLabel.setFill(Color.BLACK);

		HBox bankDepositsBox = new HBox();
		bankDepositsBox.getChildren().add(bankDepositsLabel);
		bankDepositsBox.setPadding(new Insets(3));
		bankDepositsBox.setAlignment(Pos.CENTER_RIGHT);

		Text mutualFundsLabel = new Text(twoYrFinSum.getCurrMutualFunds());
		mutualFundsLabel.setFont(Font.font("Serif", 12));
		mutualFundsLabel.setFill(Color.BLACK);

		HBox mutualFundsBox = new HBox();
		mutualFundsBox.getChildren().add(mutualFundsLabel);
		mutualFundsBox.setPadding(new Insets(3));
		mutualFundsBox.setAlignment(Pos.CENTER_RIGHT);

		Text allOtherInvestmentsLabel = new Text(twoYrFinSum.getCurrAllOtherInvestments());
		allOtherInvestmentsLabel.setFont(Font.font("Serif", 12));
		allOtherInvestmentsLabel.setFill(Color.BLACK);

		HBox allOtherInvestmentsBox = new HBox();
		allOtherInvestmentsBox.getChildren().add(allOtherInvestmentsLabel);
		allOtherInvestmentsBox.setPadding(new Insets(3));
		allOtherInvestmentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer4 = new Text(" ---------------------------- ");
		spacer4.setFont(Font.font("Serif", 12));
		spacer4.setFill(Color.BLACK);

		Text totalInvestmentsLabel = new Text(twoYrFinSum.getCurrTotalInvestments());
		totalInvestmentsLabel.setFont(Font.font("Serif", 13));
		totalInvestmentsLabel.setFill(Color.BLACK);

		HBox totalInvestmentsBox = new HBox();
		totalInvestmentsBox.getChildren().add(totalInvestmentsLabel);
		totalInvestmentsBox.setPadding(new Insets(5));
		totalInvestmentsBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer5 = new Text(" ---------------------------- ");
		spacer5.setFont(Font.font("Serif", 12));
		spacer5.setFill(Color.BLACK);

		Text loansHeldForSaleLabel = new Text(twoYrFinSum.getCurrLoansHeldForSale());
		loansHeldForSaleLabel.setFont(Font.font("Serif", 12));
		loansHeldForSaleLabel.setFill(Color.BLACK);

		HBox loansHeldForSaleBox = new HBox();
		loansHeldForSaleBox.getChildren().add(loansHeldForSaleLabel);
		loansHeldForSaleBox.setPadding(new Insets(3));
		loansHeldForSaleBox.setAlignment(Pos.CENTER_RIGHT);

		Text spacer6 = new Text(" ---------------------------- ");
		spacer6.setFont(Font.font("Serif", 12));
		spacer6.setFill(Color.BLACK);

		VBox wHeader = new VBox();
		wHeader.getChildren().add(assetsBox);
		wHeader.getChildren().add(spacer1);
		wHeader.getChildren().add(cashOnHandBox);
		wHeader.getChildren().add(cashOnDepositBox);
		wHeader.getChildren().add(cashEquivalentsBox);
		wHeader.getChildren().add(spacer2);
		wHeader.getChildren().add(cashAndEquivsBox);
		wHeader.getChildren().add(spacer3);
		wHeader.getChildren().add(govSecsBox);
		wHeader.getChildren().add(fedAgencySecsBox);
		wHeader.getChildren().add(corpCUSBox);
		wHeader.getChildren().add(bankDepositsBox);
		wHeader.getChildren().add(mutualFundsBox);
		wHeader.getChildren().add(allOtherInvestmentsBox);
		wHeader.getChildren().add(spacer4);
		wHeader.getChildren().add(totalInvestmentsBox);
		wHeader.getChildren().add(spacer5);
		wHeader.getChildren().add(loansHeldForSaleBox);
		wHeader.getChildren().add(spacer6);

		wHeader.setAlignment(Pos.CENTER_RIGHT);

		return (wHeader);
	}

	private VBox buildSearchBar(Stage primaryStage)
	{
		Text cuidLabel = new Text("CUID: ");
		cuidLabel.setFont(Font.font("Serif", 14));
		cuidLabel.setFill(Color.BLACK);

		Text id = new Text(aCreditUnion.getID());
		id.setFont(Font.font("Serif", 14));
		id.setFill(Color.BLUE);

		HBox cuid = new HBox();
		cuid.getChildren().add(cuidLabel);
		cuid.getChildren().add(id);
		cuid.setPadding(new Insets(5));

		Text fchtLabel = new Text("Charter: ");
		fchtLabel.setFont(Font.font("Serif", 14));
		fchtLabel.setFill(Color.BLACK);

		Text fcht = new Text(Integer.toString(aCreditUnion.getCharter()));
		fcht.setFont(Font.font("Serif", 14));
		fcht.setFill(Color.BLUE);

		HBox charter = new HBox();
		charter.getChildren().add(fchtLabel);
		charter.getChildren().add(fcht);
		charter.setPadding(new Insets(5));

		Text survIDLabel = new Text("Survivor ID: ");
		survIDLabel.setFont(Font.font("Serif", 14));
		survIDLabel.setFill(Color.BLACK);

		Text survid = new Text(aCreditUnion.getSurvivorID());
		survid.setFont(Font.font("Serif", 14));
		survid.setFill(Color.BLUE);

		Hyperlink survIDLnk = new Hyperlink(aCreditUnion.getSurvivorID());
		survIDLnk.setFont(Font.font("Serif", 14));
		survIDLnk.setTextFill(Color.BLUE);
		survIDLnk.setBorder(null);

		survIDLnk.setOnAction(new EventHandler<ActionEvent>() 
		{   
			public void handle(ActionEvent e) 
			{        
				cuidBatch = Utils.search(dbConn, aCreditUnion.getSurvivorID(), Boolean.TRUE);

				index = 0;
				setCreditUnion(cuidBatch[index]);
				setScreen(CU_Info.mainScreen);
				resetMergersScreen();
				start(primaryStage);
			}
		});

		HBox survivorid = new HBox();
		survivorid.getChildren().add(survIDLabel);
		survivorid.getChildren().add(survIDLnk);
		survivorid.setPadding(new Insets(5));

		HBox idBox = new HBox();
		idBox.getChildren().add(cuid);
		idBox.getChildren().add(charter);
		idBox.getChildren().add(survivorid);

		TextField searchField = new TextField ();
		searchField.setPromptText("<<Enter Search Criteria>>");
		searchField.setPrefColumnCount(20);
		searchField.getText();

		Button find = new Button("Find");
		Button previous = new Button("<- Prev");
		Button next = new Button("Next ->");
		CheckBox includeInactives = new CheckBox("Include Inactives");
		includeInactives.setSelected(inactivesIncluded);

		HBox searchBox = new HBox();
		searchBox.getChildren().addAll(searchField);
		searchBox.getChildren().addAll(find);
		searchBox.getChildren().addAll(previous);
		searchBox.getChildren().addAll(next);
		searchBox.getChildren().addAll(includeInactives);
		searchBox.setSpacing(5);
		searchBox.setPadding(new Insets(10));
		
		HBox countBox = new HBox();
		Text countLabel = new Text("Displaying Credit Union " + Integer.valueOf(index + 1) + " of " + cuidBatch.length);
		countLabel.setFont(Font.font("Serif", 12));
		countLabel.setFill(Color.BLACK);
		
		countBox.getChildren().add(countLabel);
		countBox.setSpacing(5);
		countBox.setPadding(new Insets(10));

		VBox findPanel = new VBox();
		findPanel.getChildren().addAll(idBox);
		findPanel.getChildren().addAll(searchBox);
		findPanel.getChildren().addAll(countBox);
		findPanel.setPadding(new Insets(5));

		find.setOnAction((event) -> 
		{
			String searchFieldText = searchField.getText();
			inactivesIncluded = includeInactives.isSelected();

			cuidBatch = Utils.search(dbConn, searchFieldText, inactivesIncluded);

			index = 0;
			if (cuidBatch.length == 0)
			{
				noneFound = true;
			}
			else
			{	
				noneFound = false;
				setCreditUnion(cuidBatch[index]);
				resetMergersScreen();
			}
			start(primaryStage);
		});

		searchField.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			public void handle(KeyEvent ke)
			{
				if (ke.getCode().equals(KeyCode.ENTER))
				{
					String searchFieldText = searchField.getText();
					inactivesIncluded = includeInactives.isSelected();

					cuidBatch = Utils.search(dbConn, searchFieldText, inactivesIncluded);

					index = 0;
					if (cuidBatch.length == 0)
					{
						noneFound = true;
					}
					else
					{
						noneFound = false;
						setCreditUnion(cuidBatch[index]);
						resetMergersScreen();	
					}
					start(primaryStage);
				}
			}
		});

		next.setOnAction((event) -> 
		{
			if (cuidBatch.length > 1 && ((index+1) < cuidBatch.length))
			{
				index++;	
			}
			setCreditUnion(cuidBatch[index]);
			resetMergersScreen();
			start(primaryStage);
		});

		previous.setOnAction((event) -> 
		{
			if (cuidBatch.length > 1 && ((index-1) >= 0))
			{
				index--;	
			}
			setCreditUnion(cuidBatch[index]);
			resetMergersScreen();
			start(primaryStage);
		});

		return (findPanel);
	}

	private Connection dbConn = null;

	private CreditUnion aCreditUnion = null;

	private String[] cuidBatch = null;
	private int index = 0;
	private boolean noneFound = false;

	private boolean inactivesIncluded = false;

	private static String financialsPeriod = null;

	private static int mainScreen = 1;
	private static int mergersScreen = 2;
	private static int twoYrFinancialsCompScreen = 3;
	private static int advancedSearchScreen = 4;
	private static int duesScreen = 5;
	private int whichScreen = 1;

	private int mergerScreenNumber = 1;
	private int numberOfMergerScreens = 1;

	private int financialsScreenNumber = 1;

	private int balanceSheetScreenNumber = 1;
	private int incomeStatementScreenNumber = 1;

	private TwoYearFinancialSummary twoYrFinSum = null;
}

