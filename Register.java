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

public class Register 
{
	boolean pipeline  ;// variable to decide b/w pipelined and non-pipelined execution
    long[] in= new long[7];// in register to hold input values to the stages of pipeline
    long[] out= new long[7];// out register to hold the output values of stages of pipeline
    
	public Register(boolean x)// constructor to initialize pipeline
	{
		  pipeline=x;
	}
	
	public long[] Read()
	{
		
		if(pipeline){
			return out;
		}
		else
		{
			return in;
		}
	}
	public void Write (long[] value)
	{ 
		for(int i=0; i<7;i++)
		{
		in[i]=value[i];
		}
		
	}
	
	public void Clock()
	{
		if(pipeline)
		{ 
			for(int i=0; i<7;i++)
			{
			out[i]=in[i];
			}
			
		}
	}
	
}
