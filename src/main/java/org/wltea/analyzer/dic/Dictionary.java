/*
 * IK 中文分词  版本 7.4
 * IK Analyzer release 7.4
 * update by Magese(magese@live.cn)
 */
package org.wltea.analyzer.dic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;

/**
 * 词典管理类，单例模式
 */
@SuppressWarnings("unused")
public class Dictionary {

    /*
     * 词典单子实例
     */
    private static Dictionary singleton;

    /*
     * 主词典对象
     */
    private DictSegment _MainDict;

    /*
     * 停止词词典
     */
    private DictSegment _StopWordDict;
    /*
     * 量词词典
     */
    private DictSegment _QuantifierDict;

    /**
     * 配置对象
     */
    private Configuration cfg;

    /**
     * 私有构造方法，阻止外部直接实例化本类
     *
     * @param cfg ik分词器配置实例
     */
    private Dictionary(Configuration cfg) {
        this.cfg = cfg;
        this.loadMainDict();
        this.loadStopWordDict();
        this.loadQuantifierDict();
    }

    /**
     * 词典初始化
     * 由于IK Analyzer的词典采用Dictionary类的静态方法进行词典初始化
     * 只有当Dictionary类被实际调用时，才会开始载入词典，
     * 这将延长首次分词操作的时间
     * 该方法提供了一个在应用加载阶段就初始化字典的手段
     */
    public static void initial(Configuration cfg) {
        if (singleton == null) {
            synchronized (Dictionary.class) {
                if (singleton == null) {
                    singleton = new Dictionary(cfg);
                }
            }
        }
    }

