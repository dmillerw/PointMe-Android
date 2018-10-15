package dmillerw.me.pointmelistener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    private boolean isNavigationActive = false;
    private String stageDistance;
    private String stageText;
    private String stateSubtext;
    public String stageIcon;

    public WebServer() {
        super(1298);
    }

    public void update(boolean status) {
        this.isNavigationActive = status;
        this.stageDistance = null;
        this.stageText = null;
        this.stateSubtext = null;
        this.stageIcon = null;
    }

    public void update(String distance, String text, String subtext, String icon) {
        this.isNavigationActive = true;
        this.stageDistance = distance;
        this.stageText = text;
        this.stateSubtext = subtext;
        this.stageIcon = icon;
    }

    @Override
    public Response serve(NanoHTTPD.IHTTPSession session) {
        Map<String, String> data = new HashMap<>();
        data.put("isNavigationActive", isNavigationActive ? "true" : "false");
        if (isNavigationActive) {
            data.put("stageDistance", stageDistance);
            data.put("stageText", stageText);
            data.put("stateSubtext", stateSubtext);
            data.put("stageIcon", stageIcon);
        }

        return newFixedLengthResponse(Response.Status.OK, "application/json", data.toString());
    }
}
