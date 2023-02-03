package sorting;

import java.util.ArrayList;
import java.util.Arrays;


public class BubbleSort {

	public static void main(String [] args) {
		Integer [] arr = MergeSort.getRandomArr();
		
		System.out.println(Arrays.asList(arr));
		System.out.println(Arrays.asList(sort(arr)));

		ArrayList<String> list = new ArrayList<>();
		list.add("test");
		System.out.println(list);
		
	}
	
	public static Integer[] sort(Integer [] arr) {
		Integer [] retval = arr.clone();
		for(int i=0;i<retval.length;i++) {
			for(int j=0;j<retval.length-1-i;j++) {
				if(retval[j+1]<retval[j]) {
					swap(retval,j+1,j);
				}
			}
		}
		return retval;
	}
	
	public static void swap(Integer [] arr, int i0, int i1) {
		Integer temp = arr[i0];
		arr[i0]=arr[i1];
		arr[i1]=temp;
	}
	
	
}
