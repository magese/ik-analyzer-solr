/*
 * IK 中文分词  版本 7.0
 * IK Analyzer release 7.0
 * update by 高志成(magese@live.cn)
 */
package org.wltea.analyzer.core;

/**
 *
 * 字符集识别工具类
 */
class CharacterUtil {
	
	static final int CHAR_USELESS = 0;
	
	static final int CHAR_ARABIC = 0X00000001;
	
	static final int CHAR_ENGLISH = 0X00000002;
	
	static final int CHAR_CHINESE = 0X00000004;
	
	static final int CHAR_OTHER_CJK = 0X00000008;
	
	
	/**
	 * 识别字符类型
	 * @param input 需要识别的字符
	 * @return int CharacterUtil定义的字符类型常量
	 */
	static int identifyCharType(char input){
		if(input >= '0' && input <= '9'){
			return CHAR_ARABIC;
			
		}else if((input >= 'a' && input <= 'z')
				|| (input >= 'A' && input <= 'Z')){
			return CHAR_ENGLISH;
			
		}else {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(input);
			
			if(ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A){
				//目前已知的中文字符UTF-8集合
				return CHAR_CHINESE;
				
			}else if(ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS //全角数字字符和日韩字符
					//韩文字符集
					|| ub == Character.UnicodeBlock.HANGUL_SYLLABLES 
					|| ub == Character.UnicodeBlock.HANGUL_JAMO
					|| ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
					//日文字符集
					|| ub == Character.UnicodeBlock.HIRAGANA //平假名
					|| ub == Character.UnicodeBlock.KATAKANA //片假名
					|| ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS){
				return CHAR_OTHER_CJK;
				
			}
		}
		//其他的不做处理的字符
		return CHAR_USELESS;
	}
	
	/**
	 * 进行字符规格化（全角转半角，大写转小写处理）
	 * @param input 需要转换的字符
	 * @return char
	 */
	static char regularize(char input){
        if (input == 12288) {
            input = (char) 32;
            
        }else if (input > 65280 && input < 65375) {
            input = (char) (input - 65248);
            
        }else if (input >= 'A' && input <= 'Z') {
        	input += 32;
		}
        
        return input;
	}
}
