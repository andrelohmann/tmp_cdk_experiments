package com.myorg;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
//import software.amazon.awscdk.services.sqs.QueuePolicy;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.Group;
import software.amazon.awscdk.services.iam.Policy;
import software.amazon.awscdk.services.iam.PolicyDocument;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
//import software.amazon.awscdk.services.iam.ServicePrincipal;

public class CdkSnsSqsStack extends Stack {
    public CdkSnsSqsStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public CdkSnsSqsStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        // SNS Publisher
        final Role snsPublishRole = Role
            .Builder
            .create(this, "RsEventNotificationsPublishRole")
            .assumedBy(new AnyPrincipal())
            .build();
        CfnOutput
            .Builder
            .create(this, "RsEventNotificationsPublishRoleOut")
            .exportName("RsEventNotificationsPublishRole")
            .description("ARN of RsEventNotificationsPublishRole")
            .value(snsPublishRole.getRoleArn())
            .build();
        final Group snsPublisherGroup = Group
            .Builder
            .create(this, "RsEventNotificationPublishGroup")
            .build();
        CfnOutput
            .Builder
            .create(this, "RsEventNotificationPublishGroupOut")
            .exportName("RsEventNotificationPublishGroup")
            .description("ARN of RsEventNotificationPublishGroup")
            .value(snsPublisherGroup.getGroupArn())
            .build();

        final Topic topic = Topic.Builder.create(this, "RsEventNotifications")
            .topicName("RsEventNotifications")
            .fifo(true) // or false
            .displayName("RsEventNotifications")
            .build();
        CfnOutput
            .Builder
            .create(this, "RsEventNotificationsTopicArnOut")
            .exportName("RsEventNotificationsTopicArn")
            .description("ARN of RsEventNotifications Topic")
            .value(topic.getTopicArn())
            .build();
        CfnOutput
            .Builder
            .create(this, "RsEventNotificationsTopicNameOut")
            .exportName("RsEventNotificationsTopicName")
            .description("Name of RsEventNotifications Topic")
            .value(topic.getTopicName())
            .build();

        topic.grantPublish(snsPublishRole);
        topic.grantPublish(snsPublisherGroup);

        String[] clients = {"Mobile2Clinet", "Mobile3Client", "Mobile4Client"};

        this.createClients(topic, clients);
    }

    protected void createClients(Topic topic, String[] clients){
        
        for (String client: clients) {           
            // Iterate over all clients
            Role sqsConsumeRole = Role.Builder
                .create(this, "RsEventNotificationsConsumerRole_"+client)
                .assumedBy(new AnyPrincipal())
                .build();
            Group sqsConsumerGroup = Group.Builder
                .create(this, "RsEventNotificationConsumerGroup_"+client)
                .build();
            
            Queue queue = Queue.Builder.create(this, "RsEventNotifications_"+client)
                .fifo(true) // or false
                .visibilityTimeout(Duration.seconds(300))
                .build();
            
            topic.addSubscription(new SqsSubscription(queue));

            PolicyStatement sqsPolicyStatement = new PolicyStatement();
            sqsPolicyStatement.setSid("Consume");
            sqsPolicyStatement.setEffect(Effect.ALLOW);
            sqsPolicyStatement.addActions("SQS:ReceiveMessage");
            sqsPolicyStatement.addActions("SQS:DeleteMessage");
            sqsPolicyStatement.addResources(queue.getQueueArn());
            PolicyDocument sqsPolicyDocument = new PolicyDocument();
            sqsPolicyDocument.addStatements(sqsPolicyStatement);

            Policy sqsPolicy = Policy.Builder.create(this, "RsEventNotificationsConsumerPolicy_"+client).document(sqsPolicyDocument).build();

            sqsConsumeRole.attachInlinePolicy(sqsPolicy);
            sqsConsumerGroup.attachInlinePolicy(sqsPolicy);
        }
    }
}
