package daon.analysis.ko.reader;

import com.google.protobuf.CodedInputStream;
import daon.analysis.ko.config.POSTag;
import daon.analysis.ko.fst.DaonFST;
import daon.analysis.ko.fst.DaonFSTBuilder;
import daon.analysis.ko.model.Keyword;
import daon.analysis.ko.model.ModelInfo;
import daon.analysis.ko.proto.Model;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 2017. 3. 8..
 */
public class ModelReader {

    private Logger logger = LoggerFactory.getLogger(ModelReader.class);

    private String filePath = null;
    private String url = null;
    private InputStream inputStream = null;

    public static ModelReader create() {

        return new ModelReader();
    }

    private ModelReader() {}

    public ModelReader filePath(String path){
        this.filePath = path;
        return this;
    }

    public ModelReader url(String url){
        this.url = url;
        return this;
    }

    public ModelReader inputStream(InputStream inputStream){
        this.inputStream = inputStream;
        return this;
    }

    public ModelInfo load() throws IOException {

        StopWatch watch = new StopWatch();

        watch.start();

        Model model = loadModel();

        ModelInfo modelInfo = new ModelInfo();

        initDictionary(model, modelInfo);

        initWordFst(model, modelInfo);

        initTags(model, modelInfo);

        watch.stop();

        logger.info("model load elapsed : {} ms", watch.getTime() );

        return modelInfo;
    }

    private Model loadModel() throws IOException {
        InputStream inputStream = getInputStream();

        CodedInputStream input = CodedInputStream.newInstance(inputStream);

        input.setSizeLimit(Integer.MAX_VALUE);

        return Model.parseFrom(input);
    }

    private void initDictionary(Model model, ModelInfo modelInfo) {
        Map<Integer, Model.Keyword> dictionary = model.getDictionaryMap();

        dictionary.forEach((key, value) -> {

            Keyword keyword = new Keyword();
            keyword.setSeq(value.getSeq());
            keyword.setWord(value.getWord());
            keyword.setTag(POSTag.valueOf(value.getTag()));

            modelInfo.getDictionary().put(key, keyword);
        });
    }

    private void initWordFst(Model model, ModelInfo modelInfo) throws IOException {
        byte[] wordBytes = model.getWordFst().toByteArray();

        DaonFST<Object> wordFst = DaonFSTBuilder.create().buildPairFst(wordBytes);

        modelInfo.setWordFst(wordFst);

//        logger.info("model wordFst size : {} byte", wordFst.getInternalFST().ramBytesUsed());
    }

    private void initTags(Model model, ModelInfo modelInfo) {
        initFirstTags(model, modelInfo);

        initMiddleTags(model, modelInfo);

        initLastTags(model, modelInfo);

        initConnectTags(model, modelInfo);
    }

    private void initFirstTags(Model model, ModelInfo modelInfo) {
        List<String> firstTags = model.getFirstTagsList();

        for(String firstTag : firstTags){
            String[] tags = firstTag.split(",");
            int idx = POSTag.valueOf(tags[0]).getIdx();
            int cost = NumberUtils.toInt(tags[1]);

            modelInfo.getFirstTags()[idx] = cost;
        }
    }

    private void initMiddleTags(Model model, ModelInfo modelInfo) {
        List<String> middleTags = model.getMiddleTagsList();

        for(String middleTag : middleTags){
            String[] tags = middleTag.split(",");
            int idx1 = POSTag.valueOf(tags[0]).getIdx();
            int idx2 = POSTag.valueOf(tags[1]).getIdx();
            int cost = NumberUtils.toInt(tags[2]);

            modelInfo.getMiddleTags()[idx1][idx2] = cost;
        }
    }

    private void initLastTags(Model model, ModelInfo modelInfo) {
        List<String> lastTags = model.getLastTagsList();

        for(String lastTag : lastTags){
            String[] tags = lastTag.split(",");
            int idx = POSTag.valueOf(tags[0]).getIdx();
            int cost = NumberUtils.toInt(tags[1]);

            modelInfo.getLastTags()[idx] = cost;
        }
    }

    private void initConnectTags(Model model, ModelInfo modelInfo) {
        List<String> connectTags = model.getConnectTagsList();

        for(String connectTag : connectTags){
            String[] tags = connectTag.split(",");
            int idx1 = POSTag.valueOf(tags[0]).getIdx();
            int idx2 = POSTag.valueOf(tags[1]).getIdx();
            int cost = NumberUtils.toInt(tags[2]);

            modelInfo.getConnectTags()[idx1][idx2] = cost;
        }
    }

    private InputStream getInputStream() throws IOException {

        InputStream inputStream = null;

        if(filePath != null){
            inputStream = new FileInputStream(filePath);
        }

        if(url != null){
            inputStream = new URL(url).openStream();
        }

        if(this.inputStream != null){
            inputStream = this.inputStream;
        }

        if(inputStream == null){
            inputStream = this.getClass().getResourceAsStream("model.dat");
        }

        return inputStream;
    }

}
