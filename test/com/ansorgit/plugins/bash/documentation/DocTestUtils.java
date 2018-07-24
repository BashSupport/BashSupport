/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.documentation;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

final class DocTestUtils {
    private DocTestUtils() {
    }

    static boolean isResponseContentValid(String url) {
        if ("true".equals(System.getProperty("bash.skipUrls", "false"))) {
            return true;
        }

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpOptions(url));

            Assert.assertTrue("Expected response content for " + url, 404 != response.getStatusLine().getStatusCode());

            String content = EntityUtils.toString(response.getEntity());
            return !content.contains("No matches for");
        } catch (Exception e) {
            return false;
        } finally {
            HttpClientUtils.closeQuietly(httpClient);
        }
    }
}
