package eu.vhhproject.mmsi.shotservice.error;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.core.Ordered;

/**
 * Register the {@link ShotServiceErrorController} as error page handler.
 */
public class ShotServiceErrorPageRegistration implements ErrorPageRegistrar, Ordered {

  private final DispatcherServletPath dispatcherServletPath;

  public ShotServiceErrorPageRegistration(DispatcherServletPath dispatcherServletPath) {
    this.dispatcherServletPath = dispatcherServletPath;
  }

  @Override
  public void registerErrorPages(ErrorPageRegistry errorPageRegistry)
  {
    ErrorPage errorPage = new ErrorPage(
        this.dispatcherServletPath.getRelativePath(ShotServiceErrorController.ERROR_PATH));
    errorPageRegistry.addErrorPages(errorPage);
  }

  @Override
  public int getOrder()
  {
    return 0;
  }

}