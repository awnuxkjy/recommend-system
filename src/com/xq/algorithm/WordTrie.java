package com.xq.algorithm;

import java.util.ArrayList;
import java.util.List;




/**
 * 
 * <p>Title:</p>
 * <p>Description: 单词Trie树
 * </p>
 * @createDate：2013-10-17
 * @author xq
 * @version 1.0
 */
public class WordTrie {
	/**trie树根*/
	private TrieNode root = new TrieNode();
	
	/**英文字符串正则匹配*/
	static String  englishPattern="^[A-Za-z]+$";
	/**中文正则匹配*/
	static String chinesePattern="[\u4e00-\u9fa5]";
	
	static int ARRAY_LENGTH=26;
	
	static String zeroString="";
	
	/**
	 * 
	* @Title: addWord
	* @Description: add word
	* @param @param word    
	* @return void   
	* @throws
	 */
	public void addWord(String word) {
		if(word==null || "".equals(word.trim())){
			throw new IllegalArgumentException("word can not be null!");
		}
		if(!word.matches(englishPattern)){
			throw new IllegalArgumentException("word must be english!");
		}
		addWord(root, word);
	}

	/**
	 * 
	* @Title: addWord
	* @Description:add word to node
	* @param @param node
	* @param @param word    
	* @return void   
	* @throws
	 */
	private void addWord(TrieNode node, String word) {
		if (word.length() == 0) { // if all characters of the word has been
			// added
			node.count++;
			node.nodeState=1;
		} else {
			node.prefixCount++;
			char c = word.charAt(0);
			c = Character.toLowerCase(c);
			int index = c - 'a';
			if(index>=0 && index<ARRAY_LENGTH){
				if (node.next[index] == null) { 
					node.next[index] = new TrieNode();
				}
				// go the the next character
				addWord(node.next[index], word.substring(1));
			}
			 
		}
	}
	
	/**
	 * 
	* @Title: prefixSearchWord
	* @Description: 前缀搜索
	* @param @param word
	* @param @return    
	* @return List<String>   
	* @throws
	 */
	public List<String> prefixSearchWord(String word){
		if(word==null || "".equals(word.trim())){
			return new ArrayList<String>();
		}
		if(!word.matches(englishPattern)){
			return new ArrayList<String>();
		}
		char c = word.charAt(0);
		c = Character.toLowerCase(c);
		int index = c - 'a';
		if(root.next!=null && root.next[index]!=null){
			return depthSearch(root.next[index],new ArrayList<String>(),word.substring(1),""+c,word);
		}else{
			return new ArrayList<String>();
		}
	}
	
	/**
	 * 
	* @Title: searchWord
	* @Description: 搜索单词,以a-z为根,分别向下递归搜索
	* @param @param word
	* @param @return    
	* @return List<String>   
	* @throws
	 */
	public List<String> searchWord(String word){
		if(word==null || "".equals(word.trim())){
			return new ArrayList<String>();
		}
		if(!word.matches(englishPattern)){
			return new ArrayList<String>();
		}
		char c = word.charAt(0);
		c = Character.toLowerCase(c);
		int index = c - 'a';
		List<String> list=new ArrayList<String>();
		if(root.next==null){
			return list;
		}
		for(int i=0;i<ARRAY_LENGTH;i++){
			int j='a'+i;
			char temp=(char)j;
			if(root.next[i]!=null){
				if(index==i){
					fullSearch(root.next[i],list,word.substring(1),""+temp,word);
				}else{
					fullSearch(root.next[i],list,word,""+temp,word);
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	* @Title: fullSearch
	* @Description: 匹配到对应的字母,则以该字母为字根,继续匹配完所有的单词。
	* @param @param node
	* @param @param list 保存搜索到的字符串
	* @param @param word 搜索的单词.匹配到第一个则减去一个第一个,连续匹配,直到word为空串.若没有连续匹配,则恢复到原串。
	* @param @param matchedWord 匹配到的单词
	* @param @return    
	* @return List<String>   
	* @throws
	 */
	private List<String> fullSearch(TrieNode node,List<String> list,String word,String matchedWord,String inputWord){
		if(node.nodeState==1  && word.length()==0){
			list.add(matchedWord);
		}
		if(word.length() != 0){
			char c = word.charAt(0);
			c = Character.toLowerCase(c);
			int index = c - 'a';
			for(int i=0;i<ARRAY_LENGTH;i++){
				if(node.next[i]!=null){
					int j='a'+i;
					char temp=(char)j;
					if(index==i){
						//连续匹配
						fullSearch(node.next[i], list, word.substring(1), matchedWord+temp,inputWord);
					}else{
						//未连续匹配,则重新匹配
						fullSearch(node.next[i], list, inputWord, matchedWord+temp,inputWord);
					}
				}
			}
		}else{
			if(node.prefixCount>0){
				for(int i=0;i<ARRAY_LENGTH;i++){
					if(node.next[i]!=null){
						int j='a'+i;
						char temp=(char)j;
						fullSearch(node.next[i], list, zeroString, matchedWord+temp,inputWord);
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 
	* @Title: depthSearch
	* @Description: 深度遍历子树
	* @param @param node
	* @param @param list 保存搜索到的字符串
	* @param @param word 搜索的单词.匹配到第一个则减去一个第一个,连续匹配,直到word为空串.若没有连续匹配,则恢复到原串。
	* @param @param matchedWord 匹配到的单词
	* @param @return    
	* @return List<String>   
	* @throws
	 */
	private List<String> depthSearch(TrieNode node,List<String> list,String word,String matchedWord,String inputWord){
		if(node.nodeState==1 && word.length()==0){
			list.add(matchedWord);
		}
		if(word.length() != 0){
			char c = word.charAt(0);
			c = Character.toLowerCase(c);
			int index = c - 'a';
			//继续完全匹配,直到word为空串,否则未找到
			if(node.next[index]!=null){
				depthSearch(node.next[index], list, word.substring(1), matchedWord+c,inputWord);
			}
		}else{
			if(node.prefixCount>0){//若匹配单词结束,但是trie中的单词并没有完全找到,需继续找到trie中的单词结束.
				//node.prefixCount>0表示trie中的单词还未结束
				for(int i=0;i<ARRAY_LENGTH;i++){
					if(node.next[i]!=null){
						int j='a'+i;
						char temp=(char)j;
						depthSearch(node.next[i], list, zeroString, matchedWord+temp,inputWord);
					}
				}
			}
		}
		return list;
	}
	
	class TrieNode {
		/**
		 * trie tree word count
		 */
		int count=0;
		
		/**
		 * trie tree prefix count
		 */
		int prefixCount=0;
		
		/**
		 * 指向各个子树的指针,存储26个字母[a-z]
		 */
		TrieNode[] next=new TrieNode[26];

		/**
		 * 当前TrieNode状态 ,默认 0 , 1表示从根节点到当前节点的路径表示一个词
		 */
		int nodeState = 0;
		
		TrieNode(){
			count=0;
			prefixCount=0;
			next=new TrieNode[26];
			nodeState = 0;
		}
	}
}
