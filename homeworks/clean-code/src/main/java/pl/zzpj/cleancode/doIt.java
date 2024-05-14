package pl.zzpj.cleancode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class doIt {

	Map<Integer, Integer> hashMap = new HashMap<>();
	private int intMaxVal = Integer.MIN_VALUE;
	private int intMinVal = Integer.MAX_VALUE;

	public void runTheSimulation(List<Integer> numbersList) {
		findMinAndMaxValues(numbersList);
	}
	
	private void findMinAndMaxValues(List<Integer> list) {
        for (Integer integer : list) findMinAndMaxValues(integer);
	}
	
	private void findMinAndMaxValues(Integer number) {
		hashMap.put(number, hashMap.getOrDefault(number, 0) + 1);

		intMaxVal = Math.max(intMaxVal, number);
		intMinVal = Math.min(intMinVal, number);
	}
	
	public double getWeightedMean() {
		int weightedValuesSum = 0;
		int weightsSum = 0;
		for (Entry<Integer, Integer> element : hashMap.entrySet()) {
			weightsSum += element.getValue();
			weightedValuesSum += element.getKey() * element.getValue();
		}
		return (double) weightedValuesSum /weightsSum;
	}
	
	public int getIntMinVal() {
		return intMinVal;
	}
	
	public int getIntMaxVal() {
		return intMaxVal;
	}


	public String getFizzBuzz(int number) {
		if (number % 3 == 0 && number % 5 == 0) {
			return "FizzBuzz";
		} else if (number % 3 == 0) {
			return "Fizz";
		} else if (number % 5 == 0) {
			return "Buzz";
		} else {
			return "";
		}
	}
}