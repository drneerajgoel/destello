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
Authors: Sourodeep, Gian, Neeraj 
Contact: destello-support@gmail.com
 * */
package destello2;


	public class DestelloCore {
		
		 protected static long[] reg= new long[DestelloConfig.NUM_REG];

		 /*//r0-r13 general purpose, 
		  * r14- stack pointer,
		  * r15- return address,
		  * r16- program counter and
		  * r17- psw(flags register) 
		  * since flags.e and flags.gt cannot be set simultaneously therefore r[17]=1(flags.e) r[17]=2 flags.gt r[17]=0 default
		  * */
		
		 static AssemblerUtilities assembler =new AssemblerUtilities();
		 public static long inst;
		 public static long ProgramCounter=0;
		 static int PSW;
		 private static long nop=1744830464;
		 public static long branchPC;
		 public  static int time=0;
		 protected  long startingPC;
	     static boolean ConflictFlag=false;
	   
	     // public static onChipMemory2 = new assembler.onChipMemory();// instruction memory created 4K bytes
         static Register If_Dec= new Register(DestelloConfig.PIPELINE);
         static Register Dec_Ex= new Register(DestelloConfig.PIPELINE);
         static Register Ex_Ma= new Register(DestelloConfig.PIPELINE);
         static Register Ma_Wb= new Register(DestelloConfig.PIPELINE);
		 static DebugPrint print = new DebugPrint();
		 protected static int control ;// controlsignals[22]=isBranchTaken controlsignals[23]=nop
		 protected static int isBranchTaken;
		public static long[] pipelineReg2=new long[7];
		 
  protected static void fetch()
  {//start of fetch method
	 
	//  long PC = ProgramCounter;
			 
			 long[] pipelineReg1=new long[7];
			 
		
			 
		if((ConflictFlag==false))
		  {  
		     if(isBranchTaken==1)
			  {
		    	 ProgramCounter= branchPC;
		    	 isBranchTaken=0;
			  }
		     else
		     {
		    	 ProgramCounter= (ProgramCounter+4);     
		     }
		  }

		  long PC = ProgramCounter;
		  pipelineReg1[0]=ProgramCounter;
		inst = AssemblerUtilities.onChipMemory.readMemory(PC); 
			  		pipelineReg1[1]=inst;

	  if(print.level==2)
	  {
    	 System.out.println("I'm in fetch "+ pipelineReg1[1]+" "+pipelineReg1[0]);
	  }
	
	  if(ConflictFlag==false)
	    {
		    If_Dec.Write(pipelineReg1);
		}
		 
	}//end of fetch
		
		 //decode function will take boolean input to decide b/w whether the call has been made for disassembly or evaluation 
		 
		 protected static String decode(boolean diss)
		 {//start of decode
					 
			 long[] pipelineReg3=new long[7];// stores variables of decode and wites to Dec_Ex reg
			 if(diss==true)
			 {
				 pipelineReg2[1]=inst;
			 }
			 else if(ConflictFlag==true)
			 {
				 pipelineReg2[1]=pipelineReg2[1];
			 }
			 else 
			 {
			  pipelineReg2=If_Dec.Read();
			 }
			 

			  long operand1;
	          long operand2;
	          long Off;
			 String operation= new String();
			 String destination= new String();
			 String source1= new String();
			 String source2= new String();	 
           
			 int op =(int) getNBits(pipelineReg2[1],27,5);       // opcode extracted
			 int IsImmediate= (int)getNBits(pipelineReg2[1],26,1); // immediate extracted
			 int Rd= (int)getNBits(pipelineReg2[1],22,4);// destination address extracted
			 destination="R"+Integer.toString(Rd);
			 int Rs1=(int)getNBits(pipelineReg2[1],18,4);//source 1 address extracted
			 source1="R"+Integer.toString(Rs1);
			 int Rs2=(int)getNBits( pipelineReg2[1],14,4);//source 2 address extracted
			 int Imm=(int)getNBits (pipelineReg2[1],0,16);            // 16bit immediate extracted 
			 Off= getNBits(pipelineReg2[1],0,27);         //27 bit offset extracted
			 int modifiers=(int)getNBits(pipelineReg2[1],16,2);           //2 bit modifiers extracted
			
			 
			 long immx;                       // immediate value to be used for operations 
			 if(modifiers==1)
			 {
				 immx = Imm;
			 }
			 else if(modifiers==2)
			 {
				 immx=Imm<<16;
			 }
			 else
			 {
				 immx=Imm;
			 }
			 // fetch  operands for use in execution state
			
			 operand1= reg[Rs1];
		    
			 if(IsImmediate==1)
			 {
				 operand2= immx;
				 source2=Long.toString(immx);
			 }
			 else
			 {
				 operand2=reg[Rs2];
				 source2="R"+Integer.toString(Rs2);
			 }
			 
			 
				 control=0;//control signals set to zero
			 	 
	
	System.out.println("value of diss flag "+diss);	  
	
	//  check for conflicts and stall written here	
		if(diss==false&&If_Dec.pipeline==true)
		{
			 long[] dec_ex_reg=Dec_Ex.Read();
			 long[] ex_ma_reg=Ex_Ma.Read();
			 long[] ma_wb_reg=Ma_Wb.Read();
			 long inst1 =dec_ex_reg[6];
			 long inst2 =ex_ma_reg[6];
			 long inst3=ma_wb_reg[4];
			 boolean conflict=false;
			 
		
			 if(pipelineReg2[1]!=0||inst1!=0||inst2!=0||inst3!=0)
			 {
				 
				 if(inst1!=0){	
					 conflict=CheckConflict(pipelineReg2[1],inst1);
				 }
			   
				 if(inst2!=0&&conflict==false){
					 conflict=CheckConflict(pipelineReg2[1],inst2);
				 }
			  
				 if(inst3!=0&&conflict==false){
					 conflict=CheckConflict(pipelineReg2[1],inst3);
					 }
			 }
			  			 
			 if(conflict==true)
			 {
				 ConflictFlag=true;
			 }
		  
			 else 	ConflictFlag=false;
				 
				 
		if(print.level>=2)
		{
			 System.out.println("value of conflict flag "+ConflictFlag);	  
		}	  
		
		if(ConflictFlag==true)
			  {
				  op=13;// opcode is changed to nop
			  }
	}	  
		// generation of control signals based upon opcode	 
			 switch(op)
			 {//start of switch case
			 case 0: //add isAdd
			 {
				 
				 control=(int)setNthBit(control,9,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="add";
				 else if(modifiers==1) operation = "addu";
				 else if(modifiers==2) operation = "addh";
				 break;
			 }
			 case 1://sub isSub
			 {
				 control=(int)setNthBit(control,10,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="sub";
				 else if(modifiers==1)operation="subu";
				 else if(modifiers==2)operation="subh";
				 break;
			 } 
			 case 2://mul isMul
			 {
				 control=(int) setNthBit(control,12,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="mul";
				 else if(modifiers==1)operation="mulu";
				 else if(modifiers==2)operation="mulh";
				 break;
			 } 	        
			 case 3://div isDiv
			 {
				 control=(int) setNthBit(control,13,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="div";
				 else if(modifiers==1)operation="divu";
				 break;
			 } 	 
			 case 4: // mod isMod
			 {
				 control=(int) setNthBit(control,14,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="mod";
				 else if(modifiers==1)operation="modu";
				 else if(modifiers==2)operation="moh";
				 break;
			 }
			 case 5: // cmp isCmp
			 {
				 control=(int)setNthBit(control,11,1);
				 if(modifiers==0)operation="cmp";
				 else if(modifiers==1)operation="cmpu";
				 else if(modifiers==2)operation="cmph";
				 break;
			 }
			 case 6: // and isAnd
			 {
				 control=(int)setNthBit(control,19,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="and";
				 else if(modifiers==1)operation="andu";
				 else if(modifiers==2)operation="andh";
				 break;
			 }
			 case 7: // or isOr
			 {
				 control=(int)setNthBit(control,18,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="or";
				 else if(modifiers==1)operation="oru";
				 else if(modifiers==2)operation="orh";
				 break;
			 }
			 case 8: //not isNot
			 {
				 control=(int)setNthBit(control,20,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="not";
				 else if(modifiers==1)operation="notu";
				 else if(modifiers==2)operation="noth";
				 break;
			 }
			 case 9: //mov isMov
			 {
				 control=(int) setNthBit(control,21,1);
				 control=(int) setNthBit(control,6,1);//is wb
				 if(modifiers==0)operation="mov";
				 else if(modifiers==1)operation="movu";
				 else if(modifiers==2)operation="movh";
				 break;
			 }
			 case 10: //lsl isLsl
			 {
				 control=(int)setNthBit(control,15,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 operation="lsl";
				 break;
			 }
			 case 11: // lsr isLsr
			 {
				 control=(int)setNthBit(control,16,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 operation="lsr";
				 break;
			 }
			 case 12: // asr isAsr
			 {
				 control=(int)setNthBit(control,17,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 operation="asr";
				 break;
			 }
			 case 13: //nop does nothing and breaks
			 {
				 operation="nop";
				 break;
			 }
			 case 14: //ld isLd
			 {
				 control=(int)setNthBit(control,1,1);
				 control=(int)setNthBit(control,6,1);//is wb
				 operation="ld";
				 break;
			 }
			 case 15: // st isSt
			 {
				 control=(int)setNthBit(control,0,1);
				 operation="st";
				 break;
			 }
			 case 16: //beq isBeq
			 {
				 control=(int)setNthBit(control,2,1);
				 operation="beq";
				 break;
			 }
			 case 17: //bgt isBgt
			 {
				 control=(int)setNthBit(control,3,1);
				 operation="bgt";
				 break;
			 }
			 case 18: //b isUBranch
			 {
				 control=(int)setNthBit(control,7,1);
				 operation="b";
				 break;
			 }
			 case 19: //call isCall
			 {
				 control=(int)setNthBit(control,8,1);//isCall
				 control=(int)setNthBit(control,6,1);//iswb
				 operation="call";
				 break;
			 }
			 case 20: //ret  isRet
			 {
				 control=(int)setNthBit(control,4,1);
				 operation="ret";
				 break;
			 }
			 case 21: //halt
			 {
				 control=(int)setNthBit(control,23,1);
				 operation="halt";
				 break;
			 }
			 default:
				 operation =" invalid";
			 }//end of switch
		// all the control signals are generated and decoding ends here	
			 
		String dissassembly= new String();
		
		if(operation=="ret"||operation=="nop"||operation=="halt")
		{
				 dissassembly = new StringBuilder().append(operation).toString(); 
				
		}
		
		else if(operation=="call"||operation=="b"||operation=="beq"||operation=="bgt")
		{
					 dissassembly = new StringBuilder().append(operation).append(" ").append(Off).toString(); 			 
		}
		
		else if(operation=="add"||operation=="sub"||operation=="mul"||operation=="div"||operation=="mod"||operation=="and"||operation=="or"||operation=="lsl"||operation=="lsr"||operation=="asr")
		{
					 dissassembly = new StringBuilder().append(operation).append(" ").append(destination).append(",").append(source1).append(",").append(source2).toString(); 
		}
			 
		else if(operation=="cmp")
		{
					 dissassembly = new StringBuilder().append(operation).append(" ").append(source1).append(",").append(source2).toString(); 			
		}
		
		else if(operation=="mov"||operation=="not")
		{
					 dissassembly = new StringBuilder().append(operation).append(" ").append(destination).append(",").append(source2).toString(); 			 
		}
		
		else if(operation=="ld"||operation=="st")
		{
					 dissassembly = new StringBuilder().append(operation).append(" ").append(destination).append(",").append(source2).append(" ").append(source1).toString(); 					 
		}
		
		else dissassembly= "Invalid Instruction";
			 
				 
		pipelineReg3[0]= control;
			 
		if(ConflictFlag==true)
		{   
				 pipelineReg3[1]=pipelineReg2[0];
				 pipelineReg3[2]=0;
				 pipelineReg3[3]=0;
				 pipelineReg3[4]=0;
				 pipelineReg3[5]=0;
				 pipelineReg3[6]=nop;
		}
		
		else
		{
				 pipelineReg3[1]=pipelineReg2[0];  //program counter
				 pipelineReg3[2]=Off;
				 pipelineReg3[3]=Rd;
				 pipelineReg3[4]=operand1;
				 pipelineReg3[5]=operand2;
				 pipelineReg3[6]=pipelineReg2[1];// instruction
		} 
				
		Dec_Ex.Write(pipelineReg3);
		if(print.level>=1)
		{
			System.out.println(pipelineReg2[0]+" "+dissassembly+"value of rd"+Rd);
		}
			
		return dissassembly;
			 
			 
			 
	}// end of decode
		 
// execute stage
		 
	protected static void execute()
	{
			 long[] pipelineReg4=new long[7];
			 long Result=0;
			 pipelineReg4=Dec_Ex.Read();//takes input from Dec_Ex reg
			 int rd =(int)pipelineReg4[3];
			 
			if(getNthBit(pipelineReg4[0],21)==1)// execution of mov
			 {
				 reg[rd]=pipelineReg4[5];
			 }
			
			else
			{
				 Result=aluUnit(pipelineReg4);//execution of all arithmetic and logical instructions
				 branchUnit(pipelineReg4);//execution of all branch instructions
				 if(print.level>=2)
				 {
					 System.out.println(" operand1, operand2,Alu Result = "+pipelineReg4[4] +" "+pipelineReg4[4] +" "+Result);
				 }
			}
			
			if (print.level>=2)
			 {
				 System.out.println("I'm in execute"+"value of rd"+rd);
			 }
			
			long[] pipelineReg5=new long[7];// register to write into ex_ma reg
				 pipelineReg5[0]= pipelineReg4[0];
			 
			
			pipelineReg5[1]=pipelineReg4[1];//program counter
			pipelineReg5[2]=pipelineReg4[4];//operand1
			pipelineReg5[3]=pipelineReg4[5];//operand 2
			pipelineReg5[4]=rd;
			pipelineReg5[5]=Result;// alu result
			pipelineReg5[6]=pipelineReg4[6];//instruction
			Ex_Ma.Write(pipelineReg5);
	}// end of execute

	// Alu unit
  protected static long aluUnit(long pipelineReg[])
  {
			long aluResult=0L; 
			 if(getNthBit(pipelineReg[0],9)==1||getNthBit(pipelineReg[0],0)==1||getNthBit(pipelineReg[0],1)==1)// add
			 {
				 aluResult=pipelineReg[4]+pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],10)==1)//sub
			 {
				 aluResult=pipelineReg[4]-pipelineReg[5];
				 
			 }
			 else if(getNthBit(pipelineReg[0],11)==1)//cmp
			 {
				 aluResult=pipelineReg[4]-pipelineReg[5];
				 if(aluResult==0)
				 {
					PSW=1; 
				 }
				 else if (aluResult>0)
				 {
					 PSW=2;
				 }
				 else
				 {
					 PSW=0;
				 }
			 }
			 else if(getNthBit(pipelineReg[0],12)==1)//mul
			 {
				 aluResult=pipelineReg[4]*pipelineReg[5];			
			 }
			 else if(getNthBit(pipelineReg[0],13)==1)//div
			 {
				 aluResult=pipelineReg[4]/pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],14)==1)//mod
			 {
				 aluResult=pipelineReg[4]%pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],15)==1)//lsl
			 {
				 aluResult=pipelineReg[4]<<pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],16)==1)//lsr
			 {
				 aluResult=pipelineReg[4]>>>pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],17)==1)//asr
			 {
				 aluResult=pipelineReg[4]>>pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],18)==1)//or
			 {
				 aluResult=pipelineReg[4]|pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],19)==1)//and
			 {
				 aluResult=pipelineReg[4]&pipelineReg[5];
			 }
			 else if(getNthBit(pipelineReg[0],20)==1)//not
			 {
				 aluResult=~pipelineReg[4];
			 }
			 return aluResult;
	}
	
  // branch unit	 
   protected static  void branchUnit(long pipelineReg[])
   {
			if(getNthBit(pipelineReg[0],2)==1)// beq 
			 {
				 if (PSW==1)
				 {
					 isBranchTaken=1;
					 branchPC=pipelineReg[2]<<2;
				 }
			 }
			
			else if(getNthBit(pipelineReg[0],3)==1)// bgt
			 {
				 if (PSW==2)
				 {
					 isBranchTaken=1;
					 branchPC = pipelineReg[2]<<2;
				 }		 
			 }
			
			else if(getNthBit(pipelineReg[0],4)==1)// only ret
			 {
				isBranchTaken=1;
				 branchPC=reg[15];//return address
			 }
			 
			else if(getNthBit(pipelineReg[0],7)==1)//only b
			 {
				isBranchTaken=1;
				 branchPC=pipelineReg[2]<<2;
				 
			 }
			 else if(getNthBit(pipelineReg[0],8)==1)//only call
			 {
				 isBranchTaken=1;
				 branchPC=pipelineReg[2]<<2;
				 if(print.level>=2)
				 {
					 System.out.println(" global PC and local  in branch unit "+ProgramCounter+" "+pipelineReg[1]);
				 }
				 reg[15]= pipelineReg[1]+ 4;
			 }
	
			// branch control unit starts here
			branchControlUnit();
			/*if(getNthBit(control,22)==1&&If_Dec.pipeline==true)
			{
				long[] Reg =new long [7];
				Reg=If_Dec.Read();
				Reg[1]=nop;// opcode for nop
				If_Dec.Write(Reg);
				for(int i=0;i<6;i++)// all control signals set to 0
				{
					Reg[i]=0;
				}
				Reg[6]=nop;
				Dec_Ex.Write(Reg);
				
			}*/
			
		}
		 
	protected static void memoryAccessUnit()
	{
			 long[] pipelineReg6=new long[7];// ot take input from Ex_Ma reg
			 pipelineReg6=Ex_Ma.Read();
			 long[] pipelineReg7=new long[7];// to write to MA_Wb reg
			 long mar;
			 long mdr;	 
			 int rd=(int)pipelineReg6[4]; 
			 if(getNthBit(pipelineReg6[0],0)==1)      //execution of store
			 {
				 mar = pipelineReg6[5];
				 mdr = reg[rd];
				 AssemblerUtilities.onChipMemory.writeMemory(mar, mdr);
				 pipelineReg7[0]=(int)setNthBit(pipelineReg7[0],0,0);
			 }
			 else if(getNthBit(pipelineReg6[0],1)==1)  // execution of load
			 {
			    mar= pipelineReg6[5];
			    pipelineReg7[2] =AssemblerUtilities.onChipMemory.readMemory(mar);
			 }
			 if (print.level>=2)
			 {
				 System.out.println("I'm in memory access unit"+"value of rd "+ rd);
			 }
			
			 pipelineReg7[0]=pipelineReg6[0];//control signals			 
			 pipelineReg7[1]=pipelineReg6[1];
			 pipelineReg7[3]=pipelineReg6[5];
			 pipelineReg7[4]=pipelineReg6[6];//instruction
			 pipelineReg7[5]=rd;
			 Ma_Wb.Write(pipelineReg7);
		 }
	//write back unit	 
	protected static void writeBackUnit()
		 {
			 long[] pipelineReg8=new long[7];// to get input from Ma_Wb reg
			 pipelineReg8=Ma_Wb.Read();
			 int rd=(int)pipelineReg8[5];
			 if(getNthBit(pipelineReg8[0],6)==1)//isWb is high
			 {
				 if(getNthBit(pipelineReg8[0],1)==1)//isLd is high
				  {
					 reg[rd]=pipelineReg8[2];
					 pipelineReg8[0]=(int)setNthBit(pipelineReg8[0],1,0);
					 control=(int)setNthBit(control,1,0);
					 
				  }
				 else if(getNthBit(pipelineReg8[0],8)==1)//isCall is high
				 {
					 reg[15]=pipelineReg8[1]+4;
					 pipelineReg8[0]=(int)setNthBit(pipelineReg8[0],8,0);
					 control=(int)setNthBit(control,8,0);
				 }
				 else if(getNthBit(pipelineReg8[0],21)==1)//isMov is high
				 {
					 reg[rd]=reg[rd];
					 pipelineReg8[0]=(int)setNthBit(pipelineReg8[0],21,0);
					 control=(int)setNthBit(control,21,0);
				 }
				 else
				 {
					 reg[rd]=pipelineReg8[3];
				 }
			}
			
		 if (print.level>=2)
			 {
				 System.out.println("I'm in writeback unit"+"Rd value = " +rd+" "+reg[rd]);
			 }
	}
		 
   public static String run()
	{
			 fetch();
	    	 String s1 =decode(false);
			 execute();
			 memoryAccessUnit();
			 writeBackUnit();
			 if(ConflictFlag==false)
			 {
			 If_Dec.Clock();
			 }
			 Dec_Ex.Clock();
			 Ex_Ma.Clock();
			 Ma_Wb.Clock();
			 control=0;
			return s1;
	}
		 

	
   static boolean CheckConflict(long A, long B)
   {
	   int opA;
	   int opB;
	   int rdA;
	   int rdB;
	   int src1;
	   int src2;
	   int dest;
	   int I;
	   boolean hasSrc=true;
	   
	   //check for the opcode of instruction A
	    I=(int)getNthBit(A,26);
	    
	    rdA=(int)getNBits(A,22,4);
	   
	    opA=(int)getNBits(A,27,5);
	   
	    if(print.level>=2)
	    {
	    System.out.println("valuue of opA "+opA);
	    }
	    if(opA==13||opA==18||opA==16||opA==17||opA==19||opA==21)
	    return false;
	    
	   
	  //check for the opcode of instruction B
	    rdB=(int)getNBits(B,22,4);
	   	    opB=(int)getNBits(B,27,5);
	   
	    if(print.level>=2)
	    {
	    System.out.println("valuue of opB "+opB);
	    }
	    if(opB==13||opB==18||opB==16||opB==17||opB==19||opB==21)
	    	return false;
	    
	    
	    //Check for sources of A
	    
	    src1=(int)getNBits(A,18,4);
	   
	    src2=(int)getNBits(A,14,4);
	    
	     if(opA==15)
	     {
	    	 src2=rdA;
	     }
	     if(opA==20)
	     {
	    	 src1=(int)reg[15];
	     }
	 //set the destination
	     dest=rdB;
	     if(opB==19)
	     {
	    	 dest=(int)reg[15];
	     }
	   System.out.println("destination "+rdB );  
	   System.out.println("source 1 "+src1 );  
	   System.out.println("source2 "+src2 );  
	 // check the second operand to see if it is a register 
	    if(opA==15)
	    {
	    	if(I==1)
	    	{
	    		hasSrc=false;
	    	}
	    }
	    
	    if(opB==9||opB==8)
	    {
	    	if(src1==dest)
	    	{
	    		return false;
	    	}
	    }
	 //Detects conflicts 
	    
	    if(src1==dest)
	    	return true;
	    else if((hasSrc==true)&&(src2==dest))
	    	return true;

	    return false;
   }		 
   

	 /* Exatracts Nth bit from input number starting form 0
	    */
	 public static long getNthBit(long in, long N)
	 {
		 int bit=(int) ((in >> N) & 0x1);
		 return bit;
		 
	 }
	 //ref: http://stackoverflow.com/questions/47981/how-do-you-set-clear-and-toggle-a-single-bit-in-c-c
	 private static long setNthBit(long in, long N, long setValue) 
	 {
		 in= (in | (setValue & 0x1) << N);  // sets the (N+1)th bit
		 return in;
	 }

	 /* Extract N bits starting from position pos in input number
	    */ // works fine with same shift value as used before
	 private static long getNBits(long in, long pos, long N)
	 {
		 long mask = (1 << N) - 1;
		 int bits=(int) ((in >> pos) & mask);
		 return  bits;
	 }

	public static void branchControlUnit()
	{
		if(isBranchTaken==1&&If_Dec.pipeline==true)
		{
			long[] Reg =new long [7];
			Reg=If_Dec.Read();
			Reg[1]=nop;// opcode for nop
			If_Dec.Write(Reg);
			for(int i=0;i<6;i++)// all control signals set to 0
			{
				Reg[i]=0;
			}
			Reg[6]=nop;
			Dec_Ex.Write(Reg);
			
		}
	}
}// end of class 
		 




