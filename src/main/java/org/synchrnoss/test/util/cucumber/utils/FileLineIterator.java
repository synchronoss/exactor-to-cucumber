package org.synchrnoss.test.util.cucumber.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class FileLineIterator implements Iterable<String>
{
    private final BufferedReader _reader;
    private String _next;

    public FileLineIterator(BufferedReader reader) throws FileNotFoundException {
        _reader = reader;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>()
        {
            @Override
            public boolean hasNext() {
                try {
                    _next = _reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (_next == null) {
                    return false;
                } else if (StringUtils.EMPTY.equals(_next.trim())) {
                    return hasNext();
                } else {
                    return true;
                }
            }

            @Override
            public String next() {
                return _next.trim();
            }
        };
    }
}