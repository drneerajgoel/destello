import java.io.File;
import java.util.Scanner;

public class demo {
	static long[]  breakpoint = {0L};
	public static void main(String[] args){
		DestelloDebugger d = new DestelloDebugger();
		d.reset();
		d.loadInstMemory("C:\\Users\\Gian Singh\\Documents\\hi.txt");
	
		 DebugPrint printD = new DebugPrint();
		 printD.level=3;
		d.debug(breakpoint, true, false);
	
	
	}
	
		
 }
