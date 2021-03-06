/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.test.fixtures.server.s3

import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.fixtures.resource.RemoteResource

class S3Resource implements RemoteResource {
    S3StubServer server
    TestFile file
    S3StubSupport s3StubSupport
    String bucket
    String repositoryPath

    S3Resource(S3StubServer server, TestFile file, String repositoryPath, String bucket) {
        this.repositoryPath = repositoryPath
        this.bucket = bucket
        this.server = server
        this.s3StubSupport = new S3StubSupport(server)
        this.file = file
    }

    @Override
    URI getUri() {
        return new URI("s3:/${relativeFilePath()}")
    }

    @Override
    void expectDownload() {
        s3StubSupport.stubGetFile(file, relativeFilePath())
    }

    @Override
    void expectDownloadMissing() {
        def path = relativeFilePath()
        s3StubSupport.stubFileNotFound(path)
    }

    @Override
    void expectMetadataRetrieve() {
        s3StubSupport.stubMetaData(file, relativeFilePath())
    }

    @Override
    void expectMetadataRetrieveMissing() {
        s3StubSupport.stubMetaDataMissing(relativeFilePath())
    }

    @Override
    void expectDownloadBroken() {
        s3StubSupport.stubGetFileBroken(relativeFilePath())
    }

    @Override
    void expectMetadataRetrieveBroken() {
        s3StubSupport.stubMetaDataBroken(relativeFilePath())
    }

    def relativeFilePath() {
        String absolute = file.toURI()
        String base = "/${bucket}$repositoryPath"
        absolute.substring(absolute.indexOf(base), absolute.length())
    }
}
