package destello2;


	import java.io.*;
	import java.util.*;


	public class DestelloDebugger extends DestelloCore {
		 long currentPC=startingPC;//stores value of last executed pc
		   int k=0;
		 int MAX =20;
		  private Long nextPC;

		 public long getRegisterValue(int RegID){
		 long registerValue;
		 registerValue=reg[RegID];
		return registerValue ;
	 }
	 
	 public long getMemoryValue(long address){
		 long memoryValue;
		 memoryValue= onChipMemory.readMemory(address);
		 return memoryValue;
	 }
	 
	 public void writeMemory(long address, long data){
		 onChipMemory.writeMemory(address,data);
	 }
	 
	 public void loadInstMemory(String filename){
		 long instaddr =0L;
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
			 instaddr=Long.decode(pc);
			 
			 String data=x.next();
			 long inputinst=Long.parseLong(data, 16);
			 onChipMemory.writeMemory(instaddr, inputinst);	 
			 time++;
		 }
		
		 x.close();
		 startingPC=instaddr-(4*time)+ 4;
		 if(print.level==2||print.level==3)
		 {
			 System.out.println("Starting pc="+startingPC);
		 }
		}
	//this function provides disassembly of at most 20 instructions
	public String[] disassemblyView(long startPC){
		  String[] fullDisassembly= new String[time];
		  reg[16]=startPC;
		  for(int i=0;i<MAX;i++)
		  {
			  fetch();
			  fullDisassembly[i]=decode();
			  if(controlSignals[23]==1)
			  {
				  break;
			
			  }
		  }
		  reg[16]=startingPC;
		  
		   return fullDisassembly;
	 }

	/*debug function accepts breakpoints array
	 * it executes program before breakpoint pc
	 * 'yes' is boolean value set high only by run and continue
	 * 'step' is a boolean value set high by step button only
	 * stop button in the GUI can be eliminated. It finds no use here or can be assigned another functionality 
	 */
	public void debug(Long[] breakpoints,boolean yes, boolean step){
		 // yes takes the value high when either run, continue is pressed
		int i;  
		
		  if(step)
		  {
			  run();
			  step=false;
		  }
		  else if(yes)
		  {
			  nextPC=breakpoints[k];
			  System.out.println("i'm in debug on run "+nextPC);
			  System.out.println("i'm in debug on run time is "+time);
			  if((nextPC < startingPC + 4*time)&&nextPC!=0L)
			  	{
				  	i= (int)(nextPC-currentPC)/4;//number of instructions that need to be calculated  
				  	 k++;
				}
			  else 
			  	{
				  	i=time;
			  	}
			  for(int j=0;j<i;j++)
			  	{
				  run();	  
				  System.out.println("I'm in run in debug");
			  	}
			  
		    
		  }
		  currentPC=reg[16];
		  
		  yes=false;
		  if(print.level==2||print.level==3)
			 {
				 System.out.println("current pc="+currentPC);
			 }
		  if(print.level==3)
			 { for(int x=0;x<18;x++){
				 System.out.println("Register"+x+" "+reg[x]);
			   }
			 }
		  
		}
		  
		  
	}



