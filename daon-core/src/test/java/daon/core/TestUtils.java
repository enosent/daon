package daon.core;

import lucene.core.util.ArrayUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

public class TestUtils {


    private Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public TestUtils() throws IOException {
    }



//    File seojong = new File("/Users/mac/Downloads/sejong.txt");

    //    String seojongTxt = FileUtils.readFileToString(seojong, Charset.defaultCharset());
    String seojongTxt = "테스트..";


    @Test
    public void testRead() throws IOException {

        StringReader input = new StringReader("가나다라마바사아자차카타파하ㄱㄴㄷ");

        StringBuilder document = new StringBuilder();
        char[] tmp = new char[3];
        int len;
        while ((len = input.read(tmp)) != -1) {
            document.append(new String(tmp, 0, len));
        }
        System.out.println(document.toString().toLowerCase());

    }

    @Test
    public void testRead1() throws IOException {


//        File seojong = new File("/Users/mac/Downloads/sejong.txt");

//        String seojongTxt = FileUtils.readFileToString(seojong, Charset.defaultCharset());

        int pos = 0;


        CharBuffer buffer = CharBuffer.allocate(12);

        StringReader input = new StringReader(seojongTxt);

        while (true) {

            if (input.read(buffer) == -1) {
                // End
                break;
            }

            buffer.flip();
            while (buffer.hasRemaining()) {
                char ch = buffer.get();

                logger.info("char : {}", ch);
            }
            buffer.clear();

        }


    }

    @Test
    public void testRead2() throws IOException {

/*
        int pos = 0;
        int offset = 0;

        RollingCharBuffer buffer = new RollingCharBuffer();

//		StringReader input = new StringReader("시비와 분쟁이 있었을 때, 그 해결이 양편 다 옳은 것으로 되는 동시에 양편 다 옳지 않은 것이 되기도 한다.");
        StringReader input = new StringReader(seojongTxt);

        buffer.reset(input);

        StopWatch watch = new StopWatch();
        watch.start();

        while (pos > -1) {

            int length = 0;
            while (true) {
                int ch = buffer.get(pos);

                if (ch == -1) {
                    pos = -1;
                    // End
                    break;
                }

                if (length == 0) {
                    offset = pos;
                }

                pos++;
                CharType charType = CharTypeChecker.charType(ch);


//                logger.info("char : {}, type : {}, pos : {}, length : {}", (char) ch, charType, pos, length);

                if (CharType.SPACE.equals(charType)) {
                    break;
                }

                length++;

//            System.out.println(buffer.get(pos, 1));
//            System.out.println((char) buffer.get(pos) +  " : " + charType);

            }

            try {

                if (length > 0) {
                    char[] chars = buffer.get(offset, length);
                }
//                logger.info("chars : {}, offset : {}, length : {}", chars, offset, length);
            } catch (AssertionError e) {
                System.out.println("error");
                logger.info("pos : {}, offset : {}, length : {}", pos, offset, length);
            }

        }
        watch.stop();

        logger.info("elapsed time : {}", watch.getTime());

*/
    }


    @Test
    public void testRead3() throws IOException {
/*
        int pos = 0;
        int offset = 0, bufferIndex = 0, dataLen = 0, finalOffset = 0;

        int IO_BUFFER_SIZE = 3;

        CharacterUtils.CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(IO_BUFFER_SIZE);

        StringReader input = new StringReader(seojongTxt);

        StopWatch watch = new StopWatch();
        watch.start();

        while (pos > -1) {

            int length = 0;
            int start = -1; // this variable is always initialized
            int end = -1;
            char[] buffer = new char[10];

            while (true) {

                if (bufferIndex >= dataLen) {
                    offset += dataLen;
                    CharacterUtils.fill(ioBuffer, input); // read supplementary char aware with CharacterUtils
                    if (ioBuffer.getLength() == 0) {
                        dataLen = 0; // so next offset += dataLen won't decrement offset
                        if (length > 0) {
                            break;
                        } else {
                            pos = -1;
                            //                        finalOffset = correctOffset(offset);
                            return;
                        }
                    }
                    dataLen = ioBuffer.getLength();
                    bufferIndex = 0;
                }

                final int ch = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex, ioBuffer.getLength());
                final int charCount = Character.charCount(ch);
                bufferIndex += charCount;

                pos++;
                CharType charType = CharTypeChecker.charType(ch);

                if (!CharType.SPACE.equals(charType)) {               // if it's a token char
                    if (length == 0) {                // start of token
                        assert start == -1;
                        start = offset + bufferIndex - charCount;
                        end = start;
                    } else if (length >= buffer.length - 1) { // check if a supplementary could run out of bounds
                        buffer = resizeBuffer(buffer, 2 + length); // make sure a supplementary fits in the buffer
                    }
                    end += charCount;
                    length += Character.toChars(normalize(ch), buffer, length); // buffer it, normalized
                    if (length >= 4068) // buffer overflow! make sure to check for >= surrogate pair could break == test
                        break;
                } else if (length > 0)             // at non-Letter w/ chars
                    break;


            }

            Info info = new Info(buffer);

//            logger.info("chars : {}, pos : {}, ", info.chars);

        }
        watch.stop();

        logger.info("elapsed time : {}", watch.getTime());
*/
    }

    protected int normalize(int c) {
        return c;
    }

    public final char[] resizeBuffer(char[] chars, int newSize) {
        char[] termBuffer = chars;
        if (chars.length < newSize) {
            // Not big enough; create a new array with slight
            // over allocation and preserve content
            final char[] newCharBuffer = new char[ArrayUtil.oversize(newSize, Character.BYTES)];
            System.arraycopy(termBuffer, 0, newCharBuffer, 0, termBuffer.length);
            termBuffer = newCharBuffer;
        }
        return termBuffer;
    }


    class Info {
        int pos;
        String str;
        char[] chars;

        public Info(char[] chars) {
            this.chars = chars;
        }
    }
}
