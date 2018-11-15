# ik-analyzer-solr7
ik-analyzer for solr7.x

<!-- Badges section here. -->
[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/magese/ik-analyzer-solr7/releases)

[![Crates.io](https://img.shields.io/crates/l/rustc-serialize.svg)](https://github.com/magese/ik-analyzer-solr7/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/magese/ik-analyzer-solr7.svg?branch=master)](https://travis-ci.org/magese/ik-analyzer-solr7)
[![Github Releases](https://img.shields.io/github/downloads/magese/ik-analyzer-solr7/latest/total.svg)](https://github.com/magese/ik-analyzer-solr7/releases)

[![GitHub forks](https://img.shields.io/github/forks/magese/ik-analyzer-solr7.svg?style=social&label=Fork)](https://github.com/magese/ik-analyzer-solr7/fork)
[![GitHub stars](https://img.shields.io/github/stars/magese/ik-analyzer-solr7.svg?style=social&label=Star)](https://github.com/magese/ik-analyzer-solr7/star)
<!-- /Badges section end. -->

## 简介
<p>IKAnalyzer的原作者为林良益（linliangyi2007@gmail.com），项目网站为http://code.google.com/p/ik-analyzer/</p>

* 该项目根据博主[@星火燎原智勇](http://www.cnblogs.com/liang1101/articles/6395016.html)的博客进行修改，其GITHUB地址为[@liang68](https://github.com/liang68)

<h4>适配最新版solr7，并添加动态加载字典表功能；</h4>
<h4>在不需要重启solr服务的情况下加载新增的字典。</h4>


## 使用说明
* jar包下载地址：[ik-analyzer-7.5.0.jar](https://search.maven.org/remotecontent?filepath=com/github/magese/ik-analyzer/7.5.0/ik-analyzer-7.5.0.jar)
* 历史版本：[Central Repository](https://search.maven.org/search?q=g:com.github.magese%20AND%20a:ik-analyzer&core=gav)

    ```console
    <!-- Maven仓库地址 -->
    <dependency>
        <groupId>com.github.magese</groupId>
        <artifactId>ik-analyzer</artifactId>
        <version>7.5.0</version>
    </dependency>
    ```

1. 将jar包放入Solr服务的`Jetty`或`Tomcat`的`webapp/WEB-INF/lib/`目录下；

2. 将`resources`目录下的5个配置文件放入solr服务的`Jetty`或`Tomcat`的`webapp/WEB-INF/classes/`目录下；
    ```console
    ① IKAnalyzer.cfg.xml
    ② ext.dic
    ③ stopword.dic
    ④ ik.conf
    ⑤ dynamicdic.txt
    ```

3. 配置Solr的`managed-schema`，添加`ik分词器`，示例如下；
    ```console
    <!-- ik分词器 -->
    <fieldType name="text_ik" class="solr.TextField">
      <analyzer type="index">
          <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" useSmart="false" conf="ik.conf"/>
          <filter class="solr.LowerCaseFilterFactory"/>
      </analyzer>
      <analyzer type="query">
          <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" useSmart="true" conf="ik.conf"/>
          <filter class="solr.LowerCaseFilterFactory"/>
      </analyzer>
    </fieldType>
    ```

4. 启动Solr服务测试分词；

5. `ik.conf`文件说明：
    ```console
    files=dynamicdic.txt
    lastupdate=0
    ```

    1. `files`为动态字典列表，可以设置多个字典表，用逗号进行分隔，默认动态字典表为`dynamicdic.txt`；
    2. `lastupdate`默认值为`0`，每次对动态字典表修改后请+1，不然不会将字典表中新的词语添加到内存中。<s>`lastupdate`采用的是`int`类型，不支持时间戳，如果使用时间戳的朋友可以把源码中的`int`改成`long`即可；</s> `2018-08-23` 已将源码中`lastUpdate`改为`long`类型，现可以用时间戳了。

6. `dynamicdic.txt` 为动态字典
    在此文件配置的词语不需重启服务即可加载进内存中；


## 更新说明
- `2018-10-10:` 升级lucene版本为`7.5.0`
- `2018-09-03:` 优化注释与输出信息，取消部分中文输出避免不同字符集乱码，现会打印被调用inform方法的hashcode
- `2018-08-23: `
    - ⑴完善了动态更新词库代码注释；
    - ⑵将ik.conf配置文件中的lastUpdate属性改为long类型，现已支持时间戳形式
- `2018-08-13:` 更新maven仓库地址
- `2018-08-01:` 移除默认的扩展词与停用词
- `2018-07-23:` 升级lucene版本为`7.4.0`


## BUG & 疑问 & 其它
如果您在使用过程中遇到了BUG，或者有不清楚的地方，请挂ISSUE或者联系作者：<magese@live.cn>

如果您觉得该项目对您有帮助，请别忘记给这个项目一个`star`