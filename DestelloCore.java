
public class DestelloCore {
 protected static long[] reg= new long[18];

 /*//r0-r13 general purpose, 
  * r14- stack pointer,
  * r15- return address,
  * r16- program counter and
  * r17- psw(flags register) 
  * since flags.e and flags.gt cannot be set simultaneously therefore r[17]=1(flags.e) r[17]=2 flags.gt r[17]=0 default
  * */

 public static int memSize;

 private static long inst;
 private static int Rd;
 private static long ldResult;
 private static long operand1;
 private static long operand2;
 private static long aluResult;
 private static long branchPC;
 private static long Off;
 public  int time=0;
 protected static long startingPC;
 static Memory onChipMemory = new Memory(1024);// instruction memory created 4K bytes


 protected static int[] controlSignals = new int[24];// controlsignals[22]=isBranchTaken controlsignals[23]=nop

 
 protected static void fetch(){//start of fetch method
	 long PC = reg[16];
     inst = onChipMemory.readMemory(PC);
	 if(controlSignals[22]==1)
	  {
		  reg[16]= branchPC;
	  }
     else
     {
    	 reg[(int) 16]= (reg[16]+4);     
     }
     
    
	  }//end of fetch
	 
 protected static String decode(){//start of decode

	 String operation= new String();
	 String destination= new String();
	 String source1= new String();
	 String source2= new String();	 
	 int opcode=(int) (inst>>27);            
	 int op =opcode&31;                     // opcode extracted
	 int i= (int)inst>>26;
	 i=i&1;                                 // immediate extracted
	 Rd= (int)inst>>22;
	 Rd=Rd&15;// destination address extracted
	  destination="R"+Integer.toString(Rd);
	 int Rs1=(int)inst>>18;
	 Rs1=Rs1&15;//source 1 address extracted
	 source1="R"+Integer.toString(Rs1);
	 int Rs2=(int) inst>>14;
	 Rs2=Rs2&15;                             //source 2 address extracted
	 int Imm=(int) (inst&65535);            // 16bit immediate extracted 
	 Off= inst&134217727;         //27 bit offset extracted
	 int modifiers=(int)inst>>16;           //2 bit modifiers extracted
	 modifiers=modifiers&3;
	 
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
    
	 if(i==1)
	 {
		 operand2= immx;
		 source2=Long.toString(immx);
	 }
	 else
	 {
		 operand2=reg[Rs2];
		 source2="R"+Integer.toString(Rs2);
	 }
// generation of control signals based upon opcode	 
	 switch(op)
	 {//start of switch case
	 case 0: //add isAdd
	 {
		 controlSignals[9]=1;
		 controlSignals[6]=1;//is wb
		 operation="add";
		 break;
	 }
	 case 1://sub isSub
	 {
		 controlSignals[10]=1;
		 controlSignals[6]=1;//is wb
		 operation="sub";
		 break;
	 } 
	 case 2://mul isMul
	 {
		 controlSignals[12]=1;
		 controlSignals[6]=1;//is wb
		 operation="mul";
		 break;
	 } 	        
	 case 3://div isDiv
	 {
		 controlSignals[13]=1;
		 controlSignals[6]=1;//is wb
		 operation="div";
		 break;
	 } 	 
	 case 4: // mod isMod
	 {
		 controlSignals[14]=1;
		 controlSignals[6]=1;//is wb
		 operation="mod";
		 break;
	 }
	 case 5: // cmp isCmp
	 {
		 controlSignals[11]=1;
		 operation="cmp";
		 break;
	 }
	 case 6: // and isAnd
	 {
		 controlSignals[19]=1;
		 controlSignals[6]=1;//is wb
		 operation="and";
		 break;
	 }
	 case 7: // or isOr
	 {
		 controlSignals[18]=1;
		 controlSignals[6]=1;//is wb
		 operation="and";
		 break;
	 }
	 case 8: //not isNot
	 {
		 controlSignals[20]=1;
		 controlSignals[6]=1;//is wb
		 operation="not";
		 break;
	 }
	 case 9: //mov isMov
	 {
		 controlSignals[21]=1;
		 controlSignals[6]=1;//is wb
		 operation="mov";
		 break;
	 }
	 case 10: //lsl isLsl
	 {
		 controlSignals[15]=1;
		 controlSignals[6]=1;//is wb
		 operation="lsl";
		 break;
	 }
	 case 11: // lsr isLsr
	 {
		 controlSignals[16]=1;
		 controlSignals[6]=1;//is wb
		 operation="lsr";
		 break;
	 }
	 case 12: // asr isAsr
	 {
		 controlSignals[17]=1;
		 controlSignals[6]=1;//is wb
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
		 controlSignals[1]=1;
		 controlSignals[6]=1;//is wb
		 operation="ld";
		 break;
	 }
	 case 15: // st isSt
	 {
		 controlSignals[0]=1;
		 operation="st";
		 break;
	 }
	 case 16: //beq isBeq
	 {
		 controlSignals[2]=1;
		 operation="beq";
		 break;
	 }
	 case 17: //bgt isBgt
	 {
		 controlSignals[3]=1;
		 operation="bgt";
		 break;
	 }
	 case 18: //b isUBranch
	 {
		 controlSignals[7]=1;
		 operation="b";
		 break;
	 }
	 case 19: //call isUBrranch
	 {
		 controlSignals[8]=1;//isCall
		 controlSignals[7]=1;//is UBranch
		 controlSignals[6]=1;//iswb
		 operation="call";
		 break;
	 }
	 case 20: //ret isUBranch
	 {
		 controlSignals[7]=1;
		 operation="ret";
		 break;
	 }
	 case 21: //halt
	 {
		 controlSignals[23]=1;
		 operation="halt";
		 break;
	 }
	 default:
		 operation =" invalid";
	 }//end of switch
// all the control signals are generated and decoding ends here	
	 String dissassembly= new String();
	 if(operation=="ret"||operation=="nop"||operation=="halt"){
		 dissassembly = new StringBuilder().append(operation).toString(); 
		
	 }
	 else if(operation=="call"||operation=="b"||operation=="beq"||operation=="bgt"){
			 dissassembly = new StringBuilder().append(operation).append(" ").append(Off).toString(); 
			 
}
	 else if(operation=="add"||operation=="sub"||operation=="mul"||operation=="div"||operation=="mod"||operation=="and"||operation=="or"||operation=="lsl"||operation=="lsr"||operation=="asr"){
			 dissassembly = new StringBuilder().append(operation).append(" ").append(destination).append(",").append(source1).append(",").append(source2).toString(); 
			
 }
	 else if(operation=="cmp"){
			 dissassembly = new StringBuilder().append(operation).append(" ").append(source1).append(",").append(source2).toString(); 
			
}
	 else if(operation=="mov"||operation=="not"){
			 dissassembly = new StringBuilder().append(operation).append(" ").append(destination).append(",").append(source2).toString(); 
			 
}
	 else if(operation=="ld"||operation=="st"){
			 dissassembly = new StringBuilder().append(operation).append(" ").append(destination).append(",").append(source2).append(" ").append(source1).toString(); 

			 
}
	 else
		 dissassembly= "Invalid Instruction";
	 return dissassembly;
	 
	 
	 
 }// end of decode
 
