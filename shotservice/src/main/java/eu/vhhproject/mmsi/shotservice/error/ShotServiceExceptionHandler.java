package eu.vhhproject.mmsi.shotservice.error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Path.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ShotServiceExceptionHandler extends ResponseEntityExceptionHandler {

  /** Pattern for parsing json error messages **/
  private static final Pattern JSON_ERROR_MESSAGE_PATTERN = Pattern
      .compile("(.*)\\\n.*line: ([0-9]+).*column: ([0-9]+).*");

  @Autowired
  private ShotServiceErrorController shotServiceErrorController;

  @ExceptionHandler
  public ResponseEntity<Object> handle(EntityNotFoundException ex, WebRequest request)
  {
    HttpStatus status = HttpStatus.NOT_FOUND;

    Map<String, Object> responseBody = shotServiceErrorController.createErrorResponeBody("ExceptionHandler", ex, status,
        EntityNotFoundException.ERROR_CODE, ex.getErrorDetails());

    return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), status, request);
  }

  @ExceptionHandler
  public ResponseEntity<Object> handle(ConstraintViolationException ex, WebRequest request)
  {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    List<ErrorDetailsConstraintViolation> violations = new ArrayList<>();

    for (ConstraintViolation<?> violation : ex.getConstraintViolations().stream()
        .sorted((v1, v2) -> v1.getPropertyPath().toString().compareTo(v2.getPropertyPath().toString()))
        .collect(Collectors.toList()))

    {
      ErrorDetailsConstraintViolation violationDetails = new ErrorDetailsConstraintViolation();

      violationDetails.setProperty(createPropertyPathForClient(violation.getPropertyPath()));
      violationDetails.setMessage(violation.getMessage());
      violationDetails.setInvalidValue(violation.getInvalidValue());

      violations.add(violationDetails);
    }

    Map<String, Object> responseBody = shotServiceErrorController.createErrorResponeBody("ExceptionHandler", ex, status,
        ErrorCode.CONSTRAINT_VIOLATION, violations);

    return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), status, request);
  }

  /**
   * Special handling of json parsing exceptions to provide a specific error code
   * when the client sends malformed json.
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request)
  {
    if (ex.getCause() instanceof JsonProcessingException)
    {
      String message = ex.getCause().getMessage();
      Matcher matcher = JSON_ERROR_MESSAGE_PATTERN.matcher(message);

      ErrorDetailsMalformedJson errorDetails = new ErrorDetailsMalformedJson(message);

      if (matcher.matches())
      {
        errorDetails.setMessage(matcher.group(1));
        errorDetails.setLine(Integer.valueOf(matcher.group(2)));
        errorDetails.setColumn(Integer.valueOf(matcher.group(3)));
      }

      Map<String, Object> responseBody = shotServiceErrorController.createErrorResponeBody("ExceptionHandler", ex,
          status, ErrorCode.MALFORMED_JSON, errorDetails);

      return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), status, request);
    }
    else
    {
      return handleExceptionInternal(ex, null, headers, status, request);
    }
  }

  @ExceptionHandler(value = { InternalServiceException.class })
  protected ResponseEntity<Object> handleInternalServiceException(InternalServiceException ex, WebRequest request)
  {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    Map<String, Object> responseBody = shotServiceErrorController.createErrorResponeBody("ExceptionHandler", ex, status,
        InternalServiceException.ERROR_CODE, ex.getErrorDetails());

    return super.handleExceptionInternal(ex, responseBody, new HttpHeaders(), status, request);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      Object body,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request)
  {
    Map<String, Object> responseBody = shotServiceErrorController.createErrorResponeBody("ExceptionHandler", ex,
        status);

    return super.handleExceptionInternal(ex, responseBody, headers, status, request);
  }

  /* ***** Private Methods ***** */

  /**
   * Create a validation property path for the client that only includes
   * information on the actual properties and strips any other information. E.g.
   * 'postShots.shotTO[0].inPoint' will be converted to '[0].inPoint'
   * 
   * @param validationPropertyPath the property path from the validation message
   * 
   * @return a stripped down property path suitable to be communicated to the
   *         client
   */
  private String createPropertyPathForClient(Path validationPropertyPath)
  {
    Iterator<Node> iter = validationPropertyPath.iterator();
    StringBuilder propertyPathStr = new StringBuilder();

    while (iter.hasNext())
    {
      Node n = iter.next();

      if (ElementKind.PROPERTY.equals(n.getKind()))
      {
        if (n.getIndex() != null)
        {
          propertyPathStr.append("[");
          propertyPathStr.append(n.getIndex());
          propertyPathStr.append("]");
        }

        if (propertyPathStr.length() > 0)
        {
          propertyPathStr.append(".");
        }

        propertyPathStr.append(n.getName());
      }
    }

    return propertyPathStr.toString();
  }

}
