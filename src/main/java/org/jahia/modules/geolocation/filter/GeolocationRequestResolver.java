package org.jahia.modules.geolocation.filter;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.record.Country;
import org.jahia.data.templates.JahiaTemplatesPackage;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.templates.JahiaModuleAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * Created by tarek on 9/8/14.
 */
public class GeolocationRequestResolver extends AbstractFilter implements JahiaModuleAware {
    private transient static Logger logger = LoggerFactory.getLogger(GeolocationRequestResolver.class);
    private JahiaTemplatesPackage jahiaTemplatesPackage;

    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        // Get IP client address
        logger.info("Setting location info");
        String remoteAddr = renderContext.getRequest().getRemoteAddr();
        HttpSession session = renderContext.getRequest().getSession();

        if (renderContext.isPreviewMode() || remoteAddr.equals("127.0.0.1") || remoteAddr.startsWith("10.0") || remoteAddr.startsWith("192.168")) {
            // Client address is local - no geoIP available
            String countryCode = renderContext.getRequest().getParameter("country");
            if (countryCode != null && !countryCode.isEmpty()) {
                // Set country code from request parameter
                logger.info("Local request - code set to " + countryCode);
                session.setAttribute("geolocationCountryCode", countryCode);
            }

            return null;
        }

        // Check if country code is already in session
        String countryCode = (String) session.getAttribute("geolocationCountryCode");
        if (countryCode != null && ! countryCode.isEmpty()) {
            // Country code has already been set
            return null;
        }

        // Initialize GeoIP database
        InputStream database = jahiaTemplatesPackage.getResource("/db/GeoLite2-Country.mmdb").getInputStream();

        try {
            // Resolve country code from request
            DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
            Country country = dbReader.country(InetAddress.getByName(remoteAddr)).getCountry();

            // Saving country code in session
            logger.info("Host country is " + country.getName() + "/" + country.getName());
            session.setAttribute("geolocationCountryCode", country.getIsoCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setJahiaModule(JahiaTemplatesPackage jahiaTemplatesPackage) {
        this.jahiaTemplatesPackage = jahiaTemplatesPackage;
    }
}
