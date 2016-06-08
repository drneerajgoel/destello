import java.io.File;
import java.util.Scanner;

public class demo {
//	 static long[] mem = new long[1024];

//	static int[]  breakpoint = {12};
	public static void main(String[] args){
		DestelloDebugger d = new DestelloDebugger();
		d.loadInstMemory("C:\\Users\\Gian Singh\\Documents\\hi.txt");
		d.runProgram(0L);
		
		long Rd = d.getRegisterValue(3);
		System.out.println(Rd);
		}
	
		
 }
