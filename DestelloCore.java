
public class DestelloCore {
 protected static long[] reg= new long[18];
 /*//r0-r13 general purpose, 
  * r14- stack pointer,
  * r15- return address,
  * r16- program counter and
  * r17- psw(flags register) 
  * since flags.e and flags.gt cannot be set simultaneously therefore r[17]=1(flags.e) r[17]=2 flags.gt r[17]=0 default
  * */

 public static int instMemSize;
 public static int dataMemSize;
 private static long inst;
 private static int Rd;
 private static long ldResult;
 private static long operand1;
 private static long operand2;
 private static long AluResult;
 private static long branchPC;
 private static long Off;
 
 static Memory instmemory = new Memory(1024);// instruction memory created 4K bytes
 static Memory datamemory = new Memory(1024);// data Memory created 2K Bytes

 protected static int[] controlsignals = new int[24];// controlsignals[22]=isBranchTaken controlsignals[23]=nop
 public long time;
 
 protected static void fetch(){//start of fetch method
	 long PC = reg[16];
     inst = instmemory.readMemory(PC);
	 if(controlsignals[22]==1)
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
		 controlsignals[9]=1;
		 controlsignals[6]=1;//is wb
		 operation="add";
		 break;
	 }
	 case 1://sub isSub
	 {
		 controlsignals[10]=1;
		 controlsignals[6]=1;//is wb
		 operation="sub";
		 break;
	 } 
	 case 2://mul isMul
	 {
		 controlsignals[12]=1;
		 controlsignals[6]=1;//is wb
		 operation="mul";
		 break;
	 } 	        
	 case 3://div isDiv
	 {
		 controlsignals[13]=1;
		 controlsignals[6]=1;//is wb
		 operation="div";
		 break;
	 } 	 
	 case 4: // mod isMod
	 {
		 controlsignals[14]=1;
		 controlsignals[6]=1;//is wb
		 operation="mod";
		 break;
	 }
	 case 5: // cmp isCmp
	 {
		 controlsignals[11]=1;
		 operation="cmp";
		 break;
	 }
	 case 6: // and isAnd
	 {
		 controlsignals[19]=1;
		 controlsignals[6]=1;//is wb
		 operation="and";
		 break;
	 }
	 case 7: // or isOr
	 {
		 controlsignals[18]=1;
		 controlsignals[6]=1;//is wb
		 operation="and";
		 break;
	 }
	 case 8: //not isNot
	 {
		 controlsignals[20]=1;
		 controlsignals[6]=1;//is wb
		 operation="not";
		 break;
	 }
	 case 9: //mov isMov
	 {
		 controlsignals[21]=1;
		 controlsignals[6]=1;//is wb
		 operation="mov";
		 break;
	 }
	 case 10: //lsl isLsl
	 {
		 controlsignals[15]=1;
		 controlsignals[6]=1;//is wb
		 operation="lsl";
		 break;
	 }
	 case 11: // lsr isLsr
	 {
		 controlsignals[16]=1;
		 controlsignals[6]=1;//is wb
		 operation="lsr";
		 break;
	 }
	 case 12: // asr isAsr
	 {
		 controlsignals[17]=1;
		 controlsignals[6]=1;//is wb
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
		 controlsignals[1]=1;
		 controlsignals[6]=1;//is wb
		 operation="ld";
		 break;
	 }
	 case 15: // st isSt
	 {
		 controlsignals[0]=1;
		 operation="st";
		 break;
	 }
	 case 16: //beq isBeq
	 {
		 controlsignals[2]=1;
		 operation="beq";
		 break;
	 }
	 case 17: //bgt isBgt
	 {
		 controlsignals[3]=1;
		 operation="bgt";
		 break;
	 }
	 case 18: //b isUBranch
	 {
		 controlsignals[7]=1;
		 operation="b";
		 break;
	 }
	 case 19: //call isUBrranch
	 {
		 controlsignals[8]=1;//isCall
		 controlsignals[7]=1;//is UBranch
		 controlsignals[6]=1;//iswb
		 operation="call";
		 break;
	 }
	 case 20: //ret isUBranch
	 {
		 controlsignals[7]=1;
		 operation="ret";
		 break;
	 }
	 case 21: //halt
	 {
		 controlsignals[23]=1;
		 operation="halt";
		 break;
	 }
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
	 return dissassembly;
	 
	 
	 
 }// end of decode
 
 // execute stage
 
 protected static void execute(){
	 aluUnit();//execution of all arithmetic and logical instructions
	 branchUnit();//execution of all branch instructions
	if(controlsignals[21]==1)// execution of mov
	 {
		 reg[Rd]=operand2;
	 }
	 
 }// end of execute
 
 protected static long aluUnit(){
	 
	 if(controlsignals[9]==1)// add
	 {
		 AluResult=operand1+operand2;
	 }
	 else if(controlsignals[10]==1)//sub
	 {
		 AluResult=operand1-operand2;
		 
	 }
	 else if(controlsignals[11]==1)//cmp
	 {
		 AluResult=operand1-operand2;
		 if(AluResult==0)
		 {
			reg[17]=1; 
		 }
		 else if (AluResult>0)
		 {
			 reg[17]=2;
		 }
		 else
		 {
			 reg[17]=0;
		 }
	 }
	 else if(controlsignals[12]==1)//mul
	 {
		 AluResult=operand1*operand2;
		
	 }
	 else if(controlsignals[13]==1)//div
	 {
		 AluResult=operand1/operand2;
		
	 }
	 else if(controlsignals[14]==1)//mod
	 {
		 AluResult=operand1%operand2;
		 
	 }
	 else if(controlsignals[15]==1)//lsl
	 {
		 AluResult=operand1<<operand2;
		 
	 }
	 else if(controlsignals[16]==1)//lsr
	 {
		 AluResult=operand1>>>operand2;
		 
	 }
	 else if(controlsignals[17]==1)//asr
	 {
		 AluResult=operand1>>operand2;
		 
	 }
	 else if(controlsignals[18]==1)//or
	 {
		 AluResult=operand1|operand2;
		
	 }
	 else if(controlsignals[19]==1)//and
	 {
		 AluResult=operand1&operand2;
		 
	 }
	 else if(controlsignals[20]==1)//not
	 {
		 AluResult=~operand1;
		 
	 }
	 return AluResult;
 }
 
 protected static void branchUnit(){
	if(controlsignals[2]==1)// beq 
	 {
		 if (reg[17]==1)
		 {
			 controlsignals[22]=1;
			 branchPC=Off<<2;
		 }
	 }
	 else if(controlsignals[3]==1)// bgt
	 {
		 if (reg[17]==2)
		 {
			 controlsignals[22]=1;
			 branchPC=Off<<2;
		 }		 
	 }
	 else if(controlsignals[4]==1)// ret
	 {
		 controlsignals[22]=1;
		 branchPC=reg[15];
	 }
	 else if(controlsignals[7]==1)//b,call,ret
	 {
		 controlsignals[22]=1;
	 }
	 else if(controlsignals[8]==1)
	 {
		 reg[15]= reg[16]+4;
	 }
 }
 
