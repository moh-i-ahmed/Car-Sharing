package springData.utils

import groovy.transform.CompileStatic
import org.junit.Test

@CompileStatic
class AccessCodeGeneratorTest {

    @Test
    void testGenerateAccessCode() {
        assert "result" == AccessCodeGenerator.generateAccessCode()
    }
}
