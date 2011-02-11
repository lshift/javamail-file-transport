JavaMail File Transport
=======================
When developing applications that send mail, it is often desirable to be able to test the
mail sending code. Generally, this requires a functioning mail server setup.

The JavaMail File Transport allows you to send mail using a "file" protocol, which will
instead write the data out to a text file in a directory you specify, which can then be
read and examined.

As an example, if you were to create and send a mail message with:

    public class SendAMailTest {
      public void sendTestMessage() {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "file");
		props.put("mail.from", "test@example.com");
		props.put("mail.senderName", "Test Sender");
		props.put("mail.debug", true);
		props.put("mail.file.dir", "messages");
		
		Session session = Session.getInstance(props);
		MimeMessage message = new MimeMessage(session);
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("user@domain.com"));
		message.setSubject("Subject");
		message.setText("Body");

		session.getTransport().sendMessage(message, message.getAllRecipients());
      }
    }

Then a file would be created in messages/ using a random UUID and a .msg suffix.


Isolating Mail Configuration from your app
------------------------------------------
A common pattern is often to inject a mail session in via JNDI. This transport works especially
well for that, since it allows your application to be completely agnostic of the mail transport,
and for the provided jar to only be used in test scenarios.

For a Jetty application, if the WebAppContext was being configured, a sample context might be injected
with configuration such as:

	  <New id="mail" class="org.eclipse.jetty.plus.jndi.Resource">
		<Arg>mail/Session</Arg>
		<Arg>
		  <New class="org.eclipse.jetty.jndi.factories.MailSessionReference">
			<Set name="user"></Set>
			<Set name="password"></Set>
			<Set name="properties">
			  <New class="java.util.Properties">
				<Put name="mail.transport.protocol">file</Put>
				<Put name="mail.smtp.host">localhost</Put>
				<Put name="mail.from">app@example.com</Put>
				<Put name="mail.senderName">My Application</Put>
				<Put name="mail.file.dir">target/messages</Put>
			  </New>
			</Set>
		  </New>
		</Arg>
	  </New>

Usage With Maven Jetty Plugin
-----------------------------

The following fragment shows how the transport is configured with the Maven Jetty plugin:

  <plugin>
     <groupId>org.mortbay.jetty</groupId>
     <artifactId>jetty-maven-plugin</artifactId>
        <dependencies>

          <!-- OTHER DEPENDENCIES THAT SHOULD GO INTO JETTY'S LOAD PATH -->          

          <dependency>
            <groupId>net.lshift.javamail</groupId>
            <artifactId>javamail-file-transport</artifactId>
            <version>1.0</version>
          </dependency>

        </dependencies>

        <configuration>
          <systemProperties>
            <systemProperty>
              <name>JETTY_NO_SHUTDOWN_HOOK</name>
              <value>true</value>
            </systemProperty>
            <systemProperty>
              <name>basedir</name>
              <value>${basedir}</value>
            </systemProperty>
            <systemProperty>
              <name>diffa.maildir</name>
              <value>${basedir}/target/messages</value>
            </systemProperty>
          </systemProperties>
          <connectors>
            <connector implementation="org.eclipse.jetty.server.nio.SelectChannelConnector">
              <port>19093</port>
              <maxIdleTime>60000</maxIdleTime>
            </connector>
          </connectors>
          <scanIntervalSeconds>600</scanIntervalSeconds>
          <stopKey>foo</stopKey>
          <stopPort>19094</stopPort>
          <jettyConfig>${basedir}/src/test/conf/jetty.xml</jettyConfig>
          <webAppXml>${basedir}/src/test/conf/jetty-env.xml</webAppXml>
          <webAppConfig>
            <contextPath>/some-context-path</contextPath>
          </webAppConfig>
        </configuration>
        <executions>
          <execution>
            <id>start-jetty</id>
            <phase>pre-integration-test</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
              <scanIntervalSeconds>0</scanIntervalSeconds>
              <daemon>true</daemon>
            </configuration>
          </execution>
          <execution>
            <id>stop-jetty</id>
            <phase>post-integration-test</phase>
            <goals>
              <goal>stop</goal>
            </goals>
          </execution>
        </executions>
   </plugin>


License
-------

    Copyright 2010 LShift Ltd
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

