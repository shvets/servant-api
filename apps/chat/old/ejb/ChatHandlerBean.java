package org.google.code.netapps.chat.chat.ejb;

import java.net.*;
import java.io.*;

import javax.ejb.*;
import javax.naming.*;
import javax.servlet.*;

import org.google.code.netapps.chat.chat.net.*;
import org.google.code.netapps.chat.chat.defaults.*;

/**
 * This is a chat handler bean.
 *
 * @session-type Stateless
 * @transaction-type Bean
 *
 * @display-name TheChatHandlerBean
 * @ejb-name TheChatHandlerBean
 * @jndi-name ejb/ChatHandler
 *
 * @ejb-description The description of ChatHandler Enterprise Application.
 * @ejb-display-name EjbTier
 *
 *
 * @security-role-ref csr csr_role
 * @permission csr_role
 *
 */
public class ChatHandlerBean implements SessionBean {
  /**
   * The resource reference for properties file
   *
   * @resource-ref url/ChatProperties java.net.URL Application
   * @jndi-name http://localhost:8000/chat-handler/config/services/ChatHandler.properties
   */
  final public static String CHAT_HANDLER_RESOURCE_REF = "url/ChatProperties";

  private static String error = "";

  private static EJBChatServer server;

  static {
    Context initialContext = null;
    try {
      initialContext = new InitialContext();
    }
    catch(Throwable e) {
      error = "Unable to get initial JNDI context: " + e.toString();
    }

    Context context = null;
    try {
      context = (Context)initialContext.lookup("java:comp/env");
    }
    catch (Throwable e) {
      error = "Unable to get JNDI context: " + e.toString();
    }

    try {
      // perform JNDI lookup to obtain resource manager connection factory
      URL url = (URL)context.lookup(CHAT_HANDLER_RESOURCE_REF);

      Configurator configurator = new Configurator(url);

      configurator.load();

      server = new EJBChatServer(configurator);

      server.start();
    }
    catch (Throwable e) {
      error = "Unable to get JNDI context " + CHAT_HANDLER_RESOURCE_REF + ": " + e.toString();
//      throw new IOException("Unable to get JNDI context: " + e.toString());
    }
  }

  public ChatHandlerBean() {}

   /**
   * @business-method
   */
  public StringBuffer handleRequest(String[] request) {
    StringBuffer response = new StringBuffer();

    try {
      server.service(request, response);
    }
    catch(IOException e) {
      server.getLogger().logMessage(e.toString());
    }

    return response;
  }

  public void ejbCreate() {}

  public void ejbActivate() {}

  public void ejbPassivate() {}

  public void ejbRemove() {}

  public void setSessionContext(SessionContext context) {}

}
