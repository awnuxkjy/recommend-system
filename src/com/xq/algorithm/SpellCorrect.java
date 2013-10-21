package com.xq.algorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpellCorrect {

	public static final char[] c = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z'};
	static Map<String,Integer> trainMap=train();
	public static void main(String[] args) {
		
		System.out.println(correct("speling"));
		System.out.println(correct("love"));
		System.out.println(correct("korrecter"));
		System.out.println(correct("korrect"));
		System.out.println(correct("qove"));
		//editDistance1Test("korrecter");
	}
	
	public static void editDistance1Test(String word){
		Set<String> set =editDistance1(word);
		for (String s : set) {
			System.out.println(s);
		}
		System.out.print(set.size());
	}
	
	public static String correct(String word){
		Set<String> set=new HashSet<String>();
		String str=known(word, trainMap);
		if(!"".equals(str)){
			return str;
		}else{
			set.add(word);
		}
		set.addAll(known(editDistance1(word), trainMap));
		set.addAll(editDistance2(word, trainMap));
		//set.add(word);
		Map<String,Integer> wordsMap=new HashMap<String,Integer>();
		for(String s: set){
			wordsMap.put(s, trainMap.get(s)==null ? 0: trainMap.get(s));
		}
		List<Map.Entry<String, Integer>> info = new ArrayList<Map.Entry<String, Integer>>(wordsMap.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        //语料库中没有该单词,则返回该单词本身
        return info.get(0).getValue()>0 ? info.get(0).getKey() : word;
	}

	/**
	 * 
	* @Title: words
	* @Description: 读取语料库文件
	* @param @return
	* @param @throws IOException    
	* @return Map<String,Integer>   
	* @throws
	 */
	public static Map<String,Integer> train(){
		InputStream is = new SpellCorrect().getClass().getClassLoader().getResourceAsStream("big.txt");
        if(is == null){
        	throw new RuntimeException("big.txt not found!!!");
        }
		Map<String,Integer> map = new HashMap<String,Integer>();
		try {
			//读取语料库big.txt
			BufferedReader br = new BufferedReader(new InputStreamReader(is , "UTF-8"), 512);
			String s="";
			while ((s = br.readLine()) != null) {
				// 去掉文档中除字母外的所有符号
				s = s.replaceAll("\\pP|\\pS|\\pM|\\pN|\\pC", "");
				// 将文档转成小写，然后切分成单词，存在list中
				s = s.toLowerCase();
				String[] splits = s.split(" ");
				for (int j = 0; j < splits.length; j++) {
					if (!" ".equals(splits[j]) && !"".equals(splits[j])	&& !splits[j].equals(null)){
						if(map.containsKey(splits[j])){
							Integer count=map.get(splits[j]);
							map.put(splits[j], count+1);
						}else{
							map.put(splits[j], 1);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try{
				is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 
	* @Title: editDistance2
	* @Description: 编辑距离为2的集合.通过editDistance1函数得到编辑距离为1的集合,该集合单词再通过editDistance1函数,就可以得到编辑距离为2的集合
	* @param @param set
	* @param @param trainMap
	* @param @return    
	* @return Set<String>   
	* @throws
	 */
	public static Set<String> editDistance2(Set<String> set,HashMap<String,Integer> trainMap){
		Set<String> editDistance2Set=new HashSet<String>();
		Set<String> tempSet=new HashSet<String>();
		Set<String> tmpSet=new HashSet<String>();
		for(String s: set){
			tempSet.addAll(editDistance1(s));
		}
		for(String s: tempSet){
			editDistance2Set.addAll(editDistance1(s));
		}
		for(String s : editDistance2Set){
			if(!trainMap.containsKey(s)){
				tmpSet.add(s);
			}
		}
		return tmpSet;
	}
	
	/**
	 * 
	* @Title: editDistance2
	* @Description: 得到一个word的编辑距离为2的集合
	* @param @param word
	* @param @param trainMap
	* @param @return    
	* @return Set<String>   
	* @throws
	 */
	public static Set<String> editDistance2(String word,Map<String,Integer> trainMap){
		Set<String> editDistance2Set=new HashSet<String>();
		Set<String> tmpSet=new HashSet<String>();
		Set<String> editDistance1Set=editDistance1(word);
		for(String s: editDistance1Set){
			editDistance2Set.addAll(editDistance1(s));
		}
		for(String s : editDistance2Set){
			if(!trainMap.containsKey(s)){
				tmpSet.add(s);
			}
		}
		return tmpSet;
	}
	
	/**
	 * 
	* @Title: known
	* @Description: 输入的单词集合是否在训练语料库中
	* @param @param wordsSet
	* @param @param map
	* @param @return    
	* @return Set<String>   
	* @throws
	 */
	public static Set<String> known(Set<String> wordsSet, Map<String, Integer> map) {
		Set<String> set = new HashSet<String>();
		for(String s : wordsSet){
			if (map.containsKey(s)) {
				set.add(s);
			}
		}
		return set;
	}
	
	public static String known(String word, Map<String, Integer> map) {
		if(map.containsKey(word)){
			return word;
		}else{
			return "";
		}
	}

	/**
	 * 
	* @Title: editDistance1
	* @Description: 编辑距离为1的函数
	* @param @param word
	* @param @return    
	* @return Set<String>   
	* @throws
	 */
	public static Set<String> editDistance1(String word) {
		String tempWord = "";
		Set<String> set = new HashSet<String>();
		int n = word.length();
		// delete一个字母的情况
		for (int i = 0; i < n; i++){
			tempWord = word.substring(0, i) + word.substring(i + 1);
			set.add(tempWord);
		}
		//transposition
		for (int i = 0; i < n - 1; i++) {
			/*tempWord = word.substring(0, i) + word.substring(i + 1, i + 2)
					+ word.substring(i, i + 1) + word.substring(i + 2, n);*/
			tempWord = word.substring(0, i) + word.charAt(i+1)+word.charAt(i)+word.substring(i + 2, n);

			set.add(tempWord);
		}

		// alteration 26n
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < 26; j++) {
				tempWord = word.substring(0, i) + c[j] + word.substring(i + 1, n);
				set.add(tempWord);
			}
		}

		// insertion 26n
		for (int i = 0; i < n+1; i++) {
			for (int j = 0; j < 26; j++) {
				tempWord = word.substring(0, i) + c[j] + word.substring(i, n);
				set.add(tempWord);
			}
		}
		// 将字母插入到最后 n
		for (int j = 0; j < 26; j++) {
			set.add(word + c[j]);
		}
		return set;
	}
}
