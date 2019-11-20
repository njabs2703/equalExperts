package equal.experts.assesment.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import equal.experts.assesment.testcase.CreateBooking;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;



public class BaseDriver {

	boolean BrowseralreadyLoaded = false;
	ExtentReports extent;
	ExtentTest test;
	final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	final Random rand = new Random();
	final Set<String> identifiers = new HashSet<String>();
	static Properties Object = null;
	static WebDriver driver;
	static WebDriver chromeBrowser;
	static WebDriver mozillaBrowser;
	static Logger log = null;
	static String reporter;
	static String geckoPath = System.getProperty("user.dir") + "\\lib\\geckodriver.exe";

	public void initializeObjects() throws IOException {
		//initialize logger service.
		log = Logger.getLogger(CreateBooking.class);
		BasicConfigurator.configure();
		//initialize Objects.properties file.
		Object = new Properties();
		FileInputStream fip = new FileInputStream(System.getProperty("user.dir") + "/data/DataFile.properties");
		Object.load(fip);
		log.info("Objects.properties file loaded successfully.");
	}

	//build html report
	public void generateReport() {
		reporter = System.getProperty("user.dir") + "/TestResults/equal_experts_report.html";
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(reporter);
		htmlReporter.config().setReportName("Equal Experts report");
		htmlReporter.config().setDocumentTitle("Test Results");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);

		test = extent.createTest(" Test Case", "See the steps below for this test");
		test.info("This step shows usage of info(details)");
		test.assignAuthor("Njabulo Mlahleki");
		test.assignCategory("Book Online");
	}

	public void navigateToSite() {
		driver.get(Object.getProperty("url"));
	}

	public void loadWebBrowser() {
		// check os before loading browser
		String os = System.getProperty("os.name").toLowerCase();
		test.info("running tests on "+ os + "platform");


		if (os.contains("mac")) {
			if (Object.getProperty("testBrowser").equalsIgnoreCase("Mozilla")) {
				// To load Firefox driver instance.
				System.setProperty("webdriver.gecko.driver", geckoPath);
				driver = new FirefoxDriver();
				mozillaBrowser = driver;
				test.info("Firefox Driver Instance loaded successfully.");

			} else if (Object.getProperty("testBrowser").equalsIgnoreCase("Chrome")) {
				// To load Chrome driver instance.
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/lib/chromedriver");
				driver = new ChromeDriver();
				chromeBrowser = driver;
				test.info("Chrome Driver Instance loaded successfully.");

			}
		}else if(os.contains("windows")) {
			if (Object.getProperty("testBrowser").equalsIgnoreCase("Mozilla")) {
				// To load Firefox driver instance.
				System.setProperty("webdriver.gecko.driver", geckoPath.replace("/", "\\"));
				driver = new FirefoxDriver();
				mozillaBrowser = driver;
				test.info("Firefox Driver Instance loaded successfully.");

			} else if (Object.getProperty("testBrowser").equalsIgnoreCase("Chrome")) {
				// To load Chrome driver instance.
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\lib\\chromedriver.exe");
				driver = new ChromeDriver();
				chromeBrowser = driver;
				test.info("Chrome Driver Instance loaded successfully.");
			}
		}
		
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.manage().window().maximize();
	}

	public void killWebBrowser() {
		driver.quit();
		chromeBrowser = null;
		mozillaBrowser = null;
	}

	public String getScreenshot(WebDriver driver, String screenshotName) throws Exception {
		String destination = null;
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);

		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("mac")) 
			destination = System.getProperty("user.dir") + "/TestResults/" + screenshotName + dateName + ".png";
		
		if (os.contains("windows")) 
			destination = System.getProperty("user.dir") + "\\TestResults\\" + screenshotName + dateName + ".png";
			
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);

		return destination;
	}

	//get results and writ to html file
	public void getResult(ITestResult result) throws Exception {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenShotPath = getScreenshot(driver, "ScreenshotFail");
			test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " Test case FAILED due to below issues:",
					ExtentColor.RED));
			test.fail(result.getThrowable().getMessage());
			test.fail("Snapshot below: " + test.addScreenCaptureFromPath(screenShotPath).toString());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			String screenShotPath = getScreenshot(driver, "ScreenshotPass");
			test.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " Test Case PASSED", ExtentColor.GREEN));
			test.pass("Snapshot below: " + test.addScreenCaptureFromPath(screenShotPath).toString());
		}
		extent.flush();
	}

	// get uiObject by classname
	public WebElement getElementByClassName(String className){
		WebDriverWait wait = new WebDriverWait(driver, 20);
		WebElement elem = wait.until(ExpectedConditions.elementToBeClickable(By.className(className)));
		test.info(elem + "ui object found and actioned");
		return elem;
	}

	// get uiObject by id
	public WebElement getElemById(String id) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		WebElement elem = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
		test.info(elem + " ui object ID found and actioned").toString();
		return elem;
	}

	// get uiObject by id
	public WebElement getElemByXpath(String xpath) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		WebElement elem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
		//WebElement elem = driver.findElement(By.xpath(xpath));
		test.pass(elem + " ui object ID found and actioned").toString();
		return elem;
	}

	public String getPageTitle(){
		String page  = driver.getTitle();
		return page;
	}

	// get uiObject by id
	public WebElement getElemsById(String id) {

		WebElement elem = (WebElement) driver.findElements(By.id(id));
		test.info(elem + " ui object ID found and actioned").toString();
		return elem;
	}

	// get uiObject by linkText
	public WebElement getElemByLinkText(String linkText) {

		WebElement elem = driver.findElement(By.linkText(linkText));
		test.info(elem + " ui object linkText found and actioned").toString();
		return elem;
	}

	// get uiObject by css
	public WebElement getElemByCSS(String css) {

		WebElement elem = driver.findElement(By.cssSelector(css));
		test.info(elem + " ui object CSS found and actioned").toString();
		return elem;
	}

	public void scrollToView(String id) throws Throwable {
		WebElement element = driver.findElement(By.id(id));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		Thread.sleep(500);
	}

	// class variable

	public String randomIdentifier() {
		StringBuilder builder = new StringBuilder();
		while(builder.toString().length() == 0) {
			int length = rand.nextInt(5)+5;
			for(int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
			if(identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}

	public String getCurrentDay (){
		//Create a Calendar Object
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		//Get Current Day as a number
		int todayInt = calendar.get(Calendar.DAY_OF_MONTH);
		System.out.println("Today Int: " + todayInt +"\n");

		//Integer to String Conversion
		String todayStr = Integer.toString(todayInt);
		System.out.println("Today Str: " + todayStr + "\n");

		return todayStr;
	}

}
