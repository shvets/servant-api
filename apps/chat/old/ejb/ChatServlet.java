package org.google.code.netapps.chat.chat.ejb;

import java.io.*;
import java.util.*;
import java.rmi.*;

import javax.rmi.*;
import javax.naming.*;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * This is a chat handler servlet.
 *
 * @servlet
 * @servlet-name TheChatServlet
 * @display-name TheChatServlet
 *
 * @servlet-mapping /ask
 *
 * @web-description The description of chat handler Web application.
 * @web-display-name WebTier
 * @web-context-root chat-handler
 *
 * @ejb-ref com.kana.realtime.ejb.ChatHandlerBean
 */
public class ChatServlet extends HttpServlet {

//  final static String CHAT_HANDLER_EJB_REF = "ejb/ChatHandler";
//  final static String CHAT_HANDLER_RESOURCE_REF = "url/ChatProperties";

  static {
    SecurityManager securityManager = System.getSecurityManager();
    if(securityManager == null) {
      System.setSecurityManager(new RMISecurityManager());
    }
  }

  // A reference to the remote `ChatHandler' object
  protected ChatHandlerRemote chatHandler;

  public ChatServlet() {
    super();
  }


  // Initializes this servlet
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    String contentFactory = getInitParameter(Context.INITIAL_CONTEXT_FACTORY);

    if(contentFactory != null) {
      System.setProperty(Context.INITIAL_CONTEXT_FACTORY, contentFactory);
    }

    String providerURL = getInitParameter(Context.PROVIDER_URL);

    if(providerURL != null) {
      System.setProperty(Context.PROVIDER_URL, providerURL);

    }

    // Get the initial JNDI context using our settings
    Context initialContext;
    try {
      initialContext = new InitialContext();
    }
    catch(Throwable e) {
      throw new ServletException("Unable to get initial JNDI context: " + e.toString());
    }

    Context context;
    try {
      context = (Context)initialContext.lookup("java:comp/env");
    }
    catch (Throwable e) {
      throw new ServletException("Unable to get JNDI context: " + e.toString());
    }

    ChatHandlerHome home;
    try {                                                                                
      Object boundObject = context.lookup(ChatHandlerHome.JNDI_NAME);
      home = (ChatHandlerHome)PortableRemoteObject.narrow(boundObject, ChatHandlerHome.class);
    }
    catch(Throwable e) {
      throw new ServletException("Unable to get home interface: " + e.toString());
    }

    // Get a reference to a ChatHandler instance
    try {
      chatHandler = home.create();
    }
    catch(Throwable e) {
      throw new ServletException("Unable to create ChatHandler instance: " + e.toString());
    }

    // Insanity check: Make sure we have a valid reference
    if (chatHandler == null) {
      System.out.println("Unable to create ChatHandler instance");
      throw new ServletException("Unable to create ChatHandler instance");
    }
  }


   // Handles the HTTP GET request
   public void doGet(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
     handleRequest(request, response);
   }

   // Handles the HTTP POST request
   public void doPost(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
     handleRequest(request, response);
   }

   private void handleRequest(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {
//      Principal principal = request.getUserPrincipal();

    ServletOutputStream out = response.getOutputStream();

    response.setContentType("text/html");

     // Get the answer from the bean
    String login    = request.getParameter("login");
    String password = request.getParameter("password");
    String rq       = request.getParameter("request");

    if(login == null || password == null || rq == null) {
      out.println("<HTML><BODY bgcolor=\"#FFFFFF\">");
      out.println("login: " + login);
      out.println("password: " + password);
      out.println("rq: " + rq);
      out.println("</BODY>");
      out.println("</HTML>");

      return;
    }

    try {
      String[] params = new String[] { login, password, rq };

      StringBuffer sb = chatHandler.handleRequest(params);

      out.println(sb.toString());
    }
    catch (Throwable e) {
      out.println("<HTML><BODY bgcolor=\"#FFFFFF\">");
      out.println("Time stamp: " + new Date().toString());
      out.println("<BR>ChatHandler type: " + chatHandler.getClass().getName());
      out.println("Error calling the Hello bean");
      out.println(e.toString());
      out.println("</BODY>");
      out.println("</HTML>");
    }
  }

  public String getServletInfo() {
    return "Chat Servlet";
  }

} 