 // execute stage
 
 protected static void execute(){
	 aluUnit();//execution of all arithmetic and logical instructions
	 branchUnit();//execution of all branch instructions
	if(controlSignals[21]==1)// execution of mov
	 {
		 reg[Rd]=operand2;
	 }
	 
 }// end of execute
 
 protected static long aluUnit(){
	 
	 if(controlSignals[9]==1)// add
	 {
		 aluResult=operand1+operand2;
	 }
	 else if(controlSignals[10]==1)//sub
	 {
		 aluResult=operand1-operand2;
		 
	 }
	 else if(controlSignals[11]==1)//cmp
	 {
		 aluResult=operand1-operand2;
		 if(aluResult==0)
		 {
			reg[17]=1; 
		 }
		 else if (aluResult>0)
		 {
			 reg[17]=2;
		 }
		 else
		 {
			 reg[17]=0;
		 }
	 }
	 else if(controlSignals[12]==1)//mul
	 {
		 aluResult=operand1*operand2;
		
	 }
	 else if(controlSignals[13]==1)//div
	 {
		 aluResult=operand1/operand2;
		
	 }
	 else if(controlSignals[14]==1)//mod
	 {
		 aluResult=operand1%operand2;
		 
	 }
	 else if(controlSignals[15]==1)//lsl
	 {
		 aluResult=operand1<<operand2;
		 
	 }
	 else if(controlSignals[16]==1)//lsr
	 {
		 aluResult=operand1>>>operand2;
		 
	 }
	 else if(controlSignals[17]==1)//asr
	 {
		 aluResult=operand1>>operand2;
		 
	 }
	 else if(controlSignals[18]==1)//or
	 {
		 aluResult=operand1|operand2;
		
	 }
	 else if(controlSignals[19]==1)//and
	 {
		 aluResult=operand1&operand2;
		 
	 }
	 else if(controlSignals[20]==1)//not
	 {
		 aluResult=~operand1;
		 
	 }
	 return aluResult;
 }
 
