package com.aws.example.sagemaker;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;
import software.amazon.awssdk.services.sagemakerruntime.model.SageMakerRuntimeException;
import software.amazon.awssdk.core.SdkBytes;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.nio.ByteBuffer;
import java.io.IOException;

public class Handler{
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Must have one argument for model version!");
            System.exit(1);
            
        }
        
        String version = args[0];

        Region region = Region.CN_NORTHWEST_1;
        SageMakerRuntimeClient sageMakerClient = SageMakerRuntimeClient.builder()
                .region(region)
                .build();

        callEndpoint(sageMakerClient, version);
        sageMakerClient.close();
    }
    
     public static void callEndpoint(SageMakerRuntimeClient sageMakerClient, String version) {
        try {
            Class clazz = Handler.class;
            InputStream inputStream = clazz.getResourceAsStream("/data.json");
            ByteBuffer data = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
                
            InvokeEndpointRequest req = InvokeEndpointRequest.builder()
                .endpointName("ad-tf2-endpoint-20210131")
                .contentType("application/json")
                .targetVariant(version)
                .body(SdkBytes.fromByteBuffer(data))
                .build();
                
            InvokeEndpointResponse res = sageMakerClient.invokeEndpoint(req);
            String value = res.body().asUtf8String();
            System.out.println("Response is: " + value);


        } catch (SageMakerRuntimeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
     }
}