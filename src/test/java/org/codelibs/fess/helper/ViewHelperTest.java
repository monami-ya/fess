/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.helper;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class ViewHelperTest extends UnitFessTestCase {
    public ViewHelper viewHelper;

    public void setUp() throws Exception {
        super.setUp();
        viewHelper = new ViewHelper();
        viewHelper.init();
    }

    public void test_replaceHighlightQueries() {
        String text;
        String[] queries;

        text = "<a>123<b>456<c>";
        queries = new String[] { "123", "456" };
        assertEquals("<a><strong>123</strong><b><strong>456</strong><c>", viewHelper.replaceHighlightQueries(text, queries));

        text = "<123><456>";
        queries = new String[] { "123", "456" };
        assertEquals("<123><456>", viewHelper.replaceHighlightQueries(text, queries));

        text = "123<aaa 123>456<bbb 456>123";
        queries = new String[] { "123", "456" };
        assertEquals("<strong>123</strong><aaa 123><strong>456</strong><bbb 456><strong>123</strong>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "abc";
        queries = new String[] { "123" };
        assertEquals("abc", viewHelper.replaceHighlightQueries(text, queries));

        text = "123";
        queries = new String[] { "123" };
        assertEquals("<strong>123</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "abc123efg";
        queries = new String[] { "123" };
        assertEquals("abc<strong>123</strong>efg", viewHelper.replaceHighlightQueries(text, queries));

        text = "123";
        queries = new String[] { "123", "456" };
        assertEquals("<strong>123</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "123456";
        queries = new String[] { "123", "456" };
        assertEquals("<strong>123</strong><strong>456</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "a123b456c";
        queries = new String[] { "123", "456" };
        assertEquals("a<strong>123</strong>b<strong>456</strong>c", viewHelper.replaceHighlightQueries(text, queries));

        text = "abc";
        queries = new String[] { "abc" };
        assertEquals("<strong>abc</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "1ABC2";
        queries = new String[] { "abc" };
        assertEquals("1<strong>abc</strong>2", viewHelper.replaceHighlightQueries(text, queries));
    }

    public void test_escapeHighlight() {
        viewHelper = new ViewHelper();
        viewHelper.useHighlight = true;
        viewHelper.init();

        String text = "";
        assertEquals("", viewHelper.escapeHighlight(text));

        text = "aaa";
        assertEquals("aaa", viewHelper.escapeHighlight(text));

        text = "<em>aaa</em>";
        assertEquals("<em>aaa</em>", viewHelper.escapeHighlight(text));

        text = "<em>aaa</em><b>bbb</b>";
        assertEquals("<em>aaa</em>&lt;b&gt;bbb&lt;/b&gt;", viewHelper.escapeHighlight(text));

    }

    public void test_getSitePath() {
        String urlLink;
        String sitePath;
        final Map<String, Object> docMap = new HashMap<>();

        urlLink = "http://www.google.com";
        sitePath = "www.google.com";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "https://www.jp.websecurity.symantec.com/";
        sitePath = "www.jp.websecurity.symantec.com/";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "http://www.qwerty.jp";
        sitePath = "www.qwerty.jp";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "www.google.com";
        sitePath = "www.google.com";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "smb://123.45.678.91/share1";
        sitePath = "123.45.678.91/share1";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "file://home/user/";
        sitePath = "home/user/";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));
    }
}
