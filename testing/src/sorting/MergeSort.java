package sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeSort {

	public static Integer [] getRandomArr() {
		int len = 20;
		List<Integer> numbers = new ArrayList<>();
		for(int i=0;i<20;) 
			numbers.add(++i);
		Integer [] arr = new Integer[len];
		for(int i=0;i<20;i++) 
			arr[i]=numbers.remove((int)(numbers.size()*Math.random()));
		return arr;
	}
	
	public static void main(String [] args) {
		Integer [] arr = getRandomArr();
		System.out.println(Arrays.asList(arr));
		System.out.println(Arrays.asList(sort(arr)));
	}
	
	public static Integer [] sort(Integer [] arr) {
		//Base case
		
		if(arr.length==1) {
			return arr;
		}
		int mid = arr.length/2;
		
		Integer [] left = sort(divide(arr,0,mid));
		Integer [] right = sort(divide(arr,mid,arr.length));
		Integer [] retval = new Integer[arr.length];
		
		int iL=0,iR=0,iRet=0;
		while(iL<left.length&&iR<right.length) {
			if(left[iL]>right[iR]) {
				retval[iRet]=right[iR];
				iR++;
			}else {
				retval[iRet]=left[iL];
				iL++;
			}
			iRet++;
		}
		while(iL<left.length) {
			retval[iRet]=left[iL];
			iL++;
			iRet++;
		}
		while(iR<right.length) {
			retval[iRet]=right[iR];
			iR++;
			iRet++;
		}
		
		return retval;
	}
	
	private static Integer [] divide(Integer [] arr, int i0, int i1) {
		if(i1==i0) {
			return new Integer[] {arr[i0]};
		}
		int len = i1-i0;
		Integer [] retval = new Integer[len];
		for(int i=0;i<len;i++) {
			retval[i]=arr[i0+i];
		}
		return retval;
	}

}
