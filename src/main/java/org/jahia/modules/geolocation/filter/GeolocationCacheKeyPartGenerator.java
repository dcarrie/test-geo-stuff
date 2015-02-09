package org.jahia.modules.geolocation.filter;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.cache.CacheKeyPartGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Properties;

/**
 * Created by tarek on 9/9/14.
 */
public class GeolocationCacheKeyPartGenerator implements CacheKeyPartGenerator {

    private transient static Logger logger = LoggerFactory.getLogger(GeolocationCacheKeyPartGenerator.class);

    public String getKey() {
        return "geolocationRegion";
    }

    public String getValue(Resource resource, RenderContext renderContext, Properties properties) {
        
      	try {
            JCRNodeWrapper node = resource.getNode();
            if (node.isNodeType("jmix:geolocalizable")) {
                // Retrieve list of countries set in geo-localized node
                String countries = node.getPropertyAsString("geoIPCountry");
                logger.info("Geolocation Cache - Adding placeholder: " + countries);

                return countries;
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String replacePlaceholders(RenderContext renderContext, String s) {
        if (s.isEmpty()) {
            return "";
        }

        logger.info("Retreived placeholder: " + s);

        // Check if region in session matches one region in cache key placeholder
        String requestedRegion = (String) renderContext.getRequest().getSession().getAttribute("geolocationCountryCode");

        if (requestedRegion !=null && !requestedRegion.isEmpty() && s.indexOf(requestedRegion)>=0) {
            // Match found: display content
            logger.info("Generating cache key for region: " + requestedRegion + " / $geopip-display");
            return "$geopip-display";
        }

        // No match found: don't display content
        logger.info("Generating cache key for region: " + requestedRegion + " / $geopip-nodisplay");
        return "$geoip-nodisplay";
    }
}
