package org.resign.backend;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resign.backend.RetrieveResourceHandler;
import org.resign.backend.domain.Resource;
import org.resign.backend.domain.TestContext;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class RetrieveResourceHandlerTest {

    private static Resource input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testRetrieveResourceHandler() {
        RetrieveResourceHandler handler = new RetrieveResourceHandler();
        Context ctx = createContext();

        Resource output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        Assert.assertEquals("Hello from Lambda!", output);
    }
}
