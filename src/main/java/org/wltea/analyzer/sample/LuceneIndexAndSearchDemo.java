/*
 * IK 中文分词  版本 7.5
 * IK Analyzer release 7.5
 * update by Magese(magese@live.cn)
 */
package org.wltea.analyzer.sample;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;


/**
 * 使用IKAnalyzer进行Lucene索引和查询的演示
 * 2012-3-2
 * <p>
 * 以下是结合Lucene4.0 API的写法
 */
public class LuceneIndexAndSearchDemo {


    /**
     * 模拟：
     * 创建一个单条记录的索引，并对其进行搜索
     *
     */
    public static void main(String[] args) {
        //Lucene Document的域名
        String fieldName = "text";
        //检索内容
        String text = "IK Analyzer是一个结合词典分词和文法分词的中文分词开源工具包。它使用了全新的正向迭代最细粒度切分算法。";

        //实例化IKAnalyzer分词器
        Analyzer analyzer = new IKAnalyzer(true);

        Directory directory = null;
        IndexWriter iwriter;
        IndexReader ireader = null;
        IndexSearcher isearcher;
        try {
            //建立内存索引对象
            directory = new RAMDirectory();

            //配置IndexWriterConfig
            IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
            iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            iwriter = new IndexWriter(directory, iwConfig);
            //写入索引
            Document doc = new Document();
            doc.add(new StringField("ID", "10000", Field.Store.YES));
            doc.add(new TextField(fieldName, text, Field.Store.YES));
            iwriter.addDocument(doc);
            iwriter.close();


            //搜索过程**********************************
            //实例化搜索器
            ireader = DirectoryReader.open(directory);
            isearcher = new IndexSearcher(ireader);

            String keyword = "中文分词工具包";
            //使用QueryParser查询分析器构造Query对象
            QueryParser qp = new QueryParser(fieldName, analyzer);
            qp.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = qp.parse(keyword);
            System.out.println("Query = " + query);

            //搜索相似度最高的5条记录
            TopDocs topDocs = isearcher.search(query, 5);
            System.out.println("命中：" + topDocs.totalHits);
            //输出结果
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (int i = 0; i < topDocs.totalHits; i++) {
                Document targetDoc = isearcher.doc(scoreDocs[i].doc);
                System.out.println("内容：" + targetDoc.toString());
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            if (ireader != null) {
                try {
                    ireader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
