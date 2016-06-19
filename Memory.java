package destello2;


	public class Memory {
		long [] mem = new long[5];
	   
		// constructor  to initialize memory
		public Memory(int size){
	      mem =  new long[size];
		}
	   public  long readMemory(long pC){
		   int index=(int)(pC/4);
		   return mem[index];	   
	   }
	   public void writeMemory(long addr, long data){
		   int index=(int)addr/4;
		   mem[index]=data;
	   }
	   }
	   

	   
	   



