package org.wikipedia.editing;

import android.content.*;
import org.mediawiki.api.json.*;
import org.wikipedia.*;
import org.wikipedia.concurrency.*;

public class FetchEditTokenTask extends ApiTask<String> {
    public FetchEditTokenTask(Context context, Site site) {
        super(
                ExecutorService.getSingleton().getExecutor(FetchEditTokenTask.class, 1),
                ((WikipediaApp)context.getApplicationContext()).getAPIForSite(site)
        );
    }

    @Override
    public RequestBuilder buildRequest(Api api) {
        return api.action("tokens")
                .param("type", "edit");
    }

    @Override
    public String processResult(ApiResult result) throws Throwable {
        return result.asObject().optJSONObject("tokens").optString("edittoken");
    }
}