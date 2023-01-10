package org.oci;


import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.oci.Generator.*;

public class TestUploadObject {

    private static final String NAMESPACE_NAME = "axehnqphw4ez";
    private static final String BUCKET_NAME = "bucket-20230110-1322";
    private static final String CONTENT_TYPE = "text/plain";
    private static final String CONTENT_ENCODING = "UTF-8";
    private static final String CONTENT_LANGUAGE = "en-US";
    private static Map<String, String> metadata;
    private static ConfigFileAuthenticationDetailsProvider provider;

    @BeforeAll
    public static void setUp() throws Exception {
        // Create a file and metadata Map to use in the test.
        metadata = Map.of("key", "value");
        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        provider = new ConfigFileAuthenticationDetailsProvider(configFile);
    }

    @Test
    public void testUploadDifferentFiles() throws Exception {
        // Create a file with a random string
        String fileContent1 = RandomStringUtils.randomAlphanumeric(100);
        File file1 = File.createTempFile(RandomStringUtils.randomAlphanumeric(10), ".txt");
        FileUtils.writeStringToFile(file1, fileContent1, "UTF-8");

        // Create another file with a different random string
        String fileContent2 = RandomStringUtils.randomAlphanumeric(100);
        File file2 = File.createTempFile(RandomStringUtils.randomAlphanumeric(10), ".txt");
        FileUtils.writeStringToFile(file2, fileContent2, "UTF-8");

        // Upload the first file
        String object1 = RandomStringUtils.randomAlphanumeric(10);
        String contentType1 = "text/plain";
        String contentEncoding1 = "UTF-8";
        String contentLanguage1 = "en-us";

        UploadObject.upload(NAMESPACE_NAME, BUCKET_NAME, object1, metadata, contentType1, contentEncoding1, contentLanguage1, file1);

        // Verify the first file was uploaded successfully
        ObjectStorage client = new ObjectStorageClient(provider);
        GetObjectResponse response = client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(NAMESPACE_NAME)
                        .bucketName(BUCKET_NAME)
                        .objectName(object1)
                        .build());
        assertEquals(contentType1, response.getContentType());
        assertEquals(contentEncoding1, response.getContentEncoding());

        // Upload the second file
        String object2 = RandomStringUtils.randomAlphanumeric(10);
        String contentType2 = "text/plain";
        String contentEncoding2 = "UTF-8";
        String contentLanguage2 = "en-us";

        UploadObject.upload(NAMESPACE_NAME, BUCKET_NAME, object2, metadata, contentType2, contentEncoding2, contentLanguage2, file2);

        // Verify the second file was uploaded successfully
        GetObjectResponse response2 = client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(NAMESPACE_NAME)
                        .bucketName(BUCKET_NAME)
                        .objectName(object2)
                        .build());
        assertEquals(contentType2, response2.getContentType());
        assertEquals(contentEncoding2, response2.getContentEncoding());

        // Cleanup the created files
        file1.delete();
        file2.delete();
    }

    @Test
    public void testUploadWithDifferentEncoding() throws Exception {
        String newEncoding = "UTF-16";
        // Create a new file with different encoding
        File newFile = createFileWithEncoding(newEncoding);
        String object =RandomStringUtils.randomAlphanumeric(10);

        // Call the upload method with the new file and encoding
        UploadObject.upload(NAMESPACE_NAME, BUCKET_NAME, object, metadata, CONTENT_TYPE, newEncoding, CONTENT_LANGUAGE, newFile);

        // Verify that the upload was successful by calling the client's getObject method
        ObjectStorage client = new ObjectStorageClient(provider);
        GetObjectResponse response = client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(NAMESPACE_NAME)
                        .bucketName(BUCKET_NAME)
                        .objectName(object)
                        .build());

        // Assert that the response's content-encoding match what we expect
        assertEquals(newEncoding, response.getContentEncoding());
    }

    @Test
    public void testUploadWithDifferentContentTypes() throws Exception {
        String contentType1 = "application/json";
        String contentType2 = "image/png";
        Map<String, String> fileName = Map.of(contentType1, RandomStringUtils.randomAlphanumeric(10) + ".json", contentType2, RandomStringUtils.randomAlphanumeric(10) + ".png");

        // Create two new files with different content types
        File file1 = createFileWithContentType(contentType1, fileName.get(contentType1));
        File file2 = createFileWithContentType(contentType2, fileName.get(contentType2));

        // Call the upload method with the first file and content type
        UploadObject.upload(NAMESPACE_NAME, BUCKET_NAME, fileName.get(contentType1), metadata, (contentType1), CONTENT_ENCODING, CONTENT_LANGUAGE, file1);
        // Verify that the first upload was successful by calling the client's getObject method
        ObjectStorage client = new ObjectStorageClient(provider);
        GetObjectResponse response1 = client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(NAMESPACE_NAME)
                        .bucketName(BUCKET_NAME)
                        .objectName(fileName.get(contentType1))
                        .build());
        // Assert that the response's content-type match what we expect
        assertEquals(contentType1, response1.getContentType());

        // Call the upload method with the second file and content type
        UploadObject.upload(NAMESPACE_NAME, BUCKET_NAME, fileName.get(contentType2), metadata, (contentType2), CONTENT_ENCODING, CONTENT_LANGUAGE, file2);
        // Verify that the second upload was successful by calling the client's getObject method
        GetObjectResponse response2 = client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(NAMESPACE_NAME)
                        .bucketName(BUCKET_NAME)
                        .objectName(fileName.get(contentType2))
                        .build());
        // Assert that the response's content-type match what we expect
        assertEquals(contentType2, response2.getContentType());
    }

    @Test
    public void testUpload1MBFile() throws Exception {
        // Generate a 10MB file with random text
        String fileContent = RandomStringUtils.randomAlphanumeric(1024 * 1024);
        File file = File.createTempFile(RandomStringUtils.randomAlphanumeric(10), ".txt");
        FileUtils.writeStringToFile(file, fileContent, "UTF-8");

        // Upload the file
        String object = RandomStringUtils.randomAlphanumeric(15);
        String contentType = "text/plain";
        String contentEncoding = "UTF-8";
        String contentLanguage = "en-us";

        UploadObject.upload(NAMESPACE_NAME, BUCKET_NAME, object, metadata, contentType, contentEncoding, contentLanguage, file);

        // Verify the file was uploaded successfully
        ObjectStorage client = new ObjectStorageClient(provider);
        GetObjectResponse response = client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(NAMESPACE_NAME)
                        .bucketName(BUCKET_NAME)
                        .objectName(object)
                        .build());
        assertEquals(contentType, response.getContentType());
        assertEquals(contentEncoding, response.getContentEncoding());

        // Cleanup the created file
        file.delete();
    }

}
