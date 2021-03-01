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
        final CfnOutput snsPublishRoleOut = CfnOutput
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
        final CfnOutput snsPublishGroupOut = CfnOutput
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
        final CfnOutput topicArnOut = CfnOutput
            .Builder
            .create(this, "RsEventNotificationsTopicArnOut")
            .exportName("RsEventNotificationsTopicArn")
            .description("ARN of RsEventNotifications Topic")
            .value(topic.getTopicArn())
            .build();
        final CfnOutput topicNameOut = CfnOutput
            .Builder
            .create(this, "RsEventNotificationsTopicNameOut")
            .exportName("RsEventNotificationsTopicName")
            .description("Name of RsEventNotifications Topic")
            .value(topic.getTopicName())
            .build();

        topic.grantPublish(snsPublishRole);
        topic.grantPublish(snsPublisherGroup);

        // SQS Consumer Mobile2_Client
        final Role sqsConsumeRole_m2c = Role.Builder.create(this, "RsEventNotificationsConsumerRole_m2c").assumedBy(new AnyPrincipal()).build();
        final Group sqsConsumerGroup_m2c = Group.Builder.create(this, "RsEventNotificationConsumerGroup_m2c").build();

        final Queue queue_m2c = Queue.Builder.create(this, "RsEventNotifications_m2c")
            .fifo(true) // or false
            .visibilityTimeout(Duration.seconds(300))
            .build();

        topic.addSubscription(new SqsSubscription(queue_m2c));

        PolicyStatement sqsPolicyStatement_m2c = new PolicyStatement();
        sqsPolicyStatement_m2c.setSid("Consume");
        sqsPolicyStatement_m2c.setEffect(Effect.ALLOW);
        sqsPolicyStatement_m2c.addActions("SQS:ReceiveMessage");
        sqsPolicyStatement_m2c.addActions("SQS:DeleteMessage");
        sqsPolicyStatement_m2c.addResources(queue_m2c.getQueueArn());
        PolicyDocument sqsPolicyDocument_m2c = new PolicyDocument();
        sqsPolicyDocument_m2c.addStatements(sqsPolicyStatement_m2c);

        Policy sqsPolicy_m2c = Policy.Builder.create(this, "RsEventNotificationsConsumerPolicy_m2c").document(sqsPolicyDocument_m2c).build();

        sqsConsumeRole_m2c.attachInlinePolicy(sqsPolicy_m2c);
        sqsConsumerGroup_m2c.attachInlinePolicy(sqsPolicy_m2c);
    
        // SQS Consumer Mobile3_Client
        final Role sqsConsumeRole_m3c = Role.Builder.create(this, "RsEventNotificationsConsumerRole_m3c").assumedBy(new AnyPrincipal()).build();
        final Group sqsConsumerGroup_m3c = Group.Builder.create(this, "RsEventNotificationConsumerGroup_m3c").build();

        final Queue queue_m3c = Queue.Builder.create(this, "RsEventNotifications_m3c")
            .fifo(true) // or false
            .visibilityTimeout(Duration.seconds(300))
            .build();

        topic.addSubscription(new SqsSubscription(queue_m3c));

        PolicyStatement sqsPolicyStatement_m3c = new PolicyStatement();
        sqsPolicyStatement_m3c.setSid("Consume");
        sqsPolicyStatement_m3c.setEffect(Effect.ALLOW);
        sqsPolicyStatement_m3c.addActions("SQS:ReceiveMessage");
        sqsPolicyStatement_m3c.addActions("SQS:DeleteMessage");
        sqsPolicyStatement_m3c.addResources(queue_m3c.getQueueArn());
        PolicyDocument sqsPolicyDocument_m3c = new PolicyDocument();
        sqsPolicyDocument_m3c.addStatements(sqsPolicyStatement_m3c);

        Policy sqsPolicy_m3c = Policy.Builder.create(this, "RsEventNotificationsConsumerPolicy_m3c").document(sqsPolicyDocument_m3c).build();

        sqsConsumeRole_m3c.attachInlinePolicy(sqsPolicy_m3c);
        sqsConsumerGroup_m3c.attachInlinePolicy(sqsPolicy_m3c);
    }
}
