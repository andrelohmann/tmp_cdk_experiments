package com.example.myapp;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;


/**
 * https://docs.aws.amazon.com/sns/latest/dg/sns-publishing.html
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-simple-notification-service.html
 * https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javav2/example_code/sns/src/main/java/com/example/sns/PublishTopic.java
 *
 *
 */
public class App 
{
    public static void main( String[] args )

    {
        System.out.println( "Hello World!" );

        final String USAGE = "\n" +
                "Usage: " +
                "PublishTopic <message> <topicArn>\n\n" +
                "Where:\n" +
                "  message - the message text to send.\n\n" +
                "  topicArn - the ARN of the topic to publish.\n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String message = args[0];
        String topicArn = args[1];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        PublishTopic.pubTopic(snsClient, message, topicArn);
        snsClient.close();
    }
}
