package org.wikipedia.page;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wikipedia.PageTitle;
import org.wikipedia.Site;
import org.wikipedia.Utils;
import org.wikipedia.WikipediaApp;
import org.wikipedia.bridge.CommunicationBridge;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Handles any html links coming from a {@link org.wikipedia.page.PageViewFragment}
 */
public abstract class LinkHandler implements CommunicationBridge.JSEventListener {
    private final Context context;
    private final Site currentSite;

    public LinkHandler(Context context, CommunicationBridge bridge, Site currentSite) {
        this.context = context;
        this.currentSite = currentSite;

        bridge.addListener("linkClicked", this);
    }

    public abstract void onInternalLinkClicked(PageTitle title);

    @Override
    public void onMessage(String messageType, JSONObject messagePayload) {
        try {
            String href = URLDecoder.decode(messagePayload.getString("href"), "UTF-8");
            if (href.startsWith("//")) {
                // That's a protocol specific link! Make it https!
                href = "https:" + href;
            }
            Log.d("Wikipedia", "Link clicked was " + href);
            if (href.startsWith("/wiki/")) {
                // TODO: Handle fragments

                PageTitle title = currentSite.titleForInternalLink(href);
                onInternalLinkClicked(title);
            } else {
                Uri uri = Uri.parse(href);
                String authority = uri.getAuthority();
                // FIXME: Make this more complete, only to not handle URIs that contain unsupported actions
                if (authority != null && Site.isSupportedSite(authority) && uri.getPath().startsWith("/wiki/")) {
                    Site site = new Site(authority);
                    //TODO: Handle fragments
                    PageTitle title = site.titleForInternalLink(uri.getPath());
                    onInternalLinkClicked(title);
                } else {
                    // if it's a /w/ URI, turn it into a full URI and go external
                    if (href.startsWith("/w/")) {
                        href = String.format("%1$s://%2$s", WikipediaApp.PROTOCOL, currentSite.getDomain()) + href;
                    }
                    Utils.handleExternalLink(context, Uri.parse(href));
                }
            }
        } catch (UnsupportedEncodingException e) {
            // will not happen
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
