package equal.experts.assesment.testcase;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import equal.experts.assesment.utility.BaseDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CreateBooking extends BaseDriver {

	Random rand;
	Properties DataFile;
	private Logger log;
	String pageObject = "Hotel booking form";

	


	@BeforeTest
	public void reporting() throws Throwable {
		initializeObjects();
		generateReport();
		String os = System.getProperty("os.name").toLowerCase();
		DataFile = new Properties();
		String macFilePath = "/data/DataFile.properties";
		String winFilePath = "\\data\\DataFile.properties";
		
		if (os.contains("mac")) {
			FileInputStream fip = new FileInputStream(System.getProperty("user.dir")+macFilePath);
			DataFile.load(fip);
		}
		
		if (os.contains("windows")) {
			FileInputStream fip = new FileInputStream((System.getProperty("user.dir")+winFilePath));
			DataFile.load(fip);
		}
		
		
	}
	
	//test case steps below
	@Test(priority = 1)
	public void bookingWithDep() throws Throwable {
		//this.log = Logger.getLogger(ILabAssesment.class);
		rand  = new Random();
		int totalPrice = rand.nextInt(1000);
		loadWebBrowser();
		navigateToSite();
		WebElement page = getElementByClassName("jumbotron");
		Assert.assertEquals(page.getText(), pageObject);
		getElemById("firstname").sendKeys(randomIdentifier());
		getElemById("lastname").sendKeys(randomIdentifier());
		getElemById("totalprice").sendKeys(String.valueOf(rand.nextInt(1000)));
		//WebElement deposit = getElemsById("");
		Select deposit = new Select(getElemById("depositpaid"));
		deposit.selectByIndex(0);

		WebElement checkIn = getElemById("checkin");
		WebElement checkOut = getElemById("checkout");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String today = dateFormat.format(date);
		checkIn.click();
		checkIn.sendKeys(today);
		checkIn.sendKeys(Keys.TAB);
		checkOut.sendKeys(today);
		page.click();
		Thread.sleep(1000);
		getElemByXpath("//*[@id=\"form\"]/div/div[7]/input").click();
		//Assert.assertEquals();
		System.out.println("Test completed: Booking with deposit");

	}

	@Test(priority = 2)
	public void bookingWithoutDep() throws Throwable {
		//this.log = Logger.getLogger(ILabAssesment.class);
		rand  = new Random();
		int totalPrice = rand.nextInt(1000);
		loadWebBrowser();
		navigateToSite();
		WebElement page = getElementByClassName("jumbotron");
		Assert.assertEquals(page.getText(), pageObject);
		String pageTitle = getPageTitle();
		Assert.assertEquals(pageTitle, pageObject);
		getElemById("firstname").sendKeys(randomIdentifier());
		getElemById("lastname").sendKeys(randomIdentifier());
		getElemById("totalprice").sendKeys(String.valueOf(rand.nextInt(1000)));
		//WebElement deposit = getElemsById("");
		Select deposit = new Select(getElemById("depositpaid"));
		deposit.selectByIndex(1); //changes to false

		WebElement checkIn = getElemById("checkin");
		WebElement checkOut = getElemById("checkout");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String today = dateFormat.format(date);
		checkIn.click();
		checkIn.sendKeys(today);
		checkIn.sendKeys(Keys.TAB);
		checkOut.sendKeys(today);
		page.click();
		Thread.sleep(1000);
		getElemByXpath("//*[@id=\"form\"]/div/div[7]/input").click();
		//Assert.assertEquals();
		System.out.println("Test completed: Booking without Deposit");

	}

	@Test(priority = 3)
	public void deleteBooking() throws Throwable {
		//this.log = Logger.getLogger(ILabAssesment.class);
		rand  = new Random();
		int totalPrice = rand.nextInt(1000);
		loadWebBrowser();
		navigateToSite();
		WebElement page = getElementByClassName("jumbotron");
		Assert.assertEquals(page.getText(), pageObject);
		String pageTitle = getPageTitle();
		Assert.assertEquals(pageTitle, pageObject);
		getElemByXpath("/html/body/div[1]/div[2]/div[2]/div[7]/input").click();

		System.out.println("Test completed: Booking Deleted");

	}





	@AfterMethod
	public void getTestResults(ITestResult result) {
		try {
			getResult(result);
			killWebBrowser();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterTest
	public void closeBrowser() {
		//killWebBrowser();
		System.out.println("done testing");
	}
}
