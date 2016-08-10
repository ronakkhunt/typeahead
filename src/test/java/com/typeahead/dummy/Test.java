package com.typeahead.dummy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class Dummy{
	String name;
}

public class Test {
	
	public static void main(String[] args) {
		Test.testObjectConversion();
	}
	public static void testObjectConversion() {
		try{
			String path = "/Users/ronakkhunt/Desktop/alice-in-wonderland.txt";
			
			FileInputStream fis = new FileInputStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while( (line = br.readLine()) != null){
				System.out.println(line);
			}
			
		}catch(Exception e){
			
		}
	}
	public static void test1(){
		try {
			Map<Integer, Integer> map1 = new HashMap<Integer, Integer>();
			
			for(int  i = 0; i < 1000000; i++){
				map1.put(i, i);
			}
	        
			RandomAccessFile raf = new RandomAccessFile("map.ser", "rw");
	        FileOutputStream fos = new FileOutputStream(raf.getFD());
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	        System.out.println("starting");
	        long startTime = System.currentTimeMillis();
	        oos.writeObject(map1);
	        System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
	        oos.close();
	        fos.close();
	        
	        raf = new RandomAccessFile("map.ser", "r");
	        FileInputStream fis = new FileInputStream(raf.getFD());
	        ObjectInputStream ois = new ObjectInputStream(fis);
	        startTime = System.currentTimeMillis();
	        Map<String, String> anotherMap = (Map<String, String>) ois.readObject();
	        System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
	      //  System.out.println(anotherMap);
	        
	        ois.close();
	        fis.close();
	        
	        

        } catch (Exception e) {
			e.printStackTrace();
		}
        

	}
	public static void test2(){
		try{
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			
			for(int  i = 0; i < 2000000; i++){
				map.put(i, i);
			}
			
			System.out.println("starting");
	        long startTime = System.currentTimeMillis();
			FileOutputStream baos = new FileOutputStream("map.ser", true);
			GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
			ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
			objectOut.writeObject(map);
			//objectOut.writeObject(myObj2);
			System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
			objectOut.close();
			
			//byte[] bytes = baos.
			
			startTime = System.currentTimeMillis();
			FileInputStream bais = new FileInputStream("map.ser");
			GZIPInputStream gzipIn = new GZIPInputStream(bais);
			ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
			map = (Map<Integer, Integer>) objectIn.readObject();
			//map = (Map<Integer, Integer>) objectIn.readObject();
			System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
			objectIn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void test3(){
		try{
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			
			for(int  i = 0; i < 2000000; i++){
				map.put(i, i);
			}
			System.out.println("starting");
			RandomAccessFile raf = new RandomAccessFile("map.ser", "rw");
	        long startTime = System.currentTimeMillis();
			FileOutputStream baos = new FileOutputStream(raf.getFD());
			GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
			ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
			objectOut.writeObject(map);
			//objectOut.writeObject(myObj2);
			System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
			objectOut.close();
			
			//byte[] bytes = baos.
			
			startTime = System.currentTimeMillis();
			raf = new RandomAccessFile("map.ser", "r");
			FileInputStream bais = new FileInputStream(raf.getFD());
			GZIPInputStream gzipIn = new GZIPInputStream(bais);
			ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
			map = (Map<Integer, Integer>) objectIn.readObject();
			//map = (Map<Integer, Integer>) objectIn.readObject();
			System.out.println("time taken:" + (System.currentTimeMillis() - startTime));
			objectIn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
