package net.lshift.javamail;

import javax.mail.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Java Mail transport that writes messages out to a file instead of sending them to a remote server.
 */
public class FileOutputTransport extends Transport {
  private final File directory;

  public FileOutputTransport(Session session, URLName urlname) {
    super(session, urlname);

    String configuredDirectory = session.getProperty("mail.file.dir");
    if (configuredDirectory != null) {
      directory = new File(configuredDirectory);
    } else {
      directory = new File(".").getAbsoluteFile();
    }
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        throw new IllegalArgumentException("Unable to create output directory " + directory);
      }
    }
  }

  @Override
  public void sendMessage(Message message, Address[] addresses) throws MessagingException {
    try {
      // Generate a UUID for the message
      UUID uuid = UUID.randomUUID();
      FileOutputStream fw = new FileOutputStream(new File(directory, uuid + ".msg"));
      try {
        message.writeTo(fw);
      } finally {
        fw.close();
      }
    } catch (IOException e) {
      throw new MessagingException("Failed to write file", e);
    }
  }
}
