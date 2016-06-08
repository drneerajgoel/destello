import java.io.File;
import java.util.Scanner;

public class demo {
//	 static long[] mem = new long[1024];

//	static int[]  breakpoint = {12};
	public static void main(String[] args){
		DestelloDebugger d = new DestelloDebugger();
		d.loadInstMemory("C:\\Users\\Gian Singh\\Documents\\hi.txt");
		String[] s4=d.runProgram(0L);
		
		long Rd = d.getRegisterValue(3);	
		for(int q=0;q<4;q++)
		{
		System.out.println(s4[q]);
		}
		System.out.println("Result is:"+Rd);
		}
	
		
 }
