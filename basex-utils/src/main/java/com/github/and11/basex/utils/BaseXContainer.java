package com.github.and11.basex.utils;

import com.github.and11.basex.utils.options.FunctionUrlReference;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import org.basex.core.BaseXException;
import org.basex.core.Command;
import org.basex.core.Context;
import org.basex.core.cmd.Test;
import org.basex.query.QueryProcessor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface BaseXContainer extends AutoCloseable {

    class BaseXContainerException extends Exception {
        public BaseXContainerException(Throwable cause) {
            super(cause);
        }

        public BaseXContainerException(String message) {
            super(message);
        }
    }


    class CloseableContext implements AutoCloseable {

        private final Context context;

        public CloseableContext(Context context) {
            this.context = context;
        }

        public QueryProcessor buildQueryProcessor(String query){
            return new QueryProcessor(query, context);
        }

        public void execute(Command cmd) throws BaseXException {
            cmd.execute(context);
        }

        public void execute(Test test, OutputStream os) throws BaseXException {
            test.execute(context, os);
        }

        @Override
        public void close() throws Exception {
            context.close();
        }
    }

    File getWorkingDirectory();

    void provision(ProvisionOption... urls) throws BaseXContainerException;
    void export(FunctionUrlReference function, OutputStream os) throws UrlStreamHandler.UnresolvableUrlException, IOException;
    void test(String url, OutputStream os);
}