 protected static void branchUnit(){
	if(controlSignals[2]==1)// beq 
	 {
		 if (reg[17]==1)
		 {
			 controlSignals[22]=1;
			 branchPC=Off<<2;
		 }
	 }
	 else if(controlSignals[3]==1)// bgt
	 {
		 if (reg[17]==2)
		 {
			 controlSignals[22]=1;
			 branchPC=Off<<2;
		 }		 
	 }
	 else if(controlSignals[4]==1)// ret
	 {
		 controlSignals[22]=1;
		 branchPC=reg[15];
	 }
	 else if(controlSignals[7]==1)//b,call,ret
	 {
		 controlSignals[22]=1;
	 }
	 else if(controlSignals[8]==1)
	 {
		 reg[15]= reg[16]+4;
	 }
 }
 
protected static void memoryAccessUnit(){
	 long mar;
	 long mdr;	 
	 if(controlSignals[0]==1)      //execution of store
	 {
	
		 mar = operand2 + operand1;
		 mdr = reg[Rd];
		 onChipMemory.writeMemory(mdr, mar);
		 controlSignals[0]=0;
	 }
	 else if(controlSignals[1]==1)  // execution of load
	 {
	    mar= operand1 +operand2;
	   ldResult =onChipMemory.readMemory(mar);
	 }
 }
 
 protected static void writeBackUnit(){
 	 
	 if(controlSignals[6]==1)//isWb is high
	 {
		 if(controlSignals[1]==1)//isLd is high
		  {
			 reg[Rd]=ldResult;
			 controlSignals[1]=0;
			 
		  }
		 else if(controlSignals[8]==1)//isCall is high
		 {
			 reg[15]=reg[16]+4;
			 controlSignals[8]=0;
		 }
		 else if(controlSignals[21]==1)//isMov is high
		 {
			 reg[Rd]=reg[Rd];
			 controlSignals[21]=0;
		 }
		 else
		 {
			 reg[Rd]=aluResult;
		 }
	 }
 }
 
 public static String run()
 {
	 fetch();
	String s1 =decode();
	 execute();
	 memoryAccessUnit();
	 writeBackUnit();
	return s1;
 }
 
 public void reset(){
	 int j;
	 for(j=0;j<18;j++)
	 {
		 reg[j]=0;
	 }
	 for(j=0;j<24;j++)
	 {
		 controlSignals[j]=0;
	 }
	   memSize=0;
	   
	   inst=0;
	   Rd=0;
	   ldResult=0;
	   operand1=0;
	   operand2=0;
	   aluResult=0;
	   branchPC=0;
	   Off=0;
	 
 }
 }// end of class 
 

