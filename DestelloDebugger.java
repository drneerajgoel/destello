/*Copyright (C) 2016, IIT Ropar
This file is part of Destello.

    Destello is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Destello is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
Authors: Sourodeep, Gian, Neeraj (change the order according to file)
Contact: destello-support@gmail.com
 * */
package destello2;


	import java.io.*;
	import java.util.*;
    

	public class DestelloDebugger extends DestelloCore {
		 long currentPC=startingPC;//stores value of last executed pc
		   int k=0;
		 int MAX =20;
		  private Long nextPC;
		  int cycles=0;
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
	 
	 public void loadMemory(long address, long data){
		 //System.out.println("I'm in load memory in debugger "+address+" "+data);
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
			 cycles++;
		 }
		
		 x.close();
		 startingPC=instaddr-(4*cycles)+ 4;
		 if(print.level>=2)
		 {
			 System.out.println("Starting pc= "+startingPC);
			 System.out.println("Cycles= "+cycles);
		 }
		
	}
	//this function provides disassembly of at most 20 instructions
	public String[] disassemblyView(long startPC){
	 
		String[] fullDisassembly= new String[cycles];
		  ProgramCounter=startPC;
		  for(int i=0;i<MAX;i++)
		  { 
			  fetch();
			  fullDisassembly[i]=decode(true);
			  if(print.level>=2){
			  System.out.println(" disassembly in debugger "+fullDisassembly[i]);
			  }
			   if(getNthBit(control,23)==1)
			   {
				  break;
			
			   }
		  }
		  
		  ProgramCounter=startingPC;
		  control=0;
		
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
		
		  if(step)
		  {
			  run();
			  step=false;
		  }
		  else if(yes)
		  {
			  nextPC=breakpoints[k];
			  System.out.println("i'm in debug on run nextPC and currentPc"+nextPC+" "+currentPC);
			  System.out.println("i'm in debug on run time is "+time);
			  while(getNthBit(control,23)!=1)
			  	{
				  run();
				  if(ProgramCounter==nextPC)
				  {
					  k++;
					  break;
				  }
				  time++;

				}

		    
		  }
		  currentPC=ProgramCounter;
		  
		  yes=false;
		  if(print.level>=2)
			 {
				 System.out.println("current pc="+currentPC);
			 }
			  
		}
		  
		  
	}



