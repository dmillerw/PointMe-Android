package dmillerw.me.pointmelistener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        JsonObject object = new JsonObject();
        object.addProperty("isNavigationActive", isNavigationActive);
        if (isNavigationActive) {
            object.addProperty("stageDistance", stageDistance);
            object.addProperty("stageText", stageText);
            object.addProperty("stateSubtext", stateSubtext);
            object.addProperty("stageIcon", stageIcon);
        }

        return newFixedLengthResponse(Response.Status.OK, "application/json", new Gson().toJson(object));
    }
}
