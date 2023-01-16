package org.oci;


import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.io.File;
import java.util.Map;

import static org.oci.Generator.createFileWithContentType;
import static org.oci.Generator.createFileWithEncoding;

public class TestUploadObject {

    private static final String BUCKET_NAME = "DBMI-DEV-RPS";
    private static final String CONTENT_TYPE = "text/plain";
    private static final String CONTENT_ENCODING = "UTF-8";
    private static final String CONTENT_LANGUAGE = "en-US";
    private static Map<String, String> metadata;
    private static ConfigFileAuthenticationDetailsProvider provider;

    private static final UploadObject uploadObject = new UploadObject();

    @BeforeClass
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

        GetObjectResponse response = uploadObject.uploadUsingOCIProfile(BUCKET_NAME, object1, metadata, contentType1, contentEncoding1, contentLanguage1, file1.getAbsolutePath());

        // Verify the first file was uploaded successfully
        Assert.assertEquals(contentType1, response.getContentType());
        Assert.assertEquals(contentEncoding1, response.getContentEncoding());

        // Upload the second file
        String object2 = RandomStringUtils.randomAlphanumeric(10);
        String contentType2 = "text/plain";
        String contentEncoding2 = "UTF-8";
        String contentLanguage2 = "en-us";

        GetObjectResponse response2 = uploadObject.uploadUsingOCIProfile(BUCKET_NAME, object2, metadata, contentType2, contentEncoding2, contentLanguage2, file2.getAbsolutePath());
        Assert.assertEquals(contentType2, response2.getContentType());
        Assert.assertEquals(contentEncoding2, response2.getContentEncoding());

        // Cleanup the created files
        file1.delete();
        file2.delete();
    }

    @Test
    public void testUploadWithDifferentEncoding() throws Exception {
        String newEncoding = "UTF-16";
        // Create a new file with different encoding
        File newFile = createFileWithEncoding(newEncoding);
        String object = RandomStringUtils.randomAlphanumeric(10);

        // Call the upload method with the new file and encoding
        GetObjectResponse response = uploadObject.uploadUsingOCIProfile(BUCKET_NAME, object, metadata, CONTENT_TYPE, newEncoding, CONTENT_LANGUAGE, newFile.getAbsolutePath());

        // Assert that the response's content-encoding match what we expect
        Assert.assertEquals(newEncoding, response.getContentEncoding());

        newFile.delete();
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
        GetObjectResponse response1 = uploadObject.uploadUsingOCIProfile(BUCKET_NAME, fileName.get(contentType1), metadata, (contentType1), CONTENT_ENCODING, CONTENT_LANGUAGE, file1.getAbsolutePath());

        // Assert that the response's content-type match what we expect
        Assert.assertEquals(contentType1, response1.getContentType());

        // Call the upload method with the second file and content type
        GetObjectResponse response2 = uploadObject.uploadUsingOCIProfile(BUCKET_NAME, fileName.get(contentType2), metadata, (contentType2), CONTENT_ENCODING, CONTENT_LANGUAGE, file2.getAbsolutePath());

        // Assert that the response's content-type match what we expect
        Assert.assertEquals(contentType2, response2.getContentType());

        file1.delete();
        file2.delete();
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

        GetObjectResponse response = uploadObject.uploadUsingOCIProfile(BUCKET_NAME, object, metadata, contentType, contentEncoding, contentLanguage, file.getAbsolutePath());

        // Verify the file was uploaded successfully
        Assert.assertEquals(contentType, response.getContentType());
        Assert.assertEquals(contentEncoding, response.getContentEncoding());

        // Cleanup the created file
        file.delete();
    }

}
