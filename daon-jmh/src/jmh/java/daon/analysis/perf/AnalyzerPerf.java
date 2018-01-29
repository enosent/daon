package daon.analysis.perf;

import daon.analysis.DaonAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@State(Scope.Benchmark)
public class AnalyzerPerf {

    private Analyzer analyzer;

    private String input;

    @Setup
    public void setup() throws IOException, InterruptedException {

        analyzer = new DaonAnalyzer();
        input = getStringFromTestCase();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void readLongSentence(Blackhole bh) throws IOException, InterruptedException {

//        Analyzer analyzer = new DaonAnalyzer();

//        String sentence = input;
        String sentence = "a.5kg 다우니운동화 나이키운동화아디다스 ......남자지갑♧ 아이폰6s 10,000원 [아디다스] 슈퍼스타/스탠스미스 BEST 17종(C77124외)";
//        String sentence = "북한의 6차 핵실험으로 동북아 안보가 요동치는 가운데 6일 한·러 정상회담이 열렸다. 러시아는 '원유 공급 중단'이란 마지막 남은 대북(對北) 제재 실행의 열쇠를 쥔 나라다.";
//        String sentence = longText;
//        String sentence = "박성진 중소벤처기업부 장관 후보자(49)가 지난해 뉴라이트 학계를 대표하는 이영훈 전 서울대 경제학과 교수(66)를 모교인 포항공대로 초청해 ‘대한민국 건국’을 주제로 세미나를 가진 것으로 확인됐다.";
//        String sentence = "어느 일방만이 발표를 하게 될 때는 사전 협의가 필요하다는 것 역시 일반적인 상식이다";
//        String sentence = "거슬러 내려가셨다";
        TokenStream ts = analyzer.tokenStream("bogus", sentence);
        CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);

        ts.reset();
        while (ts.incrementToken()) {
            String term = termAtt.toString();
//            System.out.println(termAtt.toString() + " : " + offsetAtt.startOffset() + "," + offsetAtt.endOffset());
            bh.consume(term);
        }
        ts.end();
        ts.close();

    }


    private String getStringFromTestCase() throws IOException {
        InputStream input = this.getClass().getResourceAsStream("/daon/analysis/testcase.txt");


        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
                textBuilder.append(System.lineSeparator());
            }
        }

        return textBuilder.toString();
    }

}
