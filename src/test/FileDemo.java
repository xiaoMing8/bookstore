package test;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

public class FileDemo {
	
	@Test
	public void fun1() {

		// 得到文件保存在磁盘上的真实路径(带盘符的那种)
		String savePath = "d:/gitDemo";

		// 得到上传的文件名称,增加uuid(为防止上传的文件名称冲突)设置为本地保存的名称
		String saveFileName = "哈哈.txt";

		// 使用目录(文件保存地址)以及文件名创建目标文件
		File destFile = new File(savePath, saveFileName);
		// 将上传的文件写到目标文件中
		System.out.println(destFile.getName());
		
	
	}
	
	@Test
	public void fun2() {
		int[][] arr = new int[10][];
		arr[1] = new int[]{1,2,3};
		arr[2] = new int[]{1,2,3};
		
		
		System.out.println(Arrays.toString(arr[2]));
//		for(int i : arr[1]){
//			System.out.println(i);
//		}
		
	}
	

}
