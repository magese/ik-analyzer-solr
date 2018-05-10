/*
 * IK 中文分词  版本 7.0
 * IK Analyzer release 7.0
 * update by 高志成(magese@live.cn)
 */
package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.wltea.analyzer.dic.Dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href="magese@live.cn">Magese</a>
 */
@SuppressWarnings("unchecked")
public class IKTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware, UpdateKeeper.UpdateJob {
    private boolean useSmart;
    private ResourceLoader loader;
    private long lastUpdateTime = -1L;
    private String conf = "ik.conf";


    public IKTokenizerFactory(Map<String, String> args) {
        super(args);
        String useSmartArg = args.get("useSmart");
        this.setUseSmart(Boolean.parseBoolean(useSmartArg));
    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        return new IKTokenizer(factory, useSmart());
    }

    @Override
    public void inform(ResourceLoader resourceLoader) throws IOException {
        System.out.println(String.format(":::ik:::inform:::::::::::::::::::::::: %s", this.conf));
        this.loader = resourceLoader;
        update();
        if ((this.conf != null) && (!this.conf.trim().isEmpty())) {
            UpdateKeeper.getInstance().register(this);
        }
    }

    @Override
    public void update() throws IOException {
        Properties p = canUpdate();
        if (p != null) {
            List<String> dicPaths = SplitFileNames(p.getProperty("files"));
            List inputStreamList = new ArrayList();
            for (String path : dicPaths) {
                if ((path != null) && (!path.isEmpty())) {
                    InputStream is = this.loader.openResource(path);
                    if (is != null) {
                        inputStreamList.add(is);
                    }
                }
            }
            if (!inputStreamList.isEmpty())
                Dictionary.reloadDic(inputStreamList);
        }
    }

    /**
     * 检查是否要更新
     */
    private Properties canUpdate() {
        try {
            if (this.conf == null)
                return null;
            Properties p = new Properties();
            InputStream confStream = this.loader.openResource(this.conf);
            p.load(confStream);
            confStream.close();
            String lastupdate = p.getProperty("lastupdate", "0");
            Long t = new Long(lastupdate);

            if (t > this.lastUpdateTime) {
                this.lastUpdateTime = t;
                String paths = p.getProperty("files");
                if ((paths == null) || (paths.trim().isEmpty()))
                    return null;
                System.out.println("loading conf files success.");
                return p;
            }
            this.lastUpdateTime = t;
            return null;
        } catch (Exception e) {
            System.err.println("IK parsing conf NullPointerException~~~~~" + Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private static List<String> SplitFileNames(String fileNames) {
        if (fileNames == null) {
            return Collections.emptyList();
        }
        List result = new ArrayList();
        Collections.addAll(result, fileNames.split("[,\\s]+"));
        return result;
    }

    private boolean useSmart() {
        return useSmart;
    }

    private void setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
    }
}