protected static void memoryAccessUnit(){
	 long mar;
	 long mdr;	 
	 if(controlsignals[0]==1)      //execution of store
	 {
	
		 mar = operand2 + operand1;
		 mdr = reg[Rd];
		 datamemory.writeMemory(mdr, mar);
		 controlsignals[0]=0;
	 }
	 else if(controlsignals[1]==1)  // execution of load
	 {
	    mar= operand1 +operand2;
	   ldResult =datamemory.readMemory(mar);
	 }
 }
 
 protected static void writeBackUnit(){
 	 
	 if(controlsignals[6]==1)//isWb is high
	 {
		 if(controlsignals[1]==1)//isLd is high
		  {
			 reg[Rd]=ldResult;
			 controlsignals[1]=0;
			 
		  }
		 else if(controlsignals[8]==1)//isCall is high
		 {
			 reg[15]=reg[16]+4;
			 controlsignals[8]=0;
		 }
		 else if(controlsignals[21]==1)//isMov is high
		 {
			 reg[Rd]=reg[Rd];
			 controlsignals[21]=0;
		 }
		 else
		 {
			 reg[Rd]=AluResult;
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
		 controlsignals[j]=0;
	 }
	   instMemSize=0;
	   dataMemSize=0;
	   inst=0;
	   Rd=0;
	   ldResult=0;
	   operand1=0;
	   operand2=0;
	   AluResult=0;
	   branchPC=0;
	   Off=0;
	 
 }
 }// end of class 
 

