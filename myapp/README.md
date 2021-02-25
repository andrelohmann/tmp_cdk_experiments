mvn package

java -jar target/myapp-1.0-SNAPSHOT.jar

java -jar target/myapp-1.0-SNAPSHOT.jar "Good morning Kiel" arn:aws:sns:eu-central-1:394046106526:CdkWorkshopStack-CdkWorkshopTopicD368A42F-12S35MWTUD2EL

mvn exec:java -D exec.mainClass=com.example.myapp.App -Dexec.args="'Good morning Kiel' arn:aws:sns:eu-central-1:394046106526:CdkWorkshopStack-CdkWorkshopTopicD368A42F-12S35MWTUD2EL"
