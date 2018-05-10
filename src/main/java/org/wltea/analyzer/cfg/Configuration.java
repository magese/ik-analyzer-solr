/*
 * IK 中文分词  版本 7.0
 * IK Analyzer release 7.0
 * update by 高志成(magese@live.cn)
 */
package org.wltea.analyzer.cfg;

import java.util.List;

/**
 * 配置管理类接口
 */
public interface Configuration {


    /**
     * 返回useSmart标志位
     * useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     *
     * @return useSmart
     */
    boolean useSmart();

    /**
     * 设置useSmart标志位
     *
     * @param useSmart =true ，分词器使用智能切分策略， =false则使用细粒度切分
     */
    void setUseSmart(boolean useSmart);


    /**
     * 获取主词典路径
     *
     * @return String 主词典路径
     */
    String getMainDictionary();

    /**
     * 获取量词词典路径
     *
     * @return String 量词词典路径
     */
    String getQuantifierDicionary();

    /**
     * 获取扩展字典配置路径
     *
     * @return List<String> 相对类加载器的路径
     */
    List<String> getExtDictionarys();


    /**
     * 获取扩展停止词典配置路径
     *
     * @return List<String> 相对类加载器的路径
     */
    List<String> getExtStopWordDictionarys();

}
