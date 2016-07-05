package destello2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AssemblerUtilities {
	public static int printlevel=2;
	public static int op;
	public static long instEncoding;
	public static String instruction; 
	public static String[] instructionList=new String[50];
	public static int instructionIndex; 
	public static String label;
	public static long labelPc;
	public static String operandString;
	public static String[] operandList=new String[50];
	public static String opcode;
	public static int immediate;
	public static String immediateString;
	public static int isImmediate;
	public static long ProgramCounterAs;
	public static long offset;
	public static String register;
	public static int modifiers;
	
	public static Memory onChipMemory = new Memory(DestelloConfig.MEM_SIZE);
	public  void runAssembler(String assemblyFile) throws IOException
	{
		//PrintWriter writer = new PrintWriter("C:\\Users\\Gian Singh\\Documents\\Test prog\\hexpipelineTest3.txt", "UTF-8");
		List<String> tempList = new ArrayList<String>();
		String[] templist2 = new String[tempList.size()] ;
		String[] labelList=new String[10];
	
		//String in =" test: add r1,r2,r3";
		String[] inputArry=Readfile(assemblyFile);
		for(int i=0;i<inputArry.length;i++)
		{//System.out.println(inputArry[i]);
			//int j=0;//index for label list
			if((IsComment(inputArry[i]))==false);
			{

				ReadLabel(inputArry[i]);
				//System.out.println(label);
				if(label!=null)
				{
					int labelIndex=instructionIndex;
					String labels=new String();
					labels= new StringBuilder().append(label).append(",").append(labelIndex).toString();
					
				 tempList.add(labels);
				 labelList=tempList.toArray(templist2);
				
				    
				    label="";
				    
				}
				instructionList[instructionIndex]=new StringBuilder().append(instruction).append(";").append(instructionIndex).toString();
				System.out.println(instructionList[instructionIndex]);
				instructionIndex++;
			}
		}
for(int i=0;i<labelList.length;i++)
{						System.out.println("label in labeList "+labelList[i]);
	}
		for(int index=0;index<instructionIndex;index++) 
		{
			String[] instTokens=instructionList[index].split(";");
			instruction= instTokens[0];
			RemoveInitialWhiteSpaces(instruction);//removes all initial white spaces in the instruction
			//removeComments(instruction);//removes all the comments after the instruction in a line
			GetOpcode(instruction);// separates opcode from the operands
		 GetOpcodeVal(opcode);//opcode value is obtained
			
			if(op==0||op==1||op==2||op==3||op==4||op==6||op==7||op==10||op==11||op==12)//3-Address instructions 
			{
				 // add we have rd, rs1, rs2 operands
					
				
					operandList=GetOperands(operandString);
					
					int k=0;
					int Rd=50;
					int Rs1=50;
					int Rs2=50;
					int immediateVal=0;
					
					while(k <operandList.length)
					{
						if(printlevel>=1)
						{
							System.out.println(operandList[k]);
						}
						if(k==0)
						{
							Rd=OperandValue(operandList[k]);
						}
						else if(k==1)
						{
							 Rs1=OperandValue(operandList[k]);
						}
						else if(k==2)
						{
							int temp=OperandValue(operandList[k]);
							if(isImmediate==1)
							{
								 Rs2=0;
								 immediateVal=1;
							}
							else
							{
								 Rs2=temp;
							}
						}	
						k++;
					}
				
					
					encodeInstruction(op,0,Rd,Rs1,Rs2,modifiers,isImmediate,3);
					isImmediate=0;
				
				
			}//  end of first if
			
			
			else if(op==20||op==13||op==21)// 0-Address instructions 
			{
				instEncoding= op<<27;
				encodeInstruction(op,0,0,0,0,modifiers,0,0);
			}
			
			
			else if(op==16||op==17||op==18||op==19)// branch instructions
			{
				String labelOperand=operandString;// operand will have the label
 				//search for operand in label list
			if(printlevel>=2)
			{
				System.out.println(labelOperand);
			}
				for(int x=0; x<labelList.length;x++)
				{
					if(printlevel>=2)
					{
						System.out.println("label in labeList "+labelList[x]);
					}
					String[] tokens = labelList[x].split(",");
					String OperandLabel=tokens[0];  
					int instIndex =Integer.parseInt(tokens[1]);
					if(labelOperand.equals(OperandLabel))
					{
						labelPc = instIndex*4;
						offset = labelPc>>2;
					}
				}

				encodeInstruction(op,offset,0,0,0,0,0,1);
			}
			
			else if(op==5)//compare instruction
			{
				operandList=GetOperands(operandString);
				int k=0;
				int Rd=15;
				int Rs1=50;
				int Rs2=50;
				int immediateVal=0;
				
				while(k <operandList.length)
				{
					if(printlevel>=1)
					{
						System.out.println(operandList[k]);
					}
					
					 if(k==0)
					{
						 Rs1=OperandValue(operandList[k]);
					}
					else if(k==1)
					{
						int temp=OperandValue(operandList[k]);
						if(isImmediate==1)
						{
							 Rs2=0;
							 immediateVal=1;
						}
						else
						{
							 Rs2=temp;
						}
					}	
					 k++;
				}
			
				
				encodeInstruction(op,0,Rd,Rs1,Rs2,modifiers,isImmediate,3);
				isImmediate=0;
				
			}
				
			else if(op==8||op==9)// mov and not
			{
				operandList=GetOperands(operandString);
				int k=0;
				int Rd=50;
				int Rs1=0;
				int Rs2=50;
				int immediateVal=0;
				while(k < operandList.length)
				{
					if(printlevel>=1)
					{
						System.out.println(operandList[k]);
					}
					 if(k==0)
					{
						 Rd=OperandValue(operandList[k]);
					}
					else if(k==1)
					{
						int temp=OperandValue(operandList[k]);
						if(isImmediate==1)
						{
							 Rs2=0;
							 immediateVal=1;
						}
						else
						{
							 Rs2=temp;
						}
					}
					 k++;
				}
			
				
				encodeInstruction(op,0,Rd,Rs1,Rs2,modifiers,immediateVal,3);
			}
			
			else if(op==14||op==15)// load and store
			{
				operandList=GetOperands(operandString);
				int k=0;
				int Rd=50;
				int Rs1=50;
				
				int immediateVal=1;
				if(printlevel>=1)
				{
					System.out.println(operandList[k]);
				}
				while(k <operandList.length)
				{
					
					if(k==0)
					{
						Rd=OperandValue(operandList[k]);
					}
					else if(k==1)
					{
						SeparatesRegImmediate(operandList[k]);
					    OperandValue(immediateString);
					    Rs1=OperandValue(register);
					}
						k++;
				}
			
				
				encodeInstruction(op,0,Rd,Rs1,0,modifiers,immediateVal,3);
			}
			
			

			String file1 = String.format("%08X",instEncoding);

		
			ProgramCounterAs = index*4;
			onChipMemory.writeMemory(ProgramCounterAs, instEncoding);
			String file2 =String.format("%08X",ProgramCounterAs);
			String output=new StringBuilder().append("0x").append(file2).append(" ").append(file1).toString();
			
		//	writer.println(output);
			System.out.println(output);
		}
		//writer.close();
		
	}// end of main function
	
	public static String[] Readfile(String filepath) throws IOException
	{
        BufferedReader in = new BufferedReader(new FileReader(filepath));
        String str;

        List<String> list = new ArrayList<String>();
        while((str = in.readLine()) != null)
        {
            list.add(str);
        }

       
		String lineArray[]= list.toArray(new String[0]);;
		
		return lineArray; 
	}
	
	
	// separates the label
	public static void ReadLabel(String in )
	   {
		boolean labelPresent=false;
		  char[] chars=in.toCharArray();
		  for(int i=0;i<chars.length;i++)
		  {
			  if(chars[i]==':')
			  {
				  labelPresent=true;
				  break;
			  }
			  
		  }
		  if(labelPresent==true)
		  {
		  String[] tokens= in.split(":");
		  label=tokens[0];
		  instruction=tokens[1];
		  }
		  else
		  {
			  instruction=in;
		  }
		  if(printlevel>=1)
		  {
			  System.out.print("label: "+label+"\n inst "+instruction);
		  }
	   } 
	   
	// fetches opcode from the instruction  
  public static void GetOpcode(String in )
	  { 
	  boolean spacePresent=false;
	  	char[] space =in.toCharArray();
	  	for(int i=0;i<space.length;i++)
	  	{
	  		if(space[i]==' ')
	  		{
	  			spacePresent=true;
	  			break;
	  		}
	  	}
	  	if(spacePresent==true)
	  	{
	  	String[] tokens= in.split(" ");
	  	opcode=tokens[0];
	  	operandString=tokens[1];// depending upon the opcode out can be a label or operands. it has to be decided by the assembler algorithm 
	  	}
	  	else
	  	{
	  		String[] opco=in.split(",");
	  		opcode=opco[0];
	  	}
	  	
	  		if(printlevel>=1)
	  	{
	  		System.out.println("opcode value from function " + opcode);
	  		System.out.println("operands function " + operandString);
	  	}
	 }
  
  //operands : whether register or immediate all are returned in a string array
  public static String[]  GetOperands(String in  )
  {
	  String[] operands = in.split(",");
	  if(printlevel>=2)
	  {
		  for(int i=0;i<operands.length;i++)
		  {
			  System.out.println(operands[i]);
		  }
	 }
	  return operands;
  }

  //operand values are extracted: register number or immediate is returned	  
  public static int OperandValue(String in )
  { 
	  int reg=50;
	 
	   char[] register =in.toCharArray();
	   if(printlevel>=2)
	   {
		   for(int i=0;i<register.length;i++)
		   {
		   System.out.println(register[i]);
		   }
	   }
	   if(register[0]!='r')
	   {
		   isImmediate=1;
		   immediate=Integer.parseInt(in); 
	   }
	   else
	   {
		   if(register.length>=3&&register[2]!=']')
		   {
			   reg= (register[1]-'0')*10 + (register[2]-'0');
		   }
		   else{
		  reg=register[1]-'0';
		   }
	   }
	   if(printlevel>=1)
	   {
		   System.out.println("value of register operand "+reg);
	   }
	   return reg;
  } 
  
  //opcode value is returned
  public static void GetOpcodeVal(String opcode)
  { 
	 
	  if(opcode.equals("add")||opcode.equals("addu")||opcode.equals("addh"))
	  {
		  op= 0;
		  if(opcode.equals("add"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("addu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("addh"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("sub")||opcode.equals("subu")||opcode.equals("subh"))
	  {
		  op= 1;
		  if(opcode.equals("sub"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("subu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("sub"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("mul")||opcode.equals("mulu")||opcode.equals("mulh"))
	  {
		  op= 2;
		  if(opcode.equals("mul"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("mulu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("mulh"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else  if(opcode.equals("div")||opcode.equals("divu")||opcode.equals("divh"))        
	  {
		  op= 3;
		  if(opcode.equals("div"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("divu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("divh"))		  
		  {	  
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("mod")||opcode.equals("modu")||opcode.equals("modh"))
	  {
		  op= 4;
		  if(opcode.equals("mod"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("modu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("modh"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("cmp")||opcode.equals("cmpu")||opcode.equals("cmph"))
	  {
		  op= 5;
		  if(opcode.equals("cmp"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("cmpu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("cmph"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("and")||opcode.equals("andu")||opcode.equals("andh"))
	  {
		  op= 6;
		  if(opcode.equals("and"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("andu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("andh"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("or")||opcode.equals("oru")||opcode.equals("orh"))
	  {
		  op= 7;
		  if(opcode.equals("or"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("oru"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("orh"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("not")||opcode.equals("notu")||opcode.equals("noth"))
	  {
		  op= 8;
		  if(opcode.equals("not"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("notu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("noth"))
		  {
			  modifiers=2;
		  }
		  
	  }
	  
	  else if(opcode.equals("mov")||opcode.equals("movu")||opcode.equals("movh"))
	  {
		  op= 9;
		  if(opcode.equals("mov"))
		  {
			  modifiers=0;
		  }
		  else if(opcode.equals("movu"))
		  {
			  modifiers=1;
		  }
		  else if(opcode.equals("movh"))
		  {
			  modifiers=2;
		  }
	  }
	  
	  else if(opcode.equals("lsl"))
	  {
		  op= 10;
	  }
	  
	  else if(opcode.equals("lsr"))
	  {
		  op= 11;
	  }
	  
	  else if(opcode.equals("asr"))
	  {
		  op= 12;
	  }
	  
	  else if(opcode.equals("nop"))
	  {
		  op= 13;
	  }
	  
	  else if(opcode.equals("ld"))
	  {
		  op= 14;
	  }
	  
	  else if(opcode.equals("st"))
	  {
		  op= 15;
	  }
	  
	  else if(opcode.equals("beq"))
	  {
		  op= 16;
	  }
	  
	  else if(opcode.equals("bgt"))
	  {
		  op= 17;
	  }
	  
	  else if(opcode.equals("b"))
	  {
		  op= 18;
	  }
	  
	  else if(opcode.equals("call"))
	  {
		  op= 19;
	  }
	  
	  else if(opcode.equals("ret"))
	  {
		  op= 20;
	  }
	  
	  else if(opcode.equals("hlt"))
	  {
		  op= 21;
	  }
	  if(printlevel>=1)
	  {
		  System.out.println("opcode value "+op);
	  }
	  
	 
	 
  }
  
  //string with all white spaces removed is returned
  public static String RemoveWhiteSpaces(String in)
  {
	  in = in.replaceAll("\\s","");
	  return in;
  }
  
  public static void RemoveInitialWhiteSpaces(String in)
  {
	  boolean whiteSpacePresent=false;
	  char[] letters = in.toCharArray();
	  if(letters[0]==' '||letters[0]=='\t'||letters[0]=='\n'||letters[0]=='\r')
	  	{
		  whiteSpacePresent=true;  
		}
	  if(whiteSpacePresent==true)
	  {
	  in = in.trim();
	  }
	  instruction=in;
	  
  }
  
  public static boolean IsComment(String in)
  {
	char[] letters = in.toCharArray();
	/*for(int z=0;z<letters.length;z++)
	{
		System.out.println(letters[z]);
	}
*/	if(letters[0]=='/')return true;
		
	else 
		return false;
  }  
  
  public static void removeComments(String in)
  {
	  int x=0;
	  char[] insts;
	  insts=null;
	  char[] chars=in.toCharArray();
	  while(chars[x]!='/')
	  {
		  insts[x]=chars[x];
	  }
	  instruction=String.valueOf(insts);
  }
  
  public static void SeparatesRegImmediate(String in)//will be used for st and ld instructions
  {
	  String[] tokens=in.split("\\[");
	   immediateString= tokens[0];
	   register=tokens[1];
  }
  
  public static void encodeInstruction(int opc,long offs, int dest, int src1, int src2, int modifier, int immediateValue,int type)
  {
	  if(type==0)
	  {
		  instEncoding = opc<<27;
		  instEncoding = (instEncoding + offs);
	  }
	  else if(type==1)
	  {
		  instEncoding =opc<<27; 
		  instEncoding = (instEncoding + offs);
		  
	  }
	  else if(type==3)
	  {
		  immediateValue= isImmediate<<26;
		  dest=dest<<22;
		  src1=src1<<18;
		  modifier=modifier<<16;
		  src2=src2<<14;
		  
		  	instEncoding = opc<<27;
			instEncoding = instEncoding + immediateValue;
			instEncoding = instEncoding + dest;
			instEncoding = instEncoding + src1;
			if (isImmediate==1)
			{
				System.out.println("immediate value "+immediate);
				instEncoding=instEncoding + modifier;
				instEncoding=instEncoding + immediate;
				isImmediate=0;
			}
			else
			{
				instEncoding=instEncoding + src2;
			}
	  }
	  
  }
  
  public  void resetAssembler()
  {
	  	op=0;
		instEncoding=0L;
		instruction=""; 
		instructionList=new String[50];
		instructionIndex=0; 
		label="";
		labelPc=0L;
		operandString="";
		operandList=new String[50];
		opcode="";
		immediate=0;
		immediateString="";
		isImmediate=0;
		ProgramCounterAs=0L;
		offset=0L;
		register="";
		modifiers=0;
		
		onChipMemory = new Memory(DestelloConfig.MEM_SIZE);
  }
}
