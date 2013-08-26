package com.xq.algorithm;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;



/**
 * 
 * <p>Title:</p>
 * <p>Description: SimpleSummariser
 * </p>
 * @createDate：2013-8-26
 * @author xq
 * @version 1.0
 */
public class SimpleSummariserAlgorithm {

    /**
     * 
    * @Title: summarise
    * @Description: 文章摘要实现
    * @param @param input
    * @param @param numSentences
    * @param @return    
    * @return String   
    * @throws
     */
    public static String summarise(String input, int numSentences) {
        // get the frequency of each word in the input
        Map<String,Integer> wordFrequencies = segStr(input);

        // now create a set of the X most frequent words
        Set<String> mostFrequentWords = getMostFrequentWords(100, wordFrequencies).keySet();

        // break the input up into sentences
        // workingSentences is used for the analysis, but
        // actualSentences is used in the results so that the 
        // capitalisation will be correct.
        String[] workingSentences = getSentences(input.toLowerCase());
        String[] actualSentences = getSentences(input);

        // iterate over the most frequent words, and add the first sentence 
        // that includes each word to the result
        Set<String> outputSentences = new LinkedHashSet<String>();
        Iterator<String> it = mostFrequentWords.iterator();
        while (it.hasNext()) {
            String word = (String) it.next();
            for (int i = 0; i < workingSentences.length; i++) {
                if (workingSentences[i].indexOf(word) >= 0) {
                    outputSentences.add(actualSentences[i]);
                    break;
                }
                if (outputSentences.size() >= numSentences) {
                    break;
                }
            }
            if (outputSentences.size() >= numSentences) {
                break;
            }

        }

        List<String> reorderedOutputSentences = reorderSentences(outputSentences, input);

        StringBuffer result = new StringBuffer("");
        it = reorderedOutputSentences.iterator();
        while (it.hasNext()) {
            String sentence = (String) it.next();
            result.append(sentence);
            result.append("."); // This isn't always correct - perhaps it should be whatever symbol the sentence finished with
            if (it.hasNext()) {
                result.append(" ");
            }
        }

        return result.toString();
    }
    
    /**
     * 
    * @Title: reorderSentences
    * @Description: 将句子按顺序输出
    * @param @param outputSentences
    * @param @param input
    * @param @return    
    * @return List<String>   
    * @throws
     */
    private static List<String> reorderSentences(Set<String> outputSentences, final String input) {
        // reorder the sentences to the order they were in the 
        // original text
        ArrayList<String> result = new ArrayList<String>(outputSentences);

        Collections.sort(result, new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                String sentence1 = (String) arg0;
                String sentence2 = (String) arg1;

                int indexOfSentence1 = input.indexOf(sentence1.trim());
                int indexOfSentence2 = input.indexOf(sentence2.trim());
                int result = indexOfSentence1 - indexOfSentence2;

                return result;
            }

        });
        return result;
    }
    
    /**
     * 
    * @Title: getMostFrequentWords
    * @Description: 对分词进行按数量排序,取出前num个
    * @param @param num
    * @param @param words
    * @param @return    
    * @return Map<String,Integer>   
    * @throws
     */
    public static Map<String, Integer> getMostFrequentWords(int num,Map<String, Integer> words){
        
        Map<String, Integer> keywords = new LinkedHashMap<String, Integer>();
        int count=0;
        // 词频统计
        List<Map.Entry<String, Integer>> info = new ArrayList<Map.Entry<String, Integer>>(words.entrySet());
        Collections.sort(info, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue() - obj1.getValue();
            }
        });
        
        // 高频词输出
        for (int j = 0; j < info.size(); j++) {
            // 词-->频
            if(info.get(j).getKey().length()>1){
                if(num>count){
                    keywords.put(info.get(j).getKey(), info.get(j).getValue());
                    count++;
                }else{
                    break;
                }
            }
        }
        return keywords;
    }
    
    
    /**
     * 
    * @Title: segStr
    * @Description: 返回LinkedHashMap的分词
    * @param @param content
    * @param @return    
    * @return Map<String,Integer>   
    * @throws
     */
    public static Map<String, Integer> segStr(String content){
        // 分词
        Reader input = new StringReader(content);
        // 智能分词关闭（对分词的精度影响很大）
        IKSegmenter iks = new IKSegmenter(input, true);
        Lexeme lexeme = null;
        Map<String, Integer> words = new LinkedHashMap<String, Integer>();
        try {
            while ((lexeme = iks.next()) != null) {
                if (words.containsKey(lexeme.getLexemeText())) {
                    words.put(lexeme.getLexemeText(), words.get(lexeme.getLexemeText()) + 1);
                } else {
                    words.put(lexeme.getLexemeText(), 1);
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return words;
    }
    
    /**
     * 
    * @Title: getSentences
    * @Description: 把段落按. ! ?分隔成句组
    * @param @param input
    * @param @return    
    * @return String[]   
    * @throws
     */
    public static String[] getSentences(String input) {
        if (input == null) {
            return new String[0];
        } else {
            // split on a ".", a "!", a "?" followed by a space or EOL
        	//"(\\.|!|\\?)+(\\s|\\z)"
            return input.split("(\\.|!|\\?)");
        }

    }
    
    public static void main(String[] args){
    	String s="被告人:对? 关于王立军,有几个基本事实.首先,1月28日我是初次听到此事.并不相信谷开来会杀人.我跟11·15杀人案无关.我不是谷开来11·15杀人罪的共犯.这个大家都认可.实际上谷开来3月14日她在北京被抓走!" +
    			"在这之前她一直非常确切地跟我说她没杀人,是王立军诬陷她.我在1月28日和次听到这个事时我不相信她会杀人." +
    			"第二个事实,免王立军的局长.是多个因素.一个,我确实认为他诬陷谷开来.但我并不是想掩盖11·15,我是觉得他人品不好." +
    			"因为谷开来和他是如胶似漆,谷开来对他是言听计从,那王立军也通过与谷开来的交往中打入了我的家庭." +
    			"那现在发生这么严重的事.作为一个起码的人,要讲人格的话,你干吗不找谷开来商量,而跑我这里来说这些话?" +
    			"第二个免他的原因,是他想要挟我.他多次谈他身体不好,打黑压力大,得罪了人." +
    			"其实这是在表功.第三,徐某某给我反映了他有五六条问题.有记录.实际上免他是有这些原因的,绝不只是一个谷开来的原因.这是多因一果.";
    	System.out.println(summarise(s, 3));
    }
}
