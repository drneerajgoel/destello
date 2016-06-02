
 public class test {
	public static void  main(String []args ){
		Memory instruction =new Memory(10);
		//instruction.memCreation(10);
		instruction.writeMemory(8,20);
		int x=instruction.readMemory(8);
		System.out.printf("data is %d", x);
	}

}
