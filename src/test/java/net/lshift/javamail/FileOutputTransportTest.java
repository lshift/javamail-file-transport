package net.lshift.javamail;

import org.junit.Before;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test cases for the FileOutputTransport.
 */
public class FileOutputTransportTest {
  private File messageDir = new File("target/messages").getAbsoluteFile();

  @Before
  public void clearMessagesDir() {
    File[] messages = messageDir.listFiles();
    if (messages != null) {
      for (File f : messages) {
        if (f.isFile()) f.delete();
      }
    }
  }

  @Test
  public void transportShouldBeMappedWithFileProtocol() throws Exception {
    Session session = createSession();
    assertThat(session.getTransport(), is(instanceOf(FileOutputTransport.class)));
  }

  @Test
  public void shouldWriteOutMailFile() throws Exception {
    Session session = createSession();
    
    MimeMessage message = new MimeMessage(session);
    message.addRecipient(Message.RecipientType.TO, new InternetAddress("user@domain.com"));
    message.setSubject("Subject");
    message.setText("Body");

    session.getTransport().sendMessage(message, message.getAllRecipients());

    assertEquals(1, messageDir.list().length);
    FileInputStream msgReader = new FileInputStream(messageDir.listFiles()[0]);
    try {
      MimeMessage msg = new MimeMessage(session, msgReader);
      assertEquals(1, msg.getAllRecipients().length);
      assertEquals("user@domain.com", msg.getAllRecipients()[0].toString());
      assertEquals("Subject", msg.getSubject());
    } finally {
      msgReader.close();
    }
  }

  private Session createSession() {
    Properties props = new Properties();
    props.put("mail.transport.protocol", "file");
    props.put("mail.from", "test@example.com");
    props.put("mail.senderName", "Test Sender");
    props.put("mail.debug", true);
    props.put("mail.file.dir", messageDir.getPath());

    return Session.getInstance(props);
  }
}
