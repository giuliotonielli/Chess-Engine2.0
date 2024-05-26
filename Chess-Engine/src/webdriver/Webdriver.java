package webdriver;

// accounts and passwords:
// enpassantmate1 - q$g*c,%t!suBCE2   (low rated)
// enpassantmate2 - q$g*c,%t!suBCE2

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import main.Move;
import java.time.Duration;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class Webdriver {
	WebDriver driver;
	String color;
	String accountName = "enpassantmate2";
	String password = "q$g*c,%t!suBCE2";
	Hashtable<Double, Character> valueToLetter = new Hashtable<Double, Character>();
	
	public void start(int time) {
		setHashtableUp();
		
//		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--remote-allow-origins=*"); //TODO ADD ME 
		driver = (WebDriver) new ChromeDriver(); // TODO no cast and options inside parenthesis
		
	    driver.get("https://www.chess.com/play/online");
	     
	    // close bottom window
	    driver.findElement(By.xpath("/html/body/div[1]/div[2]/button")).click();
	   
	    // click on login
	    driver.findElement(By.xpath("/html/body/div[2]/div[8]/div[3]/a[9]")).click();
	    
	    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
	    
	    // username, password and submit
	    try {
	    	// login form 1
	    	driver.findElement(By.xpath("/html/body/div[35]/div/div/div[1]/div[2]/form/div[1]/div/input")).sendKeys(accountName);
	    	driver.findElement(By.xpath("/html/body/div[35]/div/div/div[1]/div[2]/form/div[2]/div/input")).sendKeys(password);
		    driver.findElement(By.xpath("/html/body/div[35]/div/div/div[1]/div[2]/form/button")).click();
	    }
	    catch(Exception e) {
	    	 // login form 2
	    	driver.findElement(By.xpath("/html/body/div[2]/div/main/div/form/div[1]/input")).sendKeys(accountName);
		    driver.findElement(By.xpath("/html/body/div[2]/div/main/div/form/div[2]/input")).sendKeys(password);
		    driver.findElement(By.xpath("/html/body/div[2]/div/main/div/form/button")).click();
	    }
	    
	    // sometimes a premium version add shows
	    try {
	    	driver.findElement(By.xpath("/html/body/div[38]/div/div[2]/div/div[3]/button")).click();
	     }
	    catch (Exception e) {}
	    
	    
	    // time
	    if (time != 10) {
	    	driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div[1]/div[1]/button")).click();
	    	
	    	if (time == 1) 
	    		driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div[1]/div[1]/div/div[1]/div[2]/button[1]")).click();
	    
	    	else if (time == 3) 
	    		driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div[1]/div[1]/div/div[2]/div[2]/button[1]")).click();
	    }
	    
	    // click on play game
		driver.findElement(By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div[1]/button")).click();
	}
	
	private void setHashtableUp() {
		valueToLetter.put(1.0, 'p');
		valueToLetter.put(-1.0, 'p');
		valueToLetter.put(3.0, 'n');
		valueToLetter.put(-3.0, 'n');
		valueToLetter.put(3.2, 'b');
		valueToLetter.put(-3.2, 'b');
		valueToLetter.put(5.0, 'r');
		valueToLetter.put(-5.0, 'r');
		valueToLetter.put(9.0, 'q');
		valueToLetter.put(-9.0, 'q');
		valueToLetter.put(100.0, 'k');
		valueToLetter.put(-100.0, 'k');
	}

	public int getColor() {
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));
	    String element;
	    
	    // element is our own clock element, if its color is white then we're white
		try {
			element = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div/div[4]")).getAttribute("class");
		}
		catch (Exception e) {
			System.out.println(e);
			return getColor();
		}
		
	    if (element.contains("white")) {
	    	System.out.println("we white");
	    	color = "w";
	    	return 1;
	    }
	    
	    else {
	    	System.out.println("we black");
	    	color = "b";
	    	return -1;
	    }
	}

	public String getOpponentsMove() {
		String elementTo, elementFrom;
		
		// opponents clock element, if it contains word "player" in class its his turn
		while (driver.findElement(By.xpath("/html/body/div[4]/div[1]/div/div[4]")).getAttribute("class").contains("y")) {
			wait(100);
		}
		
		// highlighted squares
		elementFrom = driver.findElement(By.xpath("/html/body/div[4]/div[2]/chess-board/div[2]")).getAttribute("class");
		elementTo = driver.findElement(By.xpath("/html/body/div[4]/div[2]/chess-board/div[3]")).getAttribute("class");
			
		try {
			return "" + elementFrom.substring(17, 19) + elementTo.substring(17, 19);
		} catch (Exception e) {
			return getOpponentsMove();
		}
	}

	private void wait(int t) {
		try {
			TimeUnit.MILLISECONDS.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendMoveToDriver(Move move) {
		long t1 = System.currentTimeMillis();
		String p = color + valueToLetter.get(move.value);
		String d = "square-" + (move.a+1) + "" + (8-move.b);
		int offsetX, offsetY;
		
		WebElement piece = driver.findElement(By.cssSelector(".piece."+p+"."+d));
		piece.click();
		
		int pieceSize = piece.getSize().height;
		
		offsetY = (move.d - move.b)*pieceSize;
		
		System.out.println(System.currentTimeMillis() - t1);
		// TODO half a second is spent doing the next 3 lines of code!
		
		// move pointer by offset and click
		if (color == "w") {
			offsetX = (move.c - move.a)*pieceSize;
			new Actions(driver).moveToElement(piece).moveByOffset(offsetX, offsetY).click().perform();
		}
		else {
			offsetX = (move.a - move.c)*pieceSize;
			new Actions(driver).moveToElement(piece).moveByOffset(offsetX, -offsetY).click().perform();
		}
		
		System.out.println(System.currentTimeMillis() - t1);
	}
	
	public double getTime() {
		String time = driver.findElement(By.xpath("/html/body/div[4]/div[3]/div/div[4]/span")).getText();
		
		int seconds = ((int)time.charAt(2)*10 + (int)time.charAt(3))*5/3;
		
		return (int)time.charAt(0) + seconds/100;
	}
}