    /**
     * 获取词典单子实例
     *
     * @return Dictionary 单例对象
     */
    public static Dictionary getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("词典尚未初始化，请先调用initial方法");
        }
        return singleton;
    }

    /**
     * 重新更新词典
     * 由于停用词等不经常变也不建议常增加，故这里只修改动态扩展词库
     *
     * @param inputStreamList 词典文件IO流集合
     */
    public static void reloadDic(List<InputStream> inputStreamList) {
        // 如果本类单例尚未实例化，则先进行初始化操作
        if (singleton == null) {
            Configuration cfg = DefaultConfig.getInstance();
            initial(cfg);
        }
        // 对词典流集合进行循环读取，将读取到的词语加载到主词典中
        for (InputStream is : inputStreamList) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
                String theWord;
                do {
                    theWord = br.readLine();
                    if (theWord != null && !"".equals(theWord.trim())) {
                        singleton._MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
                    }
                } while (theWord != null);
            } catch (IOException ioe) {
                System.err.println("Other Dictionary loading exception.");
                ioe.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 批量加载新词条
     *
     * @param words 词条列表
     */
    public void addWords(Collection<String> words) {
        if (words != null) {
            for (String word : words) {
                if (word != null) {
                    // 批量加载词条到主内存词典中
                    singleton._MainDict.fillSegment(word.trim().toLowerCase().toCharArray());
                }
            }
        }
    }

    /**
     * 批量移除（屏蔽）词条
     */
    public void disableWords(Collection<String> words) {
        if (words != null) {
            for (String word : words) {
                if (word != null) {
                    // 批量屏蔽词条
                    singleton._MainDict.disableSegment(word.trim().toLowerCase().toCharArray());
                }
            }
        }
    }

    /**
     * 检索匹配主词典
     *
     * @return Hit 匹配结果描述
     */
    public Hit matchInMainDict(char[] charArray) {
        return singleton._MainDict.match(charArray);
    }

    /**
     * 检索匹配主词典
     *
     * @return Hit 匹配结果描述
     */
    public Hit matchInMainDict(char[] charArray, int begin, int length) {
        return singleton._MainDict.match(charArray, begin, length);
    }

    /**
     * 检索匹配量词词典
     *
     * @return Hit 匹配结果描述
     */
    public Hit matchInQuantifierDict(char[] charArray, int begin, int length) {
        return singleton._QuantifierDict.match(charArray, begin, length);
    }


    /**
     * 从已匹配的Hit中直接取出DictSegment，继续向下匹配
     *
     * @return Hit
     */
    public Hit matchWithHit(char[] charArray, int currentIndex, Hit matchedHit) {
        DictSegment ds = matchedHit.getMatchedDictSegment();
        return ds.match(charArray, currentIndex, 1, matchedHit);
    }


    /**
     * 判断是否是停止词
     *
     * @return boolean
     */
    public boolean isStopWord(char[] charArray, int begin, int length) {
        return singleton._StopWordDict.match(charArray, begin, length).isMatch();
    }

    /**
     * 加载主词典及扩展词典
     */
    private void loadMainDict() {
        // 建立一个主词典实例
        _MainDict = new DictSegment((char) 0);
        // 读取主词典文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(cfg.getMainDictionary());
        if (is == null) {
            throw new RuntimeException("Main Dictionary not found!!!");
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
            String theWord;
            do {
                theWord = br.readLine();
                if (theWord != null && !"".equals(theWord.trim())) {
                    _MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
                }
            } while (theWord != null);

        } catch (IOException ioe) {
            System.err.println("Main Dictionary loading exception.");
            ioe.printStackTrace();

        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 加载扩展词典
        this.loadExtDict();
    }

    /**
     * 加载用户配置的扩展词典到主词库表
     */
    private void loadExtDict() {
        // 加载扩展词典配置
        List<String> extDictFiles = cfg.getExtDictionarys();
        if (extDictFiles != null) {
            InputStream is;
            for (String extDictName : extDictFiles) {
                // 读取扩展词典文件
                System.out.println("加载扩展词典：" + extDictName);
                is = this.getClass().getClassLoader().getResourceAsStream(extDictName);
                // 如果找不到扩展的字典，则忽略
                if (is == null) {
                    continue;
                }
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
                    String theWord;
                    do {
                        theWord = br.readLine();
                        if (theWord != null && !"".equals(theWord.trim())) {
                            // 加载扩展词典数据到主内存词典中
                            // System.out.println(theWord);
                            _MainDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
                        }
                    } while (theWord != null);

                } catch (IOException ioe) {
                    System.err.println("Extension Dictionary loading exception.");
                    ioe.printStackTrace();

                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 加载用户扩展的停止词词典
     */
    private void loadStopWordDict() {
        // 建立一个主词典实例
        _StopWordDict = new DictSegment((char) 0);
        // 加载扩展停止词典
        List<String> extStopWordDictFiles = cfg.getExtStopWordDictionarys();
        if (extStopWordDictFiles != null) {
            InputStream is;
            for (String extStopWordDictName : extStopWordDictFiles) {
                System.out.println("加载扩展停止词典：" + extStopWordDictName);
                // 读取扩展词典文件
                is = this.getClass().getClassLoader().getResourceAsStream(extStopWordDictName);
                // 如果找不到扩展的字典，则忽略
                if (is == null) {
                    continue;
                }
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
                    String theWord;
                    do {
                        theWord = br.readLine();
                        if (theWord != null && !"".equals(theWord.trim())) {
                            // System.out.println(theWord);
                            // 加载扩展停止词典数据到内存中
                            _StopWordDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
                        }
                    } while (theWord != null);

                } catch (IOException ioe) {
                    System.err.println("Extension Stop word Dictionary loading exception.");
                    ioe.printStackTrace();

                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 加载量词词典
     */
    private void loadQuantifierDict() {
        // 建立一个量词典实例
        _QuantifierDict = new DictSegment((char) 0);
        // 读取量词词典文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(cfg.getQuantifierDicionary());
        if (is == null) {
            throw new RuntimeException("Quantifier Dictionary not found!!!");
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 512);
            String theWord;
            do {
                theWord = br.readLine();
                if (theWord != null && !"".equals(theWord.trim())) {
                    _QuantifierDict.fillSegment(theWord.trim().toLowerCase().toCharArray());
                }
            } while (theWord != null);

        } catch (IOException ioe) {
            System.err.println("Quantifier Dictionary loading exception.");
            ioe.printStackTrace();

        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
