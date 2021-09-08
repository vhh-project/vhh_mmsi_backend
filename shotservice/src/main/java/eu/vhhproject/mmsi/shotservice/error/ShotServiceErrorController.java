package eu.vhhproject.mmsi.shotservice.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import eu.vhhproject.mmsi.shotservice.ShotserviceProperties.ShotServiceErrorProperties;

@RequestMapping(ShotServiceErrorController.ERROR_PATH)
public class ShotServiceErrorController implements ErrorController {

  public static final String ERROR_PATH = "/error";

  @Autowired
  private ShotServiceErrorProperties shotServiceErrorProperties;

  @Override
  public String getErrorPath()
  {
    return ERROR_PATH;
  }

  @RequestMapping
  public ResponseEntity<Map<String, Object>> error(HttpServletRequest request, Exception ex)
  {
    try
    {
      RequestAttributes requestAttributes = new ServletRequestAttributes(request);

      Throwable error = getThrowable(requestAttributes);
      HttpStatus status = getStatus(requestAttributes);

      status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;

      Map<String, Object> body = createErrorResponeBody("ErrorController", error, status);

      return new ResponseEntity<>(body, status);
    }
    catch (Throwable t)
    {
      // fallback, if an exception occurs while creating the error response
      Map<String, Object> body = new LinkedHashMap<>();

      body.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
      body.put("statusReason", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

      return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Create the body for an error response.
   * 
   * @param handledBy  information on the handler of the error
   * @param error      the error
   * @param httpStatus the http status
   * @return a map to be used as body in the error response to the client.
   */
  public Map<String, Object> createErrorResponeBody(String handledBy, Throwable error, HttpStatus httpStatus)
  {
    return createErrorResponeBody(handledBy, error, httpStatus, null, null);
  }

  /**
   * Create the body for an error response.
   * 
   * @param handledBy    information on the handler of the error
   * @param error        the error
   * @param httpStatus   the http status
   * @param errorCode    the shot service specific error code
   * @param errorDetails error details to be included in the response
   * @return a map to be used as body in the error response to the client.
   */
  public Map<String, Object> createErrorResponeBody(
      String handledBy,
      Throwable error,
      HttpStatus httpStatus,
      String errorCode,
      Object errorDetails)
  {
    Map<String, Object> body = new LinkedHashMap<>();

    body.put("timestamp", Instant.now());

    body.put("statusCode", httpStatus != null ? httpStatus.value() : null);
    body.put("statusReason", httpStatus != null ? httpStatus.getReasonPhrase() : "no http reason");

    if (errorCode != null)
    {
      body.put("errorCode", errorCode);
    }

    if (errorDetails != null)
    {
      body.put("errorDetails", errorDetails);
    }

    if (shotServiceErrorProperties.isIncludeException())
    {
      Map<String, Object> exception = new LinkedHashMap<>();

      exception.put("handledBy", handledBy);
      exception.put("name", error != null ? error.getClass().getName() : "no exception information available");
      exception.put("message", error != null ? error.getMessage() : "no exception information available");

      if (shotServiceErrorProperties.isIncludeStackTrace())
      {
        exception.put("stacktrace", error != null ? getStackTrace(error) : "no exception information available");
      }

      body.put("exception", exception);
    }

    return body;
  }

  private List<String> getStackTrace(Throwable error)
  {
    ListWriter listWriter = new ListWriter();
    error.printStackTrace(new PrintWriter(listWriter));

    return listWriter.getList();
  }

  private @Nullable Throwable getThrowable(RequestAttributes requestAttributes)
  {
    Throwable error = getAttribute(requestAttributes, "javax.servlet.error.exception");

    if (error != null)
    {
      while (error instanceof ServletException && error.getCause() != null)
      {
        error = ((ServletException) error).getCause();
      }
    }

    return error;
  }

  private @Nullable HttpStatus getStatus(RequestAttributes requestAttributes)
  {
    Integer status = getAttribute(requestAttributes, "javax.servlet.error.status_code");

    try
    {
      return HttpStatus.valueOf(status);
    }
    catch (Exception ex)
    {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T getAttribute(RequestAttributes requestAttributes, String name)
  {
    return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
  }

  /**
   * <p>
   * A simple list writer, that writes lines to a list of string.
   * 
   * <p>
   * A new list entry is created whenever a line feed '\n' is encountered.
   * 
   * <p>
   * Tabs '\t' are converted to spaces.
   * 
   */
  private static class ListWriter extends Writer {

    private List<String> list = new ArrayList<>();
    private StringWriter currLine = new StringWriter();

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException
    {
      if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0))
      {
        throw new IndexOutOfBoundsException();
      }

      for (int i = off; i < len; i++)
      {
        char currChar = cbuf[i];

        if (currChar == '\n')
        {
          list.add(currLine.toString());
          currLine = new StringWriter();
        }
        else if (currChar == '\t')
        {
          currLine.append("    ");
        }
        else
        {
          currLine.append(currChar);
        }
      }
    }

    public List<String> getList()
    {
      return list;
    }

    @Override
    public void flush() throws IOException
    {

    }

    @Override
    public void close() throws IOException
    {

    }
  }
}
