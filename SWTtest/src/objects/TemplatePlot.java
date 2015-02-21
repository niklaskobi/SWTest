package objects;

import java.util.ArrayList;

public class TemplatePlot {
	
	public ArrayList <MeasurementEntry> allPoints;
	
	
	/**
	 * constructor
	 */
	public TemplatePlot()
	{
		allPoints = new ArrayList<MeasurementEntry>();
	}
	
	
	/**
	 * constructor with a given array list
	 * @param list
	 */
	public TemplatePlot(ArrayList<MeasurementEntry> list)
	{
		this.allPoints = list;
	}
	
	
	/**
	 * adds a new point to the array
	 * @param entry new antry
	 */
	public void addPoint(MeasurementEntry entry)
	{
		this.allPoints.add(entry);
	}
	
	
	/**
	 * replaces the current list with the given one
	 * @param list
	 */
	public void replacePointList(ArrayList<MeasurementEntry> list)
	{
		this.allPoints = list;
	}

}
