/*
 * IK 中文分词  版本 7.4
 * IK Analyzer release 7.4
 * update by Magese(magese@live.cn)
 */
package org.wltea.analyzer.dic;

/**
 * 表示一次词典匹配的命中
 */
@SuppressWarnings("unused")
public class Hit {
	//Hit不匹配
	private static final int UNMATCH = 0x00000000;
	//Hit完全匹配
	private static final int MATCH = 0x00000001;
	//Hit前缀匹配
	private static final int PREFIX = 0x00000010;
	
	
	//该HIT当前状态，默认未匹配
	private int hitState = UNMATCH;
	
	//记录词典匹配过程中，当前匹配到的词典分支节点
	private DictSegment matchedDictSegment; 
	/*
	 * 词段开始位置
	 */
	private int begin;
	/*
	 * 词段的结束位置
	 */
	private int end;
	
	
	/**
	 * 判断是否完全匹配
	 */
	public boolean isMatch() {
		return (this.hitState & MATCH) > 0;
	}
	/**
	 * 
	 */
	void setMatch() {
		this.hitState = this.hitState | MATCH;
	}

	/**
	 * 判断是否是词的前缀
	 */
	public boolean isPrefix() {
		return (this.hitState & PREFIX) > 0;
	}
	/**
	 * 
	 */
	void setPrefix() {
		this.hitState = this.hitState | PREFIX;
	}
	/**
	 * 判断是否是不匹配
	 */
	public boolean isUnmatch() {
		return this.hitState == UNMATCH ;
	}
	/**
	 * 
	 */
	void setUnmatch() {
		this.hitState = UNMATCH;
	}
	
	DictSegment getMatchedDictSegment() {
		return matchedDictSegment;
	}
	
	void setMatchedDictSegment(DictSegment matchedDictSegment) {
		this.matchedDictSegment = matchedDictSegment;
	}
	
	public int getBegin() {
		return begin;
	}
	
	void setBegin(int begin) {
		this.begin = begin;
	}
	
	public int getEnd() {
		return end;
	}
	
	void setEnd(int end) {
		this.end = end;
	}	
	
}
