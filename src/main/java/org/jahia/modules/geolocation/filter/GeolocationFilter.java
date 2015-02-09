package org.jahia.modules.geolocation.filter;

import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tarek on 9/8/14.
 */
public class GeolocationFilter extends AbstractFilter {

    private transient static Logger logger = LoggerFactory.getLogger(GeolocationFilter.class);

    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        if (! renderContext.isLiveMode() && ! renderContext.isPreviewMode() ) {
            // Not live mode: always show content
            resource.pushWrapper("geolocationset");
            return null;
        }

        // Get region from request
        String requestedCountry = (String) renderContext.getRequest().getSession().getAttribute("geolocationCountryCode");

        // Get region from current node
        String nodeCountry = resource.getNode().getPropertyAsString("geoIPCountry");
      	logger.info("Visitor region: " + requestedCountry);
        logger.info("Content region: " + nodeCountry);

      	if (requestedCountry != null) {
        	if (nodeCountry == null || nodeCountry == "" || nodeCountry.contains(requestedCountry)) {
            	return null;
        	}
        }

        return "<div style=\"display:none\"></div>";
    }
}



