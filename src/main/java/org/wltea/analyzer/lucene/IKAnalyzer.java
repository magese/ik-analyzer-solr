/*
 * IK 中文分词  版本 7.4
 * IK Analyzer release 7.4
 * update by Magese(magese@live.cn)
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

/**
 * IK分词器，Lucene Analyzer接口实现
 */
@SuppressWarnings("unused")
public final class IKAnalyzer extends Analyzer{
	
	private boolean useSmart;
	
	private boolean useSmart() {
		return useSmart;
	}

	public void setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
	}

	/**
	 * IK分词器Lucene  Analyzer接口实现类
	 * 
	 * 默认细粒度切分算法
	 */
	public IKAnalyzer(){
		this(false);
	}
	
	/**
	 * IK分词器Lucene Analyzer接口实现类
	 * 
	 * @param useSmart 当为true时，分词器进行智能切分
	 */
	public IKAnalyzer(boolean useSmart){
		super();
		this.useSmart = useSmart;
	}

	/**
	 * 重载Analyzer接口，构造分词组件
	 */
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer _IKTokenizer = new IKTokenizer(this.useSmart());
		return new TokenStreamComponents(_IKTokenizer);
	}

}
