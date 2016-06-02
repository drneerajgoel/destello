public class Memory {
	int[] mem = new int[5];
   /* this is the function which can be used to initialize memoery instead of a constructor
    * public void memCreation(int size){
	    mem = new int[size];
	   }*/
	// constructor  to initialize memory
	public Memory(int size){
		int[] mem= new int[size];
	}
   public int readMemory(int addr){
	   int index=addr/4;
	   return mem[index];	   
   }
   public  void writeMemory(int addr, int data){
	   int index=addr/4;
	   mem[index]=data;
   }
   }
   

   
   

