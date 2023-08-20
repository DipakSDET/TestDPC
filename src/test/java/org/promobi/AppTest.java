package org.promobi;


import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    private AndroidDriver driver;
    private final String PORT = "4723";
    private final String HOST = "127.0.0.1";
    private String deviceName = "pixel_7_pro";


    @BeforeMethod
    public void setUp() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android")
                .setAutomationName("uiautomator2")
                .setDeviceName(deviceName)
                .setNoReset(true)
                .setNewCommandTimeout(Duration.ofSeconds(60));
        System.out.println(getUrl(HOST, PORT));
        driver = new AndroidDriver(getUrl(HOST, PORT), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void setTime() throws InterruptedException {
        openTestDpcApp();
        String TextValue = driver.findElements(By.xpath("//*[@resource-id='android:id/title']")).get(5).getAttribute("text");
        System.out.println("set Time from App" +TextValue);
        driver.findElements(By.xpath("//*[@resource-id='android:id/title']")).get(5).click();
        waitForElementVisible(By.id("com.afwsamples.testdpc:id/input"));
        driver.findElement(By.id("com.afwsamples.testdpc:id/input")).clear();
        driver.findElement(By.id("com.afwsamples.testdpc:id/input")).sendKeys("1629467134");
        waitForElementVisible(By.id("android:id/button1"));
        driver.findElement(By.id("android:id/button1")).click();
        openSettingsApp();
        WebElement system = scrollToWebElement(By.xpath("//*[contains(@text,'System')]"));
        system.click();
        waitForElementVisible(By.xpath("//*[contains(@text,'Date & time')]"));
        WebElement dateAndTime = driver.findElement(By.xpath("//*[contains(@text,'Date & time')]"));
        dateAndTime.click();
        Thread.sleep(3000);
        List<WebElement> time = driver.findElements(By.id("android:id/summary"));
        String timeStr = time.get(1).getText();
        System.out.println("Time in Setting is getting reflected as :: " +timeStr);
        Assert.assertTrue(stringMatchingRegularExpression("^([2]):[0][7-9] [APap][Mm]$", timeStr), "Timestamp are not matching");
    }

    @Test
    public void setTimeZone() throws InterruptedException {
        openTestDpcApp();
        String TextValue = driver.findElements(By.xpath("//*[@resource-id='android:id/title']")).get(6).getAttribute("text");
        System.out.println("set Time from App" +TextValue);
        driver.findElements(By.xpath("//*[@resource-id='android:id/title']")).get(6).click();
        waitForElementVisible(By.id("com.afwsamples.testdpc:id/input"));
        driver.findElement(By.id("com.afwsamples.testdpc:id/input")).clear();
        driver.findElement(By.id("com.afwsamples.testdpc:id/input")).sendKeys("Asia/Kolkata");
        waitForElementVisible(By.id("android:id/button1"));
        driver.findElement(By.id("android:id/button1")).click();
        openSettingsApp();
        WebElement system = scrollToWebElement(By.xpath("//*[contains(@text,'System')]"));
        system.click();
        waitForElementVisible(By.xpath("//*[contains(@text,'Date & time')]"));
        WebElement dateAndTime = driver.findElement(By.xpath("//*[contains(@text,'Date & time')]"));
        dateAndTime.click();
        Thread.sleep(3000);
        List<WebElement> time = driver.findElements(By.id("android:id/summary"));
        String timeZoneString = time.get(3).getText();
        System.out.println("Time zone in Setting is getting reflected as :: " +timeZoneString);
        Assert.assertEquals(timeZoneString, "GMT+05:30 India Standard Time", "Timezone are not matching");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null){
            driver.quit();
        }
    }

    public boolean stringMatchingRegularExpression(String regularExpression, String inputString){
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(inputString);

        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private void openSettingsApp() {
        driver.startActivity(new Activity("com.android.settings", ".Settings"));
    }

    private void openTestDpcApp() {
        driver.startActivity(new Activity("com.afwsamples.testdpc", ".PolicyManagementActivity"));
    }

    public void scrollVertically()
    {
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Dimension size = driver.manage().window().getSize();
        int startX = size.getWidth() / 2;
        int startY = size.getHeight() / 2;
        int endX = startX;
        int endY = (int) (size.getHeight() * 0.25);
        Sequence sequence = new Sequence(finger1, 1)
                .addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),startX,startY))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger1, Duration.ofMillis(100)))
                .addAction(finger1.createPointerMove(Duration.ofMillis(100), PointerInput.Origin.viewport(), endX,endY))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(sequence));
    }

    public WebElement scrollToWebElement(By byLocator){
        WebElement element = null;
        boolean isElementVisible = false;
        while (!isElementVisible) {
            try{
                List<WebElement> elements = driver.findElements(byLocator);
                if (!elements.isEmpty()){
                    element = elements.get(0);
                    isElementVisible = true;
                }
                scrollVertically();
            } catch (NoSuchElementException e){
                e.printStackTrace();
            }
        }
        return element;
    }


    private URL getUrl(String host, String port) {
        try {
            return new URL("http://"+host+":"+port);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void waitForElementVisible(By element){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(element)));
    }

}
