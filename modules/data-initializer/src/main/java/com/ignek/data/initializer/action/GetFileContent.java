package com.ignek.data.initializer.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.InputStream;

public class GetFileContent {

    protected String getFileContents(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (Exception e) {
            _log.error("Unable to read file: " + fileName, e);
        }
        return StringPool.BLANK;
    }

    private static final Log _log = LogFactoryUtil.getLog(GetFileContent.class);
}
