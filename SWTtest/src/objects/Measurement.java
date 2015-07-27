package objects;

import java.util.Iterator;

import org.apache.commons.collections4.queue.CircularFifoQueue;

/*
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
*/

public class Measurement {
	
	final static int 	cycleOffset	  	  =	1000;	// 1 sec offset
	final static int	valueOffset		  = 10;		// 10 ms
	
	private CircularFifoQueue 		allValues;
	private CircularFifoQueue		maximaBuffer;
	private double		max;
	private CircularFifoQueue		minimaBuffer;
	private double		min;
	private String		uid;
	private int 		valuesPerLastCycle;
	private CircularFifoQueue		valuesCnt;
	private int 		index;						// 0 = primary sensor, 1 = secondary
	private double[]	lastValues = new double[3];
	/**
	 * constructor
	 * @param max max number of values
	 */
	public Measurement(int maxValues, int maxCycles, String uid, int i)
	{
		this.allValues 			= new CircularFifoQueue(maxValues);
		this.maximaBuffer 		= new CircularFifoQueue(maxCycles);
		this.minimaBuffer 		= new CircularFifoQueue(maxCycles);
		this.uid				= uid;
		max 					= 0;
		min 					= 999; 
		valuesPerLastCycle		= 0;
		this.valuesCnt			= new CircularFifoQueue(maxCycles);
		this.index 				= i;
		for (int k=0; k<lastValues.length; k++) lastValues[k] = 0;
	}
	
	
	/**
	 * add a new value
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void addValue(double value)
	{
		allValues.add(value);
		valuesPerLastCycle++;
				
		// add a new value to the last values array  
		updateLastValueArray(value);
		
		// check for extrema
		if (detectExtrema()==1)
		{
			addMax(lastValues[1]);
			valuesCnt.add(valuesPerLastCycle);
			valuesPerLastCycle = 0;						
		}
		else if (detectExtrema() == -1)
		{
			addMin(lastValues[1]);
		}
				
		updateAverage();		
	}

	
	/**
	 * add a value to maxima buffer
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void addMax(double value)
	{
		this.maximaBuffer.add(value);
		double tmp;
		max = -999;
		
		// find maxima
		for(Iterator<Double> iterator = maximaBuffer.iterator(); iterator.hasNext();)
		{
			tmp = (double)iterator.next();
			if (tmp > max) max = tmp;
		}

		// call update maxima event
		functions.Events.updateMaxima(uid, max, this.index);
	}
	
	
	/**
	 * add a value to minima buffer
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void addMin(double value)
	{
		this.minimaBuffer.add(value);
		double tmp;
		min = 999;

		// find maxima
		for(Iterator<Double> iterator = minimaBuffer.iterator(); iterator.hasNext();)
		{
			tmp = (double)iterator.next();
			if (tmp < min) min = tmp;
		}
		
		// call update minima event
		functions.Events.updateMinima(uid, min, this.index);		
	}
	
	
	/**
	 * returns average number of measurements per cycle, 
	 * considering maxCycles cycles
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private int getAverageMeasurementsNumber()
	{
		long sum=0;
		
		// get sum
		for(Iterator<Integer> iterator = valuesCnt.iterator(); iterator.hasNext();)
		{
			sum += (int)iterator.next();
		}
		
		return (int) (sum/valuesCnt.size());
	}
	
	
	/**
	 * add last value to a 5 values array, which is storing last 5 values.
	 */
	private void updateLastValueArray(double nValue)
	{
		for (int i=0; i<lastValues.length-1; i++)
		{
			lastValues[i] = lastValues[i+1];
		}
		lastValues[(lastValues.length)-1] = nValue; 
	}
	
	
	/**
	 * detect extrema, considering the last values array
	 * @return	-1 for local minima
	 * 			 1 for local maxima
	 * 			 0 for rest 
	 */
	private int detectExtrema()
	{
		if 		((lastValues[0] < lastValues[1]) &&
		   		 (lastValues[1] > lastValues[2])) return 1;
		else if ((lastValues[0] > lastValues[1]) &&
				 (lastValues[1] < lastValues[2])) return -1;
		else return 0;
	}
	
	
	/**
	 * calculate the average value of last n cycles
	 */
	@SuppressWarnings("unchecked")
	private void updateAverage()
	{
		int cnt = 0;
		int i   = 0;
		double sum = 0;
		double avg = 0;
		
		// get entity of values stored in last n cycles
		for(Iterator<Integer> iterator = valuesCnt.iterator(); iterator.hasNext();)
		{
			cnt += (int)iterator.next();
		}		
		if (cnt < (allValues.size()-1))		
		{
			// we need less values than we have stored
			// get sum of last values
			for(Iterator<Double> iterator = allValues.iterator(); iterator.hasNext();)
			{
				i++;
				if (i>(allValues.size()-cnt))
					{					
					sum += (double)iterator.next();
					}				
				else iterator.next();
				if (sum > Double.MAX_VALUE - 1000) System.out.println("SUM is too big!!");
			}
			// get average
			avg = sum / cnt;			
		}
		else 
		{
			// buffer are not complete yet, so build a sum over all available values
			// get sum of last values
			for(Iterator<Double> iterator = allValues.iterator(); iterator.hasNext();)
			{
				i++;
				sum += (double)iterator.next();
				if (sum > Double.MAX_VALUE - 1000) System.out.println("SUM is too big!!");
			}
			// get average
			avg = sum / i;			
		}
	
		// call update average event
		functions.Events.updateAverage(uid, avg, this.index);
	}
	
	/**
	 * might be helpful for later
	 * to update average value live, without taking into account all the previous values
	 * we might use following formula:
	 * 	"running average"
		a) M = Sum / N; then
		b) Sum = M * N; and
		c) N = Sum / M
		If you insert new value X
		d) M = (M * N) + X / (N + 1)
		from : http://www.bennadel.com/blog/1627-create-a-running-average-without-storing-individual-values.htm
	 */

}




































