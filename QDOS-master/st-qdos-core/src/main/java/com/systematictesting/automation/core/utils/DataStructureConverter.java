package com.systematictesting.automation.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.systematictesting.qdos.beans.DataSet;

public class DataStructureConverter {

	public static Map<String, HashMap<String, String>> convertTestCaseDataSets(Map<String, ArrayList<DataSet>> columnVsListOfDataSets){
		HashMap<String, HashMap<String, String>> dataSetResult = new HashMap<String, HashMap<String, String>>();
		Set<String> columnKeySets = columnVsListOfDataSets.keySet();
		for (String columnKey : columnKeySets){
			//dataSetResult.put(columnKey, new HashMap<String,String>());
			List<DataSet> dataSetLists = columnVsListOfDataSets.get(columnKey);
			for(DataSet dataset : dataSetLists){
				//dataSetResult.get(columnKey).put(dataset.getId(), dataset.getValue());
				if (!dataSetResult.containsKey(dataset.getId())){
					dataSetResult.put(dataset.getId(), new HashMap<String, String>());
				}
				dataSetResult.get(dataset.getId()).put(columnKey, dataset.getValue());
				
			}
		}
		return dataSetResult;
	}
}
