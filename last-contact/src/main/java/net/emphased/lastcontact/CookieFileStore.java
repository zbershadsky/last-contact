/* Copyright (c) 2010 Dmitry Lisay <pingw33n@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. */
package net.emphased.lastcontact;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Supports storing cookies in a file..
 */
public class CookieFileStore extends BasicCookieStore
{
    public static final char SEPARATOR = ',';

    public static CookieFileStore load(Reader reader)
                throws IOException, CookieFileStoreException
    {
        CSVReader csvReader = new CSVReader(reader);
        CookieFileStore result = new CookieFileStore();
        for (String[] cookieData: csvReader.readAll()) {
            result.addCookie(cookieFromStringArray(cookieData));
        }
        return result;
    }

    public static CookieFileStore load(File file)
                throws IOException, CookieFileStoreException
    {
        Reader reader = new FileReader(file);
        try {
            return load(reader);
        } finally {
            reader.close();
        }
    }

    public static CookieFileStore load(String filename)
                throws IOException, CookieFileStoreException
    {
        return load(new File(filename));
    }

    public synchronized void save(Writer writer)
                throws IOException, CookieFileStoreException
    {
        CSVWriter csvWriter = new CSVWriter(writer, SEPARATOR);
        List<Cookie> cookies = getCookies();
        for (Cookie cookie: cookies) {
            csvWriter.writeNext(cookieToStringArray(cookie));
        }
    }

    public void save(File file)
            throws IOException, CookieFileStoreException
    {
        Writer writer = new FileWriter(file);
        try {
            save(writer);
        } finally {
            writer.close();
        }
    }

    public void save(String filename)
            throws IOException, CookieFileStoreException
    {
        save(new File(filename));
    }

    private static String[] cookieToStringArray(Cookie cookie)
    {
        return new String[] {
            cookie.getName(),
            cookie.getValue(),
            String.valueOf(cookie.getExpiryDate().getTime()),
            cookie.getPath(),
            cookie.getDomain(),
        };
    }

    private static Cookie cookieFromStringArray(String[] cookieData) throws CookieFileStoreException
    {
        BasicClientCookie result = new BasicClientCookie(cookieData[0], cookieData[1]);
        try {
            result.setExpiryDate(new Date(Long.parseLong(cookieData[2])));
        } catch (NumberFormatException e){
            throw new CookieFileStoreException(e);
        }
        result.setPath(cookieData[3]);
        result.setDomain(cookieData[4]);
        return result;
    }
}
