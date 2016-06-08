import java.io.*;
import java.util.*;


public class DestelloDebugger extends DestelloCore {
	 private static long currentpc=0;
	  private static int k=0;
	 private static long nextpc=0;
 public long getRegisterValue(int RegID){
	 long registerValue;
	 registerValue=reg[RegID];
	return registerValue ;
 }
 
 public long getMemoryValue(long address){
	 long memoryValue;
	 memoryValue= datamemory.readMemory(address);
	 return memoryValue;
 }
 public void loadInstMemory(String filename){
	Scanner x=null;
	try{
	x = new Scanner( new File(filename));
	}
	catch(Exception e){
		System.out.println("File Not Found");
	}
	 while(x.hasNext())
	 {
		 String pc = x.next();
		 long instaddr=Long.decode(pc);
		 
		 String data=x.next();
		 long inputinst=Long.parseLong(data, 16);
		 instmemory.writeMemory(instaddr, inputinst);	 
	 }
	
	 x.close();
	}
 public static void debug(int[] breakpoints, boolean yes){
	 // yes takes the value high when either run, continue is pressed
	  currentpc=reg[16];
	  k=0;
	  nextpc=breakpoints[k];
	  int i= (int)(nextpc-currentpc)/4;//number of instructions that need to be calculated  
	
	  for(int x=0;x<breakpoints.length;x++ )
	  {
		  if(yes){
	     for(int j=0;j<i;j++)
	     {
	    	// run(currentpc);
	    	 //currentpc=currentpc+4;
	     }
	     k++; 
	     nextpc=breakpoints[k];
	     i= (int)(nextpc-currentpc)/4;
		  yes=false;
		  }
	  } 
	  }
 public  String[] runProgram(long pc)
 {
	 String[] s3 = new String[10];
	reg[16]=pc;
	int y=0;
	 while(controlsignals[23]!=1)
	 { 
		 String s2=run();
		 s3[y]= s2;
		 y++;
		 
	 }
	 return s3;
 }
}
