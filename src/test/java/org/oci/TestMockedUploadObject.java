package org.oci;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigFileReader.class, ConfigFileAuthenticationDetailsProvider.class})
public class TestMockedUploadObject {

    @Test
    public void testUploadMethod() throws Exception {
        // mock the ConfigFileAuthenticationDetailsProvider constructor
        ConfigFileAuthenticationDetailsProvider provider = PowerMockito.mock(ConfigFileAuthenticationDetailsProvider.class);
        PowerMockito.whenNew(ConfigFileAuthenticationDetailsProvider.class)
                .withAnyArguments()
                .thenReturn(provider);

        File body = new File("path.txt");
        String configurationFilePath = "DEFAULT";
        String bucketName = "my-bucket";
        String objectName = "file.txt";
        Map<String, String> metadata = new HashMap<>();
        String contentType = "text/plain";
        String contentEncoding = "UTF-8";
        String contentLanguage = "en";

        UploadObject uploadObject = new UploadObject();
        UploadObject spy = spy(uploadObject);
        doReturn(GetObjectResponse.builder().build()).when(spy).uploadUsingRuntimeValues(any(), any(), any(), any(), any(), any(), any(), any());
        spy.uploadUsingOCIProfile(bucketName, objectName, metadata, contentType, contentEncoding, contentLanguage, "path.txt");

        // assert that the expected methods were called
        verify(spy, times(1)).uploadUsingOCIProfile(bucketName, objectName, metadata, contentType, contentEncoding, contentLanguage, "path.txt");
        ConfigFileReader.parse(configurationFilePath);
    }
}

