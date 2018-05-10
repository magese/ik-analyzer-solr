# ik-analyzer-solr7
ik-analyzer for solr7.x

适配最新版solr7，并添加动态加载字典表功能；
在不需要重启solr服务的情况下加载新增的字典。

<hr>
<h2>使用说明：</h2><br>

1-将jar包放入solr服务的jetty或tomcat的webapp/WEB-INF/lib/目录下；<br>

2-将resources目录下的5个配置文件(IKAnalyzer.cfg.xml; ext.dic; stopword.dic; ik.conf; dynamicdic.txt)放入solr服务的jetty或tomcat的webapp/WEB-INF/classes/目录下；<br>

3-配置solr的managed-schema，添加ik分词器，示例如下；<br>
<div class="content">
    <pre>
      {{ 
        <!-- ik分词器 -->
        <fieldType name="text_ik" class="solr.TextField">
            <analyzer type="index">
                <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" isMaxWordLength="false" useSmart="false" conf="ik.conf"/>
                <filter class="solr.LowerCaseFilterFactory"/>
            </analyzer>
            <analyzer type="query">
                <tokenizer class="org.wltea.analyzer.lucene.IKTokenizerFactory" isMaxWordLength="true" useSmart="true" conf="ik.conf"/>
                <filter class="solr.LowerCaseFilterFactory"/>
            </analyzer>
        </fieldType>
      }}
    </pre>
</div>


4-启动solr服务测试分词；<br>

5-ik.conf文件说明：<br>
  files=dynamicdic.txt<br>
  lastupdate=0<br>
  
  files为动态字典列表，可以设置多个字典表，用逗号进行分隔，默认动态字典表为dynamicdic.txt；<br>
  lastupdate默认值为0，每次对动态字典表修改后请+1，不然不会将字典表中新的词语添加到内存中，lastupdate采用的是int类型，不支持时间戳，如果使用时间戳的朋友可以把源码中的int改成long即可；<br>

5-dynamicdic.txt 为动态字典，在此文件配置的词语不需重启服务即可加载进内存中；<br>
<hr>

有问题可以联系作者邮箱magese@live.cn；<br>
欢迎大家一起交流~<br>
