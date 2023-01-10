package org.oci;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class UploadObject {

    public GetObjectResponse uploadUsingOCIProfile(String namespaceName, String bucketName, String objectName, Map<String, String> metadata, String contentType, String contentEncoding, String contentLanguage, File body, String configurationFilePath) throws Exception {
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configurationFilePath);

        final ConfigFileAuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(configFile);

        ObjectStorage client = new ObjectStorageClient(provider);
        client.setRegion(Region.US_ASHBURN_1);

        // configure upload settings as desired
        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();

        UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

         //TODO() namesapces
         GetNamespaceRequest getNamespaceRequest = GetNamespaceRequest.builder().build();
         GetNamespaceResponse getNamespaceResponse = client.getNamespace(getNamespaceRequest);
         namespaceName = getNamespaceResponse.getValue();

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .objectName(objectName)
                        .contentType(contentType)
                        .contentLanguage(contentLanguage)
                        .contentEncoding(contentEncoding)
                        .opcMeta(metadata)
                        .build();

        UploadManager.UploadRequest uploadDetails =
                UploadManager.UploadRequest.builder(body).allowOverwrite(true).build(request);

        // upload request and print result
        // if multi-part is used, and any part fails, the entire upload fails and will throw
        // BmcException
        UploadResponse response = uploadManager.upload(uploadDetails);
        System.out.println(response);

        // fetch the object just uploaded
        GetObjectResponse getResponse =
                client.getObject(
                        GetObjectRequest.builder()
                                .namespaceName(namespaceName)
                                .bucketName(bucketName)
                                .objectName(objectName)
                                .build());

        // use the response's function to print the fetched object's metadata
        System.out.println(getResponse.getOpcMeta());
        return getResponse;
    }

    public GetObjectResponse uploadUsingRuntimeProperties() throws IOException {
        return null;
    }

}
