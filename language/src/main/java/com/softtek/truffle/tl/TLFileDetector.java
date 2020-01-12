package com.softtek.truffle.tl;

import com.oracle.truffle.api.TruffleFile;
import java.io.IOException;
import java.nio.charset.Charset;

public final class TLFileDetector implements TruffleFile.FileTypeDetector {

    @Override
    public String findMimeType(TruffleFile file) throws IOException {
        String name = file.getName();
        if (name != null && name.endsWith(".tl")) {
            return TLLanguage.MIME_TYPE;
        }
        return null;
    }

    @Override
    public Charset findEncoding(TruffleFile file) throws IOException {
        return null;
    }
}
