package com.github.chen0040.regp;


import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.exception.GrokException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by xschen on 6/12/17.
 */
public class GrokRepository implements Serializable {

   private static final long serialVersionUID = -1373610496881191557L;
   private static Map<String, String> defaultPatterns = new HashMap<>();
   private static Map<String, String> firewallPatterns = new HashMap<>();
   private static Map<String, String> haproxyPatterns = new HashMap<>();
   private static Map<String, String> javaPatterns = new HashMap<>();
   private static Map<String, String> linuxSyslogPatterns = new HashMap<>();
   private static Map<String, String> rubyPatterns = new HashMap<>();
   private static final List<String> EVOLVABLE_PATTERNS = new ArrayList<>();
   private static Set<String> nonEvovablePatterns = new HashSet<>();

   private String regex = "%{COMBINEDAPACHELOG}";

   static {
      defaultPatterns.put("USERNAME", "[a-zA-Z0-9._-]+");
      defaultPatterns.put("USER", "%{USERNAME:UNWANTED}");
      defaultPatterns.put("INT", "(?:[+-]?(?:[0-9]+))");
      defaultPatterns.put("BASE10NUM", "(?<![0-9.+-])(?>[+-]?(?:(?:[0-9]+(?:\\.[0-9]+)?)|(?:\\.[0-9]+)))");
      defaultPatterns.put("NUMBER", "(?:%{BASE10NUM:UNWANTED})");
      defaultPatterns.put("BASE16NUM", "(?<![0-9A-Fa-f])(?:[+-]?(?:0x)?(?:[0-9A-Fa-f]+))");
      defaultPatterns.put("BASE16FLOAT", "\\b(?<![0-9A-Fa-f.])(?:[+-]?(?:0x)?(?:(?:[0-9A-Fa-f]+(?:\\.[0-9A-Fa-f]*)?)|(?:\\.[0-9A-Fa-f]+)))\\b");

      defaultPatterns.put("POSINT", "\\b(?:[1-9][0-9]*)\\b");
      defaultPatterns.put("NONNEGINT", "\\b(?:[0-9]+)\\b");
      defaultPatterns.put("WORD", "\\b\\w+\\b");
      defaultPatterns.put("NOTSPACE", "\\S+");
      defaultPatterns.put("SPACE", "\\s*");
      defaultPatterns.put("DATA", ".*?");
      defaultPatterns.put("GREEDYDATA", ".*");

      defaultPatterns.put("QUOTEDSTRING",
              "(?>(?<!\\\\)(?>\"(?>\\\\.|[^\\\\\"]+)+\"|\"\"|(?>\'(?>\\\\.|[^\\\\\']+)+\')|\'\'|(?>`(?>\\\\.|[^\\\\`]+)+`)|``))");
      defaultPatterns.put("UUID", "[A-Fa-f0-9]{8}-(?:[A-Fa-f0-9]{4}-){3}[A-Fa-f0-9]{12}");

      //Networking
      defaultPatterns.put("MAC", "(?:%{CISCOMAC:UNWANTED}|%{WINDOWSMAC:UNWANTED}|%{COMMONMAC:UNWANTED})");
      defaultPatterns.put("CISCOMAC", "(?:(?:[A-Fa-f0-9]{4}\\.){2}[A-Fa-f0-9]{4})");
      defaultPatterns.put("WINDOWSMAC", "(?:(?:[A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2})");
      defaultPatterns.put("COMMONMAC", "(?:(?:[A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2})");
      defaultPatterns.put("IPV6",
              "((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?");
      defaultPatterns.put("IPV4",
              "(?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[.](?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9])");
      defaultPatterns.put("IP", "(?:%{IPV6:UNWANTED}|%{IPV4:UNWANTED})");
      defaultPatterns.put("HOSTNAME", "\\b(?:[0-9A-Za-z][0-9A-Za-z-]{0,62})(?:\\.(?:[0-9A-Za-z][0-9A-Za-z-]{0,62}))*(\\.?|\\b)");
      defaultPatterns.put("HOST", "%{HOSTNAME:UNWANTED}");
      defaultPatterns.put("IPORHOST", "(?:%{HOSTNAME:UNWANTED}|%{IP:UNWANTED})");
      defaultPatterns.put("HOSTPORT", "(?:%{IPORHOST}:%{POSINT:PORT})");

      //paths
      defaultPatterns.put("PATH", "(?:%{UNIXPATH}|%{WINPATH})");
      defaultPatterns.put("UNIXPATH", "(?>/(?>[\\w_%!$@:.,~-]+|\\\\.)*)+");
      defaultPatterns.put("TTY", "(?:/dev/(pts|tty([pq])?)(\\w+)?/?(?:[0-9]+))");
      defaultPatterns.put("WINPATH", "(?>[A-Za-z]+:|\\\\)(?:\\\\[^\\\\?*]*)+");
      defaultPatterns.put("URIPROTO", "[A-Za-z]+(\\+[A-Za-z+]+)?");
      defaultPatterns.put("URIHOST", "%{IPORHOST}(?::%{POSINT:port})?");

      defaultPatterns.put("URIPATH", "(?:/[A-Za-z0-9$.+!*\'(){},~:;=@#%_\\-]*)+");
      defaultPatterns.put("URIPARAM", "\\?[A-Za-z0-9$.+!*\'|(){},~@#%&/=:;_?\\-\\[\\]]*");
      defaultPatterns.put("URIPATHPARAM", "%{URIPATH}(?:%{URIPARAM})?");
      defaultPatterns.put("URI", "%{URIPROTO}://(?:%{USER}(?::[^@]*)?@)?(?:%{URIHOST})?(?:%{URIPATHPARAM})?");

      //Months: January, Feb, 3, 03, 12, December
      defaultPatterns.put("MONTH",
              "\\b(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)\\b");
      defaultPatterns.put("MONTHNUM", "(?:0?[1-9]|1[0-2])");
      defaultPatterns.put("MONTHNUM2", "(?:0[1-9]|1[0-2])");
      defaultPatterns.put("MONTHDAY", "(?:(?:0[1-9])|(?:[12][0-9])|(?:3[01])|[1-9])");

      //Days: Monday, Tue, Thu, etc...
      defaultPatterns.put("DAY", "(?:Mon(?:day)?|Tue(?:sday)?|Wed(?:nesday)?|Thu(?:rsday)?|Fri(?:day)?|Sat(?:urday)?|Sun(?:day)?)");

      //Years
      defaultPatterns.put("YEAR", "(?>\\d\\d){1,2}");
      //Time: HH:MM:SS

      defaultPatterns.put("HOUR", "(?:2[0123]|[01]?[0-9])");
      defaultPatterns.put("MINUTE", "(?:[0-5][0-9])");
      defaultPatterns.put("SECOND", "(?:(?:[0-5]?[0-9]|60)(?:[:.,][0-9]+)?)");
      defaultPatterns.put("TIME", "(?!<[0-9])%{HOUR}:%{MINUTE}(?::%{SECOND})(?![0-9])");
      //datestamp is YYYY/MM/DD-HH:MM:SS.UUUU (or something like it)
      defaultPatterns.put("DATE_US", "%{MONTHNUM}[/-]%{MONTHDAY}[/-]%{YEAR}");
      defaultPatterns.put("DATE_EU", "%{MONTHDAY}[./-]%{MONTHNUM}[./-]%{YEAR}");
      defaultPatterns.put("ISO8601_TIMEZONE", "(?:Z|[+-]%{HOUR}(?::?%{MINUTE}))");
      defaultPatterns.put("ISO8601_SECOND", "(?:%{SECOND}|60)");
      defaultPatterns.put("TIMESTAMP_ISO8601", "%{YEAR}-%{MONTHNUM}-%{MONTHDAY}[T ]%{HOUR}:?%{MINUTE}(?::?%{SECOND})?%{ISO8601_TIMEZONE}?");
      defaultPatterns.put("DATE", "%{DATE_US}|%{DATE_EU}");
      defaultPatterns.put("DATESTAMP", "%{DATE}[- ]%{TIME}");
      defaultPatterns.put("TZ", "(?:[PMCE][SD]T|UTC)");
      defaultPatterns.put("DATESTAMP_RFC822", "%{DAY} %{MONTH} %{MONTHDAY} %{YEAR} %{TIME} %{TZ}");
      defaultPatterns.put("DATESTAMP_RFC2822", "%{DAY}, %{MONTHDAY} %{MONTH} %{YEAR} %{TIME} %{ISO8601_TIMEZONE}");
      defaultPatterns.put("DATESTAMP_OTHER", "%{DAY} %{MONTH} %{MONTHDAY} %{TIME} %{TZ} %{YEAR}");
      defaultPatterns.put("DATESTAMP_EVENTLOG", "%{YEAR}%{MONTHNUM2}%{MONTHDAY}%{HOUR}%{MINUTE}%{SECOND}");

      //Syslog Dates: Month Day HH:MM:SS
      defaultPatterns.put("SYSLOGTIMESTAMP", "%{MONTH} +%{MONTHDAY} %{TIME}");
      defaultPatterns.put("PROG", "(?:[\\w._/%-]+)");
      defaultPatterns.put("SYSLOGPROG", "%{PROG:program}(?:\\[%{POSINT:pid}\\])?");
      defaultPatterns.put("SYSLOGHOST", "%{IPORHOST}");
      defaultPatterns.put("SYSLOGFACILITY", "<%{NONNEGINT:facility}.%{NONNEGINT:priority}>");
      defaultPatterns.put("HTTPDATE", "%{MONTHDAY}/%{MONTH}/%{YEAR}:%{TIME} %{INT}");

      //# Shortcuts
      defaultPatterns.put("QS", "%{QUOTEDSTRING:UNWANTED}");

      // Log formats
      defaultPatterns.put("SYSLOGBASE", "%{SYSLOGTIMESTAMP:timestamp} (?:%{SYSLOGFACILITY} )?%{SYSLOGHOST:logsource} %{SYSLOGPROG}:");

      defaultPatterns.put("MESSAGESLOG", "%{SYSLOGBASE} %{DATA}");

      defaultPatterns.put("COMMONAPACHELOG",
              "%{IPORHOST:clientip} %{USER:ident} %{USER:auth} \\[%{HTTPDATE:timestamp}\\] \"(?:%{WORD:verb} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion})?|%{DATA:rawrequest})\" %{NUMBER:response} (?:%{NUMBER:bytes}|-)");
      defaultPatterns.put("COMBINEDAPACHELOG", "%{COMMONAPACHELOG} %{QS:referrer} %{QS:agent}");
      defaultPatterns.put("COMMONAPACHELOG_DATATYPED",
              "%{IPORHOST:clientip} %{USER:ident;boolean} %{USER:auth} \\[%{HTTPDATE:timestamp;date;dd/MMM/yyyy:HH:mm:ss Z}\\] \"(?:%{WORD:verb;string} %{NOTSPACE:request}(?: HTTP/%{NUMBER:httpversion;float})?|%{DATA:rawrequest})\" %{NUMBER:response;int} (?:%{NUMBER:bytes;long}|-)");

      //Log Levels
      defaultPatterns.put("LOGLEVEL",
              "([A|a]lert|ALERT|[T|t]race|TRACE|[D|d]ebug|DEBUG|[N|n]otice|NOTICE|[I|i]nfo|INFO|[W|w]arn?(?:ing)?|WARN?(?:ING)?|[E|e]rr?(?:or)?|ERR?(?:OR)?|[C|c]rit?(?:ical)?|CRIT?(?:ICAL)?|[F|f]atal|FATAL|[S|s]evere|SEVERE|EMERG(?:ENCY)?|[Ee]merg(?:ency)?)");

      //# NetScreen firewall logs
      firewallPatterns.put("NETSCREENSESSIONLOG",
              "%{SYSLOGTIMESTAMP:date} %{IPORHOST:device} %{IPORHOST}: NetScreen device_id=%{WORD:device_id}%{DATA}: start_time=%{QUOTEDSTRING:start_time} duration=%{INT:duration} policy_id=%{INT:policy_id} service=%{DATA:service} proto=%{INT:proto} src zone=%{WORD:src_zone} dst zone=%{WORD:dst_zone} action=%{WORD:action} sent=%{INT:sent} rcvd=%{INT:rcvd} src=%{IPORHOST:src_ip} dst=%{IPORHOST:dst_ip} src_port=%{INT:src_port} dst_port=%{INT:dst_port} src-xlated ip=%{IPORHOST:src_xlated_ip} port=%{INT:src_xlated_port} dst-xlated ip=%{IPORHOST:dst_xlated_ip} port=%{INT:dst_xlated_port} session_id=%{INT:session_id} reason=%{GREEDYDATA:reason}");

      //haproxy
      haproxyPatterns.put("HAPROXYTIME", "(?!<[0-9])%{HOUR:haproxy_hour}:%{MINUTE:haproxy_minute}(?::%{SECOND:haproxy_second})(?![0-9])");
      haproxyPatterns.put("HAPROXYDATE",
              "%{MONTHDAY:haproxy_monthday}/%{MONTH:haproxy_month}/%{YEAR:haproxy_year}:%{HAPROXYTIME:haproxy_time}.%{INT:haproxy_milliseconds}");

      haproxyPatterns.put("HAPROXYCAPTUREDREQUESTHEADERS", "%{DATA:captured_request_headers}");
      haproxyPatterns.put("HAPROXYCAPTUREDRESPONSEHEADERS", "%{DATA:captured_response_headers}");

      // parse a haproxy 'httplog' line
      haproxyPatterns.put("HAPROXYHTTP",
              "%{SYSLOGTIMESTAMP:syslog_timestamp} %{IPORHOST:syslog_server} %{SYSLOGPROG}: %{IP:client_ip}:%{INT:client_port} \\[%{HAPROXYDATE:accept_date}\\] %{NOTSPACE:frontend_name} %{NOTSPACE:backend_name}/%{NOTSPACE:server_name} %{INT:time_request}/%{INT:time_queue}/%{INT:time_backend_connect}/%{INT:time_backend_response}/%{NOTSPACE:time_duration} %{INT:http_status_code} %{NOTSPACE:bytes_read} %{DATA:captured_request_cookie} %{DATA:captured_response_cookie} %{NOTSPACE:termination_state} %{INT:actconn}/%{INT:feconn}/%{INT:beconn}/%{INT:srvconn}/%{NOTSPACE:retries} %{INT:srv_queue}/%{INT:backend_queue} (\\{%{HAPROXYCAPTUREDREQUESTHEADERS}\\})?( )?(\\{%{HAPROXYCAPTUREDRESPONSEHEADERS}\\})?( )?\"%{WORD:http_verb} %{URIPATHPARAM:http_request}( HTTP/%{NUMBER:http_version}\")?");

      //parse a haproxy 'tcplog' line
      haproxyPatterns.put("HAPROXYTCP",
              "%{SYSLOGTIMESTAMP:syslog_timestamp} %{IPORHOST:syslog_server} %{SYSLOGPROG}: %{IP:client_ip}:%{INT:client_port} \\[%{HAPROXYDATE:accept_date}\\] %{NOTSPACE:frontend_name} %{NOTSPACE:backend_name}/%{NOTSPACE:server_name} %{INT:time_queue}/%{INT:time_backend_connect}/%{NOTSPACE:time_duration} %{NOTSPACE:bytes_read} %{NOTSPACE:termination_state} %{INT:actconn}/%{INT:feconn}/%{INT:beconn}/%{INT:srvconn}/%{NOTSPACE:retries} %{INT:srv_queue}/%{INT:backend_queue}");

      //java
      javaPatterns.put("JAVACLASS", "(?:[a-zA-Z0-9-]+\\.)+[A-Za-z0-9$]+");
      javaPatterns.put("JAVAFILE", "(?:[A-Za-z0-9_.-]+)");
      javaPatterns.put("JAVASTACKTRACEPART", "at %{JAVACLASS:class}\\.%{WORD:method}\\(%{JAVAFILE:file}:%{NUMBER:line}\\)");

      //linux syslog
      linuxSyslogPatterns.put("SYSLOGBASE2",
              "(?:%{SYSLOGTIMESTAMP:timestamp}|%{TIMESTAMP_ISO8601:timestamp8601}) (?:%{SYSLOGFACILITY} )?%{SYSLOGHOST:logsource} %{SYSLOGPROG}:");
      linuxSyslogPatterns.put("SYSLOGPAMSESSION",
              "%{SYSLOGBASE} (?=%{GREEDYDATA:message})%{WORD:pam_module}\\(%{DATA:pam_caller}\\): session %{WORD:pam_session_state} for user %{USERNAME:username}(?: by %{GREEDYDATA:pam_by})?");
      linuxSyslogPatterns.put("CRON_ACTION", "[A-Z ]+");
      linuxSyslogPatterns.put("CRONLOG", "%{SYSLOGBASE} \\(%{USER:user}\\) %{CRON_ACTION:action} \\(%{DATA:message}\\)");
      linuxSyslogPatterns.put("SYSLOGLINE", "%{SYSLOGBASE2} %{GREEDYDATA:message}");

      //ruby
      rubyPatterns.put("RUBY_LOGLEVEL", "(?:DEBUG|FATAL|ERROR|WARN|INFO)");
      rubyPatterns.put("RUBY_LOGGER",
              "[DFEWI], \\[%{TIMESTAMP_ISO8601:timestamp} #%{POSINT:pid}\\] *%{RUBY_LOGLEVEL:loglevel} -- +%{DATA:progname}: %{GREEDYDATA:message}");

      nonEvovablePatterns.add("INT");
      nonEvovablePatterns.add("BASE10NUM");
      nonEvovablePatterns.add("NUMBER");
      nonEvovablePatterns.add("BASE16NUM");
      nonEvovablePatterns.add("BASE16FLOAT");
      nonEvovablePatterns.add("POSINT");
      nonEvovablePatterns.add("NONNEGINT");
      nonEvovablePatterns.add("WORD");
      nonEvovablePatterns.add("NOTSPACE");
      nonEvovablePatterns.add("SPACE");
      nonEvovablePatterns.add("DATA");
      nonEvovablePatterns.add("GREEDYDATA");
      nonEvovablePatterns.add("QUOTEDSTRING");
      nonEvovablePatterns.add("MONTH");
      nonEvovablePatterns.add("MONTHNUM");
      nonEvovablePatterns.add("MONTHNUM2");
      nonEvovablePatterns.add("MONTHDAY");
      nonEvovablePatterns.add("DAY");
      nonEvovablePatterns.add("YEAR");
      nonEvovablePatterns.add("HOUR");
      nonEvovablePatterns.add("MINUTE");
      nonEvovablePatterns.add("SECOND");
      nonEvovablePatterns.add("TIME");

      nonEvovablePatterns.add("DATE_US");
      nonEvovablePatterns.add("DATE_EU");
      nonEvovablePatterns.add("ISO8601_TIMEZONE");
      nonEvovablePatterns.add("ISO8601_SECOND");
      nonEvovablePatterns.add("TIMESTAMP_ISO8601");
      nonEvovablePatterns.add("DATE");
      nonEvovablePatterns.add("DATESTAMP");
      nonEvovablePatterns.add("TZ");
      nonEvovablePatterns.add("DATESTAMP_RFC822");
      nonEvovablePatterns.add("DATESTAMP_RFC2822");
      nonEvovablePatterns.add("DATESTAMP_OTHER");
      nonEvovablePatterns.add("DATESTAMP_EVENTLOG");
      nonEvovablePatterns.add("SYSLOGTIMESTAMP");
      nonEvovablePatterns.add("PROG");
      nonEvovablePatterns.add("SYSLOGPROG");
      nonEvovablePatterns.add("SYSLOGHOST");
      nonEvovablePatterns.add("SYSLOGFACILITY");
      nonEvovablePatterns.add("HTTPDATE");
      nonEvovablePatterns.add("QS");

      EVOLVABLE_PATTERNS.addAll(defaultPatterns.keySet().stream().filter(p -> !nonEvovablePatterns.contains(p)).collect(Collectors.toList()));
      EVOLVABLE_PATTERNS.addAll(haproxyPatterns.keySet().stream().collect(Collectors.toList()));
      EVOLVABLE_PATTERNS.addAll(firewallPatterns.keySet().stream().collect(Collectors.toList()));
      EVOLVABLE_PATTERNS.addAll(javaPatterns.keySet().stream().collect(Collectors.toList()));
      EVOLVABLE_PATTERNS.addAll(linuxSyslogPatterns.keySet().stream().collect(Collectors.toList()));
      EVOLVABLE_PATTERNS.addAll(rubyPatterns.keySet().stream().collect(Collectors.toList()));

   }

   private GrokRepository(String regex) {
      this.regex = regex;
   }


   public static GrokRepository regex(String regex) {
      return new GrokRepository(regex);
   }


   public static Grok build(String regex) throws GrokException {
      Grok grok = new Grok();

      grok.copyPatterns(defaultPatterns);
      grok.copyPatterns(firewallPatterns);
      grok.copyPatterns(haproxyPatterns);
      grok.copyPatterns(javaPatterns);
      grok.copyPatterns(linuxSyslogPatterns);
      grok.copyPatterns(rubyPatterns);

      if (regex.startsWith("%{") && regex.endsWith("}")) {
         grok.compile(regex);
      }
      else {
         grok.compile("%{" + regex + "}");
      }

      return grok;
   }


   public static String getPattern(Integer filterIndex) {
      return EVOLVABLE_PATTERNS.get(filterIndex);
   }


   public static int countPatterns() {
      return EVOLVABLE_PATTERNS.size();
   }



   public static String regex(List<Integer> express) {

      StringBuilder sb = new StringBuilder();
      for(int i=0; i < express.size(); ++i){

      }

      return sb.toString();
   }


}
