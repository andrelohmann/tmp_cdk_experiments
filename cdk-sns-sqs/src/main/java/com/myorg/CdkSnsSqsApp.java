package com.myorg;

import software.amazon.awscdk.core.App;

public final class CdkSnsSqsApp {
    public static void main(final String[] args) {
        App app = new App();

        new CdkSnsSqsStack(app, "CdkSnsSqsStack");

        app.synth();
    }
}
