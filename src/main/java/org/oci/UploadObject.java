package org.oci;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
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

    /* reads OCI connection details from a config file, then makes a connection using coded values */
    public GetObjectResponse uploadUsingOCIProfile(String bucketName,
                                                   String objectName,
                                                   Map<String, String> metadata,
                                                   String contentType,
                                                   String contentEncoding,
                                                   String contentLanguage,
                                                   String uploadFilePath) throws IOException {
        File body = new File(uploadFilePath);
        String configurationFilePath = "c:\\Users\\AHodgson\\Documents\\OCIProfile.txt";

        // Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI
        // config file
        // "~/.oci/config", and a profile in that config with the name "DEFAULT". Make changes to
        // the following
        // line if needed and use ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);

        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configurationFilePath);

        final ConfigFileAuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(configFile);

        return uploadUsingRuntimeValues(provider, uploadFilePath, bucketName, objectName, metadata, contentType, contentEncoding, contentLanguage);

    }

    /* reads OCI connection auth details from a class, then connects as above.*/
    public GetObjectResponse uploadUsingRuntimeValues(AuthenticationDetailsProvider provider,
                                                      String uploadFilePath,
                                                      String bucketName,
                                                      String objectName,
                                                      Map<String, String> metadata,
                                                      String contentType,
                                                      String contentEncoding,
                                                      String contentLanguage) {

        File body = new File(uploadFilePath);


        ObjectStorage client = new ObjectStorageClient(provider);
        client.setRegion(Region.US_ASHBURN_1);

        // configure upload settings as desired
        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();

        UploadManager uploadManager = new UploadManager(client, uploadConfiguration);


        GetNamespaceRequest getNamespaceRequest = GetNamespaceRequest.builder().build();

        GetNamespaceResponse getNamespaceResponse = client.getNamespace(getNamespaceRequest);

        String namespaceName = getNamespaceResponse.getValue();

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(bucketName)
                        .namespaceName(namespaceName)
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


}
