package org.uom.tesla.utils;

public class Constants {
    // server constants
    public final static String IP_BOOTSTRAP_SERVER = "23.94.83.39";
    public final static String IP_NODE = "127.0.0.1";
    public final static int PORT_BOOTSTRAP_SERVER = 55555;
    public final static String USERNAME_BOOTSTRAP_SERVER = "Bootstrap";
    public final static int MIN_PORT_NODE = 40000;
    public final static int MAX_PORT_NODE = 50000;

    // commands
    public final static String REG = "REG";
    public final static String REG_OK = "REGOK";
    public final static String UN_REG = "UNREG";
    public final static String UN_REG_OK = "UNROK";
    public final static String JOIN = "JOIN";
    public final static String JOIN_OK = "JOINOK";
    public final static String LEAVE = "LEAVE";
    public final static String LEAVE_OK = "LEAVEOK";
    public final static String SEARCH = "SER";
    public final static String SEARCH_OK = "SEROK";
    public final static String ERROR = "ERROR";

    // error codes
    public final static int ERROR_CANNOT_REGISTER = 9996;
    public final static int ERROR_DUPLICATE_IP = 9997;
    public final static int ERROR_ALREADY_REGISTERED = 9998;
    public final static int ERROR_COMMAND = 9999;
    public final static int ERROR_NODE_UNREACHABLE = 9999;
    public final static int ERROR_OTHER = 9998;
}
