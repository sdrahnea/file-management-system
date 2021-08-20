package util;

import com.fms.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileUtilsTest {

    FileUtils fileUtils;

    @Test
    public void should_correct_paths(){
        Assertions.assertEquals(
                null, fileUtils.computeAbsolutePath()
        );

        Assertions.assertEquals(
                "aaa", fileUtils.computeAbsolutePath("aaa")
        );

        Assertions.assertEquals(
                "aaa/bbb", fileUtils.computeAbsolutePath("aaa", "bbb")
        );

        Assertions.assertEquals(
                "aaa/bbb/ccc", fileUtils.computeAbsolutePath("aaa", "bbb", "ccc")
        );
    }

}
